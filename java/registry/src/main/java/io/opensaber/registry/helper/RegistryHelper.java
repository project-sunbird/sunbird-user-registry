package io.opensaber.registry.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opensaber.pojos.OpenSaberInstrumentation;
import io.opensaber.pojos.Response;
import io.opensaber.pojos.ResponseParams;
import io.opensaber.registry.middleware.MiddlewareHaltException;
import io.opensaber.registry.model.DBConnectionInfoMgr;
import io.opensaber.registry.service.RegistryService;
import io.opensaber.registry.service.impl.RegistryServiceImpl;
import io.opensaber.registry.sink.shard.Shard;
import io.opensaber.registry.sink.shard.ShardManager;
import io.opensaber.registry.util.RecordIdentifier;
import io.opensaber.validators.IValidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * This is helper class, user-service calls this class in-order to access registry functionality
 */
@Component
public class RegistryHelper {

    private static Logger logger = LoggerFactory.getLogger(RegistryHelper.class);

    @Autowired
    private IValidate iValidate;

    @Autowired
    private ShardManager shardManager;

    @Autowired
    RegistryService registryService;

    @Autowired
    private DBConnectionInfoMgr dbConnectionInfoMgr;

    @Autowired
    private OpenSaberInstrumentation watch;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * calls validation and then persists the record to registry.
     * @param inputJson
     * @return
     * @throws MiddlewareHaltException
     */
    public Response addEntity(JsonNode inputJson) throws MiddlewareHaltException, JsonProcessingException {
        ResponseParams responseParams = new ResponseParams();
        Response response = new Response(Response.API_ID.CREATE, "OK", responseParams);
        Map<String, Object> result = new HashMap<>();
        String entityType = inputJson.fields().next().getKey();
        String jsonString = objectMapper.writeValueAsString(inputJson);
        iValidate.validate(entityType, jsonString);

        try {
            logger.info("Add api: entity type and shard propery: {}", shardManager.getShardProperty());
            Shard shard = shardManager.getShard(inputJson.get(shardManager.getShardProperty()));
            watch.start("RegistryController.addToExistingEntity");
            String resultId = registryService.addEntity(jsonString,shard,"dummy-user");
            RecordIdentifier recordId = new RecordIdentifier(shard.getShardLabel(), resultId);
            Map resultMap = new HashMap();
            String label = recordId.toString();
            resultMap.put(dbConnectionInfoMgr.getUuidPropertyName(), label);
            result.put(entityType, resultMap);
            response.setResult(result);
            responseParams.setStatus(Response.Status.SUCCESSFUL);
            watch.stop("RegistryController.addToExistingEntity");
            logger.info("AddEntity,{}", resultId);
        } catch (Exception e) {
            logger.error("Exception in controller while adding entity !", e);
            response.setResult(result);
            responseParams.setStatus(Response.Status.UNSUCCESSFUL);
            responseParams.setErrmsg(e.getMessage());
        }
        return response;
    }
}

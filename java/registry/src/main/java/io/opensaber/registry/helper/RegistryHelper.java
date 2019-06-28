package io.opensaber.registry.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opensaber.pojos.OpenSaberInstrumentation;
import io.opensaber.registry.model.DBConnectionInfoMgr;
import io.opensaber.registry.service.RegistryService;
import io.opensaber.registry.sink.shard.Shard;
import io.opensaber.registry.sink.shard.ShardManager;
import io.opensaber.registry.util.RecordIdentifier;
import io.opensaber.validators.IValidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * This is helper class, user-service calls this class in-order to access registry functionality
 */
@Component
public class RegistryHelper {

    private static Logger logger = LoggerFactory.getLogger(RegistryHelper.class);

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
     * @throws Exception
     */
    public String addEntity(JsonNode inputJson, String userId) throws Exception {
        RecordIdentifier recordId = null;
        String entityType = inputJson.fields().next().getKey();
        String jsonString = objectMapper.writeValueAsString(inputJson);
        try {
            logger.info("Add api: entity type and shard propery: {}", shardManager.getShardProperty());

            Shard shard = shardManager.getShard(inputJson.get(entityType).get(shardManager.getShardProperty()));
            watch.start("RegistryController.addToExistingEntity");
            String resultId = registryService.addEntity(shard, jsonString,userId);
            recordId = new RecordIdentifier(shard.getShardLabel(), resultId);
            watch.stop("RegistryController.addToExistingEntity");
            logger.info("AddEntity,{}", resultId);
        } catch (Exception e) {
            logger.error("Exception in controller while adding entity !", e);
            throw new Exception(e);
        }
        return recordId.toString();
    }
}


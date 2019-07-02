package io.opensaber.registry.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opensaber.pojos.OpenSaberInstrumentation;
import io.opensaber.registry.model.DBConnectionInfoMgr;
import io.opensaber.registry.service.IReadService;
import io.opensaber.registry.service.RegistryService;
import io.opensaber.registry.sink.shard.Shard;
import io.opensaber.registry.sink.shard.ShardManager;
import io.opensaber.registry.util.ReadConfigurator;
import io.opensaber.registry.util.ReadConfiguratorFactory;
import io.opensaber.registry.util.RecordIdentifier;
import io.opensaber.registry.util.ViewTemplateManager;
import io.opensaber.views.ViewTemplate;
import io.opensaber.views.ViewTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;


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
    IReadService readService;

    @Autowired
    private ViewTemplateManager viewTemplateManager;

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
            Map<String, Object> shardIdValue = objectMapper.convertValue(inputJson.get(entityType),Map.class);
            Shard shard = shardManager.getShard(shardIdValue.getOrDefault(shardManager.getShardProperty(),null));
            watch.start("RegistryController.addToExistingEntity");
            String resultId = registryService.addEntity(shard,jsonString,userId);
            recordId = new RecordIdentifier(shard.getShardLabel(), resultId);
            watch.stop("RegistryController.addToExistingEntity");
            logger.info("AddEntity,{}", resultId);
        } catch (Exception e) {
            logger.error("Exception in controller while adding entity !", e);
            throw new Exception(e);
        }
        return recordId.toString();
    }

    /**
     * Get entity details from the DB and modifies data according to view template
     * @param inputJson
     * @param requireLDResponse
     * @return
     * @throws Exception
     */
    public JsonNode readEntity(JsonNode inputJson, boolean requireLDResponse) throws Exception {
        logger.debug("readEntity starts");
        boolean includeSignatures = false;
        String entityType = inputJson.fields().next().getKey();
        String label = inputJson.get(entityType).get(dbConnectionInfoMgr.getUuidPropertyName()).asText();
        RecordIdentifier recordId = RecordIdentifier.parse(label);
        String shardId = dbConnectionInfoMgr.getShardId(recordId.getShardLabel());
        Shard shard = shardManager.activateShard(shardId);
        logger.info("Read Api: shard id: " + recordId.getShardLabel() + " for label: " + label);
        JsonNode signatureNode = inputJson.get(entityType).get("includeSignatures");
        if(null != signatureNode) {
            includeSignatures = true;
        }
        ReadConfigurator configurator = ReadConfiguratorFactory.getOne(includeSignatures);
        configurator.setIncludeTypeAttributes(requireLDResponse);
        JsonNode resultNode =  readService.getEntity(shard, recordId.getUuid(), entityType, configurator);

        ViewTemplate viewTemplate = viewTemplateManager.getViewTemplate(inputJson);

        if (viewTemplate != null) {
            ViewTransformer vTransformer = new ViewTransformer();
            resultNode = vTransformer.transform(viewTemplate, resultNode);
        }
        logger.info("ReadEntity Id, {} ", recordId.toString());
        logger.debug("readEntity ends");
        return resultNode;

    }
}


package io.opensaber.registry.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opensaber.pojos.OpenSaberInstrumentation;
import io.opensaber.registry.exception.ErrorConstants;
import io.opensaber.registry.exception.OpenSaberException;
import io.opensaber.registry.exception.ReadEntityException;
import io.opensaber.registry.model.DBConnectionInfoMgr;
import io.opensaber.registry.service.DecryptionHelper;
import io.opensaber.registry.service.IReadService;
import io.opensaber.registry.service.ISearchService;
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
    private ISearchService searchService;

    @Autowired
    private ViewTemplateManager viewTemplateManager;

    @Autowired
    private DBConnectionInfoMgr dbConnectionInfoMgr;

    @Autowired
    private DecryptionHelper decryptionHelper;

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
    public String addEntity(JsonNode inputJson, String userId) throws OpenSaberException {
        RecordIdentifier recordId = null;
        try{
            String entityType = inputJson.fields().next().getKey();
            logger.info("Add api: entity type: {} and shard propery: {}", entityType, shardManager.getShardProperty());
            Shard shard = shardManager.getShard(inputJson.get(entityType).get(shardManager.getShardProperty()));
            watch.start("RegistryController.addToExistingEntity");
            String resultId = registryService.addEntity(shard,userId,inputJson);
            recordId = new RecordIdentifier(shard.getShardLabel(), resultId);
            watch.stop("RegistryController.addToExistingEntity");
            logger.info("AddEntity,{}", recordId.toString());
        } /*catch(ClassCastException e) {
            logger.error("Exception in writeNodeEntity",e);
            throw new OpenSaberException("error code for node manipulation", e.getMessage(), ErrorConstants.ENCRYPTION_INTERNAL_ERROR_STATUS);
        }*/
        catch(RuntimeException e) {
            logger.error("Exception in addEntity", e);
            if (e.getMessage().contains("unique constraint")) {
                throw new OpenSaberException(ErrorConstants.DB_CONSTRAINT_ERROR_CODE, e.getMessage(), ErrorConstants.OS_CORE_INTERNAL_ERROR_STATUS);
            } else {
                throw new OpenSaberException(ErrorConstants.OS_CORE_INTERNAL_ERROR_CODE, e.getMessage(), ErrorConstants.OS_CORE_INTERNAL_ERROR_STATUS);
            }
        } catch(OpenSaberException e) {
            logger.error("Exception in addEntity",e);
            throw e;
        } catch(Exception e) {
            logger.error("Exception in addEntity",e);
            throw new OpenSaberException(ErrorConstants.OS_CORE_INTERNAL_ERROR_CODE, e.getMessage(), ErrorConstants.OS_CORE_INTERNAL_ERROR_STATUS);
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
    public JsonNode readEntity(JsonNode inputJson, String userId, boolean requireLDResponse) throws OpenSaberException {
        logger.debug("readEntity starts");
        boolean includeSignatures = false;
        boolean includePrivateFields = false;
        JsonNode resultNode = null;
        try{
            String entityType = inputJson.fields().next().getKey();
            String label = inputJson.get(entityType).get(dbConnectionInfoMgr.getUuidPropertyName()).asText();
            RecordIdentifier recordId = RecordIdentifier.parse(label);
            String shardId = dbConnectionInfoMgr.getShardId(recordId.getShardLabel());
            Shard shard = shardManager.activateShard(shardId);
            logger.info("Read Api: shard id: " + recordId.getShardLabel() + " for label: " + label);
            JsonNode signatureNode = inputJson.get(entityType).get("includeSignatures");
            if (null != signatureNode) {
                includeSignatures = true;
            }
            ReadConfigurator configurator = ReadConfiguratorFactory.getOne(includeSignatures);
            configurator.setIncludeTypeAttributes(requireLDResponse);
            ViewTemplate viewTemplate = viewTemplateManager.getViewTemplate(inputJson);
            if (viewTemplate != null) {
                includePrivateFields = viewTemplateManager.isPrivateFieldEnabled(viewTemplate, entityType);
            }
            configurator.setIncludeEncryptedProp(includePrivateFields);
            resultNode = readService.getEntity(shard, userId, recordId.getUuid(), entityType, configurator);
            if (viewTemplate != null) {
                ViewTransformer vTransformer = new ViewTransformer();
                resultNode = includePrivateFields ? decryptionHelper.getDecryptedJson(resultNode) : resultNode;
                resultNode = vTransformer.transform(viewTemplate, resultNode);
            }
            logger.debug("readEntity ends");
        } catch (OpenSaberException e) {
            logger.error("Exception in readEntity", e);
            throw e;
        } catch (Exception e) {
            logger.error("Exception in readEntity", e);
            throw new OpenSaberException(ErrorConstants.OS_CORE_INTERNAL_ERROR_CODE,e.getMessage(),ErrorConstants.OS_CORE_INTERNAL_ERROR_STATUS);
        }
        return resultNode;

    }

    /**
     * Get entity details from the DB and modifies data according to view template, requests which need only json format can call this method
     * @param inputJson
     * @return
     * @throws Exception
     */
    public JsonNode readEntity(JsonNode inputJson, String userId) throws OpenSaberException {
        return readEntity(inputJson,userId,false);
    }

    /** Search the input in the configured backend, external api's can use this method for searching
     * @param inputJson
     * @return
     * @throws Exception
     */
    public JsonNode searchEntity(JsonNode inputJson) throws Exception {
        logger.debug("searchEntity starts");
        JsonNode resultNode = searchService.search(inputJson);
        ViewTemplate viewTemplate = viewTemplateManager.getViewTemplate(inputJson);
        if (viewTemplate != null) {
            ViewTransformer vTransformer = new ViewTransformer();
            resultNode = vTransformer.transform(viewTemplate, resultNode);
        }
        // Search is tricky to support LD. Needs a revisit here.
        logger.debug("searchEntity ends");
        return resultNode;
    }

    /** Updates the input entity, external api's can use this method to update the entity
     * @param inputJson
     * @param userId
     * @return
     * @throws Exception
     */
    public String updateEntity(JsonNode inputJson, String userId) throws Exception {
        logger.debug("updateEntity starts");
        String entityType = inputJson.fields().next().getKey();
        String jsonString = objectMapper.writeValueAsString(inputJson);
        Shard shard = shardManager.getShard(inputJson.get(entityType).get(shardManager.getShardProperty()));
        String label = inputJson.get(entityType).get(dbConnectionInfoMgr.getUuidPropertyName()).asText();
        RecordIdentifier recordId = RecordIdentifier.parse(label);
        logger.info("Update Api: shard id: " + recordId.getShardLabel() + " for uuid: " + recordId.getUuid());
        registryService.updateEntity(shard, userId, recordId.getUuid(), jsonString);
        logger.debug("updateEntity ends");
        return "SUCCESS";
    }

}


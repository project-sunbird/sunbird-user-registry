package io.opensaber.registry.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import io.opensaber.registry.exception.EncryptionException;
import io.opensaber.registry.service.EncryptionService;
import io.opensaber.registry.util.Definition;
import io.opensaber.registry.util.DefinitionsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DecryptionHelper extends EncryptionHelper{

    public JsonNode getDecryptedJson(JsonNode rootNode) throws EncryptionException {
        JsonNode encryptedRoot = rootNode;
        String rootFieldName = rootNode.fieldNames().next();
        Definition definition = definitionsManager.getDefinition(rootFieldName);
        List<String> privatePropertyLst = definition.getOsSchemaConfiguration().getPrivateFields();
        if (rootNode.isObject()) {
            Map<String, Object> plainMap = super.getToBeEncryptedMap(rootNode, privatePropertyLst);
            if(null != plainMap){
                Map<String, Object> encodedMap = encryptionService.decrypt(plainMap);
                encryptedRoot  = replaceWithEncryptedValues(rootNode, privatePropertyLst, encodedMap);
            }
        }
        return encryptedRoot;
    }

}

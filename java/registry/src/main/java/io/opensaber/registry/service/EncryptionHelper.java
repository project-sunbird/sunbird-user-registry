package io.opensaber.registry.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import io.opensaber.registry.exception.EncryptionException;
import io.opensaber.registry.util.Definition;
import io.opensaber.registry.util.DefinitionsManager;
import io.opensaber.registry.util.PrivateField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EncryptionHelper {
    @Autowired
    private EncryptionService encryptionService;
    @Autowired
    private DefinitionsManager definitionsManager;

    public JsonNode getEncryptedJson(JsonNode rootNode) throws EncryptionException {
        JsonNode encryptedRoot = rootNode;
        String rootFieldName = rootNode.fieldNames().next();
        Definition definition = definitionsManager.getDefinition(rootFieldName);
        List<String> privatePropertyLst = definition.getOsSchemaConfiguration().getPrivateFields();
        if (rootNode.isObject()) {
            Map<String, Object> plainMap = PrivateField.getPrivateFields(rootNode, privatePropertyLst);
            if(null != plainMap){
                Map<String, Object> encodedMap = encryptionService.encrypt(plainMap);
                encryptedRoot  = PrivateField.replacePrivateFields(rootNode, privatePropertyLst, encodedMap);
            }
        }
        return encryptedRoot;
    }

}

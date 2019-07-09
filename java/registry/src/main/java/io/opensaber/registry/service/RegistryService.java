package io.opensaber.registry.service;

import com.fasterxml.jackson.databind.JsonNode;
import io.opensaber.pojos.HealthCheckResponse;
import io.opensaber.registry.sink.shard.Shard;

public interface RegistryService {

	HealthCheckResponse health(Shard shard) throws Exception;

	void deleteEntityById(Shard shard, String id) throws Exception;

	String addEntity(Shard shard, JsonNode inputJson, String userId) throws Exception;

	void updateEntity(Shard shard, String id, String jsonString, String userId) throws Exception;

}

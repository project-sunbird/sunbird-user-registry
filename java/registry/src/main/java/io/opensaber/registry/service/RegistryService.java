package io.opensaber.registry.service;

import io.opensaber.pojos.HealthCheckResponse;
import io.opensaber.registry.sink.shard.Shard;

public interface RegistryService {

	HealthCheckResponse health() throws Exception;

	void deleteEntityById(String id) throws Exception;

	String addEntity(Shard shard, String jsonString, String userId) throws Exception;

	void updateEntity(String id, String jsonString, String userId) throws Exception;

}

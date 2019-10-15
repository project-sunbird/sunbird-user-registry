package io.opensaber.registry.service;

import io.opensaber.registry.exception.ExternalServiceException;

import java.util.Map;

public interface EncryptionService {

	public String encrypt(Object propertyValue) throws ExternalServiceException.ResourceAccessException, ExternalServiceException.InternalException;

	public String decrypt(Object propertyValue) throws ExternalServiceException.ResourceAccessException, ExternalServiceException.InternalException;

	public Map<String, Object> encrypt(Map<String, Object> propertyValue) throws ExternalServiceException.ResourceAccessException, ExternalServiceException.InternalException;

	public Map<String, Object> decrypt(Map<String, Object> propertyValue) throws ExternalServiceException.ResourceAccessException, ExternalServiceException.InternalException;

	public boolean isEncryptionServiceUp();

}

package io.opensaber.registry.exception;

public class ExternalServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8531501706088259947L;

	public static class ResourceAccessException extends OpenSaberException {
		public ResourceAccessException(String message){
			super(ErrorConstants.EXTERNAL_SERVICE_ACCESS_ERROR_CODE,message,ErrorConstants.RESOURCE_ACCESS_ERROR_STATUS);
		}
	}

	public static class InternalException extends OpenSaberException {
		public InternalException(String message){
			super(ErrorConstants.EXTERNAL_SERVICE_INTERNAL_ERROR_CODE,message,ErrorConstants.ENCRYPTION_INTERNAL_ERROR_STATUS);
		}
	}

}

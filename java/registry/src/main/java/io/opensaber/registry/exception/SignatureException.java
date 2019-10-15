package io.opensaber.registry.exception;

public class SignatureException {

	private static final long serialVersionUID = -6315798195661762882L;

	public class CreationException extends OpenSaberException {
		private static final long serialVersionUID = 6174717850058203376L;

		public CreationException(String msg) {
			super(ErrorConstants.EXTERNAL_SERVICE_INTERNAL_ERROR_CODE,"Unable to create signature: " + msg,ErrorConstants.INTERNAL_ERROR_STATUS);
		}
	}

	public class VerificationException extends OpenSaberException {

		private static final long serialVersionUID = 4996784337180620650L;

		public VerificationException(String message) {
			super(ErrorConstants.EXTERNAL_SERVICE_INTERNAL_ERROR_CODE,"Unable to verify signature: " + message,ErrorConstants.INTERNAL_ERROR_STATUS);
		}
	}

	public class UnreachableException extends OpenSaberException {

		private static final long serialVersionUID = 5384120386096139083L;

		public UnreachableException(String message) {
			super(ErrorConstants.EXTERNAL_SERVICE_ACCESS_ERROR_CODE,"Unable to reach service: " + message,ErrorConstants.RESOURCE_ACCESS_ERROR_STATUS);
		}
	}

	public class KeyNotFoundException extends OpenSaberException {

		private static final long serialVersionUID = 8311355815972497247L;

		public KeyNotFoundException(String message) {
			super(ErrorConstants.EXTERNAL_SERVICE_INTERNAL_ERROR_CODE,"Unable to get key: " + message,ErrorConstants.INTERNAL_ERROR_STATUS);
		}
	}
}

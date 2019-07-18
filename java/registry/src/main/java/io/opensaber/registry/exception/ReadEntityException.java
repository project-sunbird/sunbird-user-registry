package io.opensaber.registry.exception;


public class ReadEntityException {

	public static class RecordNotFoundException extends OpenSaberException{

		private static final long serialVersionUID = 8531501706088259947L;

		public RecordNotFoundException() {
			super(ErrorConstants.READ_INACTIVE_ENTITY_ERROR_CODE,ErrorConstants.READ_INACTIVE_ENTITY_MSG,ErrorConstants.INTERNAL_ERROR_STATUS);
		}

	}

	public static class InValidRecordIdException extends OpenSaberException{

		private static final long serialVersionUID = 8531501706088259947L;

		public InValidRecordIdException() {
			super(ErrorConstants.READ_INVALID_ID_ERROR_CODE,ErrorConstants.READ_INVALID_ID_MSG,ErrorConstants.INTERNAL_ERROR_STATUS);
		}

		public InValidRecordIdException(String errMessage) {
			super(ErrorConstants.READ_INVALID_ID_ERROR_CODE,errMessage,ErrorConstants.INTERNAL_ERROR_STATUS);
		}

	}
}


package io.opensaber.registry.exception;

public class OpenSaberException extends Exception{

    String errorCode;
    String errorMessage;
    int status;

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getStatus() {
        return status;
    }

    public OpenSaberException(String errorCode, String errorMessage, int status) {
        super();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.status = status;
    }
}

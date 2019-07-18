package io.opensaber.registry.exception;

public class ErrorConstants {

    //Error codes
    public static String EXTERNAL_SERVICE_ACCESS_ERROR_CODE = "EXTERNAL_SERVICE_ACCESS_ERROR";
    public static String EXTERNAL_SERVICE_INTERNAL_ERROR_CODE = "ENCRYPT_INTERNAL_ERROR";
    public static String READ_INVALID_ID_ERROR_CODE = "READ_INVALID_ID_ERROR";
    public static String READ_INACTIVE_ENTITY_ERROR_CODE = "READ_INACTIVE_ENTITY_ERROR";
    public static String DB_CONSTRAINT_ERROR_CODE = "DB_CONSTRAINT_ERROR";
    public static String READ_INTERNAL_ERROR_CODE = "READ_INTERNAL_ERROR";
    public static String OS_CORE_INTERNAL_ERROR_CODE = "OS_CORE_INTERNAL_ERROR";

    //Error Messages
    public static String READ_INVALID_ID_MSG = "Invalid id";
    public static String READ_INACTIVE_ENTITY_MSG = "entity status is inactive";

    //Status codes
    public static int RESOURCE_ACCESS_ERROR_STATUS = 404;
    public static int ENCRYPTION_INTERNAL_ERROR_STATUS = 500;
    public static int INTERNAL_ERROR_STATUS = 500;
    public static int OS_CORE_INTERNAL_ERROR_STATUS = 500;
}

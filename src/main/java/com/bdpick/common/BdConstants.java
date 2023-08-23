package com.bdpick.common;

public class BdConstants {
    static public final String PREFIX_API_URL = "/api";

    static public final String DIRECTORY_NAME_IMAGES = "images";


    /**
     * Exception class
     */
    public static class Exception {
        static public final String KEY_DUPLICATE_DATA = "DUPLICATE_KEY";
        static public final String KEY_NO_USER = "NO_USER";
        static public final String KEY_NOT_CORRECT = "NOT_CORRECT";
        static public final String KEY_EMAIL_EXIST = "EMAIL_EXIST";

        public static final String NAME_EXCEPTION_DUPLICATE_DATA = "org.hibernate.exception.ConstraintViolationException";
    }
}

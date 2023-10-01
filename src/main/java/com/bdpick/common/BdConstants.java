package com.bdpick.common;

public class BdConstants {
    static public final String PREFIX_API_URL = "/api";

    static public final String DIRECTORY_NAME_IMAGES = "images";


    /**
     * Exception class
     */
    public static class Exception {
        public static final String KEY_DUPLICATE_DATA = "DUPLICATE_KEY";
        public static final String KEY_NO_USER = "NO_USER";
        public static final String KEY_NOT_CORRECT = "NOT_CORRECT";
        public static final String KEY_EMAIL_EXIST = "EMAIL_EXIST";

        public static final String KEY_DUPLICATE_REGISTER = "DUPLICATE_REGISTER";
        public static final String KEY_TOKEN_IS_NOT_CORRECT = "TOKEN_IS_NOT_CORRECT";
        public static final String KEY_TOKEN_EXPIRED = "TOKEN_EXPIRED";



        public static final String MSG_NO_USER = "해당 계정이 존재하지 않습니다.";
        public static final String MSG_NOT_CORRECT = "아이디와 패스워드를 확인해주세요.";
        public static final String MSG_TOKEN_IS_NOT_CORRECT = "유효하지 않은 토큰입니다.";
        public static final String MSG_TOKEN_EXPIRED = "토큰이 만료 됐습니다.";
        public static final String MSG_DUPLICATE_REGISTER = "해당 사업자 번호는 이미 가입되어있습니다.";
        public static final String MSG_EMAIL_EXIST = "이미 사용중인 이메일입니다.";

        public static final String MSG_DATA_DUPLICATE = "이미 등록된 데이터 입니다.";

        public static final String NAME_EXCEPTION_DUPLICATE_DATA = "org.hibernate.exception.ConstraintViolationException";
    }
}

package com.bdpick.common;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class BdUtil {
    public static String getTokenByHeader(Map<String, Object> headerMap) {
        Optional<Map<String, Object>> optionalMap = Optional.ofNullable(headerMap);
        return optionalMap.stream()
                .map(stringObjectMap -> (String) stringObjectMap.get("authorization"))
                .flatMap(s -> Arrays.stream(s.split("Bearer ")))
                .filter(s -> !"".equals(s))
                .findAny()
                .orElse(null);
    }
}

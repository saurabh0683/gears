package com.xy.gears.valve.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by Saurabh on 27-06-2016.
 */
public class Resources {
    public final static String REQUEST_HANDLER_PACKAGE = "request-handler-package";
    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
    }

    public static <T> T mapObject(String value, Class<T> clazz) throws IOException {
        if (null == value || 0 == value.trim().length()) {
            return null;
        }
        return mapper.readValue(value, clazz);
    }

    public static String getJson(Object value) throws JsonProcessingException {
        return mapper.writeValueAsString(value);
    }
}

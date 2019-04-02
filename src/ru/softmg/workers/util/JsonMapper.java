package ru.softmg.workers.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMapper {
    private static ObjectMapper objectMapper = null;

    public synchronized static ObjectMapper getInstance() {
        if(objectMapper == null)
            objectMapper = new ObjectMapper();
        return objectMapper;
    }
}

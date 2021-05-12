package com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class DolphinAppConnUtils {

    /**
     * Gets value from entity.
     *
     * @param entityString the entity string
     * @param searchKey    the search key
     * @return the value from entity
     * @throws IOException the io exception
     */
    public static String getValueFromEntity(String entityString, String searchKey) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(entityString);
        return jsonNode.get(searchKey).asText();
    }

    /**
     * Gets value from json string.
     *
     * @param jsonString the json string
     * @param searchKey  the search key
     * @return the value from json string
     * @throws IOException the io exception
     */
    public static String getValueFromJsonString(String jsonString, String searchKey) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonString);
        return jsonNode.get(searchKey).asText();
    }
}

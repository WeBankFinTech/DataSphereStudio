package com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Objects;

public class DolphinAppConnUtils {

    /**
     * Gets value from entity.
     *
     * @param entityString the entity string
     * @param searchKey    the search key
     * @return the value from entity 返回null，则不存在该key
     * @throws IOException the io exception
     */
    public static String getValueFromEntity(String entityString, String searchKey) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(entityString);
        JsonNode valueNode = jsonNode.get(searchKey);
        return Objects.isNull(valueNode) ? null : valueNode.asText();
    }

    /**
     * Gets code from entity.
     *
     * @param entityString the entity string
     * @return the code from entity 没有code属性，则返回Integer.MIN_VALUE
     * @throws IOException the io exception
     */
    public static int getCodeFromEntity(String entityString) throws IOException {
        String code = getValueFromEntity(entityString, "code");
        return Objects.isNull(code) ? Integer.MIN_VALUE : Integer.valueOf(code);
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
        JsonNode valueNode = jsonNode.get(searchKey);
        return Objects.isNull(valueNode) ? null : valueNode.asText();
    }
}

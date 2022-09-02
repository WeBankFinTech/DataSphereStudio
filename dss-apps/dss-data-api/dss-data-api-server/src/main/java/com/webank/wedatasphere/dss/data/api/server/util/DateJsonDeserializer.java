package com.webank.wedatasphere.dss.data.api.server.util;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateJsonDeserializer extends JsonDeserializer<Date>
{
    public static final ThreadLocal<SimpleDateFormat> format
            = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException
    {
        try
        {
            return format.get().parse(jsonParser.getText());
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

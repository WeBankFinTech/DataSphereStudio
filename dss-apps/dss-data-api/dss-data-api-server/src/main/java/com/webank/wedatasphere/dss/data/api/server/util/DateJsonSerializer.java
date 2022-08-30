package com.webank.wedatasphere.dss.data.api.server.util;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateJsonSerializer extends JsonSerializer<Date>
{
    public static final ThreadLocal<SimpleDateFormat> format
            = ThreadLocal.withInitial (()->new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException, JsonProcessingException
    {
        jsonGenerator.writeString(format.get().format(date));
    }
}

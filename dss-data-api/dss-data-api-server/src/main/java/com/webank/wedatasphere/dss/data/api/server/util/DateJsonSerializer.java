package com.webank.wedatasphere.dss.data.api.server.util;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Classname DateJsonSerializer
 * @Description 自定义类型转换格式：时间字符串与java.util.Date
 * @Date 2021/7/14 9:44
 * @Created by suyc
 */
public class DateJsonSerializer extends JsonSerializer<Date>
{
    public static final SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException, JsonProcessingException
    {
        jsonGenerator.writeString(format.format(date));
    }
}

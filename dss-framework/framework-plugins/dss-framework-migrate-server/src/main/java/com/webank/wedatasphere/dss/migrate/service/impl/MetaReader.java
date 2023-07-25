package com.webank.wedatasphere.dss.migrate.service.impl;

import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import org.apache.linkis.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class MetaReader<T> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Class<T> tClass;
    private final String tableName;
    private String commentPrefix = "#";
    private String seperator = "\\|";
    private List<T> datas = new ArrayList<>();
    private List<String> fields = null;
    private List<String> comments = new ArrayList<>();
    private List<List<String>> body = new ArrayList<>();
    private boolean finded = false;
    private boolean firstLine = true;

    public MetaReader(Class<T> tClass, String tableName) {
        this.tClass = tClass;
        this.tableName = tableName;
    }

    public static <T> MetaReader<T> of(String tableName, Class<T> tClass) {
        return new MetaReader<>(tClass, tableName);
    }

    public List<T> read(InputStream inputStream) throws IOException {
        try (InputStreamReader streamReader = new InputStreamReader(inputStream);
             BufferedReader reader = new BufferedReader(streamReader);) {
            readTable(reader);
        }
        readT();
        return datas;
    }

    public String read(InputStream inputStream, String key) throws IOException {
        try (InputStreamReader streamReader = new InputStreamReader(inputStream);
             BufferedReader reader = new BufferedReader(streamReader);) {
            readTable(reader);
        }
        String comment = comments.stream().filter(c -> c.contains(key + ":")).findFirst().orElse("");
        String[] split = comment.split(":");
        if (split.length > 1) return split[1];
        return "";
    }

    private void readT() {
        body.stream().map(DSSExceptionUtils.map(this::lineToT)).forEach(datas::add);
    }

    private T lineToT(List<String> list) throws IllegalAccessException, InstantiationException, ParseException {
        T t = tClass.newInstance();
        for (int i = 0; i < list.size(); i++) {
            try{
                String valueStr = list.get(i);
                if ("null".equalsIgnoreCase(valueStr)) continue;
                Field declaredField;
                try {
                    declaredField = tClass.getDeclaredField(fields.get(i));
                }catch ( java.lang.NoSuchFieldException e){
                    continue;
                }
                declaredField.setAccessible(true);
                Object value = null;
                String type = declaredField.getType().getSimpleName();
                switch (type) {
                    case "String":
                        value = valueStr;
                        break;
                    case "Date":
                        value = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZ yyyy", Locale.ENGLISH).parse(valueStr);
                        break;
                    case "Long":
                        value = Long.valueOf(valueStr);
                        break;
                    case "Boolean":
                        value = Boolean.valueOf(valueStr);
                        break;
                    case "Integer":
                        //由于两个版本字段类型发生变化，先做下适配处理
                        if (declaredField.getName().equalsIgnoreCase("isPersonal")) {
                            if ("true".equals(valueStr)) {
                                value = Integer.valueOf(1);
                            } else {
                                value = Integer.valueOf(0);
                            }
                        } else {
                            value = Integer.valueOf(valueStr);
                        }
                        break;
                    default:
                        logger.warn(String.format("unsupport type %s", type));
                }

                declaredField.set(t, value);
            }catch (Exception e){
                logger.warn("设置对象属性失败", e);
            }

        }
        return t;
    }

    private void readTable(BufferedReader reader) throws IOException {
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (!finded && !shut(line)) {
                continue;
            }
            if (finded && isTableName(line)) {
                break;
            }
            if (shut(line)) {
                finded = true;
                continue;
            }
            // TODO: 2020/3/9
            if (isComment(line)) {
                comments.add(line);
                continue;
            }
            if (firstLine) {
                //handle head
                fields = Arrays.stream(line.split(seperator)).collect(Collectors.toList());
                firstLine = false;
                continue;
            }
            body.add(Arrays.stream(line.split(seperator)).collect(Collectors.toList()));
            //handle body
        }
    }

    private boolean isComment(String str) {
        return str.startsWith(commentPrefix);
    }

    private boolean isTableName(String str) {
        return isComment(str) && str.contains(commentPrefix + "tableName:");
    }

    private boolean isClass(String str) {
        return isComment(str) && str.contains(commentPrefix + "class:");
    }

    private String getComment(String str) {
        return str.substring(1);
    }

    private boolean shut(String str) {
        return isTableName(str) && getComment(str).equals(String.format("tableName:%s", tableName));
    }
}

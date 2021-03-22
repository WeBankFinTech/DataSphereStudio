/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.orchestrator.publish.io.export;

import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.orchestrator.core.conf.DSSOrchestratorConf;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MetaWriter<T> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Class<T> tClass;
    private final String tableName;
    private List<T> datas = new ArrayList<>();
    private List<String> ignoreFields = new ArrayList<>();
    private List<String> fields = null;
    private List<String> comments = new ArrayList<>();
    private List<String> table = new ArrayList<>();
    private String commentPrefix = "#";
    private String seperator = "|";

    public MetaWriter(Class<T> tClass, String tableName) {
        this.tClass = tClass;
        this.tableName = tableName;
    }

    public static <T> MetaWriter<T> of(String tableName, Class<T> tClass) {
        return new MetaWriter<>(tClass, tableName);
    }

    public MetaWriter<T> ignore(String... fields) {
        ignoreFields.addAll(Arrays.stream(fields).collect(Collectors.toList()));
        return this;
    }

    public MetaWriter<T> data(List<T> datas) {
        this.datas.addAll(datas);
        return this;
    }

    public MetaWriter<T> data(T data) {
        this.datas.add(data);
        return this;
    }

    public MetaWriter<T> comment(String... comments) {
        this.comments.addAll(Arrays.stream(comments).collect(Collectors.toList()));
        return this;
    }

    /**
     * 上传bml
     *
     * @return
     */
    public InputStream write() {
        writeComment();
        writeHead();
        writeBody();
        //table 添加换行符进行转流
        String tableStr = table.stream().reduce((a, b) -> a + "\n" + b).orElse("") + "\n";
        logger.info("\n" + tableStr);
        return new ByteArrayInputStream(tableStr.getBytes());
    }

    /**
     * 写入本地
     *
     * @param outputStream
     */
    public void write(OutputStream outputStream) throws IOException {
        writeComment();
        writeHead();
        writeBody();
        IOUtils.writeLines(table, "\n", outputStream);
    }

    private void writeBody() {
        datas.forEach(DSSExceptionUtils.handling(this::writeBody));
    }

    private void writeBody(T t) throws NoSuchFieldException, IllegalAccessException {
        ArrayList<String> line = new ArrayList<>();
        for (String field : fields) {
            Field declaredField = tClass.getDeclaredField(field);
            declaredField.setAccessible(true);
            Object o = declaredField.get(t);
            if (o != null) {
                line.add(o.toString());
            } else {
                line.add(null);
            }
        }
        table.add(reduce(line));
    }

    private String reduce(List<String> strs) {
        return strs.stream().reduce((a, b) -> a + seperator + b).orElse("");
    }

    /**
     * 写表的头部
     * 1.过滤ignore的属性
     * 2.驼峰转mysql的 _ (暂时略过)
     * 3.写入List<String> table
     */
    private void writeHead() {
        fields = Arrays.stream(tClass.getDeclaredFields())
                .map(Field::getName)
                .filter(n -> !ignoreFields.contains(n))
                .collect(Collectors.toList());
        table.add(reduce(fields));
    }

    /**
     * 驼峰转_
     *
     * @param str
     * @return
     */
    private String unCamel(String str) {
        // TODO: 2020/3/9
        return null;
    }

    /**
     * 写comment,包括表名,class名,和外部自定义的comment
     */
    private void writeComment() {
        table.add(connectCommentPrefix(String.format("tableName:%s", tableName)));
        table.add(connectCommentPrefix(String.format("class:%s", tClass.getName())));
        table.add(connectCommentPrefix(String.format("env:%s", DSSOrchestratorConf.DSS_EXPORT_ENV.getValue())));
        comments.stream().map(this::connectCommentPrefix).forEach(table::add);
    }

    /**
     * str前加上comment标识
     *
     * @param str
     * @return
     */
    private String connectCommentPrefix(String str) {
        return commentPrefix + str;
    }

}

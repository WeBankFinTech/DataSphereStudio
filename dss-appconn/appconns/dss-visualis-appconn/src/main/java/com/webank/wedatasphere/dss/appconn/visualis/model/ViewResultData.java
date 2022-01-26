package com.webank.wedatasphere.dss.appconn.visualis.model;

import java.util.List;
import java.util.Map;

public class ViewResultData extends HttpResponseModel {
    private Data data;

    public static class Column {
        private String name;
        private String type;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }


    public static class Data {
        private List<Column> columns;
        private List<Map<String, Object>> resultList;

        public List<Column> getColumns() {
            return columns;
        }

        public void setColumns(List<Column> columns) {
            this.columns = columns;
        }

        public List<Map<String, Object>> getResultList() {
            return resultList;
        }

        public void setResultList(List<Map<String, Object>> resultList) {
            this.resultList = resultList;
        }
    }


    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}

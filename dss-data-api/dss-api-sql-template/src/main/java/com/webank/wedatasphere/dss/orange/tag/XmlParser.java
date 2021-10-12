package com.webank.wedatasphere.dss.orange.tag;

import com.webank.wedatasphere.dss.orange.node.MixedSqlNode;
import com.webank.wedatasphere.dss.orange.node.SqlNode;
import com.webank.wedatasphere.dss.orange.node.TextSqlNode;
import org.dom4j.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class XmlParser {

    static Map<String, TagHandler> nodeHandlers = new HashMap<String, TagHandler>() {
        {
            put("foreach", new ForeachHandler());
            put("if", new IfHandler());
            put("trim", new TrimHandler());
            put("where", new WhereHandler());
            put("set", new SetHandler());
        }
    };

    //将xml内容解析成sqlNode类型

    public static SqlNode parseXml2SqlNode(String text) {

        Document document = null;
        try {
            document = DocumentHelper.parseText(text);
        } catch (DocumentException e) {
            throw new RuntimeException(e.getMessage());
        }
        Element rootElement = document.getRootElement();
        List<SqlNode> contents = parseElement(rootElement);
        SqlNode sqlNode = new MixedSqlNode(contents);
        return sqlNode;
    }

    //解析单个标签的子内容，转化成sqlNode list

    public static List<SqlNode> parseElement(Element element) {
        List<SqlNode> nodes = new ArrayList<>();

        List<Object> children = element.content();
        for (Object node : children) {
            if (node instanceof Text) {
                TextSqlNode textSqlNode = new TextSqlNode(((Text) node).getText());
                nodes.add(textSqlNode);

            } else if (node instanceof Element) {
                String nodeName = ((Element) node).getName();
                TagHandler handler = nodeHandlers.get(nodeName.toLowerCase());
                if (handler == null) {
                    throw new RuntimeException("tag not supported");
                }
                //内部递归调用此方法
                handler.handle((Element) node, nodes);

            }

        }

        return nodes;

    }

    public static void main(String[] args) {
        parseXml2SqlNode("<a>111<if test='true'>222<if test='true'>333</if>444<foreach collection='list' open='(' close=')' separator=',' item='item'>fff</foreach></if>555</a>");
    }
}

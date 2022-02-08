package com.webank.wedatasphere.dss.orange.tag;

import com.webank.wedatasphere.dss.orange.node.MixedSqlNode;
import com.webank.wedatasphere.dss.orange.node.SqlNode;
import com.webank.wedatasphere.dss.orange.node.WhereSqlNode;

import java.util.List;


import org.dom4j.Element;

public class WhereHandler implements TagHandler {

    @Override
    public void handle(Element element, List<SqlNode> targetContents) {
        List<SqlNode> contents = XmlParser.parseElement(element);

        WhereSqlNode node = new WhereSqlNode(new MixedSqlNode(contents));
        targetContents.add(node);
    }
}

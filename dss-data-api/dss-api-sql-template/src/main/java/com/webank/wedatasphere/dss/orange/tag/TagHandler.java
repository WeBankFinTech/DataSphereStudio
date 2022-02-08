package com.webank.wedatasphere.dss.orange.tag;

import com.webank.wedatasphere.dss.orange.node.SqlNode;

import java.util.List;


import org.dom4j.Element;

public interface TagHandler {

    void handle(Element element, List<SqlNode> contents);
}

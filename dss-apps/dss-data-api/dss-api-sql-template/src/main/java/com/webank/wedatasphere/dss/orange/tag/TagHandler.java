package com.webank.wedatasphere.dss.orange.tag;

import com.webank.wedatasphere.dss.orange.node.SqlNode;
import org.dom4j.Element;

import java.util.List;

public interface TagHandler {

    void handle(Element element, List<SqlNode> contents);
}

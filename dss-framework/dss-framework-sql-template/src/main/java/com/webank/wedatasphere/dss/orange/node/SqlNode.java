package com.webank.wedatasphere.dss.orange.node;

import com.webank.wedatasphere.dss.orange.context.Context;

import java.util.Set;


public interface SqlNode {

    void apply(Context context);

    void applyParameter(Set<String> set);

}

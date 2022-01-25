package com.webank.wedatasphere.dss.orange.node;

import com.webank.wedatasphere.dss.orange.context.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Set;


public class TrimSqlNode implements SqlNode {

    SqlNode contents;
    String prefix;
    String suffix;
    List<String> prefixesToOverride;
    List<String> suffixesToOverride;

    public TrimSqlNode(SqlNode contents, String prefix, String suffix, List<String> prefixesToOverride, List<String> suffixesToOverride) {
        this.contents = contents;
        this.prefix = prefix;
        this.suffix = suffix;
        this.prefixesToOverride = prefixesToOverride;
        this.suffixesToOverride = suffixesToOverride;
    }

    @Override
    public void apply(Context context) {
        context.appendSql(" ");//标签类SqlNode先拼接空格，和前面的内容隔开
        Context proxy = new Context(context.getData());
//        FilterContext filterContext = new FilterContext(context);
        contents.apply(proxy);
        String sql = proxy.getSql().trim();

        if (sql.length() > 0) {
            if (prefixesToOverride != null)
                for (String key : prefixesToOverride) {
                    if (sql.startsWith(key)) {
                        sql = sql.substring(key.length());
                    }
                }
            if (suffixesToOverride != null)
                for (String key : suffixesToOverride) {
                    if (sql.endsWith(key)) {
                        sql = sql.substring(0, sql.length() - key.length());
                    }
                }
        }

        if (StringUtils.isNotBlank(sql) && StringUtils.isNotBlank(prefix)) {
            context.appendSql(prefix);
        }

        context.appendSql(sql);

        if (StringUtils.isNotBlank(sql) && StringUtils.isNotBlank(suffix)) {
            context.appendSql(suffix);
        }

    }

    @Override
    public void applyParameter(Set<String> set) {
        contents.applyParameter(set);
    }
}

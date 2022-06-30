package com.webank.wedatasphere.dss.orange.node;

import com.webank.wedatasphere.dss.orange.context.Context;
import com.webank.wedatasphere.dss.orange.token.TokenHandler;
import com.webank.wedatasphere.dss.orange.token.TokenParser;

import java.util.Set;


public class TextSqlNode implements SqlNode {

    String text;

    public TextSqlNode(String text) {
        this.text = text;
    }

    @Override
    public void apply(Context context) {
        //解析常量值 ${xxx}
        TokenParser tokenParser = new TokenParser("${", "}", new TokenHandler() {
            @Override
            public String handleToken(String paramName) {
                Object value = context.getOgnlValue(paramName);
                return value == null ? "" : value.toString();
            }
        });
        String s = tokenParser.parse(text);


        context.appendSql(s);

    }

    @Override
    public void applyParameter(Set<String> set) {
        TokenParser tokenParser = new TokenParser("${", "}", new TokenHandler() {
            @Override
            public String handleToken(String paramName) {
                set.add(paramName);
                return paramName;
            }
        });
        String s = tokenParser.parse(text);

        TokenParser tokenParser2 = new TokenParser("#{", "}", new TokenHandler() {
            @Override
            public String handleToken(String paramName) {
                set.add(paramName);
                return paramName;
            }
        });
        tokenParser2.parse(s);
    }
}

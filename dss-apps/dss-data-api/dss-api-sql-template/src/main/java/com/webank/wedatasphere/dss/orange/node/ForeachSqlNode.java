package com.webank.wedatasphere.dss.orange.node;

import com.webank.wedatasphere.dss.orange.context.Context;
import com.webank.wedatasphere.dss.orange.token.TokenHandler;
import com.webank.wedatasphere.dss.orange.token.TokenParser;
import com.webank.wedatasphere.dss.orange.util.OgnlUtil;
import com.webank.wedatasphere.dss.orange.util.RegexUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class ForeachSqlNode implements SqlNode {

    String collection;
    String open;
    String close;
    String separator;
    String item;
    String index;
    SqlNode contents;

    String indexDataName;

    public ForeachSqlNode(String collection, String open, String close, String separator, String item, String index, SqlNode contents) {
        this.collection = collection;
        this.open = open;
        this.close = close;
        this.separator = separator;
        this.item = item;
        this.index = index;
        this.contents = contents;

        this.indexDataName = String.format("__index_%s", collection);
    }

    @Override
    public void apply(Context context) {
        context.appendSql(" ");//标签类SqlNode先拼接空格，和前面的内容隔开
        Iterable<?> iterable = OgnlUtil.getIterable(collection, context.getData());
        int currentIndex = 0;

        ArrayList<Integer> indexs = new ArrayList<>();

        context.getData().put(indexDataName, indexs);
        context.appendSql(open);

        for (Object o : iterable) {

            ((ArrayList<Integer>) context.getData().get(indexDataName)).add(currentIndex);
            //不是第一次，需要拼接分隔符
            if (currentIndex != 0) {
                context.appendSql(separator);
            }

            Context proxy = new Context(context.getData());
            String childSqlText = getChildText(proxy, currentIndex);
            context.appendSql(childSqlText);

            currentIndex++;
        }

        context.appendSql(close);

    }

    @Override
    public void applyParameter(Set<String> set) {
        set.add(collection);
        Set<String> temp = new HashSet<>();
        contents.applyParameter(set);
        for (String key: temp){
            if (key.matches(item + "[.,:\\s\\[]"))
                continue;
            if (key.matches(index + "[.,:\\s\\[]"))
                continue;
            set.add(key);
        }
    }

    public String getChildText(Context proxy, int currentIndex) {
        String newItem = String.format("%s[%d]", collection, currentIndex);  //ognl可以直接获取  aaa[0]  形式的值
        String newIndex = String.format("%s[%d]", indexDataName, currentIndex);
        this.contents.apply(proxy);
        String sql = proxy.getSql();
        TokenParser tokenParser = new TokenParser("#{", "}", new TokenHandler() {
            @Override
            public String handleToken(String content) {
                //item替换成自己的变量名: item[0]  item[1] item[2] ......
                String replace = RegexUtil.replace(content, item, newItem);
                if (replace.equals(content))
                    //index替换成自己的变量名: __index_xxx[0]  __index_xxx[1] __index_xxx[2] ......
                    replace = RegexUtil.replace(content, index, newIndex);
                StringBuilder builder = new StringBuilder();
                return builder.append("#{").append(replace).append("}").toString();
            }
        });
        String parse = tokenParser.parse(sql);
        return parse;
    }

}

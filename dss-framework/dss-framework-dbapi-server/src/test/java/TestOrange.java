/*

import com.webank.wedatasphere.dss.orange.SqlMeta;
import com.webank.wedatasphere.dss.orange.engine.DynamicSqlEngine;
//import org.codehaus.jettison.json.JSONArray;
//import org.codehaus.jettison.json.JSONException;
//import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

*/
/**
 * @program: orange
 * @description:
 * @author: jiangqiang
 * @create: 2021-02-23 10:19
 **//*

public class TestOrange {

    @Test
    public void test() {
        StringBuilder builder = new StringBuilder();
        String a = null;
        builder.append("abc").append(a).append("333");
        System.out.println(builder.toString());
    }

    @Test
    public void testIf() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = "id &lt;= #{maxId}";
        Map<String, Object> map = new HashMap<>();
        map.put("maxId", 10);

        SqlMeta sqlMeta = engine.parse(sql, map);
        System.out.println(sqlMeta.getSql());
        sqlMeta.getJdbcParamValues().forEach(System.out::println);

    }

    @Test
    public void testTrim() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = "<trim prefix='(' suffix=')' suffixesToOverride=',' prefixesToOverride='and' ><foreach collection='list' index='idx' open='(' separator=',' close=')'>#{item.name}== #{idx}</foreach><if test='id!=null'>  and xyz.,</if></trim>";
        Map<String, Object> map = new HashMap<>();
        map.put("id", 2);
        ArrayList<User> arrayList = new ArrayList<>();
        arrayList.add(new User(10, "tom"));
        arrayList.add(new User(11, "jerry"));
        map.put("list", arrayList);

        SqlMeta sqlMeta = engine.parse(sql, map);
        System.out.println(sqlMeta.getSql());
        sqlMeta.getJdbcParamValues().forEach(System.out::println);
    }

    @Test
    public void testWhere() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = "<where><if test='id!=null'>  and id = #{id}</if><if test='id!=null'>  and id = #{id}</if></where>";
        Map<String, Object> map = new HashMap<>();
        map.put("id", 2);
        ArrayList<User> arrayList = new ArrayList<>();
        arrayList.add(new User(10, "tom"));
        arrayList.add(new User(11, "jerry"));
        map.put("list", arrayList);

        SqlMeta sqlMeta = engine.parse(sql, map);
        System.out.println(sqlMeta.getSql());
        sqlMeta.getJdbcParamValues().forEach(System.out::println);
    }

    @Test
    public void testForeach() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = ("select * from user where name in <foreach collection='list' index='idx' open='(' separator=',' close=')'>#{item.name}== #{idx}</foreach>");
        Map<String, Object> map = new HashMap<>();

        ArrayList<User> arrayList = new ArrayList<>();
        arrayList.add(new User(10, "tom"));
        arrayList.add(new User(11, "jerry"));
        map.put("list", arrayList.toArray());

        SqlMeta sqlMeta = engine.parse(sql, map);
        System.out.println(sqlMeta.getSql());
        sqlMeta.getJdbcParamValues().forEach(System.out::println);
    }

    @Test
    public void testForeachIF() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = ("select * from user where name in <foreach collection='list' index='idx' open='(' separator=',' close=')'>#{item.name}== #{idx}<if test='id!=null'>  and id = #{id}</if></foreach>");
        Map<String, Object> map = new HashMap<>();

        ArrayList<User> arrayList = new ArrayList<>();
        arrayList.add(new User(10, "tom"));
        arrayList.add(new User(11, "jerry"));
        map.put("list", arrayList.toArray());
        map.put("id", 100);

        SqlMeta sqlMeta = engine.parse(sql, map);
        System.out.println(sqlMeta.getSql());
        sqlMeta.getJdbcParamValues().forEach(System.out::println);
    }

*/
/*
    @Test
    public void testForeachMap() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = ("<foreach collection='users' open='(' separator=',' close=')'>#{item}</foreach>");
        Map<String, Object> map = new HashMap<>();

        Map<String, Object> users = new HashMap<String, Object>() {
            {
                put("aaa", "a1");
                put("bbb", "b1");
            }
        };

        map.put("users", users);

        SqlMeta sqlMeta = engine.parse(sql, map);
        System.out.println(sqlMeta.getSql());
        sqlMeta.getJdbcParamValues().forEach(System.out::println);
    }
*//*


    @Test
    public void testMultiForeach() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = ("<foreach collection='list' open='(' separator=',' close=')'>#{item}</foreach><foreach collection='list2' open='{' separator=',' close='}'>#{item}</foreach>");
        Map<String, Object> map = new HashMap<>();

        ArrayList<String> list = new ArrayList<String>() {{
            add("a");
            add("b");
        }};

        map.put("list", list);

        ArrayList<String> list2 = new ArrayList<String>() {{
            add("c");
            add("d");
        }};

        map.put("list2", list2.toArray());

        SqlMeta sqlMeta = engine.parse(sql, map);
        System.out.println(sqlMeta.getSql());
        sqlMeta.getJdbcParamValues().forEach(System.out::println);
    }

    @Test
    public void testSet() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = ("update<set><if test='id !=null'> id = #{id} ,</if><if test='id !=null'> id = #{id} , </if></set>");
        Map<String, Object> map = new HashMap<>();
        map.put("id",10);
        SqlMeta sqlMeta = engine.parse(sql, map);
        System.out.println(sqlMeta.getSql());
        sqlMeta.getJdbcParamValues().forEach(System.out::println);

    }

    @Test
    public void testParseParam() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = ("<foreach collection='list' open='(' separator=',' close=')'>#{item.name} #{item} #{id} ${indexName} </foreach><where><if test='id!=null'>  and id = #{mid}</if> ${name}</where>");
        Set<String> set = engine.parseParameter(sql);
        set.stream().forEach(System.out::println);
    }

}
*/

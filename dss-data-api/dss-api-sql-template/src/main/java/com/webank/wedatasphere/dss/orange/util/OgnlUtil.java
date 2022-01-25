package com.webank.wedatasphere.dss.orange.util;

import ognl.Ognl;
import ognl.OgnlException;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OgnlUtil {

    public static Object getValue(String expression, Map<String, Object> root) {
        try {
            Map context = Ognl.createDefaultContext(root);
            Object value = Ognl.getValue(Ognl.parseExpression(expression), context, root);
            return value;
        } catch (OgnlException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Boolean getBooleanValue(String expression, Map<String, Object> root) {
        Object value = getValue(expression, root);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof Number)
            return !new BigDecimal(String.valueOf(value)).equals(BigDecimal.ZERO);
        else
            throw new RuntimeException("expression value is not boolean or number type: " + expression);
    }

    public static Iterable<?> getIterable(String expression, Map<String, Object> root) {
        Object value = getValue(expression, root);
        if (value == null)
            throw new RuntimeException("The expression '" + expression + "' evaluated to a null value.");
        if (value instanceof Iterable)
            return (Iterable<?>) value;
        if (value.getClass().isArray()) {
            // the array may be primitive, so Arrays.asList() may throw
            // a ClassCastException (issue 209). Do the work manually
            // Curse primitives! :) (JGB)
            int size = Array.getLength(value);
            List<Object> answer = new ArrayList<Object>();
            for (int i = 0; i < size; i++) {
                Object o = Array.get(value, i);
                answer.add(o);
            }
            return answer;
        }
        if (value instanceof Map) {
            return ((Map) value).entrySet();
        }
        throw new RuntimeException("Error evaluating expression '" + expression + "'.  Return value (" + value + ") was not iterable.");
    }

    public static void main(String[] args) {
        Map<String, Object> root = new HashMap<>();
        List<Integer> list = new ArrayList<>();
        list.add(12);
        list.add(22);
        list.add(32);
        list.add(42);
        root.put("ids", list);

        Object o = getValue("ids[3]", root);
        System.out.println(o);

    }
}

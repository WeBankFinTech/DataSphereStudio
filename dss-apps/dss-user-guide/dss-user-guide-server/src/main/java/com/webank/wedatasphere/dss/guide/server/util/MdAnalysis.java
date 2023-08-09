package com.webank.wedatasphere.dss.guide.server.util;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MdAnalysis {

    private static final Logger logger = LoggerFactory.getLogger(MdAnalysis.class);

    private final static Pattern ATTR_PATTERN3 = Pattern.compile("(?<=\\[)(.+?)(?=\\])");

    private final static Pattern ATTR_PATTERN2 = Pattern.compile("(?<=\\()(.+?)(?=\\))", Pattern.CASE_INSENSITIVE);

    private final static Pattern ATTR_PATTERN1 = Pattern.compile("<a[^>]*href=(\"([^\"]*)\"|'([^']*)'|([^\\s>]*))[^>]*>");

    private static int ROOT_COUNT = 0;

    private static int Y = 0;

    private static int Z = 0;


    /**
     * 读取MD文件
     *
     * @param filePath
     * @return
     */
    public static String readMd(String filePath) {
        logger.info("开始读取md文件：" + filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            logger.info("文件不存在！" + filePath);
            return null;
        }
        FileReader fr = null;
        BufferedReader br = null;
        StringBuffer sb = new StringBuffer("");
        try {
            fr = new FileReader(filePath);
            br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
        } catch (IOException e) {
            logger.error("文件读取异常===》" + e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
                logger.error(String.valueOf(e));
            }
        }
        return sb.toString();
    }

    /**
     * 解析SUMMARY.md文件
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static List<Map<String, Map<String, String>>> analysisMd(String filePath, String type, String ignoreModel) throws IOException {
        logger.info("开始解析summary.md文件=============》");
        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"))){
            List<Map<String, Map<String, String>>> mapList = new ArrayList<>();
            String line = null;
            boolean flag = false;
            while ((line = br.readLine()) != null) {
                Map<String, Map<String, String>> map = new HashMap<>();
                Map<String, String> dataMap = new HashMap<>();
                if (StringUtils.equals(type, "guide")) {
                    if (StringUtils.equals(line.trim(), type)) {
                        logger.info("开始解析学习引导模块");
                        flag = true;
                    }
                    if (StringUtils.equals(line.trim(), "knowledge") || ignoreModel.contains(line.trim())) {
                        flag = false;
                    }
                }
                if (StringUtils.equals(type, "knowledge")) {
                    if (StringUtils.equals(line.trim(), type)) {
                        logger.info("开始解析知识库模块");
                        flag = true;
                    }
                    if (StringUtils.equals(line.trim(), "guide") || ignoreModel.contains(line.trim())) {
                        flag = false;
                    }
                }
                if (flag) {
                    absolveKnowledge(line, dataMap, map, mapList);
                }
            }
            Y = 0;
            Z = 0;
            return mapList;
        }
    }

    private static void absolveKnowledge(String line, Map<String, String> dataMap, Map<String, Map<String, String>> map, List<Map<String, Map<String, String>>> mapList) {
        if (line.startsWith("  -")) {
            ROOT_COUNT++;
            Y = 0;
            matcherContent(line, dataMap);
            map.put(String.valueOf(ROOT_COUNT), dataMap);
            mapList.add(map);
        } else if (line.startsWith("    -")) {
            Y++;
            Z = 0;
            matcherContent(line, dataMap);
            map.put(ROOT_COUNT + "-" + Y, dataMap);
            mapList.add(map);
        } else if (line.startsWith("      -")) {
            Z++;
            matcherContent(line, dataMap);
            map.put(ROOT_COUNT + "-" + Y + "-" + Z, dataMap);
            mapList.add(map);
        } else if (line.trim().startsWith("/")) {
            dataMap.put("path",line);
            map.put("url",dataMap);
            mapList.add(map);
        }
    }

    /**
     * 获取跟目录数量
     *
     * @return
     */
    public static Long getRootCount() {
        return Long.valueOf(ROOT_COUNT);
    }

    public static void rootCountInit() {
        ROOT_COUNT = 0;
    }

    ;

    /**
     * 解析SUMMARY中的标题和文件
     *
     * @param line
     * @param dataMap
     */
    private static void matcherContent(String line, Map<String, String> dataMap) {
        Matcher matcher1 = ATTR_PATTERN3.matcher(line);
        while (matcher1.find()) {
            dataMap.put("title", matcher1.group(1) == null ? "" : matcher1.group(1));
        }
        Matcher matcher2 = ATTR_PATTERN2.matcher(line);
        while (matcher2.find()) {
            dataMap.put("file", matcher2.group(1) == null ? "" : matcher2.group(1));
        }
    }

    /**
     * md转html
     *
     * @param filePath
     * @return
     */
    public static String markdown2Html(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        MutableDataSet OPTIONS = new MutableDataSet();
        InputStream stream = new FileInputStream(filePath);
        String htmlContent = IOUtils.toString(stream, "UTF-8");
        Parser parser = Parser.builder(OPTIONS).build();
        HtmlRenderer renderer = HtmlRenderer.builder(OPTIONS).build();
        Node document = parser.parse(htmlContent);
        return renderer.render(document);
    }

    public static String changeHtmlTagA(String htmlContent)throws IOException{
        if(StringUtils.isEmpty(htmlContent)){
            return htmlContent;
        }
        //判断是否含有<a>标签
        if(!htmlContent.contains("<a href")){
            return htmlContent;
        }
        Matcher matcher = ATTR_PATTERN1.matcher(htmlContent);
        String tagA = "";
        String href = "";
        if (matcher.find()) {
            tagA = matcher.group(0);
            href = matcher.group(2);
        }
        String replaceContent = htmlContent.replace(tagA, "<a target=\"_blank\" href = "+"\"/_book" + href +"\"" +">");
        return replaceContent;
    }

    private static String matcherDir(String line) {
        Matcher matcher = ATTR_PATTERN2.matcher(line);
        return matcher.find() == true ? matcher.group(1) : null;
    }

    /**
     * 解析一级标题
     *
     * @param str
     * @return
     */
    public static boolean isStr2Num(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 解析二级三级标题
     *
     * @param str
     * @return
     */
    public static int isSecondOrThird(String str) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            int i1 = str.indexOf("-", i);
            if (i1 == i) {
                count++;
            }
        }
        return count;
    }

}

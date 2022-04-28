package com.webank.wedatasphere.dss.guide.server.util;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadMdFile {

    private static final Logger logger = LoggerFactory.getLogger(ReadMdFile.class);

    private final static Pattern ATTR_PATTERN3 = Pattern.compile("(?<=\\[)(.+?)(?=\\])");

    private final static Pattern ATTR_PATTERN2 = Pattern.compile("(?<=\\()(.+?)(?=\\))", Pattern.CASE_INSENSITIVE);

    private static int ROOT_COUNT = 0;


    /**
     * 读取MD文件
     * @param filePath
     * @return
     */
    public static String readMd(String filePath){
        logger.info("开始读取md文件===========》》》》》》");
        File file = new File(filePath);
        if(!file.exists()){
            logger.info("文件不存在！");
            return null;
        }
        FileReader fr = null;
        StringBuffer sb = new StringBuffer("");
        try {
            fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
        } catch (IOException e) {
            logger.error("文件读取一场===》"+ e);
            throw new RuntimeException(e);
        } finally {
            try {
                fr.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return sb.toString();
    }

    /**
     * 解析SUMMARY.md文件
     * @param filePath
     * @return
     * @throws IOException
     */
    public static List<Map<String, Map<String, String>>>  analysisMd(String filePath) throws IOException{
        logger.info("开始读取summary.md文件=============》");
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"));
        List<Map<String, Map<String, String>>> mapList = new ArrayList<>();
        String line = null;
        int y = 0;
        int z = 0;
        while ((line = br.readLine()) != null) {
            Map<String, Map<String, String>> map = new HashMap<>();
            Map<String, String> dataMap = new HashMap<>();
            if (line.startsWith("-")) {
                ROOT_COUNT++;
                y = 0;
                matcherContent(line, dataMap);
                map.put(String.valueOf(ROOT_COUNT), dataMap);
                mapList.add(map);
                logger.info(ROOT_COUNT + line);
            } else if (line.startsWith("  -")) {
                y++;
                z = 0;
                matcherContent(line, dataMap);
                map.put(ROOT_COUNT + "-" + y, dataMap);
                mapList.add(map);
                logger.info(ROOT_COUNT + "-" + y + line);
            } else if (line.startsWith("    -")) {
                z++;
                matcherContent(line, dataMap);
                map.put(ROOT_COUNT + "-" + y + "-" + z, dataMap);
                mapList.add(map);
                logger.info(ROOT_COUNT + "-" + y + "-" + z + line);
            }
        }
        return mapList;
    }

    /**
     * 获取跟目录数量
     * @return
     */
    public static Long getRootCount(){
        return Long.valueOf(ROOT_COUNT);
    }

    public static void rootCountInit(){ ROOT_COUNT = 0;};

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
     * @param filePath
     * @return
     */
    public static String markdown2Html(String filePath) throws IOException {
        File file = new File(filePath);
        if(!file.exists()){
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

}

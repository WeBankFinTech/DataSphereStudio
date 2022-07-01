package com.webank.wedatasphere.dss.guide.server.util;

import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    public static boolean upload(MultipartFile file, String path, String fileName) {
        String realPath = path + File.separator + fileName;
        logger.info("上传文件路径：" + realPath);

        File dest = new File(realPath);
        //判断文件父目录是否存在
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdir();
        }
        try {
            byte[] bytes = file.getBytes();
            Files.write(bytes, dest);
            return true;
        } catch (Exception ex) {
            logger.warn("文件上传出错：" + ex.getMessage());
            return false;
        }

    }

    /**
     * 判断文件是否存在，存在则删除
     *
     * @param filePath
     * @return
     */
    public static boolean fileExist(String filePath) {
        logger.info("删除文件路径：" + filePath);
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        }
        return false;
    }
}


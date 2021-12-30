package com.webank.wedatasphere.dss.guide.server.util;

import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author suyc
 * @Classname FileUtils
 * @Description TODO
 * @Date 2021/12/23 15:53
 * @Created by suyc
 */
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
            Files.write(bytes,dest);
            return true;
        } catch (Exception ex) {
            logger.warn("文件上传出错：" + ex.getMessage());
            return false;
        }

    }
}


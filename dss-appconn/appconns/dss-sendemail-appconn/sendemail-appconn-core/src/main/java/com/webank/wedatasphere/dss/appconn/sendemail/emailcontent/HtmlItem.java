package com.webank.wedatasphere.dss.appconn.sendemail.emailcontent;

public class HtmlItem {

    // 文件名
    private String fileName;

    // 文件类型
    private String fileType;

    // 内容id
    private String contentId;

    // 内容类型
    private String contentType;

    // 内容
    private String content;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

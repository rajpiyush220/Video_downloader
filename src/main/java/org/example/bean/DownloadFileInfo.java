package org.example.bean;


public class DownloadFileInfo {
    Integer downloadStart;
    Integer downloadEnd;
    String fileName;
    String url;

    public Integer getDownloadStart() {
        return downloadStart;
    }

    public void setDownloadStart(Integer downloadStart) {
        this.downloadStart = downloadStart;
    }

    public Integer getDownloadEnd() {
        return downloadEnd;
    }

    public void setDownloadEnd(Integer downloadEnd) {
        this.downloadEnd = downloadEnd;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

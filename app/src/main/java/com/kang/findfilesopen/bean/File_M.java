package com.kang.findfilesopen.bean;



public class File_M {
    private String fileId;//文件id
    private String filePath;//文件路径
    private String fileSize;//文件大小
    private String fileType;//文件类型
    private String fileTitle;//文件标题
    private boolean isChecked;

    public String getFileId() {
        return fileId==null?"":fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFilePath() {
        return filePath==null?"":filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileSize() {
        return fileSize==null?"":fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType==null?"":fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileTitle() {
        return fileTitle==null?"":fileTitle;
    }

    public void setFileTitle(String fileTitle) {
        this.fileTitle = fileTitle;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
    public void toggle(){
        this.isChecked = !isChecked;
    }
}

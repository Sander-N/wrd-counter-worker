package com.sander.wrdcounterworker.dto;

public class FileData {
    private String id;
    private String fileContent;

    public FileData(String id, String fileContent) {
        this.id = id;
        this.fileContent = fileContent;
    }

    public String getId() {
        return id;
    }

    public String getFileContent() {
        return fileContent;
    }
}

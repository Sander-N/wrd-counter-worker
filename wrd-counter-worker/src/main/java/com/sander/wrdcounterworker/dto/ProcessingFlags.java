package com.sander.wrdcounterworker.dto;

public class ProcessingFlags {
    public Boolean ignoreStopWords;
    public Boolean ignoreOutliers;

    public ProcessingFlags(Boolean ignoreStopWords, Boolean ignoreOutliers) {
        this.ignoreStopWords = ignoreStopWords;
        this.ignoreOutliers = ignoreOutliers;
    }
}

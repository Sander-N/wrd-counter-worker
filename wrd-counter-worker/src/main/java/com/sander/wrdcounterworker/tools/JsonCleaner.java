package com.sander.wrdcounterworker.tools;
import org.apache.commons.text.StringEscapeUtils;

public class JsonCleaner {
    public static String removeQuotesAndUnescape(String uncleanJson) {
        String noQuotes = uncleanJson.replaceAll("^\"|\"$", "");
        return StringEscapeUtils.unescapeJava(noQuotes);
    }
}

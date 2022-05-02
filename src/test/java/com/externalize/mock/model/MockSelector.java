package com.externalize.mock.model;

import java.util.List;
import java.util.regex.Pattern;

public class MockSelector {
    private String exactMatch;
    private String regEx;
    private Pattern pattern;
    private List<String> targetUriList;
    private List<String> targetHttpMethodValList;

    public List<String> getTargetUriList() {
        return targetUriList;
    }

    public void setTargetUriList(List<String> targetUriList) {
        this.targetUriList = targetUriList;
    }

    public List<String> getTargetHttpMethodValList() {
        return targetHttpMethodValList;
    }

    public void setTargetHttpMethodValList(List<String> targetHttpMethodValList) {
        this.targetHttpMethodValList = targetHttpMethodValList;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public String getExactMatch() {
        return exactMatch;
    }

    public void setExactMatch(String exactMatch) {
        this.exactMatch = exactMatch;
    }

    public String getRegEx() {
        return regEx;
    }

    public void setRegEx(String regEx) {
        this.regEx = regEx;
        if(regEx != null) {
            this.pattern = Pattern.compile(regEx);
        }
    }
}
package com.externalize.mock.model;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockValidation {
    private List<MockHeader> header;
    private Map<String, String> headerMap = new HashMap<>();
    private StringBuilder headerSb = new StringBuilder();
    public List<MockHeader> getHeader() {
        return header;
    }

    public void setHeader(List<MockHeader> header) {
        this.header = header;
        if(header!=null){
            header.stream().forEach(
                    x -> {
                        headerMap.put(x.getName(), x.getValue());
                        headerSb.append("[" + x.getName() + "=" + x.getValue() + "]");
                    }
            );
        }
    }

    public String getHeaderListString(){
        return headerSb.toString();
    }

    public boolean isHeaderMatched(String name, String value){
        boolean isMatched = true;
        if(headerMap!=null){
            isMatched = value.contains(headerMap.get(name));
        }
        return isMatched;
    }
}
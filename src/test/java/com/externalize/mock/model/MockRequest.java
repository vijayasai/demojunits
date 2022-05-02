package com.externalize.mock.model;

import java.util.Collections;
import java.util.List;

public class MockRequest {
    private String httpMethod;
    private MockSelector uri;
    private MockSelector body;
    private MockValidation validation;
    private List<MockQueryParam> queryParam;
    private List<MockHeader> header;
    private String queryParamStr = "";

    public List<MockQueryParam> getQueryParam() {
        return queryParam;
    }

    public void setQueryParam(List<MockQueryParam> queryParam) {
        this.queryParam = queryParam;
        if(queryParam!=null && !queryParam.isEmpty()) {
            Collections.sort(queryParam);
            queryParam.forEach(
                    x-> queryParamStr=queryParamStr+ "[" + x.getName() + "=" + x.getValue() + "]"
            );
        }
    }

    public String getQueryParamAsString(){
        return queryParamStr;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public MockSelector getUri() {
        return uri;
    }

    public void setUri(MockSelector uri) {
        this.uri = uri;
    }

    public MockSelector getBody() {
        return body;
    }

    public void setBody(MockSelector body) {
        this.body = body;
    }

    public MockValidation getValidation() {
        return validation;
    }

    public void setValidation(MockValidation validation) {
        this.validation = validation;
    }

    public List<MockHeader> getHeader() {
        return header;
    }

    public void setHeader(List<MockHeader> header) {
        this.header = header;
    }
}

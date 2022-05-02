package com.externalize.mock.model;

import java.util.List;

public class MockResponse {
    private MockResponseBody body;
    private MockResponseError error;
    private List<MockHeader> header;
    private int httpStatus;

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public List<MockHeader> getHeader() {
        return header;
    }

    public void setHeader(List<MockHeader> header) {
        this.header = header;
    }

    public MockResponseBody getBody() {
        return body;
    }

    public void setBody(MockResponseBody body) {
        this.body = body;
    }

    public MockResponseError getError() {
        return error;
    }

    public void setError(MockResponseError error) {
        this.error = error;
    }

}

package com.externalize.mock.model;


public class MockCase {
    private MockRequest request;
    private MockResponse response;

    public MockRequest getRequest() {
        return request;
    }

    public void setRequest(MockRequest request) {
        this.request = request;
    }

    public MockResponse getResponse() {
        return response;
    }

    public void setResponse(MockResponse response) {
        this.response = response;
    }
}

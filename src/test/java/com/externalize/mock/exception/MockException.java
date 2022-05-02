package com.externalize.mock.exception;


import java.io.Serializable;


public class MockException extends Exception implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int httpStatus;
    private String errorMessge;

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getErrorMessge() {
        return errorMessge;
    }

    public void setErrorMessge(String errorMessge) {
        this.errorMessge = errorMessge;
    }

    public MockException(String errorMessge, int httpStatus){
        super(errorMessge);
        this.errorMessge = errorMessge;
        this.httpStatus = httpStatus;
    }
}

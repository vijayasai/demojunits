package com.externalize.mock.model;

import java.util.Objects;

public class MockHeader {
    private String name;
    private String value;
    public MockHeader(){
        super();
    }
    public MockHeader(String name, String value){
        super();
        this.name=name.toLowerCase();
        this.value=value;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.toLowerCase();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MockHeader)) return false;
        MockHeader that = (MockHeader) o;
        return Objects.equals(getName(), that.getName()) &&
                Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getValue());
    }

    @Override
    public String toString() {
        return "[" + name + "=" + value + "]";
    }
}
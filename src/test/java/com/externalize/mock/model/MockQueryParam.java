package com.externalize.mock.model;


import java.util.Objects;

public class MockQueryParam implements Comparable<MockQueryParam> {
    private String name;
    private String value;

    public MockQueryParam(){
        super();
    }

    public MockQueryParam(String name, String value){
        super();
        setName(name);
        setValue(value);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        if (!(o instanceof MockQueryParam)) return false;
        MockQueryParam that = (MockQueryParam) o;
        return Objects.equals(getName(), that.getName()) &&
                Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getValue());
    }

    @Override
    public int compareTo(MockQueryParam o) {
        if(o==null && this.name==null) {
            return 0;
        }else if(o==null){
            return 1;
        }else if(this.name==null){
            return -1;
        }
        return this.name.compareTo(o.getName());
    }
}

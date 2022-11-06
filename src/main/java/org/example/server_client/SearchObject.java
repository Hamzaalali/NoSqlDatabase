package org.example.server_client;

import java.io.Serializable;

public class SearchObject implements Serializable {
    private String property;
    private Object value;
    public String getProperty() {
        return property;
    }
    public void setProperty(String property) {
        this.property = property;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SearchObject{" +
                "property='" + property + '\'' +
                ", value=" + value +
                '}';
    }
}

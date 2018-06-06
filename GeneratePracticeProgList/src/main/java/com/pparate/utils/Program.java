package com.pparate.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Program {
    private String name;
    private String path;
    private HashMap<String, String> attribs;

    Program(String name, String path) {
        this.setName(name);
        this.setPath(path);
        this.attribs = new HashMap<String, String>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public HashMap<String, String> getAttribs() {
        return attribs;
    }

    public void setAttribs(HashMap<String, String> attribs) {
        this.attribs = attribs;
    }

    public void addAttrib(String key, String value) {
        HashMap<String, String> map = new HashMap<>();
        this.getAttribs().put(key, value);
    }

}

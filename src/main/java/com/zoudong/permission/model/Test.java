package com.zoudong.permission.model;

import javax.persistence.*;
import java.io.Serializable;

public class Test implements Serializable{
    @Id
    private Long id;

    private String test;

    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return test
     */
    public String getTest() {
        return test;
    }

    /**
     * @param test
     */
    public void setTest(String test) {
        this.test = test;
    }
}
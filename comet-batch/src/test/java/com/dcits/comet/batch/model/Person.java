package com.dcits.comet.batch.model;

import javax.validation.constraints.Size;

public class Person {

    @Size(max = 4, min = 2) //1此处使用JSR-303注解来校验数据。
    private String name;
    private String age;
    private String nation;
    private String address;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
package com.collectivity.dto;


public class UpdateCollectivityDto {

    private Integer number;
    private String name;

    public UpdateCollectivityDto() {}

    public String getNumber() { return String.valueOf(number); }
    public void setNumber(Integer number) { this.number = number; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
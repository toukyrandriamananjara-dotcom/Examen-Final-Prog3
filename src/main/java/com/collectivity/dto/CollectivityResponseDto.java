package com.collectivity.dto;


public class CollectivityResponseDto extends CollectivityDto {
    private Integer number;
    private String name;
    private String agriculturalSpecialty;

    public CollectivityResponseDto() {}

    public Integer getNumber() { return number; }
    public void setNumber(Integer number) { this.number = number; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAgriculturalSpecialty() { return agriculturalSpecialty; }
    public void setAgriculturalSpecialty(String agriculturalSpecialty) { this.agriculturalSpecialty = agriculturalSpecialty; }
}
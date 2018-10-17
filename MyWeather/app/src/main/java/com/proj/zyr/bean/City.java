package com.proj.zyr.bean;

/**
 * Created by IrisZYR on 2018/10/17
 */
public class City {

    private String province;
    private String city;
    private String number;
    private String firstPY;
    private String allPY;
    private String allFristPY;

    public City(String province, String city, String number, String firstPY,
                String allPY, String allFristPY){

        this.province=province;
        this.city=city;
        this.number=number;
        this.firstPY=firstPY;
        this.allFristPY=allFristPY;
        this.allPY=allPY;

    }

    public String getCity() {
        return city;
    }

    public String getNumber() {
        return number;
    }

    public String getProvince() {
        return province;
    }

    public String getFirstPY() {
        return firstPY;
    }

    public String getAllFristPY() {
        return allFristPY;
    }

    public String getAllPY() {
        return allPY;
    }




}

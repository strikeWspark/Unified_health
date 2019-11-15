package com.dryfire.unifiedhealth;

import java.io.Serializable;

public class Uified implements Serializable {

    private String aadhar_no;
    private String name;
    private String age;
    private String haemoglobin;
    private String heartrate;
    private String town;
    private String photourl;
    private String BMI;
    private String mobile;

    public Uified(){

    }


    public Uified(String aadhar_no, String name, String age, String haemoglobin, String heartrate, String town, String photourl,String BMI,String mobile) {
        this.aadhar_no = aadhar_no;
        this.name = name;
        this.age = age;
        this.haemoglobin = haemoglobin;
        this.heartrate = heartrate;
        this.town = town;
        this.photourl = photourl;
        this.BMI = BMI;
        this.mobile = mobile;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAadhar_no() {
        return aadhar_no;
    }

    public void setAadhar_no(String aadhar_no) {
        this.aadhar_no = aadhar_no;
    }

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

    public String getHaemoglobin() {
        return haemoglobin;
    }

    public void setHaemoglobin(String haemoglobin) {
        this.haemoglobin = haemoglobin;
    }

    public String getHeartrate() {
        return heartrate;
    }

    public String getBMI() {
        return BMI;
    }

    public void setBMI(String BMI) {
        this.BMI = BMI;
    }

    public void setHeartrate(String heartrate) {
        this.heartrate = heartrate;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }
}

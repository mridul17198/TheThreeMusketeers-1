package com.example.thethreemusketeers;

public class database_user_registeration_information {

    String email;
    String mobile;
    String address;

    public database_user_registeration_information() {
    }

    public database_user_registeration_information(String email, String mobile, String address) {
        this.email = email;
        this.mobile = mobile;
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

package com.example.myapplication;

public class Message {

    private String text;

    private String phoneNumber;

    private String type;

    public String getText() {
        return text;
    }

    public void setText(String text) {

        this.text = text;
    }

    public String getPhoneNumber() {

        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {

        this.phoneNumber = phoneNumber;
    }

    public String getType() {

        return type;
    }

    public void setType(String type) {

        this.type = type;
    }
}

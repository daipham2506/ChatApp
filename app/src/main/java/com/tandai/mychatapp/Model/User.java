package com.tandai.mychatapp.Model;

public class User {
    private String name;
    private String email;
    private String pass;
    private String phone;
    private String image;
    private String userID;
    private String status;


    public User() {
    }

    public User(String name, String email, String pass, String phone, String image,String userID,String status) {
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.phone = phone;
        this.image = image;
        this.userID = userID;
        this.status = status;
    }

    public String getUserID() {
        return userID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

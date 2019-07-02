package com.ayush.anonytweet.Classes;


public class User {
    private String data_id, text, imagePath, email;
    private int no_of_likes;

    public User() {
    }


    public User(String data_id, String text, String imagePath, String email, int no_of_likes) {
        this.data_id = data_id;
        this.text = text;
        this.imagePath = imagePath;
        this.email = email;
        this.no_of_likes = no_of_likes;
    }

    public int getNo_of_likes() {
        return no_of_likes;
    }

    public void setNo_of_likes(int no_of_likes) {
        this.no_of_likes = no_of_likes;
    }

    public String getData_id() {
        return data_id;
    }

    public void setData_id(String data_id) {
        this.data_id = data_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}


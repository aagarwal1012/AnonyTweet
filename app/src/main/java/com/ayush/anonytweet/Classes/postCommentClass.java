package com.ayush.anonytweet.Classes;

import java.util.Date;

public class postCommentClass {

    private String commentText, userId;
    private long commentTime;

    public postCommentClass() {
    }

    public postCommentClass(String commentText, String userId) {
        this.commentText = commentText;
        this.userId = userId;
        this.commentTime = new Date().getTime();
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(long commentTime) {
        this.commentTime = commentTime;
    }
}

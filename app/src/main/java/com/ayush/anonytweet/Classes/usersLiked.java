package com.ayush.anonytweet.Classes;

import java.util.List;

public class usersLiked {
    private List<String> liked_users;
    private String tweetId;
    String owner_tweet_email;

    public usersLiked() {
    }

    public usersLiked(List<String> liked_users, String tweetId) {
        this.liked_users = liked_users;
        this.tweetId = tweetId;
    }

    public String getTweetId() {
        return tweetId;
    }

    public void setTweetId(String tweetId) {
        this.tweetId = tweetId;
    }

    public List<String> getLiked_users() {
        return liked_users;
    }

    public void setLiked_users(List<String> liked_users) {
        this.liked_users = liked_users;
    }
}

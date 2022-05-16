package com.dp.account.entity;
import java.util.Collections;

public class User{
    private Long userId;
    private String userName;
    private String userPwdEncoded;
    private String userAvatar;
    private String salt;

    public User(String userName, String userPwdEncoded, String salt) {
        this.userName = userName;
        this.userPwdEncoded = userPwdEncoded;
        this.salt = salt;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPwdEncoded() {
        return userPwdEncoded;
    }

    public void setUserPwdEncoded(String userPwdEncoded) {
        this.userPwdEncoded = userPwdEncoded;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }
}

package com.dp.file.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyFile {
    private Long id;
    private Long fromUserId;
    private String realName;
    private String savedPath;
    private LocalDateTime createTime;
    private Long size;
    private String type;

    public MyFile(Long id, Long fromUserId, String realName, String savedPath, LocalDateTime createTime, Long size, String type) {
        this.id = id;
        this.fromUserId = fromUserId;
        this.realName = realName;
        this.savedPath = savedPath;
        this.createTime = createTime;
        this.size = size;
        this.type = type;
    }

    public MyFile(Long fromUserId, String realName, String savedPath, LocalDateTime createTime, Long size, String type) {
        this.fromUserId = fromUserId;
        this.realName = realName;
        this.savedPath = savedPath;
        this.createTime = createTime;
        this.size = size;
        this.type = type;
    }

    @Override
    public String toString() {
        return "MyFile{" +
                "id=" + id +
                ", fromUserId=" + fromUserId +
                ", realName='" + realName + '\'' +
                ", savedPath='" + savedPath + '\'' +
                ", createTime=" + createTime +
                ", size=" + size +
                ", type='" + type + '\'' +
                '}';
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setSavedPath(String savedPath) {
        this.savedPath = savedPath;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public String getRealName() {
        return realName;
    }

    public String getSavedPath() {
        return savedPath;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public Long getSize() {
        return size;
    }

    public String getType() {
        return type;
    }
}

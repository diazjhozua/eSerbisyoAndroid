package com.example.eserbisyo.Models;

import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;

public class Announcement {

    private int id;
    private Type type;
    private String customType;
    private boolean selfLike;
    private String title;
    private String description;
    private ArrayList<SlideModel> pictureArray;
    private String createdAt;
    private int likesCount;
    private ArrayList<Like> likeArray;
    private int commentsCount;
    private ArrayList<Comment> commentArray;

    public Announcement(int id, Type type, String customType, boolean selfLike, String title, String description, ArrayList<SlideModel> pictureArray, String createdAt, int likesCount, int commentsCount) {
        this.id = id;
        this.type = type;
        this.customType = customType;
        this.selfLike = selfLike;
        this.title = title;
        this.description = description;
        this.pictureArray = pictureArray;
        this.createdAt = createdAt;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
    }
    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getCustomType() {
        return customType;
    }

    public void setCustomType(String customType) {
        this.customType = customType;
    }

    public boolean isSelfLike() {
        return selfLike;
    }

    public void setSelfLike(boolean selfLike) {
        this.selfLike = selfLike;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<SlideModel>  getPictureArray() {
        return pictureArray;
    }

    public void setPictureArray(ArrayList<SlideModel> pictureArray) {
        this.pictureArray = pictureArray;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public ArrayList<Like> getLikeArray() {
        return likeArray;
    }

    public void setLikeArray(ArrayList<Like> likeArray) {
        this.likeArray = likeArray;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public ArrayList<Comment> getCommentArray() {
        return commentArray;
    }

    public void setCommentArray(ArrayList<Comment> commentArray) {
        this.commentArray = commentArray;
    }
}

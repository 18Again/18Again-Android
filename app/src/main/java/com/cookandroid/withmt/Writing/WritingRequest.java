package com.cookandroid.withmt.Writing;

import com.google.gson.annotations.SerializedName;

public class WritingRequest {
    @SerializedName("title")
    private String title;
    @SerializedName("member")
    private int member;
    @SerializedName("link")
    private String link;
    @SerializedName("content")
    private String content;
    @SerializedName("boardGender")
    private int boardGender;
    @SerializedName("date")
    private String date;

    public WritingRequest(String title, int member, String link, String content, int boardGender, String date){
        this.title = title;
        this.member = member;
        this.link = link;
        this.content = content;
        this.boardGender = boardGender;
        this.date = date;
    }
}

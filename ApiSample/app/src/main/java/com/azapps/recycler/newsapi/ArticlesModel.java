package com.azapps.recycler.newsapi;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by prashant.chovatiya on 1/12/2018.
 */

public class ArticlesModel implements Serializable {

    @SerializedName("author")
    public String author;

    @SerializedName("title")
    public String title;

    @SerializedName("description")
    public String description;

    @SerializedName("url")
    public String url;

    @SerializedName("urlToImage")
    public String urlToImage;

    @SerializedName("publishedAt")
    public String publishedAt;

}

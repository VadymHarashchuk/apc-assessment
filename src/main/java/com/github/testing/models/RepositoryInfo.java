package com.github.testing.models;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RepositoryInfo {

    private int id;
    @SerializedName("node_id")
    private String nodeId;
    private String name;
    @SerializedName("full_name")
    private String fullName;
    @SerializedName("private")
    private boolean privateRepo;
    private Owner owner;
    @SerializedName("html_url")
    private boolean htmlUrl;
    private String description;

    @Data
    @Builder
    public static class Owner {
        private String login;
        private int id;
        @SerializedName("node_id")
        private String nodeId;
        @SerializedName("avatar_url")
        private String avatarUrl;
        @SerializedName("gravatar_id")
        private String gravatarId;
        private String url;
        @SerializedName("html_url")
        private String htmlUrl;
        @SerializedName("followers_url")
        private String followersUrl;
        @SerializedName("following_url")
        private String followingUrl;
        @SerializedName("gists_url")
        private String gistsUrl;
        @SerializedName("starred_url")
        private String starredUrl;
        private String type;
    }
}
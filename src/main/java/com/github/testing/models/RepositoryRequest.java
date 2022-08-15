package com.github.testing.models;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RepositoryRequest {
    private String name;
    private String description;
    private String homepage;
    @SerializedName("private")
    private boolean privateRepo;
    @SerializedName("has_issues")
    private boolean hasIssues;
    @SerializedName("has_wiki")
    private boolean hasWiki;
    @SerializedName("has_projects")
    private boolean hasProjects;
}
package com.github.testing.service;

import com.github.javafaker.Faker;
import com.github.testing.models.RepositoryRequest;
import feign.Response;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class GithubClientService {

    private final String token;
    private final GithubClient githubClient;

    public GithubClientService(GithubClient githubClient, String token){
        this.githubClient = githubClient;
        this.token = new String(Base64.getUrlDecoder().decode(token));
    }

    public Response createRepository(RepositoryRequest body, boolean withAuthorization) {
        Map<String, String> authHeader = new HashMap<>();
        if (withAuthorization) {
            authHeader.put("Authorization", token);
        }
        return githubClient.createRepository(authHeader, body);
    }

    public Response deleteRepository(String repoName, String owner, boolean withAuthorization) {
        Map<String, String> authHeader = new HashMap<>();
        if (withAuthorization) {
            authHeader.put("Authorization", token);
        }
        return githubClient.deleteRepositoryByUserAndName(authHeader, owner, repoName);
    }

    public Response getRepositories(String owner, boolean withAuthorization) {
        Map<String, String> authHeader = new HashMap<>();
        if (withAuthorization) {
            authHeader.put("Authorization", token);
        }
        return githubClient.getListOfRepositories(authHeader, owner);
    }

    public Response getRepositoryByName(String owner, String repoName, boolean withAuthorization) {
        Map<String, String> authHeader = new HashMap<>();
        if (withAuthorization) {
            authHeader.put("Authorization", token);
        }
        return githubClient.getRepositoryByUserAndName(authHeader, owner, repoName);
    }

    public RepositoryRequest buildRequest() {
        Faker faker = new Faker();
        return RepositoryRequest.builder()
                .name(faker.backToTheFuture().character().replace(" ", "-"))
                .description(faker.chuckNorris().fact())
                .privateRepo(false)
                .build();
    }
}
package com.github.testing.service;

import com.github.testing.config.FeignConfig;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "gitHubClient", url = "${feign.client.url}", configuration = FeignConfig.class)
public interface GithubClient {

        @GetMapping("/users/{username}/repos")
        Response getListOfRepositories(@RequestHeader Map<String, String> headers, @PathVariable("username") String username);

        @GetMapping("/repos/{owner}/{repo}")
        Response getRepositoryByUserAndName(@RequestHeader Map<String, String> headers,
                                          @PathVariable("owner") String owner,
                                          @PathVariable("repo") String repo);

        @DeleteMapping("/repos/{owner}/{repo}")
        Response deleteRepositoryByUserAndName(@RequestHeader Map<String, String> headers,
                                             @PathVariable("owner") String owner,
                                             @PathVariable("repo") String repo);

        @PostMapping(value = "/user/repos", consumes = MediaType.APPLICATION_JSON_VALUE)
        Response createRepository(@RequestHeader Map<String, String> headers, @RequestBody Object body);
}
package com.github.testing.config;

import com.github.testing.service.GithubClient;
import com.github.testing.service.GithubClientService;
import com.google.gson.Gson;
import org.assertj.core.api.SoftAssertions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = GithubClient.class)
@ImportAutoConfiguration({FeignAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class})
public class GithubClientConfiguration {

    @Bean
    public GithubClientService githubClientService(GithubClient githubClient, @Value("${github.token}") String token){
        return new GithubClientService(githubClient, token);
    }

    @Bean
    public Gson gson() {
        return new Gson();
    }
}

package com.github.testing.tests;

import com.github.testing.config.GithubClientConfiguration;
import com.github.testing.models.RepositoryRequest;
import com.github.testing.service.GithubClientService;
import com.google.gson.Gson;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = GithubClientConfiguration.class)
public class DeleteRepositoryTests {

    @Value("${github.owner}")
    private String owner;

    @Autowired
    private GithubClientService githubClientService;
    @Autowired
    private Gson gson;

    private RepositoryRequest request;

    @BeforeEach
    public void setUp() {
        request = githubClientService.buildRequest();
        githubClientService.createRepository(request, true);
    }

    @Test
    @DisplayName("Delete repository (POSITIVE SCENARIO)")
    public void deleteRepoTest() {
        Response response = githubClientService.deleteRepository(request.getName(), owner, true);
        assertThat(response.status()).as("Status code").isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("Trying to delete the same repository twice (NEGATIVE SCENARIO)")
    public void deleteSameRepoTwiceTest() {

        Response response =  githubClientService.deleteRepository(request.getName(), owner, true);
        assertThat(response.status()).as("Status code").isEqualTo(HttpStatus.NO_CONTENT.value());

        response = githubClientService.deleteRepository(request.getName(), owner, true);
        String errorMessage = "";
        try {
            errorMessage = IOUtils.toString(response.body().asInputStream());
        } catch (IOException e) {
            log.debug("Parsing exception occurs while creating repository: {}", e.getMessage());
        }

        assertThat(response.status()).as("Status code").isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(errorMessage).as("Error message").contains("Not Found");
    }

    @Test
    @DisplayName("Delete repository without authorization header (NEGATIVE SCENARIO)")
    public void deleteRepoWithoutAuthTest() {
        Response response = githubClientService.deleteRepository(request.getName(), owner, false);
        String errorMessage = "";
        try {
            errorMessage = IOUtils.toString(response.body().asInputStream());
        } catch (IOException e) {
            log.debug("Parsing exception occurs while creating repository: {}", e.getMessage());
        }

        assertThat(response.status()).as("Status code").isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(errorMessage).as("Error message").contains("Must have admin rights to Repository.");

        //Is used only for clean-up purposes to avoid obsolete tests repositories
        response = githubClientService.deleteRepository(request.getName(), owner, true);
        assertThat(response.status()).as("Status code").isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
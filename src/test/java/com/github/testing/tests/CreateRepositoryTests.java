package com.github.testing.tests;

import com.github.testing.config.GithubClientConfiguration;
import com.github.testing.models.RepositoryInfo;
import com.github.testing.models.RepositoryRequest;
import com.github.testing.service.GithubClientService;
import com.google.gson.Gson;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.assertj.core.api.SoftAssertions;
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
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = GithubClientConfiguration.class)
public class CreateRepositoryTests {

    @Value("${github.owner}")
    private String owner;

    @Autowired
    private GithubClientService githubClientService;
    @Autowired
    private Gson gson;

    @Test
    @DisplayName("Create repository (POSITIVE SCENARIO)")
    public void createRepoTest() {
        RepositoryRequest request = githubClientService.buildRequest();
        Response response = githubClientService.createRepository(request, true);

        RepositoryInfo repoInfo = null;
        try {
            //Parse response to the RepositoryInfo object
            repoInfo = gson.fromJson(IOUtils.toString(response.body().asInputStream()), RepositoryInfo.class);
        } catch (IOException e) {
            log.debug("Parsing exception occurs while creating repository: {}", e.getMessage());
        }
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(response.status()).as("Status code").isEqualTo(HttpStatus.CREATED.value());
        softAssertions.assertThat(Objects.requireNonNull(repoInfo).getOwner().getLogin()).as("Owner").isEqualTo(owner);
        softAssertions.assertThat(repoInfo.getName()).as("Name").isEqualTo(request.getName());
        softAssertions.assertThat(repoInfo.getDescription()).as("Description").isEqualTo(request.getDescription());
        softAssertions.assertThat(repoInfo.getFullName()).as("Description").isEqualTo(owner + "/" + request.getName());
        softAssertions.assertAll();

        //Is used only for clean-up purposes to avoid obsolete tests repositories
        response = githubClientService.deleteRepository(request.getName(), owner, true);
        assertThat(response.status()).as("Status code").isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("Create repository without required field (NEGATIVE SCENARIO)")
    public void createRepoWithoutRequiredFieldTest() {
        RepositoryRequest request = githubClientService.buildRequest();
        request.setName("");
        Response response = githubClientService.createRepository(request, true);
        String errorMessage = "";
        try {
            errorMessage = IOUtils.toString(response.body().asInputStream());
        } catch (IOException e) {
            log.debug("Parsing exception occurs while creating repository: {}", e.getMessage());
        }

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(response.status()).as("Status code").isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        softAssertions.assertThat(errorMessage).as("Error message").contains("Repository creation failed.");
        softAssertions.assertThat(errorMessage).as("Error message").contains("\"code\":\"missing_field\",\"field\":\"name\"");
        softAssertions.assertAll();
    }

    @Test
    @DisplayName("Create repository without authorization header (NEGATIVE SCENARIO)")
    public void createRepoWithoutAuthTest() {
        RepositoryRequest request = githubClientService.buildRequest();
        Response response = githubClientService.createRepository(request, false);

        assertThat(response.status()).as("Status code").isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}
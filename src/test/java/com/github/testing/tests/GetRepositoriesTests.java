package com.github.testing.tests;

import com.github.testing.config.GithubClientConfiguration;
import com.github.testing.models.RepositoryInfo;
import com.github.testing.service.GithubClientService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.assertj.core.api.SoftAssertions;
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
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = GithubClientConfiguration.class)
public class GetRepositoriesTests {

    @Value("${github.owner}")
    private String owner;

    @Autowired
    private GithubClientService githubClientService;
    @Autowired
    private Gson gson;

    private RepositoryInfo createdRepoInfo;

    @BeforeEach
    public void setUp() throws IOException {
        Response response = githubClientService.createRepository(githubClientService.buildRequest(), true);

        //Parse response to the RepositoryInfo object
        createdRepoInfo = gson.fromJson(IOUtils.toString(response.body().asInputStream()), RepositoryInfo.class);
    }

    @Test
    @DisplayName("Check that created repository is in list (POSITIVE SCENARIO)")
    //There is a defect in API-specification - according to documentation this API-call couldn't be executed w/o any authorization
    public void getRepoListTest() {
        Response response = githubClientService.getRepositories(owner, false);

        List<RepositoryInfo> repositoriesList = null;
        try {
            //Parse response to the list of RepositoryInfo objects
            repositoriesList = gson.fromJson(IOUtils.toString(response.body().asInputStream()), new TypeToken<List<RepositoryInfo>>() {
            }.getType());
        } catch (IOException e) {
            log.debug("Parsing exception occurs while creating repository: {}", e.getMessage());
        }

        RepositoryInfo repository = Objects.requireNonNull(repositoriesList).stream().filter(repoInfo -> repoInfo.getId() == createdRepoInfo.getId())
                .findFirst().orElse(null);

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(response.status()).as("Status code").isEqualTo(HttpStatus.OK.value());
        softAssertions.assertThat(repository.getDescription()).as("Description").isEqualTo(createdRepoInfo.getDescription());
        softAssertions.assertThat(repository.getFullName()).as("Full name").isEqualTo(createdRepoInfo.getFullName());

//        Is used only for clean-up purposes to avoid obsolete tests repositories
        response = githubClientService.deleteRepository(createdRepoInfo.getName(), owner, true);
        softAssertions.assertThat(response.status()).as("Status code").isEqualTo(HttpStatus.NO_CONTENT.value());
        softAssertions.assertAll();
    }

    @Test
    @DisplayName("Get repository by name (POSITIVE SCENARIO)")
    //There is a defect in API-specification - according to documentation this API-call couldn't be executed w/o any authorization
    public void getRepositoryByNameTest(){
        Response response = githubClientService.getRepositoryByName(owner, createdRepoInfo.getName(),  true);
        RepositoryInfo repoInfo = null;
        try {
            //Parse response to the RepositoryInfo object
            repoInfo = gson.fromJson(IOUtils.toString(response.body().asInputStream()), RepositoryInfo.class);
        } catch (IOException e) {
            log.debug("Parsing exception occurs while creating repository: {}", e.getMessage());
        }
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(response.status()).as("Status code").isEqualTo(HttpStatus.OK.value());
        softAssertions.assertThat(repoInfo.getFullName()).as("Full name").isEqualTo(createdRepoInfo.getFullName());
        softAssertions.assertThat(repoInfo.getId()).as("Full name").isEqualTo(createdRepoInfo.getId());

        //Is used only for clean-up purposes to avoid obsolete tests repositories
        response = githubClientService.deleteRepository(createdRepoInfo.getName(), owner, true);
        softAssertions.assertThat(response.status()).as("Status code").isEqualTo(HttpStatus.NO_CONTENT.value());
        softAssertions.assertAll();
    }

    @Test
    @DisplayName("Get repository by invalid name (NEGATIVE SCENARIO)")
    //There is a defect in API-specification - according to documentation this API-call couldn't be executed w/o any authorization
    public void  getRepositoryByInvalidNameTest(){
        Response response = githubClientService.getRepositoryByName(owner, "invalid-name",  true);
        String errorMessage = "";
        try {
            errorMessage = IOUtils.toString(response.body().asInputStream());
        } catch (IOException e) {
            log.debug("Parsing exception occurs while creating repository: {}", e.getMessage());
        }
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(response.status()).as("Status code").isEqualTo(HttpStatus.NOT_FOUND.value());
        softAssertions.assertThat(errorMessage).as("Error message").contains("Not Found");

        //Is used only for clean-up purposes to avoid obsolete tests repositories
        response = githubClientService.deleteRepository(createdRepoInfo.getName(), owner, true);
        softAssertions.assertThat(response.status()).as("Status code").isEqualTo(HttpStatus.NO_CONTENT.value());
        softAssertions.assertAll();
    }
}
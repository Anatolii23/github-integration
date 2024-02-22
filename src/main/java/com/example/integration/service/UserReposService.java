package com.example.integration.service;

import com.example.integration.config.ConfigurationManager;
import com.example.integration.exception.ReposNotFoundException;
import com.example.integration.exception.RestClientErrorException;
import com.example.integration.exception.UserNotFoundException;
import com.example.integration.response.BranchResponse;
import com.example.integration.response.RepositoryResponse;
import com.example.integration.response.UserRepoResponse;
import com.example.integration.response.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * User Repos Controller.
 *
 * @author Anatolii Hamza
 */
@Service
public class UserReposService {

    private static final Logger LOG = LoggerFactory.getLogger(UserReposService.class);
    private static final String USERS = "users";
    private static final String REPOS = "repos";
    private static final String BRANCHES = "branches";
    private static final String SLASH = "/";
    private ConfigurationManager configurationManager;
    private RestClient restClient;


    @Autowired
    public UserReposService(ConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
        this.restClient = RestClient.builder()
                .baseUrl(configurationManager.githubConfig().url())
                .defaultHeader("Accept", "application/vnd.github+json")
                .defaultHeader("Authorization", "Bearer " + configurationManager.githubConfig().token())
                .defaultHeader("X-GitHub-Api-Version", configurationManager.githubConfig().version())
                .build();
    }

    /**
     * Gets user repos with branches.
     *
     * @param login {@link String} user login
     * @return {@link List} of {@link UserRepoResponse}
     * @throws UserNotFoundException    when user was not found
     * @throws RestClientErrorException when request to GitHub client failed
     * @throws ReposNotFoundException   when repos was not found
     */
    public List<UserRepoResponse> getUserRepos(String login)
            throws UserNotFoundException, RestClientErrorException, ReposNotFoundException {

        login = login.trim();
        var responses = new ArrayList<UserRepoResponse>();
        var userResponse = getUserLogin(login);
        var repoResponse = getReposResponses(userResponse.login());
        var filtered = Arrays.stream(repoResponse).filter(repo -> !repo.fork()).toList();
        if (!filtered.isEmpty()) {
            for (var repo : filtered) {
                var branchResponses = getBranchResponses(login, repo);
                if (Objects.nonNull(branchResponses)) {
                    responses.add(new UserRepoResponse(
                            repo.name(), userResponse.login(), branchResponses));
                }
            }
        }
        return responses;
    }

    /**
     * Gets users branches.
     *
     * @param login {@link String} user login
     * @param repo  {@link RepositoryResponse}
     * @return array of {@link BranchResponse}
     * @throws RestClientErrorException when request to GitHub client failed
     */
    private BranchResponse[] getBranchResponses(String login, RepositoryResponse repo)
            throws RestClientErrorException {

        try {
            return restClient.get()
                    .uri(SLASH + REPOS + SLASH + login + SLASH + repo.name() + SLASH + BRANCHES)
                    .retrieve()
                    .toEntity(BranchResponse[].class)
                    .getBody();
        } catch (HttpClientErrorException e) {
            LOG.error("Error during process: {}", e.getMessage());
            throw new RestClientErrorException(e.getMessage());
        }
    }

    /**
     * Gets users repos.
     *
     * @param login {@link String} user login
     * @return {@link List} of {@link UserRepoResponse}
     * @throws RestClientErrorException when request to GitHub client failed
     * @throws ReposNotFoundException   when repos was not found
     */
    private RepositoryResponse[] getReposResponses(String login)
            throws ReposNotFoundException, RestClientErrorException {

        try {
            var responses = restClient.get()
                    .uri(SLASH + USERS + SLASH + login + SLASH + REPOS)
                    .retrieve()
                    .toEntity(RepositoryResponse[].class)
                    .getBody();
            if (Objects.nonNull(responses) && !Arrays.asList(responses).isEmpty()) {
                return responses;
            } else {
                throw new ReposNotFoundException();
            }
        } catch (HttpClientErrorException e) {
            LOG.error("Error during process: {}", e.getMessage());
            throw new RestClientErrorException(e.getMessage());
        }
    }

    /**
     * Gets users info.
     *
     * @param login {@link String} user login
     * @return {@link UserResponse}
     * @throws RestClientErrorException when request to GitHub client failed
     * @throws UserNotFoundException    when user was not found
     */
    private UserResponse getUserLogin(String login) throws UserNotFoundException, RestClientErrorException {
        try {
            return restClient.get()
                    .uri(SLASH + USERS + SLASH + login)
                    .retrieve()
                    .toEntity(UserResponse.class)
                    .getBody();
        } catch (HttpClientErrorException e) {
            LOG.error("Error during process: {}", e.getMessage());
            if (Objects.equals(e.getStatusCode(), HttpStatus.NOT_FOUND)) {
                throw new UserNotFoundException();
            } else {
                throw new RestClientErrorException(e.getMessage());
            }
        }
    }
}

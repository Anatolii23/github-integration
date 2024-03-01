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
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

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
        var userResponse = getUserByLogin(login);
        addUserResponses(responses, userResponse);
        return responses;
    }

    /**
     * Gets users repos with branches.
     *
     * @return {@link List} of {@link UserRepoResponse}
     * @throws RestClientErrorException when request to GitHub client failed
     */
    public List<UserRepoResponse> getUsersRepos() throws RestClientErrorException {
        var responses = new ArrayList<UserRepoResponse>();
        var users = getUsers();
        for (var user : users) {
            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                executor.submit(() -> {
                    try {
                        addUserResponses(responses, user);
                    } catch (ReposNotFoundException | RestClientErrorException e) {
                        LOG.error("Error during process: {}", e.getMessage());
                    }
                });
            }
        }
        return responses;
    }

    /* Private methods */

    /**
     * Add users repos with branches to response.
     *
     * @param responses    {@link List} of {@link UserRepoResponse}
     * @param userResponse {@link UserResponse}
     * @throws RestClientErrorException when request to GitHub client failed
     * @throws ReposNotFoundException   when repos was not found
     */
    private void addUserResponses(List<UserRepoResponse> responses, UserResponse userResponse)
            throws ReposNotFoundException, RestClientErrorException {
        var repoResponse = getReposResponses(userResponse.login());
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = Arrays.stream(repoResponse)
                    .filter(repo -> !repo.fork())
                    .map(repo -> executor.submit(() -> processRepository(userResponse.login(), repo)))
                    .toList();
            for (var future : futures) {
                try {
                    future.get().ifPresent(responses::add);
                } catch (InterruptedException | ExecutionException e) {
                    LOG.error("Error during process: {}", e.getMessage());
                    throw new RestClientErrorException(e.getMessage());
                }
            }
        }
    }

    /**
     * Process repository.
     *
     * @param login {@link String} user login
     * @param repo  {@link RepositoryResponse}
     * @return {@link Optional} of {@link UserRepoResponse}
     */
    private Optional<UserRepoResponse> processRepository(String login, RepositoryResponse repo) {
        var branchResponses = getBranchResponses(login, repo);
        return branchResponses.map(responses -> new UserRepoResponse(repo.name(), login, responses));
    }

    /**
     * Gets users branches.
     *
     * @param login {@link String} user login
     * @param repo  {@link RepositoryResponse}
     * @return array of {@link BranchResponse}
     */
    private Optional<BranchResponse[]> getBranchResponses(String login, RepositoryResponse repo) {
        return Optional.ofNullable(restClient.get()
                .uri(SLASH + REPOS + SLASH + login + SLASH + repo.name() + SLASH + BRANCHES)
                .retrieve()
                .toEntity(BranchResponse[].class)
                .getBody());
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
    private UserResponse getUserByLogin(String login) throws UserNotFoundException, RestClientErrorException {
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

    /**
     * Gets users info.
     *
     * @return array of {@link UserResponse}
     * @throws RestClientErrorException when request to GitHub client failed
     */
    private UserResponse[] getUsers() throws RestClientErrorException {
        try {
            return restClient.get()
                    .uri(SLASH + USERS)
                    .retrieve()
                    .toEntity(UserResponse[].class)
                    .getBody();
        } catch (HttpClientErrorException e) {
            LOG.error("Error during process: {}", e.getMessage());
            throw new RestClientErrorException(e.getMessage());
        }
    }
}

package com.example.integration.response;

/**
 * User Repo Response.
 *
 * @author Anatolii Hamza
 */
public record UserRepoResponse(String repoName,
                               String owner,
                               BranchResponse[] branches) {
}

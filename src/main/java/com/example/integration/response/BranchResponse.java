package com.example.integration.response;

/**
 * Branch Response.
 *
 * @author Anatolii Hamza
 */
public record BranchResponse(String name,
                             CommitResponse commit) {
}

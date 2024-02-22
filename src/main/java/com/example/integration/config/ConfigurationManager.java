package com.example.integration.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration Manager.
 *
 * @author Anatolii Hamza
 */
@ConfigurationProperties(prefix = "app")
@Validated
public record ConfigurationManager(@Valid @NotNull ConfigurationManager.GithubApiConfig githubConfig) {

    public record GithubApiConfig(@NotEmpty String url,
                                  @NotEmpty String token,
                                  @NotEmpty String version) {
    }
}



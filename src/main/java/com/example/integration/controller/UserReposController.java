package com.example.integration.controller;

import com.example.integration.exception.ServiceException;
import com.example.integration.response.UserRepoResponse;
import com.example.integration.service.UserReposService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * User Repos Controller.
 *
 * @author Anatolii Hamza
 */
@RestController
@RequestMapping("/api/users")
@Validated
@AllArgsConstructor
public class UserReposController {

    private UserReposService UserReposService;

    @GetMapping("/repo/{login}")
    public List<UserRepoResponse> getUserRepo(@PathVariable String login) throws ServiceException {
        return UserReposService.getUserRepos(login);
    }
}

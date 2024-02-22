package com.example.integration.controller;

import com.example.integration.exception.ServiceException;
import com.example.integration.response.UserRepoResponse;
import com.example.integration.service.UserReposService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class UserReposController {

    @Autowired
    private UserReposService userReposService;

    @GetMapping("/repo/{login}")
    public List<UserRepoResponse> getUserRepo(@PathVariable String login) throws ServiceException {
        return userReposService.getUserRepos(login);
    }
}

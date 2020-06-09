package com.patternpedia.api;

import com.patternpedia.api.rest.controller.UserController;
import com.patternpedia.api.service.IssueService;
import com.vladmihalcea.hibernate.type.util.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;


@EnableTransactionManagement
@Slf4j
@RestController
@SpringBootApplication
public class PatternPediaAPI implements CommandLineRunner {

    @Autowired
    private UserController userController;

    @Autowired
    private IssueService issueService;

    public static void main(String[] args) {
        System.setProperty(Configuration.PropertyKey.PRINT_BANNER.getKey(), Boolean.FALSE.toString());
        SpringApplication.run(PatternPediaAPI.class, args);
    }

    @Override
    public void run(String... args) {

        log.info("PatternPediaAPI is up");
        // Used this for testing purposes, will be deleted in the final build
//        userController.defaultUsers();
//        Issue issue = new Issue();
//        issue.setUri("uri");
//        issue.setName("name");
//        issue.setDescription("description");
//        Issue p = issueService.createIssue(issue);
//        log.info(p.toString());
    }
}

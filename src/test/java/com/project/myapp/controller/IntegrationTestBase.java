package com.project.myapp.controller;

import com.project.myapp.repository.ContactRepository;
import com.project.myapp.repository.PersonRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;

public class IntegrationTestBase {

    @Autowired
    protected PersonRepository personRepository;

    @Autowired
    protected ContactRepository contactRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    protected void clearDatabase() {
        RestAssured.port = this.port;

        personRepository.deleteAll();
        contactRepository.deleteAll();
    }
}

package com.project.myapp.controller;

import static com.project.myapp.utils.MyAppTestUtils.createPost;
import static com.project.myapp.utils.MyAppTestUtils.createGet;
import static com.project.myapp.utils.MyAppTestUtils.createPut;
import static com.project.myapp.utils.MyAppTestUtils.createDelete;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import com.project.myapp.dto.request.PersonRequestDTO;
import com.project.myapp.dto.response.PersonResponseDTO;
import com.project.myapp.dto.response.ResponseDTO;
import com.project.myapp.model.Contact;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.common.mapper.TypeRef;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Collections;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class PersonControllerTest extends IntegrationTestBase {

    private static final String URL_BASE = "/api/v1/person";
    private static final String REGISTER = "/register";
    private static final String UPDATE = "/update";
    private static final String DELETE = "/delete";
    private static final String ID = "/{id}";

    private static final String CPF_1 = "11111111111";
    private static final String CPF_2 = "22222222222";

    @Test
    public void createPersonSuccessfully() {
        PersonRequestDTO person = createPersonRequestDTO(CPF_1);

        createPost(URL_BASE + REGISTER, person, HttpStatus.CREATED)
                .rootPath("data")
                    .body("name", equalTo(person.getName()))
                    .body("cpf", equalTo(person.getCpf()))
                    .body("birthDate", notNullValue())
                    .body("contact", notNullValue());
    }

    @Test
    public void createPersonExistent() {
        PersonRequestDTO person = createPersonRequestDTO(CPF_1);

        createPost(URL_BASE + REGISTER, person, HttpStatus.CREATED);

        PersonRequestDTO personExistent = createPersonRequestDTO(CPF_1);

        createPost(URL_BASE + REGISTER, personExistent, HttpStatus.BAD_REQUEST)
                .body("errors", hasSize(1))
                .body("errors[0].message", containsString("api.person.existent"));
    }

    @Test
    public void findPersonByIdSuccessfully(){
        PersonRequestDTO person = createPersonRequestDTO(CPF_1);

        ResponseDTO<PersonResponseDTO> response = createPost(URL_BASE + REGISTER, person, HttpStatus.CREATED)
                .extract()
                .body()
                .as(new TypeRef<ResponseDTO<PersonResponseDTO>>() {});

        RequestSpecification requestSpecification = new RequestSpecBuilder()
                .addPathParam("id", response.getData().getId())
                .build();

        createGet(URL_BASE + ID, requestSpecification, HttpStatus.OK);
    }

    @Test
    public void findPersonByIdNonexistent() {
        RequestSpecification requestSpecification = new RequestSpecBuilder()
                .addPathParam("id", 1)
                .build();

        createGet(URL_BASE + ID, requestSpecification, HttpStatus.BAD_REQUEST)
                .body("errors", hasSize(1))
                .body("errors[0].message", containsString("api.person.not-found"));
    }

    @Test
    public void findAllPerson() {

        PersonRequestDTO person1 = createPersonRequestDTO(CPF_1);
        PersonRequestDTO person2 = createPersonRequestDTO(CPF_2);

        createPost(URL_BASE + REGISTER, person1, HttpStatus.CREATED);
        createPost(URL_BASE + REGISTER, person2, HttpStatus.CREATED);

        createGet(URL_BASE, HttpStatus.OK)
                .rootPath("data")
                    .body("content", hasSize(2));

    }

    @Test
    public void updatePersonSuccessfully() {
        PersonRequestDTO person = createPersonRequestDTO(CPF_1);

        ResponseDTO<PersonResponseDTO> response = createPost(URL_BASE + REGISTER, person, HttpStatus.CREATED)
                .extract()
                .body()
                .as(new TypeRef<ResponseDTO<PersonResponseDTO>>() {});

        String idPerson = "/" + response.getData().getId();

        person.setName("Pedro");
        person.setBirthDate(LocalDate.parse("1999-11-23"));
        person.setCpf(CPF_2);

        createPut(URL_BASE + idPerson + UPDATE, person, HttpStatus.NO_CONTENT);

        RequestSpecification requestSpecification = new RequestSpecBuilder()
                .addPathParam("id", response.getData().getId())
                .build();

        createGet(URL_BASE + ID, requestSpecification, HttpStatus.OK)
                .rootPath("data")
                    .body("name", equalTo(person.getName()))
                    .body("cpf", equalTo(person.getCpf()))
                    .body("birthDate", notNullValue());
    }

    @Test
    public void updatePersonNonexistent() {
        PersonRequestDTO person = createPersonRequestDTO(CPF_1);

        createPut(URL_BASE + "/" + 1 + UPDATE, person, HttpStatus.BAD_REQUEST)
                .body("errors", hasSize(1))
                .body("errors[0].message", containsString("api.person.not-found"));
    }

    @Test
    public void deletePersonSuccessfully() {
        PersonRequestDTO person = createPersonRequestDTO(CPF_1);

        ResponseDTO<PersonResponseDTO> response = createPost(URL_BASE + REGISTER, person, HttpStatus.CREATED)
                .extract()
                .body()
                .as(new TypeRef<ResponseDTO<PersonResponseDTO>>() {});

        RequestSpecification requestSpecification = new RequestSpecBuilder()
                .addPathParam("id", response.getData().getId())
                .build();

        createDelete(URL_BASE + ID + DELETE, requestSpecification, HttpStatus.NO_CONTENT);

        createGet(URL_BASE + ID, requestSpecification, HttpStatus.BAD_REQUEST)
                .body("errors", hasSize(1))
                .body("errors[0].message", containsString("api.person.not-found"));
    }

    @Test
    public void deletePersonNonexistent() {
        RequestSpecification requestSpecification = new RequestSpecBuilder()
                .addPathParam("id", 1)
                .build();

        createDelete(URL_BASE + ID + DELETE, requestSpecification, HttpStatus.BAD_REQUEST)
                .body("errors", hasSize(1))
                .body("errors[0].message", containsString("api.person.not-found"));
    }

    public PersonRequestDTO createPersonRequestDTO(String cpf) {
        Contact contact = createContactModel();

        return PersonRequestDTO.builder()
                .name("Wesley")
                .cpf(cpf)
                .birthDate(LocalDate.parse("1997-03-26"))
                .contact(Collections.singletonList(contact))
                .build();
    }

    public Contact createContactModel() {
        return Contact.builder()
                .name("Lucas")
                .email("lucas@gmail.com")
                .phone("44999995555")
                .build();
    }

}

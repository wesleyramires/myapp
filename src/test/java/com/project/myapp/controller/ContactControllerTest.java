package com.project.myapp.controller;

import static com.project.myapp.utils.MyAppTestUtils.createPost;
import static com.project.myapp.utils.MyAppTestUtils.createGet;
import static com.project.myapp.utils.MyAppTestUtils.createPut;
import static com.project.myapp.utils.MyAppTestUtils.createDelete;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import com.project.myapp.dto.request.ContactRequestDTO;
import com.project.myapp.dto.request.PersonRequestDTO;
import com.project.myapp.dto.response.ContactResponseDTO;
import com.project.myapp.dto.response.PersonResponseDTO;
import com.project.myapp.dto.response.ResponseDTO;
import com.project.myapp.model.Contact;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.common.mapper.TypeRef;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Collections;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ContactControllerTest extends IntegrationTestBase {

    private static final String URL_BASE = "/api/v1/person";
    private static final String CONTACT = "/contact";
    private static final String REGISTER = "/register";
    private static final String UPDATE = "/update";
    private static final String DELETE = "/delete";

    private static final String ID_PERSON = "/{id}";

    private static final String CPF = "11111111111";
    private static final String CONTACT_NAME = "Lucas";
    private static final String CONTACT_PHONE = "44999995555";
    private static final String CONTACT_EMAIL = "lucas@gmail.com";

    @Test
    public void registerContactSuccessfully() {
        PersonRequestDTO person = createPersonRequestDTO(CPF);

        ResponseDTO<PersonResponseDTO> response = createPost(URL_BASE + REGISTER, person, HttpStatus.CREATED)
                .extract()
                .body()
                .as(new TypeRef<ResponseDTO<PersonResponseDTO>>() {});

        RequestSpecification requestSpecification = new RequestSpecBuilder()
                .addPathParam("id", response.getData().getId())
                .build();

        createGet(URL_BASE + ID_PERSON, requestSpecification, HttpStatus.OK);

        ContactRequestDTO contact = createContactRequestDTO();

        createPost(URL_BASE + "/" + response.getData().getId() + CONTACT + REGISTER, contact, HttpStatus.CREATED)
                .rootPath("data")
                    .body("name", equalTo(contact.getName()))
                    .body("email", equalTo(contact.getEmail()))
                    .body("phone", equalTo(contact.getPhone()));
    }

    @Test
    public void registerContactExistent() {
        PersonRequestDTO person = createPersonRequestDTO(CPF);

        ResponseDTO<PersonResponseDTO> response = createPost(URL_BASE + REGISTER, person, HttpStatus.CREATED)
                .extract()
                .body()
                .as(new TypeRef<ResponseDTO<PersonResponseDTO>>() {});

        Long idPerson = response.getData().getId();

        ContactRequestDTO contact = createContactRequestDTO();
        contact.setName(CONTACT_NAME);
        contact.setEmail(CONTACT_EMAIL);
        contact.setPhone(CONTACT_PHONE);

        createPost(URL_BASE + "/" + idPerson + CONTACT + REGISTER, contact, HttpStatus.BAD_REQUEST)
                .body("errors", hasSize(1))
                .body("errors[0].message", containsString("api.contact.existent"));
    }

    @Test
    public void updateContactSuccessfully() {
        PersonRequestDTO person = createPersonRequestDTO(CPF);

        ResponseDTO<PersonResponseDTO> responsePerson = createPost(URL_BASE + REGISTER, person, HttpStatus.CREATED)
                .extract()
                .body()
                .as(new TypeRef<ResponseDTO<PersonResponseDTO>>() {});

        String idPerson = "/" + responsePerson.getData().getId();

        ContactRequestDTO contact = createContactRequestDTO();

        ResponseDTO<ContactResponseDTO> responseContact = createPost(URL_BASE + idPerson + CONTACT + REGISTER, contact, HttpStatus.CREATED)
                .extract()
                .body()
                .as(new TypeRef<ResponseDTO<ContactResponseDTO>>() {});

        String idContact = "/" + responseContact.getData().getId();

        contact.setName("João");
        contact.setEmail("joao@gmail.com");
        contact.setPhone("44988771155");

        createPut(URL_BASE + idPerson + CONTACT + idContact + UPDATE, contact, HttpStatus.NO_CONTENT);

        RequestSpecification requestSpecification = new RequestSpecBuilder()
                .addPathParam("id", responsePerson.getData().getId())
                .build();

        createGet(URL_BASE + ID_PERSON, requestSpecification, HttpStatus.OK)
                .rootPath("data")
                    .body("contact", hasSize(2))
                    .body("contact[1].name", equalTo(contact.getName()))
                    .body("contact[1].email", equalTo(contact.getEmail()))
                    .body("contact[1].phone", equalTo(contact.getPhone()));
    }

    @Test
    public void updateContactNonexistent() {
        PersonRequestDTO person = createPersonRequestDTO(CPF);

        ResponseDTO<PersonResponseDTO> response = createPost(URL_BASE + REGISTER, person, HttpStatus.CREATED)
                .extract()
                .body()
                .as(new TypeRef<ResponseDTO<PersonResponseDTO>>() {});

        String idPerson = "/" + response.getData().getId();
        String idContact = "/" + (response.getData().getContact().get(0).getId() + 10);

        ContactRequestDTO contact = createContactRequestDTO();

        createPut(URL_BASE + idPerson + CONTACT + idContact + UPDATE, contact, HttpStatus.BAD_REQUEST)
                .body("errors", hasSize(1))
                .body("errors[0].message", containsString("api.contact.not-found"));
    }

    @Test
    public void deleteContactSuccessfully() {
        PersonRequestDTO person = createPersonRequestDTO(CPF);

        ResponseDTO<PersonResponseDTO> response = createPost(URL_BASE + REGISTER, person, HttpStatus.CREATED)
                .extract()
                .body()
                .as(new TypeRef<ResponseDTO<PersonResponseDTO>>() {});

        String idPerson = "/" + response.getData().getId();

        ContactRequestDTO contact = createContactRequestDTO();

        ResponseDTO<ContactResponseDTO> responseContact = createPost(URL_BASE + idPerson +  CONTACT + REGISTER, contact, HttpStatus.CREATED)
                .extract()
                .body()
                .as(new TypeRef<ResponseDTO<ContactResponseDTO>>() {});

        createGet(URL_BASE + idPerson, HttpStatus.OK)
                .rootPath("data")
                .body("contact", hasSize(2));

        String idContact = "/" + responseContact.getData().getId();

        RequestSpecification requestSpecification = new RequestSpecBuilder().build();

        createDelete(URL_BASE + idPerson + CONTACT + idContact + DELETE, requestSpecification, HttpStatus.NO_CONTENT);

        createGet(URL_BASE + idPerson, HttpStatus.OK)
                .rootPath("data")
                    .body("contact", hasSize(1));

    }

    @Test
    public void deleteContactNonexistent() {
        PersonRequestDTO person = createPersonRequestDTO(CPF);

        ResponseDTO<PersonResponseDTO> response = createPost(URL_BASE + REGISTER, person, HttpStatus.CREATED)
                .extract()
                .body()
                .as(new TypeRef<ResponseDTO<PersonResponseDTO>>() {});

        String idPerson = "/" + response.getData().getId();
        String idContact = "/" + (response.getData().getId() + 10);

        RequestSpecification requestSpecification = new RequestSpecBuilder().build();

        createDelete(URL_BASE + idPerson + CONTACT + idContact + DELETE, requestSpecification, HttpStatus.BAD_REQUEST)
                .body("errors", hasSize(1))
                .body("errors[0].message", containsString("api.contact.not-found"));
    }

    public PersonRequestDTO createPersonRequestDTO(String cpf) {
        Contact contact = createContactModel();

        return PersonRequestDTO.builder()
                .name("Wesley")
                .cpf("11111111111")
                .birthDate(LocalDate.parse("1997-03-26"))
                .contact(Collections.singletonList(contact))
                .build();
    }

    public ContactRequestDTO createContactRequestDTO() {
        return ContactRequestDTO.builder()
                .name("Fernando")
                .email("fernando@gmail.com")
                .phone("44988116622")
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

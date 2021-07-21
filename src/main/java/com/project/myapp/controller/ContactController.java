package com.project.myapp.controller;

import com.project.myapp.dto.request.ContactRequestDTO;
import com.project.myapp.dto.response.ContactResponseDTO;
import com.project.myapp.dto.response.ResponseDTO;
import com.project.myapp.service.ContactService;
import com.project.myapp.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ContactController {

    @Autowired
    private final ContactService contactService;

    @Autowired
    private final PersonService personService;

    @PostMapping("/person/{idPerson}/contact/register")
    public ResponseEntity<ResponseDTO<ContactResponseDTO>> registerContact(@PathVariable Long idPerson,
                                                                           @RequestBody ContactRequestDTO requestDTO) {
        ContactResponseDTO responseDTO = contactService.register(idPerson, requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO<>(responseDTO));
    }

    @PutMapping("/person/{idPerson}/contact/{idContact}/update")
    public ResponseEntity<ResponseDTO<ContactResponseDTO>> editContact(@PathVariable Long idPerson,
                                                                       @PathVariable Long idContact,
                                                                       @RequestBody @Valid ContactRequestDTO requestDTO) {
        contactService.updateContact(idContact, idPerson, requestDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/person/{idPerson}/contact/{idContact}/delete")
    public ResponseEntity<ResponseDTO<ContactResponseDTO>> deleteContact(@PathVariable Long idPerson,
                                                                         @PathVariable Long idContact) {
        contactService.deleteContact(idContact, idPerson);
        return ResponseEntity.noContent().build();
    }

}

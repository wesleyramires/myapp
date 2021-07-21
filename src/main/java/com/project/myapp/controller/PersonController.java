package com.project.myapp.controller;

import com.project.myapp.dto.request.PersonRequestDTO;
import com.project.myapp.dto.response.PersonResponseDTO;
import com.project.myapp.dto.response.ResponseDTO;
import com.project.myapp.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
public class PersonController {

    @Autowired
    private final PersonService personService;

    @PostMapping("/person/register")
    public ResponseEntity<ResponseDTO<PersonResponseDTO>> registerPerson(@RequestBody @Valid PersonRequestDTO requestDTO) {
        PersonResponseDTO responseDTO = personService.register(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO<>(responseDTO));
    }

    @GetMapping("/person")
    public ResponseEntity<ResponseDTO<Page<PersonResponseDTO>>> listAll(Pageable pageable) {
        Page<PersonResponseDTO> personList = personService.findAllPerson(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO<>(personList));
    }

    @GetMapping("/person/{id}")
    public ResponseEntity<ResponseDTO<PersonResponseDTO>> listPerson(@PathVariable Long id) {
        PersonResponseDTO responseDTO = personService.findPersonById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO<>(responseDTO));
    }

    @PutMapping("/person/{id}/update")
    public ResponseEntity<ResponseDTO<PersonResponseDTO>> editPerson(@PathVariable Long id,
                                                                     @RequestBody @Valid PersonRequestDTO requestDTO) {
        personService.updatePerson(id, requestDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/person/{id}/delete")
    public ResponseEntity<ResponseDTO<PersonResponseDTO>> editPerson(@PathVariable Long id) {
        personService.deletePerson(id);
        return ResponseEntity.noContent().build();
    }

}

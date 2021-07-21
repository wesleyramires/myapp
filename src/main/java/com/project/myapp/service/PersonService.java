package com.project.myapp.service;

import com.project.myapp.dto.request.PersonRequestDTO;
import com.project.myapp.dto.response.PersonResponseDTO;
import com.project.myapp.exception.ServiceException;
import com.project.myapp.model.Contact;
import com.project.myapp.model.Person;
import com.project.myapp.repository.PersonRepository;
import com.project.myapp.util.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonService {

    @Autowired
    private final PersonRepository personRepository;

    @Autowired
    private final ContactService  contactService;

    @Transactional
    public PersonResponseDTO register(PersonRequestDTO requestDTO) {
        validateRegister(requestDTO);

        Person person = personRequestDTOForEntity(requestDTO, new Person());

        personRepository.save(person);

        for (Contact contact : requestDTO.getContact()){
            contactService.registerContactInRegisterPerson(person.getId(), contact);
        }

        return entityForPersonResponseDTO(person);
    }

    @Transactional
    public void updatePerson(Long id, PersonRequestDTO requestDTO) {
        validateUpdate(id, requestDTO);

        Person personUpdated = personRequestDTOForEntity(requestDTO, searchPersonById(id));
        personRepository.save(personUpdated);
    }

    @Transactional
    public void deletePerson(Long id) {
        Person personDeleted = searchPersonById(id);
        personRepository.delete(personDeleted);
    }

    public PersonResponseDTO findPersonById(Long id) {
        Person person = searchPersonById(id);
        return entityForPersonResponseDTO(person);
    }

    public Page<PersonResponseDTO> findAllPerson(Pageable pageable) {
        Page<Person> personList = personRepository.findAll(pageable);
        return personList.map(this::entityForPersonResponseDTO) ;
    }

    private void validateRegister(PersonRequestDTO requestDTO) {
        validateCpf(requestDTO.getCpf());
        validateBirthDate(requestDTO.getBirthDate());
        validateListContact(requestDTO.getContact());

        if(personRepository.findByCpf(requestDTO.getCpf()).isPresent()) {
            throw new ServiceException(ErrorMessage.PERSON_EXISTENT, requestDTO.getCpf());
        }
    }

    private void validateUpdate(Long id, PersonRequestDTO requestDTO) {
        validateCpf(requestDTO.getCpf());
        validateBirthDate(requestDTO.getBirthDate());
        validateListContact(requestDTO.getContact());

        Optional<Person> personExistent = personRepository.findByCpf(requestDTO.getCpf());

        if (personExistent.isPresent() && !personExistent.get().getId().equals(id)) {
            throw new ServiceException(ErrorMessage.PERSON_EXISTENT, requestDTO.getCpf());
        }
    }

    private Person searchPersonById(Long id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new ServiceException(ErrorMessage.PERSON_NOT_FOUND, id));
    }

    private void validateBirthDate(LocalDate localDate) {
        if (localDate.isAfter(LocalDate.now())) {
            throw new ServiceException(ErrorMessage.PERSON_DATE_ERROR, localDate);
        }
    }

    private void validateCpf(String cpf) {
        if(cpf.length() != 11) {
            throw new ServiceException(ErrorMessage.PERSON_CPF_ERROR, cpf);
        }
    }

    private void validateListContact(List<Contact> list) {
        if(list == null || list.isEmpty()) {
            throw new ServiceException(ErrorMessage.PERSON_ERROR, list);
        }
    }

    private Person personRequestDTOForEntity(PersonRequestDTO dto, Person person) {
        person.setName(dto.getName());
        person.setCpf(dto.getCpf());
        person.setBirthDate(dto.getBirthDate());
        person.setContact(dto.getContact());
        return person;
    }

    private PersonResponseDTO entityForPersonResponseDTO(Person person) {
        return PersonResponseDTO.builder()
                                .id(person.getId())
                                .name(person.getName())
                                .cpf(person.getCpf())
                                .birthDate(person.getBirthDate())
                                .contact(person.getContact())
                                .build();
    }

}

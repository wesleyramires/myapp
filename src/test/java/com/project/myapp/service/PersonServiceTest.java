package com.project.myapp.service;

import com.project.myapp.dto.request.PersonRequestDTO;
import com.project.myapp.dto.response.ContactResponseDTO;
import com.project.myapp.dto.response.PersonResponseDTO;
import com.project.myapp.exception.ServiceException;
import com.project.myapp.model.Contact;
import com.project.myapp.model.Person;
import com.project.myapp.repository.PersonRepository;
import com.project.myapp.util.ErrorMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private ContactService contactService;

    @InjectMocks
    private PersonService personService;

    @Test
    public void registerPerson_ReturnSuccess() {
        PersonRequestDTO personRequestDTO = createPersonRequestDTO();
        ContactResponseDTO contactResponseDTO = createContactResponseDTO();

        when(personRepository.findByCpf(personRequestDTO.getCpf())).thenReturn(Optional.empty());
        when(contactService.registerContactInRegisterPerson(any(), any())).thenReturn(contactResponseDTO);

        PersonResponseDTO personResponseDTO = personService.register(personRequestDTO);

        verify(personRepository).findByCpf(any());
        verify(personRepository).save(any());
        verifyNoMoreInteractions(personRepository);

        assertEquals(personResponseDTO.getCpf(), personRequestDTO.getCpf());
        assertEquals(personResponseDTO.getContact(), personRequestDTO.getContact());
    }

    @Test
    public void registerPersonExistent_ReturnException() {
        PersonRequestDTO requestDTO = createPersonRequestDTO();
        Person personExistent = createPersonModel("11111111111");

        when(personRepository.findByCpf(requestDTO.getCpf())).thenReturn(Optional.of(personExistent));

        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> personService.register(requestDTO));

        verify(personRepository).findByCpf(personExistent.getCpf());
        verifyNoMoreInteractions(personRepository);

        assertEquals(ErrorMessage.PERSON_EXISTENT.getMessage(), serviceException.getCodeErrors());
    }

    @Test
    public void updatePersonExistent_ReturnSuccess() {
        PersonRequestDTO requestDTO = createPersonRequestDTO();
        Person person = createPersonModel("11111111111");

        when(personRepository.findByCpf(person.getCpf())).thenReturn(Optional.empty());
        when(personRepository.findById(person.getId())).thenReturn(Optional.of(person));

        personService.updatePerson(person.getId(), requestDTO);

        verify(personRepository).findByCpf(person.getCpf());
        verify(personRepository).findById(person.getId());
        verify(personRepository).save(any(Person.class));
        verifyNoMoreInteractions(personRepository);
    }

    @Test
    public void updatePersonNonexistent_ReturnException() {
        PersonRequestDTO requestDTO = createPersonRequestDTO();
        Person person = createPersonModel("11111111111");

        when(personRepository.findByCpf(person.getCpf())).thenReturn(Optional.empty());
        when(personRepository.findById(person.getId())).thenReturn(Optional.empty());

        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> personService.updatePerson(person.getId(), requestDTO));


        verify(personRepository).findByCpf(person.getCpf());
        verify(personRepository).findById(person.getId());
        verifyNoMoreInteractions(personRepository);

        assertEquals(ErrorMessage.PERSON_NOT_FOUND.getMessage(), serviceException.getCodeErrors());
    }

    @Test
    public void findPersonById_ReturnSuccess() {
        Person person = createPersonModel("11111111111");

        when(personRepository.findById(person.getId())).thenReturn(Optional.of(person));

        PersonResponseDTO personResponseDTO = personService.findPersonById(1L);

        verify(personRepository).findById(person.getId());
        verifyNoMoreInteractions(personRepository);

        assertEquals(person.getCpf(), personResponseDTO.getCpf());
    }

    @Test
    public void findPersonById_ReturnException() {
        when(personRepository.findById(anyLong())).thenReturn(Optional.empty());

        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> personService.findPersonById(1L));

        verify(personRepository).findById(anyLong());
        verifyNoMoreInteractions(personRepository);

        assertEquals(ErrorMessage.PERSON_NOT_FOUND.getMessage(), serviceException.getCodeErrors());
    }

    @Test
    public void findAllPerson_ReturnSuccess() {
        Person person = createPersonModel("11111111111");
        Person person1 = createPersonModel("22222222222");
        Person person2 = createPersonModel("33333333333");
        Person person3 = createPersonModel("44444444444");

        Pageable pageable = PageRequest.of(1, 20);

        List<Person> personList = Arrays.asList(person, person1, person2, person3);
        Page<Person> personPage = new PageImpl<>(personList);

        when(personRepository.findAll(pageable)).thenReturn(personPage);

        Page<PersonResponseDTO> personResponseDTO = personService.findAllPerson(pageable);
        List<PersonResponseDTO> personResponseDTOList = personResponseDTO.getContent();

        verify(personRepository).findAll(pageable);
        verifyNoMoreInteractions(personRepository);

        assertEquals(personResponseDTOList.size(), personList.size());
    }

    @Test
    public void findAllPersonReturn_ReturnEmpty() {
        Pageable pageable = PageRequest.of(1, 20);

        List<Person> personList = new ArrayList<>();
        Page<Person> personPage = new PageImpl<>(personList);

        when(personRepository.findAll(pageable)).thenReturn(personPage);

        Page<PersonResponseDTO> personResponseDTO = personService.findAllPerson(pageable);
        List<PersonResponseDTO> personResponseDTOList = personResponseDTO.getContent();

        verify(personRepository).findAll(pageable);
        verifyNoMoreInteractions(personRepository);

        assertEquals(personResponseDTOList.isEmpty(), personList.isEmpty());
    }

    @Test
    public void deletePersonExistent_ReturnSuccess() {
        Person person = createPersonModel("11111111111");

        when(personRepository.findById(person.getId())).thenReturn(Optional.of(person));
        doNothing().when(personRepository).delete(person);

        personService.deletePerson(person.getId());

        verify(personRepository).findById(person.getId());
        verify(personRepository).delete(any(Person.class));
        verifyNoMoreInteractions(personRepository);
    }

    @Test
    public void deletePersonNonexistent_ReturnException() {
        when(personRepository.findById(anyLong())).thenReturn(Optional.empty());

        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> personService.deletePerson(anyLong()));

        verify(personRepository).findById(anyLong());
        verifyNoMoreInteractions(personRepository);

        assertEquals(ErrorMessage.PERSON_NOT_FOUND.getMessage(), serviceException.getCodeErrors());
    }

    public PersonRequestDTO createPersonRequestDTO() {
        Contact contact = createContactModel();

        return PersonRequestDTO.builder()
                .name("Wesley")
                .cpf("11111111111")
                .birthDate(LocalDate.parse("1997-03-26"))
                .contact(Collections.singletonList(contact))
                .build();
    }

    public Person createPersonModel(String cpf) {
        Contact contact = createContactModel();

        return Person.builder()
                .id(1L)
                .name("Wesley")
                .cpf(cpf)
                .birthDate(LocalDate.parse("1997-03-26"))
                .contact(Collections.singletonList(contact))
                .build();
    }

    public Contact createContactModel() {
        return Contact.builder()
                .id(1L)
                .name("Lucas")
                .email("lucas@gmail.com")
                .phone("44999995555")
                .build();
    }

    public ContactResponseDTO createContactResponseDTO() {
       return ContactResponseDTO.builder()
                    .id(1L)
                    .name("Lucas")
                    .email("lucas@gmail.com")
                    .phone("44999995555")
                    .personId(1L)
                    .build();

    }
}

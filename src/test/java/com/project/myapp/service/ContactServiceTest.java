package com.project.myapp.service;

import com.project.myapp.dto.request.ContactRequestDTO;
import com.project.myapp.dto.response.ContactResponseDTO;
import com.project.myapp.exception.ServiceException;
import com.project.myapp.model.Contact;
import com.project.myapp.model.Person;
import com.project.myapp.repository.ContactRepository;
import com.project.myapp.repository.PersonRepository;
import com.project.myapp.util.ErrorMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private ContactService contactService;

    @Test
    public void registerContact_ReturnSuccess() {
        ContactRequestDTO contactRequestDTO = createContactRequestDTO();
        ContactResponseDTO contactResponseDTO = createContactResponseDTO();

        Person person = createPersonModel("11111111111");

        when(personRepository.findById(person.getId())).thenReturn(Optional.of(person));
        when(contactRepository.findByEmailAndByPersonId(eq(person.getId()), eq(contactRequestDTO.getEmail()))).thenReturn(Optional.empty());
        when(contactRepository.findByPhoneAndByPersonId(eq(person.getId()), eq(contactRequestDTO.getPhone()))).thenReturn(Optional.empty());

        contactService.register(person.getId(), contactRequestDTO);

        verify(contactRepository).findByPhoneAndByPersonId(anyLong(), anyString());
        verify(contactRepository).findByEmailAndByPersonId(anyLong(), anyString());
        verify(contactRepository).save(any(Contact.class));

        assertEquals(contactResponseDTO.getEmail(), contactRequestDTO.getEmail());
        assertEquals(contactResponseDTO.getPhone(), contactRequestDTO.getPhone());
    }

    @Test
    public void registerContactExistentByEmail_ReturnException() {
        ContactRequestDTO contactRequestDTO = createContactRequestDTO();

        Person person = createPersonModel("11111111111");
        Contact contactExistent = createContactModel();

        when(personRepository.findById(person.getId())).thenReturn(Optional.of(person));
        when(contactRepository.findByEmailAndByPersonId(eq(person.getId()), eq(contactRequestDTO.getEmail()))).thenReturn(Optional.of(contactExistent));

        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> contactService.register(person.getId(), contactRequestDTO));

        verify(contactRepository).findByEmailAndByPersonId(anyLong(), anyString());
        verifyNoMoreInteractions(contactRepository);

        assertEquals(ErrorMessage.CONTACT_EXISTENT.getMessage(), serviceException.getCodeErrors());
    }

    @Test
    public void registerContactExistentByPhone_ReturnException() {
        ContactRequestDTO contactRequestDTO = createContactRequestDTO();

        Person person = createPersonModel("11111111111");

        Contact contactExistent = createContactModel();

        when(personRepository.findById(person.getId())).thenReturn(Optional.of(person));
        when(contactRepository.findByPhoneAndByPersonId(eq(person.getId()), eq(contactRequestDTO.getPhone()))).thenReturn(Optional.of(contactExistent));

        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> contactService.register(person.getId(), contactRequestDTO));

        verify(contactRepository).findByEmailAndByPersonId(anyLong(), anyString());
        verifyNoMoreInteractions(contactRepository);

        assertEquals(ErrorMessage.CONTACT_EXISTENT.getMessage(), serviceException.getCodeErrors());
    }

    @Test
    public void updateContactExistent_ReturnSuccess() {
        ContactRequestDTO contactRequestDTO = createContactRequestDTO();

        Person person = createPersonModel("11111111111");

        Contact contact = createContactModel();

        when(contactRepository.findByEmail(contactRequestDTO.getEmail())).thenReturn(Optional.empty());
        when(contactRepository.findById(contact.getId())).thenReturn(Optional.of(contact));
        when(personRepository.findById(person.getId())).thenReturn(Optional.of(person));

        contactService.updateContact(contact.getId(), person.getId(), contactRequestDTO);

        verify(contactRepository).findById(contact.getId());
        verify(contactRepository).findByEmail(contact.getEmail());
        verify(contactRepository).save(any(Contact.class));
        verify(personRepository).findById(person.getId());
        verifyNoMoreInteractions(contactRepository);
        verifyNoMoreInteractions(personRepository);
    }

    @Test
    public void updateContactNonexistent_ReturnException() {
        ContactRequestDTO contactRequestDTO = createContactRequestDTO();

        Person person = createPersonModel("11111111111");

        Contact contact = createContactModel();

        when(contactRepository.findByEmail(contactRequestDTO.getEmail())).thenReturn(Optional.empty());
        when(contactRepository.findById(contact.getId())).thenReturn(Optional.empty());

        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> contactService.updateContact(contact.getId(), person.getId(), contactRequestDTO));

        verify(contactRepository).findById(contact.getId());
        verify(contactRepository).findByEmail(contact.getEmail());
        verifyNoMoreInteractions(contactRepository);
        verifyNoMoreInteractions(personRepository);

        assertEquals(ErrorMessage.CONTACT_NOT_FOUND.getMessage(), serviceException.getCodeErrors());
    }

    @Test
    public void deleteContactExistent_ReturnSuccess() {
        Contact contact = createContactModel();
        Person person = createPersonModel("11111111111");

        when(contactRepository.findByIdAndPersonId(contact.getId(), person.getId())).thenReturn(Optional.of(contact));
        doNothing().when(contactRepository).delete(contact);

        contactService.deleteContact(contact.getId(), person.getId());

        verify(contactRepository).findByIdAndPersonId(contact.getId(), person.getId());
        verify(contactRepository).delete(contact);
        verifyNoMoreInteractions(contactRepository);
    }

    @Test
    public void deleteContactNonexistent_ReturnException() {
        when(contactRepository.findByIdAndPersonId(anyLong(), anyLong())).thenReturn(Optional.empty());

        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> contactService.deleteContact(anyLong(), anyLong()));

        verify(contactRepository).findByIdAndPersonId(anyLong(), anyLong());
        verifyNoMoreInteractions(contactRepository);

        assertEquals(ErrorMessage.CONTACT_NOT_FOUND.getMessage(), serviceException.getCodeErrors());
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

    public ContactRequestDTO createContactRequestDTO() {
        return ContactRequestDTO.builder()
                .name("Lucas")
                .email("lucas@gmail.com")
                .phone("44999995555")
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

package com.project.myapp.service;

import com.project.myapp.dto.request.ContactRequestDTO;
import com.project.myapp.dto.response.ContactResponseDTO;
import com.project.myapp.exception.ServiceException;
import com.project.myapp.model.Contact;
import com.project.myapp.model.Person;
import com.project.myapp.repository.ContactRepository;
import com.project.myapp.repository.PersonRepository;
import com.project.myapp.util.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContactService {

    @Autowired
    private final ContactRepository contactRepository;

    @Autowired
    private final PersonRepository personRepository;

    @Transactional
    public ContactResponseDTO register(Long id, ContactRequestDTO requestDTO) {
        Person person = searchPersonById(id);

        validateRegister(id, requestDTO);

        Contact contact = Contact.builder()
                .name(requestDTO.getName())
                .email(requestDTO.getEmail())
                .phone(requestDTO.getPhone())
                .person(person)
                .build();

        contactRepository.save(contact);

        return entityForContactResponseDTO(contact);
    }

    @Transactional
    public ContactResponseDTO registerContactInRegisterPerson(Long id, Contact contact) {
        Person person = searchPersonById(id);

        ContactRequestDTO requestDTO = ContactRequestDTO.builder()
                .name(contact.getName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .build();

        validateRegister(id, requestDTO);

        contact.setPerson(person);

        contactRepository.save(contact);

        return entityForContactResponseDTO(contact);
    }

    @Transactional
    public void updateContact(Long idContact, Long idPerson, ContactRequestDTO requestDTO) {
        validateUpdate(idContact, requestDTO);

        Contact contactUpdated = contactRequestDTOForEntity(requestDTO, searchContactById(idContact), searchPersonById(idPerson));
        contactRepository.save(contactUpdated);
    }

    @Transactional
    public void deleteContact(Long idContact, Long idPerson) {
        Contact contactDeleted = searchContactByIdAndPersonId(idContact, idPerson);
        contactRepository.delete(contactDeleted);
    }

    private void validateRegister(Long id, ContactRequestDTO requestDTO) {
        validateEmail(requestDTO.getEmail());
        validatePhone(requestDTO.getPhone());

        if(contactRepository.findByEmailAndByPersonId(id, requestDTO.getEmail()).isPresent()) {
            throw new ServiceException(ErrorMessage.CONTACT_EXISTENT, requestDTO.getEmail());
        } else if (contactRepository.findByPhoneAndByPersonId(id, requestDTO.getPhone()).isPresent()) {
            throw new ServiceException(ErrorMessage.CONTACT_EXISTENT, requestDTO.getPhone());
        }
    }

    private void validateUpdate(Long id, ContactRequestDTO requestDTO) {
        validateEmail(requestDTO.getEmail());
        validatePhone(requestDTO.getPhone());

        Optional<Contact> contactExistent = contactRepository.findByEmail(requestDTO.getEmail());

        if (contactExistent.isPresent() && !contactExistent.get().getId().equals(id)) {
            throw new ServiceException(ErrorMessage.CONTACT_EXISTENT, requestDTO.getEmail());
        }
    }

    private void validateEmail(String email) {
        if(!email.contains("@") || !email.contains(".com")) {
            throw new ServiceException(ErrorMessage.CONTACT_EMAIL_ERROR, email);
        }
    }

    private void validatePhone(String phone) {
        if(phone.length() != 11) {
            throw new ServiceException(ErrorMessage.CONTACT_PHONE_ERROR, phone);
        }
    }

    private Contact searchContactByIdAndPersonId(Long idContact, Long idPerson) {
        return contactRepository.findByIdAndPersonId(idContact, idPerson)
                .orElseThrow(() -> new ServiceException(ErrorMessage.CONTACT_NOT_FOUND, idContact));
    }

    private Contact searchContactById(Long id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new ServiceException(ErrorMessage.CONTACT_NOT_FOUND, id));
    }

    private Person searchPersonById(Long id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new ServiceException(ErrorMessage.PERSON_NOT_FOUND, id));
    }

    private Contact contactRequestDTOForEntity(ContactRequestDTO dto, Contact contact, Person person) {
        contact.setName(dto.getName());
        contact.setEmail(dto.getEmail());
        contact.setPhone(dto.getPhone());
        contact.setPerson(person);
        return contact;
    }

    public ContactResponseDTO entityForContactResponseDTO(Contact contact) {
        return ContactResponseDTO.builder()
                .id(contact.getId())
                .name(contact.getName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .personId(contact.getPerson().getId())
                .build();
    }

}

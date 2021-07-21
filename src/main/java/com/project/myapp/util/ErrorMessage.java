package com.project.myapp.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {

    PERSON_NOT_FOUND("api.person.not-found"),
    PERSON_EXISTENT("api.person.existent"),
    PERSON_CPF_ERROR("api.person.cpf-error"),
    PERSON_DATE_ERROR("api.person.date-error"),
    PERSON_ERROR("api.person.error-contact"),
    CONTACT_NOT_FOUND("api.contact.not-found"),
    CONTACT_EXISTENT("api.contact.existent"),
    CONTACT_EMAIL_ERROR("api.contact.email-error"),
    CONTACT_PHONE_ERROR("api.contact.phone-error"),
    INTERNAL_SERVER_ERROR("api.server-error");

    private final String message;
}

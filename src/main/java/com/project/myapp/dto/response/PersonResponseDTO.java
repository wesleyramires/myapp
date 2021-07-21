package com.project.myapp.dto.response;

import com.project.myapp.model.Contact;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonResponseDTO {

    private Long id;
    private String name;
    private String cpf;
    private LocalDate birthDate;
    private List<Contact> contact;
}

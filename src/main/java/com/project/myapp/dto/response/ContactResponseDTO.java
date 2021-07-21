package com.project.myapp.dto.response;

import com.project.myapp.model.Person;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactResponseDTO {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private Long personId;

}

package com.project.myapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponseDTO {

    private String field;
    private String message;

    public ErrorResponseDTO(String message) {
        this.message = message;
    }

}

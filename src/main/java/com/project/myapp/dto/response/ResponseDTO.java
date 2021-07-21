package com.project.myapp.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ResponseDTO<T> {

    private T data;
    private List<ErrorResponseDTO> errors;

    public ResponseDTO(T data) {
        this.data = data;
    }

    public static ResponseDTO<Object> withErrors(List<ErrorResponseDTO> errors) {
        ResponseDTO<Object> responseDTO = new ResponseDTO<>();
        responseDTO.setErrors(errors);
        return responseDTO;
    }

    public static ResponseDTO<Object> withError(ErrorResponseDTO error) {
        return withErrors(Arrays.asList(error));
    }

}

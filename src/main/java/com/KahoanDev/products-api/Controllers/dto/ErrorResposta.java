package com.KahoanDev.poc_crud_api.Controllers.dto;

import org.springframework.http.HttpStatus;

public record ErrorResposta(int status, String mensagem) {

    public static ErrorResposta conflito(String mensagem){
        return new ErrorResposta(HttpStatus.CONFLICT.value(), mensagem);
    }
}

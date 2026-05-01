package com.KahoanDev.poc_crud_api.Controllers.common;

import com.KahoanDev.poc_crud_api.Controllers.dto.ErrorResposta;
import com.KahoanDev.poc_crud_api.Exceptions.ProdutoCadastradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProdutoCadastradoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResposta handleProdutoCadastradoException(ProdutoCadastradoException e){
        return ErrorResposta.conflito(e.getMessage());
    }
}

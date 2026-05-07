package com.KahoanDev.products_api.Controllers.common;

import com.KahoanDev.products_api.Controllers.dto.ErrorResposta;
import com.KahoanDev.products_api.Exceptions.ProdutoJaCadastradoException;
import com.KahoanDev.products_api.Exceptions.UsuarioJaCadastradoException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProdutoJaCadastradoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResposta handleProdutoJaCadastradoException(ProdutoJaCadastradoException e){
        return ErrorResposta.conflito(e.getMessage());
    }

    @ExceptionHandler(UsuarioJaCadastradoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResposta handleUsuarioJaCadastradoException(UsuarioJaCadastradoException e){
        return ErrorResposta.conflito(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResposta handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> erros = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        return ErrorResposta.badRequest(erros.toString());
    }
}

package com.KahoanDev.poc_crud_api.Controllers;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

public interface GenericController {

    default URI gerarHeaderLoction(Long id){
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}

package com.KahoanDev.products_api.Controllers.dto;

import com.KahoanDev.products_api.Model.enums.UsuarioRoles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioDTO(
        @Email(message = "formato inválido")
        @NotBlank(message = "campo obrigatório")
        String email,
        @NotBlank(message = "campo obrigatório")
        String senha,
        UsuarioRoles role
) {
}

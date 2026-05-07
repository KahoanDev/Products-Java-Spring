package com.KahoanDev.products_api.Controllers.dto;

import com.KahoanDev.products_api.Model.enums.UsuarioRoles;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "Usuario")
public record UsuarioDTO(
        @Email(message = "formato inválido")
        @NotBlank(message = "campo obrigatório")
        String email,
        @NotBlank(message = "campo obrigatório")
        String senha,
        UsuarioRoles role
) {
}

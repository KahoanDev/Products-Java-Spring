package com.KahoanDev.products_api.Controllers.mappers;

import com.KahoanDev.products_api.Controllers.dto.UsuarioDTO;
import com.KahoanDev.products_api.Model.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    Usuario toEntity(UsuarioDTO dto);
}

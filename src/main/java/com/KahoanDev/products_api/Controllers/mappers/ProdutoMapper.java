package com.KahoanDev.products_api.Controllers.mappers;

import com.KahoanDev.products_api.Controllers.dto.ProdutoDTO;
import com.KahoanDev.products_api.Model.Produto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    Produto toEntity(ProdutoDTO dto);

    ProdutoDTO toDTO(Produto produto);
}

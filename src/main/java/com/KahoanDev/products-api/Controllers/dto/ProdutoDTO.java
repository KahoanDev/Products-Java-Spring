package com.KahoanDev.poc_crud_api.Controllers.dto;

import com.KahoanDev.poc_crud_api.Model.enums.TipoProduto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "Produto")
public record ProdutoDTO(
        Long id,
        @NotBlank(message = "campo obrigatório!")
        @Size(min = 2, max = 200, message = "campo fora do tamanho padrão")
        String descricao,
        TipoProduto tipo,
        Long quantidade
) {
}

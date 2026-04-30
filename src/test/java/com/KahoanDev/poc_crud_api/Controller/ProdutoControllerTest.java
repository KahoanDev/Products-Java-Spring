package com.KahoanDev.poc_crud_api.Controller;

import com.KahoanDev.poc_crud_api.Controllers.ProdutoController;
import com.KahoanDev.poc_crud_api.Controllers.dto.ProdutoDTO;
import com.KahoanDev.poc_crud_api.Controllers.mappers.ProdutoMapper;
import com.KahoanDev.poc_crud_api.Model.Produto;
import com.KahoanDev.poc_crud_api.Model.enums.TipoProduto;
import com.KahoanDev.poc_crud_api.Service.ProdutoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProdutoController.class)
public class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProdutoService service;

    @MockitoBean
    private ProdutoMapper mapper;

    Produto produto;
    ProdutoDTO dto;

    @BeforeEach
    void setUp() {
        dto = new ProdutoDTO(1L, "Iphone 16 Pro Max 512gb", TipoProduto.ELETRONICO, 10L);

        produto = new Produto();
        produto.setId(1L);
        produto.setDescricao("Iphone 16 Pro Max 512gb");
        produto.setTipo(TipoProduto.ELETRONICO);
        produto.setQuantidade(10L);
    }

    // =====================================================================
    // POST /produto
    // =====================================================================

    @Nested
    @DisplayName("POST /produto")
    class Salvar {
        @Test
        @DisplayName("deve retornar 201 e header Location ao criar produto válido")
        void testGivenProduct_WhenCreate_ThenReturnStatusCreated() throws Exception {
            // Given / Arrange
            given(mapper.toEntity(any())).willReturn(produto);
            given(service.salvar(any())).willReturn(produto);

            // When / Act
            ResultActions response = mockMvc.perform(post("/produto")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)));

            // Then / Assert

            response
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(header().string("Location", containsString("/produto/1")));
        }

        @Test
        @DisplayName("deve retornar 409 quando produto já existir")
        void testProductThatExists_WhenCreate_ThenReturnStatusConflict() throws Exception {
            // Given / Arrange
            given(mapper.toEntity(any())).willReturn(produto);
            given(service.salvar(any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Produto já cadastrado!"));

            // When / Act
            ResultActions response = mockMvc.perform(post("/produto")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)));

            // Then / Assert
            response
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("deve retornar 400 quando body estiver vazio")
        void testBadRequest_WhenCreate() throws Exception{
            mockMvc.perform(post("/produto")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }

    // =====================================================================
    // GET /produto
    // =====================================================================

    @Nested
    @DisplayName("GET /produto")
    class PesquisarTudo {
        @Test
        @DisplayName("deve retornar 200 com lista de produtos")
        void testFindAllProducts_ThenReturnListProducts() throws Exception {
            // Given / Arrange
            given(service.pesquisarTudo()).willReturn(List.of(produto));
            given(mapper.toDTO(any())).willReturn(dto);

            // When / Act
            ResultActions response = mockMvc.perform(get("/produto"));

            response
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].descricao").value("Iphone 16 Pro Max 512gb"));
        }

        @Test
        @DisplayName("deve retornar 200 com lista vazia quando não há produtos")
        void testFindAllProducts_ThenReturnEmptyListProducts() throws Exception {
            // Given / Arrange
            given(service.pesquisarTudo()).willReturn(List.of());

            // When / Act
            ResultActions response = mockMvc.perform(get("/produto"));

            // Then / Assert
            response
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    // =====================================================================
    // GET /produto/{id}
    // =====================================================================

    @Nested
    @DisplayName("GET /produto/{id}")
    class PesquisarPorId {

        @Test
        @DisplayName("deve retornar 200 com produto quando id existe")
        void deveRetornar200QuandoIdExiste() throws Exception {
            given(service.pesquisar(1L)).willReturn(Optional.of(produto));
            given(mapper.toDTO(produto)).willReturn(dto);

            mockMvc.perform(get("/produto/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.descricao").value("Iphone 16 Pro Max 512gb"))
                    .andExpect(jsonPath("$.quantidade").value(10));
        }

        @Test
        @DisplayName("deve retornar 404 quando id não existe")
        void deveRetornar404QuandoIdNaoExiste() throws Exception {
            given(service.pesquisar(999L)).willReturn(Optional.empty());

            mockMvc.perform(get("/produto/999"))
                    .andExpect(status().isNotFound());
        }
    }

    // =====================================================================
    // PUT /produto/{id}
    // =====================================================================

    @Nested
    @DisplayName("PUT /produto/{id}")
    class Atualizar {

        @Test
        @DisplayName("deve retornar 204 ao atualizar produto existente")
        void deveRetornar204AoAtualizar() throws Exception {
            given(service.pesquisar(1L)).willReturn(Optional.of(produto));

            mockMvc.perform(put("/produto/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNoContent());

            verify(service, times(1)).atualizar(any());
        }

        @Test
        @DisplayName("deve retornar 404 ao tentar atualizar id inexistente")
        void deveRetornar404AoAtualizarIdInexistente() throws Exception {
            given(service.pesquisar(999L)).willReturn(Optional.empty());

            mockMvc.perform(put("/produto/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound());

            verify(service, never()).atualizar(any());
        }
    }

    // =====================================================================
    // DELETE /produto/{id}
    // =====================================================================

    @Nested
    @DisplayName("DELETE /produto/{id}")
    class Deletar {

        @Test
        @DisplayName("deve retornar 204 ao deletar produto existente")
        void deveRetornar204AoDeletar() throws Exception {
            given(service.pesquisar(1L)).willReturn(Optional.of(produto));

            mockMvc.perform(delete("/produto/1"))
                    .andExpect(status().isNoContent());

            verify(service, times(1)).deletar(1L);
        }

        @Test
        @DisplayName("deve retornar 404 ao tentar deletar id inexistente")
        void deveRetornar404AoDeletarIdInexistente() throws Exception {
            given(service.pesquisar(999L)).willReturn(Optional.empty());

            mockMvc.perform(delete("/produto/999"))
                    .andExpect(status().isNotFound());

            verify(service, never()).deletar(any());
        }
    }
}

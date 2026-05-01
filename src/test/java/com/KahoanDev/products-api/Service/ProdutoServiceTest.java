package com.KahoanDev.poc_crud_api.Service;

import com.KahoanDev.poc_crud_api.Exceptions.ProdutoCadastradoException;
import com.KahoanDev.poc_crud_api.Model.Produto;
import com.KahoanDev.poc_crud_api.Model.enums.TipoProduto;
import com.KahoanDev.poc_crud_api.Repository.ProdutoRepository;
import com.KahoanDev.poc_crud_api.Validator.ProdutoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {
    @Mock
    private ProdutoRepository repository;

    @Mock
    private ProdutoValidator validator;

    @InjectMocks
    private ProdutoService service;

    private Produto produto;

    @BeforeEach
    void setUp() {
        produto = new Produto();
        produto.setId(1L);
        produto.setDescricao("Iphone 16 Pro Max 512gb");
        produto.setTipo(TipoProduto.ELETRONICO);
        produto.setQuantidade(10L);
    }


    // =====================================================================
    // SALVAR
    // =====================================================================

    @Nested
    @DisplayName("salvar()")
    class Salvar {
        @Test
        @DisplayName("deve salvar produto válido e retornar o produto salvo")
        void testValidateProductThenSave_WhenSaveProduct_ShouldReturn_SavedProduct(){
            // Given / Arrange
            given(repository.save(any(Produto.class))).willReturn(produto);

            // When / Act
            Produto savedProduct = service.salvar(produto);

            // Then / Assert
            assertNotNull(savedProduct);
            assertTrue(savedProduct.getId() > 0);
            assertEquals(1L, savedProduct.getId());
            assertEquals(produto.getDescricao(), savedProduct.getDescricao());
            assertEquals(produto.getTipo(), savedProduct.getTipo());
            assertEquals(produto.getQuantidade(), savedProduct.getQuantidade());

            verify(repository, times(1)).save(produto);
            verify(validator, times(1)).validar(produto);
        }

        @Test
        @DisplayName("deve chamar o validator antes de salvar")
        void testValidateProduct_BeforeSaveProduct(){
            // Given / Arrange
            given(repository.save(any())).willReturn(produto);

            service.salvar(produto);

            // When / Act
            var inOrder = inOrder(validator, repository);

            // Then / Assert
            inOrder.verify(validator, times(1)).validar(produto);
            inOrder.verify(repository, times(1)).save(produto);
        }

        @Test
        @DisplayName("não deve salvar se o validator lançar exceção")
        void testShouldNotSaveProduct_WhenValidatorThrowException(){
            // Given / Arrange
            willThrow(new ProdutoCadastradoException("Produto já cadastrado!")).given(validator).validar(any());

            // When / Act
            assertThrows(ProdutoCadastradoException.class, () -> service.salvar(produto));

            // Then / Assert
            verify(repository, never()).save(any(Produto.class));
        }
    }

    // =====================================================================
    // PESQUISAR TUDO
    // =====================================================================

    @Nested
    @DisplayName("pesquisarTudo()")
    class PesquisarTudo {
        @Test
        @DisplayName("deve retornar lista com todos os produtos")
        void testFindAll_ShouldReturnListOfProducts(){
            // Given / Arrange
            var novoProduto = new Produto();
            novoProduto.setId(2L);
            novoProduto.setDescricao("Heiniken 330ml");
            novoProduto.setTipo(TipoProduto.BEBIDA);

            // When / Act
            given(repository.findAll()).willReturn(List.of(produto, novoProduto));
            List<Produto> result = service.pesquisarTudo();

            // Then / Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("Iphone 16 Pro Max 512gb", result.get(0).getDescricao());
            assertEquals("Heiniken 330ml", result.get(1).getDescricao());
        }

        @Test
        @DisplayName("deve retornar lista vazia quando não há produtos")
        void testFindAll_WhenListProductsIsEmpty(){
            // Given / Arrange
            given(repository.findAll()).willReturn(List.of());

            // When / Act
            List<Produto> result = service.pesquisarTudo();

            // Then / Assert
            assertEquals(0, result.size());
        }
    }

    // =====================================================================
    // PESQUISAR POR ID
    // =====================================================================

    @Nested
    @DisplayName("pesquisar(id)")
    class PesquisarPorId {
        @Test
        @DisplayName("deve retornar produto quando id existe")
        void testFindById_WhenIdExists(){
            // Given / Arrange
            given(repository.findById(1L)).willReturn(Optional.of(produto));

            // When / Act
            Optional<Produto> result = service.pesquisar(1L);

            // Then / Assert
            assertNotNull(result);
            assertEquals(produto.getDescricao(), result.get().getDescricao());
        }

        @Test
        @DisplayName("deve retornar Optional vazio quando id não existe")
        void testFindById_WhenIdNotExists(){
            // Given / Arrange
            given(repository.findById(999L)).willReturn(Optional.empty());

            // When / Act
            Optional<Produto> result = service.pesquisar(999L);

            // Then / Assert
            assertTrue(result.isEmpty());
        }
    }

    // =====================================================================
    // ATUALIZAR
    // =====================================================================

    @Nested
    @DisplayName("atualizar()")
    class Atualizar {
        @Test
        @DisplayName("deve salvar as atualizações")
        void testUpdate_WhenSaveChanges(){
            // Given / Arrange
            produto.setQuantidade(50L);
            service.atualizar(produto);

            // Then / Assert
            verify(repository, times(1)).save(produto);
            assertEquals(50L, produto.getQuantidade());
        }
    }

    // =====================================================================
    // DELETAR
    // =====================================================================

    @Nested
    @DisplayName("deletar(id)")
    class Deletar {

        @Test
        @DisplayName("deve chamar deleteById com o id correto")
        void testDeleteById_WhenIdIsDeleted() {
            service.deletar(1L);

            verify(repository, times(1)).deleteById(1L);
        }
    }
}
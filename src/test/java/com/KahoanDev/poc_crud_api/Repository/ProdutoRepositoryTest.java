package com.KahoanDev.poc_crud_api.Repository;

import com.KahoanDev.poc_crud_api.Model.Produto;
import com.KahoanDev.poc_crud_api.Model.enums.TipoProduto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
//@AutoConfigureTestDatabase(replace =  AutoConfigureTestDatabase.Replace.NONE)
public class ProdutoRepositoryTest {

    @Autowired
    private ProdutoRepository repository;

    private Produto produto;

    @BeforeEach
    public void setUp() {
        produto = new Produto();
        produto.setDescricao("Salgadinho Cheetos");
        produto.setTipo(TipoProduto.ALIMENTICIO);
        produto.setQuantidade(50L);
    }

    @Test
    @DisplayName("deve salvar produto e retornar o produto salvo")
    void testGivenProductObject_WhenSave_ThenShouldReturn_SavedProduct() {
        // Given / Arrange

        // When / Act
        Produto savedProduto = repository.save(produto);

        // Then / Assert
        assertNotNull(savedProduto);
        assertTrue(savedProduto.getId() > 0);
        assertEquals(savedProduto.getDescricao(), produto.getDescricao());
    }

    @Test
    @DisplayName("deve retornar lista com todos os produtos")
    void testGivenProductObject_WhenFindAll_ThenReturn_SavedProductList() {
        // Given / Arrange
        Produto produto1 = new Produto(
                "Desinfetante OMO",
                TipoProduto.LIMPEZA,
                20L
        );

        repository.save(produto);
        repository.save(produto1);

        // When / Act
        List<Produto> list = repository.findAll();

        // Then / Assert
        assertNotNull(list);
        assertFalse(list.isEmpty());
        assertEquals(2, list.size());
        assertTrue(list.contains(produto1));
    }

    @Test
    @DisplayName("deve retornar produto quando id existe")
    void testGivenProductObject_WhenFindById_ThenShouldReturn_ProductObject() {
        // Given / Arrange
        repository.save(produto);

        // When / Act
        Produto savedProduto = repository.findById(produto.getId()).get();

        // Then / Assert
        assertNotNull(savedProduto);
        assertTrue(savedProduto.getId() > 0);
        assertEquals(savedProduto.getId(), produto.getId());
        assertEquals(savedProduto.getDescricao(), produto.getDescricao());
        assertEquals(savedProduto.getTipo(), produto.getTipo());
        assertEquals(savedProduto.getQuantidade(), produto.getQuantidade());
    }

    @Test
    @DisplayName("deve retornar produto quando id existe")
    void testGivenProductObject_WhenFindByDescricaoAndTipo_ThenShouldReturn_ProductObject() {
        // Given / Arrange
        repository.save(produto);

        // When / Act
        Produto savedProduto = repository.findByDescricaoAndTipo(produto.getDescricao(), produto.getTipo()).get();

        // Then / Assert
        assertNotNull(savedProduto);
        assertTrue(savedProduto.getId() > 0);
        assertEquals(savedProduto.getId(), produto.getId());
        assertEquals(savedProduto.getDescricao(), produto.getDescricao());
        assertEquals(savedProduto.getTipo(), produto.getTipo());
        assertEquals(savedProduto.getQuantidade(), produto.getQuantidade());
    }
}

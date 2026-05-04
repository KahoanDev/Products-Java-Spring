package com.KahoanDev.products_api.Repository;

import com.KahoanDev.products_api.Model.Produto;
import com.KahoanDev.products_api.Model.enums.TipoProduto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    Optional<Produto> findByDescricaoAndTipo(String descricao, TipoProduto tipo);
}

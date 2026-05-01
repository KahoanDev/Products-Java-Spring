package com.KahoanDev.poc_crud_api.Repository;

import com.KahoanDev.poc_crud_api.Model.Produto;
import com.KahoanDev.poc_crud_api.Model.enums.TipoProduto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    Optional<Produto> findByDescricaoAndTipo(String descricao, TipoProduto tipo);
}

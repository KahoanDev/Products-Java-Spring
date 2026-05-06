package com.KahoanDev.products_api.Validator;

import com.KahoanDev.products_api.Model.Produto;
import com.KahoanDev.products_api.Repository.ProdutoRepository;
import com.KahoanDev.products_api.Exceptions.ProdutoJaCadastradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProdutoValidator {

    @Autowired
    private ProdutoRepository repository;

    public void validar(Produto produto){
        if (produtoJaCadastrado(produto)){
            throw new ProdutoJaCadastradoException("Produto já cadastrado!");
        }
    }

    private boolean produtoJaCadastrado(Produto produto) {
        Optional<Produto> produtoEncontrado = repository.findByDescricaoAndTipo(produto.getDescricao(), produto.getTipo());

        if (produto.getId() == null){
            return produtoEncontrado.isPresent();
        }

        return produtoEncontrado.isPresent() && !produto.getId().equals(produtoEncontrado.get().getId());
    }
}

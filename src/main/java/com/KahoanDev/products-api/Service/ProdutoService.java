package com.KahoanDev.poc_crud_api.Service;

import com.KahoanDev.poc_crud_api.Model.Produto;
import com.KahoanDev.poc_crud_api.Repository.ProdutoRepository;
import com.KahoanDev.poc_crud_api.Validator.ProdutoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository repository;
    private final ProdutoValidator validator;

    public Produto salvar(Produto produto){
        validator.validar(produto);
        return repository.save(produto);
    }

    public List<Produto> pesquisarTudo(){
        return repository.findAll();
    }

    public Optional<Produto> pesquisar(Long id) {
        return repository.findById(id);
    }

    public void atualizar(Produto produto) {
        repository.save(produto);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}

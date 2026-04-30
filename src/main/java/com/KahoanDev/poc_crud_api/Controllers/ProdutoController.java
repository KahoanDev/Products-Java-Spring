package com.KahoanDev.poc_crud_api.Controllers;

import com.KahoanDev.poc_crud_api.Controllers.dto.ProdutoDTO;
import com.KahoanDev.poc_crud_api.Controllers.mappers.ProdutoMapper;
import com.KahoanDev.poc_crud_api.Model.Produto;
import com.KahoanDev.poc_crud_api.Service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/produto")
@RequiredArgsConstructor
@Tag(name = "Produtos")
public class ProdutoController implements GenericController{

    private final ProdutoMapper mapper;
    private final ProdutoService service;

    @PostMapping
    @Operation(summary = "Salvar", description = "Cadastrar um novo produto")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cadastrado com sucesso!"),
            @ApiResponse(responseCode = "409", description = "Produto já cadastrado!")
    })
    public ResponseEntity<Void> salvar(@RequestBody @Valid ProdutoDTO dto){
        var produto = mapper.toEntity(dto);
        service.salvar(produto);
        URI location = gerarHeaderLoction(produto.getId());

        return ResponseEntity.created(location).build();
    }

    @GetMapping
    @Operation(summary = "Pesquisa todos", description = "Pesquisa todos os produtos cadastrados!")
    @ApiResponse(responseCode = "200", description = "Retorna todos os Produtos")
    public ResponseEntity<List<ProdutoDTO>> pesquisarTudo(){
        List<Produto> resultado = service.pesquisarTudo();
        List<ProdutoDTO> lista = resultado
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Pesquisa por Id", description = "Pesquisa o Produto pelo Id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto encontrado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<ProdutoDTO> pesquisar(@PathVariable Long id){

        return service.pesquisar(id).map(produto -> {
            ProdutoDTO dto = mapper.toDTO(produto);
            return ResponseEntity.ok(dto);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar", description = "Atualiza por Id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<Void> atualizar(@PathVariable Long id, @RequestBody @Valid ProdutoDTO dto){
        Optional<Produto> produtoOptional = service.pesquisar(id);

        if (produtoOptional.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        var produto = produtoOptional.get();
        produto.setDescricao(dto.descricao());
        produto.setTipo(dto.tipo());
        produto.setQuantidade(dto.quantidade());

        service.atualizar(produto);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Deletar", description = "Deleta por Id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<Void> deletar(@PathVariable Long id){
        Optional<Produto> produtoOptional = service.pesquisar(id);

        if (produtoOptional.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

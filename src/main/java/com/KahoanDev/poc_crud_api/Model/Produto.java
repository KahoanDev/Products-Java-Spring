package com.KahoanDev.poc_crud_api.Model;

import com.KahoanDev.poc_crud_api.Model.enums.TipoProduto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200, nullable = false)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private TipoProduto tipo;

    private Long quantidade;

    public Produto(String descricao, TipoProduto tipo, Long quantidade) {
        this.descricao = descricao;
        this.tipo = tipo;
        this.quantidade = quantidade;
    }
}

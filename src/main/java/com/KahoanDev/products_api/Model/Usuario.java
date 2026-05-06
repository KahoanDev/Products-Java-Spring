package com.KahoanDev.products_api.Model;

import com.KahoanDev.products_api.Model.enums.UsuarioRoles;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String email;
    private String senha;
    @Enumerated(EnumType.STRING)
    private UsuarioRoles role;
}

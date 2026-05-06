package com.KahoanDev.products_api.Validator;

import com.KahoanDev.products_api.Exceptions.UsuarioJaCadastradoException;
import com.KahoanDev.products_api.Model.Usuario;
import com.KahoanDev.products_api.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UsuarioValidator {

    @Autowired
    private UsuarioRepository repository;

    public void validar(Usuario usuario){
        if (usuarioJaCadastrado(usuario)){
            throw new UsuarioJaCadastradoException("Usuário já cadastrado!");
        }
    }

    private boolean usuarioJaCadastrado(Usuario usuario) {
        Optional<Usuario> usuarioEncontrado = repository.findByEmail(usuario.getEmail());

        if (usuario.getId() == null){
            return usuarioEncontrado.isPresent();
        }

        return usuarioEncontrado.isPresent() && !usuario.getId().equals(usuarioEncontrado.get().getId());
    }
}

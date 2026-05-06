package com.KahoanDev.products_api.Service;

import com.KahoanDev.products_api.Model.Usuario;
import com.KahoanDev.products_api.Repository.UsuarioRepository;
import com.KahoanDev.products_api.Validator.UsuarioValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;
    private final UsuarioValidator validator;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        var authority = new SimpleGrantedAuthority(usuario.getRole().name());

        return new User(
                usuario.getEmail(),
                usuario.getSenha(),
                List.of(authority)
        );
    }

    public Usuario salvar(Usuario usuario) {
        validator.validar(usuario);
        var senha = usuario.getSenha();
        usuario.setSenha(encoder.encode(senha));
        return repository.save(usuario);
    }

    public Optional<Usuario> obterPorEmail(String email){
        return repository.findByEmail(email);
    }
}

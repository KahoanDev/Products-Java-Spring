package com.KahoanDev.products_api.Model.enums;

public enum UsuarioRoles {
    ADMIN,
    USER;

    public String authority(){
        return this.name();
    }
}

package com.klugesoftware.farmainvoice.model;

public class Cliente {
    private String nomeCliente;
    private String user; //partita iva
    private String email;
    private String asl;
    private String updated;

    public Cliente(){}

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAsl(){
        return asl;
    }

    public void setAsl(String asl){
        this.asl = asl;
    }

    public String getUpdated(){
        return updated;
    }

    public void setUpdated(String updated){
        this.updated = updated;
    }
}

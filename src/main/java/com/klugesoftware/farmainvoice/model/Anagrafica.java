package com.klugesoftware.farmainvoice.model;

public class Anagrafica {

    private Integer idAnagrafica;
    private String denominazione;
    private TipoAnagrafica tipoAnagrafica;
    private String partitaIva;
    private String codiceFiscale;
    private String indirizzo;
    private String cap;
    private String comune;
    private String nazione;

    public Anagrafica(){}

    public Anagrafica(
            Integer idAnagrafica,
            String denominazione,
            TipoAnagrafica tipoAnagrafica,
            String partitaIva,
            String codiceFiscale,
            String indirizzo,
            String cap,
            String comune,
            String nazione
    ){
        this.idAnagrafica = idAnagrafica;
        this.denominazione = denominazione;
        this.tipoAnagrafica = tipoAnagrafica;
        this.partitaIva = partitaIva;
        this.codiceFiscale = codiceFiscale;
        this.indirizzo = indirizzo;
        this.cap = cap;
        this.comune = comune;
        this.nazione = nazione;
    }

    public Integer getIdAnagrafica() {
        return idAnagrafica;
    }

    public void setIdAnagrafica(Integer idAnagrafica) {
        this.idAnagrafica = idAnagrafica;
    }

    public String getDenominazione() {
        return denominazione;
    }

    public void setDenominazione(String denominazione) {
        this.denominazione = denominazione;
    }

    public TipoAnagrafica getTipoAnagrafica() {
        return tipoAnagrafica;
    }

    public void setTipoAnagrafica(TipoAnagrafica tipoAnagrafica) {
        this.tipoAnagrafica = tipoAnagrafica;
    }

    public String getPartitaIva() {
        return partitaIva;
    }

    public void setPartitaIva(String partitaIva) {
        this.partitaIva = partitaIva;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public String getComune() {
        return comune;
    }

    public void setComune(String comune) {
        this.comune = comune;
    }

    public String getNazione() {
        return nazione;
    }

    public void setNazione(String nazione) {
        this.nazione = nazione;
    }
}

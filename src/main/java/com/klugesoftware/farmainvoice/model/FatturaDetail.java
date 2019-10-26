package com.klugesoftware.farmainvoice.model;

import java.math.BigDecimal;

public class FatturaDetail {

    private Integer idDetail;
    private Integer idFattura;
    private Integer numeroLinea;
    private String descrizione;
    private String codiceArticolo;
    private Integer quantita;
    private BigDecimal prezzoUnitario;
    private BigDecimal prezzoTotale;
    private String aliquotaIva;


    public FatturaDetail(){}

    public FatturaDetail(
            Integer idDetail,
            Integer idFattura,
            Integer numeroLinea,
            String descrizione,
            String codiceArticolo,
            Integer quantita,
            BigDecimal prezzoUnitario,
            BigDecimal prezzoTotale,
            String aliquotaIva
    ){
        this.idDetail = idDetail;
        this.idFattura = idFattura;
        this.numeroLinea = numeroLinea;
        this.descrizione = descrizione;
        this.codiceArticolo = codiceArticolo;
        this.quantita = quantita;
        this.prezzoUnitario = prezzoUnitario;
        this.prezzoTotale = prezzoTotale;
        this.aliquotaIva = aliquotaIva;
    }

    public Integer getIdDetail() {
        return idDetail;
    }

    public void setIdDetail(Integer idDetail) {
        this.idDetail = idDetail;
    }

    public Integer getIdFattura() {
        return idFattura;
    }

    public void setIdFattura(Integer idFattura) {
        this.idFattura = idFattura;
    }

    public Integer getNumeroLinea() {
        return numeroLinea;
    }

    public void setNumeroLinea(Integer numeroLinea) {
        this.numeroLinea = numeroLinea;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getCodiceArticolo() {
        return codiceArticolo;
    }

    public void setCodiceArticolo(String codiceArticolo) {
        this.codiceArticolo = codiceArticolo;
    }

    public Integer getQuantita() {
        return quantita;
    }

    public void setQuantita(Integer quantita) {
        this.quantita = quantita;
    }

    public BigDecimal getPrezzoUnitario() {
        return prezzoUnitario;
    }

    public void setPrezzoUnitario(BigDecimal prezzoUnitario) {
        this.prezzoUnitario = prezzoUnitario;
    }

    public BigDecimal getPrezzoTotale() {
        return prezzoTotale;
    }

    public void setPrezzoTotale(BigDecimal prezzoTotale) {
        this.prezzoTotale = prezzoTotale;
    }

    public String getAliquotaIva() {
        return aliquotaIva;
    }

    public void setAliquotaIva(String aliquotaIva) {
        this.aliquotaIva = aliquotaIva;
    }
}

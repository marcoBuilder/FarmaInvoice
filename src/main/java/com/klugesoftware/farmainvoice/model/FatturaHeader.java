package com.klugesoftware.farmainvoice.model;

import java.math.BigDecimal;
import java.util.Date;

public class FatturaHeader {

    private Integer idFattura;
    private Integer idAnagrafica;
    private TipoFattura tipoFattura;
    private String numeroFattura;
    private Date dataFattura;
    private BigDecimal importoTotale;
    private String causale;
    private String ibanPagamento;
    private String statoPagamento;
    private String tipoPagamento;
    private String notePagamento;
    private String nomeFile;

    public FatturaHeader(){}

    public FatturaHeader(
            Integer idFattura,
            Integer idAnagrafica,
            TipoFattura tipoFattura,
            String numeroFattura,
            Date dataFattura,
            BigDecimal importoTotale,
            String causale,
            String ibanPagamento,
            String statoPagamento,
            String tipoPagamento,
            String notePagamento,
            String nomeFile
    ){
        this.idFattura = idFattura;
        this.idAnagrafica = idAnagrafica;
        this.tipoFattura = tipoFattura;
        this.numeroFattura = numeroFattura;
        this.dataFattura = dataFattura;
        this.importoTotale = importoTotale;
        this.causale = causale;
        this.ibanPagamento = ibanPagamento;
        this.statoPagamento = statoPagamento;
        this.tipoPagamento = tipoPagamento;
        this.notePagamento = notePagamento;
        this.nomeFile = nomeFile;

    }

    public Integer getIdFattura() {
        return idFattura;
    }

    public void setIdFattura(Integer idFattura) {
        this.idFattura = idFattura;
    }

    public Integer getIdAnagrafica() {
        return idAnagrafica;
    }

    public void setIdAnagrafica(Integer idAnagrafica) {
        this.idAnagrafica = idAnagrafica;
    }

    public TipoFattura getTipoFattura() {
        return tipoFattura;
    }

    public void setTipoFattura(TipoFattura tipoFattura) {
        this.tipoFattura = tipoFattura;
    }

    public String getNumeroFattura() {
        return numeroFattura;
    }

    public void setNumeroFattura(String numeroFattura) {
        this.numeroFattura = numeroFattura;
    }

    public Date getDataFattura() {
        return dataFattura;
    }

    public void setDataFattura(Date dataFattura) {
        this.dataFattura = dataFattura;
    }

    public BigDecimal getImportoTotale() {
        return importoTotale;
    }

    public void setImportoTotale(BigDecimal importoTotale) {
        this.importoTotale = importoTotale;
    }

    public String getCausale() {
        return causale;
    }

    public void setCausale(String causale) {
        this.causale = causale;
    }

    public String getIbanPagamento() {
        return ibanPagamento;
    }

    public void setIbanPagamento(String ibanPagamento) {
        this.ibanPagamento = ibanPagamento;
    }

    public String getStatoPagamento() {
        return statoPagamento;
    }

    public void setStatoPagamento(String statoPagamento) {
        this.statoPagamento = statoPagamento;
    }

    public String getTipoPagamento() {
        return tipoPagamento;
    }

    public void setTipoPagamento(String tipoPagamento) {
        this.tipoPagamento = tipoPagamento;
    }

    public String getNotePagamento() {
        return notePagamento;
    }

    public void setNotePagamento(String notePagamento) {
        this.notePagamento = notePagamento;
    }

    public String getNomeFile() {
        return nomeFile;
    }

    public void setNomeFile(String nomeFile) {
        this.nomeFile = nomeFile;
    }
}

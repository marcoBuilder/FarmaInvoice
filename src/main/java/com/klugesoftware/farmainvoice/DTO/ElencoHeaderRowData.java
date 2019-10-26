package com.klugesoftware.farmainvoice.DTO;

import java.math.BigDecimal;

public class ElencoHeaderRowData {

    private String numeroFattura;
    private String dataFattura;
    private BigDecimal importo;
    private String denominazione;
    private String partitaIva;
    private String causale;
    private String nomeFile;

    public ElencoHeaderRowData(){}

    public ElencoHeaderRowData(
            String numeroFattura,
            String dataFattura,
            BigDecimal importo,
            String denominazione,
            String partitaIva,
            String causale,
            String nomeFile
    ){
        this.numeroFattura = numeroFattura;
        this.dataFattura = dataFattura;
        this.importo = importo;
        this.denominazione = denominazione;
        this.partitaIva = partitaIva;
        this.numeroFattura = numeroFattura;
        this.causale = causale;
        this.nomeFile = nomeFile;
    }

    public String getNumeroFattura() {
        return numeroFattura;
    }

    public void setNumeroFattura(String numeroFattura) {
        this.numeroFattura = numeroFattura;
    }

    public String getDataFattura() {
        return dataFattura;
    }

    public void setDataFattura(String dataFattura) {
        this.dataFattura = dataFattura;
    }

    public BigDecimal getImporto() {
        return importo;
    }

    public void setImporto(BigDecimal importo) {
        this.importo = importo;
    }

    public String getDenominazione() {
        return denominazione;
    }

    public void setDenominazione(String denominazione) {
        this.denominazione = denominazione;
    }

    public String getPartitaIva() {
        return partitaIva;
    }

    public void setPartitaIva(String partitaIva) {
        this.partitaIva = partitaIva;
    }

    public String getCausale() {
        return causale;
    }

    public void setCausale(String causale) {
        this.causale = causale;
    }

    public String getNomeFile() {
        return nomeFile;
    }

    public void setNomeFile(String nomeFile) {
        this.nomeFile = nomeFile;
    }
}

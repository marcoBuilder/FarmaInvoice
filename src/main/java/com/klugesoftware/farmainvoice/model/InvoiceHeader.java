package com.klugesoftware.farmainvoice.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Classe che rappresenta lo stato della testata del file xml visualizzati in table viewer
 * memorizzato nella Tabella InvoiceHeader
 */
public class InvoiceHeader {

    private Integer idInvoice;
    private TipoDocumento tipoDocumento;
    private TipoFattura tipoFattura;
    private String denominazione;
    private String partitaIva;
    private String codiceFiscale;
    private Date dataEmissione;
    private Date dataInvioRicezione;
    private BigDecimal importo;
    private Boolean visualizzato;
    private Boolean inviato;
    private String nomeFileXml;

    public InvoiceHeader(){
        this.inviato = false;
        this.visualizzato = false;
    }

    public InvoiceHeader(
            Integer idInvoice,
            TipoDocumento tipoDocumento,
            TipoFattura tipoFattura,
            String denominazione,
            String partitaIva,
            String codiceFiscale,
            Date dataEmissione,
            Date dataInvioRicezione,
            BigDecimal importo,
            Boolean visualizzato,
            Boolean inviato,
            String nomeFileXml
    ){
        this.idInvoice =  idInvoice;
        this.tipoDocumento = tipoDocumento;
        this.tipoFattura = tipoFattura;
        this.denominazione = denominazione;
        this.partitaIva = partitaIva;
        this.codiceFiscale = codiceFiscale;
        this.dataEmissione = dataEmissione;
        this.dataInvioRicezione = dataInvioRicezione;
        this.importo = importo;
        this.visualizzato = visualizzato;
        this.inviato = inviato;
        this.nomeFileXml = nomeFileXml;
    }

    public Integer getIdInvoice() {
        return idInvoice;
    }

    public void setIdInvoice(Integer idInvoice) {
        this.idInvoice = idInvoice;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
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

    public Date getDataEmissione() {
        return dataEmissione;
    }

    public void setDataEmissione(Date dataEmissione) {
        this.dataEmissione = dataEmissione;
    }

    public Date getDataInvioRicezione() {
        return dataInvioRicezione;
    }

    public void setDataInvioRicezione(Date dataInvioRicezione) {
        this.dataInvioRicezione = dataInvioRicezione;
    }

    public BigDecimal getImporto() {
        return importo;
    }

    public void setImporto(BigDecimal importo) {
        this.importo = importo;
    }

    public Boolean getVisualizzato() {
        return visualizzato;
    }

    public void setVisualizzato(Boolean visualizzato) {
        this.visualizzato = visualizzato;
    }

    public Boolean getInviato() {
        return inviato;
    }

    public void setInviato(Boolean inviato) {
        this.inviato = inviato;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public TipoFattura getTipoFattura() {
        return tipoFattura;
    }

    public void setTipoFattura(TipoFattura tipoFattura) {
        this.tipoFattura = tipoFattura;
    }

    public String getNomeFileXml() {
        return nomeFileXml;
    }

    public void setNomeFileXml(String nomeFileXml) {
        this.nomeFileXml = nomeFileXml;
    }
}

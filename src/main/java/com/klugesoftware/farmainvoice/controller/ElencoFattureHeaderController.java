package com.klugesoftware.farmainvoice.controller;

import com.klugesoftware.farmainvoice.DTO.ElencoHeaderRowData;
import com.klugesoftware.farmainvoice.DTO.MappingXmlToRowData;
import com.klugesoftware.farmainvoice.model.TipoFattura;
import com.klugesoftware.farmainvoice.report.ReportFattura;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class ElencoFattureHeaderController implements Initializable {

    private final Logger logger = LogManager.getLogger(ElencoFattureHeaderController.class.getName());
    @FXML private TableView<ElencoHeaderRowData> tableElencoHeader;
    @FXML private TableColumn<ElencoHeaderRowData,String> colNumero;
    @FXML private TableColumn<ElencoHeaderRowData,String> colData;
    @FXML private TableColumn<ElencoHeaderRowData, BigDecimal> colImporto;
    @FXML private TableColumn<ElencoHeaderRowData,String> colDenominazione;
    @FXML private TableColumn<ElencoHeaderRowData,String> colPartitaIva;
    @FXML private TableColumn<ElencoHeaderRowData,String> colTipoDoc;
    @FXML private TableColumn colCheckBox;
    @FXML private TableColumn colDettagliPdf;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        colNumero.setCellValueFactory(new PropertyValueFactory<ElencoHeaderRowData,String>("numeroFattura"));
        colData.setCellValueFactory(new PropertyValueFactory<ElencoHeaderRowData,String>("dataFattura"));
        colImporto.setCellValueFactory(new PropertyValueFactory<ElencoHeaderRowData,BigDecimal>("importo"));
        colDenominazione.setCellValueFactory(new PropertyValueFactory<ElencoHeaderRowData,String>("denominazione"));
        colPartitaIva.setCellValueFactory(new PropertyValueFactory<ElencoHeaderRowData,String>("partitaIva"));
        colTipoDoc.setCellValueFactory(new PropertyValueFactory<ElencoHeaderRowData,String>("causale"));

        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.ITALY);
        DecimalFormat df = (DecimalFormat)nf;

        colImporto.setCellFactory(new Callback<TableColumn<ElencoHeaderRowData, BigDecimal>, TableCell<ElencoHeaderRowData, BigDecimal>>() {
            @Override
            public TableCell<ElencoHeaderRowData, BigDecimal> call(TableColumn<ElencoHeaderRowData, BigDecimal> param) {
                return new TableCell<ElencoHeaderRowData, BigDecimal>(){
                  @Override
                  public void updateItem(BigDecimal item,boolean empty){
                      if(item == null || empty){
                          setText("");
                      }else{
                          setText(df.format(item));
                      }

                  }
                };
            }
        });

        colDettagliPdf.setCellFactory(ActionButtonTableCell.forTableColumn("",(ElencoHeaderRowData p) -> {
            tableElencoHeader.getSelectionModel().select(p);
            String nomeFileXml = p.getNomeFile();
            ReportFattura reportFattura = new ReportFattura();
            reportFattura.makeReport("./resources/examples/xml/"+nomeFileXml,"./resources/examples/pdf/", TipoFattura.PASSIVA);
            reportFattura.openFilePdf();
            return p;
        }));

        MappingXmlToRowData mapping = new MappingXmlToRowData();
        ObservableList<ElencoHeaderRowData> elencoRighe = FXCollections.observableArrayList(mapping.mappingHeaderFattureXml());
        tableElencoHeader.getItems().setAll(elencoRighe);
    }
}

package de.vanitasvitae.omemoqrgenerator;

import java.util.Collection;

import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

public class ListViewController {

    @FXML
    private JFXListView<OmemoIdentity> listView;

    private ObservableList<OmemoIdentity> identities = FXCollections.observableArrayList();

    private QrDisplayController displayController = null;

    public void setDisplayController(QrDisplayController controller) {
        this.displayController = controller;
    }

    public void setIdentities(Collection<OmemoIdentity> identities) {
        this.identities.clear();
        this.identities.addAll(identities);
    }

    @FXML
    void initialize() {
        listView.setCellFactory(listView -> {
            ListViewCell cell = new ListViewCell();
            cell.setDisplayController(displayController);
            return new ListViewCell();
        });
        listView.setItems(identities);
    }
}

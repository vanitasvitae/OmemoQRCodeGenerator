package de.vanitasvitae.omemoqrgenerator;

import java.io.IOException;

import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXToggleButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ListViewCell extends JFXListCell<OmemoIdentity> {

    @FXML
    private HBox hBox;

    @FXML
    private Label id;

    @FXML
    private Label fingerprint;

    @FXML
    private JFXToggleButton toggle;

    private QrDisplayController displayController = null;

    public ListViewCell() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/item.fxml"));
        fxmlLoader.setController(this);
        try
        {
            fxmlLoader.load();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void setDisplayController(QrDisplayController controller)  {
        this.displayController = controller;
    }

    @Override
    public void updateItem(OmemoIdentity identity, boolean empty) {
        if (empty) {
            return;
        }

        if (id != null) id.setText(Integer.toString(identity.getDevice().getDeviceId()));
        if (fingerprint != null) fingerprint.setText(identity.getFingerprint().blocksOf8Chars());
        if (toggle != null) toggle.setSelected(identity.getEnabled());
        if (hBox != null) setGraphic(hBox);
        if (toggle != null) {
            toggle.setOnAction(actionEvent -> {
                identity.setEnabled(toggle.isSelected());
                if (displayController != null) {
                    displayController.drawQRCode();
                }
            });
        }
    }
}

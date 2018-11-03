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

    @Override
    public void updateItem(OmemoIdentity identity, boolean empty) {
        Repository repository = Repository.getInstance();
        if (empty) {
            return;
        }

        id.setText(Integer.toString(identity.getDevice().getDeviceId()));
        fingerprint.setText(Util.twoLinesFingerprint(identity.getFingerprint()));
        toggle.setSelected(identity.getEnabled());
        setGraphic(hBox);

        toggle.setOnAction(actionEvent -> {
            identity.setEnabled(toggle.isSelected());
            repository.getIdentities().add(repository.getIdentities().remove(repository.getIdentities().size() -1));
        });
    }
}

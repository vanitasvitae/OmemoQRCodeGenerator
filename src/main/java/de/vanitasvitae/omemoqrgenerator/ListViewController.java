package de.vanitasvitae.omemoqrgenerator;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.ScrollEvent;

public class ListViewController {

    @FXML
    private ListView<OmemoIdentity> listView;


    @FXML
    void initialize() {
        Repository repository = Repository.getInstance();
        listView.setCellFactory(listView -> {
            ListViewCell cell = new ListViewCell();
            return cell;
        });
        listView.setItems(repository.getIdentities());
        listView.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.getDeltaX() != 0) {
                event.consume();
            }
        });
    }
}

package de.vanitasvitae.omemoqrgenerator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jxmpp.jid.BareJid;

public class Repository {

    private static Repository INSTANCE;

    private final ObservableList<OmemoIdentity> IDENTITIES = FXCollections.observableArrayList();
    private BareJid JID = null;

    private Repository() {

    }

    public static Repository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Repository();
        }
        return INSTANCE;
    }

    public BareJid getJid() {
        return JID;
    }

    public void setJid(BareJid jid) {
        this.JID = jid;
    }

    public ObservableList<OmemoIdentity> getIdentities() {
        return IDENTITIES;
    }
}

package de.vanitasvitae.omemoqrgenerator;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.omemo.internal.OmemoDevice;
import org.jivesoftware.smackx.omemo.trust.OmemoFingerprint;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jxmpp.jid.BareJid;

public class Main extends Application implements LoginCallback {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private Stage stage;

    /*
     * Trim down Smack.
     */
    static {
        SmackConfiguration.DEBUG = true;
        SmackConfiguration.addDisabledSmackClasses("org.jivesoftware.smack.ReconnectionManager",
                "org.jivesoftware.smack.sasl.javax",
                "org.jivesoftware.smack.legacy",
                "org.jivesoftware.smackx.gcm",
                "org.jivesoftware.smackx.httpfileupload",
                "org.jivesoftware.smackx.hoxt",
                "org.jivesoftware.smackx.iot",
                "org.jivesoftware.smackx.json",
                "org.jivesoftware.smackx.muc",
                "org.jivesoftware.smackx.xdata",
                "org.jivesoftware.smackx.xdatalayout",
                "org.jivesoftware.smackx.xdatavalidation",
                "org.jivesoftware.smackx.admin",
                "org.jivesoftware.smackx.amp",
                "org.jivesoftware.smackx.attention",
                "org.jivesoftware.smackx.blocking",
                "org.jivesoftware.smackx.bob",
                "org.jivesoftware.smackx.bookmark",
                "org.jivesoftware.smackx.bytestreams",
                "org.jivesoftware.smackx.chatstates",
                "org.jivesoftware.smackx.commands",
                "org.jivesoftware.smackx.filetransfer",
                "org.jivesoftware.smackx.forward",
                "org.jivesoftware.smackx.sid",
                "org.jivesoftware.smackx.eme",
                "org.jivesoftware.smackx.vcardtemp",
                "org.jivesoftware.smackx.xhtmlim",
                "org.jivesoftware.smackx.time",
                "org.jivesoftware.smackx.privacy",
                "org.jivesoftware.smackx.ping",
                "org.jivesoftware.smackx.iqlast",
                "org.jivesoftware.smackx.receipts",
                "org.jivesoftware.smackx.iqversion"
        );
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Render the login screen.
     * @param stage stage
     * @throws Exception in case something goes wrong.
     */
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();
        stage.setMinHeight(600);
        stage.setMaxHeight(600);
        stage.setMinWidth(400);
        stage.setMaxWidth(400);

        // Register ourselves as callback for the login button.
        LoginController loginController = loader.getController();
        loginController.setLoginCallback(this);

        Scene scene = new Scene(root, 400, 600);
        stage.setTitle("OMEMO QR-Code Generator - Login");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void login(String username, String password) {
        Repository repository = Repository.getInstance();
        XMPPTCPConnection connection;
        try {
            connection = new XMPPTCPConnection(username, password);
            connection.connect().login();

            repository.setJid(connection.getUser().asBareJid());
            Map<OmemoDevice, OmemoFingerprint> fingerprints = Util.getFingerprints(connection);

            ObservableList<OmemoIdentity> identities = repository.getIdentities();
            LOGGER.info("Received " + identities.size() + " OMEMO identities");
            for (OmemoDevice device : fingerprints.keySet()) {
                identities.addAll(new OmemoIdentity(device, fingerprints.get(device)));
                LOGGER.info(Util.twoLinesFingerprint(fingerprints.get(device)));
            }

            connection.disconnect(new Presence(Presence.Type.unavailable));

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/qrdisplay.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 400, 600);
            stage.setTitle("OMEMO QR-Code Generator");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Exception in login", e);
        }
    }
}

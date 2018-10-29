package de.vanitasvitae.omemoqrgenerator;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jivesoftware.smackx.omemo.internal.OmemoDevice;
import org.jivesoftware.smackx.omemo.trust.OmemoFingerprint;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import org.jxmpp.jid.BareJid;

public class QrDisplayController {

    @FXML
    private ImageView qr_view;

    @FXML
    private GridPane listView;

    @FXML
    private ListViewController listViewController;

    private BareJid jid;
    private ObservableList<OmemoIdentity> identities = null;

    public void setFingerprints(BareJid jid, ObservableList<OmemoIdentity> identities) {
        this.jid = jid;
        this.identities = identities;
        this.listViewController.setDisplayController(this);
        this.listViewController.setIdentities(this.identities);
        drawQRCode();
    }

    public void drawQRCode() {
        int width = 300, height = 300;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        String content = "xmpp:" + jid.toString();

        Iterator<OmemoIdentity> iterator = identities.iterator();

        while (iterator.hasNext()) {
            OmemoIdentity first = iterator.next();
            if (first.getEnabled()) {
                content += "?omemo-sid-" + first.getDevice().getDeviceId() + "=" + first.getFingerprint().toString();
                break;
            }
        }

        while (iterator.hasNext()) {
            OmemoIdentity next = iterator.next();
            if (next.getEnabled()) {
                content += ";omemo-sid-" + next.getDevice().getDeviceId() + "=" + next.getFingerprint().toString();
            }
        }

        BufferedImage image;

        try {
            BitMatrix byteMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            image.createGraphics();

            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, height);
            graphics.setColor(Color.BLACK);

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }

            System.out.println("Success...");

        } catch (WriterException ex) {
            ex.printStackTrace();
            return;
        }

        qr_view.setImage(SwingFXUtils.toFXImage(image, null));
    }

}

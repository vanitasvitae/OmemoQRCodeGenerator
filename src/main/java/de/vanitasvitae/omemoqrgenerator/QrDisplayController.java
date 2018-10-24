package de.vanitasvitae.omemoqrgenerator;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Map;

import org.jivesoftware.smackx.omemo.internal.OmemoDevice;
import org.jivesoftware.smackx.omemo.trust.OmemoFingerprint;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.jfoenix.controls.JFXTextArea;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import org.jxmpp.jid.BareJid;

public class QrDisplayController {

    @FXML
    private ImageView qr_view;

    @FXML
    private JFXTextArea content_text;

    private BareJid jid;
    private Map<OmemoDevice, OmemoFingerprint> fingerprintMap;

    public void setFingerprints(BareJid jid, Map<OmemoDevice, OmemoFingerprint> fingerprints) {
        this.jid = jid;
        this.fingerprintMap = fingerprints;
        drawQRCode(jid, fingerprints);
    }

    public void drawQRCode(BareJid jid, Map<OmemoDevice, OmemoFingerprint> fingerprints) {
        int width = 300, height = 300;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        String content = "xmpp:" + jid.toString();

        Iterator<OmemoDevice> iterator = fingerprints.keySet().iterator();
        if (iterator.hasNext()) {
            OmemoDevice first = iterator.next();
            content += "?omemo-sid-" + first.getDeviceId() + "=" + fingerprints.get(first);
        }

        while (iterator.hasNext()) {
            OmemoDevice next = iterator.next();
            content += ";omemo-sid-" + next.getDeviceId() + "=" + fingerprints.get(next);
        }

        content_text.setText(content);

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

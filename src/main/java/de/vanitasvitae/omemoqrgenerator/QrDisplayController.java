package de.vanitasvitae.omemoqrgenerator;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Iterator;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.jfoenix.controls.JFXListView;
import javafx.collections.ListChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class QrDisplayController {

    @FXML
    private ImageView qr_view;

    @FXML
    private ListView listView;

    @FXML
    private ListViewController listViewController;

    private Repository repository = Repository.getInstance();

    @FXML
    public void initialize() {
        drawQRCode();
        repository.getIdentities().addListener((ListChangeListener<OmemoIdentity>) change -> drawQRCode());
    }

    public void drawQRCode() {
        System.out.println("Draw!");
        Repository repository = Repository.getInstance();

        int width = 360, height = 360;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        String content = "xmpp:" + repository.getJid().toString();

        Iterator<OmemoIdentity> iterator = repository.getIdentities().iterator();

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

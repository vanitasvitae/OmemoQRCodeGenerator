package de.vanitasvitae.omemoqrgenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.omemo.element.OmemoBundleElement;
import org.jivesoftware.smackx.omemo.element.OmemoDeviceListElement;
import org.jivesoftware.smackx.omemo.exceptions.CorruptedOmemoKeyException;
import org.jivesoftware.smackx.omemo.internal.OmemoDevice;
import org.jivesoftware.smackx.omemo.signal.SignalOmemoKeyUtil;
import org.jivesoftware.smackx.omemo.trust.OmemoFingerprint;
import org.jivesoftware.smackx.omemo.util.OmemoConstants;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubException;
import org.jivesoftware.smackx.pubsub.PubSubManager;

import org.jxmpp.jid.BareJid;

public class Util {

    private static final SignalOmemoKeyUtil KEYUTIL = new SignalOmemoKeyUtil();

    public static Map<OmemoDevice, OmemoFingerprint> getFingerprints(XMPPTCPConnection connection)
            throws InterruptedException, PubSubException.NotALeafNodeException,
            SmackException.NoResponseException, SmackException.NotConnectedException,
            XMPPException.XMPPErrorException, PubSubException.NotAPubSubNodeException,
            CorruptedOmemoKeyException {
        BareJid jid = connection.getUser().asBareJid();
        Map<OmemoDevice, OmemoFingerprint> fingerprintMap = new HashMap<>();

        OmemoDeviceListElement deviceList = fetchDeviceList(connection, jid);
        if (deviceList == null) {
            return fingerprintMap;
        }

        for (int id : deviceList.getDeviceIds()) {
            OmemoDevice device = new OmemoDevice(jid, id);
            OmemoBundleElement bundle = fetchBundle(connection, device);
            if (bundle == null) {
                continue;
            }

            OmemoFingerprint fingerprint = KEYUTIL.getFingerprintOfIdentityKey(KEYUTIL.BUNDLE.identityKey(bundle));
            fingerprintMap.put(device, fingerprint);
        }

        return fingerprintMap;
    }

    public static OmemoBundleElement fetchBundle(XMPPConnection connection,
                                                 OmemoDevice contactsDevice)
            throws SmackException.NotConnectedException, InterruptedException, SmackException.NoResponseException,
            XMPPException.XMPPErrorException, PubSubException.NotALeafNodeException,
            PubSubException.NotAPubSubNodeException {

        PubSubManager pm = PubSubManager.getInstance(connection, contactsDevice.getJid());
        LeafNode node = pm.getLeafNode(contactsDevice.getBundleNodeName());

        if (node == null) {
            return null;
        }

        List<PayloadItem<OmemoBundleElement>> bundleItems = node.getItems();
        if (bundleItems.isEmpty()) {
            return null;
        }

        return bundleItems.get(bundleItems.size() - 1).getPayload();
    }

    public static OmemoDeviceListElement fetchDeviceList(XMPPConnection connection, BareJid contact)
            throws InterruptedException, PubSubException.NotALeafNodeException, SmackException.NoResponseException,
            SmackException.NotConnectedException, XMPPException.XMPPErrorException,
            PubSubException.NotAPubSubNodeException {

        PubSubManager pm = PubSubManager.getInstance(connection, contact);
        String nodeName = OmemoConstants.PEP_NODE_DEVICE_LIST;
        LeafNode node = pm.getLeafNode(nodeName);

        if (node == null) {
            return null;
        }

        List<PayloadItem<OmemoDeviceListElement>> items = node.getItems();
        if (items.isEmpty()) {
            return null;
        }

        return items.get(items.size() - 1).getPayload();
    }
}

package de.vanitasvitae.omemoqrgenerator;

import org.jivesoftware.smackx.omemo.internal.OmemoDevice;
import org.jivesoftware.smackx.omemo.trust.OmemoFingerprint;

public class OmemoIdentity {

    private final OmemoDevice device;
    private final OmemoFingerprint fingerprint;
    private boolean enabled;

    public OmemoIdentity(OmemoDevice device, OmemoFingerprint fingerprint) {
        this.device = device;
        this.fingerprint = fingerprint;
        this.enabled = true;
    }

    public OmemoDevice getDevice() {
        return device;
    }

    public OmemoFingerprint getFingerprint() {
        return fingerprint;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

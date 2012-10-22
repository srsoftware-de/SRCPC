
package de.dermoba.srcp.devices.listener;

public interface LOCKInfoListener {
    public void LOCKset(double timestamp, int bus, int address,
        String deviceGroup, int duration, int sessionID);

    public void LOCKterm(double timestamp, int bus, int address,
        String deviceGroup);
}

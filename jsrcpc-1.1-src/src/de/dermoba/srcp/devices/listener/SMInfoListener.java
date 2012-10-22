package de.dermoba.srcp.devices.listener;

/**
 * Interface for listeners of SM messages.
 * 
 * @author schenk
 */
public interface SMInfoListener {
    void SMset(double timestamp, int bus, int address, String type,
            String[] values);

    void SMinit(double timestamp, int bus, String protocol);

    void SMterm(double timestamp, int bus);
}

package de.dermoba.srcp.devices.listener;

public interface GAInfoListener {
	public void GAset(double timestamp, int bus, int address, int port, int value);

	public void GAinit(double timestamp, int bus, int address, String protocol,
			String[] params);

	public void GAterm(double timestamp, int bus, int address);
}

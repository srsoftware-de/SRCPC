package de.dermoba.srcp.devices.listener;

import de.dermoba.srcp.model.locomotives.SRCPLocomotiveDirection;

public interface GLInfoListener {
	public void GLset(double timestamp, int bus, int address, SRCPLocomotiveDirection drivemode,
			int v, int vMax, boolean[] functions);

	public void GLinit(double timestamp, int bus, int address, String protocol,
			String[] params);

	public void GLterm(double timestamp, int bus, int address);
}

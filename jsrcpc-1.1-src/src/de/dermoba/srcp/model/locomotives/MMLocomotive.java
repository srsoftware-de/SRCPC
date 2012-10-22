package de.dermoba.srcp.model.locomotives;

public class MMLocomotive extends SRCPLocomotive {

	public final static int		MAX_MM_LOCOMOTIVE_ADDRESS	= 99;
	public static final String	PROTOCOL_VERSION	= "2";

	public static final String	PROTOCOL			= "M";


	public MMLocomotive() {
		this(0, 0);
	}

	public MMLocomotive(int bus, int address) {
		super(bus, address);
		params = new String[3];
		params[0] = PROTOCOL_VERSION;

		protocol = PROTOCOL;
	}

	@Override
	public boolean checkAddress() {
		return !(address < 0 || address > MMLocomotive.MAX_MM_LOCOMOTIVE_ADDRESS);
	}
}

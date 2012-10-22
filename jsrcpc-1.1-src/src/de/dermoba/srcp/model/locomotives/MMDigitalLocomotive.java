package de.dermoba.srcp.model.locomotives;

public class MMDigitalLocomotive extends MMLocomotive {

	public final static int	DRIVING_STEPS	= 28;
	public final static int	FUNCTION_COUNT	= 5;

	public MMDigitalLocomotive() {
		this(0,0);
	}
	
	public MMDigitalLocomotive(int bus, int address) {
		super(bus, address);
		params[1] = "" + DRIVING_STEPS;
		params[2] = "" + FUNCTION_COUNT;
		functions = new boolean[FUNCTION_COUNT];
		drivingSteps = DRIVING_STEPS;
	}


}

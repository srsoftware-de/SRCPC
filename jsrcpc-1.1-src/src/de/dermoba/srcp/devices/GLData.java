package de.dermoba.srcp.devices;

import java.util.ArrayList;

import de.dermoba.srcp.common.exception.SRCPNoDataException;
import de.dermoba.srcp.model.locomotives.SRCPLocomotiveDirection;

public class GLData {
    public final double timestamp;
    public final int bus;
    public final int address;
    public final SRCPLocomotiveDirection drivemode;
    public final int v;
    public final int vMax;
    public final boolean[] f;

    public GLData(String data) throws SRCPNoDataException {
        String[] tokens = data.split(" ");

        if (tokens.length < 9) {
            throw new SRCPNoDataException();
        }

        timestamp = Double.parseDouble(tokens[0]);
        bus = Integer.parseInt(tokens[3]);
        address = Integer.parseInt(tokens[5]);
        drivemode = SRCPLocomotiveDirection.valueOf(Integer.parseInt(tokens[6]));
        v = Integer.parseInt(tokens[7]);
        vMax = Integer.parseInt(tokens[8]);

        ArrayList<Boolean> functions = new ArrayList<Boolean>();

        for (int index = 9; index < tokens.length; index++) {
            final int function = Integer.parseInt(tokens[index]);

            functions.add(function == 1);
        }
        f = new boolean[functions.size()];
        for (int index = 0; index < f.length; index++) {
            f[index] = ((Boolean) functions.get(index)).booleanValue();
        }
    }
}

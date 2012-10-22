package de.dermoba.srcp.model.locomotives;

public enum SRCPLocomotiveDirection {
    FORWARD(1), REVERSE(0), UNDEF(-1);

    private final int direction;

    SRCPLocomotiveDirection(int direction) {
        this.direction = direction;
    }

    public int getDirection() {
        return direction;
    }

    public static SRCPLocomotiveDirection valueOf(int direction) {
        SRCPLocomotiveDirection result = null;

        for (SRCPLocomotiveDirection e : SRCPLocomotiveDirection.values()) {
            if (e.direction == direction) {
                result = e;
                break;
            }
        }
        if (result == null) {
            result = UNDEF;
        }
        return result;
    }
}

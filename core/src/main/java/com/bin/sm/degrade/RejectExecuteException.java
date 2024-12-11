package com.bin.sm.degrade;

import com.bin.sm.common.FluxType;

public class RejectExecuteException extends RuntimeException{

    private FluxType fluxType;

    public RejectExecuteException(String message, FluxType fluxType) {
        super(message);
        this.fluxType = fluxType;
    }

    public FluxType getFluxType() {
        return fluxType;
    }

    public RejectExecuteException setFluxType(FluxType fluxType) {
        this.fluxType = fluxType;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UnExcutableException{");
        sb.append("fluxType=").append(fluxType);
        sb.append('}');
        return sb.toString();
    }
}

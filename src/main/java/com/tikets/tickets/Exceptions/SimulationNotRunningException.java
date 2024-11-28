package com.tikets.tickets.Exceptions;

public class SimulationNotRunningException extends RuntimeException {
    public SimulationNotRunningException() {
        super("Simulation is not running.");
    }
}

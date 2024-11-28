package com.tikets.tickets.Exceptions;

public class SimulationAlreadyRunningException extends RuntimeException {
    public SimulationAlreadyRunningException() {
        super("Simulation is already running.");
    }
    
}

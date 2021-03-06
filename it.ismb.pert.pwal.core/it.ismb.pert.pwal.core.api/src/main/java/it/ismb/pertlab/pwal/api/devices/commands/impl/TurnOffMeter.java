package it.ismb.pertlab.pwal.api.devices.commands.impl;

import it.ismb.pertlab.pwal.api.devices.commands.internal.AbstractCommand;
import it.ismb.pertlab.pwal.api.devices.model.Meter;

import java.util.HashMap;

public class TurnOffMeter extends AbstractCommand {

	Meter device;
	 
	public TurnOffMeter(Meter device) {
		this.device = device;
		this.setCommandName("turnOff");
	}
	@Override
	public void execute() {
		this.device.turnOff();
	}

	@Override
	public void setParams(HashMap<String, Object> params)
			throws IllegalArgumentException {
	}
}

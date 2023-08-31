package com.krmi.urcap.sample.modbusCommunicator.signal;

import javax.swing.Box;
import javax.swing.JLabel;

import com.krmi.urcap.sample.modbusCommunicator.impl.Modbus4jUtils;

public class BooleanSignal extends Signal {

    private String value;
    private final JLabel valueLabel;
    private final Modbus4jUtils modbusClient;
    private final int slaveId;
    private final int registerNum;
    private final String ipAddress;

    public BooleanSignal(String name, int slaveId, int registerNum, String ipAddress) {
        super(name);
        this.valueLabel = new JLabel();
        this.modbusClient = new Modbus4jUtils();
        this.slaveId = slaveId;
        this.registerNum = registerNum;
        this.ipAddress = ipAddress;

        getSignalBox().add(Box.createHorizontalStrut(10)); 
        getSignalBox().add(valueLabel);
    }

    @Override
    public void updateSignal() {
        Boolean value;
        try {
            value = modbusClient.readCoilStatus(slaveId, registerNum, ipAddress);
        } catch (Exception e) {
            value = null;
        }
        updateValue(value);
    }

    public void updateValue(Boolean value) {
        if (value == null) {
            this.status = SignalStatus.DISCONNECTED;
            this.value = "N/A";
        } else {
            this.status = SignalStatus.CONNECTED;
            this.value = value.toString();
        }
    }

    @Override
    public void refreshUI() {
        statusLabel.setIcon(getIcon(status == SignalStatus.CONNECTED ? "connected" : "disconnected"));
        valueLabel.setText(value);
    }
}

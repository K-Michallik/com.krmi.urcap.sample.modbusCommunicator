package com.krmi.urcap.sample.modbusCommunicator.signal;

import javax.swing.Box;
import javax.swing.JLabel;

import com.krmi.urcap.sample.modbusCommunicator.impl.Modbus4jUtils;
import com.krmi.urcap.sample.modbusCommunicator.impl.ModbusCommInstallationNodeContribution;

public class BooleanSignal extends Signal {

    private String value;
    private final JLabel valueLabel;
    private final Modbus4jUtils modbusClient;
    private final int slaveId;
    private final int registerNum;
    private final ModbusCommInstallationNodeContribution contribution;

    public BooleanSignal(String name, int slaveId, int registerNum, ModbusCommInstallationNodeContribution contribution) {
        super(name);
        this.valueLabel = new JLabel();
        this.modbusClient = new Modbus4jUtils();
        this.slaveId = slaveId;
        this.registerNum = registerNum;
        this.contribution = contribution;

        getSignalBox().add(Box.createHorizontalStrut(10)); 
        getSignalBox().add(valueLabel);
    }

    @Override
    public void updateSignal() {
        Boolean value;
        try {
            value = modbusClient.readCoilStatus(slaveId, registerNum, contribution.getIpAddress());
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

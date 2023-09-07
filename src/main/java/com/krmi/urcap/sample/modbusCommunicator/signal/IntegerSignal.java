package com.krmi.urcap.sample.modbusCommunicator.signal;

import javax.swing.Box;
import javax.swing.JLabel;

import com.krmi.urcap.sample.modbusCommunicator.impl.Modbus4jUtils;
import com.krmi.urcap.sample.modbusCommunicator.impl.ModbusCommInstallationNodeContribution;
import com.serotonin.modbus4j.code.DataType;

public class IntegerSignal extends Signal {

    private Integer value;
    private final JLabel valueLabel;
    private final Modbus4jUtils modbusClient;
    private final int slaveId;
    private final int registerNum;
    private final ModbusCommInstallationNodeContribution contribution;

    public IntegerSignal(String name, int slaveId, int registerNum, ModbusCommInstallationNodeContribution contribution) {
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
        Integer value;
        try {
            value = modbusClient.readInputRegisters(slaveId, registerNum, DataType.TWO_BYTE_INT_SIGNED, contribution.getIpAddress()).intValue();
        } catch (Exception e) {
            value = null;
        }
        updateValue(value);
    }

    public void updateValue(Integer value) {
        if (value == null) {
            this.status = SignalStatus.DISCONNECTED;
            this.value = null;
        } else {
            this.status = SignalStatus.CONNECTED;
            this.value = value;
        }
    }

    @Override
    public void refreshUI() {
        statusLabel.setIcon(getIcon(status == SignalStatus.CONNECTED ? "connected" : "disconnected"));
        valueLabel.setText(value != null ? value.toString() : "N/A");
    }
}
package com.krmi.urcap.sample.modbusCommunicator.toolbar;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.krmi.urcap.sample.modbusCommunicator.impl.ModbusCommInstallationNodeContribution;
import com.krmi.urcap.sample.modbusCommunicator.signal.BooleanSignal;
import com.krmi.urcap.sample.modbusCommunicator.signal.IntegerSignal;
import com.ur.urcap.api.contribution.toolbar.ToolbarAPIProvider;
import com.ur.urcap.api.contribution.toolbar.ToolbarContext;
import com.ur.urcap.api.contribution.toolbar.swing.SwingToolbarContribution;

public class ModbusCommToolbarContribution implements SwingToolbarContribution {

    private final ToolbarAPIProvider apiProvider;
    private final ModbusCommInstallationNodeContribution contribution;
    private Timer uiTimer;

    private BooleanSignal di1Signal;
    private BooleanSignal di2Signal;
    private IntegerSignal holdReg130Signal;
    private IntegerSignal holdReg131Signal;

    ModbusCommToolbarContribution(ToolbarContext context) {
        apiProvider = context.getAPIProvider();
        this.contribution = apiProvider.getApplicationAPI().getInstallationNode(ModbusCommInstallationNodeContribution.class);

        di1Signal = new BooleanSignal("Digital Input 1", 255, 1, contribution.getIpAddress());
        di2Signal = new BooleanSignal("Digital Input 2", 255, 2, contribution.getIpAddress());
        holdReg130Signal = new IntegerSignal("Register Input 130", 255, 130, contribution.getIpAddress());
        holdReg131Signal = new IntegerSignal("Register Input 131", 255, 131, contribution.getIpAddress());
    }

    @Override
    public void buildUI(JPanel panel) {
        panel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Modbus Communicator");
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 24));
        titleLabel.setVerticalAlignment(SwingConstants.TOP);
        titleLabel.setAlignmentX(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        //Create a panel for the signals to be held.
        JPanel signalsPanel = new JPanel(new GridLayout(0, 1));
        signalsPanel.setBackground(new Color(170, 207, 233));

        signalsPanel.add(di1Signal.getSignalBox());
        signalsPanel.add(di2Signal.getSignalBox());
        signalsPanel.add(holdReg130Signal.getSignalBox());
        signalsPanel.add(holdReg131Signal.getSignalBox());

        panel.add(signalsPanel, BorderLayout.CENTER);
    }

    @Override
    public void openView() {
        uiTimer = new Timer(true);
        uiTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (contribution.isReachable()) {
                    updateAllSignals();
                } else {
                    di1Signal.updateValue(null);
                    di2Signal.updateValue(null);
                    holdReg130Signal.updateValue(null);
                    holdReg131Signal.updateValue(null);
                }

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        refreshAllUI();
                    }
                });
            }
        }, 0, 200);
    }

    @Override
    public void closeView() {
        if (uiTimer != null) {
            uiTimer.cancel();
        }
    }

    private void updateAllSignals() {
        di1Signal.updateSignal();
        di2Signal.updateSignal();
        holdReg130Signal.updateSignal();
        holdReg131Signal.updateSignal();
    }

    private void refreshAllUI() {
        di1Signal.refreshUI();
        di2Signal.refreshUI();
        holdReg130Signal.refreshUI();
        holdReg131Signal.refreshUI();
    }
}

package com.krmi.urcap.sample.modbusCommunicator.toolbar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.krmi.urcap.sample.modbusCommunicator.impl.Modbus4jUtils;
import com.krmi.urcap.sample.modbusCommunicator.impl.ModbusCommInstallationNodeContribution;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.ur.urcap.api.contribution.toolbar.ToolbarAPIProvider;
import com.ur.urcap.api.contribution.toolbar.ToolbarContext;
import com.ur.urcap.api.contribution.toolbar.swing.SwingToolbarContribution;

public class ModbusCommToolbarContribution implements SwingToolbarContribution{

    private static final int HEADER_FONT_SIZE = 24;
    private static final int VERTICAL_SPACE = 10;

    private final ToolbarAPIProvider apiProvider;
    private final ModbusCommInstallationNodeContribution contribution;
    private Modbus4jUtils modbusClient = new Modbus4jUtils();
    private static final int dType = DataType.TWO_BYTE_INT_SIGNED;
    private static int slaveId = 255;

    private Timer uiTimer;
    private Boolean pauseTimer = false;

    //Labels and status for the Toolbar GUI.
    private JLabel di1Label = new JLabel("Digital Input 1");
    private int di1Status = 0;
    private JLabel di2Label = new JLabel("Digital Input 2");
    private int di2Status = 0;
    private JLabel do1Label = new JLabel("Digital Output 1");
    private int do1Status = 0;
    private JLabel do2Label = new JLabel("Digital Output 2");
    private int do2Status = 0;
    private JLabel holdReg130Label = new JLabel("Register Input 130:");
    private int holdReg130Status = 0;
    private JLabel holdReg130ValLabel = new JLabel("Value");
    private int holdReg130Val = 0;
    private JLabel holdReg131Label = new JLabel("Register Input 131:");
    private int holdReg131Status = 0;
    private JLabel holdReg131ValLabel = new JLabel("Value");
    private int holdReg131Val = 0;


    enum readMode {
        COIL,
        DISCRETEINPUT,
        INPUTREGISTER,
        HOLDINGREGISTER,
    }

    
    ModbusCommToolbarContribution(ToolbarContext context) {
        apiProvider = context.getAPIProvider();
        this.contribution = apiProvider.getApplicationAPI().getInstallationNode(ModbusCommInstallationNodeContribution.class);
    }

    @Override
    public void buildUI(JPanel panel) {
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        System.out.println("IP address is: "+ contribution.getIpAddress());

        JLabel lblNewLabel = new JLabel("Modbus Communicator");
		lblNewLabel.setFont(new Font("Roboto", Font.BOLD, 20));
		lblNewLabel.setVerticalAlignment(SwingConstants.TOP);
		lblNewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblNewLabel.setBackground(new Color(240, 240, 240));
		panel.add(lblNewLabel);

		panel.add(createVerticalSpace());
		
		JPanel ioPanel = new JPanel();
		panel.add(ioPanel);
		ioPanel.setLayout(new GridLayout(1, 2, 0, 0));
		
		JPanel inputPanel = new JPanel();
        inputPanel.setBackground(new Color(170, 207, 233));
		ioPanel.add(inputPanel);
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		
		JLabel diHeader = new JLabel("Inputs");
		diHeader.setFont(new Font("Roboto", Font.BOLD, 14));
		inputPanel.add(diHeader);
		
		inputPanel.add(createVerticalSpace());
		
		//DI1
		di1Label.setHorizontalAlignment(SwingConstants.LEFT);
		di1Label.setIcon(getIcon("warning"));
		di1Label.setFont(new Font("Roboto", Font.PLAIN, 12));
		inputPanel.add(di1Label);
		
		inputPanel.add(createVerticalSpace());
		
		//DI2
		di2Label.setHorizontalAlignment(SwingConstants.LEFT);
		di2Label.setFont(new Font("Roboto", Font.PLAIN, 12));
		di2Label.setIcon(getIcon("warning"));
		inputPanel.add(di2Label);
		
		inputPanel.add(createVerticalSpace());
		
        //Box for Register 130 value
		Box holdReg130Box = Box.createHorizontalBox();
		holdReg130Box.setAlignmentX(Component.LEFT_ALIGNMENT);
		inputPanel.add(holdReg130Box);
		
        //Input Register 130 label
		holdReg130Label.setFont(new Font("Roboto", Font.PLAIN, 12));
		holdReg130Box.add(holdReg130Label);
		
		Component holdRegStrut = Box.createHorizontalStrut(20);
		holdReg130Box.add(holdRegStrut);
		
        //Input Register 130 value
		holdReg130ValLabel.setFont(new Font("Roboto", Font.PLAIN, 12));
		holdReg130Box.add(holdReg130ValLabel);
		
		inputPanel.add(createVerticalSpace());
		
        //Box for Register 131 value
		Box holdReg131Box = Box.createHorizontalBox();
		holdReg131Box.setAlignmentX(Component.LEFT_ALIGNMENT);
		inputPanel.add(holdReg131Box);
		
        //Input Register 131 label
		holdReg131Label.setFont(new Font("Roboto", Font.PLAIN, 12));
		holdReg131Box.add(holdReg131Label);
		
		holdReg131Box.add(holdRegStrut);
		
        //Input Register 131 value
		holdReg131ValLabel.setFont(new Font("Roboto", Font.PLAIN, 12));
		holdReg131Box.add(holdReg131ValLabel);
		
		inputPanel.add(createVerticalSpace());
		
		Component verticalGlue = Box.createVerticalGlue();
		inputPanel.add(verticalGlue);
    }


    @Override
    public void openView() {
        //UI updates from non-GUI threads must use EventQueue.invokeLater (or SwingUtilities.invokeLater)
		uiTimer = new Timer(true);
		uiTimer.schedule(new TimerTask() {
			@Override
			public void run() {
                if (contribution.isReachable()) {
                    updateAllSignals();
                }
                else {
                    di1Status = 0;
                    di2Status = 0;
                    holdReg130Status = 0;
                    holdReg131Status = 0;
                }
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						updateUI();
					}
				});
			}
		}, 0, 1000);
        
    }

    @Override
    public void closeView() {
        if (uiTimer != null) {
			uiTimer.cancel();
		}
    }

    private void updateAllSignals() {
        int[] tmpResult = updateSignal(readMode.COIL, 1);
        di1Status = tmpResult[0];
        tmpResult = updateSignal(readMode.COIL, 2);
        di2Status = tmpResult[0];
        tmpResult = updateSignal(readMode.INPUTREGISTER, 130);
        holdReg130Status = tmpResult[0];
        holdReg130Val = tmpResult[1];
        tmpResult = updateSignal(readMode.INPUTREGISTER, 131);
        holdReg131Status = tmpResult[0];
        holdReg131Val = tmpResult[1];
    }

    private int[] updateSignal(readMode mode, int registerNum){
        // Read and update the coil response
        if (readMode.COIL == mode) {
            try {
                Boolean coilResponse = modbusClient.readCoilStatus(slaveId, registerNum, contribution.getIpAddress());
                if (coilResponse) {
                    return new int[] {1,0};
                }
                else {
                    return new int[] {2,0};
                }
            }
            catch (Exception e) {
                System.out.println("Signal error");
                // e.printStackTrace();
                return new int[] {0,0};
            }
        }

        if (readMode.DISCRETEINPUT == mode) {
            try {
                Boolean discInputResponse = modbusClient.readHoldingRegisterBit(slaveId, registerNum, dType, contribution.getIpAddress());
                if (discInputResponse) {
                    return new int[] {1,0};
                }
                else {
                    return new int[] {2,0};
                }
            }
            catch (Exception e) {
                System.out.println("Signal error");
                // e.printStackTrace();
                return new int[] {0,0};
            }
        }

        if (readMode.INPUTREGISTER == mode) {
            try {
                Number inputRegResponse = modbusClient.readInputRegisters(slaveId, registerNum, dType, contribution.getIpAddress());
                return new int[] {1,inputRegResponse.intValue()};
                
            }
            catch (Exception e) {
                System.out.println("Signal error");
                // e.printStackTrace();
                return new int[] {0,0};
            }
        }

        if (readMode.HOLDINGREGISTER == mode) {
            try {
                Number holdRegResponse = modbusClient.readHoldingRegister(slaveId, registerNum, dType, contribution.getIpAddress());
                return new int[] {1,holdRegResponse.intValue()};
            }
            catch (Exception e) {
                System.out.println("Signal error");
                // e.printStackTrace();
                return new int[] {0,0};
            }
        }
        else{
            System.err.println("Invalid readmode selected!");
            return new int[] {0,0};
        }
    }

    private void updateUI() {
        updateSignalIcon(di1Label, di1Status);
        updateSignalIcon(di2Label, di2Status);
        updateSignalIcon(holdReg130Label, holdReg130Status);
        holdReg130ValLabel.setText(Integer.toString(holdReg130Val));
        updateSignalIcon(holdReg131Label, holdReg131Status);
        holdReg131ValLabel.setText(Integer.toString(holdReg131Val));
    }

    private Box createHeader() {
		Box headerBox = Box.createHorizontalBox();
		headerBox.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel header = new JLabel("Modbus Communicator");
		header.setFont(header.getFont().deriveFont(Font.BOLD, HEADER_FONT_SIZE));
		headerBox.add(header);
		return headerBox;
	}

    private Component createVerticalSpace() {
		return Box.createRigidArea(new Dimension(0, VERTICAL_SPACE));
	}

    /**
     * Reads in the specified icon file from the icons folder and rescales it to
     * 25x25px
     * 
     * @param icon - String name of icon file
     * @return icon in ImageIcon class format
     */
    public Icon getIcon(String icon) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResource("/icons/" + icon + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ImageIcon(image.getScaledInstance(25, -1, Image.SCALE_SMOOTH));
    }

    public void updateSignalIcon (JLabel label, int signalStatus){
        if (signalStatus == 0) {
            label.setIcon(getIcon("warning"));
        } else if (signalStatus == 1) {
            label.setIcon(getIcon("connected"));
        } else if (signalStatus == 2) {
            label.setIcon(getIcon("disconnected"));
        } else {
            label.setIcon(getIcon("warning"));
            System.out.println("Here be dragons. Signal status is: " + signalStatus);
        }
    }

    public void updateRegisterValue(JLabel label, int value){
        label.setText(Integer.toString(value));
    }
    
}

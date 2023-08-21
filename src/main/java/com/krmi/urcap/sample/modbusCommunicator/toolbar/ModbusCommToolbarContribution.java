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
    private boolean modbusConnected = false;

    //Labels for the Toolbar GUI.
    private JLabel di1Label = new JLabel("Digital Input 1");
    private JLabel di2Label = new JLabel("Digital Input 2");
    private JLabel do1Label = new JLabel("Digital Output 1");
    private JLabel do2Label = new JLabel("Digital Output 2");
    private JLabel holdReg130Label = new JLabel("Register Input 130:");
    private JLabel holdReg130ValLabel = new JLabel("Value");
    private JLabel holdReg131Label = new JLabel("Register Input 131:");
    private JLabel holdReg131ValLabel = new JLabel("Value");


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
		
		JPanel outputPanel = new JPanel();
        outputPanel.setBackground(new Color(176, 188, 191));
		ioPanel.add(outputPanel);
		outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.Y_AXIS));
		
		JLabel doHeader = new JLabel("Outputs");
		doHeader.setFont(new Font("Roboto", Font.BOLD, 14));
		outputPanel.add(doHeader);

    }


    @Override
    public void openView() {
        //UI updates from non-GUI threads must use EventQueue.invokeLater (or SwingUtilities.invokeLater)
		uiTimer = new Timer(true);
		uiTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						if (!pauseTimer) {
							modbusMonitor();
						}
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

    private void modbusMonitor() {
        String text = "";
		try {
			modbusClient.getMaster(contribution.getIpAddress());

			text = "Connected to Modbus server.";
			modbusConnected = modbusClient.getMaster(contribution.getIpAddress()).isConnected();
            System.out.println("Modbus server initialization is: "+ modbusConnected);
		} catch (ModbusInitException e) {
			modbusConnected = false;
			text = "Could not connect to Modbus server; InitException.";
			// e.printStackTrace();
		} catch (Exception e) {
			modbusConnected = false;
			text = "Other catch exception";
			// e.printStackTrace();
		}

        if (modbusConnected){
            updateSignal(readMode.COIL, 16, di1Label);
            updateSignal(readMode.COIL, 17,  di2Label);
            updateSignal(readMode.INPUTREGISTER, 130, holdReg130Label, holdReg130ValLabel);
            updateSignal(readMode.INPUTREGISTER, 131, holdReg131Label, holdReg131ValLabel);
        }

    }

    //Updates boolean signals which don't have a value label.
    private void updateSignal(readMode mode, int registerNum, JLabel label){
        updateSignal(mode, registerNum, label, null);
    }

    private void updateSignal(readMode mode, int registerNum, JLabel label, JLabel registerValue){
        // Read and update the coil response
        if (readMode.COIL == mode) {
            try {
                Boolean coilResponse = modbusClient.readCoilStatus(slaveId, registerNum, contribution.getIpAddress());
                if (coilResponse) {
                    label.setIcon(getIcon("connected"));
                }
                else {
                    label.setIcon(getIcon("disconnected"));
                }
            }
            catch (ModbusTransportException e) {
                System.out.println("Transport error");
                // e.printStackTrace();
                label.setIcon(getIcon("warning"));
            }
            catch (Exception e) {
                System.out.println("General error");
                // e.printStackTrace();
                label.setIcon(getIcon("warning"));
            }
        }

        if (readMode.DISCRETEINPUT == mode) {
            try {
                Boolean discInputResponse = modbusClient.readHoldingRegisterBit(slaveId, registerNum, dType, contribution.getIpAddress());
                if (discInputResponse) {
                    label.setIcon(getIcon("connected"));
                }
                else {
                    label.setIcon(getIcon("disconnected"));
                }
            }
            catch (ModbusTransportException e) {
                System.out.println("Transport error");
                // e.printStackTrace();
                label.setIcon(getIcon("warning"));
            }
            catch (Exception e) {
                System.out.println("General error");
                // e.printStackTrace();
                label.setIcon(getIcon("warning"));
            }
        }

        if (readMode.INPUTREGISTER == mode) {
            try {
                Number inputRegResponse = modbusClient.readInputRegisters(slaveId, registerNum, dType, contribution.getIpAddress());
                label.setIcon(getIcon("connected"));
                registerValue.setText(inputRegResponse.toString());
            }
            catch (ModbusTransportException e) {
                System.out.println("Transport error");
                // e.printStackTrace();
                label.setIcon(getIcon("warning"));
            }
            catch (Exception e) {
                System.out.println("General error");
                // e.printStackTrace();
                label.setIcon(getIcon("warning"));
            }
        }

        if (readMode.HOLDINGREGISTER == mode) {
            try {
                Number holdRegResponse = modbusClient.readHoldingRegister(slaveId, registerNum, dType, contribution.getIpAddress());
                label.setIcon(getIcon("connected"));
                registerValue.setText(holdRegResponse.toString());
            }
            catch (ModbusTransportException e) {
                System.out.println("Transport error");
                // e.printStackTrace();
                label.setIcon(getIcon("warning"));
            }
            catch (Exception e) {
                System.out.println("General error");
                // e.printStackTrace();
                label.setIcon(getIcon("warning"));
            }
        }
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
    
}

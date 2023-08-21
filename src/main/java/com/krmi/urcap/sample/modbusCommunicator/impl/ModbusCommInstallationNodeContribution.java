package com.krmi.urcap.sample.modbusCommunicator.impl;

import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.ur.urcap.api.contribution.DaemonContribution;
import com.ur.urcap.api.contribution.InstallationNodeContribution;
import com.ur.urcap.api.contribution.installation.CreationContext;
import com.ur.urcap.api.contribution.installation.InstallationAPIProvider;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.domain.userinteraction.inputvalidation.InputValidationFactory;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputCallback;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputFactory;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardTextInput;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ModbusCommInstallationNodeContribution implements InstallationNodeContribution {
	public static final int PORT = 40405;
	private static final String IP_ADDRESS = "IpAddress";
	private static final String IP_ADDRESS_DEFAULT = "127.0.0.1";
	private static final int slaveId = 255;
	private static final int dType = DataType.TWO_BYTE_INT_SIGNED;
	private static final String ENABLED_KEY = "enabled";
	private static final String modbusReachable = "modbusReachable";

	private DataModel model;

	private final ModbusCommInstallationNodeView view;
	private Timer uiTimer;
	private boolean pauseTimer = false;
	private KeyboardInputFactory keyboardInputFactory;
	private final InputValidationFactory inputValidationFactory;

	private Modbus4jUtils modbusClient = new Modbus4jUtils();

	public ModbusCommInstallationNodeContribution(InstallationAPIProvider apiProvider, ModbusCommInstallationNodeView view, DataModel model, CreationContext context) {
		keyboardInputFactory = apiProvider.getUserInterfaceAPI().getUserInteraction().getKeyboardInputFactory();
		inputValidationFactory = apiProvider.getUserInterfaceAPI().getUserInteraction().getInputValidationFactory();
		this.view = view;
		this.model = model;
		if (context.getNodeCreationType() == CreationContext.NodeCreationType.NEW) {
			model.set(IP_ADDRESS, IP_ADDRESS_DEFAULT);
		}
		validateIpAddress();
	}

	@Override
	public void openView() {
		view.setIpAddress(getIpAddress());
		
		//UI updates from non-GUI threads must use EventQueue.invokeLater (or SwingUtilities.invokeLater)
		uiTimer = new Timer(true);
		uiTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						if (!pauseTimer) {
							updateUI();
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

	@Override
	public void generateScript(ScriptWriter writer) {

	}

	// private void updateUI() {
	// 	String text = "";
	// 	try {
	// 		modbusClient.getMaster(getIpAddress()).testSlaveNode(0);
	// 		text = "Connected to Modbus server.";
	// 		modbusConnected = true;
	// 	} catch (ModbusInitException e) {
	// 		modbusConnected = false;
	// 		text = "Could not connect to Modbus server; InitException.";
	// 		// e.printStackTrace();
	// 	} catch (Exception e) {
	// 		modbusConnected = false;
	// 		text = "Other catch exception";
	// 		// e.printStackTrace();
	// 	}
	// 	if (modbusConnected){
	// 		try {
	// 			Number num000 = modbusClient.readHoldingRegister(slaveId, 250, dType, getIpAddress());
	// 			System.out.println("Holding register 0 is: " + num000.toString());
	// 			view.setStatusLabel(text);
	// 		} catch (ModbusTransportException e) {
	// 			modbusConnected = false;
	// 			text = "Could not connect to Modbus server; TransportException.";
	// 			view.setStatusLabel(text);
	// 			// e.printStackTrace();
	// 		} catch (Exception e) {
	// 			modbusConnected = false;
	// 			text = "Could not connect to Modbus server; general Exception.";
	// 			view.setStatusLabel(text);
	// 			// e.printStackTrace();
	// 		}
	// 	}

	// }

		private void updateUI() {
		boolean state = model.get(modbusReachable, false);
		String text = "";
		if (state) {
			view.setTextFieldColor(Color.GREEN);
			text = "Modbus server is reachable";
		} else {
			view.setTextFieldColor(Color.RED);
			text = "Modbus server is not reachable";
		}

		view.setStatusLabel(text);
	}

	public KeyboardTextInput getKeyboardForIpAddress() {
		KeyboardTextInput keyboard = keyboardInputFactory.createIPAddressKeyboardInput();
		keyboard.setInitialValue(model.get(IP_ADDRESS, ""));
		return keyboard;
	}

	public KeyboardInputCallback<String> getCallbackForIpAddress() {
		return new KeyboardInputCallback<String>() {
			@Override
			public void onOk(String value) {
				model.set(IP_ADDRESS,value);
				view.setIpAddress(value);
				validateIpAddress();
			}
		};
	}

	private void validateIpAddress() {
		new Thread (new Runnable() {
			@Override
			public void run() {
				try {
					pauseTimer = true;
					if (modbusClient.getMaster(getIpAddress()).testSlaveNode(0)) {
						model.set(modbusReachable, true);
					}
					else {
						model.set(modbusReachable, false);
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
				finally {
					pauseTimer = false;
					}
				}
		}).start();
	}

	private Boolean isModbusEnabled() {
		return model.get(ENABLED_KEY, true);
	}
	
	//TODO: Add check to make sure modbus server is reachable.
	public boolean isReachable() {
		return model.isSet(IP_ADDRESS) && model.get(modbusReachable, false);
	}

	public String getIpAddress() {
		return model.get(IP_ADDRESS, IP_ADDRESS_DEFAULT);
	}
}

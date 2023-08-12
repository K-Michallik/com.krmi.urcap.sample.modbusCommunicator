package com.krmi.urcap.sample.modbusCommunicator.impl;

import com.serotonin.modbus4j.exception.ModbusInitException;
import com.ur.urcap.api.contribution.InstallationNodeContribution;
import com.ur.urcap.api.contribution.installation.CreationContext;
import com.ur.urcap.api.contribution.installation.InstallationAPIProvider;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.domain.userinteraction.inputvalidation.InputValidationFactory;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputCallback;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputFactory;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardTextInput;
import com.ur.urcap.api.contribution.driver.general.userinput.CustomUserInputConfiguration;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class ModbusCommInstallationNodeContribution implements InstallationNodeContribution {
	private static final String POPUPTITLE_KEY = "popuptitle";

	private static final String XMLRPC_VARIABLE = "my_daemon_swing";
	private static final String ENABLED_KEY = "enabled";
	private static final String DEFAULT_VALUE = "Hello My Daemon";
	public static final int PORT = 40405;
	private static final String IP_ADDRESS = "IpAddress";
	private static final String IP_ADDRESS_DEFAULT = "192.168.199.1";
	private static final int slaveId = 255;

	private DataModel model;

	private final ModbusCommInstallationNodeView view;
	private Timer uiTimer;
	private boolean pauseTimer = false;
	private boolean modbusConnected = false;

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
		// applyDesiredDaemonStatus();
	}

	@Override
	public void openView() {
		// view.setPopupText(getPopupTitle());

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

	private void updateUI() {
		try {
			Modbus4jUtils.getMaster(IP_ADDRESS);
			modbusConnected = true;
			System.out.println("Connected to Modbus server.");
		} catch (ModbusInitException e) {
			modbusConnected = false;
			System.out.println("Could not connect at: "+ IP_ADDRESS);
			e.printStackTrace();
		}
		if (modbusConnected){
			try {
				Number num000 = modbusClient.readHoldingRegister(slaveId, PORT, PORT, IP_ADDRESS);
				System.out.println("Holding register 0 is: " + num000.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}

	}

	// public void onStartClick() {
	// 	model.set(ENABLED_KEY, true);
	// 	applyDesiredDaemonStatus();
	// }

	// public void onStopClick() {
	// 	model.set(ENABLED_KEY, false);
	// 	applyDesiredDaemonStatus();
	// }

	// private void applyDesiredModbusStatus() {
	// 	new Thread(new Runnable() {
	// 		@Override
	// 		public void run() {
	// 			if (isServerEnabled()) {
	// 				// Download the daemon settings to the daemon process on initial start for real-time preview purposes
	// 				try {
	// 					pauseTimer = true;
	// 					awaitDaemonRunning(5000);
	// 				} catch(Exception e){
	// 					System.err.println("Could not set the title in the daemon process.");
	// 				} finally {
	// 					pauseTimer = false;
	// 				}
	// 			} else {
	// 				daemonService.getDaemon().stop();
	// 			}
	// 		}
	// 	}).start();
	// }

	// private void awaitDaemonRunning(long timeOutMilliSeconds) throws InterruptedException {
	// 	daemonService.getDaemon().start();
	// 	long endTime = System.nanoTime() + timeOutMilliSeconds * 1000L * 1000L;
	// 	while(System.nanoTime() < endTime && (daemonService.getDaemon().getState() != DaemonContribution.State.RUNNING || !xmlRpcDaemonInterface.isReachable())) {
	// 		Thread.sleep(100);
	// 	}
	// }

	// public String getPopupTitle() {
	// 	return model.get(POPUPTITLE_KEY, DEFAULT_VALUE);
	// }

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
			}
		};
	}
	

	// private void setPopupTitle(String title) {
	// 	model.set(POPUPTITLE_KEY, title);
	// 	// Apply the new setting to the daemon for real-time preview purposes
	// 	// Note this might influence a running program, since the actual state is stored in the daemon.
	// 	try {
	// 		xmlRpcDaemonInterface.setTitle(title);
	// 	} catch(Exception e){
	// 		System.err.println("Could not set the title in the daemon process.");
	// 	}
	// }

	//TODO: Add check to make sure modbus server is reachable.
	public boolean isDefined() {
		return model.isSet(IP_ADDRESS);
	}

	// private Boolean isServerEnabled() {
	// 	return model.get(ENABLED_KEY, true); //This daemon is enabled by default
	// }

	// private String getIpAddress() {
	// 	return model.get(IP_ADDRESS, IP_ADDRESS_DEFAULT);
	// }
}

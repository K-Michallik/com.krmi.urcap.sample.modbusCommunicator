package com.krmi.urcap.sample.modbusCommunicator.impl;

import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeView;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardTextInput;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModbusCommInstallationNodeView implements SwingInstallationNodeView<ModbusCommInstallationNodeContribution> {

	private final Style style;
	private JTextField ipAddress = new JTextField();
	private JButton startButton;
	private JButton stopButton;
	private JLabel statusLabel;
	private Box ipBox;


	public ModbusCommInstallationNodeView(Style style) {
		this.style = style;
	}

	@Override
	public void buildUI(JPanel panel, ModbusCommInstallationNodeContribution contribution) {
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	
		panel.add(createInfo());
		panel.add(createVerticalSpacing());
	
		ipAddress.setHorizontalAlignment(JTextField.RIGHT);
		panel.add(createIPAddressBar(contribution));

		panel.add(createVerticalSpacing(style.getLargeVerticalSpacing()));
	
		panel.add(createStatusInfo());
	}

	private Box createInfo() {
		Box infoBox = Box.createVerticalBox();
		infoBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		JTextPane pane = new JTextPane();
		pane.setBorder(BorderFactory.createEmptyBorder());
		SimpleAttributeSet attributeSet = new SimpleAttributeSet();
		StyleConstants.setLineSpacing(attributeSet, 0.5f);
		StyleConstants.setLeftIndent(attributeSet, 0f);
		pane.setParagraphAttributes(attributeSet, false);
		pane.setText("The popup title below is shared between all My Daemon program nodes.\nThe title cannot be empty.");
		pane.setEditable(false);
		pane.setMaximumSize(pane.getPreferredSize());
		pane.setBackground(infoBox.getBackground());
		infoBox.add(pane);
		return infoBox;
	}

	private Box createIPAddressBar(final ModbusCommInstallationNodeContribution contribution) {
		ipBox = Box.createHorizontalBox();
		ipBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		ipBox.add(new JLabel("IP Address:"));
		ipBox.add(createHorizontalSpacing());

		ipAddress.setFocusable(false);
		ipAddress.setPreferredSize(style.getInputfieldSize());
		ipAddress.setMaximumSize(ipAddress.getPreferredSize());
		ipAddress.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				KeyboardTextInput keyboardInput = contribution.getKeyboardForIpAddress();
				keyboardInput.show(ipAddress, contribution.getCallbackForIpAddress());
			}
		});
		ipBox.add(ipAddress);


		return ipBox;
	}

	private Box createStatusInfo() {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);

		statusLabel = new JLabel("Modbus status");
		box.add(statusLabel);
		return box;
	}

	private Component createHorizontalSpacing() {
		return Box.createRigidArea(new Dimension(style.getHorizontalSpacing(), 0));
	}

	private Component createVerticalSpacing(int space) {
		return Box.createRigidArea(new Dimension(0, space));
	}

	private Component createVerticalSpacing() {
		return createVerticalSpacing(style.getVerticalSpacing());
	}

	public void setStatusLabel(String text) {
		statusLabel.setText(text);
	}

	public void setIpAddress(String value) {
		ipAddress.setText(value);
	}

	public void setTextFieldColor (Color color) {
		try {
			ipBox.setBackground(color);
		}
		catch (Exception e) {
			ipBox.setBackground(Color.WHITE);
		}
	}
}

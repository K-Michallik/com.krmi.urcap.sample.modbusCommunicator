package com.krmi.urcap.sample.modbusCommunicator.signal;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Image;
import java.awt.image.BufferedImage;

public abstract class Signal {
    protected JLabel nameLabel;
    protected JLabel statusLabel;
    protected final Box horizontalBox;
    protected SignalStatus status;

    public Signal(String name) {
        this.nameLabel = new JLabel(name);
        this.statusLabel = new JLabel();
        this.horizontalBox = Box.createHorizontalBox();
        this.status = SignalStatus.DISCONNECTED;
        
        horizontalBox.add(nameLabel);
        horizontalBox.add(Box.createHorizontalStrut(10)); 
        horizontalBox.add(statusLabel);
    }

    public Box getSignalBox() {
        return horizontalBox;
    }

    protected abstract void updateSignal();
    protected abstract void refreshUI();

    protected enum SignalStatus {
        CONNECTED,
        DISCONNECTED
    }

    protected Icon getIcon(String iconName) {
       BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResource("/icons/" + iconName + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ImageIcon(image.getScaledInstance(25, -1, Image.SCALE_SMOOTH));
    }
}

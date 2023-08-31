package com.krmi.urcap.sample.modbusCommunicator.impl;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import java.awt.Image;
import java.awt.image.BufferedImage;

public abstract class Signal {
    protected final String name;
    protected final JLabel iconLabel;

    public Signal(String name, JLabel iconLabel) {
        this.name = name;
        this.iconLabel = iconLabel;
    }

    public abstract void updateUI(Object value);

    public String getName() {
        return name;
    }

    // public JLabel getIconLabel() {
    //     return iconLabel;
    // }

    protected Icon getIcon(String icon) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResource("/icons/" + icon + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ImageIcon(image.getScaledInstance(25, -1, Image.SCALE_SMOOTH));
    }
    
    public abstract void updateIcon(int signalStatus);

}

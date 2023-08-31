package com.krmi.urcap.sample.modbusCommunicator.impl;

import javax.swing.JLabel;

public class BooleanSignal extends Signal{
    
    public BooleanSignal(String name, JLabel iconLabel) {
        super(name, iconLabel);
    }

    @Override
    public void updateIcon(int signalStatus){
        
    }

    @Override
    public void updateUI(Object value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateUI'");
    }
    
}


package com.krmi.urcap.sample.modbusCommunicator.toolbar;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ur.urcap.api.contribution.toolbar.ToolbarConfiguration;
import com.ur.urcap.api.contribution.toolbar.ToolbarContext;
import com.ur.urcap.api.contribution.toolbar.swing.SwingToolbarContribution;
import com.ur.urcap.api.contribution.toolbar.swing.SwingToolbarService;

public class ModbusCommToolbarService implements SwingToolbarService{

    @Override
    public Icon getIcon() {
        // TODO Auto-generated method stub
        return new ImageIcon(getClass().getResource("/icons/acme_logo.png"));
    }

    @Override
    public void configureContribution(ToolbarConfiguration configuration) {
        // TODO Auto-generated method stub
        configuration.setToolbarHeight(300);
    }

    @Override
    public SwingToolbarContribution createToolbar(ToolbarContext context) {
        // TODO Auto-generated method stub
        return new ModbusCommToolbarContribution(context);
    }
    
}

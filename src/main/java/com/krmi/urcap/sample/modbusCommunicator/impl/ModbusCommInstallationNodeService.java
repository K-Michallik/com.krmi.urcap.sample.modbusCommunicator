package com.krmi.urcap.sample.modbusCommunicator.impl;

import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.installation.ContributionConfiguration;
import com.ur.urcap.api.contribution.installation.CreationContext;
import com.ur.urcap.api.contribution.installation.InstallationAPIProvider;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeService;
import com.ur.urcap.api.domain.data.DataModel;

import java.util.Locale;

public class ModbusCommInstallationNodeService implements SwingInstallationNodeService<ModbusCommInstallationNodeContribution, ModbusCommInstallationNodeView> {
	
	@Override
	public String getTitle(Locale locale) {
		return "Modbus Communicator";
	}

	@Override
	public void configureContribution(ContributionConfiguration configuration) {
	}

	@Override
	public ModbusCommInstallationNodeView createView(ViewAPIProvider apiProvider) {
		Style style = new V5Style();
		return new ModbusCommInstallationNodeView(style);
	}

	@Override
	public ModbusCommInstallationNodeContribution createInstallationNode(InstallationAPIProvider apiProvider, ModbusCommInstallationNodeView view, DataModel model, CreationContext context) {
		return new ModbusCommInstallationNodeContribution(apiProvider, view, model, context);
	}

}

package com.krmi.urcap.sample.modbusCommunicator.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.krmi.urcap.sample.modbusCommunicator.toolbar.ModbusCommToolbarService;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeService;
import com.ur.urcap.api.contribution.toolbar.swing.SwingToolbarService;

/**
 * Hello world activator for the OSGi bundle URCAPS contribution
 *
 */
public class Activator implements BundleActivator {
	@Override
	public void start(BundleContext context) throws Exception {
		context.registerService(SwingInstallationNodeService.class, new ModbusCommInstallationNodeService(), null);
		context.registerService(SwingToolbarService.class, new ModbusCommToolbarService(), null);
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
	}
}


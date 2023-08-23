package com.krmi.urcap.sample.modbusCommunicator.impl;

import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.locator.BaseLocator;

public class Modbus4jUtils {
	
	static ModbusFactory modbusFactory = new ModbusFactory();
	
	public Modbus4jUtils() {
		// TODO Auto-generated constructor stub
		// if(modbusFactory ==  null) {
		// 	modbusFactory = new ModbusFactory();
		// }
		// try {
		// 	getMaster(ipAddr);
		// } catch (ModbusInitException e) {
		// 	// TODO Auto-generated catch block
		// 	e.printStackTrace();
		// }
	}
	public static ModbusMaster getMaster(String IPAddr) throws ModbusInitException {
		IpParameters params = new IpParameters();
		params.setHost(IPAddr);
		params.setPort(502);
		
		ModbusMaster master = modbusFactory.createTcpMaster(params, false);
		master.init();
		return master;
	}
	
	public static Number readInputRegisters(int slaveId, int offset, int dataType, String ipAddr)
	throws ModbusTransportException, ErrorResponseException, ModbusInitException{
		BaseLocator<Number> loc = BaseLocator.inputRegister(slaveId, offset, dataType);
		Number value = getMaster(ipAddr).getValue(loc);
		return value;
	}
	public static Number readHoldingRegister(int slaveId, int offset, int dataType, String ipAddr)
	throws ModbusTransportException, ErrorResponseException, ModbusInitException{
		BaseLocator<Number> loc = BaseLocator.holdingRegister(slaveId, offset, dataType);
		Number value = getMaster(ipAddr).getValue(loc);
		return value;
	}
	public static Boolean readHoldingRegisterBit(int slaveId, int offset, int bit, String ipAddr) 
	throws ModbusTransportException, ErrorResponseException, ModbusInitException{
		BaseLocator<Boolean> loc = BaseLocator.holdingRegisterBit(slaveId, offset, bit);
		Boolean value = getMaster(ipAddr).getValue(loc);
		return value;
	}
	public static Boolean readCoilStatus(int slaveId, int offset, String ipAddr)
	throws ModbusTransportException, ErrorResponseException, ModbusInitException{
		BaseLocator<Boolean> loc = BaseLocator.coilStatus(slaveId, offset);
		Boolean value = getMaster(ipAddr).getValue(loc);
		return value;
	}

	public static void setInputStatus (int slaveId, int offset, String ipAddr, boolean bValue)
	throws ModbusTransportException, ErrorResponseException, ModbusInitException {
		BaseLocator<Boolean> loc = BaseLocator.coilStatus(slaveId, offset);
		getMaster(ipAddr).setValue(loc, bValue);
	}

}

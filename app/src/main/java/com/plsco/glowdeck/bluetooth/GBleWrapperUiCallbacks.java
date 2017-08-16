package com.plsco.glowdeck.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.List;

public interface GBleWrapperUiCallbacks {

	void uiDeviceFound(final BluetoothDevice device, int rssi, byte[] record);
	
	void uiDeviceConnected(final BluetoothGatt gatt,
                           final BluetoothDevice device);
	
	void uiDeviceDisconnected(final BluetoothGatt gatt,
                              final BluetoothDevice device);
	
	void uiAvailableServices(final BluetoothGatt gatt,
                             final BluetoothDevice device,
                             final List<BluetoothGattService> services);
	
	void uiCharacteristicForService(final BluetoothGatt gatt,
                                    final BluetoothDevice device,
                                    final BluetoothGattService service,
                                    final List<BluetoothGattCharacteristic> chars);

	void uiCharacteristicsDetails(final BluetoothGatt gatt,
                                  final BluetoothDevice device,
                                  final BluetoothGattService service,
                                  final BluetoothGattCharacteristic characteristic);
	
	void uiNewValueForCharacteristic(final BluetoothGatt gatt,
                                     final BluetoothDevice device,
                                     final BluetoothGattService service,
                                     final BluetoothGattCharacteristic ch,
                                     final String strValue,
                                     final int intValue,
                                     final byte[] rawValue,
                                     final String timestamp);
	
	void uiGotNotification(final BluetoothGatt gatt,
                           final BluetoothDevice device,
                           final BluetoothGattService service,
                           final BluetoothGattCharacteristic characteristic);
	
	void uiSuccessfulWrite(final BluetoothGatt gatt,
                           final BluetoothDevice device,
                           final BluetoothGattService service,
                           final BluetoothGattCharacteristic ch,
                           final String description);
	
	void uiFailedWrite(final BluetoothGatt gatt,
                       final BluetoothDevice device,
                       final BluetoothGattService service,
                       final BluetoothGattCharacteristic ch,
                       final String description);
	
	void uiNewRssiAvailable(final BluetoothGatt gatt, final BluetoothDevice device, final int rssi);
	
	/* define Null Adapter class for that interface */
	class Null implements GBleWrapperUiCallbacks {
		@Override
		public void uiDeviceConnected(BluetoothGatt gatt, BluetoothDevice device) {}
		@Override
		public void uiDeviceDisconnected(BluetoothGatt gatt, BluetoothDevice device) {}
		@Override
		public void uiAvailableServices(BluetoothGatt gatt, BluetoothDevice device,
				List<BluetoothGattService> services) {}
		@Override
		public void uiCharacteristicForService(BluetoothGatt gatt,
				BluetoothDevice device, BluetoothGattService service,
				List<BluetoothGattCharacteristic> chars) {}
		@Override
		public void uiCharacteristicsDetails(BluetoothGatt gatt,
				BluetoothDevice device, BluetoothGattService service,
				BluetoothGattCharacteristic characteristic) {}
		@Override
		public void uiNewValueForCharacteristic(BluetoothGatt gatt,
				BluetoothDevice device, BluetoothGattService service,
				BluetoothGattCharacteristic ch, String strValue, int intValue,
				byte[] rawValue, String timestamp) {}
		@Override
		public void uiGotNotification(BluetoothGatt gatt, BluetoothDevice device,
				BluetoothGattService service,
				BluetoothGattCharacteristic characteristic) {}
		@Override
		public void uiSuccessfulWrite(BluetoothGatt gatt, BluetoothDevice device,
				BluetoothGattService service, BluetoothGattCharacteristic ch,
				String description) {}
		@Override
		public void uiFailedWrite(BluetoothGatt gatt, BluetoothDevice device,
				BluetoothGattService service, BluetoothGattCharacteristic ch,
				String description) {}
		@Override
		public void uiNewRssiAvailable(BluetoothGatt gatt, BluetoothDevice device,
				int rssi) {}
		@Override
		public void uiDeviceFound(BluetoothDevice device, int rssi, byte[] record) {}		
	}
}


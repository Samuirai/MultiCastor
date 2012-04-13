package zisko.multicastor.program.mmrp;

import java.io.IOException;

public class MMRPReceiver extends MMRPEntity{

	public MMRPReceiver(byte[] deviceMACAddress, byte[] streamMACAddress) throws IOException {
		super(deviceMACAddress, streamMACAddress);
	}
	
}

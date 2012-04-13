package zisko.multicastor.program.mmrp;

import java.io.IOException;

public class MMRPReceiver extends MMRPEntity{
	
	private ThreadReceiveDataPacket receiveDataPacket = null;
	
	public MMRPReceiver(byte[] deviceMACAddress, byte[] streamMACAddress) throws IOException {
		super(deviceMACAddress, streamMACAddress);
		this.receiveDataPacket = new ThreadReceiveDataPacket(deviceMACAddress, streamMACAddress);
	}
	
	public byte[] getDataPacket (){
		return this.receiveDataPacket.getDataPacket();
	}
	
	public void waitForDataPacket(){
		new Thread(this.receiveDataPacket).start();
	}
}

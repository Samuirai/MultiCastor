package zisko.multicastor.program.mmrp;

import java.io.IOException;

public class MMRPSender extends MMRPEntity {
	
	public MMRPSender(byte[] deviceMACAddress, byte[] streamMACAddress) throws IOException {
		super(deviceMACAddress, streamMACAddress);
	}
	
	public void sendDataPacket(byte[] data) throws IOException{
		PacketHandler.sendPacket(this.deviceMACAddress,DataPacket.getDataPacket(this.deviceMACAddress,this.streamMACAddress,data));
	}
}

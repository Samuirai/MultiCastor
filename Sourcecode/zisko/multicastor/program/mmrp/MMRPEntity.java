package zisko.multicastor.program.mmrp;

import java.io.IOException;


public class MMRPEntity {
	protected byte[] deviceMACAddress = null;
	protected byte[] streamMACAddress = null;
	private Thread keepPathAlive = null;
	
	public MMRPEntity(byte[] deviceMACAddress,byte[] streamMACAddress) throws IOException {
		this.deviceMACAddress = deviceMACAddress;
		this.streamMACAddress = streamMACAddress;
	}
	
	public void registerPath() throws IOException{
		PacketHandler.sendPacket(this.deviceMACAddress,MMRPPacket.getJoinEmpty(this.deviceMACAddress, this.streamMACAddress));
		this.keepPathAlive = new Thread(new ThreadKeepPathAlive(this.deviceMACAddress, this.streamMACAddress));
		this.keepPathAlive.start();
		
	}
	
	public void deregisterPath() throws IOException{
		PacketHandler.sendPacket(this.deviceMACAddress,MMRPPacket.getLeave(this.deviceMACAddress, this.streamMACAddress));
		this.keepPathAlive.interrupt();
	}
	
	public void deregisterAllPaths() throws IOException{
		PacketHandler.sendPacket(this.deviceMACAddress,MMRPPacket.getLeaveAll(this.deviceMACAddress, this.streamMACAddress));
		this.keepPathAlive.interrupt();
	}
}



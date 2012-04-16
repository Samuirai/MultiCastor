package zisko.multicastor.program.mmrp;

import java.io.IOException;

import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

public class MMRPReceiver extends MMRPEntity{
	
	private boolean found;
	private byte[] foundPacket;
	private Pcap pcap = null;
	
	public MMRPReceiver(byte[] deviceMACAddress, byte[] streamMACAddress) throws IOException {
		super(deviceMACAddress, streamMACAddress);
		this.pcap = PcapHandler.getPcapInstance(deviceMACAddress);
	}
	
	public byte[] waitForDataPacketAndGetIt(byte[] buffer){
		found = false;
		foundPacket = buffer;
		while(!Thread.interrupted() && !found){
			PcapPacketHandler<String> pcapPacketHandler = new PcapPacketHandler<String>() {
				public void nextPacket(PcapPacket packet, String arg1) {
					byte [] destination = new byte [6];
					
					for(int i = 0; i < 6; i++){
						destination[i] = packet.getByte(i);
					}

					int length = (int)packet.getByte(13);
					
					if(PcapHandler.compareMACs(destination, streamMACAddress)){
						for(int i = 14; i < length+14; i++)
								foundPacket[i-14] = packet.getByte(i);
						found = true;
					}
				}
			};
			pcap.loop(1, pcapPacketHandler, "");
		}
		return foundPacket;
	}
}

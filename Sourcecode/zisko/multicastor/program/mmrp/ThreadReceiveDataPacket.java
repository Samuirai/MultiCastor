package zisko.multicastor.program.mmrp;

import java.io.IOException;

import org.jnetpcap.Pcap;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

import zisko.multicastor.program.mmrp.PcapHandler;

import zisko.multicastor.program.mmrp.MMRPPacket;
import zisko.multicastor.program.mmrp.PacketHandler;

public class ThreadReceiveDataPacket implements Runnable {
	private byte[] streamMACAddress;
	private Pcap pcap = null;
	private byte[] dataPacket;
	private boolean found;
	private boolean interrupted = false;
	public ThreadReceiveDataPacket(byte[] deviceMACAddress,byte[] streamMACAddress) throws IOException {
		this.streamMACAddress = streamMACAddress;
		this.pcap = PcapHandler.getPcapInstance(deviceMACAddress);
	}
	
	public synchronized void waitForDataPacket(){
		this.found = false;
		while(!interrupted && !found){
			PcapPacketHandler<String> pcapPacketHandler = new PcapPacketHandler<String>() {
				public void nextPacket(PcapPacket packet, String arg1) {
					byte [] destination = new byte [6];
					
					for(int i = 0; i < 6; i++){
						destination[i] = packet.getByte(i);
					}

					int length = (int)packet.getByte(13);
					
					if(PcapHandler.compareMACs(destination, streamMACAddress)){
						byte[] buffer = new byte[packet.getTotalSize()];

						for(int i = 0; i < length+14; i++)
								buffer[i] = packet.getByte(i);

						dataPacket = buffer;
						found = true;
					}
				}
			};
			pcap.loop(1, pcapPacketHandler, "");
		}
	}
	
	public synchronized byte [] getDataPacket(){
		return this.dataPacket;
	}

	@Override
	public void run() {
		waitForDataPacket();
	}

}

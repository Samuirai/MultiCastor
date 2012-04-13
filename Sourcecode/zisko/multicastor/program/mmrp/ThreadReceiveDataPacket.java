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
	private byte[] dataPacket ;
	
	public ThreadReceiveDataPacket(byte[] deviceMACAddress,byte[] streamMACAddress) throws IOException {
		this.streamMACAddress = streamMACAddress;
		this.pcap = PcapHandler.getPcapInstance(deviceMACAddress);
	}
	
	public synchronized void waitForDataPacket(){
		while(!Thread.interrupted()){
			PcapPacketHandler<String> pcapPacketHandler = new PcapPacketHandler<String>() {


				public void nextPacket(PcapPacket packet, String arg1) {
					byte [] destination = new byte [6];
					
					for(int i = 0; i < 6; i++){
						destination[i] = packet.getByte(i);
					}
					
					if(PcapHandler.compareMACs(destination, streamMACAddress)){
						/*for(int i = 0; i < packet.getTotalSize(); i++)
							System.out.println(packet.getByte(i));
						
						System.out.println("Found data packet");
						dataPacket = packet.getByteArray(0, packet.getTotalSize()-1);
						packet.getBy*/
		                final JPacket.State packetState = packet.getState();
		                for (int index = 0; index < packetState.getHeaderCount(); index++) {
		                    int start = packetState.getHeaderOffsetByIndex(index);
		                    int length = packetState.getHeaderLengthByIndex(index);
		                    dataPacket = packet.getByteArray(start, length);
		                }
		 
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

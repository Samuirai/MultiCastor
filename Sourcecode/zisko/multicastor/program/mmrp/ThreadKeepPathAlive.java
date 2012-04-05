package zisko.multicastor.program.mmrp;

import java.io.IOException;

import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;


public class ThreadKeepPathAlive implements Runnable {
	private byte[] deviceMACAddress;
	private byte[] streamMACAddress;
	private Pcap pcap = null;
	
	public ThreadKeepPathAlive(byte[] deviceMACAddress,byte[] streamMACAddress) throws IOException {
		this.deviceMACAddress = deviceMACAddress;
		this.streamMACAddress = streamMACAddress;
		this.pcap = PcapHandler.getPcapInstance(deviceMACAddress);
	}
	
	public void waitForEmpty(){
		while(!Thread.interrupted()){
			PcapPacketHandler<String> pcapPacketHandler = new PcapPacketHandler<String>() {

				public void nextPacket(PcapPacket packet, String arg1) {
					
					//check for mmrp packet
					if((packet.getByte(12) == (byte) 0x88)
							&& (packet.getByte(13) == (byte) 0xf6)){
						
						//check for stream address
						boolean streamcheck = true;
						
						for(int i = 0; i < 6; i++){
							if(packet.getByte(19+i) != streamMACAddress[i]){
								streamcheck = false;
							}
						}
						
						if(streamcheck){
							
							int cast = (int)packet.getByte(25) & 0xff;
							cast = cast /36;
							
							
							if(cast == 4){
								try {
									PacketHandler.sendPacket(deviceMACAddress,MMRPPacket.getJoinIn(deviceMACAddress, streamMACAddress));
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}
				}
			};
			
			pcap.loop(1, pcapPacketHandler, "");
		}
	}
	
	@Override
	public void run() {
		this.waitForEmpty();
	}

}

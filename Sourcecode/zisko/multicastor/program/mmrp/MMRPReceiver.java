package zisko.multicastor.program.mmrp;

import java.io.IOException;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapClosedException;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

public class MMRPReceiver extends MMRPEntity{
	
	private boolean found;
	private byte[] foundPacket;
	private Pcap pcap = null;
	
	PcapPacketHandler<String> pcapPacketHandler = new PcapPacketHandler<String>() {
		public void nextPacket(PcapPacket packet, String arg1) {
			int length = (int)(packet.getByte(13) & 0xFF)
					+ ((int)(packet.getByte(12) & 0xFF))*255;	
			System.arraycopy(packet.getByteArray(14, length), 0, foundPacket, 0, length);
			found = true;
		}
	};
	
	public void stopLoop(){
		this.pcap.breakloop();
	}
	public MMRPReceiver(byte[] deviceMACAddress, byte[] streamMACAddress) throws IOException {
		super(deviceMACAddress, streamMACAddress);
		this.pcap = PcapHandler.getPcapInstance(deviceMACAddress);
		PcapBpfProgram programm = new PcapBpfProgram();
		this.pcap.compile(programm, "ether dst " + PcapHandler.byteMACToString(streamMACAddress), 0, (int) 0xFFFFFF00);
		this.pcap.setFilter(programm);
	}
	
	public byte[] waitForDataPacketAndGetIt(byte[] buffer){
		found = false;
		foundPacket = buffer;

		while(!found){
			pcap.loop(1, pcapPacketHandler, "");
		}
		
		return foundPacket;
	}
}

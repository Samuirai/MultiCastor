package zisko.multicastor.program.mmrp;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.jnetpcap.Pcap;

public class PacketHandler {
	public static void sendPacket(byte[] deviceMACAddress, byte[] packet) throws IOException{
		Pcap pcap = PcapHandler.getPcapInstance(deviceMACAddress);
		ByteBuffer b = ByteBuffer.wrap(packet);
		if (pcap.sendPacket(b) != Pcap.OK) {
			System.err.println(pcap.getErr());
		}
		pcap.close();
	}
}

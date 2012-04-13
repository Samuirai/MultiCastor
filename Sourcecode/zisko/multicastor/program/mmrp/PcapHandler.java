package zisko.multicastor.program.mmrp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

public class PcapHandler {
	
	public static Pcap getPcapInstance(byte[] deviceMACAddress) throws IOException{
		byte [] source = {(byte) 0x00,(byte) 0x27,(byte) 0x13,(byte) 0x69,(byte) 0x4b,(byte) 0x04}; 
		System.out.println(Arrays.toString(deviceMACAddress));
		System.out.println(Arrays.toString(source));
		
		PcapIf device = getDevice(source);
		int snaplen = 64 * 1024; // Capture all packets, no trucation
		int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
		int timeout = 10 * 1000; // 10 seconds in millis
		StringBuilder errbuf = new StringBuilder();
		
		if(device == null)
			throw new IOException();
		
		Pcap pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout,
				errbuf);
		
		return pcap;
	}
	
	private static PcapIf getDevice(byte[] deviceMACAddress) throws IOException{
		List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with
		// NICs
		StringBuilder errbuf = new StringBuilder(); // For any error msgs

		int r = Pcap.findAllDevs(alldevs, errbuf);
		if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
			//System.err.printf("Can't read list of devices, error is %s",
					//errbuf.toString());
			throw new IOException();
		}
		
		for (int i = 0; i < alldevs.size(); i++){
			if(alldevs.get(i).getHardwareAddress() != null){
				if(compareMACs(deviceMACAddress, alldevs.get(i).getHardwareAddress())){
					return alldevs.get(i);
				}
			}
		}
		
		return null;
	}
	
	private static boolean compareMACs(byte[] a, byte[] b){
		boolean sameMAC = true;
		
		for(int j = 0; j < 6; j++){
			if(a[j] != b[j]){
				sameMAC = false;
				break;
			}
		}
		
		return sameMAC;
	}
}

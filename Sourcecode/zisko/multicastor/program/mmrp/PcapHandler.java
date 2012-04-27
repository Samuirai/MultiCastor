package zisko.multicastor.program.mmrp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

public class PcapHandler {

	public static Pcap getPcapInstance(byte[] deviceMACAddress)
			throws IOException {
		PcapIf device = getDevice(deviceMACAddress);
		int snaplen = 64 * 1024; // Capture all packets, no trucation
		int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
		int timeout = 10 * 1000; // 10 seconds in millis
		StringBuilder errbuf = new StringBuilder();

		if (device == null){
			throw new IOException();
		}

		Pcap pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout,
				errbuf);

		return pcap;
	}

	private static List<PcapIf> alldevs;
	private static List<byte[]> alldevsAdress;

	private static PcapIf getDevice(byte[] deviceMACAddress) throws IOException {
		if (alldevs == null) {
			alldevs = new ArrayList<PcapIf>(); // Will be filled with
			alldevsAdress = new ArrayList<byte[]>();
			// NICs

			StringBuilder errbuf = new StringBuilder(); // For any error msgs
			int r = 0;
			try {
				r = Pcap.findAllDevs(alldevs, errbuf);
			} catch (NoClassDefFoundError e) {
				System.out.println("[Warning] NoClassDefFoundError. jnetpcap probably not installed.");
				r = 0;
			}
			if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
				// System.err.printf("Can't read list of devices, error is %s",
				// errbuf.toString());
				System.out.println("Error Stuff");
				throw new IOException();
			}
			for (int i = 0; i < alldevs.size(); i++) {
					alldevsAdress.add(alldevs.get(i).getHardwareAddress());
			}
		} 
		
		for(byte[] address : alldevsAdress)
			if(address != null && compareMACs(deviceMACAddress, address))
				return alldevs.get(alldevsAdress.indexOf(address));
		
		return null;
	}

	public static boolean compareMACs(byte[] a, byte[] b) {
		boolean sameMAC = true;

		for (int j = 0; j < 6; j++) {
			if (a[j] != b[j]) {
				sameMAC = false;
				break;
			}
		}

		return sameMAC;
	}
	
	public static String byteMACToString(byte [] mac){
		if (mac == null)
	        return null;

	    StringBuilder sb = new StringBuilder(18);
	    for (byte b : mac) {
	        if (sb.length() > 0)
	            sb.append(':');
	        sb.append(String.format("%02x", b));
	    }
	    return sb.toString();

	}
}

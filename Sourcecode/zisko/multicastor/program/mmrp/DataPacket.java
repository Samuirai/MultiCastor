package zisko.multicastor.program.mmrp;

public class DataPacket {
	
	public static byte[] getDataPacket(byte[] source, byte[] destination, byte[] data){
		byte [] packet = new byte[14 + data.length];
		
		for(int i = 0; i < 6; i++){
			packet[i] = destination[i];
			packet[6 + i] = source[i];
		}
		
		packet[12] = (byte) 0;
		packet[13] = (byte) data.length; 
 		
		for(int i = 0; i < data.length; i++){
			packet[14 + i] = data[i];
		}
		
		return packet;
	}
}

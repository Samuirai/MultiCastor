package zisko.multicastor.program.mmrp;

public class DataPacket {
	
	public static byte[] getDataPacket(byte[] source, byte[] destination, byte[] data){
		byte [] packet = new byte[14 + data.length];
		
		for(int i = 0; i < 6; i++){
			packet[i] = destination[i];
			packet[6 + i] = source[i];
		}
		
		// FH Did Length in Byte 12 & 13
		packet[12] = (byte) (data.length/255);
		packet[13] = (byte) (data.length%255); 
		
		for(int i = 0; i < data.length; i++){
			packet[14 + i] = data[i];
		}
		
		return packet;
	}
}

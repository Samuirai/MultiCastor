package zisko.multicastor.program.mmrp;

/**
 * Build the MMRPPacket
 *
 */
public class MMRPPacket {
	
	private static final byte[] type = {(byte) 0x88,(byte) 0xf6};
	private static final byte[] destination = {(byte)0x01,(byte)0x80,(byte)0xc2,(byte)0x00,(byte)0x00,(byte)0x20};
	private static final byte protocolVersion = (byte) 1;
	private static final byte attributeLength = (byte) 6;
	private static final byte leaveAll = (byte) 0x20;
	private static final byte noLeaveAll = (byte) 0x00;
	private static final byte numberOfValues = (byte) 1;
	private static final byte attributeType = (byte) 2;
	private static final byte[] endmark = {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00};
	
	private static final int joinIn = 1;
	private static final int in = 2;
	private static final int joinEmpty = 3;
	private static final int empty = 4;
	private static final int leave = 5;
	
	public static byte[] getJoinIn(byte[] source, byte[] firstValue){
		return buildMMRPPacket(source, firstValue, MMRPPacket.joinIn, false);
	}
	
	public static byte[] getIn(byte[] source, byte[] firstValue){
		return buildMMRPPacket(source, firstValue, MMRPPacket.in, false);
	}
	
	public static byte[] getJoinEmpty(byte[] source, byte[] firstValue){
		return buildMMRPPacket(source, firstValue, MMRPPacket.joinEmpty, false);
	}
	
	public static byte[] getEmpty(byte[] source, byte[] firstValue){
		return buildMMRPPacket(source, firstValue, MMRPPacket.empty, false);
	}
	
	public static byte[] getLeave(byte[] source, byte[] firstValue){
		return buildMMRPPacket(source, firstValue, MMRPPacket.leave, false);
	}
	
	public static byte[] getLeaveAll(byte[] source, byte[] firstValue){
		return buildMMRPPacket(source, firstValue, MMRPPacket.leave, true);
	}
	
	private static byte[] buildMMRPPacket(byte[] source, byte[] firstValue, int event, boolean leaveAll){
		byte[] mmrpPacket = new byte[30];
		
		for(int i = 0; i < 6; i++){
			mmrpPacket[i] = MMRPPacket.destination[i];
			mmrpPacket[i+6] = source[i];
			mmrpPacket[i+19] = firstValue[i];
		}
		for(int i = 12; i < 14; i++){
			mmrpPacket[i] = MMRPPacket.type[i-12];
		} 
		mmrpPacket[14] = (byte) MMRPPacket.protocolVersion;
		mmrpPacket[15] = (byte) MMRPPacket.attributeType;
		mmrpPacket[16] = (byte) MMRPPacket.attributeLength;
		
		if(leaveAll){
			mmrpPacket[17] = MMRPPacket.leaveAll;
		}
		else {
			mmrpPacket[17] = MMRPPacket.noLeaveAll;
		}
		
		mmrpPacket[18] = MMRPPacket.numberOfValues;
		mmrpPacket[25] = (byte) (36 * event);
		
		for(int i = 26; i < 30; i++){
			mmrpPacket[i] = MMRPPacket.endmark[i-26];
		}
		
		return mmrpPacket;
	}
}

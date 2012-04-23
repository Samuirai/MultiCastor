package zisko.multicastor.program.data;

import java.net.InetAddress;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

/**
 * Diese Bean-Klasse h�llt Informationen �ber einen Multicast.
 * Objekte von dieser Klasse werden daf�r benutzt Multicast-
 * Informationen innerhalb des Programms zu verteilen.
 * 
 * !!In dieser Klasse ist keinerlei Logik implementiert!!
 */
public class MulticastData {
	
	//********************************************
	// Daten, die gehalten werden
	//********************************************
	private InetAddress groupIp = null;  // InetAddress.getByName()
	private InetAddress sourceIp = null; // siehe dr�ber
	private int udpPort = -1;
	private int packetLength = -1;
	private int ttl = -1;
	private int packetRateDesired = -1; // wird versendet von jannik
	private int packetRateMeasured = -1;
	private Typ typ = Typ.UNDEFINED;
	private int threadID = -1;
	private String hostID = "";
	private boolean active = false;
	private int maxInterruptionTime = -1;
	private int numberOfInterruptions = -1; 
	private int averageInterruptionTime = -1;
	private int packetLossPerSecond = -1;
	private int jitter = -1;
	private int traffic = -1;
	/** Shows if data from multiple Senders is received */
	private senderState senders = senderState.NONE;
	/** Total packets sent/received	 */
	private long packetCount = -1;
	/** Packet source program */
	private Source packetSource = Source.UNDEFINED;
	private byte[] mmrpGroupMac;
	private byte[] mmrpSourceMac;
	// haette ich gerne noch Ende
	// Avg Werte
	private long jitterAvg = -1;
	private long packetRateAvg = -1;
	private long packetLossPerSecondAvg = -1;
	private long trafficAvg = -1;
	
	//V1.5 [FH] added packetLost
	private int packetLostCount = 0;
	
	//********************************************
	// Eigene Datentypen
	//********************************************	
	public enum Typ {
		// 1.5 Added L3_SENDER, L3_RECEIVER, L2_SENDER, L2_RECEIVER
		UNDEFINED, CONFIG, L3_SENDER, L3_RECEIVER, L2_SENDER, L2_RECEIVER
	}
	
	public enum Source {
		UNDEFINED, HIRSCHMANN, MULTICASTOR
	}
	
	public enum senderState {
		NONE, SINGLE, RECENTLY_CHANGED, MULTIPLE, NETWORK_ERROR
	}
	
	//********************************************
	// Constructors
	//********************************************
	public MulticastData(){
	}
	public MulticastData(InetAddress groupIp, InetAddress sourceIp, int udpPort,
			int packetLength, int ttl, int packetRateDesired, boolean active,  Typ typ) {
		this.groupIp = groupIp;
		this.sourceIp = sourceIp;
		this.udpPort = udpPort;
		this.packetLength = packetLength;
		this.ttl = ttl;
		this.packetRateDesired = packetRateDesired;
		this.typ = typ;
		this.active = active;
	}
	public MulticastData(InetAddress groupIp, InetAddress sourceIp, int udpPort, boolean active, Typ typ){
		this.groupIp = groupIp;
		this.sourceIp = sourceIp;
		this.udpPort = udpPort;
		this.active = active;
		this.typ = typ;
	}
	public MulticastData(InetAddress groupIp, InetAddress sourceIp, int udpPort,
			int packetLength, int ttl, int packetRateDesired,
			int packetRateMeasured, Typ typ, int threadID, String hostID,
			boolean active, int numberOfInterruptions,
			int averageInterruptionTime, int packetLossPerSecond, int jitter) {
		this.groupIp = groupIp;
		this.sourceIp = sourceIp;
		this.udpPort = udpPort;
		this.packetLength = packetLength;
		this.ttl = ttl;
		this.packetRateDesired = packetRateDesired;
		this.packetRateMeasured = packetRateMeasured;
		this.typ = typ;
		this.threadID = threadID;
		this.active = active;
		this.numberOfInterruptions = numberOfInterruptions;
		this.averageInterruptionTime = averageInterruptionTime;
		this.packetLossPerSecond = packetLossPerSecond;
		this.jitter = jitter;
	}
	
	public void resetValues(){
		int d = 0;
		
		if(typ == Typ.L3_RECEIVER){
			ttl = d;
			packetRateDesired = d;
			packetLength = d;
		}
		
		maxInterruptionTime = d;
		packetRateAvg = d;
		packetRateMeasured = d;
	//	hostID = "";	// muss man mal noch �berlegen ob man das zur�cksetzten m�chte
		numberOfInterruptions = d;
		averageInterruptionTime = d;
		packetLossPerSecondAvg = d;
		jitter = d;
		jitterAvg = d;
		packetCount = d;
		packetLostCount = d;
		traffic = d;
		trafficAvg = d;
		packetSource = Source.UNDEFINED;
	}

	//********************************************
	// Getters und Setters
	//********************************************
	public InetAddress getGroupIp() {
		return groupIp;
	}

	public void setGroupIp(InetAddress groupIp) {
		this.groupIp = groupIp;
	}

	public InetAddress getSourceIp() {
		return sourceIp;
	}

	public void setSourceIp(InetAddress sourceIp) {
		this.sourceIp = sourceIp;
	}

	public int getUdpPort() {
		return udpPort;
	}

	public void setUdpPort(int udpPort) {
		this.udpPort = udpPort;
	}

	public int getPacketLength() {
		return packetLength;
	}

	public void setPacketLength(int packetLength) {
		this.packetLength = packetLength;
	}

	public int getTtl() {
		return ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	public int getPacketRateDesired() {
		return packetRateDesired;
	}

	public void setPacketRateDesired(int packetRateDesired) {
		this.packetRateDesired = packetRateDesired;
	}

	public int getPacketRateMeasured() {
		return packetRateMeasured;
	}

	public void setPacketRateMeasured(int packetRateMeasured) {
		this.packetRateMeasured = packetRateMeasured;
	}

	public Typ getTyp() {
		return typ;
	}

	public void setTyp(Typ typ) {
		this.typ = typ;
	}

	public int getThreadID() {
		return threadID;
	}

	public void setThreadID(int threadID) {
		this.threadID = threadID;
	}

	public String getHostID() {
		return hostID;
	}

	public void setHostID(String hostID) {
		this.hostID = hostID;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getNumberOfInterruptions() {
		return numberOfInterruptions;
	}

	public void setNumberOfInterruptions(int numberOfInterruptions) {
		this.numberOfInterruptions = numberOfInterruptions;
	}

	public int getAverageInterruptionTime() {
		return averageInterruptionTime;
	}

	public void setAverageInterruptionTime(int averageInterruptionTime) {
		this.averageInterruptionTime = averageInterruptionTime;
	}

	public int getPacketLossPerSecond() {
		return packetLossPerSecond;
	}

	public void setPacketLossPerSecond(int packetLossPerSecond) {
		this.packetLossPerSecond = packetLossPerSecond;
	}

	public int getJitter() {
		return jitter;
	}

	public void setJitter(int jitter) {
		this.jitter = jitter;
	}

	@Override
	public String toString() {
		return "MulticastData [\n\nactive=" + active
				+ "\naverageInterruptionTime=" + averageInterruptionTime
				+ "\ngroupIp=" + groupIp + "\nsourceIp=" + sourceIp + "\nhostID=" + hostID + "\njitter="
				+ jitter + "\nnumberOfInterruptions=" + numberOfInterruptions
				+ "\npacketLength=" + packetLength + "\npacketLossPerSecond="
				+ packetLossPerSecond + "\npacketRateDesired="
				+ packetRateDesired + "\npacketRateMeasured="
				+ packetRateMeasured + "\nthreadID="
				+ threadID + "\nttl=" + ttl + "\ntyp=" + typ + "\nudpPort="
				+ udpPort 
				+ "\nactive= " + active + "]";
	}
	
	public String toStringConsole(){
		if (typ == Typ.L3_SENDER)
			return groupIp + "\t" + udpPort + "\t" + sourceIp + "\t" + packetRateDesired + "\t"
			       + threadID + "\t" + ttl + "\t" + packetCount + "\t" + typ + "\t" + getSenderID() + "\t"; 
		
		return groupIp + "\t" + udpPort + "\t" + sourceIp + "\t" + jitter + "\t" 
			+ numberOfInterruptions + "\t" + packetLossPerSecond + "\t" + packetRateDesired + "\t"
			+ packetRateMeasured + "\t" + threadID + "\t" + ttl + "\t" + packetCount + "\t" + typ + "\t" + getSenderID() + "\t" + packetSource + "\t" + senders;
	}
	
	public String identify(){
		if(typ == Typ.L2_SENDER || typ == Typ.L2_RECEIVER)
			return this.typ + "_" + this.getSenderID() + "_" + getMmrpGroupMacAsString();
		else
			return this.typ + "_" + this.getSenderID() + "_" + getGroupIp();
	}	
	
	public long getPacketRateAvg() {
		return packetRateAvg;
	}
	public void setPacketRateAvg(long packetRateAvg) {
		this.packetRateAvg = packetRateAvg;
	}
	public long getPacketLossPerSecondAvg() {
		return packetLossPerSecondAvg;
	}
	public void setPacketLossPerSecondAvg(long packetLossPerSecondAvg) {
		this.packetLossPerSecondAvg = packetLossPerSecondAvg;
	}
	public long getJitterAvg() {
		return jitterAvg;
	}
	public void setJitterAvg(long jitterAvg) {
		this.jitterAvg = jitterAvg;
	}
	public int getTraffic() {
		return traffic;
	}
	public void setTraffic(int traffic) {
		this.traffic = traffic;
	}
	public long getTrafficAvg() {
		return trafficAvg;
	}
	public void setTrafficAvg(long trafficAvg) {
		this.trafficAvg = trafficAvg;
	}
	public long getPacketCount() {
		return packetCount;
	}
	public void setPacketCount(long packetCount) {
		this.packetCount = packetCount;
	}
	/**
	 * Returns a unique SenderID consisting of the hostID and the threadID
	 * @return
	 */
	public String getSenderID(){
		return hostID + threadID;
	}
	public Source getPacketSource() {
		return packetSource;
	}
	public void setPacketSource(Source packetSource) {
		this.packetSource = packetSource;
	}
	public senderState getSenders() {
		return senders;
	}
	public void setSenders(senderState senders) {
		this.senders = senders;
	}
	public void setMaxInterruptionTime(int maxInterruptionTime) {
		this.maxInterruptionTime = maxInterruptionTime;
	}
	public int getMaxInterruptionTime() {
		return maxInterruptionTime;
	}
	public void addLostPackets(int lost){
		this.packetLostCount += lost;
	}
	public int getLostPackets() {
		return packetLostCount;
	}
	public int getReceivedPackets() {
		return (int)packetCount;
	}
	public byte[] getMmrpGroupMac() {
		return mmrpGroupMac;
	}
	public String getMmrpGroupMacAsString(){
		String s = "";
		if(mmrpGroupMac != null)
			for(int i = 0; i < mmrpGroupMac.length; i++){

				String tmp = Integer.toHexString(mmrpGroupMac[i]);

				//Falls negativer Wert wird fffffXX zurückgegeben deswegen nur XX nehmen
				if(tmp.length() > 2)
					tmp = tmp.substring(tmp.length()-2,tmp.length());
			

				if(tmp.length() == 1)
					tmp = "0"+tmp;

				s+=tmp;
				if(i != (mmrpGroupMac.length-1))
					s+= ":";
			}
		return s;
	}
	
	public byte[] getMMRPFromString(String s) throws Exception{
		byte[] b = new byte[6];
		String substring;
		HexBinaryAdapter h = new HexBinaryAdapter();
		
		for(int begin = 0, end, counter = 0; counter < 6 ; counter++ ){
			end = s.indexOf(":", begin);
			
			if(end != -1)
				substring = (String)s.subSequence(begin, end);
			else
				substring = (String)s.subSequence(begin, s.length());
			
			//Shouldn't happen ;)
			if(substring.length() == 1)
				substring = "0" + substring;
			
		
			b[counter] = h.unmarshal(substring)[0];
			begin = end+1;
		}
		return b;
	}
	public String getMmrpSourceMacAsString(){
		String s = "";
		System.out.println("length: "+mmrpSourceMac.length);
		for(int i = 0; i < mmrpSourceMac.length; i++){
			String tmp = Integer.toHexString((int)mmrpSourceMac[i]);
			
			//Falls negativer Wert wird fffffXX zurückgegeben deswegen nur XX nehmen
			if(tmp.length() > 2)
				tmp = tmp.substring(tmp.length()-2,tmp.length());
			
			if(tmp.length() == 1)
				tmp = "0"+tmp;

			s+=tmp;
			if(i != (mmrpSourceMac.length-1))
				s+= ":";
		}
		return s;
	}
	public void setMmrpGroupMac(byte[] mmrpGroupMac) {
		this.mmrpGroupMac = mmrpGroupMac;
	}
	public byte[] getMmrpSourceMac() {
		return mmrpSourceMac;
	}
	public void setMmrpSourceMac(byte[] mmrpSourceMac) {
		this.mmrpSourceMac = mmrpSourceMac;
	}
}

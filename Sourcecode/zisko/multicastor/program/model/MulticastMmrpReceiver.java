package zisko.multicastor.program.model;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.logging.Level;

import java.util.logging.Logger;

import zisko.multicastor.program.mmrp.*;

import zisko.multicastor.program.data.MulticastData;
import zisko.multicastor.program.interfaces.MulticastThreadSuper;

public class MulticastMmrpReceiver extends MulticastThreadSuper {
	
	/** Wenn auf wahr, lauscht dieser Receiver auf ankommende Pakete. */
	private boolean active = false;
	/** Wird fuer die Fehlerausgabe verwendet. */
	private Logger logger;
	/** Maximale Paketlaenge */
	private final int length = 256;
	/** Byte Array in dem das Paket gespeichert wird. */
	private byte[] buf = new byte[length];
	/** Analysiert ankommende Pakete */
	PacketAnalyzer packetAnalyzer;
	private MMRPReceiver receiver;

	public MulticastMmrpReceiver(MulticastData multicastData, Logger logger) {
		super(multicastData);
		if(logger==null){
			System.out.println("FATAL ERROR - cannot create MulticastReceiver without Logger");
			return;
		}
		if(multicastData==null){
			logger.log(Level.WARNING, "Error while creating MulticastReceiver. MulticastData cannot be empty.");
			return;
		}
		
		try {
			multicastData.setHostID(InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			proclaim(1, "Unable to get Host-Name. Error is: " + e.getMessage());
		}
		
		this.logger = logger;
		packetAnalyzer = new PacketAnalyzer(mcData, logger, length);
		try {
			receiver = new MMRPReceiver(mcData.getMmrpSourceMac(), mcData.getMmrpGroupMac());
		} catch (IOException e) {
			
			proclaim(3, "Could not create Receiver. The interface '" + mcData.getMmrpSourceMacAsString() + "' seems to have "
			+ "no MMRP functionality");
		}
		
		// resets MulticastData Object to avoid default value -1
		//mcData.resetValues();
	}
	
	/**
	 * Methode �ber die die Kommunikation zum MultiCastController realisiert wird.
	 * @param level unterscheidet zwischen Fehlern und Status-Meldungen
	 * @param mssg Die zu sendende Nachricht (String)
	 */
	private void proclaim(int level, String mssg){
		Level l;
		mssg = mcData.identify() + ": " + mssg;
		switch(level){	
			case 1:		l = Level.WARNING;
						break;
			case 2:		l = Level.INFO;
						break;
			default:	l = Level.SEVERE;
		}
		logger.log(l,mssg);
	}

	public void setActive(boolean b) {
		if(b){
			setStillRunning(true);
			// Verhindert eine "recentlyChanged"-Markierung direkt nach dem Starten
			packetAnalyzer.setLastActivated(6);
		}
		active = b;
		mcData.setActive(b);

	}

	public void update() {
		packetAnalyzer.update();
		// damit die Paketlaenge beim naechsten Paket erneut bestimmt werden kann
		//		groessere Pakete fallen immer auf, jedoch sind kleinere Pakete nur so erkennbar
		initializeBuf();
	}

	public void updateMin() {
		packetAnalyzer.updateMin();
	}

	/**
	 * Schreibt lauter Einsen in den Buffer.
	 */
	private void initializeBuf(){
		for(int i = 0; i<buf.length; i++){
			buf[i] = 1;
		}
	}
	
	public void run() {
		
		// Initialisiert den Buffer mit Einsen
		initializeBuf();

		try {
			receiver.registerPath();
		} catch (IOException e) {
			proclaim(3, "Could not register receiver path");
		}
		
		while(active){
			receiver.waitForDataPacketAndGetIt(buf);
			//packetAnalyzer.setTimeout(false);
			packetAnalyzer.analyzePacket(buf);
			initializeBuf();
		}
		
		// Resetted gemessene Werte (SenderID bleibt erhalten, wegen des Wiedererkennungswertes)
		packetAnalyzer.resetValues();
		packetAnalyzer.update();
		packetAnalyzer.updateMin();
		
		// Thread ist beendet
		setStillRunning(false);
		
		try {
			receiver.deregisterPath();
		} catch (IOException e) {
			proclaim(3, "Could not deregister receiver path");
		}
	}

}

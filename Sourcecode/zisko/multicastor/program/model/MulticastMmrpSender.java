package zisko.multicastor.program.model;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import zisko.multicastor.program.controller.MulticastController;
import zisko.multicastor.program.data.MulticastData;
import zisko.multicastor.program.data.MulticastData.senderState;
import zisko.multicastor.program.interfaces.MulticastThreadSuper;
import zisko.multicastor.program.mmrp.*;

public class MulticastMmrpSender extends MulticastThreadSuper{

	/** Wenn auf wahr, lauscht dieser Sender auf ankommende Pakete. */
	private boolean isSending = false;
	/** Wird fuer die Fehlerausgabe verwendet. */
	private Logger logger;
	private PacketBuilder myPacketBuilder;
	
	private int totalPacketCount			= 0;
	private int resetablePcktCnt			= 0;
	private int cumulatedResetablePcktCnt	= 0;
	private int packetRateDes;

	private MulticastController mCtrl;
	private MMRPSender sender;

	public MulticastMmrpSender(MulticastData multicastData, Logger logger, MulticastController multiCtrl) {
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
		this.mCtrl = multiCtrl;
		this.myPacketBuilder 		= new PacketBuilder(mcData);
		this.packetRateDes			= mcData.getPacketRateDesired();
		try {
			this.sender					= new MMRPSender(mcData.getMmrpSourceMac(), mcData.getMmrpGroupMac());
		} catch (IOException e) {
			proclaim(3, "Could not Create MMRP Sender");
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

	public void setActive(boolean active) {
		System.out.println("Set Active: " + active);
		isSending = active;
		mcData.setActive(false);
		if(active) {
			//Setzen der ThreadID, da diese evtl.
			//im Controller noch einmal ge�ndert wird
			myPacketBuilder.alterThreadID(mcData.getThreadID());
			setStillRunning(true);
			proclaim(2, "MultiCast-Sender activated");
		}else{
			proclaim(2, "MultiCast-Sender deaktivated" );
		}
	}

	/**
	 * Aktualisiert das MultiCastData-Objekt
	 * und resetet den internen Paket-Counter
	 */
	public void update(){
		mcData.setPacketRateMeasured(resetablePcktCnt);
		mcData.setPacketCount(totalPacketCount);
		mcData.setTraffic(resetablePcktCnt*mcData.getPacketLength());
		cumulatedResetablePcktCnt =+ resetablePcktCnt;
		resetablePcktCnt = 0;
	}

	/**
	 * Aktualisiert das MultiCastData-Objekt.
	 * Dieser Counter summiert die gemessene Paketrate,
	 * die mit der update()-Funktion ins MultiCastData-Objekt geschrieben wird.
	 * Diese Summe wird bei jedem Aufruf resetet.
	 */
	public void updateMin(){
		mcData.setPacketRateAvg(cumulatedResetablePcktCnt);
		cumulatedResetablePcktCnt = 0;
	}

	public void run() {
		
		try {
			sender.registerPath();
		} catch (IOException e2) {
			proclaim(3, "Could not register path for MMRP Sender");
		}
		
		//Paketzähler auf 0 setzen
		totalPacketCount			= 0;
		resetablePcktCnt			= 0;
		cumulatedResetablePcktCnt	= 0;
		
		int ioExceptionCnt = 0;
		
		//Misst wie lange er sendt um die Paketrate zu erhalten
		//und sleept den Rest der Sekunde
		long endTime	= 0,
			 timeLeft	= 0;
		
		while(isSending){
			//Sleep wenn noch etwas von der letzten Sekunde �brig
			timeLeft = endTime-System.nanoTime();
			if(timeLeft>0)	try{
								Thread.sleep(timeLeft/1000000, (int) (timeLeft%1000000));
							}catch(InterruptedException e){
								proclaim(1, "Sleep after sending with method PEAK failed: " + e.getMessage());
							}
			endTime	  = System.nanoTime() + 1000000000;	//Plus 1s (in ns)
			do{
				System.out.println("hier");
				try{
					sender.sendDataPacket(myPacketBuilder.getPacket());
					if(totalPacketCount<65535)	totalPacketCount++;
					else						totalPacketCount = 0;
					resetablePcktCnt++;
					
/*					if(ioExceptionCnt != 0){
						mcData.setSenders(senderState.SINGLE);
						proclaim(2, "Sender is working again");
						JOptionPane.showMessageDialog(new JFrame(), "Sender is working again");
						ioExceptionCnt = 0;
					}*/
					
				//Hier die richtige Sendenexception Einfügen 
				//Sonst IOException
				}catch(Exception e1){
/*					Object[] options = { "Stop Sender", "Reattemp to connect"};

					 mcData.setSenders(senderState.NETWORK_ERROR);
					 
					if(ioExceptionCnt == 0)
						proclaim(1, "Problem with sending. Trying to reconnect...");

					if(ioExceptionCnt == 10)
						 if( JOptionPane.showOptionDialog(null, "Sender is still not working correctly.\n"+
									"Because sending via " +mcData.identify()+ " is not reachable " , "Sending warning",
									JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
									null, options, options[0]) == 0){
							 isSending = false;
							 this.setActive(false);
							 mCtrl.stopMC(mcData);
						 }else
							 ioExceptionCnt = 0;
					
					ioExceptionCnt ++;*/
					e1.printStackTrace();
					System.exit(1);
				}
			}while( ((totalPacketCount%packetRateDes)!=0) && isSending);
		}
		try {
			sender.deregisterPath();
		} catch (IOException e) {
			proclaim(3, "Could not deregister Path");
		}
		proclaim(2, totalPacketCount + " packets send in total");
		
		//Counter reseten
		totalPacketCount = 0;
		resetablePcktCnt = 0;
		cumulatedResetablePcktCnt = 0;
		update();
		updateMin();
		setStillRunning(false);
	}

}

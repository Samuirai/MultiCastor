package zisko.multicastor.program.model;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import zisko.multicastor.program.controller.MulticastController;
import zisko.multicastor.program.data.MulticastData;
import zisko.multicastor.program.data.MulticastData.senderState;
import zisko.multicastor.program.interfaces.MulticastSenderInterface;
import zisko.multicastor.program.interfaces.MulticastThreadSuper;
import zisko.multicastor.program.mmrp.*;

/**
 * Die MultiCastMmrpSender-Klasse kümmert sich um das tatsächliche Senden der
 * Multicast-Objekte über das Netzwerk per MMRP Protokoll.
 * Sie extended{@link MulticastThreadSuper}, ist also ein Runnable. 
 * 
 * Ein MultiCastMmrpSender hat eine Grundkonfiguration, 
 * die nicht mehr abgeändert werden kann, wie zum
 * Beispiel die gesetzten MACs. Soll diese Grundkonfiguration geändert werden,
 * muss eine neue Instanz de Klasse gebildet werden. Das Erleichtert die
 * nachträgliche Analyse, Da das Objekt eindeutig einem "Test" zuordnungsbar
 * ist.
 * 
 * @author Filip Haase
 * @author Christopher Westphal
 * 
 */
public class MulticastMmrpSender extends MulticastThreadSuper implements MulticastSenderInterface{

	/** Wenn auf wahr, sendet dieser Sender. */
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

	/**
	 * Einziger Konstruktor der Klasse (Sieht man vom Konstruktor der
	 * Superklasse ab). Im Konstruktor wird die hostID gesetzt (entspricht dem
	 * hostnamen des Geräts), der {@link MMRPSender} initialisiert
	 * und das Datenpaket mit dem {@link PacketBuilder} erstellt.
	 * 
	 * @param mcBean
	 *            Das {@link MulticastData}-Object, dass alle f�r den Betrieb
	 *            n�tigen Daten enth�lt.
	 * @param logger
	 *            Eine {@link Queue}, �ber den der Sender seine Ausgaben an
	 *            den Controller weitergibt.
	 *          
	 * @param MultiCtrl
	 * 			  Eine Referenz auf den entsprechenden{@link MulticastController}
	 * 			  damit MulticastStröme ggf. richtig gestoppt werden kann
	 */
	public MulticastMmrpSender(MulticastData multicastData, Logger logger, MulticastController multiCtrl) throws IOException{
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
			throw new IOException();
		}	
	}
	
	/**
	 * Methode über die die Kommunikation zum MultiCastController realisiert wird.
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

	/**
	 * Wird der Methode true übergeben, startet der Multicast 
	 * zu senden. 
	 * 
	 * Wird der Methode false übergeben, stoppt 
	 * sie das senden der Multicasts.
	 * 
	 * @param active
	 *            boolean
	 */
	public void setActive(boolean active) {
		isSending = active;
		mcData.setActive(active);
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

	/**
	 * hier geschieht das eigentliche Senden. Beim Starten des Threads wird
	 * probiert, denn Mmrp Pfad zu registrieren. Gelingt dies nicht, wird ein
	 * Fehler ausgegeben und das Senden wird garnicht erst gestartet. Gelingt
	 * das registrieren, wird so lange gesendet, bis setActive(false) aufgerufen
	 * wird.
	 */
	public void run() {
		
		try {
			sender.registerPath();
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
					try{
						sender.sendDataPacket(myPacketBuilder.getPacket());
						//System.out.println("Sending packet " + totalPacketCount );
						if(totalPacketCount<65535)	totalPacketCount++;
						else						totalPacketCount = 0;
						resetablePcktCnt++;
						
					}catch(Exception e1){
						proclaim(2, "Problem with sending");
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
		} catch (IOException e2) {
			proclaim(3, "Could not register path for MMRP Sender, stopping it againg");
			isSending = false;
			this.setActive(false);
			mCtrl.stopMC(mcData);
		}
	}

}

package zisko.multicastor.program.model;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import zisko.multicastor.program.controller.MulticastController;
import zisko.multicastor.program.data.MulticastData;
import zisko.multicastor.program.interfaces.MulticastSenderInterface;
import zisko.multicastor.program.interfaces.MulticastThreadSuper;
import zisko.multicastor.program.lang.LanguageManager;
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
 */
public class MulticastMmrpSender extends MulticastThreadSuper implements MulticastSenderInterface{

	/**
	 * Language Manager ist wichtig f�r die multi Language Unterst�tzung 
	 */
	private LanguageManager lang = LanguageManager.getInstance();
	
	/** Wenn auf wahr, sendet dieser Sender. */
	private boolean isSending = false;
	/** Wird fuer die Fehlerausgabe verwendet. */
	private Logger logger;
	
	/** baut das ByteArray f�r die Pakete*/
	private PacketBuilder myPacketBuilder;
	
	/** Anzahl aller Pakete */
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
			System.out.println(lang.getProperty("error.mr.logger"));
			return;
		}
		if(multicastData==null){
			logger.log(Level.WARNING, lang.getProperty("error.mr.mcdata"));
			return;
		}
		
		try {
			multicastData.setHostID(InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			proclaim(1, lang.getProperty("message.getHostname") + " " + e.getMessage());
		}
		
		this.logger = logger;
		this.mCtrl = multiCtrl;
		this.myPacketBuilder 		= new PacketBuilder(mcData);
		this.packetRateDes			= mcData.getPacketRateDesired();
		try {
			this.sender					= new MMRPSender(mcData.getMmrpSourceMac(), mcData.getMmrpGroupMac());
		} catch (IOException e) {
			proclaim(3, lang.getProperty("message.mmrpSender"));
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
			myPacketBuilder.alterRandomID(mcData.getRandomID());
			setStillRunning(true);
			proclaim(2, lang.getProperty("message.mcSenderActivated"));
		}else{
			proclaim(2, lang.getProperty("message.mcSenderDeactivated"));
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
									proclaim(1, lang.getProperty("message.sleapPeak") + " " + e.getMessage());
								}
				endTime	  = System.nanoTime() + 1000000000;	//Plus 1s (in ns)
				do{
					try{
						sender.sendDataPacket(myPacketBuilder.getPacket());
						if(totalPacketCount<65535)	totalPacketCount++;
						else						totalPacketCount = 0;
						resetablePcktCnt++;
						
					}catch(Exception e1){
						proclaim(2, lang.getProperty("message.problemSending"));
					}
				}while( ((totalPacketCount%packetRateDes)!=0) && isSending);
			}
			try {
				sender.deregisterPath();
			} catch (IOException e) {
				proclaim(3, "Could not deregister Path");
			}
			proclaim(2, totalPacketCount + " " + lang.getProperty("message.packetsSendTotal"));
			
			//Counter reseten
			totalPacketCount = 0;
			resetablePcktCnt = 0;
			cumulatedResetablePcktCnt = 0;
			update();
			updateMin();
			setStillRunning(false);
			
		} catch (IOException e2) {
			proclaim(3, lang.getProperty("message.registerMmrpSender"));
			isSending = false;
			this.setActive(false);
			mCtrl.stopMC(mcData);
		}
		
	}

}

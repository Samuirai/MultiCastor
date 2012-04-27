package zisko.multicastor.program.model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
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
import zisko.multicastor.program.lang.LanguageManager;


/**
 * Die MultiCastSender-Klasse k�mmert sich um das tats�chliche Senden der
 * Multicast- Objekte �ber das Netzwerk. Sie extended
 * {@link MulticastThreadSuper}, ist also ein Runnable. Ein MultiCastSender hat
 * eine Grundkonfiguration, die nicht mehr abge�ndert werden kann, wie zum
 * Beispiel die gesetzten IPs. Soll diese Grundkonfiguration ge�ndert werden,
 * muss eine neue Instanz der Klasse gebildet werden. Das Erleichtert die
 * n�chtr�gliche Analyse, Da das Objekt eindeutig einem "Test" zuordnungsbar
 * ist.
 * 
 */
public class MulticastSender extends MulticastThreadSuper implements MulticastSenderInterface {
	
	private LanguageManager lang = LanguageManager.getInstance();
	private PacketBuilder myPacketBuilder;

	/**
	 * Variablen f�r die verschiedenen Sendemethoden
	 */
	public static enum sendingMethod {
		/**
		 * Senden unter Volllast, ohne auf die angegebene Paketrate zu achten
		 */
		NO_SLEEP,

		/**
		 * Alle Pakete werden am Anfang der Sekunde gesendet, danach wird der
		 * Thread f�r den Rest der Sekunde in den sleep geschickt
		 */
		PEAK,

		/**
		 * Nach jedem Paket wird eine errechnete Zeitspanne der thread auf sleep
		 * gesetzt (l�sst zur Zeit keine hohen Paketraten zu)
		 */
		SLEEP_MULTIPLE
	};

	private sendingMethod usedMethod = sendingMethod.PEAK;
	private int packetRateDes;

	// Variablen f�r die Verbindung
	private MulticastSocket mcSocket;
	private InetAddress mcGroupIp;
	private InetAddress sourceIp;
	private int udpPort;
	private byte ttl;

	// Variablen f�r das Senden
	private boolean isSending = false;
	private long pausePeriodMs;
	private int pausePeriodNs, totalPacketCount = 0;

	// Variablen f�r Logging
	private Logger messages;
	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss");
	int resetablePcktCnt = 0, cumulatedResetablePcktCnt = 0;

	// V1.5 [FH] Added MulticastController to stop it in case of network error
	private MulticastController mCtrl;

	/**
	 * Einziger Konstruktor der Klasse (Sieht man vom Konstruktor der
	 * Superklasse ab). Im Konstruktor wird die hostID gesetzt (entspricht dem
	 * hostnamen des Ger�ts), der MultiCastSocket initialisiert, das
	 * Network-Interface �ber das die Multicasts gesendet werden sollen
	 * gesetzt und das Datenpaket mit dem {@link PacketBuilder} erstellt.
	 * 
	 * @param mcBean
	 *            Das {@link MulticastData}-Object, dass alle f�r den Betrieb
	 *            n�tigen Daten enth�lt.
	 * @param _messages
	 *            Eine {@link Queue}, �ber den der Sender seine Ausgaben an
	 *            den Controller weitergibt.
	 */
	public MulticastSender(MulticastData mcBean, Logger _logger) {
		super(mcBean);

		// Den hostName setzen
		try {
			mcData.setHostID(InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			proclaim(1, lang.getProperty("error.network.noHostName")+ "\n" + e.getMessage());
		}

		// �brige Variablen initialisieren
		messages = _logger;
		myPacketBuilder = new PacketBuilder(mcData);
		udpPort = mcData.getUdpPort();
		ttl = (byte) mcData.getTtl();
		packetRateDes = mcData.getPacketRateDesired();

		mcGroupIp = mcData.getGroupIp();
		sourceIp = mcData.getSourceIp();

		// einen MultiCastSocket initiieren
		try {
			mcSocket = new MulticastSocket(udpPort);
		} catch (IOException e) {
			proclaim(1, "'" + e.getMessage()
					+ "': "+lang.getProperty("message.message.useDefaultPort"));

			try {
				udpPort = 4711; // Default-Port
				mcData.setUdpPort(udpPort);
				mcSocket = new MulticastSocket(udpPort);
			} catch (IOException e2) {
				proclaim(1, lang.getProperty("error.network.settingUDP") + " " + e.getMessage());
				proclaim(-1, lang.getProperty("error.network.noDefaultPort"));
				return;
			}
		}

		// Network-Adapter setzen (Source-IP)
		try {
			mcSocket.setInterface(sourceIp);
		} catch (IOException e) {
			proclaim(
					1,
					lang.getProperty("error.network.sourceIP")+" " + sourceIp + ": "
							+ e.getMessage());
		}

		// L�nge der Pause zwischen den Multicasts in Millisekunden und
		// Nanusekunden setzen
		pausePeriodMs = (int) (1000.0 / (double) mcData.getPacketRateDesired()); // Pause
																					// in
																					// Milisekunden
		pausePeriodNs = (int) (((1000.0 / (double) mcData
				.getPacketRateDesired()) - (double) pausePeriodMs) * 1000000.0); // "Rest"-Pausenzeit(alles
																					// kleiner
																					// als
																					// 1ms)
																					// in
																					// ns
	}

	// V1.5 [FH] Added MulticastController to stop it in case of network error
	/**
	 * Einen MulticastController setzten
	 * @param MulticastController Instanz
	 */
	public void setMCtrl(MulticastController mc) {
		this.mCtrl = mc;
	}

	/**
	 * Wird der Methode true �bergeben, meldet diese sich bei der
	 * Multicastgruppe an und startet zu senden. Wird der Methode false
	 * �bergeben, stoppt sie das senden der Multicasts und veranlasst damit
	 * auch das Abmelden von der Multicastgruppe.
	 * 
	 * @param active
	 *            boolean
	 */
	public void setActive(boolean active) {
		if (active) {
			// Setzen der ThreadID, da diese evtl.
			// im Controller noch einmal ge�ndert wird
			myPacketBuilder.alterThreadID(mcData.getThreadID());
			isSending = true;
			mcData.setActive(true);
			setStillRunning(true);
			proclaim(2, lang.getProperty("message.mcActivated"));
		} else {
			isSending = false;
			mcData.setActive(false);
			proclaim(2, lang.getProperty("message.mcDeactivated"));
		}
	}

	/**
	 * Siehe public void setActive(boolean active). Es l�sst sich zus�tzlich
	 * die Art und Weise angeben, mit der gesendet wird (diese M�glichkeit
	 * wird in der aktuellen Version nur auf Modulebene unterst�tzt und zielt
	 * auf sp�tere Versionen des ByteTools und Testzwecke ab)
	 * 
	 * @param active
	 *            true=senden, false=nicht senden
	 * @param _method
	 *            Art und Weise des Sendens
	 */
	public void setActive(boolean active, sendingMethod _method) {
		usedMethod = _method;
		setActive(active);
	}
	
	/** Multicast Sender stoppen **/
	public void endIt(){
		isSending = false;
		this.setActive(false);
		mCtrl.stopMC(mcData);
	}

	/**
	 * hier geschieht das eigentliche Senden. Beim Starten des Threads wird
	 * probiert, der Multicast-Gruppe beizutreten. Gelingt dies nicht, wird ein
	 * Fehler ausgegeben und das Senden wird garnicht erst gestartet. Gelingt
	 * das Beitreten, wird so lange gesendet, bis setActive(false) aufgerufen
	 * wird. Auf Modulebene kann bei setActive() festgelegt werden, auf welche
	 * Art und Weise gesendet wird. Im Tool wird in dieser Version
	 * ausschlie�lich PEAK verwendet.
	 */
	public void run() {
		// Paketz�hler auf 0 setzen
		totalPacketCount = 0;
		resetablePcktCnt = 0;
		cumulatedResetablePcktCnt = 0;

		// V1.5 [FH] Added for network-fails
		int ioExceptionCnt = 0, lastIOExceptionCnt = 10;

		// Der Multicastgruppe beitreten
		try {
			mcSocket.joinGroup(mcGroupIp);
			proclaim(2, lang.getProperty("message.joinMcPart1")+" " + mcData.getGroupIp()
					+ " "+lang.getProperty("message.joinMcPart1")+" " + usedMethod + ".");
		} catch (IOException e) {
			proclaim(
					1,
					lang.getProperty("message.unableToJoin")+" "
							+ mcData.getGroupIp() + ": " + e.getMessage());
			// �berspringen der Sende-Whileschleife
			this.endIt();
		}

		// TTL setzen
		try {
			mcSocket.setTimeToLive(ttl);
		} catch (IOException e) {
			proclaim(1, lang.getProperty("message.settingTTLErr")+" " + ttl);
		}

		// Multicasts zum resetten des Receivers Senden
		// zur Sicherheit ein paar mehr als eins...
		myPacketBuilder.setReset(true);
		try {
			for (int i = 0; i < 3; i++){
				mcSocket.send(new DatagramPacket(myPacketBuilder.getPacket(),
						mcData.getPacketLength(), mcGroupIp, udpPort));
			}
		} catch (IOException e) {
			proclaim(1, lang.getProperty("message.resetPackets")+" " + e.getMessage()
					+ "\n"+lang.getProperty("message.senderStoped"));
			this.endIt();
		}
		myPacketBuilder.setReset(false);
		resetablePcktCnt += 3;
		totalPacketCount += 3;

		// So lange senden, bis setActive(false) aufgerufen wird
		// Variable 'usedMethod' bestimmt auf welche Art und Weise.
		// Wurde bei setActive keine Methode angegeben, wird standartm��ig
		// die
		// PEAK-Methode verwendet.
		switch (usedMethod) {
		case NO_SLEEP: // ohne sleep, unter Volllast
			while (isSending) {
				try {
					mcSocket.send(new DatagramPacket(myPacketBuilder
							.getPacket(), mcData.getPacketLength(), mcGroupIp,
							udpPort));
					if (totalPacketCount < 65535)
						totalPacketCount++;
					else
						totalPacketCount = 0;
					resetablePcktCnt++;

				} catch (IOException e1) {
					proclaim(1, lang.getProperty("message.whileSending") + e1.getMessage());
				}
			}
			break;
		case PEAK:
			// Misst wie lange er sendt um die Paketrate zu erhalten
			// und sleept den Rest der Sekunde
			long endTime = 0,
			timeLeft = 0;
			while (isSending) {
				// Sleep wenn noch etwas von der letzten Sekunde �brig
				timeLeft = endTime - System.nanoTime();
				if (timeLeft > 0)
					try {
						Thread.sleep(timeLeft / 1000000,
								(int) (timeLeft % 1000000));
					} catch (InterruptedException e) {
						proclaim(1, lang.getProperty("message.sleepPeak") + e.getMessage());
					}
				endTime = System.nanoTime() + 1000000000; // Plus 1s (in ns)
				if (lastIOExceptionCnt <= 10)
					lastIOExceptionCnt++;
				do {
					try {

						mcSocket.send(new DatagramPacket(myPacketBuilder
								.getPacket(), mcData.getPacketLength(),
								mcGroupIp, udpPort));
						if (totalPacketCount < 65535)
							totalPacketCount++;
						else
							totalPacketCount = 0;
						resetablePcktCnt++;

						// V1.5 [FH] added for networkfail foo
						if (ioExceptionCnt != 0) {
							mcData.setSenders(senderState.SINGLE);
							proclaim(2, lang.getProperty("message.senderWorkingAgain"));
							JOptionPane.showMessageDialog(new JFrame(), lang.getProperty("message.senderWorkingAgain"));
							ioExceptionCnt = 0;
						}
						lastIOExceptionCnt = 0;						

					} catch (IOException e1) {
						if (lastIOExceptionCnt >= 10) {// V1.5 [FH] Made this
														// Code work
							Object[] options = { "Stop Sender",
									"Reattemp to connect" };

							mcData.setSenders(senderState.NETWORK_ERROR);

							if (ioExceptionCnt == 1)
								proclaim(1, lang.getProperty("message.problemIp")
										+ "'" + sourceIp + "'. "
										+ lang.getProperty("message.tryReconnect"));

							if (ioExceptionCnt == 10)
								if (JOptionPane.showOptionDialog(null,
										lang.getProperty("message.senderFail")
												+ " " + lang.getProperty("message.senderViaIp")
												+ " (" + sourceIp + ").",
										lang.getProperty("message.sendingWarning"),
										JOptionPane.DEFAULT_OPTION,
										JOptionPane.WARNING_MESSAGE, null,
										options, options[0]) == 0) {
									this.endIt();
								} else
									ioExceptionCnt = 1;

							ioExceptionCnt++;
						}
					}catch (IllegalArgumentException e){}

				} while (((totalPacketCount % packetRateDes) != 0) && isSending);
			}
			break;

		case SLEEP_MULTIPLE: // mit Thread.sleep zwischen jedem Senden
		default:
			while (isSending) {
				try {
					mcSocket.send(new DatagramPacket(myPacketBuilder
							.getPacket(), mcData.getPacketLength(), mcGroupIp,
							udpPort));
					if (totalPacketCount < 65535)
						totalPacketCount++;
					else
						totalPacketCount = 0;
					resetablePcktCnt++;
					Thread.sleep(pausePeriodMs, pausePeriodNs);
				} catch (IOException e1) {
					proclaim(1, lang.getProperty("message.whileSending") + " " + e1.getMessage());
				} catch (InterruptedException e2) {
					proclaim(1, lang.getProperty("message.whileSendingSleepFail") + " " + e2.getMessage());
				}
			}
		}

		// MulticastGruppe verlassen, da Senden von Multicast (im Moment)
		// beendet
		try {
			mcSocket.leaveGroup(mcGroupIp);
		} catch (IOException e) {
			proclaim(1, e.getMessage());
		}
		proclaim(2, totalPacketCount + " " + lang.getProperty("message.packetsSendTotal"));
		// Counter reseten
		totalPacketCount = 0;
		resetablePcktCnt = 0;
		cumulatedResetablePcktCnt = 0;
		update();
		updateMin();
		setStillRunning(false);
	}

	/**
	 * Aktualisiert das MultiCastData-Objekt und resetet den internen
	 * Paket-Counter
	 */
	public void update() {
		mcData.setPacketRateMeasured(resetablePcktCnt);
		mcData.setPacketCount(totalPacketCount);
		mcData.setTraffic(resetablePcktCnt * mcData.getPacketLength());
		cumulatedResetablePcktCnt = +resetablePcktCnt;
		resetablePcktCnt = 0;
	}

	/**
	 * Aktualisiert das MultiCastData-Objekt. Dieser Counter summiert die
	 * gemessene Paketrate, die mit der update()-Funktion ins
	 * MultiCastData-Objekt geschrieben wird. Diese Summe wird bei jedem Aufruf
	 * resetet.
	 */
	public void updateMin() {
		mcData.setPacketRateAvg(cumulatedResetablePcktCnt);
		cumulatedResetablePcktCnt = 0;
	}

	/**
	 * Methode �ber die die Kommunikation zum MultiCastController realisiert
	 * wird.
	 * 
	 * @param level
	 *            unterscheidet zwischen Fehlern und Status-Meldungen
	 * @param mssg
	 *            Die zu sendende Nachricht (String)
	 */
	private void proclaim(int level, String mssg) {
		Level l;
		mssg = mcData.identify() + ": " + mssg;
		switch (level) {
		case 1:
			l = Level.WARNING;
			break;
		case 2:
			l = Level.INFO;
			break;
		default:
			l = Level.SEVERE;
		}
		messages.log(l, mssg);
	}
}
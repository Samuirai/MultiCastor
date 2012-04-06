package zisko.multicastor.program.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.xml.sax.SAXException;

import zisko.multicastor.program.lang.LanguageManager;
import zisko.multicastor.program.model.MulticastLogHandler;
import zisko.multicastor.program.model.WrongConfigurationException;

/**
 * Main-Methode.
 * 
 * @author Thomas Lï¿½der
 */

public class Main {

	public static final long MIN_MAX_HEAP = 32 * 1024 * 1024;
	public static long REAL_MAX_HEAP;
	private static LanguageManager lang=LanguageManager.getInstance();
	
	/**
	 * Initialisiert den MulticastController sowie die GUI und liest die
	 * Parameter ein, die dem Programm übegeben wurden und startet den
	 * entsprechenden Programmteil.
	 * 
	 * @param args
	 *            Ein Feld aus Strings, das die Parameter enthï¿½lt, die dem
	 *            Programm in der Kommandozeile ï¿½bergeben wurden.
	 */

	public static void main(String[] args) {

		Main.REAL_MAX_HEAP = Runtime.getRuntime().maxMemory();
		
		// Initialisierung
		ViewController gui;
		MulticastController controller;
		MulticastLogHandler consoleHandlerWithGUI;
		MulticastLogHandler consoleHandler;

		// Erstellen und Einrichten vom Logger
		Logger logger = Logger.getLogger("zisko.multicastor.program.controller.main");
		logger.setUseParentHandlers(false);
		logger.setLevel(Level.FINEST);
		
		//TODO @FH Hier passt die kommentierung nicht mehr. [JT]
		// Hauptprogrammteil - Parameter aus der Kommandozeile auswerten
		// V1.5 [FH]
		if (Main.REAL_MAX_HEAP < Main.MIN_MAX_HEAP) {
			
			consoleHandler = new MulticastLogHandler();
			consoleHandler.setLevel(Level.FINEST);
			logger.addHandler(consoleHandler);
			
			logger.severe(lang.getProperty("error.memory.part1")+" " + Main.REAL_MAX_HEAP/(1024*1024)
					+ " MB. "+lang.getProperty("error.memory.part2")+" " + Main.MIN_MAX_HEAP/(1024*1024)
					+ " MB "+lang.getProperty("error.memory.part3"));
			
			if (args.length == 0 || !args[0].equals("-g"))
				JOptionPane.showMessageDialog(new JFrame(), lang.getProperty("error.memory.part1")+" " + Main.REAL_MAX_HEAP/(1024*1024)
						+ " MB. "+lang.getProperty("error.memory.part2")+" " + Main.MIN_MAX_HEAP/(1024*1024)
						+ " MB "+lang.getProperty("error.memory.part3"), lang.getProperty("error.memory.title"), JOptionPane.WARNING_MESSAGE);
			logSystemInformation(logger);
			System.exit(1);
		} else if (args.length == 0) {
			gui = new ViewController();
			controller = new MulticastController(gui, logger);
			gui.initialize(controller);

			// Handler fï¿½r formatierte Ausgabe in der Konsole- und GUI-Konsole
			consoleHandlerWithGUI = new MulticastLogHandler(gui);
			consoleHandlerWithGUI.setLevel(Level.FINEST);
			logger.addHandler(consoleHandlerWithGUI);
			logSystemInformation(logger);
			logger.info(lang.getProperty("logger.info.startWithGui"));

			controller.loadDefaultMulticastConfig();
			// TODO [MH] Hier GUIConfig laden
			
			for (Handler h : logger.getHandlers()) {
				h.close();
			}
		} else if (args[0].equals("-g")) {
			// System.out.println("Parameter -g mitgegeben");
			if (args.length == 1) {
				System.out.println(lang.getProperty("error.config.notSpecified"));
			} else {
				// File checkfile = new File(args[1]);
				// if(checkfile.exists()) {
				// xml_parser = new xmlParser();

				String pfad = args[1];
				try {
					controller = new MulticastController(null,logger,4000);
					// System.out.println("Parsing");
					// Handler fï¿½r formatierte Ausgabe in der Konsole
					consoleHandler = new MulticastLogHandler();
					consoleHandler.setLevel(Level.FINEST);
					logger.addHandler(consoleHandler);
					logSystemInformation(logger);
					logger.info(lang.getProperty("logger.info.startNoGui"));

					controller.loadConfigWithoutGUI(pfad);

				} catch (FileNotFoundException e) {
					System.out
							.println(lang.getProperty("error.config.notSpecified"));
					// e.printStackTrace();
					System.exit(0);
				} catch (SAXException e) {
					System.out
							.println(lang.getProperty("error.config.wrongFormat"));
					// e.printStackTrace();
					System.exit(0);
				} catch (IOException e) {
					System.out.println("IOE");
					// e.printStackTrace();
					System.exit(0);
				} catch (WrongConfigurationException e) {
					System.out
							.println(lang.getProperty("error.config.wrongFormat"));
					e.printStackTrace();
					System.exit(0);
				}
				for (Handler h : logger.getHandlers()) {
					h.close();
				}
			}
		} else if (args[0].equals("-h")) {
			// System.out.println("Parameter -h mitgegeben");
			System.out.println(lang.getProperty("console.helptext"));
		} else if (args.length != 0) {
			// TODO Was fuern checkfile ist denn gemeint? (MH)
			File checkfile = new File(args[0]);
			if (checkfile.exists()) {
				gui = new ViewController();
				controller = new MulticastController(gui, logger);
				gui.initialize(controller);

				// Handler fï¿½r formatierte Ausgabe in der Konsole- und
				// GUI-Konsole
				consoleHandlerWithGUI = new MulticastLogHandler(gui);
				consoleHandlerWithGUI.setLevel(Level.FINEST);
				logger.addHandler(consoleHandlerWithGUI);
				logSystemInformation(logger);
				logger.info(lang.getProperty("logger.info.startGuiFile"));

				controller.loadMulticastConfig(args[0], true);
				// TODO [MH] Hier GUIConfig laden 

				for (Handler h : logger.getHandlers()) {
					h.close();
				}
			} else {
				// System.out.println("Falscher Parameter mitgegeben");
				System.out.println(lang.getProperty("error.invalidParameter"));
			}
		}
	}
	
	/**
	 * Method will log some basic information about the system
	 * @param logger the current logger
	 */
	private static void logSystemInformation(Logger logger){
		//Systeminformationen loggen
		try {
			logger.info("Hostname: "+java.net.InetAddress.getLocalHost().getHostName());
			logger.info("Hostadress: "+java.net.InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e1) {
			logger.info("Unable to get hostname and hostadress");
		}
		logger.info("Username: "+System.getProperty("user.name")+"; User location: "+System.getProperty("user.region")+" - "+System.getProperty("user.timezone"));
		logger.info("Java version: "+System.getProperty("java.version")+"; Java vendor: "+System.getProperty("java.vendor"));
		logger.info("File encoding: "+System.getProperty("file.encoding"));
	}
}

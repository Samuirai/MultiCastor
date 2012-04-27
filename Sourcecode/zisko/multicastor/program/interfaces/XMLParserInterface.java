package zisko.multicastor.program.interfaces;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import org.xml.sax.SAXException;

import zisko.multicastor.program.data.GUIData;
import zisko.multicastor.program.data.MulticastData;
import zisko.multicastor.program.model.WrongConfigurationException;


public interface XMLParserInterface
{

	/** Liest eine XML-Konfigurationsdatei ein.
	 * @param pfad Ort, an dem die Konfigurationsdatei liegt
	 * @param v1 Enth�lt nach dem Laden alle Multicast-Eintraege der XML-Datei
	 * @throws IOException
	 * @throws SAXException
	 * @throws FileNotFoundException
	 */
	public void loadMultiCastConfig(String path, Vector<MulticastData> v) throws SAXException, FileNotFoundException, IOException, WrongConfigurationException;
	
	/**Liest die Default ULD Konfiguration und Standartwerte aus dem JAR-File
	 * @param v1
	 * Vektor aus Multicast Konfigurationseinstellungen
	 * @param v2
	 * Vektor aus GUI Konfigurationseinstellungen
	 * @throws IOException
	 * @throws SAXException
	 */
	public void loadDefaultULD(Vector<MulticastData> v1) throws IOException, SAXException, WrongConfigurationException;
	
	/** Speichert die get�tigten Konfigurationen in einer XML-Datei ab.
	 * @param pfad
	 * Der Ort, an dem die Datei gespeichert werden soll
	 * @param v
	 * Vektor aus Multicast Konfigurationseinstellungen
	 * @throws IOException
	 */
	public void saveMulticastConfig(String path, Vector<MulticastData> v) throws IOException;

	
	/** Speichert die get�tigten GUI Konfigurationen in einer XML-Datei ab.
	 * @param pfad
	 * Der Ort, an dem die Datei gespeichert werden soll
	 * @param data
	 * GUIData mit GUI Daten
	 * @throws IOException
	 */
	public void saveGUIConfig(String p, GUIData data) throws IOException; // [FF] GUI Config Zeug

	/** l�dt die GUI Konfigurationen aus einer XML-Datei.
	 * @param pfad
	 * Der Ort, an dem die Datei liegt 
	 * @param data
	 * GUIData mit GUI Daten
	 * @throws IOException
	 */
	public void loadGUIConfig(String string, GUIData data) throws SAXException, FileNotFoundException, IOException, WrongConfigurationException;


}

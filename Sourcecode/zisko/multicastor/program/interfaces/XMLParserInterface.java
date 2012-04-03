package zisko.multicastor.program.interfaces;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import org.xml.sax.SAXException;

import zisko.multicastor.program.data.MulticastData;
import zisko.multicastor.program.data.UserInputData;
import zisko.multicastor.program.data.UserlevelData;
import zisko.multicastor.program.model.WrongConfigurationException;


public interface XMLParserInterface
{

	/** Liest eine XML-Konfigurationsdatei ein.
	 * @param pfad Ort, an dem die Konfigurationsdatei liegt
	 * @param v1 Enthält nach dem Laden alle Multicast-Eintraege der XML-Datei
	 * @throws IOException
	 * @throws SAXException
	 * @throws FileNotFoundException
	 */
	public void loadMultiCastConfig(String path, Vector<MulticastData> v) throws SAXException, FileNotFoundException, IOException, WrongConfigurationException;
	
	// TODO [MH] check if tbr
//	/** Auslesen einer XML-Konfigurationsdatei für den User und aus der Kommandozeile
//	 * @param pfad
//	 * Ort, an dem die Konfigurationsdatei liegt
//	 * @param v1
//	 * MultiCastData enthält Multicast Konfigurationseinstellungen
//	 * @param v2
//	 * UserLevelData enthält persönliche Einstellungen des Users, zB. welche GUI Elemente angezeigt werden sollen
//	 * @throws SAXException
//	 * @throws FileNotFoundException
//	 * @throws IOException
//	 */
//	public void loadConfig( String pfad, Vector<MulticastData> v1, Vector<UserlevelData> v2 ) throws SAXException, FileNotFoundException, IOException, WrongConfigurationException;
	
	/**Liest die Default ULD Konfiguration und Standartwerte aus dem JAR-File
	 * @param v1
	 * Vektor aus Multicast Konfigurationseinstellungen
	 * @param v2
	 * Vektor aus GUI Konfigurationseinstellungen
	 * @throws IOException
	 * @throws SAXException
	 */
	public void loadDefaultULD(Vector<MulticastData> v1, Vector<UserlevelData> v2) throws IOException, SAXException, WrongConfigurationException;
	
	/** Speichert die getätigten Konfigurationen in einer XML-Datei ab.
	 * @param pfad
	 * Der Ort, an dem die Datei gespeichert werden soll
	 * @param v
	 * Vektor aus Multicast Konfigurationseinstellungen
	 * @throws IOException
	 */
	public void saveMulticastConfig(String path, Vector<MulticastData> v) throws IOException;

	//TODO [JT] Check if tbr 
//	/** Speichert die getätigten Konfigurationen in einer XML-Datei ab.
//	 *  Für Kommandozeile und manuelle Speicherung einer Konfigurationsdatei durch den Benutzer.
//	 * @param pfad
//	 * Der Ort, an dem die Datei gespeichert werden soll
//	 * @param v1
//	 * Vektor aus Multicast Konfigurationseinstellungen
//	 * @param v2
//	 * Vektor aus GUI Konfigurationseinstellungen
//	 * @throws IOException
//	 */
//	public void saveConfig(String pfad, Vector<MulticastData> v1, Vector<UserlevelData> v2) throws IOException;

}

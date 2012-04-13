package zisko.multicastor.program.model;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import sun.nio.cs.ext.MacHebrew;

import zisko.multicastor.program.controller.ViewController.MessageTyp;
import zisko.multicastor.program.data.MulticastData;
import zisko.multicastor.program.data.MulticastData.Typ;
/**
 * Abstrakte Hilfsklasse welche die Netzwerkadapter des Systems ausliest und nach IPv4 und IPv6 sortiert.
 * @author Daniel Becker
 *
 */
public abstract class NetworkAdapter {
	
	public enum IPType {
		IPv4, IPv6, MAC
	}
	/**
	 * Vector welcher alle vorgebenen IPv4 Netzwerkadressen im System h�lt.
	 */
	public static Vector<InetAddress> ipv4Interfaces=new Vector<InetAddress>();
	/**
	 * Vector welcher alle vorgebenen IPv6 Netzwerkadressen im System h�lt.
	 */
	public static Vector<InetAddress> ipv6Interfaces=new Vector<InetAddress>();
	/**
	 * Vector welcher alle vorgebenen MAP Netzwerkadressen im System h�lt.
	 */
	public static Vector<byte[]> macInterfaces=new Vector<byte[]>();
	public static Vector<String> macInterfacesName= new Vector<String>();
	
	/**
	 * Static Block welcher zu Beginn des Programms die Netzwerk Adapter ausliest und auf die beiden Vectoren aufteilt.
	 */
	static{
		Enumeration<NetworkInterface> adapters = null;
		NetworkInterface current=null;
		try {
			adapters = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		while(adapters.hasMoreElements()){
			current = adapters.nextElement();
//			if(current.getDisplayName().contains("Microsoft") || current.getDisplayName().contains("Loop")){
//				continue;
//			}
			Enumeration<InetAddress> addresses = current.getInetAddresses();
			while(addresses.hasMoreElements()){
				InetAddress currentaddress = addresses.nextElement();
				//System.out.println("checking: "+currentaddress.toString().substring(1));
				//if(InputValidator.checkIPv4(currentaddress.toString().substring(1))!=null){
				if(InputValidator.checkIPv4(currentaddress.getHostAddress())!=null){
					ipv4Interfaces.add(currentaddress);
				}
				//System.out.println("checking: "+currentaddress.toString().substring(1).split("%")[0]);
				//if(InputValidator.checkIPv6(currentaddress.toString().substring(1).split("%")[0])!=null){
				//if(InputValidator.checkIPv6(currentaddress.toString().substring(1))!=null){
				if(InputValidator.checkIPv6(currentaddress.getHostAddress().split("%")[0])!=null){
					ipv6Interfaces.add(currentaddress);
				}
			}
			try {
				if(current.getHardwareAddress() != null){
					macInterfaces.add(current.getHardwareAddress());
					macInterfacesName.add(current.getDisplayName());
				}
			} catch (SocketException e) {

			}
		}
	}
	/**
	 * Funktion welche alle vergebenen IPv4 Netzwerkadressen im System als Vector zur�ck gibt.
	 * @return Vector mit IPv4 Adressen
	 */
	public static Vector<InetAddress> getipv4Adapters(){
		return ipv4Interfaces;
	}
	/**
	 * Funktion welche alle vergebenen IPv6 Netzwerkadressen im System als Vector zur�ck gibt.
	 * @return Vector mit IPv6 Adressen
	 */
	public static Vector<InetAddress> getipv6Adapters(){
		return ipv6Interfaces;
	}
	
	/**
	 * Funktion welche die Mac Netzwerkadressen im System als Vector zurück gibt.
	 * @return Vector mit Mac Adressen
	 */
	public static Vector<byte[]> getMacAdapters(){
		return macInterfaces;
	}
	
	public static String getNameToMacAdress(byte[] mac){
		return macInterfacesName.get(macInterfaces.indexOf(mac));
	}
	
	/**
	 * �berpr�ft ob eine bestimmte IP Adresse im System vergeben ist.
	 * @param typ Unterscheidet ob es sich um IPv4 oder IPv6 Adresse handelt.
	 * @param address Adresse welche �berpr�ft werden soll
	 * @return falls die Adresse vergeben ist wird der Index im jeweiligen Vector zur�ckgegeben, ansonsten -1
	 */
	public static int findAddressIndex(String address){
		// TODO [MH] Linux Check
		int ret = -1;
		if(getAddressType(address) == IPType.IPv4){
			for(int i = 0; i < ipv4Interfaces.size() ; i++){
				//System.out.println("comparing index "+i+": \""+ipv4Interfaces.get(i).toString()+"\" against \""+address+"\"");
				if(ipv4Interfaces.get(i).toString().substring(1).equals(address)){
					ret = i;
				}
			}
		} else if(getAddressType(address) == IPType.IPv6) {
			for(int i = 0; i < ipv6Interfaces.size() ; i++) {
				if(ipv6Interfaces.get(i).toString().substring(1).startsWith(address)){
					ret = i;
				}
			}
		}
		return ret;
	}
	
	public static int findAddressIndexMMRP(String address){
		int ret = -1;
		for(int i = 0; i < macInterfaces.size(); i++){
			if(Arrays.toString(macInterfaces.get(i)).equals(address))
				ret = i;
		}
		return ret+1;
	}
	
	public static IPType getAddressType(String address) {
		if (InputValidator.checkMC_IPv4(address) != null || InputValidator.checkIPv4(address) != null) {
			return IPType.IPv4;
		} else if (InputValidator.checkMC_IPv6(address) != null || InputValidator.checkIPv6(address) != null) {
			return IPType.IPv6;
		}
		return null;
	}
	
	public static byte[] getMacToMMRP(String sourceMac) {
		return macInterfaces.get(macInterfacesName.indexOf(sourceMac));
	} 
	
	public static byte[] getMacToMMRP(int sourceMac) {
		return macInterfaces.get(sourceMac);
	}
	
	
	public static Vector<String> getMacAdapterNames() {
		return macInterfacesName;
	}
}

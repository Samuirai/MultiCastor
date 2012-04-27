package zisko.multicastor.program.data;

import java.net.InetAddress;

/**
 * Diese Bean-Klasse h�llt Informationen �ber die GUI Config.
 * 
 * !!In dieser Klasse ist keinerlei Logik implementiert!!
 */
public class GUIData {
	
	//********************************************
	// Daten, die gehalten werden
	//********************************************
	
	public TabState getL3_SENDER() {
		return L3_SENDER;
	}


	public void setL3_SENDER(TabState l3_SENDER) {
		L3_SENDER = l3_SENDER;
	}


	public TabState getL3_RECEIVER() {
		return L3_RECEIVER;
	}


	public void setL3_RECEIVER(TabState l3_RECEIVER) {
		L3_RECEIVER = l3_RECEIVER;
	}


	public TabState getL2_SENDER() {
		return L2_SENDER;
	}


	public void setL2_SENDER(TabState l2_SENDER) {
		L2_SENDER = l2_SENDER;
	}


	public TabState getL2_RECEIVER() {
		return L2_RECEIVER;
	}


	public TabState getPLUS() {
		return PLUS;
	}


	/**
	 * It's not allowed to set the Plus Tab invisible
	 * @param pLUS
	 */
	public void setPLUS(TabState pLUS) {
		if(pLUS!= TabState.invisible)
			PLUS = pLUS;
	}


	public TabState getABOUT() {
		return ABOUT;
	}


	public void setABOUT(TabState aBOUT) {
		ABOUT = aBOUT;
	}


	public void setL2_RECEIVER(TabState l2_RECEIVER) {
		L2_RECEIVER = l2_RECEIVER;
	}


	public String getWindowName() {
		return windowName;
	}


	public void setWindowName(String windowName) {
		this.windowName = windowName;
	}


	public String getLanguage() {
		return Language;
	}


	public void setLanguage(String language) {
		Language = language;
	}

	private TabState L3_SENDER = TabState.visible;
	private TabState L3_RECEIVER = TabState.visible;
	private TabState L2_SENDER = TabState.visible;
	private TabState L2_RECEIVER = TabState.visible;
	private TabState PLUS = TabState.visible;
	private TabState ABOUT = TabState.invisible;
	private String windowName = "MCastor 2.0";
	private String Language = "english";
	
	
	//********************************************
	// Eigene Datentypen
	//********************************************	
	public enum TabState {
		visible, invisible, selected;
		public String toString() {
			return name();
		};
	}
	
	//********************************************
	// Constructors
	//********************************************
	public GUIData(){
	}
	
	
	public void resetValues(){
		this.L3_SENDER = TabState.visible;
		this.L3_RECEIVER = TabState.visible;
		this.L2_SENDER = TabState.visible;
		this.L2_RECEIVER = TabState.visible;
		this.PLUS = TabState.visible;
		this.ABOUT = TabState.invisible;
		this.windowName = "MCastor 2.0";
		this.Language = "english";
	}

	//********************************************
	// Getters und Setters
	//********************************************

	@Override
	public String toString() {
		String message = "";
		message += "L3_SENDER: "+this.L3_SENDER.toString()+"\n"+
				"L3_RECEIVER: "+this.L3_RECEIVER.toString()+"\n"+
				"L2_SENDER: "+this.L2_SENDER.toString()+"\n"+
				"L2_RECEIVER: "+this.L2_RECEIVER.toString()+"\n"+
				"PLUS: "+this.L2_RECEIVER.toString()+"\n"+
				"ABOUT: "+this.L2_RECEIVER.toString()+"\n"+
				"WindowName: "+this.windowName+"\n"+
				"Language: "+this.Language+"\n";
		return message;
	}
	
	public String toStringConsole(){
		String message = "";
		message += "L3_SENDER: "+this.L3_SENDER.toString()+"\n"+
				"L3_RECEIVER: "+this.L3_RECEIVER.toString()+"\n"+
				"L2_SENDER: "+this.L2_SENDER.toString()+"\n"+
				"L2_RECEIVER: "+this.L2_RECEIVER.toString()+"\n"+
				"PLUS: "+this.L2_RECEIVER.toString()+"\n"+
				"ABOUT: "+this.L2_RECEIVER.toString()+"\n"+
				"WindowName: "+this.windowName+"\n"+
				"Language: "+this.Language+"\n";
		return message;
	}
	
	
}

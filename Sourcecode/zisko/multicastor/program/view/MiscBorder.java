package zisko.multicastor.program.view;


import java.awt.Color;
import java.awt.Font;
import java.util.Vector;

import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import zisko.multicastor.program.lang.LanguageManager;
/**
 * Hilfsklasse zum verwalten der Raender fuer Textfelder.
 *
 */
@SuppressWarnings("serial")
public class MiscBorder extends TitledBorder {
	/**
	 * Enum welches bestimmt ob der Rahmen rot, gruen oder neutral gezeichnet werden muss, 
	 * je nach dem ob ein Korrekter Input vorliegt.
	 *
	 */
	public enum BorderType{
		NEUTRAL, TRUE, FALSE
	}
	
	/**
	 * Enum welches bestimmt um welchen Rahmen es sich handelt
	 *
	 */
	public enum BorderTitle{
		L3GROUP, L3SOURCE, PORT, RATE, LENGTH, TTL, L2GROUP, L2SOURCE
	}
	
	private static Vector<TitledBorder> b_neutral = new Vector<TitledBorder>();
	private static Vector<TitledBorder> b_true = new Vector<TitledBorder>();
	private static Vector<TitledBorder> b_false = new Vector<TitledBorder>();
	private static Color enabledColor=new Color(0,175,0);
	private static Color disabledColor=new Color(200,0,0);
	private static Color neutralLineColor=  new Color(240,240,240);
	private static Color neutralTextColor= Color.gray;
	private static Color lineColor=Color.lightGray;
	private static LanguageManager lang;
	private static Font f = MiscFont.getFont(0,12);
	private static Color c = Color.black;
	
	/**
	 * Kontruktor welcher eine LineBorder mit einem Bestimmten Namen erstellt.
	 * @param title Der Name der neuen LineBorder.
	 */
	public MiscBorder(String title) {
		super(title);
		setBorder(new LineBorder(lineColor));
		titleFont = f;
		titleColor = c;
	}
	/**
	 * Statische Funktion zum erstellen von Lineborders.
	 * @param title Name der LineBorder.
	 * @param titleColor ZeichenFarbe der LineBorder.
	 * @param lineColor LinienFarbe der LineBorder.
	 * @return Die LineBorder mit dem bestimmten Namen.
	 */
	private static TitledBorder createBorder(String title, Color titleColor, Color lineColor){
		TitledBorder b = new TitledBorder(new LineBorder(lineColor), title, TitledBorder.LEFT, TitledBorder.TOP, f, titleColor);
		return b;
	}
	
	public static void reloadLanguage(){
		String[] ipv4Names={
			lang.getProperty("miscBorder.ipGroupAddress"),
			lang.getProperty("miscBorder.ipNetworkInterface"),
			lang.getProperty("miscBorder.udpPort"),
			lang.getProperty("miscBorder.packetRate"),
			lang.getProperty("miscBorder.packetLength"),
			lang.getProperty("miscBorder.timeToLive"),
			lang.getProperty("miscBorder.MacGroupAddress"),
			lang.getProperty("miscBorder.NetworkInterface")
		};
		if (b_neutral.size()==0){
			for(int i = 0; i < ipv4Names.length; i++){
				b_neutral.add(createBorder(ipv4Names[i], neutralTextColor, neutralLineColor));
				b_true.add(createBorder(ipv4Names[i], enabledColor, enabledColor));
				b_false.add(createBorder(ipv4Names[i], disabledColor, disabledColor));			
			}	
		}
		else{
			for(int i = 0; i < ipv4Names.length; i++){
				b_neutral.get(i).setTitle(ipv4Names[i]);
				b_true.get(i).setTitle(ipv4Names[i]);
				b_false.get(i).setTitle(ipv4Names[i]);
			}
		}
		
	}
	
	/**
	 * Statische Funktion zum Abrufen einer LineBorder
	 * @param title Name der LineBorder (Titel)
	 * @param bordertype Farbe der Border
	 * @return die erstellte LineBorder
	 */
	public static TitledBorder getBorder(BorderTitle title, BorderType bordertype){
		if (b_neutral.size()==0){
			lang=LanguageManager.getInstance();
			reloadLanguage();
		}
		switch(bordertype){
			case NEUTRAL:
				switch(title){
					case L3GROUP:		return b_neutral.get(0);
					case L3SOURCE: 		return b_neutral.get(1);
					case PORT: 			return b_neutral.get(2);
					case RATE: 			return b_neutral.get(3);
					case LENGTH: 		return b_neutral.get(4);
					case TTL: 			return b_neutral.get(5);
					case L2GROUP: 		return b_neutral.get(6);
					case L2SOURCE: 		return b_neutral.get(7);
				}
				break;
			case TRUE:
				switch(title){
					case L3GROUP: 		return b_true.get(0);
					case L3SOURCE: 		return b_true.get(1);
					case PORT: 			return b_true.get(2);
					case RATE: 			return b_true.get(3);
					case LENGTH: 		return b_true.get(4);
					case TTL: 			return b_true.get(5);
					case L2GROUP: 		return b_true.get(6);
					case L2SOURCE: 		return b_true.get(7);
				}
			break;
			case FALSE:
				switch(title){
					case L3GROUP: 		return b_false.get(0);
					case L3SOURCE: 		return b_false.get(1);
					case PORT: 			return b_false.get(2);
					case RATE: 			return b_false.get(3);
					case LENGTH: 		return b_false.get(4);
					case TTL: 			return b_false.get(5);
					case L2GROUP: 		return b_false.get(6);
					case L2SOURCE: 		return b_false.get(7);
			}
		}
		return null;
	}
	
}

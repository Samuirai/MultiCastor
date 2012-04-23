package zisko.multicastor.program.controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumn;

import com.sun.corba.se.spi.orbutil.fsm.Input;

import zisko.multicastor.program.view.FrameMain;
import zisko.multicastor.program.view.MiscBorder;
import zisko.multicastor.program.view.MiscFont;
import zisko.multicastor.program.view.MiscTableModel;
import zisko.multicastor.program.view.PanelMulticastConfig;
import zisko.multicastor.program.view.PanelMulticastControl;
import zisko.multicastor.program.view.PanelStatusBar;
import zisko.multicastor.program.view.PanelTabbed;
import zisko.multicastor.program.view.PopUpMenu;
import zisko.multicastor.program.view.WideComboBox;
import zisko.multicastor.program.view.MiscBorder.BorderTitle;
import zisko.multicastor.program.view.MiscBorder.BorderType;
import zisko.multicastor.program.view.ReceiverGraph.valueType;
import zisko.multicastor.program.view.SnakeGimmick;
import zisko.multicastor.program.view.SnakeGimmick.SNAKE_DIRECTION;
import zisko.multicastor.program.data.GUIData;
import zisko.multicastor.program.data.MulticastData;
import zisko.multicastor.program.data.UserInputData;
import zisko.multicastor.program.data.MulticastData.Typ;
import zisko.multicastor.program.data.UserlevelData.Userlevel;
import zisko.multicastor.program.lang.LanguageManager;
import zisko.multicastor.program.model.InputValidator;
import zisko.multicastor.program.model.NetworkAdapter;
import zisko.multicastor.program.model.NetworkAdapter.IPType;
/**
 * Steuerungsklasse des GUI
 * @author	Daniel Becker
 *
 */
public class ViewController implements 	ActionListener, MouseListener, ChangeListener, ComponentListener, 
										ListSelectionListener, KeyListener, DocumentListener, ItemListener, 
										ContainerListener, TableColumnModelListener, WindowListener{
	/**
	 * Enum welches angibt um was f�r eine Art von GUI Benachrichtigung es sich handelt.
	 * @author Daniel Becker
	 *
	 */
	public enum MessageTyp {
		INFO, WARNING, ERROR
	}
	/**
	 * Enum welches angibt um was f�r eine Art von GUI Update es sich handelt.
	 * @author Daniel Becker
	 *
	 */
	public enum UpdateTyp{
		UPDATE, INSERT, DELETE
	}
	private SnakeGimmick.SNAKE_DIRECTION snakeDir = SNAKE_DIRECTION.E;
	/**
	 * Referenz zum MulticastController, wichtigste Schnittstelle der Klasse.
	 */
	private MulticastController mc;
	/**
	 * Referenz zum Frame in welchem das MultiCastor Tool angezeigt wird.
	 */
	private FrameMain f;
	/**
	 * 2-Dimensionales Boolean Feld welches angibt in welchem Feld des jeweiligen Konfiguration Panels
	 * eine richtige oder falsche eingabe get�tigt wurde.
	 */
	private boolean[][] input = new boolean[4][6];
	/**
	 * Hilfsvariable welche ben�tigt wird um die gemeinsamen Revceiver Graph Daten 
	 * (JITTER, LOST PACKETS, MEASURED PACKET RATE) von mehreren Mutlicasts zu berechnen.
	 */
	private MulticastData[] graphData;
	/**
	 * Hilfsvariable welche angibt wann die Initialisierung der GUI abgeschlossen ist.
	 */
	private boolean initFinished=false;
	public void setInitFinished(boolean initFinished) {
		this.initFinished = initFinished;
	}
//	/**
//	 * Datenobjekt welches den Input des IPv4 Senders enth�lt.
//	 */
//	private UserInputData inputData_S4;
//	/**
//	 * Datenobjekt welches den Input des IPv6 Senders enth�lt.
//	 */
//	private UserInputData inputData_S6;
//	/**
//	 * Datenobjekt welches den Input des IPv4 Receivers enth�lt.
//	 */
//	private UserInputData inputData_R4;
//	/**
//	 * Datenobjekt welches den Input des IPv6 Receivers enth�lt.
//	 */
//	private UserInputData inputData_R6;
	
	/**
	 * Datenobjekt welches den Input des Layer3 Senders enthaelt.
	 */
	private UserInputData inputData_S3;
	/**
	 * Datenobjekt welches den Input des Layer3 Receivers enthaelt.
	 */
	private UserInputData inputData_R3;
	/**
	 * Datenobjekt welches den Input des Layer2 Senders enthaelt.
	 */
	private UserInputData inputData_S2;
	/**
	 * Datenobjekt welches den Input des Layer2 Receivers enthaelt.
	 */
	private UserInputData inputData_R2;
	
	private LanguageManager lang;
	private GUIData guidata;
	
	public boolean isInitFinished() {
		return initFinished;
	}
	
	/**
	 * Standardkonstruktor der GUI, hierbei wird die GUI noch nicht initialisiert!
	 */
	public ViewController(){
		lang=LanguageManager.getInstance();
	}
	/**
	 * Implementierung des ActionListeners, betrifft die meisten GUI Komponenten.
	 * Diese Funktion wird aufgerufen wenn eine Interaktion mit einer GUI Komponente stattfindet, welche
	 * den ActionListener dieser ViewController Klasse h�lt. Die IF-THEN-ELSEIF Abragen dienen dazu 
	 * die Komponente zu identifizieren bei welcher die Interaktion stattgefunden hat.
	 * @throws NeedRestartException 
	 */
	public void actionPerformed(ActionEvent e) {

		//V1.5: Wenn Draggable Tabbed Pane entfernt wurde
		if(f.isPaneDel())
			f.openPane();
		//V1.5: Wenn "Views" ausgewählt werden
		if(e.getActionCommand().startsWith("open_layer") || e.getActionCommand().equals("open_about")){
			f.getTabpane().openTab(e.getActionCommand());
		}
		
		//TODO Help file �ffnen!
		else if(e.getSource()==f.getMi_help()){
			JOptionPane.showMessageDialog(f, "Leider ist dies noch nicht implementiert");
		}
		
		else if (e.getActionCommand().startsWith("change_lang_to")){
			LanguageManager.setCurrentLanguage(e.getActionCommand().replaceFirst("change_lang_to_", ""));
			f.reloadLanguage();
			f.repaint();
		}
		
		else if(e.getSource()==f.getMi_saveconfig()){
			saveGUIFileEvent();
		}
		
		else if(e.getSource()==f.getMi_saveAllMc()){
			//check if there are any Multicasts to save
			if (mc.getMCs(Typ.L2_RECEIVER).size()+mc.getMCs(Typ.L3_RECEIVER).size()+mc.getMCs(Typ.L2_SENDER).size()+mc.getMCs(Typ.L3_SENDER).size()<1){
				JOptionPane.showMessageDialog(f, lang.getProperty("message.noMcCreated"));
			}
			else{
				saveFileEvent(false);
			}
		}
		
		else if(e.getSource()==f.getMi_saveSelectedMc()){
			//check if there are any Multicasts to save
			if (
					f.getPanel_rec_lay2().getTable().getSelectedRowCount()+
					f.getPanel_rec_lay3().getTable().getSelectedRowCount()+
					f.getPanel_sen_lay2().getTable().getSelectedRowCount()+
					f.getPanel_sen_lay3().getTable().getSelectedRowCount()<1
				)
			{
				JOptionPane.showMessageDialog(f, lang.getProperty("message.noMcSelected"));
			}
			else{
				saveFileEvent(true);
			}
		}
		
		else if(e.getSource()==f.getMi_loadconfig()){
			loadGUIFileEvent();
//			f.getFc_load().toggle();
//			setColumnSettings(getUserInputData(getSelectedTab()), getSelectedTab());
		}
		
		else if(e.getSource()==f.getMi_loadMc()){
			loadFileEvent(false);
		}
		
		else if(e.getSource()==f.getMi_loadAdditionalMc()){
			loadFileEvent(true);
		}
		
		else if(e.getSource()==f.getMi_snake()){
			if(getSelectedTab()!=Typ.UNDEFINED && getSelectedTab()!=Typ.CONFIG){
				if(getFrame().getPanelPart(getSelectedTab()).getPan_graph().runSnake)
					getFrame().getPanelPart(getSelectedTab()).getPan_graph().snake(false);
				else
					getFrame().getPanelPart(getSelectedTab()).getPan_graph().snake(true);
			}
		}
		else if(e.getSource()==f.getMi_exit()){
			closeProgram();
		}
		
		// V1.5: Wenn "Titel aendern" im Menue ausgewaehlt wurde, oeffne einen InputDialog
		else if (e.getSource() == f.getMi_setTitle()) {
			String temp = JOptionPane.showInputDialog(lang.getProperty("message.setNewTitle"), f.getBaseTitle());
			if (temp != null) {
				f.setBaseTitle(temp);
				f.updateTitle();
			}
		}
		
		/* v1.5 Neue Actionlistener fuer Layer3 */
		
		/* Add Button im Config Panel */
		else if(e.getSource() == getPanConfig(Typ.L3_SENDER).getBt_enter()){
			pressBTenter(Typ.L3_SENDER);
		} else if(e.getSource() == getPanConfig(Typ.L3_RECEIVER).getBt_enter()){
			pressBTenter(Typ.L3_RECEIVER);
		} else if(e.getSource() == getPanConfig(Typ.L2_SENDER).getBt_enter()){
			pressBTenter(Typ.L2_SENDER);
		} else if(e.getSource() == getPanConfig(Typ.L2_RECEIVER).getBt_enter()){
			pressBTenter(Typ.L2_RECEIVER);
		} 
		/* Active/Inactive Button im Config Panel */
		else if(e.getSource()==getPanConfig(Typ.L3_SENDER).getTb_active()){
			toggleBTactive(Typ.L3_SENDER);
		} else if(e.getSource()==getPanConfig(Typ.L3_RECEIVER).getTb_active()){
			toggleBTactive(Typ.L3_RECEIVER);
		}else if(e.getSource()==getPanConfig(Typ.L2_SENDER).getTb_active()){
			toggleBTactive(Typ.L2_SENDER);
		} else if(e.getSource()==getPanConfig(Typ.L2_RECEIVER).getTb_active()){
			toggleBTactive(Typ.L2_RECEIVER);
		}
		/* Delete Button im Control Panel */
		else if(e.getSource()==getPanControl(Typ.L3_SENDER).getDelete()){
			pressBTDelete(Typ.L3_SENDER);
		}
		else if(e.getSource()==getPanControl(Typ.L3_RECEIVER).getDelete()){
			pressBTDelete(Typ.L3_RECEIVER);
		}
		else if(e.getSource()==getPanControl(Typ.L2_RECEIVER).getDelete()){
			pressBTDelete(Typ.L2_RECEIVER);
		}
		else if(e.getSource()==getPanControl(Typ.L2_SENDER).getDelete()){
			pressBTDelete(Typ.L2_SENDER);
		}
		/* New Button im Control Panel*/
		else if(e.getSource()==getPanControl(Typ.L3_SENDER).getNewmulticast()){
			pressBTNewMC(Typ.L3_SENDER);
		}
		else if(e.getSource()==getPanControl(Typ.L3_RECEIVER).getNewmulticast()){
			pressBTNewMC(Typ.L3_RECEIVER);
		}
		else if(e.getSource()==getPanControl(Typ.L2_SENDER).getNewmulticast()){
			pressBTNewMC(Typ.L2_SENDER);
		}
		else if(e.getSource()==getPanControl(Typ.L2_RECEIVER).getNewmulticast()){
			pressBTNewMC(Typ.L2_RECEIVER);
		}
		
		else if(e.getActionCommand().equals("hide")){
			hideColumnClicked();
			getTable(getSelectedTab()).getColumnModel().removeColumn(getTable(getSelectedTab()).getColumnModel().getColumn(PopUpMenu.getSelectedColumn()));
		}
		else if(e.getActionCommand().equals("showall")){
			popUpResetColumnsPressed();
		}
		else if(e.getActionCommand().equals("PopupCheckBox")){
			popUpCheckBoxPressed();
		}
		autoSave();
	}
	/**
	 * Funktion welche aufgerufen wird wenn der User Reset View im Popup Menu des Tabellenkopf dr�ckt.
	 * Stellt das urspr�ngliche Aussehen der Tabelle wieder her.
	 */
	private void popUpResetColumnsPressed() {
		getUserInputData(getSelectedTab()).resetColumns();
		getPanTabbed(getSelectedTab()).setTableModel(this, getSelectedTab());
	}
	/**
	 * Funktion die aufgerufen wird wenn eine Checkbox im Popup Menu des Tabellenkopfs gedr�ckt wird.
	 * Wird verwendet zum Einblenden und Ausblenden von Tabellenspalten. 
	 */
	private void popUpCheckBoxPressed() {
		ArrayList<Integer> visibility = new ArrayList<Integer>();
		UserInputData store = new UserInputData();
		store.setColumnOrder(getUserInputData(getSelectedTab()).getColumnOrder());
		JCheckBox[] columns = PopUpMenu.getColumns();
		for(int i = 0 ; i < columns.length ; i++){
			if(columns[i].isSelected()){
				String s = columns[i].getText();
				if(s.equals("STATE")){
					visibility.add(new Integer(0));
				}
				else if(s.equals("ID")){
					visibility.add(new Integer(1));
				}
				else if(s.equals("GRP IP")){
					visibility.add(new Integer(2));
				}
				else if(s.equals("D RATE")){
					visibility.add(new Integer(3));
				}
				else if(s.equals("M RATE")){
					visibility.add(new Integer(4));
				}
				else if(s.equals("Mbit/s")){
					visibility.add(new Integer(5));
				}
				else if(s.equals("PORT")){
					visibility.add(new Integer(6));
				}
				else if(s.equals("SRC IP") || s.equals("LOSS/S")){
					visibility.add(new Integer(7));
				}
				else if(s.equals("#SENT") || s.equals("AVG INT")){
					visibility.add(new Integer(8));
				}
				else if(s.equals("#INT") || s.equals("TTL")){
					visibility.add(new Integer(9));
				}
				else if(s.equals("SRC") || s.equals("LENGTH")){
					visibility.add(new Integer(10));
				}
			}
		}
		store.setColumnVisibility(visibility);
		setColumnSettings(store, getSelectedTab());
	}
	/**
	  * Added einen Container und einen Keylistener zu dem
	  * Component c. Hat die Komponente Kinder, mache das
	  * gleiche mit jedem Child
	  * @param c Container, der die Listener erhalten soll
	  */
	 private void addKeyAndContainerListenerToAll(Component c)
    {
         c.addKeyListener(this);
         //Wenn Container, noch containerListener ben�tigt
         //Au�erdem ben�tigen die Childs den gleichen listener
         if(c instanceof Container) {
              Container cont = (Container)c;
              cont.addContainerListener(this);
              Component[] children = cont.getComponents();
              for(int i = 0; i < children.length; i++){
           	   addKeyAndContainerListenerToAll(children[i]);
              }
         }
    }
	 /**
	  * Funktion welche ein neues Multicast Datenobjekt an den MultiCast Controller weitergibt zur Verarbeitung.
	  * @param mcd neu erstelltes Multicast DatenObjekt welches verarbeitet werden soll.
	  */
	public void addMC(MulticastData mcd){
		mc.addMC(mcd);
		updateTable(mcd.getTyp(), UpdateTyp.INSERT);
	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn sich die Parameter eines Textfelds ge�ndert haben.
	 */
	public void changedUpdate(DocumentEvent arg0) {

		
	}
	/**
	 * Funktion welche ein ge�ndertes Multicast Datenobjekt an den MultiCast Controller weitergibt zur Verarbeitung.
	 * @param mcd Das ge�nderter MulticastData Object.
	 */
	public void changeMC(MulticastData mcd){
		//System.out.println(mcd.toString());
		mc.changeMC(mcd);
		updateTable(mcd.getTyp(),UpdateTyp.UPDATE);
	}
		
	/**
	 * Hilfsfunktion welche den momentanen Input des KonfigurationsPanels in ein Multicast Datenobjekt schreibt.
	 * Wird ben�tigt zum anlegen neuer Multicast sowie zum �ndern vorhandener Multicasts.
	 * @param mcd MulticastData Objet welches ge�ndert werden soll.
	 * @param typ Programmteil aus welchem die Input Daten ausgelesen werden sollen.
	 * @return Ge�nderters Multicast Datenobjekt.
	 */
	private MulticastData changeMCData(MulticastData mcd, MulticastData.Typ typ){
		NetworkAdapter.IPType iptype;
		
		iptype = NetworkAdapter.getAddressType(getPanConfig(typ).getTf_groupIPaddress().getText());

		switch(typ) {
			case L3_SENDER:
				if(!getPanConfig(typ).getTf_groupIPaddress().getText().equals("...")) {
					if (iptype == IPType.IPv4) {
						mcd.setGroupIp(InputValidator.checkMC_IPv4(getPanConfig(typ).getTf_groupIPaddress().getText()));
					} else if (iptype == IPType.IPv6) {
						mcd.setGroupIp(InputValidator.checkMC_IPv6(getPanConfig(typ).getTf_groupIPaddress().getText()));
					}
				}
				if(!(getPanConfig(typ).getCb_sourceIPaddress().getSelectedIndex()==0)){
					mcd.setSourceIp(getPanConfig(typ).getSelectedAddress(typ, iptype));	
				}
				if(!getPanConfig(typ).getTf_udp_packetlength().getText().equals("...")){
					if (iptype == IPType.IPv4) {
						mcd.setPacketLength(InputValidator.checkIPv4PacketLength(getPanConfig(typ).getTf_udp_packetlength().getText()));
					} else if (iptype == IPType.IPv6) {
						mcd.setPacketLength(InputValidator.checkIPv6PacketLength(getPanConfig(typ).getTf_udp_packetlength().getText()));
					}
				}
				if(!getPanConfig(typ).getTf_ttl().getText().equals("...")){
					mcd.setTtl(InputValidator.checkTimeToLive(getPanConfig(typ).getTf_ttl().getText()));
				}
				if(!getPanConfig(typ).getTf_packetrate().getText().equals("...")){
					mcd.setPacketRateDesired(InputValidator.checkPacketRate(getPanConfig(typ).getTf_packetrate().getText()));
				}
				break;
			case L3_RECEIVER:
				if(!getPanConfig(typ).getTf_groupIPaddress().getText().equals("...")) {
					if (iptype == IPType.IPv4) {
						mcd.setGroupIp(InputValidator.checkMC_IPv4(getPanConfig(typ).getTf_groupIPaddress().getText()));
					} else if (iptype == IPType.IPv6) {
						mcd.setGroupIp(InputValidator.checkMC_IPv6(getPanConfig(typ).getTf_groupIPaddress().getText()));
					}
				}
				//V1.5 [FH] Added source IP for receiver (network interface)
				if(!(getPanConfig(typ).getCb_sourceIPaddress().getSelectedIndex()==0)){
					mcd.setSourceIp(getPanConfig(typ).getSelectedAddress(typ, iptype));	
				}
				break;
			case L2_SENDER:
				if(!getPanConfig(typ).getTf_groupIPaddress().getText().equals("...")) {
					if(InputValidator.checkMulticastGroup(getPanConfig(typ).getTf_groupIPaddress().getText())){
						String inputGroupText = getPanConfig(typ).getTf_groupIPaddress().getText();
						byte[] b = new byte[6];
						for(int fromIndex = -1, arrayIndex=0, begin=0; arrayIndex < 6; arrayIndex++){
							begin = fromIndex;
							
							if(arrayIndex < 5)
								fromIndex = inputGroupText.indexOf(':', fromIndex+1);
							else
								fromIndex = inputGroupText.length();

							String tmp = (String)inputGroupText.subSequence(begin+1, fromIndex);
							if(!tmp.equals("")){
								b[arrayIndex] = new Byte((byte)Integer.parseInt(tmp, 16));
							}else{
								b[arrayIndex] = new Byte("0");
							}
						}
						mcd.setMmrpGroupMac(b);
					};
				}
				if(!(getPanConfig(typ).getCb_sourceIPaddress().getSelectedIndex()==0)){
					mcd.setMmrpSourceMac(getPanConfig(typ).getSelectedAddress(typ));	
				}
				if(!getPanConfig(typ).getTf_udp_packetlength().getText().equals("...")){
					mcd.setPacketLength(InputValidator.checkMMRPPacketLength(getPanConfig(typ).getTf_udp_packetlength().getText()));
				}
				if(!getPanConfig(typ).getTf_packetrate().getText().equals("...")){
					mcd.setPacketRateDesired(InputValidator.checkPacketRate(getPanConfig(typ).getTf_packetrate().getText()));
				}
				break;
			case L2_RECEIVER:
				if(!getPanConfig(typ).getTf_groupIPaddress().getText().equals("...")) {
					if(InputValidator.checkMulticastGroup(getPanConfig(typ).getTf_groupIPaddress().getText())){
						String inputGroupText = getPanConfig(typ).getTf_groupIPaddress().getText();
						byte[] b = new byte[6];
						for(int fromIndex = -1, arrayIndex=0, begin=0; arrayIndex < 6; arrayIndex++){
							begin = fromIndex;
							
							if(arrayIndex < 5)
								fromIndex = inputGroupText.indexOf(':', fromIndex+1);
							else
								fromIndex = inputGroupText.length();

							String tmp = (String)inputGroupText.subSequence(begin+1, fromIndex);
							if(!tmp.equals("")){
								b[arrayIndex] = new Byte((byte)Integer.parseInt(tmp, 16));
							}else{
								b[arrayIndex] = new Byte("0");
							}
						}
						mcd.setMmrpGroupMac(b);
					};
				}
				if(!(getPanConfig(typ).getCb_sourceIPaddress().getSelectedIndex()==0)){
					mcd.setMmrpSourceMac(getPanConfig(typ).getSelectedAddress(typ));	
				}
				break;
			default:
				System.out.println("changeMCData(Multicastdata mcd) - ERROR");
		}
		
		if(!getPanConfig(typ).getTf_udp_port().getText().equals("...")){
			mcd.setUdpPort(InputValidator.checkPort(getPanConfig(typ).getTf_udp_port().getText()));
		}
		if(!getPanConfig(typ).getTb_active().getText().equals("multiple")){
			mcd.setActive(getPanConfig(typ).getTb_active().isSelected());
		}
		
		mcd.setTyp(typ);
		return mcd;
	}
	/**
	 * Funktion welche aufgerufen wird wenn der User das Netzwerk Interface in einem Sender �ndert.
	 * @param typ Programmteil in welchem das Netzwerkinterface ge�ndert wurde.
	 */
	private void changeNetworkInterface(Typ typ) {
		PanelMulticastConfig configpart = getPanConfig(typ);
		int selectedIndex = configpart.getTf_sourceIPaddress().getSelectedIndex();
		
		if(selectedIndex != 0) {
			if(typ == Typ.L3_SENDER || typ == Typ.L3_RECEIVER){
				configpart.getPan_sourceIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.L3SOURCE, BorderType.TRUE));
				if(typ==Typ.L3_SENDER){
					input[0][1]=true;
				} else {
					input[1][1]=true;
				}
			}else if(typ == Typ.L2_RECEIVER || typ == Typ.L2_SENDER){
				configpart.getPan_sourceIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.L2SOURCE, BorderType.TRUE));
				if(typ==Typ.L2_SENDER){
					input[2][1]=true;
				} else {
					input[3][1]=true;
				}
			}
		} else if(getSelectedRows(typ).length > 1 && configpart.getCb_sourceIPaddress().getItemAt(0).equals("...")){
			if(typ==Typ.L3_SENDER || typ == Typ.L3_RECEIVER){
				configpart.getPan_sourceIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.L3SOURCE, BorderType.TRUE));
				if(typ==Typ.L3_SENDER){
					input[0][1]=true;
				} else {
					input[1][1]=true;
				}
			}else if(typ == Typ.L2_RECEIVER || typ == Typ.L2_SENDER){
				configpart.getPan_sourceIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.L2SOURCE, BorderType.TRUE));
				if(typ==Typ.L2_SENDER){
					input[2][1]=true;
				} else {
					input[3][1]=true;
				}
			}
		} else {
			switch(typ){
				case L3_SENDER: input[0][1] = false; break;
				case L3_RECEIVER: input[1][1] = false; break;
				case L2_SENDER: input[2][1] = false; break;
				case L2_RECEIVER: input[3][1] = false; break;
			}
			if(typ == Typ.L3_RECEIVER || typ == Typ.L3_SENDER)
				configpart.getPan_sourceIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.L3SOURCE, BorderType.NEUTRAL));
			else if(typ == Typ.L2_RECEIVER || typ == Typ.L2_SENDER)
				configpart.getPan_sourceIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.L2SOURCE, BorderType.NEUTRAL));
		}
		checkInput(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn sich der Inhalt eines Textfelds im Konfigurations Panel �ndert.
	 * Pr�ft ob alle eingaben korrekt sind.
	 * @param typ Programmteil in welchem die Eingaben gepr�ft werden sollen.
	 */
	private void checkInput(Typ typ){

		switch(typ){
			case L3_SENDER:
				if(	
					input[0][0] &&
					input[0][1] &&
					input[0][2] &&
					input[0][3] &&
					input[0][4] &&
					input[0][5]
				){
					getPanConfig(typ).getBt_enter().setEnabled(true);
				} else if(getSelectedRows(getSelectedTab()).length <=1){
					getPanConfig(typ).getBt_enter().setEnabled(false);
				} else {
					getPanConfig(typ).getBt_enter().setEnabled(false);
				}
				break;
			case L3_RECEIVER:
				if (input[1][0] && 
					input[1][1] &&	
					input[1][2]) {
					getPanConfig(typ).getBt_enter().setEnabled(true);
				} else if(getSelectedRows(getSelectedTab()).length <=1){
					getPanConfig(typ).getBt_enter().setEnabled(false);
				} else {
					getPanConfig(typ).getBt_enter().setEnabled(false);
				}
				break;
			case L2_SENDER:
				if( input[2][0] &&
					input[2][1] &&
					input[2][4] &&
					input[2][5]){
							getPanConfig(typ).getBt_enter().setEnabled(true);
						} else if(getSelectedRows(getSelectedTab()).length <=1){
							getPanConfig(typ).getBt_enter().setEnabled(false);
						} else {
							getPanConfig(typ).getBt_enter().setEnabled(false);
						}
				break;
			case L2_RECEIVER:
				if(input[3][0] &&
				   input[3][1]){
					getPanConfig(typ).getBt_enter().setEnabled(true);
				} else if(getSelectedRows(getSelectedTab()).length <=1){
					getPanConfig(typ).getBt_enter().setEnabled(false);
				} else {
					getPanConfig(typ).getBt_enter().setEnabled(false);
				}
				break;
			}
	}
	/**
	 * Funktion welche aufgerufen wird wenn die Eingaben der Textfelder des Konfigurations Panels zur�ckgesetzt werden sollen.
	 * @param typ Programmteil in welchem die Textfelder zur�ckgesetzt werden sollen.
	 */
	private void clearInput(Typ typ){
		if(initFinished){
			getPanConfig(typ).getTf_groupIPaddress().setText("");
			getPanConfig(typ).getTf_udp_port().setText("");
			getPanConfig(typ).getCb_sourceIPaddress().removeItemAt(0);
			getPanConfig(typ).getCb_sourceIPaddress().insertItemAt("", 0);
			getPanConfig(typ).getTf_sourceIPaddress().setSelectedIndex(0);
			if(typ==Typ.L2_SENDER || typ==Typ.L3_SENDER){
				getPanConfig(typ).getTf_ttl().setText("");
				getPanConfig(typ).getTf_packetrate().setText("");;
				getPanConfig(typ).getTf_udp_packetlength().setText("");;
			}
			getPanConfig(typ).getTb_active().setSelected(false);
			getPanConfig(typ).getTb_active().setText(lang.getProperty("button.inactive"));
			getPanConfig(typ).getTb_active().setForeground(Color.red);
			getPanConfig(typ).getTf_groupIPaddress().requestFocusInWindow();
			
			/* easiest method to have the NetworkAdapter and PacketLength field set their states correcly */
			docEventTFgrp(typ);
		}
	}
	/**
	 * Funktion welche aufgerufen wird wenn das Programm beendet wird. 
	 * Sorgt f�r ein sauberes Beenden des Programms. (nicht immer m�glich)
	 */
	private void closeProgram() {
		//System.out.println("Shutting down GUI...");
		f.setVisible(false);
		//System.out.println("Cleanup...");
		mc.destroy();
		//System.out.println("Closing program...");
		System.exit(0);
	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine Spalte zur Tabelle hinzugef�gt wird.
	 */
	public void columnAdded(TableColumnModelEvent arg0) {
		
	}
	@Override	
	/**
	 * Funktion welche aufgerufen wird wenn sich der Aussenabstand der Tabellenspalte �ndert.
	 */
	public void columnMarginChanged(ChangeEvent arg0) {
		
	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine Spalte in der Tabelle verschoben wird.
	 */
	public void columnMoved(TableColumnModelEvent arg0) {
		if(arg0.getFromIndex() != arg0.getToIndex()){
			//System.out.println("column moved from "+arg0.getFromIndex()+" "+arg0.getToIndex());
			getUserInputData(getSelectedTab()).changeColumns(arg0.getFromIndex(), arg0.getToIndex());
			autoSave();
		}
	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine Spalte aus der Tabelle entfernt wird.
	 */
	public void columnRemoved(TableColumnModelEvent arg0) {
		
	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine Andere Spalte in der Tabelle selektiert wird.
	 */
	public void columnSelectionChanged(ListSelectionEvent arg0) {
		
	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine Komponente mit dem ComponentListener zum ViewPort hinzugef�gt wird.
	 */
	public void componentAdded(ContainerEvent e) {
		addKeyAndContainerListenerToAll(e.getChild());
		
	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine Komponente mit dem ComponentListener unsichtbar gemacht wird.
	 */
	public void componentHidden(ComponentEvent e) {
		
	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine Komponente mit dem ComponentListener verschoben wird.
	 */
	public void componentMoved(ComponentEvent e) {
		
	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine Komponente mit dem ComponentListener von dem ViewPane entfernt wird.
	 */
	public void componentRemoved(ContainerEvent e) {
		removeKeyAndContainerListenerToAll(e.getChild());
		
	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine Komponente mit dem ComponentListener in der Gr��e ver�ndert wird.
	 */
	public void componentResized(ComponentEvent e) {
		if(e.getSource()==getFrame()){
			frameResizeEvent();
		}
	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine Komponente mit dem ComponentListener sichtbar gemacht wird.
	 */
	public void componentShown(ComponentEvent e) {
		
	}
	/**
	 * Hilfsfunktion welche alle Multicasts aus dem jeweiligen Programmteil l�scht
	 * @param typ
	 */
	public void deleteAllMulticasts(Typ typ){
		pressBTSelectAll(typ);
		pressBTDelete(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn ein bestimmter Multicast gel�scht werden soll.
	 * @param mcd MulticastData Objekt des Multicasts welcher gel�scht werden soll.
	 */
	public void deleteMC(MulticastData mcd){
		mc.deleteMC(mcd);
		updateTable(mcd.getTyp(), UpdateTyp.DELETE);
	}

	/**
	 * Funktion, welche die ComboBox für Layer2(MMRP/MAC) mit den richtigen Netzwerkadaptern fuellt.
	 * @param typ Programmteil in welchem die Box geupdated werden soll.
	 */
	public void insertNetworkAdapters(Typ typ){
		WideComboBox cbSrc = getPanConfig(typ).getCb_sourceIPaddress();
		
		/* Als erstes die CB leeren */
		cbSrc.removeAllItems();
		cbSrc.addItem("");
		
		Vector<byte[]> temp = NetworkAdapter.getMacAdapters();
		
		if (temp != null)
			for(int i = 0 ; i < temp.size(); i++)
					cbSrc.addItem(NetworkAdapter.getNameToMacAdress(temp.get(i)));
		
	}
	
	/**
	 * Funktion, welche die ComboBox mit den richtigen Netzwerkadaptern fuellt.
	 * @param typ Programmteil in welchem die Box geupdated werden soll.
	 * @param isIPv4 true bei IPv4 und false bei IPv6
	 */
	private void insertNetworkAdapters(Typ typ, boolean isIPv4) {
		WideComboBox cbSrc = getPanConfig(typ).getCb_sourceIPaddress();
		
		/* Als erstes die CB leeren */
		cbSrc.removeAllItems();
		cbSrc.addItem("");
		
		Vector<InetAddress> temp = null;
		if (isIPv4) {
			temp = NetworkAdapter.getipv4Adapters();
			
		} else {
			temp = NetworkAdapter.getipv6Adapters();
		}
		if (temp != null) {
			for(int i = 0 ; i < temp.size(); i++){
				try {
					cbSrc.addItem(NetworkInterface.getByInetAddress(temp.get(i)).getDisplayName());
				} catch (SocketException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Funktion welche aufgerufen wird wenn der Input des Group IP Adress Felds ge�ndert wurde.
	 * @param typ Programmteil in welchem das Group IP Adress Feld ge�ndert wurde.
	 */
	private void docEventTFgrp(Typ typ){
		// TODO [MH] umschreiben zu NetworkAdapter.getAdressType
		boolean isIPv4 = InputValidator.checkMC_IPv4(getPanConfig(typ).getTf_groupIPaddress().getText()) != null;
		boolean isIPv6 = InputValidator.checkMC_IPv6(getPanConfig(typ).getTf_groupIPaddress().getText()) != null;
		boolean mac = InputValidator.checkMulticastGroup(getPanConfig(typ).getTf_groupIPaddress().getText());
		
		if (typ == Typ.L2_SENDER || typ == Typ.L2_RECEIVER){
			if(getPanConfig(typ).getCb_sourceIPaddress().getItemCount() <= 1)
				insertNetworkAdapters(typ);
			
			if(mac){
				getPanConfig(typ).getPan_groupIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.L2GROUP, BorderType.TRUE));
				if(typ == Typ.L2_SENDER)
					input[2][0] = true;
				else
					input[3][0] = true;
			}else{
				getPanConfig(typ).getPan_groupIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.L2GROUP, BorderType.FALSE));
				if(typ == Typ.L2_SENDER)
					input[2][0] = false;
				else
					input[3][0] = false;			
			}
		}else if (typ == Typ.L3_RECEIVER || typ == Typ.L3_SENDER) {
			if(
				isIPv4 || isIPv6 ||
				(getSelectedRows(typ).length > 1 && getPanConfig(typ).getTf_groupIPaddress().getText().equals("..."))
			) {
				getPanConfig(typ).getPan_groupIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.L3GROUP, BorderType.TRUE));
				/* Lade die richtigen Netzwerkinterfaces */
				if (isIPv4) {
					insertNetworkAdapters(typ, true);
				} else if (isIPv6) {
					insertNetworkAdapters(typ, false);
				}
				
				if (typ == Typ.L3_SENDER) {
					input[0][0] = true;
					getPanConfig(typ).getTf_udp_packetlength().setText("");
					getPanConfig(typ).getTf_udp_packetlength().setEnabled(true);
				} else {
					input[1][0] = true;
				}
			} else {
				getPanConfig(typ).getPan_groupIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.L3GROUP, BorderType.FALSE));
				//insertNetworkAdapters(typ); 
				if (typ == Typ.L3_SENDER) {
					input[0][0] = false;
					getPanConfig(typ).getTf_udp_packetlength().setEnabled(false);
					getPanConfig(typ).getTf_udp_packetlength().setText(lang.getProperty("config.message.ipFirstShort"));
				} else {
					input[1][0] = false;
				}
				/* Netzwerkadapterliste leeren, da jetzt wieder v4 oder v6 sein kann */
				getPanConfig(typ).getCb_sourceIPaddress().removeAllItems();
				getPanConfig(typ).getCb_sourceIPaddress().addItem(lang.getProperty("config.message.ipFirst"));
				
			}
		}
		if(getPanConfig(typ).getTf_groupIPaddress().getText().equalsIgnoreCase("") && typ != Typ.L2_SENDER && typ != typ.L2_RECEIVER){		
			getPanConfig(typ).getPan_groupIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.L3GROUP, BorderType.NEUTRAL));
			/* Netzwerkadapterliste leeren, da jetzt wieder v4 oder v6 sein kann */
			getPanConfig(typ).getCb_sourceIPaddress().removeAllItems();
			getPanConfig(typ).getCb_sourceIPaddress().addItem(lang.getProperty("config.message.ipFirst"));
			
			if (typ == Typ.L3_SENDER) {
				getPanConfig(typ).getTf_udp_packetlength().setEnabled(false);
				getPanConfig(typ).getTf_udp_packetlength().setText("IP first");
			}
		}
		checkInput(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Input des Packet Length Felds ge�ndert wurde.
	 * @param typ Programmteil in welchem das Packet Length Feld ge�ndert wurde.
	 */
	private void docEventTFlength(Typ typ){
		IPType ipTyp = NetworkAdapter.getAddressType(getPanConfig(typ).getTf_groupIPaddress().getText());
		if (typ == Typ.L3_SENDER && ipTyp != null) {
			if((ipTyp == IPType.IPv4 && InputValidator.checkIPv4PacketLength(getPanConfig(typ).getTf_udp_packetlength().getText()) > 0) ||
				(ipTyp == IPType.IPv6 && InputValidator.checkIPv6PacketLength(getPanConfig(typ).getTf_udp_packetlength().getText()) > 0)){
				getPanConfig(typ).getPan_packetlength().setBorder(MiscBorder.getBorder(BorderTitle.LENGTH, BorderType.TRUE));
				input[0][5]=true;
			} else {
				getPanConfig(typ).getPan_packetlength().setBorder(MiscBorder.getBorder(BorderTitle.LENGTH, BorderType.FALSE));
				input[0][5]=false;
			}
		}else if(typ == Typ.L2_SENDER){
			if(InputValidator.checkMMRPPacketLength(getPanConfig(typ).getTf_udp_packetlength().getText()) > 0){
				getPanConfig(typ).getPan_packetlength().setBorder(MiscBorder.getBorder(BorderTitle.LENGTH, BorderType.TRUE));
				input[2][5]=true;
			}else{
				getPanConfig(typ).getPan_packetlength().setBorder(MiscBorder.getBorder(BorderTitle.LENGTH, BorderType.FALSE));
				input[2][5]=false;
			}
				
		}
			/*if (NetworkAdapter.getAddressType(getPanConfig(typ).getTf_groupIPaddress().getText()) == IPType.IPv4) {
				if((InputValidator.checkIPv4PacketLength(getPanConfig(typ).getTf_udp_packetlength().getText()) > 0)) {
					getPanConfig(typ).getPan_packetlength().setBorder(MiscBorder.getBorder(BorderTitle.LENGTH, BorderType.TRUE));
					input[0][5]=true;
				} else {
					getPanConfig(typ).getPan_packetlength().setBorder(MiscBorder.getBorder(BorderTitle.LENGTH, BorderType.FALSE));
					input[0][5]=false;
				}
			} else {
				if((InputValidator.checkIPv6PacketLength(getPanConfig(typ).getTf_udp_packetlength().getText())> 0)) {
						getPanConfig(typ).getPan_packetlength().setBorder(MiscBorder.getBorder(BorderTitle.LENGTH, BorderType.TRUE));
						input[0][5]=true;
					} else {
						getPanConfig(typ).getPan_packetlength().setBorder(MiscBorder.getBorder(BorderTitle.LENGTH, BorderType.FALSE));
						input[0][5]=false;
					} 
					
			}*/
		
		if(getPanConfig(typ).getTf_udp_packetlength().getText().equalsIgnoreCase("")){					
			getPanConfig(typ).getPan_packetlength().setBorder(MiscBorder.getBorder(BorderTitle.LENGTH, BorderType.NEUTRAL));					
		}
		checkInput(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Input des Port Felds ge�ndert wurde.
	 * @param typ Programmteil in welchem das Port Feld ge�ndert wurde.
	 */
	private void docEventTFport(Typ typ){
		if(
			(InputValidator.checkPort(getPanConfig(typ).getTf_udp_port().getText()) > 0) || 
			(getSelectedRows(typ).length > 1 && getPanConfig(typ).getTf_udp_port().getText().equals("..."))
		){
			getPanConfig(typ).getPan_udp_port().setBorder(MiscBorder.getBorder(BorderTitle.PORT, BorderType.TRUE));
			if(typ == Typ.L3_SENDER){
				input[0][2]=true;		
			}
			else if(typ == Typ.L3_RECEIVER){
				input[1][2]=true;
			}
		} else {
			getPanConfig(typ).getPan_udp_port().setBorder(MiscBorder.getBorder(BorderTitle.PORT, BorderType.FALSE));
			if(typ == Typ.L3_SENDER){
				input[0][2] = false;		
			}
			else if(typ == Typ.L3_RECEIVER){
				input[1][2] = false;
			}
		}
		
		if(getPanConfig(typ).getTf_udp_port().getText().equalsIgnoreCase("")){		
			getPanConfig(typ).getPan_udp_port().setBorder(MiscBorder.getBorder(BorderTitle.PORT, BorderType.NEUTRAL));
		}		
		checkInput(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Input des Packet Rate Felds ge�ndert wurde.
	 * @param typ Programmteil in welchem das Packet Rate Feld ge�ndert wurde.
	 */
	private void docEventTFrate(Typ typ){
		if(
			(InputValidator.checkPacketRate(getPanConfig(typ).getTf_packetrate().getText())> 0) || 
			(getSelectedRows(typ).length > 1 && getPanConfig(typ).getTf_packetrate().getText().equals("..."))
		){
			getPanConfig(typ).getPan_packetrate().setBorder(MiscBorder.getBorder(BorderTitle.RATE, BorderType.TRUE));
			if(typ == Typ.L3_SENDER){
				input[0][4]=true;
			}else if(typ == Typ.L2_SENDER){
				if(InputValidator.checkPacketRate(getPanConfig(typ).getTf_packetrate().getText()) > 0)
					input[2][4]=true;
			}
		}
		else{
			getPanConfig(typ).getPan_packetrate().setBorder(MiscBorder.getBorder(BorderTitle.RATE, BorderType.FALSE));
			if(typ == Typ.L3_SENDER){
				input[0][4]=false;
			}else if(typ == Typ.L2_SENDER){
				input[2][4]=false;
			}
		}
		if(getPanConfig(typ).getTf_packetrate().getText().equalsIgnoreCase("")){					
			getPanConfig(typ).getPan_packetrate().setBorder(MiscBorder.getBorder(BorderTitle.RATE, BorderType.NEUTRAL));					
		}
		checkInput(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Input des TTL Felds ge�ndert wurde.
	 * @param typ Programmteil in welchem das TTL Feld ge�ndert wurde.
	 */
	private void docEventTFttl(Typ typ){
		if(
			(InputValidator.checkTimeToLive(getPanConfig(typ).getTf_ttl().getText())> 0) ||
			(getSelectedRows(typ).length > 1 && getPanConfig(typ).getTf_ttl().getText().equals("..."))
		){
			getPanConfig(typ).getPan_ttl().setBorder(MiscBorder.getBorder(BorderTitle.TTL, BorderType.TRUE));
			if(typ == Typ.L3_SENDER){
				input[0][3]=true;
			}
		}
		else{
			getPanConfig(typ).getPan_ttl().setBorder(MiscBorder.getBorder(BorderTitle.TTL, BorderType.FALSE));
			if(typ == Typ.L3_SENDER){
				input[0][3]=false;
			}
		}
		if(getPanConfig(typ).getTf_ttl().getText().equalsIgnoreCase("")){					
			getPanConfig(typ).getPan_ttl().setBorder(MiscBorder.getBorder(BorderTitle.TTL, BorderType.NEUTRAL));					
		}
		checkInput(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn die Fenstergroesse geaendert wurde. 
	 * Passt die Komponenten der GUI auf die neue Groesse an.
	 */
	private void frameResizeEvent() {
		if(getSelectedTab() != Typ.UNDEFINED){
			if(getFrame().getSize().width > 1000){
				getTable(getSelectedTab()).setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
			}
			else{
				getTable(getSelectedTab()).setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			}
		}
		if(getSelectedTab() == Typ.L2_RECEIVER || getSelectedTab() == Typ.L3_RECEIVER){
			if(getPanTabbed(getSelectedTab()).getPan_recGraph().isVisible() && getPanTabbed(getSelectedTab()).getTab_console().isVisible()){
				getPanTabbed(getSelectedTab()).getPan_recGraph().resize(new Dimension(getFrame().getSize().width-262,100));
			}
		}
		else if(getSelectedTab() == Typ.L2_SENDER || getSelectedTab() == Typ.L3_SENDER){
			if(getPanTabbed(getSelectedTab()).getPan_graph().isVisible() && getPanTabbed(getSelectedTab()).getTab_console().isVisible()){
				getPanTabbed(getSelectedTab()).getPan_graph().resize(new Dimension(getFrame().getSize().width-262,100));
			}
		}
	}
	/**
	 * Hilfsfunktion welche das Frame zur�ckgibt in welchem das MultiCastor Tool gezeichnet wird.
	 * @return FrameMain Objekt welches angefordert wurde.
	 */
	public FrameMain getFrame() {
		return f;
	}
	/**
	 * Hilfsfunktion welche die aktuelle Anzahl an Multicasts vom MulticastController anfordert.
	 * @param typ Programmteil in welchem die Multicasts gez�hlt werden sollen.
	 * @return Z�hler der angibt wievielel Multicasts sich in einem Programmteil befinden.
	 */
	public int getMCCount(Typ typ){
		return mc.getMCs(typ).size();
	}
	/**
	 * Hilfsfunktion welche Multicast Daten vom MulticastController anfordert.
	 * @param i Index des angeforderten Multicasts (Index in der Tabelle).
	 * @param typ Programmteil in zu welchem der Multicast geh�rt.
	 * @return MulticastData Objekt welches vom MulticastController zur�ckgegeben wird.
	 */
	public MulticastData getMCData(int i, MulticastData.Typ typ){
		return mc.getMC(i, typ);
	}
	/**
	 * Hilfsfunktion welche das Configuration Panel eines bestimmten Programmteils zur�ckgibt.
	 * @param typ Programmteil in welchem sich das Configuration Panel befindet.
	 * @return PnaleMulticastConfig, welches angefordert wurde.
	 */
	private PanelMulticastConfig getPanConfig(Typ typ){
		PanelMulticastConfig configpart = null;
		switch(typ){
			case L2_SENDER: configpart=f.getPanel_sen_lay2().getPan_config(); break;
			case L2_RECEIVER: configpart=f.getPanel_rec_lay2().getPan_config(); break;
			case L3_SENDER: configpart=f.getPanel_sen_lay3().getPan_config(); break;
			case L3_RECEIVER: configpart=f.getPanel_rec_lay3().getPan_config(); break;
		}
		return configpart;
	}
	/**
	 * Hilfsfunktion welche das Control Panel eines bestimmten Programmteils zur�ckgibt.
	 * @param typ Programmteil in welchem sich das Control Panel befindet.
	 * @return PanelMulticastControl, welches angefordert wurde.
	 */
	private PanelMulticastControl getPanControl(Typ typ){
		PanelMulticastControl controlpart = null;
		switch(typ){
			case L2_SENDER: controlpart=f.getPanel_sen_lay2().getPan_control(); break;
			case L2_RECEIVER: controlpart=f.getPanel_rec_lay2().getPan_control(); break;
			case L3_SENDER: controlpart=f.getPanel_sen_lay3().getPan_control(); break;
			case L3_RECEIVER: controlpart=f.getPanel_rec_lay3().getPan_control(); break;
		}
		return controlpart;
	}
	/**
	 * Hilfsfunktion welche die Statusbar des jeweiligen Programmteils zur�ck gibt.
	 * @param typ Programmteil in welchem sich die Statusbar befindet.
	 * @return PanelStatusbar welche angefordert wurde.
	 */
	private PanelStatusBar getPanStatus(Typ typ){
		PanelStatusBar statusbarpart = null;
		switch(typ){
			case L2_SENDER: statusbarpart=f.getPanel_sen_lay2().getPan_status(); break;
			case L2_RECEIVER: statusbarpart=f.getPanel_rec_lay2().getPan_status(); break;
			case L3_SENDER: statusbarpart=f.getPanel_sen_lay3().getPan_status(); break;
			case L3_RECEIVER: statusbarpart=f.getPanel_rec_lay3().getPan_status(); break;
		}
		return statusbarpart;
	}
	/**
	 * Hilfsfunktion welche einen bestimten Programmteil zur�ck gibt.
	 * @param typ Programmteil welcher angeforder wird.
	 * @return JPanel mit dem angeforderten Programmteil.
	 */
	public PanelTabbed getPanTabbed(Typ typ){
		PanelTabbed ret = null;
		switch(typ){
			case L2_SENDER: ret=f.getPanel_sen_lay2(); break;
			case L2_RECEIVER: ret=f.getPanel_rec_lay2(); break;
			case L3_SENDER: ret=f.getPanel_sen_lay3(); break;
			case L3_RECEIVER: ret=f.getPanel_rec_lay3(); break;
		}
		return ret;
	}
	/**
	 * Hilfsfunktion welche ein Integer Array mit den Selektierten Zeilen einer Tabele zur�ckgibt.
	 * @param typ Programmteil in welchem sich die Tabelle befindet.
	 * @return Integer Array mit selektierten Zeilen. (leer wenn keine Zeile selektiert ist).
	 */
	public int[] getSelectedRows(Typ typ){
		int[] ret=null;
		ret = getTable(typ).getSelectedRows();
		//System.out.println("ret: " + Arrays.toString(ret));
		return ret;
	}
	/**
	 * Hilfsfunktion welche den Programmteil zurueckgibt welcher im Moment per Tab selektiert ist.
	 * @return Programmteil welcher im Vordergrund ist.
	 */
	public Typ getSelectedTab() {
		String title = f.getTabpane().getTitleAt(f.getTabpane().getSelectedIndex());
		Typ typ;
		if(title.equals(" "+lang.getProperty("tab.l3s")+" ")) typ = Typ.L3_SENDER;
		else if(title.equals(" "+lang.getProperty("tab.l3r")+" ")) typ = Typ.L3_RECEIVER;
		else if(title.equals(" "+lang.getProperty("tab.l2s")+" ")) typ = Typ.L2_SENDER;
		else if(title.equals(" "+lang.getProperty("tab.l2r")+" ")) typ = Typ.L2_RECEIVER;
		else typ = Typ.UNDEFINED;

		return typ;
	}
	/**
	 * Hilfsfunktion welche die Richtung im SnakeProgramm zur�ckgibt.
	 * @return Richtung in welche die "Snake" laufen soll
	 */
	public SNAKE_DIRECTION getSnakeDir(){
		return snakeDir;
	}
	/**
	 * Hilfsfunktion welche die Tabelle des jeweiligen Programmteil zur�ckgibt.
	 * @param typ Programmteil aus welchem die Tabelle angeforder wird.
	 * @return Die JTable welche angefordert wurde.
	 */
	public JTable getTable(Typ typ){
		JTable tablepart = null;
		switch(typ){
			case L2_RECEIVER: tablepart=f.getPanel_rec_lay2().getTable(); break;
			case L2_SENDER: tablepart=f.getPanel_sen_lay2().getTable(); break;
			case L3_RECEIVER: tablepart=f.getPanel_rec_lay3().getTable(); break;
			case L3_SENDER: tablepart=f.getPanel_sen_lay3().getTable(); break;
		}
		return tablepart;
	}
	/**
	 * Hilfsfunktion welches das Model der jeweiligen Tabelle zur�ckgibt.
	 * @param typ Programmteil von welchem das Tabellenmodel angeforder wird.
	 * @return das Tabellenmodel des spezifizierten Programmteils.
	 */
	public MiscTableModel getTableModel(Typ typ){
		return ((MiscTableModel) getTable(typ).getModel());
	}
	/**
	 * Hilfsfunktion zum Berechnen des insgesamten Traffics welcher vom Multicast Tool empfangen wird (IPv4 & IPv6).
	 * @return Gibt den Insgesamten Traffic des IPv4SENDER und IPv6SENDER als String zur�ck (Mbit/s) im Format "##0.000"
	 */
	public String getTotalTrafficDown(){
		DecimalFormat ret = new DecimalFormat("##0.000");
		double sum = 0.0;
		
		for(int i = 0; i < getTable(Typ.L3_RECEIVER).getModel().getRowCount(); i++){
			sum = sum + Double.parseDouble(((String) getTable(Typ.L3_RECEIVER).getModel().getValueAt(i, 5)).replace(",", "."));
	 	}
		return ret.format(sum);
	 }
	/**
	 * Hilfsfunktion zum Berechnen des insgesamten Traffics welcher vom Multicast Tool verschickt wird (IPv4 & IPv6).
	 */
	public String getTotalTrafficUP(){
		DecimalFormat ret = new DecimalFormat("##0.000");
		double sum = 0.0;
		
		for(int i = 0; i < getTable(Typ.L3_SENDER).getModel().getRowCount(); i++){
			sum = sum + Double.parseDouble(((String) getTable(Typ.L3_SENDER).getModel().getValueAt(i, 5)).replace(",", "."));
	 	}
		return ret.format(sum);
	}
	/**
	 * Funktion welche aufgerufen wird wenn Hide im PopupMenu des Tabellenkopfs gedr�ckt wurde
	 */
	private void hideColumnClicked() {
		getUserInputData(getSelectedTab()).hideColumn(PopUpMenu.getSelectedColumn());
//		PopUpMenu.updateCheckBoxes(this);
//		removeColumnsFromTable(PopUpMenu.getSelectedColumn());
	}
	/**
	 * Funktion welche die GUI startet und initialisiert
	 * @param p_mc Referenz zum MultiCast Controller, die wichtigste Schnittstelle der GUI.
	 */
	public void initialize(MulticastController p_mc){
		mc = p_mc;
		
		inputData_S3 = new UserInputData();
		inputData_R3 = new UserInputData();
		inputData_S2 = new UserInputData();
		inputData_R2 = new UserInputData();
		
		//levelData = new UserlevelData();
		f = new FrameMain(this);
		addKeyAndContainerListenerToAll(f);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Hilfsfunktion zum Testen des Programm mits realen daten, durch diese Funktion k�nnen extrem gro�e
	 * Datenmengen simuliert werden.
	 * [MH] Ab v1.5 umgestellt auf Layer3
	 */
	@SuppressWarnings("unused")
	private void insertTestData(){
		// TODO [MH] nochmal testen
		for(int i = 1 ; i < 6 ; i++){
			getPanConfig(Typ.L3_SENDER).getTf_groupIPaddress().setText("224.0.0."+i);
			getPanConfig(Typ.L3_SENDER).getCb_sourceIPaddress().setSelectedIndex(1);
			getPanConfig(Typ.L3_SENDER).getTf_udp_port().setText("4000"+i);
			getPanConfig(Typ.L3_SENDER).getTf_ttl().setText("32");
			getPanConfig(Typ.L3_SENDER).getTf_packetrate().setText(""+10*i);
			getPanConfig(Typ.L3_SENDER).getTf_udp_packetlength().setText("2048");
			getPanConfig(Typ.L3_SENDER).getTb_active().setSelected(true);
			pressBTAdd(Typ.L3_SENDER);
			//Thread.sleep(100);
			
			getPanConfig(Typ.L3_RECEIVER).getTf_groupIPaddress().setText("224.0.0."+i);
			getPanConfig(Typ.L3_RECEIVER).getTf_udp_port().setText("4000"+i);
			getPanConfig(Typ.L3_RECEIVER).getTb_active().setSelected(true);
			pressBTAdd(Typ.L3_RECEIVER);
			//Thread.sleep(100);
			getPanConfig(Typ.L3_SENDER).getTf_groupIPaddress().setText("ff00::"+i);
			getPanConfig(Typ.L3_SENDER).getCb_sourceIPaddress().setSelectedIndex(1);
			getPanConfig(Typ.L3_SENDER).getTf_udp_port().setText("4000"+i);
			getPanConfig(Typ.L3_SENDER).getTf_ttl().setText("32");
			getPanConfig(Typ.L3_SENDER).getTf_packetrate().setText(""+10*i);
			getPanConfig(Typ.L3_SENDER).getTf_udp_packetlength().setText("1024");
			getPanConfig(Typ.L3_SENDER).getTb_active().setSelected(true);
			pressBTAdd(Typ.L3_SENDER);
			//Thread.sleep(100);
			getPanConfig(Typ.L3_RECEIVER).getTf_groupIPaddress().setText("ff00::"+i);
			getPanConfig(Typ.L3_RECEIVER).getTf_udp_port().setText("4000"+i);
			getPanConfig(Typ.L3_RECEIVER).getTb_active().setSelected(true);
			pressBTAdd(Typ.L3_RECEIVER);
			//Thread.sleep(100);
		}
		
	}
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn Inhalt in ein Feld des Configuration Panel eingetrgen wird.
	 */
	public void insertUpdate(DocumentEvent source) {
		if(source.getDocument() == getPanConfig(Typ.L3_SENDER).getTf_groupIPaddress().getDocument()) {
			docEventTFgrp(Typ.L3_SENDER);
		} else if (source.getDocument() == getPanConfig(Typ.L3_RECEIVER).getTf_groupIPaddress().getDocument()) {
			docEventTFgrp(Typ.L3_RECEIVER);
		} else if(source.getDocument() == getPanConfig(Typ.L3_SENDER).getTf_udp_port().getDocument()) {
			docEventTFport(Typ.L3_SENDER);
		} else if(source.getDocument() == getPanConfig(Typ.L3_RECEIVER).getTf_udp_port().getDocument()) {
			docEventTFport(Typ.L3_RECEIVER);
		} else if(source.getDocument() == getPanConfig(Typ.L3_SENDER).getTf_ttl().getDocument()) {
			docEventTFttl(Typ.L3_SENDER);
		} else if(source.getDocument() == getPanConfig(Typ.L3_SENDER).getTf_packetrate().getDocument()) {
			docEventTFrate(Typ.L3_SENDER);
		} else if(source.getDocument() == getPanConfig(Typ.L3_SENDER).getTf_udp_packetlength().getDocument()) {
			docEventTFlength(Typ.L3_SENDER);
		} else if (source.getDocument() == getPanConfig(Typ.L2_RECEIVER).getTf_groupIPaddress().getDocument()) {
			docEventTFgrp(Typ.L2_RECEIVER);
		} else if (source.getDocument() == getPanConfig(Typ.L2_SENDER).getTf_groupIPaddress().getDocument()) {
			docEventTFgrp(Typ.L2_SENDER);
		} else if(source.getDocument() == getPanConfig(Typ.L2_SENDER).getTf_udp_packetlength().getDocument()) {
			docEventTFlength(Typ.L2_SENDER);
		} else if(source.getDocument() == getPanConfig(Typ.L2_SENDER).getTf_packetrate().getDocument()) {
			docEventTFrate(Typ.L2_SENDER);
		}
		autoSave();
	}
	@SuppressWarnings({ "static-access" })
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn eine GUI Komponente mit dem ItemListener selektiert oder deselektiert wird.
	 * Dieser Listener wird f�r RadioButtons und Checkboxen verwendet.
	 */
	public void itemStateChanged(ItemEvent arg0) {
		if(arg0.getStateChange() == arg0.SELECTED){
			/* Auswahl eines Netzwerkinterfaces */
			if(arg0.getSource() == getPanConfig(Typ.L3_SENDER).getTf_sourceIPaddress()){
				changeNetworkInterface(Typ.L3_SENDER);
			} else if(arg0.getSource() == getPanConfig(Typ.L3_RECEIVER).getTf_sourceIPaddress()){
				changeNetworkInterface(Typ.L3_RECEIVER);
			} else if(arg0.getSource() == getPanConfig(Typ.L2_SENDER).getTf_sourceIPaddress()){
				changeNetworkInterface(Typ.L2_SENDER);
			}else if(arg0.getSource() == getPanConfig(Typ.L2_RECEIVER).getTf_sourceIPaddress()){
				changeNetworkInterface(Typ.L2_RECEIVER);
			}
			
			/* Auswahl des Lost Packages Graphen im Receiver */
			else if(arg0.getSource() == getPanTabbed(Typ.L3_RECEIVER).getPan_recGraph().getLostPktsRB()){
				getPanTabbed(Typ.L3_RECEIVER).getPan_recGraph().selectionChanged(valueType.LOSTPKT);
			}
			/* Auswahl des Jitter Graphen im Receiver */
			else if(arg0.getSource() == getPanTabbed(Typ.L3_RECEIVER).getPan_recGraph().getJitterRB()){
				getPanTabbed(Typ.L3_RECEIVER).getPan_recGraph().selectionChanged(valueType.JITTER);
			}
			/* Auswahl des Measured Packages Graphen im Receiver */
			else if(arg0.getSource() == getPanTabbed(Typ.L3_RECEIVER).getPan_recGraph().getMeasPktRtRB()){
				System.out.println("RECEIVER_V4 - MeasPktRtRB");
				getPanTabbed(Typ.L3_RECEIVER).getPan_recGraph().selectionChanged(valueType.MEASPKT);
			}
			// TODO Layer2 Receiver Buttons einbauen
			
		} else {
			if(arg0.getSource() == f.getMi_autoSave()){
				if(arg0.getStateChange() == ItemEvent.DESELECTED)
				submitInputData();
			}
		}
		if(arg0.getSource() == f.getMi_autoSave()){
			f.setAutoSave(f.isAutoSaveEnabled());
		}
		autoSave();
	}

	/**
	 * Funktion welche Aufgerufen wird wenn eine Taste der Tastatur gedrueckt wird.
	 */
	public void keyPressed(KeyEvent arg0) {
		switch(arg0.getKeyCode()){
		  	case 38: snakeDir = SnakeGimmick.SNAKE_DIRECTION.N;
		  			 break;
		 	case 40: snakeDir = SnakeGimmick.SNAKE_DIRECTION.S;
		  			 break;
		  	case 37: snakeDir = SnakeGimmick.SNAKE_DIRECTION.W;
		  			 break;
		  	case 39: snakeDir = SnakeGimmick.SNAKE_DIRECTION.E;
		  	default:
		  	}
	}
	@Override
	/**
	 * Funktion welche Aufgerufen wird wenn eine Taste der Tastatur losgelassen wird.
	 */
	public void keyReleased(KeyEvent arg0) {
		
	}
	@Override
	/**
	 * Funktion welche Aufgerufen wird sobald die Tastatur einen Input bei gedr�ckter Taste an das System weitergibt.
	 */
	public void keyTyped(KeyEvent arg0) {

	}
	/**
	 * Funktion welche ausgel�st wird wenn der User eine oder mehrere Zeilen in der Tabelle selektiert.
	 * @param typ Programmteil in welchem der User die Zeilen selektiert hat.
	 */
	private void listSelectionEventFired(Typ typ) {
		PanelTabbed tabpart = null;
		//TODO @SK/FH Muss hier noch L2 rein????
		switch(typ){
			case L3_SENDER: tabpart=f.getPanel_sen_lay3(); break;
			case L3_RECEIVER: tabpart=f.getPanel_rec_lay3(); break;
			case L2_SENDER: tabpart = f.getPanel_sen_lay2(); break;
			case L2_RECEIVER: tabpart = f.getPanel_rec_lay2(); break;
		}
		
		int[] selectedRows = tabpart.getTable().getSelectedRows();
		tabpart.getPan_status().getLb_multicasts_selected().setText(selectedRows.length+" "+lang.getProperty("status.mcSelected")+" ");
		if(selectedRows.length > 1){
			multipleSelect(typ);
		}
		if(selectedRows.length == 1){
			tabpart.getPan_config().getBt_enter().setText(lang.getProperty("button.change"));
			tabpart.getPan_config().getTf_groupIPaddress().setEnabled(true);
			if(typ == Typ.L3_RECEIVER || typ == Typ.L3_SENDER){
				tabpart.getPan_config().getTf_groupIPaddress().setText(getMCData(selectedRows[0],typ).getGroupIp().toString().substring(1));
				tabpart.getPan_config().getTf_udp_port().setText(""+getMCData(selectedRows[0],typ).getUdpPort());
				tabpart.getPan_config().getTf_udp_port().setEnabled(true);
				
				IPType iptype = NetworkAdapter.getAddressType(tabpart.getPan_config().getTf_groupIPaddress().getText());
				if (iptype == IPType.IPv4) {
					insertNetworkAdapters(typ, true);
				} else if (iptype == IPType.IPv6) {
					insertNetworkAdapters(typ, false);
				}
				tabpart.getPan_config().getTf_sourceIPaddress().setSelectedIndex(
						NetworkAdapter.findAddressIndex(getMCData(selectedRows[0],typ).getSourceIp().toString().substring(1))+1
					);
			}else{
				tabpart.getPan_config().getTf_groupIPaddress().setText(getMCData(selectedRows[0], typ).getMmrpGroupMacAsString());
				insertNetworkAdapters(typ);
				tabpart.getPan_config().getTf_sourceIPaddress().setSelectedIndex(
						NetworkAdapter.findAddressIndexMMRP(Arrays.toString(getMCData(selectedRows[0],typ).getMmrpSourceMac()))
					);			
			}

			if(typ == Typ.L2_SENDER){
				tabpart.getPan_config().getTf_packetrate().setText(""+getMCData(selectedRows[0],typ).getPacketRateDesired());
				tabpart.getPan_config().getTf_udp_packetlength().setText(""+getMCData(selectedRows[0],typ).getPacketLength());
			}
			if(typ == Typ.L3_SENDER){
				tabpart.getPan_config().getTf_packetrate().setText(""+getMCData(selectedRows[0],typ).getPacketRateDesired());
				tabpart.getPan_config().getTf_ttl().setText(""+getMCData(selectedRows[0],typ).getTtl());;
				tabpart.getPan_config().getTf_udp_packetlength().setText(""+getMCData(selectedRows[0],typ).getPacketLength());;
			}
			setTBactive(selectedRows, typ);
			setBTStartStopDelete(typ);
		}
		else if(selectedRows.length == 0){
			clearInput(typ);
			tabpart.getPan_config().getBt_enter().setText(lang.getProperty("button.add"));
			tabpart.getPan_config().getTf_groupIPaddress().setEnabled(true);
			if(typ==Typ.L3_SENDER){
				tabpart.getPan_config().getTf_sourceIPaddress().setEnabled(true);				
			}
			tabpart.getPan_config().getTf_udp_port().setEnabled(true);
			setTBactive(selectedRows, typ);
		}
	}
	
	/**
	 * Funktion welche ausgeloest wird, wenn der User versucht ein GUI Konfigurationsdatei mit dem "Konfiguration Laden"
	 * Dialog zu laden.
	 */
	private void loadGUIFileEvent() {
		// TODO Auto-generated method stub
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("XML Config Files", "xml"));
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFont(MiscFont.getFont());
		int ret = chooser.showOpenDialog(f);
		if (ret == JFileChooser.APPROVE_OPTION) {
			mc.loadGUIConfig(chooser.getSelectedFile().toString(), false);
		}
	}
	
	/**
	 * Funktion welche ausgeloest wird, wenn der User versucht eine Konfigurationsdatei mit dem "Datei Laden"
	 * Dialog zu laden.
	 */
	private void loadFileEvent(boolean incremental) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("XML Config Files", "xml"));
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFont(MiscFont.getFont());
		
        int ret = chooser.showOpenDialog(f);
        
        if (ret == JFileChooser.APPROVE_OPTION) {
        	loadMulticastConfig(chooser.getSelectedFile().toString(), incremental);
        }
	}
	
	private void loadMulticastConfig(String path, boolean incremental) {
		if(!incremental){
			deleteAllMulticasts(Typ.L3_SENDER);
			deleteAllMulticasts(Typ.L2_SENDER);
			deleteAllMulticasts(Typ.L3_RECEIVER);
			deleteAllMulticasts(Typ.L2_RECEIVER);
		}
		mc.loadMulticastConfig(path, false);
	}
	@Override
	/**
	 * MouseEvent welches ausgel�st wird wenn eine Maustaste gedr�ckt und wieder losgelassen wird.
	 */
	public void mouseClicked(MouseEvent e) {
		//is rightclick?
		if(e.getButton() == MouseEvent.BUTTON3){
			//Source of click is table header of selected tab?
			if(getSelectedTab() != Typ.CONFIG && getSelectedTab() != Typ.UNDEFINED){
				if(e.getSource()==getTable(getSelectedTab()).getTableHeader()){
					if(getPanTabbed(getSelectedTab()).isPopupsAllowed()){
						PopUpMenu.createTableHeaderPopup(getTable(getSelectedTab()), this, e);
					}
				}
			}
		}
		autoSave();
	}
	@Override
	/**
	 * MouseEvent welches auf das Betreten einer Komponente der Maus reagiert.
	 */
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	/**
	 * MouseEvent welches auf das Verlassen einer Komponente der Maus reagiert.
	 */
	public void mouseExited(MouseEvent e) {
		
	}
	@Override
	/**
	 * MouseEvent welches auf Dr�cken einer Maustaste reagiert.
	 */
	public void mousePressed(MouseEvent e) {

		
	}
	@Override
	/**
	 * MouseEvent welches auf Loslassen einer Maustaste reagiert.
	 */
	public void mouseReleased(MouseEvent e) {
		
	}
	/**
	 * Funktion welche aufgerufen wird wenn mehr als ein Multicast in der Tabelle selektiert wurde.
	 * Passt die GUI entsprechend an.
	 * @param typ Programmteil in welchem mehrere Multicasts Selektiert wurden.
	 */
	private void multipleSelect(Typ typ) {
		getPanConfig(typ).getBt_enter().setText(lang.getProperty("button.changeAll"));
		if(typ == Typ.L3_SENDER){
			getPanConfig(typ).getTf_sourceIPaddress().removeItemListener(this);
			getPanConfig(typ).getTf_packetrate().getDocument().removeDocumentListener(this);
			getPanConfig(typ).getTf_udp_packetlength().getDocument().removeDocumentListener(this);
			getPanConfig(typ).getTf_ttl().getDocument().removeDocumentListener(this);
			getPanConfig(typ).getPan_sourceIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.L3SOURCE, BorderType.TRUE));
			getPanConfig(typ).getPan_packetrate().setBorder(MiscBorder.getBorder(BorderTitle.RATE, BorderType.TRUE));
			getPanConfig(typ).getPan_packetlength().setBorder(MiscBorder.getBorder(BorderTitle.LENGTH, BorderType.TRUE));
			getPanConfig(typ).getPan_ttl().setBorder(MiscBorder.getBorder(BorderTitle.TTL, BorderType.TRUE));
			getPanConfig(typ).getTf_sourceIPaddress().setSelectedIndex(0);
			getPanConfig(typ).getTf_packetrate().setText("...");
			getPanConfig(typ).getTf_udp_packetlength().setText("...");
			getPanConfig(typ).getTf_ttl().setText("...");
			getPanConfig(typ).getTf_sourceIPaddress().addItemListener(this);
			getPanConfig(typ).getTf_packetrate().getDocument().addDocumentListener(this);
			getPanConfig(typ).getTf_udp_packetlength().getDocument().addDocumentListener(this);
			getPanConfig(typ).getTf_ttl().getDocument().addDocumentListener(this);
			getPanConfig(typ).getCb_sourceIPaddress().removeItemAt(0);
			getPanConfig(typ).getCb_sourceIPaddress().insertItemAt("...", 0);
			getPanConfig(typ).getCb_sourceIPaddress().setSelectedIndex(0);
		}
		getPanConfig(typ).getTf_groupIPaddress().getDocument().removeDocumentListener(this);
		getPanConfig(typ).getTf_udp_port().getDocument().removeDocumentListener(this);
		getPanConfig(typ).getPan_groupIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.L3GROUP, BorderType.TRUE));
		getPanConfig(typ).getPan_udp_port().setBorder(MiscBorder.getBorder(BorderTitle.PORT, BorderType.TRUE));
		getPanConfig(typ).getTf_groupIPaddress().setText("...");
		getPanConfig(typ).getTf_udp_port().setText("...");
		getPanConfig(typ).getTb_active().setText("multiple");
		getPanConfig(typ).getTb_active().setForeground(Color.green);
		getPanConfig(typ).getTb_active().setSelected(false);
		getPanConfig(typ).getTf_groupIPaddress().getDocument().addDocumentListener(this);
		getPanConfig(typ).getTf_udp_port().getDocument().addDocumentListener(this);
		getPanConfig(typ).getBt_enter().setEnabled(true);
		setBTStartStopDelete(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Add Button gedr�ckt wird.
	 * @param typ Programmteil in welchem der Add Button gedr�ckt wurde
	 */
	private void pressBTAdd(Typ typ) {
		this.addMC(changeMCData(new MulticastData(), typ));
		clearInput(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Change Button gedr�ckt wird. Bei selektierten Multicast(s).
	 * @param typ Programmteil in welchem der Change Button gedr�ckt wurde
	 */
	private void pressBTChange(Typ typ) {
		int[] selectedList = getSelectedRows(typ);
		if(selectedList.length == 1){
			MulticastData mcd = getMCData(selectedList[0],typ);
			changeMC(changeMCData(mcd, typ));
			getTable(typ).getSelectionModel().setSelectionInterval(0, 0);
		}
		else{
			PanelMulticastConfig config = getPanConfig(typ);
			if((typ == Typ.L3_SENDER)
			&& (config.getTf_groupIPaddress().getText().equals("..."))
			&& (config.getCb_sourceIPaddress().getSelectedIndex()==0)
			&& (config.getTf_udp_port().getText().equals("..."))
			&& (config.getTf_ttl().getText().equals("..."))
			&& (config.getTf_packetrate().getText().equals("..."))
			&& (config.getTf_udp_packetlength().getText().equals("..."))
			&& (config.getTf_udp_port().getText().equals("..."))
			&& (config.getTb_active().getText().equals("multiple"))){
				showMessage(MessageTyp.INFO, "No changes were made.\n\"...\" keeps old values!");
			}
			else if((typ == Typ.L3_RECEIVER)
					&& (config.getTf_groupIPaddress().getText().equals("..."))
					&& (config.getTf_udp_port().getText().equals("..."))
					&& (config.getTb_active().getText().equals("multiple"))){
				showMessage(MessageTyp.INFO, "No changes were made.\n\"...\" keeps old values!");
			}
			else{
				// TODO [MH] BUG: Multiple Select -> Netzwerk Interface auswaehlen
				// das failt noch und muss hier geloest werden
				// schauen, wie das gemacht wird, wenn nur eins selekted is
				for(int i=selectedList.length-1; i >= 0  ; i--){
					//System.out.println("selected: "+i+": "+selectedList[i]);
					//System.out.println("getting: "+((selectedList.length-1)-i));
					MulticastData mcd = getMCData(selectedList[i]+((selectedList.length-1)-i),typ);
					changeMC(changeMCData(mcd, typ));				
				}
				getTable(typ).getSelectionModel().setSelectionInterval(0, selectedList.length-1);
			}
			setBTStartStopDelete(getSelectedTab());
		}
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Delete Button gedr�ckt wird.
	 * @param typ Programmteil in welchem der Delete Button gedr�ckt wurde
	 */
	private void pressBTDelete(Typ typ) {
		int[] selectedRows =getTable(typ).getSelectedRows();
		for(int i=0 ; i<selectedRows.length ; i++){
			//System.out.println("l�sche zeile: "+selectedRows[i]);
			deleteMC(getMCData(selectedRows[i]-i, typ));
		}
		setBTStartStopDelete(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Deselect All Button gedr�ckt wird.
	 * @param typ Programmteil in welchem der Deselect All Button gedr�ckt wurde
	 */
	private void pressBTDeselectAll(Typ typ) {
		getTable(typ).clearSelection();
		setBTStartStopDelete(typ);
		if(typ == Typ.L3_SENDER){
			getPanConfig(typ).getCb_sourceIPaddress().removeItemAt(0);
			getPanConfig(typ).getCb_sourceIPaddress().insertItemAt("", 0);
			getPanConfig(typ).getTf_sourceIPaddress().setSelectedIndex(0);
		}
		getPanStatus(typ).getLb_multicasts_selected().setText("0 "+lang.getProperty("status.mcSelected")+" ");
		
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Add Button gedr�ckt wird.
	 * Diese Funktion unterscheided ob eine �nderung an einem Multicast stattfinden soll,
	 * oder ein Neuer angelegt werden soll.
	 * @param typ Programmteil in welchem der Add Button gedr�ckt wurde
	 */
	private void pressBTenter(Typ typ) {
		if(getPanConfig(typ).getBt_enter().getText().equals(lang.getProperty("button.add"))){
			pressBTAdd(typ);
		}
		else{
			pressBTChange(typ);
		}
	}
	/**
	 * Funktion welche aufgerufen wird wenn der New Button gedr�ckt wird.
	 * @param typ Programmteil in welchem der New Button gedr�ckt wurde
	 */
	private void pressBTNewMC(Typ typ) {
		clearInput(typ);
		getTable(typ).clearSelection();
		setBTStartStopDelete(typ);
		getPanStatus(typ).getLb_multicasts_selected().setText("0 "+lang.getProperty("status.mcSelected")+"");
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Select All Button gedr�ckt wird.
	 * @param typ Programmteil in welchem der Select All Button gedr�ckt wurde
	 */
	private void pressBTSelectAll(Typ typ) {
		getTable(typ).selectAll();
		if(getSelectedRows(typ).length > 1){
			multipleSelect(typ);
		}
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Start Button gedr�ckt wird.
	 * @param typ Programmteil in welchem der Start Button gedr�ckt wurde
	 */
	private void pressBTStart(Typ typ){
		int[] selectedLine = getSelectedRows(typ);
		for(int i = 0 ; i < selectedLine.length ; i++){
			if(!getMCData(selectedLine[i], typ).isActive()){
				startMC(selectedLine[i], typ);
				updateTable(typ, UpdateTyp.UPDATE);
			}
		}
		setBTStartStopDelete(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Stop Button gedr�ckt wird.
	 * @param typ Programmteil in welchem der Stop Button gedr�ckt wurde
	 */
	private void pressBTStop(Typ typ){
		int[] selectedLine = getSelectedRows(typ);
		for(int i = 0 ; i < selectedLine.length ; i++){
			if(getMCData(selectedLine[i], typ).isActive()){
				stopMC(selectedLine[i], typ);
				updateTable(typ, UpdateTyp.UPDATE);	
			}
		}
		setBTStartStopDelete(typ);
	}
	/**
	 * Funktion welche es dem Multicast Controller und somit den restlichen Programmteilen erm�glicht
	 * Ausgaben in der Konsole des GUI zu t�tigen. 
	 * @param s Nachricht welche in der Konsole der GUI ausgegeben werden soll
	 */
	public void printConsole(String s){
//		getFrame().getPanelPart(Typ.SENDER_V4).getTa_console().append(s+"\n");
//		getFrame().getPanelPart(Typ.SENDER_V6).getTa_console().append(s+"\n");
//		getFrame().getPanelPart(Typ.RECEIVER_V4).getTa_console().append(s+"\n");
//		getFrame().getPanelPart(Typ.RECEIVER_V6).getTa_console().append(s+"\n");
//		getFrame().getPanelPart(Typ.SENDER_V6).getTa_console().setCaretPosition(getFrame().getPanelPart(Typ.SENDER_V6).getTa_console().getText().length());
//		getFrame().getPanelPart(Typ.RECEIVER_V4).getTa_console().setCaretPosition(getFrame().getPanelPart(Typ.RECEIVER_V4).getTa_console().getText().length());
//		getFrame().getPanelPart(Typ.RECEIVER_V6).getTa_console().setCaretPosition(getFrame().getPanelPart(Typ.RECEIVER_V6).getTa_console().getText().length());

		getFrame().getPanelPart(Typ.L3_SENDER).getTa_console().append(s+"\n");
		getFrame().getPanelPart(Typ.L3_RECEIVER).getTa_console().append(s+"\n");
		getFrame().getPanelPart(Typ.L2_SENDER).getTa_console().append(s+"\n");
		getFrame().getPanelPart(Typ.L2_RECEIVER).getTa_console().append(s+"\n");
		getFrame().getPanelPart(Typ.L3_SENDER).getTa_console().setCaretPosition(getFrame().getPanelPart(Typ.L3_SENDER).getTa_console().getText().length());
		getFrame().getPanelPart(Typ.L3_RECEIVER).getTa_console().setCaretPosition(getFrame().getPanelPart(Typ.L3_RECEIVER).getTa_console().getText().length());
		getFrame().getPanelPart(Typ.L2_SENDER).getTa_console().setCaretPosition(getFrame().getPanelPart(Typ.L2_SENDER).getTa_console().getText().length());
		getFrame().getPanelPart(Typ.L2_RECEIVER).getTa_console().setCaretPosition(getFrame().getPanelPart(Typ.L2_RECEIVER).getTa_console().getText().length());
	}
	
	/**
	  * Removed einen Container und einen Keylistener vom
	  * Component c. Hat die Komponente Kinder, mache das
	  * gleiche mit jedem Child
	  * @param c Container, von dem die Listener entfernt werden sollen
	  */
	 private void removeKeyAndContainerListenerToAll(Component c)
     {
          c.removeKeyListener(this);
          if(c instanceof Container) {
               Container cont = (Container)c;
               cont.removeContainerListener(this);
               Component[] children = cont.getComponents();
               for(int i = 0; i < children.length; i++){
            	   removeKeyAndContainerListenerToAll(children[i]);
               }
          }
     }
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn ein Zeichen aus einem Textfeld gel�scht wird.
	 */
	public void removeUpdate(DocumentEvent e) {
		insertUpdate(e);
	}
	/**
	 * Funktion welche aufgerufen wird wenn versucht wird eine Datei zu speichern im Datei speichern Dialog.
	 * @param e ActionEvent welches vom Datei speichern Dialog erzeugt wird
	 */
	private void saveFileEvent(boolean partiell) {
		//Create the Save Dialog
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("XML Config Files", "xml"));
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFont(MiscFont.getFont());
		//show the save dialog
        int ret = chooser.showSaveDialog(f);
        //save the file
        if (ret == JFileChooser.APPROVE_OPTION) {
        	if (!partiell){
        		Vector<MulticastData> v = new Vector<MulticastData>();
        		v.addAll(mc.getMCs(Typ.L2_RECEIVER));
        		v.addAll(mc.getMCs(Typ.L3_RECEIVER));
        		v.addAll(mc.getMCs(Typ.L2_SENDER));
        		v.addAll(mc.getMCs(Typ.L3_SENDER));
        		mc.saveMulticastConfig(chooser.getSelectedFile().getPath(), v);
        	}
        	else{
        		Vector<MulticastData> v = new Vector<MulticastData>();
        		for (int row : f.getPanel_rec_lay2().getTable().getSelectedRows()){
        			v.add(mc.getMC(row, Typ.L2_RECEIVER));
        		}
        		for (int row : f.getPanel_rec_lay3().getTable().getSelectedRows()){
        			v.add(mc.getMC(row, Typ.L3_RECEIVER));
        		}
        		for (int row : f.getPanel_sen_lay2().getTable().getSelectedRows()){
        			v.add(mc.getMC(row, Typ.L2_SENDER));
        		}
        		for (int row : f.getPanel_sen_lay3().getTable().getSelectedRows()){
        			v.add(mc.getMC(row, Typ.L3_SENDER));
        		}
        		mc.saveMulticastConfig(chooser.getSelectedFile().getPath(), v);
        	}
        }
	}
        
    private void saveGUIFileEvent() {
		//Create the Save Dialog
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("XML Config Files", "xml"));
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFont(MiscFont.getFont());
		//show the save dialog
        int ret = chooser.showSaveDialog(f);
        //save the file
        if (ret == JFileChooser.APPROVE_OPTION) {
        	mc.updateGUIData(guidata);
    		mc.saveGUIConfig(chooser.getSelectedFile().getPath(), guidata);
        }		
    }
		
//      TODO @FF F�r dich als Codebsp. hier gelassen, falls das noch gebraucht wird (JT)
//		if(e.getActionCommand().equals("ApproveSelection")){
//			FrameFileChooser fc_save = getFrame().getFc_save();
//			//System.out.println("selected File: "+fc_save.getSelectedFile());
//			
//			mc.saveConfig(	fc_save.getSelectedFile(), 
//							fc_save.isCbSenderL3Selected(), 
//							fc_save.isCbReceiverL3Selected());
//			f.updateLastConfigs(fc_save.getSelectedFile());
//			fc_save.getChooser().rescanCurrentDirectory();
//			fc_save.toggle();
//		}
//		else if(e.getActionCommand().equals("CancelSelection")){
//			getFrame().getFc_save().toggle();
//		}
	//}
	 
	/**
	  * Funktion welche das Aussehen des Start Stop und Delete Buttons anpasst je nach dem welche Multicasts ausgew�hlt wurden.
	  * @param typ Programmteil in welchem die Buttons angepasst werden sollen.
	  */
	 private void setBTStartStopDelete(Typ typ) {
		int[] selectedLine=getSelectedRows(typ);
		if(selectedLine.length == 1){
			getPanControl(typ).getDelete().setEnabled(true);
			if(getMCData(selectedLine[0],typ).isActive()){
				//getPanControl(typ).getStart().setEnabled(false);
				//getPanControl(typ).getStop().setEnabled(true);
			}
			else{
				//getPanControl(typ).getStart().setEnabled(true);
				//getPanControl(typ).getStop().setEnabled(false);
			}
		}
		else if(selectedLine.length == 0){
			//getPanControl(typ).getStart().setEnabled(false);
			//getPanControl(typ).getStop().setEnabled(false);
			getPanControl(typ).getDelete().setEnabled(false);
		}
		else{
			getPanControl(typ).getDelete().setEnabled(true);
			for(int i = 1 ; i < selectedLine.length ; i++){
				if(getMCData(selectedLine[i-1], typ).isActive() && getMCData(selectedLine[i], typ).isActive()){
					//getPanControl(typ).getStart().setEnabled(false);
				}
				else{
					//getPanControl(typ).getStart().setEnabled(true);
					break;
				}
			}
			for(int i = 1 ; i < selectedLine.length ; i++){
				if((!getMCData(selectedLine[i-1], typ).isActive()) && (!getMCData(selectedLine[i], typ).isActive())){
					//getPanControl(typ).getStop().setEnabled(false);	
				}
				else{
					//getPanControl(typ).getStop().setEnabled(true);
					break;
				}
			}
		}
	}
	 /**
	  * Funktion welche das aussehen des ActiveButtons anpasst (Togglefunktion)
	  * @param b Array welches die Selektierten Reihen in einem Programmteil angibt
	  * @param typ Programmteil in welchem sich der Active Button befindet
	  */
	 private void setTBactive(boolean b, Typ typ) {
		if(b){
			getPanConfig(typ).getTb_active().setSelected(true);
			getPanConfig(typ).getTb_active().setText(lang.getProperty("button.active"));
			getPanConfig(typ).getTb_active().setForeground(new Color(0,175,0));
		}
		else{
			getPanConfig(typ).getTb_active().setSelected(false);
			getPanConfig(typ).getTb_active().setText(lang.getProperty("button.inactive"));
			getPanConfig(typ).getTb_active().setForeground(new Color(200,0,0));
		}
	}
	 /**
	  * Funktion welche das aussehen des ActiveButtons anpasst je nach dem welcher Multicast selektiert ist in der Tabelle
	  * @param selectedLine Array welches die Selektierten Reihen in einem Programmteil angibt
	  * @param typ Programmteil in welchem sich der Active Button befindet
	  */
	 public void setTBactive(int[] selectedLine, Typ typ){
		getPanConfig(typ).getTb_active().setEnabled(true);
		if(selectedLine.length==1){	
			if(mc.getMC(selectedLine[0], typ).isActive()){
				getPanConfig(typ).getTb_active().setSelected(true);
				getPanConfig(typ).getTb_active().setText(lang.getProperty("button.active"));
				getPanConfig(typ).getTb_active().setForeground(new Color(0,175,0));
			}
			else{
				getPanConfig(typ).getTb_active().setSelected(false);
				getPanConfig(typ).getTb_active().setText(lang.getProperty("button.inactive"));
				getPanConfig(typ).getTb_active().setForeground(new Color(200,0,0));
			}
		}
	}
	/**
	 * Funktion welche erm�glich Nachrichten in der GUI anzuzeigen. Gibt anderen Programmteilen �ber den
	 * MulticastController die M�glichkeit Informations, Warnungs und Errormeldungen auf dem GUI auszugeben.
	 * @param typ Art der Nachricht (INFO / WARNING / ERROR)
	 * @param message Die eigentliche Nachricht welche angezeigt werden soll
	 */
	public void showMessage(MessageTyp typ, String message){ //type 0 == info, type 1 == warning, type 2 == error
		switch(typ){
			case INFO: JOptionPane.showMessageDialog(null, message, "Information", JOptionPane.INFORMATION_MESSAGE); break;
			case WARNING: JOptionPane.showMessageDialog(null, message, "Warning", JOptionPane.WARNING_MESSAGE); break;
			case ERROR: JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE); break;
		}
	}
	/**
	 * Bildet die Schnittstelle zum Multicast Controller zum starten von einem Bestimmten Multicast.
	 * Sorgt f�r die ensprechenden Updates in der GUI nach dem Versuch den Multicast zu stoppen.
	 * @param row Zeilenindex des Multicast welcher gestartet werden soll
	 * @param typ Programmteil in welchem sich der Multicast befindet welcher gestartet werden soll
	 */
	public void startMC(int row, Typ typ){
		mc.startMC(mc.getMC(row, typ));
		setBTStartStopDelete(typ);
	}
	
	@Override
	/**
	 * Funktion welche aufgerufen wird wenn das Frame in der Gr��e ge�ndert oder verschoben wird.
	 */
	public void stateChanged(ChangeEvent arg0) {
		if(arg0.getSource() == getFrame().getTabpane()){
			frameResizeEvent();
		}	
	}
	/**
	 * Bildet die Schnittstelle zum Multicast Controller zum stoppen von einem Bestimmten Multicast.
	 * Sorgt f�r die ensprechenden Updates in der GUI nach dem Versuch den Multicast zu stoppen.
	 * @param row Zeilenindex des Multicast welcher gestoppt werden soll
	 * @param typ Programmteil in welchem sich der Multicast befindet welcher gestoppt werden soll
	 */
	public void stopMC(int row, Typ typ){
		mc.stopMC(mc.getMC(row, typ));
		setBTStartStopDelete(typ);
	}
	/**
	 * Funktion welche aufgerufen wird wenn der Active Button im ControlPanel gedr�ckt wird.
	 * @param typ Bestimmt den Programmteil in welchem der Active Button gedr�ckt wurde
	 */
	private void toggleBTactive(Typ typ) {
		if(getPanConfig(typ).getTb_active().isSelected()){
			setTBactive(true, typ);
		}
		else{
			setTBactive(false, typ);
		}
	}
	/**
	 * Funktion welche sich um das Update des Graphen k�mmert.
	 * @param typ bestimmt welcher Graph in welchem Programmteil geupdatet werden soll
	 */
	private void updateGraph(Typ typ) {
		//boolean variable which determines if graph can be painted or not, is graph in front of SRC address dropdown
		boolean showupdate=false;
		//check if graph is selected
		if(getPanTabbed(typ).getTab_console().getSelectedIndex()==0){
			if(getPanTabbed(typ).getPan_graph().isVisible() && getPanTabbed(typ).getTab_console().isVisible()){
				//System.out.println("panel graph visible");
				if(typ == Typ.L2_SENDER || typ == Typ.L3_SENDER){
					showupdate = 	!getPanConfig(typ).getCb_sourceIPaddress().isPopupVisible() &&
									!PopUpMenu.isPopUpVisible();
				}
				else{
					showupdate = !PopUpMenu.isPopUpVisible();
				}
			}
			else{
				showupdate=false;
			}
		}
		//check which tab is selected and update graph for specific program part
		if(typ == Typ.L2_SENDER || typ == Typ.L3_SENDER){
			//System.out.println("showupdate "+showupdate);
			getPanTabbed(typ).getPan_graph().updateGraph(mc.getPPSSender(typ), showupdate);
		}
		else{
			graphData = new MulticastData[getSelectedRows(typ).length];
			//System.out.println("GraphData: " + grphData.length);
			for(int i = 0; i < getSelectedRows(typ).length ; i++){
				//System.out.println(i);
				graphData[i]=mc.getMC(getSelectedRows(typ)[i], typ);
			}
			getPanTabbed(typ).getPan_recGraph().updateGraph(graphData, showupdate);
			
		}
	}
	/**
	 * Funktion welche unterscheidet welche Art von Update in der Multicast Tabelle erfolgt ist.
	 * Hierbei kann zwischen Einf�gen, L�schen und Updaten einer Zeile unterschieden werden.
	 * @param typ Bestimmt den Programmteil welcher geupdated wird
	 * @param utyp Bestimmt die Art des Updates welches Erfolgt ist
	 */
	public void updateTable(Typ typ, UpdateTyp utyp){ //i=0 -> insert, i=1 -> delete, i=2 -> change
		PanelTabbed tabpart = getPanTabbed(typ);
		switch(utyp){
			case INSERT:	tabpart.getTableModel().insertUpdate();
							tabpart.getPan_status().getLb_multicast_count().setText(getMCCount(typ)+" "+lang.getProperty("status.mcTotal"));
							if(initFinished){
								clearInput(typ);
							}
							break;
			case DELETE:	tabpart.getTableModel().deleteUpdate();
							tabpart.getPan_status().getLb_multicast_count().setText(getMCCount(typ)+" "+lang.getProperty("status.mcTotal"));
							if(initFinished){
								clearInput(typ);
							}
							break;
							
			case UPDATE:	tabpart.getTableModel().changeUpdate();
							break;
		}
	}
	/**
	 * Funktion die einen bestimmten Programmteil updatet.
	 * @param typ Programmteil der geupdatet werden soll
	 */
	public void updateTablePart(Typ typ){
		if(typ != Typ.UNDEFINED){
			updateTable(typ, UpdateTyp.UPDATE);
		}
	}
	@Override
	/**
	 * Implementierung des ListSelectionListeners, sorgt f�r korrektes Verhalten der GUI
	 * beim Selektieren und Deselektieren von einer oder mehreren Zeilen in der Tabelle.
	 */
	public void valueChanged(ListSelectionEvent e) {
		if(e.getSource()==getTable(Typ.L3_SENDER).getSelectionModel()){
			listSelectionEventFired(Typ.L3_SENDER);
		}
		if(e.getSource()==getTable(Typ.L3_RECEIVER).getSelectionModel()){
			listSelectionEventFired(Typ.L3_RECEIVER);
		}
		if(e.getSource()==getTable(Typ.L2_SENDER).getSelectionModel()){
			listSelectionEventFired(Typ.L2_SENDER);
		}
		if(e.getSource()==getTable(Typ.L2_RECEIVER).getSelectionModel()){
			listSelectionEventFired(Typ.L2_RECEIVER);
		}
		autoSave();
	}
	/**
	 * Diese Funktion bildet die eigentliche Schnittstelle zum MulticastController und ermoeglicht
	 * die GUI zu einem bestimmen Zeitpunkt zu updaten.
	 */
	public void viewUpdate(){
		Typ typ = getSelectedTab();
		if(typ!=Typ.UNDEFINED){
			updateTablePart(typ);
			updateGraph(typ);
			getPanStatus(typ).updateTraffic(this);
			getPanStatus(typ).getLb_multicast_count().setText(getMCCount(typ)+" "+lang.getProperty("status.mcTotal"));
			PanelTabbed tabpart = null;
			switch(typ){
				case L2_SENDER: tabpart=f.getPanel_sen_lay2(); break;
				case L3_SENDER: tabpart=f.getPanel_sen_lay3(); break;
				case L2_RECEIVER: tabpart=f.getPanel_rec_lay2(); break;
				case L3_RECEIVER: tabpart=f.getPanel_rec_lay3(); break;
			}
			int[] selectedRows = tabpart.getTable().getSelectedRows();
			getPanStatus(typ).getLb_multicasts_selected().setText(selectedRows.length+" "+lang.getProperty("status.mcSelected")+" ");
		}
	}
	/**
	 * Diese Funktion liest die akutellen Benutzereingaben in der GUI aus und speichert sie
	 * in den 4 UserInputData Objekten und gibt sie weiter zum speichern in der permanenten
	 * Konfigurationsdatei.
	 */
	public void submitInputData(){
		// TODO [MH] GUIConfigaenderungen hier
		// Hier sind die gesamten GUI Einstellungen auszulesen zum Speichern
			inputData_S3.setSelectedTab(getSelectedTab());
			inputData_S2.setSelectedTab(getSelectedTab());
			inputData_R3.setSelectedTab(getSelectedTab());
			inputData_R2.setSelectedTab(getSelectedTab());
			inputData_S3.setSelectedRowsArray(getSelectedRows(Typ.L3_SENDER));
			inputData_S2.setSelectedRowsArray(getSelectedRows(Typ.L2_SENDER));
			inputData_R3.setSelectedRowsArray(getSelectedRows(Typ.L3_RECEIVER));
			inputData_R2.setSelectedRowsArray(getSelectedRows(Typ.L2_RECEIVER));
			inputData_S3.setNetworkInterface(getPanConfig(Typ.L3_SENDER).getSelectedSourceIndex());
			inputData_S2.setNetworkInterface(getPanConfig(Typ.L2_SENDER).getSelectedSourceIndex());			
			inputData_S3.setGroupadress(getPanConfig(Typ.L3_SENDER).getTf_groupIPaddress().getText());
			inputData_S2.setGroupadress(getPanConfig(Typ.L2_SENDER).getTf_groupIPaddress().getText());	
			inputData_R3.setGroupadress(getPanConfig(Typ.L3_RECEIVER).getTf_groupIPaddress().getText());
			inputData_R2.setGroupadress(getPanConfig(Typ.L2_RECEIVER).getTf_groupIPaddress().getText());
			inputData_S3.setPort(getPanConfig(Typ.L3_SENDER).getTf_udp_port().getText());
			inputData_S2.setPort(getPanConfig(Typ.L2_SENDER).getTf_udp_port().getText());
			inputData_R3.setPort(getPanConfig(Typ.L3_RECEIVER).getTf_udp_port().getText());
			inputData_R2.setPort(getPanConfig(Typ.L2_RECEIVER).getTf_udp_port().getText());		
			inputData_S3.setTtl(getPanConfig(Typ.L3_SENDER).getTf_ttl().getText());
			inputData_S2.setTtl(getPanConfig(Typ.L2_SENDER).getTf_ttl().getText());			
			inputData_S3.setPacketrate(getPanConfig(Typ.L3_SENDER).getTf_packetrate().getText());
			inputData_S2.setPacketrate(getPanConfig(Typ.L2_SENDER).getTf_packetrate().getText());		
			inputData_S3.setPacketlength(getPanConfig(Typ.L3_SENDER).getTf_udp_packetlength().getText());
			inputData_S2.setPacketlength(getPanConfig(Typ.L2_SENDER).getTf_udp_packetlength().getText());		
			inputData_S3.setActiveButton(getPanConfig(Typ.L3_SENDER).getTb_active().isSelected());
			inputData_S2.setActiveButton(getPanConfig(Typ.L2_SENDER).getTb_active().isSelected());
			inputData_R3.setActiveButton(getPanConfig(Typ.L3_RECEIVER).getTb_active().isSelected());
			inputData_R2.setActiveButton(getPanConfig(Typ.L2_RECEIVER).getTb_active().isSelected());	
			inputData_S3.setSelectedRows(getSelectedRows(Typ.L3_SENDER));
			inputData_S2.setSelectedRows(getSelectedRows(Typ.L2_SENDER));
			inputData_R3.setSelectedRows(getSelectedRows(Typ.L3_RECEIVER));
			inputData_R2.setSelectedRows(getSelectedRows(Typ.L2_RECEIVER));
			inputData_S3.setIsAutoSaveEnabled(""+f.getMi_autoSave().isSelected());
			inputData_S2.setIsAutoSaveEnabled(""+f.getMi_autoSave().isSelected());
			inputData_R3.setIsAutoSaveEnabled(""+f.getMi_autoSave().isSelected());
			inputData_R2.setIsAutoSaveEnabled(""+f.getMi_autoSave().isSelected());
			
			Vector<UserInputData> packet = new Vector<UserInputData>();
			packet.add(inputData_S3);
			packet.add(inputData_R3);
			packet.add(inputData_S2);
			packet.add(inputData_R2);
			mc.autoSave(packet);
	}
	/**
	 * Hilfsfunktion zur Bestimmung des UserInputData Objekts anhand des Typs.
	 * @param typ Programmteil fuer welchen das UserInputData Objekt angefordert wird
	 * @return Gibt das UserInputData Objekt des entsprechenden Typs zurueck
	 */
	public UserInputData getUserInputData(Typ typ){
		UserInputData ret = null;
		switch(typ){
			case L3_SENDER: ret = inputData_S3; break;
			case L3_RECEIVER: ret = inputData_R3; break;
			case L2_SENDER: ret = inputData_S2; break;
			case L2_RECEIVER: ret = inputData_R2; break;
		}
		return ret;
	}
	/**
	 *	liest die UserInputData f�r einen bestimmten Programmteil, 
	 *	ordnet die Tabellenspalten entsprechend an und setzt die Sichtbarkeit der Tabellenspalten.
	 * @param input UserInputData Objekt welches aus der permanenten Konfigurationsdatei gelesen wird
	 * @param typ Bestimmt den Programmteil f�r welchen die Tabelle angepasst werden soll
	 */
	public void setColumnSettings(UserInputData input, Typ typ) {
		System.out.println("input: " + input);
		System.out.println("typ: " + typ);
		ArrayList<TableColumn> columns = getPanTabbed(typ).getColumns();
		ArrayList<Integer> saved_visibility = input.getColumnVisbility();
		ArrayList<Integer> saved_order = input.getColumnOrder();
		int columnCount = getTable(typ).getColumnCount();
		for(int i = 0 ; i < columnCount ; i++){
			getTable(getSelectedTab()).getColumnModel().removeColumn(getTable(getSelectedTab()).getColumnModel().getColumn(0));
		}
		//System.out.println("saved visibility size"+saved_visibility.size());
		for(int i = 0; i < saved_visibility.size() ; i++){
			getTable(getSelectedTab()).getColumnModel().addColumn(columns.get(saved_visibility.get(i).intValue()));
		}
		getUserInputData(typ).setColumnOrder(saved_order);
		getUserInputData(typ).setColumnVisibility(saved_visibility);
	}
	/**
	 * Funktion welche die aktuellen Nutzereingaben im Programm speichert.
	 */
	public void autoSave(){
		if(initFinished && f.isAutoSaveEnabled()){
			submitInputData();
		}
	}
	/**
	 * Funktion welche bei Programmstart die Automatische 
	 */
	public void loadAutoSave() {
		Vector<UserInputData> loaded = mc.loadAutoSave();
		//System.out.println("loaded.size "+loaded.size());
		if((loaded != null) && (loaded.size()==4)){
			inputData_S3 = loaded.get(0);
			inputData_R3 = loaded.get(1);
			inputData_S2 = loaded.get(2);
			inputData_R2 = loaded.get(3);		
			loadAutoSavePart(inputData_S3, Typ.L3_SENDER);
			loadAutoSavePart(inputData_R3, Typ.L3_RECEIVER);
			loadAutoSavePart(inputData_S2, Typ.L2_SENDER);
			loadAutoSavePart(inputData_R2, Typ.L2_RECEIVER);
			f.setLastConfigs(mc.getLastConfigs(), false);
			//System.out.println("size: "+mc.getLastConfigs().size());
		}
		initFinished=true;
	}
	/**
	 * Hilfsfunktion zum teilweise laden der Autosave Date, unterschieden nach Programmteil
	 * welche sie betreffen
	 * @param die zu ladenden UserInputData
	 * @param typ der zu den UserInputData zugeh�rige Programmtetil
	 */
	public void loadAutoSavePart(UserInputData data, Typ typ){
		//System.out.println("typ: "+typ);
		switch(data.getTyp()){
			case L3_SENDER: f.getTabpane().setSelectedIndex(0); break;
			case L3_RECEIVER: f.getTabpane().setSelectedIndex(1); break;
			case L2_SENDER: f.getTabpane().setSelectedIndex(2); break;
			case L2_RECEIVER: f.getTabpane().setSelectedIndex(3); break;
			default:  f.getTabpane().setSelectedIndex(0);
		}
		f.setAutoSave((data.isAutoSaveEnabled()));
		//System.out.println("setgroupIP: "+data.getGroupadress());
		getPanConfig(typ).getTf_groupIPaddress().setText(data.getGroupadress());
		//System.out.println("setport: "+data.getPort());
		getPanConfig(typ).getTf_udp_port().setText(data.getPort());
		//System.out.println("setactive: "+data.isActive());
		if(data.isActive()){
			//System.out.println("toggle bt");
			setTBactive(true, typ);
		}
		if(typ == Typ.L3_SENDER){
			//System.out.println("adapter index: "+data.getSourceAdressIndex());
			getPanConfig(typ).getCb_sourceIPaddress().setSelectedIndex(data.getSourceAdressIndex());
			//System.out.println("packetrate: "+data.getPacketrate());
			getPanConfig(typ).getTf_packetrate().setText(data.getPacketrate());
			//System.out.println("ttl: "+data.getTtl());
			getPanConfig(typ).getTf_ttl().setText(data.getTtl());
			//System.out.println("length: "+data.getPacketlength());
			getPanConfig(typ).getTf_udp_packetlength().setText(data.getPacketlength());
		}
	}

	@Override
	/**
	 * Listener welcher darauf reagiert wenn das Fenster Object aktiviert wird
	 */
	public void windowActivated(WindowEvent e) {
		
	}
	@Override
	/**
	 * Listener welcher darauf reagiert wenn das Fenster geschlossen wird
	 */
	public void windowClosed(WindowEvent e) {
		
	}
	@Override
	/**
	 * Listener welcher darauf reagiert wenn das Fenster ge�ffnet wird
	 */
	public void windowClosing(WindowEvent e) {
		closeProgram();
	}
	@Override
	/**
	 * Listener welcher darauf reagiert wenn das Fenster Object deaktiviert wird
	 */
	public void windowDeactivated(WindowEvent e) {
		
	}
	@Override
	/**
	 * Listener welcher darauf reagiert wenn das Fenster de-minimiert wurde
	 */
	public void windowDeiconified(WindowEvent e) {
		
	}
	@Override
	/**
	 * Listener welcher darauf reagiert wenn das Fenster minimiert wurde
	 */
	public void windowIconified(WindowEvent e) {
		
	}
	@Override
	/**
	 * Listener welcher darauf reagiert wenn das Fenster ge�ffnet wird
	 */
	public void windowOpened(WindowEvent e) {
		
	}

	public void setGUIConfig(GUIData data) {
		
		this.guidata = data;
		LanguageManager.setCurrentLanguage(data.getLanguage());
		this.f.reloadLanguage();
		this.f.repaint();
		
		for (int i=0;i<LanguageManager.languages.length;i++){
			if (this.f.getMi_languages()[i].getText().equals(data.getLanguage())) this.f.getMi_languages()[i].setSelected(true);
			else this.f.getMi_languages()[i].setSelected(false);
		}
		
		this.f.setBaseTitle(data.getWindowName());
		this.f.updateTitle();
		
		String title = "";
		
		// close all invisible taps
		
		this.f.getTabpane().closeTab(title);
		
		//pane.closeTab(pane.getTitleAt(i));
		//pane.remove(i);
		//pane.lookIfWeCan();
		
		if(data.getL2_RECEIVER() == GUIData.TabState.invisible) {
			title = " "+lang.getProperty("tab.l2r")+" ";
			for(int i=0; i<this.f.getTabpane().getTabCount(); ++i) 
				if(this.f.getTabpane().getTitleAt(i).equals(title)) {
					this.f.getTabpane().closeTab(this.f.getTabpane().getTitleAt(i));
					this.f.getTabpane().remove(i);
				}
		}
		if(data.getL3_RECEIVER() == GUIData.TabState.invisible) {
			title = " "+lang.getProperty("tab.l3r")+" ";
			for(int i=0; i<this.f.getTabpane().getTabCount(); ++i) 
				if(this.f.getTabpane().getTitleAt(i).equals(title)) {
					this.f.getTabpane().closeTab(this.f.getTabpane().getTitleAt(i));
					this.f.getTabpane().remove(i);
				}
		}
		if(data.getL2_SENDER() == GUIData.TabState.invisible) {
			title = " "+lang.getProperty("tab.l2s")+" ";
			for(int i=0; i<this.f.getTabpane().getTabCount(); ++i) 
				if(this.f.getTabpane().getTitleAt(i).equals(title)) {
					this.f.getTabpane().closeTab(this.f.getTabpane().getTitleAt(i));
					this.f.getTabpane().remove(i);
				}
		}
		if(data.getL3_SENDER() == GUIData.TabState.invisible) {
			title = " "+lang.getProperty("tab.l3s")+" ";
			for(int i=0; i<this.f.getTabpane().getTabCount(); ++i) 
				if(this.f.getTabpane().getTitleAt(i).equals(title)) {
					this.f.getTabpane().closeTab(this.f.getTabpane().getTitleAt(i));
					this.f.getTabpane().remove(i);
				}
		}
		
		
		if(data.getL2_RECEIVER() == GUIData.TabState.visible || data.getL2_RECEIVER() == GUIData.TabState.selected) {
			this.f.getTabpane().openTab("open_layer2_r");
		}
		System.out.println(data.getL3_RECEIVER().toString());
		if(data.getL3_RECEIVER() == GUIData.TabState.visible || data.getL3_RECEIVER() == GUIData.TabState.selected) {
			this.f.getTabpane().openTab("open_layer3_r");
		}
		System.out.println(data.getL2_SENDER().toString());
		if(data.getL2_SENDER() == GUIData.TabState.visible || data.getL2_SENDER() == GUIData.TabState.selected) {
			this.f.getTabpane().openTab("open_layer2_s");
		}
		System.out.println(data.getL3_SENDER().toString());
		if(data.getL3_SENDER() == GUIData.TabState.visible || data.getL3_RECEIVER() == GUIData.TabState.selected) {
			this.f.getTabpane().openTab("open_layer3_s");
		}
		
		// select the selected tab
		
		if(data.getL2_RECEIVER() == GUIData.TabState.selected) {
			title = " "+lang.getProperty("tab.l2r")+" ";
			for(int i=0; i<this.f.getTabpane().getTabCount(); ++i) 
				if(this.f.getTabpane().getTitleAt(i).equals(title))
					this.f.getTabpane().setSelectedIndex(i);
		}
		if(data.getL3_RECEIVER() == GUIData.TabState.selected) {
			title = " "+lang.getProperty("tab.l3r")+" ";
			for(int i=0; i<this.f.getTabpane().getTabCount(); ++i) 
				if(this.f.getTabpane().getTitleAt(i).equals(title))
					this.f.getTabpane().setSelectedIndex(i);
		}
		if(data.getL2_SENDER() == GUIData.TabState.selected) {
			title = " "+lang.getProperty("tab.l2s")+" ";
			for(int i=0; i<this.f.getTabpane().getTabCount(); ++i) 
				if(this.f.getTabpane().getTitleAt(i).equals(title))
					this.f.getTabpane().setSelectedIndex(i);
		}
		if(data.getL3_SENDER() == GUIData.TabState.selected) {
			title = " "+lang.getProperty("tab.l3s")+" ";
			for(int i=0; i<this.f.getTabpane().getTabCount(); ++i) 
				if(this.f.getTabpane().getTitleAt(i).equals(title))
					this.f.getTabpane().setSelectedIndex(i);
		}
		
		this.f.getTabpane().lookIfWeCan();
		
	}
}

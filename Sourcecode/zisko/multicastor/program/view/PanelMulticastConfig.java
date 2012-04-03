package zisko.multicastor.program.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.Border;

import zisko.multicastor.program.controller.ViewController;
import zisko.multicastor.program.data.MulticastData.Typ;
import zisko.multicastor.program.lang.LanguageManager;
import zisko.multicastor.program.model.InputValidator;
import zisko.multicastor.program.model.NetworkAdapter;
import zisko.multicastor.program.model.NetworkAdapter.IPType;
import zisko.multicastor.program.view.MiscBorder.BorderTitle;
import zisko.multicastor.program.view.MiscBorder.BorderType;
/**
 * Das KonfigurationPanel f�r Multicasts (links unten im Programm).
 * Dient zum Einstellen und Erstellen von Multicast Sendern und Receivern
 * @author Daniel Becker
 *
 */
@SuppressWarnings("serial")
public class PanelMulticastConfig extends JPanel {
	private JTextField tf_groupIPaddress;
	private WideComboBox cb_sourceIPaddress;
	private JTextField tf_udp_port;
	private JTextField tf_ttl;
	private JTextField tf_udp_packetlength;
	private JTextField tf_packetrate;
	//private JSlider;
	private JPanel pan_groupIPaddress;
	private JPanel pan_sourceIPaddress;
	private JPanel pan_udp_port;
	private JPanel pan_ttl;
	private JPanel pan_packetlength;
	private JPanel pan_packetrate;
	private JToggleButton bt_active;
	private JButton bt_enter;
	private LanguageManager lang;
	private MiscBorder mainBorder;
	
	public WideComboBox getCb_sourceIPaddress() {
		return cb_sourceIPaddress;
	}
	/**
	 * Konstruktor welcher das komplette Configuration Panel initialisiert
	 * @param ctrl Ben�tigte Referenz zum GUI Controller.
	 * @param typ Gibt an zu welchem Programmteil das Panel geh�rt.
	 */
	public PanelMulticastConfig(ViewController ctrl, Typ typ){ 
		lang=LanguageManager.getInstance();
		setBorder(mainBorder = new MiscBorder(lang.getProperty("miscBorder.mcConfig")));
		setLayout(null);
		setPreferredSize(new Dimension(225, 239));
		createAddressFields(ctrl, typ); //GUI Elements for IPv4
		createGUIstandard(typ);
		setToolTips(typ);

	}
	/**
	 * Initialisiert die Standard Textfelder des KonfigurationsPanels
	 * @param typ Gibt an zu welchem Programmteil das Panel geh�rt.
	 */
	private void createGUIstandard(Typ typ) {
		add(bt_enter);
		add(pan_groupIPaddress);
		if(typ == Typ.L3_RECEIVER || typ == Typ.L3_SENDER)
			add(pan_udp_port);
		add(bt_active);
		//V1.5: typ==Typ.L3_SENDER || typ==Typ.L2_SENDER hinzugef�gt
		if(typ==Typ.L3_SENDER || typ==Typ.L2_SENDER ){
			add(pan_packetrate);
			add(pan_packetlength);
			if(typ == Typ.L3_SENDER)
				add(pan_ttl);
		}
	}
	
	private void setToolTips(Typ typ) {
		if (typ == Typ.L3_RECEIVER || typ == Typ.L3_SENDER) {
			tf_groupIPaddress.setToolTipText(
				"<html>" +
				"Select a Multicast IP Address, either IPv4 or IPv6.<br />" +
				"IPv4: 224.0.0.0 to 239.255.255.255<br />" +
				"IPv6: FF00:: to FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF</html>"
			);
			cb_sourceIPaddress.setToolTipText("Specify the IP address first, then select an interface");
			tf_udp_port.setToolTipText("Specify an UDP port (1 to 65535).");
			
			if (typ == Typ.L3_SENDER) {
				tf_ttl.setToolTipText("Specify the time to live (1 to 32)");
				tf_packetrate.setToolTipText("Specify the packetrate (1 to 65535)");
				tf_udp_packetlength.setToolTipText(
					"<html>" +
					"Specify the packetlength<br />" +
					"IPv4: 52 to 65507<br />" +
					"IPv6: 52 to 65527" +
					"</html>"
				);
			}
		} else if (typ == Typ.L2_RECEIVER || typ == Typ.L2_SENDER) {
			tf_groupIPaddress.setToolTipText("Select a multicast MAC Address (80:00:00:00:00:00 to ff:ff:ff:ff:ff:fe)");
			cb_sourceIPaddress.setToolTipText("Select a network interface for multicast");
			
			if(typ == Typ.L2_SENDER) {
				tf_packetrate.setToolTipText("Specify the packetrate (1 to 65535)");
				tf_udp_packetlength.setToolTipText("Specfiy the packetlength (64 to 1520 Byte)");
			}
		}
	}
	
	public void reloadLanguage(){
		bt_active.setText(lang.getProperty("button.inactive"));
		bt_enter.setText(lang.getProperty("button.add"));
		mainBorder.setTitle(lang.getProperty("miscBorder.mcConfig"));
	}
	
	/**
	 * Funktion welche die spezifischen Textfelder f�r einen Programmteil erstellt.
	 * @param ctrl Ben�tigte Referenz zum GUI Controller.
	 * @param typ Gibt an zu welchem Programmteil das Panel geh�rt.
	 */
	private void createAddressFields(ViewController ctrl, Typ typ) {
		
		bt_active = new JToggleButton(lang.getProperty("button.inactive"));
		bt_active.setForeground(Color.red);
		bt_active.setFont(MiscFont.getFont(0,11));
		pan_groupIPaddress=new JPanel();
		pan_sourceIPaddress=new JPanel();
		pan_udp_port=new JPanel();
		bt_enter = new JButton(lang.getProperty("button.add"));
		bt_enter.addActionListener(ctrl);
		bt_enter.setBounds(115,204,100,25);
		bt_enter.setFont(MiscFont.getFont(0,11));
		bt_enter.setEnabled(false);
		
		//V1.5 [FH] Use cb_sourceIPadress for all kinds
		cb_sourceIPaddress = new WideComboBox();
		if (typ == Typ.L3_RECEIVER || typ == Typ.L3_SENDER) {
			cb_sourceIPaddress.addItem(lang.getProperty("config.message.ipFirst"));
		} else {
			cb_sourceIPaddress.addItem("");
		}
		cb_sourceIPaddress.setBounds(5,15,205,20);
		cb_sourceIPaddress.setFont(MiscFont.getFont(0,12));
		cb_sourceIPaddress.setBorder(null);
		cb_sourceIPaddress.addItemListener(ctrl);
		
		pan_sourceIPaddress.add(cb_sourceIPaddress,BorderLayout.CENTER);
		add(pan_sourceIPaddress);
		
		//V1.5: Layer 2 und Layer 3 Tabs hinzugef�gt: typ==Typ.L2_SENDER || typ==Typ.L3_SENDER
		if(typ==Typ.L2_SENDER || typ==Typ.L3_SENDER){
			
			pan_packetrate=new JPanel();
			pan_packetlength=new JPanel();
			pan_ttl=new JPanel();
			pan_packetrate.setLayout(null);
			pan_packetlength.setLayout(null);
			pan_ttl.setLayout(null);
			pan_packetrate.setBounds(5,140,105,40);
			pan_packetlength.setBounds(115,140,105,40);
			pan_ttl.setBounds(115,100,105,40);
			pan_packetrate.setBorder(MiscBorder.getBorder(BorderTitle.RATE, BorderType.NEUTRAL));
			pan_packetlength.setBorder(MiscBorder.getBorder(BorderTitle.LENGTH, BorderType.NEUTRAL));
			pan_ttl.setBorder(MiscBorder.getBorder(BorderTitle.TTL, BorderType.NEUTRAL));
			tf_udp_packetlength = new JTextField();
			if (typ == Typ.L3_RECEIVER || typ == Typ.L3_SENDER) {
				tf_udp_packetlength.setText(lang.getProperty("config.message.ipFirstShort"));
			}
			tf_packetrate = new JTextField();
			tf_ttl = new JTextField();
			tf_udp_packetlength.setBounds(5,15,95,20);
			tf_ttl.setBounds(5,15,95,20);
			tf_packetrate.setBounds(5,15,95,20);
			tf_packetrate.setBorder(null);
			tf_ttl.setBorder(null);
			tf_udp_packetlength.setBorder(null);
			tf_packetrate.getDocument().addDocumentListener(ctrl);
			tf_udp_packetlength.getDocument().addDocumentListener(ctrl);
			tf_ttl.getDocument().addDocumentListener(ctrl);
			tf_ttl.setFont(MiscFont.getFont(0,14));
			tf_packetrate.setFont(MiscFont.getFont(0,14));
			tf_udp_packetlength.setFont(MiscFont.getFont(0,14));
			tf_udp_packetlength.setHorizontalAlignment(JTextField.CENTER);
			tf_ttl.setHorizontalAlignment(JTextField.CENTER);
			tf_packetrate.setHorizontalAlignment(JTextField.CENTER);
			pan_packetlength.add(tf_udp_packetlength,BorderLayout.CENTER);
			pan_ttl.add(tf_ttl,BorderLayout.CENTER);
			pan_packetrate.add(tf_packetrate,BorderLayout.CENTER);
		}
		
		//V1.5 [FH] Use this bounds for sender and receiver
		pan_udp_port.setBounds(5,100,105,40);
		
		pan_groupIPaddress.setLayout(null);
		pan_sourceIPaddress.setLayout(null);
		pan_sourceIPaddress.setBounds(5,60,215,40);
		pan_udp_port.setLayout(null);
		pan_udp_port.setBorder(MiscBorder.getBorder(BorderTitle.PORT, BorderType.NEUTRAL));
		pan_groupIPaddress.setBounds(5,20,215,40);
		bt_active.setBounds(10,204,100,25);
		bt_active.setFocusable(false);
		bt_active.addActionListener(ctrl);	
		
		//V1.5: Added new Tabs
		if (typ == Typ.L3_SENDER || typ == Typ.L3_RECEIVER){
			pan_groupIPaddress.setBorder(MiscBorder.getBorder(BorderTitle.L3GROUP, BorderType.NEUTRAL));
			pan_sourceIPaddress.setBorder(MiscBorder.getBorder(BorderTitle.L3SOURCE, BorderType.NEUTRAL));
		} else if (typ == Typ.L2_SENDER || typ == Typ.L2_RECEIVER){
			pan_groupIPaddress.setBorder(MiscBorder.getBorder(BorderTitle.L2Group, BorderType.NEUTRAL));
			pan_sourceIPaddress.setBorder(MiscBorder.getBorder(BorderTitle.L2Source, BorderType.NEUTRAL));
		}

		tf_udp_port = new JTextField();		
		tf_groupIPaddress = new JTextField();
		pan_udp_port.add(tf_udp_port,BorderLayout.CENTER);
		//cb_sourceIPaddress.setPreferredSize(new Dimension(500,20));
		tf_groupIPaddress.setBounds(5,15,205,20);		
		tf_udp_port.setBounds(5,15,95,20);
		tf_groupIPaddress.setFont(MiscFont.getFont(0,14));
		tf_udp_port.setFont(MiscFont.getFont(0,14));
		tf_udp_port.setHorizontalAlignment(JTextField.CENTER);
		tf_groupIPaddress.setHorizontalAlignment(JTextField.CENTER);
//		cb_sourceIPaddress.setHorizontalAlignment(JTextField.CENTER);
		tf_groupIPaddress.setBorder(null);
		tf_udp_port.setBorder(null);
		tf_groupIPaddress.getDocument().addDocumentListener(ctrl);
//		cb_sourceIPaddress.getDocument().addDocumentListener(ctrl);
		tf_udp_port.getDocument().addDocumentListener(ctrl);
		pan_groupIPaddress.add(tf_groupIPaddress,BorderLayout.CENTER);
				
	}

	public JPanel getPan_udp_port() {
		return pan_udp_port;
	}

	public JPanel getPan_ttl() {
		return pan_ttl;
	}

	public JPanel getPan_packetlength() {
		return pan_packetlength;
	}

	public JPanel getPan_packetrate() {
		return pan_packetrate;
	}

	public JPanel getPan_groupIPaddress() {
		return pan_groupIPaddress;
	}

	public JPanel getPan_sourceIPaddress() {
		return pan_sourceIPaddress;
	}

	public JTextField getTf_groupIPaddress() {
		return tf_groupIPaddress;
	}

	public JComboBox getTf_sourceIPaddress() {
		return cb_sourceIPaddress;
	}
	public JTextField getTf_udp_port() {
		return tf_udp_port;
	}

	public JTextField getTf_ttl() {
		return tf_ttl;
	}

	public JTextField getTf_udp_packetlength() {
		return tf_udp_packetlength;
	}

	public JTextField getTf_packetrate() {
		return tf_packetrate;
	}

	public JToggleButton getTb_active() {
		return bt_active;
	}

	public JButton getBt_enter() {
		return bt_enter;
	}

	public void setTf_groupIPaddress(JFormattedTextField tfGroupIPaddress) {
		tf_groupIPaddress = tfGroupIPaddress;
	}
	public void setTf_udp_port(JTextField tfUdpPort) {
		tf_udp_port = tfUdpPort;
	}

	public void setTf_ttl(JTextField tfTtl) {
		tf_ttl = tfTtl;
	}

	public void setTf_udp_packetlength(JTextField tfUdpPacketlength) {
		tf_udp_packetlength = tfUdpPacketlength;
	}

	public void setTf_packetrate(JTextField tfPacketrate) {
		tf_packetrate = tfPacketrate;
	}

	public void setTb_active(JToggleButton cbActive) {
		bt_active = cbActive;
	}

	public void setBt_enter(JButton btEnter) {
		bt_enter = btEnter;
	}
	public String getSourceIP(int i, IPType iptype){
		// TODO Hier muessen die Networkadapter fuer MMRP referenziert werden.
		if (iptype == IPType.IPv4) {
			return NetworkAdapter.getipv4Adapters().get(i).toString().substring(1);
		} else if (iptype == IPType.IPv6) {
			return NetworkAdapter.getipv6Adapters().get(i).toString().substring(1).split("%")[0];
		}
		return null;
	}

	public InetAddress getSelectedAddress(Typ typ, IPType iptype){
		// V1.5 [FH] Added L3 with IPv4 Stuff
		/* [MH] Changed to iptype */
		if(typ == Typ.L3_RECEIVER || typ == Typ.L3_SENDER)
			if(iptype == IPType.IPv4)
				return InputValidator.checkIPv4(getSourceIP(cb_sourceIPaddress.getSelectedIndex()-1, iptype));
			else
				return InputValidator.checkIPv6(getSourceIP(cb_sourceIPaddress.getSelectedIndex()-1, iptype));
		// TODO MMRP Eingabepruefung erstellen
		// This is wrong, we need to fix this for MMRP when we do L2 Panels
		else
			return InputValidator.checkIPv6(getSourceIP(cb_sourceIPaddress.getSelectedIndex()-1, iptype));
				
		
	}
	public int getSelectedSourceIndex(){
		return cb_sourceIPaddress.getSelectedIndex();
	}
}

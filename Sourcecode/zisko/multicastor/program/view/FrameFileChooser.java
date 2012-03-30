package zisko.multicastor.program.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Locale;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import zisko.multicastor.program.controller.ViewController;
/**
 * FileChooser fï¿½r Speichern und Laden von Config Files
 * @author Daniel Becker
 *
 */
@SuppressWarnings("serial")

//TODO: Diese Klasse sollte raus fliegen... Deshalb wurde hier auch nichts übersetzt!!!

public class FrameFileChooser extends JFrame {
	/**
	 * JFileChooser Menu welches angepasst wird je nach dem ob es sich um einen Speichern oder Laden Dialog handelt.
	 */
	private JFileChooser chooser;
	/**
	 * Filter welcher bestimmt das nur .xml Dateien gespeichert und geladen werden.
	 */
	private FileNameExtensionFilter filter;
	/**
	 * Hilfsvariable welche bestimmt um was fï¿½r eine Art von Dialog es sich handelt (Speichern/Laden).
	 */
	private boolean typeSave;
	/**
	 * Checkbox welche angibt ob der SenderV4 Programmteil gespeichert/geladen werden soll.
	 */
	private JCheckBox cb_senderL3;
	/**
	 * Checkbox welche angibt ob der SenderV6 Programmteil gespeichert/geladen werden soll.
	 */
	private JCheckBox cb_senderL2;
	/**
	 * Checkbox welche angibt ob der ReceiverV4 Programmteil gespeichert/geladen werden soll.
	 */
	private JCheckBox cb_receiverL3;
	/**
	 * Checkbox welche angibt ob der ReceiverV6 Programmteil gespeichert/geladen werden soll.
	 */
	private JCheckBox cb_receiverL2;
	/**
	 * Checkbox welche angibt ob inkrementell geladen werden soll oder nicht.
	 */
	private JCheckBox cb_incremental;
	/**
	 * Kontruktir des FileChoosers
	 * @param ctrl Benï¿½igte Referenz zum GUI Controller
	 * @param save gibt an ob es sich um einen Datei speichern Dialog handelt
	 */
	public FrameFileChooser(ViewController ctrl, boolean save){
		typeSave = save;
		initWindow();
		initFileChooser(ctrl);
		initSelection();
	}
	/**
	 * Initialisiert die Checkboxen des Filechoosers
	 */
	private void initSelection() {
		JPanel selection = new JPanel();
		selection.setLayout(null);
		selection.setPreferredSize(new Dimension(600,60));
		cb_senderL3 = new JCheckBox("Layer3 Sender",true);
		cb_senderL2 = new JCheckBox("Layer2 (not yet)",true);
		cb_receiverL3 = new JCheckBox("Layer3 Receiver",true);
		cb_receiverL2 = new JCheckBox("Layer2 (not yet)",true);
		
		cb_senderL3.setFont(MiscFont.getFont());
		cb_senderL2.setFont(MiscFont.getFont());
		cb_receiverL3.setFont(MiscFont.getFont());
		cb_receiverL2.setFont(MiscFont.getFont());
		
		cb_senderL3.setFocusable(false);
		cb_senderL2.setFocusable(false);
		cb_receiverL3.setFocusable(false);
		cb_receiverL2.setFocusable(false);
		
		cb_senderL3.setBounds(10,0,150,20);
		cb_senderL2.setBounds(10,30,150,20);
		cb_receiverL3.setBounds(170,0,150,20);
		cb_receiverL2.setBounds(170,30,150,20);
		
		
		selection.add(cb_senderL3);
		selection.add(cb_senderL2);
		selection.add(cb_receiverL3);
		selection.add(cb_receiverL2);
		if(!typeSave){
			cb_incremental = new JCheckBox("Incremental Load");
			cb_incremental.setFont(MiscFont.getFont());
			cb_incremental.setFocusable(false);
			cb_incremental.setBounds(220,0,120,20);
			selection.add(cb_incremental);
		}
		add(selection,BorderLayout.SOUTH);
	}
	/**
	 * Initialisiert die Dateiauswahl.
	 * @param ctrl Benï¿½tigte Referenz zum GUI Controller.
	 */
	private void initFileChooser(ViewController ctrl) {
		filter = new FileNameExtensionFilter("XML Config Files", "xml");
		chooser = new JFileChooser();
		if(typeSave){
			chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		}
		else{
			chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		}
		//chooser.setControlButtonsAreShown(false);
		chooser.setFileFilter(filter);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFont(MiscFont.getFont());
		chooser.addActionListener(ctrl);
		add(chooser, BorderLayout.CENTER);
	}
	/**
	 * Setzt den Fenstertitel fï¿½r Laden
	 */
	private void initLoad() {
		setTitle("Load Configuration");
		
	}
	/**
	 * Setzt den Fenstertitel fï¿½r Speichern
	 */
	private void initSave() {
		setTitle("Save Configuration");
		
	}
	/**
	 * Konfiguriert das Frame in welchem sich der FileChooser befindet
	 */
	private void initWindow(){
		setLayout(new BorderLayout());
		setSize(600,600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		if(typeSave){
			initSave();
		}
		else{
			initLoad();
		}
	}
	/**
	 * Funktion zum anzeigen und verbergen des FileChoosers
	 */
	public void toggle(){
		if(this.isVisible()){
			setVisible(false);
		}
		else{
			setVisible(true);
		}
	}
	public JFileChooser getChooser() {
		return chooser;
	}
	public FileNameExtensionFilter getFilter() {
		return filter;
	}
	public boolean isTypeSave() {
		return typeSave;
	}
	public JCheckBox getCb_senderL3() {
		return cb_senderL3;
	}
	public JCheckBox getCb_senderL2() {
		return cb_senderL2;
	}
	public JCheckBox getCb_receiverL3() {
		return cb_receiverL3;
	}
	public JCheckBox getCb_receiverL2() {
		return cb_receiverL2;
	}
	public JCheckBox getCb_incremental() {
		return cb_incremental;
	}
	public boolean isCbSenderL3Selected(){
		return cb_senderL3.isSelected();
	}
	public boolean isCbSenderL2Selected(){
		return cb_senderL2.isSelected();
	}
	public boolean isCbReceiverL3Selected(){
		return cb_receiverL3.isSelected();
	}
	public boolean isCbReceiverL2Selected(){
		return cb_receiverL2.isSelected();
	}
	public boolean isCbIncrementalSelected(){
		return cb_incremental.isSelected();
	}
	public String getSelectedFile(){
		if(chooser.getSelectedFile().getAbsolutePath().endsWith(".xml")){
			return ""+chooser.getSelectedFile().getAbsolutePath();
		}
		else{
			return ""+chooser.getSelectedFile().getAbsolutePath()+".xml";
		}
		
	}
}

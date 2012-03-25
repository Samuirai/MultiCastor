package zisko.multicastor.program.view;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.*;
import zisko.multicastor.program.controller.ViewController;
/**
 * Das Kontrollpanel f�r Multicasts. 
 * Mit diesem Panel k�nnen Multicasts gestartet, gestoppt und gel�scht werden.
 * @author Daniel Becker
 *
 */
@SuppressWarnings("serial")
public class PanelMulticastControl extends JPanel {
	
	private JButton start_stop;
	private JButton delete;
	private JButton select_deselect_all;
	private JButton newmulticast;

	/**
	 * Konstruktor f�r das Kontrollpanel welcher alle zugeh�rigen GUI Komponenten initialisiert.
	 * @param ctrl Ben�tigete Referenz zum GUI Controller
	 */
	public PanelMulticastControl(ViewController ctrl){
		setBorder(new MiscBorder("MultiCast Control"));
		setLayout(null);
		setPreferredSize(new Dimension(225,85));
		initButtons(ctrl);
	}
	/**
	 * Hilfsfunktion welche die Buttons des Kontrollpanels initialisiert
	 * @param ctrl Ben�tigete Referenz zum GUI Controller
	 */
	private void initButtons(ViewController ctrl) {
		
		start_stop = new JButton("Start / Stop");
		delete = new JButton("Delete");
		select_deselect_all = new JButton("(De-)Select All");
		newmulticast = new JButton("New");
		start_stop.setEnabled(false);
		delete.setEnabled(false);
		
		Font myFont = new Font("Helvetica", Font.BOLD,11);
		start_stop.setFont(myFont);
		delete.setFont(MiscFont.getFont());
		select_deselect_all.setFont(MiscFont.getFont());
		newmulticast.setFont(MiscFont.getFont());
		
		start_stop.setFocusable(false);
		delete.setFocusable(false);
		select_deselect_all.setFocusable(false);
		newmulticast.setFocusable(false);
		
		select_deselect_all.setBounds(10,20,100,25);
		delete.setBounds(115,20,100,25);
		newmulticast.setBounds(10,50,100,25);
		start_stop.setBounds(115,50,100,25);
		
		start_stop.addActionListener(ctrl);
		select_deselect_all.addActionListener(ctrl);
		newmulticast.addActionListener(ctrl);
		delete.addActionListener(ctrl);
		add(start_stop);
		add(newmulticast);
		add(select_deselect_all);
		add(delete);
	}

	public JButton getStartStop() {
		return start_stop;
	}
	
	public JButton getDelete() {
		return delete;
	}

	public JButton getSelectDeselect_all() {
		return select_deselect_all;
	}

	public void setStartStop(JButton start) {
		this.start_stop = start;
	}

	public void setDelete(JButton delete) {
		this.delete = delete;
	}

	public void setSelectDeselect_all(JButton selectAll) {
		select_deselect_all = selectAll;
	}

	public JButton getNewmulticast() {
		return newmulticast;
	}
}

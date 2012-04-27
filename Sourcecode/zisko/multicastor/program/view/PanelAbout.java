package zisko.multicastor.program.view;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

import zisko.multicastor.program.lang.LanguageManager;

/**
 * About-Panel. Enthält Informationen zu den Entwicklern und der Lizenz.
 * @author Christopher Westphal
 */

@SuppressWarnings("serial")
public class PanelAbout extends javax.swing.JPanel{
	
	private javax.swing.JScrollPane sp_about; /* Scrollbalken horizontal und vertikal (falls Fenster verkleinert wird). */
	private javax.swing.JPanel panel_about_outer;
    private javax.swing.JPanel panel_about_inner;
    private javax.swing.JLabel lb_image;
    private LanguageManager lang;
    
    private JLabel labelDev = new JLabel();
    private JLabel labelLicense = new JLabel();

    /* Namen der Entwickler als Konstanten. */
    public static final String developer1 = "Jonas Traub";
    public static final String developer2 = "Matthis Hauschild";
    public static final String developer3 = "Sebastian Koralewski";
    public static final String developer4 = "Filip Haase";
    public static final String developer5 = "Fabian Fäßler";
    public static final String developer6 = "Christopher Westphal";
    
    public PanelAbout() {
    	initComponents();
    }

    public void reloadLanguage() {
    	
		labelDev.setText(lang.getProperty("about.labelDev"));
		labelLicense.setText(lang.getProperty("about.license"));
    	
    }
    
    /* Komponenten definieren und anzeigen. */
    private void initComponents() {

    	lang = LanguageManager.getInstance();
    	
        sp_about = new javax.swing.JScrollPane();
        panel_about_inner = new javax.swing.JPanel();
        panel_about_outer = new javax.swing.JPanel();

        setPreferredSize(new java.awt.Dimension(985, 395));

        sp_about.setMinimumSize(new java.awt.Dimension(0, 0));
        sp_about.setPreferredSize(new java.awt.Dimension(980, 395));

        /* Benutzt wird ein verschachteltes Box-Layout. Zuerst wird das Box-Layout in
         * X-Richtung genutzt (panel_about_outer), um den linken Abstand vom Inhalt 
         * zum Rand zu erreichen. Das zweite Feld in X-Richtung ist wiederum ein Box-Layout,
         * jedoch in Y-Richtung (panel_about_inner). */
        
        panel_about_outer.setPreferredSize(new java.awt.Dimension(965, 380));
        panel_about_outer.setRequestFocusEnabled(false);
        
        /* Box-Layout in Y-Richtung definieren für Inhalt. */
        BoxLayout panel_aboutLayout = new BoxLayout(panel_about_inner, BoxLayout.Y_AXIS);
        panel_about_inner.setLayout(panel_aboutLayout);
        
        /* MC 2.0 Logo. */
        lb_image = new javax.swing.JLabel();
        lb_image.setIcon(new javax.swing.ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/mcastor20_logo.png")));
        panel_about_inner.add(lb_image);
        
        /* Lizenzhinweis. */
        labelLicense.setText(lang.getProperty("about.license"));
        labelLicense.setFont(new Font("Helvetica", Font.BOLD, 11));
        panel_about_inner.add(labelLicense);
        
        /* Platzhalter (20 Pixel hoch) */
        panel_about_inner.add(Box.createRigidArea(new Dimension(0, 20)));
        
        /* Entwickler-Überschrift */
        labelDev.setText(lang.getProperty("about.labelDev"));
        labelDev.setFont(new Font("Helvetica", Font.BOLD, 12));
        panel_about_inner.add(labelDev);  
        
        /* Platzhalter (5 Pixel hoch) */
        panel_about_inner.add(Box.createRigidArea(new Dimension(0, 5)));
        
        /* Auflistung der Entwickler */
        panel_about_inner.add(new JLabel(developer1));
        panel_about_inner.add(new JLabel(developer2));
        panel_about_inner.add(new JLabel(developer3));
        panel_about_inner.add(new JLabel(developer4));
        panel_about_inner.add(new JLabel(developer5));
        panel_about_inner.add(new JLabel(developer6));
        
        /* Box-Layout in X-Richtung definieren, um den generierten Inhalte einzufügen. */
        BoxLayout panel_aboutLayoutOuter = new BoxLayout(panel_about_outer, BoxLayout.X_AXIS);
        panel_about_outer.setLayout(panel_aboutLayoutOuter);
        
        /* Abstand zum linken Rand (20 Pixel). */
        panel_about_outer.add(Box.createRigidArea(new Dimension(20, 0)));
        panel_about_outer.add(panel_about_inner);

        /* Definiert das äußere Box-Layout als "Viewport" für den Scrollbereich. */
        sp_about.setViewportView(panel_about_outer);

        /* Definiert weitere Einstellungen für den Scrollbereich. */
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp_about, javax.swing.GroupLayout.DEFAULT_SIZE, 985, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(sp_about, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        
    }
}

package zisko.multicastor.program.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class PanelPlus extends JPanel implements ActionListener{
	
	private FrameMain frame;
    private DraggableTabbedPane pane;
	public PanelPlus(FrameMain pFrame){
		frame = pFrame;
		pane = frame.getTabpane();
		initComponents();
	}
	
    private void initComponents() {
    	GridBagLayout gridBayLayout = new GridBagLayout();
        this.setLayout(gridBayLayout);
    	
    	JButton ipv4_s = new JButton("Layer2 Sender");
    	ipv4_s.setMinimumSize(new Dimension(100, 100));
    	gridBayLayout.setConstraints(ipv4_s, new GridBagConstraints (0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0 ));
    	ipv4_s.setIcon(new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/ipv4sender.png")));
    	ipv4_s.setFont(MiscFont.getFont(0,17));
    	ipv4_s.setActionCommand("open_layer2_s");
    	ipv4_s.addActionListener(this);
    	add(ipv4_s);
    	JLabel ipv4_s_t = new JLabel("Opens the Layer2 (with MMRP) Sender Panel. If the Tab is currently Closed the Tab gets opened. If it's open it get focused.");
    	gridBayLayout.setConstraints(ipv4_s_t, new GridBagConstraints (1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0 ));
    	add(ipv4_s_t);
    	
    	JButton ipv4_r = new JButton("Layer2 Receiver");
    	gridBayLayout.setConstraints(ipv4_r, new GridBagConstraints (0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0 ));
    	ipv4_r.setIcon(new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/ipv4receiver.png")));
    	ipv4_r.setFont(MiscFont.getFont(0,17));
    	ipv4_r.setActionCommand("open_layer2_r");
    	ipv4_r.addActionListener(this);
    	add(ipv4_r);
    	JLabel ipv4_r_t = new JLabel("Opens the Layer2 Receiver Panel. If the Tab is currently Closed the Tab gets opened. If it's open it get focused.");
    	gridBayLayout.setConstraints(ipv4_r_t, new GridBagConstraints (1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0 ));    	
    	add(ipv4_r_t);
    	
    	JButton ipv6_s = new JButton("Layer3 Sender");
    	gridBayLayout.setConstraints(ipv6_s, new GridBagConstraints (0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0 ));
    	ipv6_s.setIcon(new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/ipv6sender.png")));
    	ipv6_s.setFont(MiscFont.getFont(0,17));
    	ipv6_s.setActionCommand("open_layer3_s");
    	ipv6_s.addActionListener(this);
    	add(ipv6_s);
    	JLabel ipv6_s_t = new JLabel("Opens the Layer3 Sender Panel. If the Tab is currently Closed the Tab gets opened. If it's open it get focused.");
    	gridBayLayout.setConstraints(ipv6_s_t, new GridBagConstraints (1, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0 ));    	
    	add(ipv6_s_t);
    	
    	JButton ipv6_r = new JButton("Layer3 Receiver");
    	gridBayLayout.setConstraints(ipv6_r, new GridBagConstraints (0, 3, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0 ));
    	ipv6_r.setIcon(new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/ipv6receiver.png")));
    	ipv6_r.setFont(MiscFont.getFont(0,17));
    	ipv6_r.setActionCommand("open_layer3_r");
    	ipv6_r.addActionListener(this);
    	add(ipv6_r);
    	JLabel ipv6_r_t = new JLabel("Opens the Layer3 Receiver Panel. If the Tab is currently Closed the Tab gets opened. If it's open it get focused.");
    	gridBayLayout.setConstraints(ipv6_r_t, new GridBagConstraints (1, 3, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0 ));    	
    	add(ipv6_r_t);
    	
    	JButton about = new JButton("About");
    	gridBayLayout.setConstraints(about, new GridBagConstraints (0, 4, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0 ));
    	about.setIcon(new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/about.png")));
    	about.setFont(MiscFont.getFont(0,17));
    	about.setActionCommand("open_about");
    	about.addActionListener(this);
    	add(about);
    	JLabel about_t = new JLabel("Opens the About Panel. If the Tab is currently Closed the Tab gets opened. If it's open it get focused.");
    	gridBayLayout.setConstraints(about_t, new GridBagConstraints (1, 4, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0 ));    	
    	add(about_t);    	
    }

	public void actionPerformed(ActionEvent e) {
		frame.getTabpane().openTab(e.getActionCommand());
	}
}

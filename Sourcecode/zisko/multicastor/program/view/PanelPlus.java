package zisko.multicastor.program.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class PanelPlus extends JPanel implements ActionListener{
	
	private FrameMain frame;
    private JTabbedPane pane;
	public PanelPlus(FrameMain pFrame){
		frame = pFrame;
		pane = frame.getTabpane();
		initComponents();
	}
	
    private void initComponents() {
    	GridBagLayout gridBayLayout = new GridBagLayout();
        this.setLayout(gridBayLayout);
    	
    	JButton ipv4_s = new JButton("IPv4 Sender");
    	ipv4_s.setMinimumSize(new Dimension(100, 100));
    	gridBayLayout.setConstraints(ipv4_s, new GridBagConstraints (0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0 ));
    	ipv4_s.setIcon(new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/ipv4sender.png")));
    	ipv4_s.setFont(MiscFont.getFont(0,17));
    	ipv4_s.setActionCommand("ipv4_s");
    	ipv4_s.addActionListener(this);
    	add(ipv4_s);
    	JLabel ipv4_s_t = new JLabel("Opens the IPv4 Sender Panel. If the Tab is currently Closed the Tab gets opened. If it's open it get focused.");
    	gridBayLayout.setConstraints(ipv4_s_t, new GridBagConstraints (1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0 ));
    	add(ipv4_s_t);
    	
    	JButton ipv4_r = new JButton("IPv4 Receiver");
    	gridBayLayout.setConstraints(ipv4_r, new GridBagConstraints (0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0 ));
    	ipv4_r.setIcon(new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/ipv4receiver.png")));
    	ipv4_r.setFont(MiscFont.getFont(0,17));
    	ipv4_r.setActionCommand("ipv4_r");
    	ipv4_r.addActionListener(this);
    	add(ipv4_r);
    	JLabel ipv4_r_t = new JLabel("Opens the IPv4 Receiver Panel. If the Tab is currently Closed the Tab gets opened. If it's open it get focused.");
    	gridBayLayout.setConstraints(ipv4_r_t, new GridBagConstraints (1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0 ));    	
    	add(ipv4_r_t);
    	
    	JButton ipv6_s = new JButton("IPv6 Sender");
    	gridBayLayout.setConstraints(ipv6_s, new GridBagConstraints (0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0 ));
    	ipv6_s.setIcon(new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/ipv6sender.png")));
    	ipv6_s.setFont(MiscFont.getFont(0,17));
    	ipv6_s.setActionCommand("ipv6_s");
    	ipv6_s.addActionListener(this);
    	add(ipv6_s);
    	JLabel ipv6_s_t = new JLabel("Opens the IPv6 Sender Panel. If the Tab is currently Closed the Tab gets opened. If it's open it get focused.");
    	gridBayLayout.setConstraints(ipv6_s_t, new GridBagConstraints (1, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0 ));    	
    	add(ipv6_s_t);
    	
    	JButton ipv6_r = new JButton("IPv6 Receiver");
    	gridBayLayout.setConstraints(ipv6_r, new GridBagConstraints (0, 3, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0 ));
    	ipv6_r.setIcon(new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/ipv6receiver.png")));
    	ipv6_r.setFont(MiscFont.getFont(0,17));
    	ipv6_r.setActionCommand("ipv6_r");
    	ipv6_r.addActionListener(this);
    	add(ipv6_r);
    	JLabel ipv6_r_t = new JLabel("Opens the IPv6 Receiver Panel. If the Tab is currently Closed the Tab gets opened. If it's open it get focused.");
    	gridBayLayout.setConstraints(ipv6_r_t, new GridBagConstraints (1, 3, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0 ));    	
    	add(ipv6_r_t);
    	
    	JButton about = new JButton("About");
    	gridBayLayout.setConstraints(about, new GridBagConstraints (0, 4, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0 ));
    	about.setIcon(new ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/about.png")));
    	about.setFont(MiscFont.getFont(0,17));
    	about.setActionCommand("about");
    	about.addActionListener(this);
    	add(about);
    	JLabel about_t = new JLabel("Opens the About Panel. If the Tab is currently Closed the Tab gets opened. If it's open it get focused.");
    	gridBayLayout.setConstraints(about_t, new GridBagConstraints (1, 4, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0 ));    	
    	add(about_t);    	
    }

	public void actionPerformed(ActionEvent e) {
		Map<String, Integer> openTabs = new HashMap<String, Integer>();
		
		pane = frame.getTabpane();
		int openTabsCount = pane.getTabCount();
		
		for(int i =0; i < openTabsCount; i++)
			openTabs.put(pane.getTitleAt(i),i);
		
		if(e.getActionCommand().equals("ipv6_r")){
			if(openTabs.containsKey(" Receiver IPv6 "))
				pane.setSelectedIndex(openTabs.get(" Receiver IPv6 "));
			else{
				pane.insertTab(" Receiver IPv6 ", null, frame.getPanel_rec_ipv6(), null, openTabsCount-1);
				pane.setTabComponentAt(openTabsCount-1, new ButtonTabComponent(pane, "/zisko/multicastor/resources/images/ipv6receiver.png"));
				pane.setSelectedIndex(openTabsCount-1);
			}
		}else if(e.getActionCommand().equals("ipv6_s")){
			if(openTabs.containsKey(" Sender IPv6 "))
				pane.setSelectedIndex(openTabs.get(" Sender IPv6 "));
			else{
				pane.insertTab(" Sender IPv6 ", null, frame.getPanel_sen_ipv6(), null, openTabsCount-1);
				pane.setTabComponentAt(openTabsCount-1, new ButtonTabComponent(pane, "/zisko/multicastor/resources/images/ipv6sender.png"));
				pane.setSelectedIndex(openTabsCount-1);
			}
		}else if(e.getActionCommand().equals("ipv4_s")){
			if(openTabs.containsKey(" Sender IPv4 "))
				pane.setSelectedIndex(openTabs.get(" Sender IPv4 "));
			else{
				pane.insertTab(" Sender IPv4 ", null, frame.getPanel_sen_ipv4(), null, openTabsCount-1);
				pane.setTabComponentAt(openTabsCount-1, new ButtonTabComponent(pane, "/zisko/multicastor/resources/images/ipv4sender.png"));
				pane.setSelectedIndex(openTabsCount-1);
			}
		}else if(e.getActionCommand().equals("ipv4_r")){
			if(openTabs.containsKey(" Receiver IPv4 "))
				pane.setSelectedIndex(openTabs.get(" Receiver IPv4 "));
			else{
				pane.insertTab(" Receiver IPv4 ", null, frame.getPanel_rec_ipv4(), null, openTabsCount-1);
				pane.setTabComponentAt(openTabsCount-1, new ButtonTabComponent(pane, "/zisko/multicastor/resources/images/ipv4receiver.png"));
				pane.setSelectedIndex(openTabsCount-1);
			}
		}else if(e.getActionCommand().equals("about")){
			if(openTabs.containsKey(" About "))
				pane.setSelectedIndex(openTabs.get(" About "));
			else{
				pane.insertTab(" About ", null, frame.getPanel_about(), null, openTabsCount-1);
				pane.setTabComponentAt(openTabsCount-1, new ButtonTabComponent(pane, "/zisko/multicastor/resources/images/about.png"));
				pane.setSelectedIndex(openTabsCount-1);
			}
		}
		
	}
}

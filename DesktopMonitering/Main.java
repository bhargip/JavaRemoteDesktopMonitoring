
package DesktopMonitering;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.TableModel;

class Myframe extends JFrame
{
    MenuHandler menu_handler;
    JList com_list;
    JPanel panel1,panel2,panel3,panel4,top_panel,p1,p2;
    JSplitPane splitpane_H;
    JMenuBar menubar;
    JMenu option;
    JMenu message;
    JMenuItem add_com,remove_com,exit,reset,send_msg,check_con,start_mon,get_pro;
    JTabbedPane tabpanel;
    DefaultListModel listmodel;
    ImageReceive imageReceive;
    ClosableTabbedPane tabbedPane;
    Myframe(String frame_name) throws SocketException, IOException
    {
        super(frame_name);
        setVisible(true);
        setSize(1000,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        menu_handler=new MenuHandler(this);
        menubar=new JMenuBar();
        add(menubar);
        option=new JMenu("Options");
        message=new JMenu("Adv option");
        add_com=new JMenuItem("Add Computer");
        remove_com=new JMenuItem("Remove Computer");
        reset=new JMenuItem("Reset");
        exit=new JMenuItem("Exit");
        send_msg=new JMenuItem("Send Message");
        check_con=new JMenuItem("Check Connection");
        start_mon=new JMenuItem("Start Monitoring");
        get_pro=new JMenuItem("Get Process List");
        menubar.add(option);
        menubar.add(message);
        option.add(add_com);
        option.add(remove_com);
        option.add(reset);
        option.add(exit);
        message.add(start_mon);
        message.add(send_msg);
        message.add(check_con);
        message.add(get_pro);

        add_com.addActionListener(menu_handler);
        remove_com.addActionListener(menu_handler);
        reset.addActionListener(menu_handler);
        exit.addActionListener(menu_handler);
        send_msg.addActionListener(menu_handler);
        check_con.addActionListener(menu_handler);
        start_mon.addActionListener(menu_handler);
        get_pro.addActionListener(menu_handler);
        add(menubar,BorderLayout.PAGE_START);

        top_panel = new JPanel();
		top_panel.setLayout( new BorderLayout() );
        getContentPane().add(top_panel);
        //show all computers
        panel1 = new JPanel();
        panel1.setLayout( new BorderLayout() );
        listmodel=new DefaultListModel();
        com_list=new JList(listmodel);
        com_list.setLayoutOrientation(JList.VERTICAL);
        com_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        Dimension d=new Dimension(200,600);
        com_list.setPreferredSize(d);
        Font font=new Font(null,Font.BOLD, 14);
        com_list.setFont(font);
        panel1.add( com_list, BorderLayout.CENTER );
        //tabs
        panel2 = new JPanel();
        panel2.setLayout( new FlowLayout() );
        splitpane_H = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
		splitpane_H.setLeftComponent(panel1);
        splitpane_H.setDividerLocation(200);
        top_panel.add( splitpane_H, BorderLayout.CENTER );
        splitpane_H.setRightComponent(panel2);
        p1=new JPanel(new GridLayout(50,1));
        p2=new JPanel(new GridLayout(50,1));
        panel2.add(p1);
        panel2.add(p2);
    }

    private void addTabbedPane()
    {
        tabbedPane = new ClosableTabbedPane();
	panel2.add(tabbedPane);
    }
}

class ClosableTabbedPane extends JTabbedPane
{
    private TabCloseUI closeUI = new TabCloseUI(this);

    @Override
	public void paint(Graphics g)
	{
		super.paint(g);
		closeUI.paint(g);
	}

    @Override
	public void addTab(String title, Component component)
	{
		super.addTab(title+"", component);
	}


	public String getTabTitleAt(int index)
	{
		return super.getTitleAt(index).trim();
	}

	private class TabCloseUI implements MouseListener, MouseMotionListener
	{
		private ClosableTabbedPane  tabbedPane;
		private int closeX = 0 ,closeY = 0, meX = 0, meY = 0;
		private int selectedTab;
		private final int  width = 8, height = 8;
		private Rectangle rectangle = new Rectangle(0,0,width, height);
		private TabCloseUI(){}
		public TabCloseUI(ClosableTabbedPane pane)
		{

			tabbedPane = pane;
			tabbedPane.addMouseMotionListener(this);
			tabbedPane.addMouseListener(this);
		}
		public void mouseEntered(MouseEvent me) {}
		public void mouseExited(MouseEvent me) {}
		public void mousePressed(MouseEvent me) {}
		public void mouseClicked(MouseEvent me) {}
		public void mouseDragged(MouseEvent me) {}



		public void mouseReleased(MouseEvent me)
		{
			if(closeUnderMouse(me.getX(), me.getY()))
			{
				boolean isToCloseTab = tabAboutToClose(selectedTab);
				if (isToCloseTab && selectedTab > -1)
				{
                    InetAddress add=null;
                    String ad=tabbedPane.getTabTitleAt(selectedTab);
                    String a=null;
                    a=ad.substring(0,0)+ad.substring(0+1);
                    tabbedPane.removeTabAt(selectedTab);
                    System.out.println("add="+a);
                    try
                    {
                        add = InetAddress.getByName(a);
                    }
                    catch (UnknownHostException ex)
                    {
                        Logger.getLogger(ClosableTabbedPane.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try
                    {
                        MenuHandler.sendEndPacket(add);
                    }
                    catch (SocketException ex)
                    {
                        Logger.getLogger(ClosableTabbedPane.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    catch (IOException ex)
                    {
                        Logger.getLogger(ClosableTabbedPane.class.getName()).log(Level.SEVERE, null, ex);
                    }                                 
				}
				selectedTab = tabbedPane.getSelectedIndex();
			}
		}

		public void mouseMoved(MouseEvent me)
		{
			meX = me.getX();
			meY = me.getY();
			if(mouseOverTab(meX, meY))
			{
				controlCursor();
				tabbedPane.repaint();
			}
		}

		private void controlCursor()
		{
			if(tabbedPane.getTabCount()>0)
				if(closeUnderMouse(meX, meY))
				{
					tabbedPane.setCursor(new Cursor(Cursor.HAND_CURSOR));
					if(selectedTab > -1)
						tabbedPane.setToolTipTextAt(selectedTab, "Close " +tabbedPane.getTitleAt(selectedTab));
				}
				else
				{
					tabbedPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					if(selectedTab > -1)
						tabbedPane.setToolTipTextAt(selectedTab,"");
				}
		}

		private boolean closeUnderMouse(int x, int y)
		{
			rectangle.x = closeX;
			rectangle.y = closeY;
			return rectangle.contains(x,y);
		}

		public void paint(Graphics g)
		{
			int tabCount = tabbedPane.getTabCount();
			for(int j = 0; j < tabCount; j++)
				if(tabbedPane.getComponent(j).isShowing())
				{
					int x = tabbedPane.getBoundsAt(j).x + tabbedPane.getBoundsAt(j).width -width-5;
					int y = tabbedPane.getBoundsAt(j).y +5;
					drawClose(g,x,y);
					break;
				}
			if(mouseOverTab(meX, meY)){
				drawClose(g,closeX,closeY);
			}
		}

		private void drawClose(Graphics g, int x, int y) {
			if(tabbedPane != null && tabbedPane.getTabCount() > 0){
				Graphics2D g2 = (Graphics2D)g;
				drawColored(g2, isUnderMouse(x,y)? Color.RED : Color.WHITE, x, y);
			}
		}

		private void drawColored(Graphics2D g2, Color color, int x, int y)
		{
			g2.setStroke(new BasicStroke(5,BasicStroke.JOIN_ROUND,BasicStroke.CAP_ROUND));
			g2.setColor(Color.BLACK);
			g2.drawLine(x, y, x + width, y + height);
			g2.drawLine(x + width, y, x, y + height);
			g2.setColor(color);
			g2.setStroke(new BasicStroke(3, BasicStroke.JOIN_ROUND, BasicStroke.CAP_ROUND));
			g2.drawLine(x, y, x + width, y + height);
			g2.drawLine(x + width, y, x, y + height);

		}

		private boolean isUnderMouse(int x, int y)
		{
			if(Math.abs(x-meX)<width && Math.abs(y-meY)<height )
				return  true;
			return  false;
		}

		private boolean mouseOverTab(int x, int y)
		{
                    int tabCount = tabbedPane.getTabCount();
                    for(int j = 0; j < tabCount; j++)
                    if(tabbedPane.getBoundsAt(j).contains(meX, meY))
                    {
                        selectedTab = j;
                        closeX = tabbedPane.getBoundsAt(j).x + tabbedPane.getBoundsAt(j).width -width-5;
			closeY = tabbedPane.getBoundsAt(j).y +5;
                    }
                    return false;
		}
        }
        public boolean tabAboutToClose(int tabIndex)
	{
		return true;
	}
}

/*class MessageDialog extends JDialog
{
    TextArea msg;
    JButton send,cancel;
    public MessageDialog(Myframe frame)
    {
        super(frame);
        setSize(300,200);
        setVisible(true);
        setTitle("Write Message");
        msg=new TextArea();
        send=new JButton("Send");
        cancel=new JButton("Cancel");
        setLayout(new GridLayout(4,1));
        add(new Label("IPAddress:"));
        add(msg);
        add(send);
        add(cancel);
    }

}*/

public class Main
{
    public static void main(String[] args) throws SocketException, IOException
    {
       Myframe frame=new Myframe("Desktop Monitering");
    }

}

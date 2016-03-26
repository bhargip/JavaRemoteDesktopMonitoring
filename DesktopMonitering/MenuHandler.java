package DesktopMonitering;

import java.awt.Label;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.awt.event.*;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import jpcap.*;
import org.jnetpcap.*;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import jpcap.packet.Packet;

public class MenuHandler implements ActionListener
{
    Myframe frame;
    int temp=0,loop1;
    JpcapCaptor captor;
    InetAddress localhost = null;
    InetAddress address = null;
    //ServerSocket server;
    static int no=0;
    static int port=7000;
    JPanel[] pane=new JPanel[10];
    JPanel panel;
    int f=0;
    static int count=9000;
    MenuHandler(Myframe frame) throws IOException
    {
        this.frame=frame;
    }

    public void actionPerformed(ActionEvent e)
    {
        String action_command=e.getActionCommand();
        if(action_command.equals("Exit"))
        {
           System.exit(0);
        }
        else if(action_command.equals("Add Computer"))
        {
            frame.listmodel.removeAllElements();
            localhost = null;
            try
            {
                localhost = InetAddress.getLocalHost();
            }
            catch (UnknownHostException ex)
            {
                Logger.getLogger(MenuHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

            byte[] ip = localhost.getAddress();
            
            for (int i =33; i <= 35; i++)
            {
                ip[3] = (byte)i;
                address = null;
                try
                {
                    address = InetAddress.getByAddress(ip);
                    System.out.println("address:"+address);

                    if (address.isReachable(1000))
                    {
                        System.out.println("pinged");
                        frame.listmodel.addElement(address);
                    }
                    else
                    {
                        System.out.println("notrechable "+i);
                    }
                    
                }
                catch (UnknownHostException ex)
                {
                    Logger.getLogger(MenuHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (IOException ex)
                {
                    Logger.getLogger(MenuHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        else if(action_command.equals("Remove Computer"))
        {
            InetAddress add=null;
            add=(InetAddress) frame.com_list.getSelectedValue();
            if(frame.com_list.isSelectionEmpty())
            {
                JOptionPane.showMessageDialog(frame,"Please select IPAddress","Error",JOptionPane.ERROR_MESSAGE);
            }
            else
            {
                temp=frame.com_list.getSelectedIndex();
                frame.listmodel.removeElementAt(temp);
            }

        }
        else if(action_command.equals("Reset"))
        {
            frame.splitpane_H.resetToPreferredSizes();
        }
        else if(action_command.equals("Send Message"))
        {
            InetAddress add=(InetAddress) frame.com_list.getSelectedValue();
            if(frame.com_list.isSelectionEmpty())
            {
                JOptionPane.showMessageDialog(frame,"Please select IPAddress","Error",JOptionPane.ERROR_MESSAGE);
            }
            else
            {
                String msg;
                msg="";
                msg=JOptionPane.showInputDialog(frame,"Message will be sent to\n"+"IPAddress:"+add);
                System.out.println(msg);
                MessageSend ms=new MessageSend(msg,add);
                try
                {
                    ms.sendMessage();
                }
                catch (SocketException ex)
                {
                    Logger.getLogger(MenuHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (UnknownHostException ex)
                {
                    Logger.getLogger(MenuHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (IOException ex)
                {
                    Logger.getLogger(MenuHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        else if(action_command.equals("Check Connection"))
        {
            InetAddress add=null;
            add=(InetAddress) frame.com_list.getSelectedValue();
            if(frame.com_list.isSelectionEmpty())
            {
                JOptionPane.showMessageDialog(frame,"Please select IPAddress","Error",JOptionPane.ERROR_MESSAGE);
            }
            else
            {
                try
                {
                    if (add.isReachable(1500))
                    {
                        System.out.println("pinged");
                        JOptionPane.showMessageDialog(frame,add+" is connected","Connection",JOptionPane.INFORMATION_MESSAGE);
                    }
                    else
                    {
                        System.out.println("notreachable ");
                        JOptionPane.showMessageDialog(frame,add+" is not reachable","Error",JOptionPane.ERROR_MESSAGE);
                    }
                }
                catch (IOException ex)
                {
                    Logger.getLogger(MenuHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        else if(action_command.equals("Get Process List"))
        {
            System.out.println("Process List");
            
            byte[] data=new byte[100000];
            if(frame.com_list.isSelectionEmpty())
            {
                JOptionPane.showMessageDialog(frame,"Please select IPAddress","Error",JOptionPane.ERROR_MESSAGE);
            }
            else
            {
                int index=frame.com_list.getSelectedIndex();
                InetAddress add=(InetAddress)frame.listmodel.getElementAt(index);
                DatagramSocket serverSocket = null;
                try
                {
                    serverSocket = new DatagramSocket();
                }
                catch (SocketException ex) {
                    Logger.getLogger(MenuHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                byte[] hello=new byte[4];
                String po=count+"";
                hello=po.getBytes();
                DatagramPacket helloPacket=new DatagramPacket(hello, hello.length,add,9998);
                try
                {
                    serverSocket.send(helloPacket);
                }
                catch (IOException ex)
                {
                    Logger.getLogger(MenuHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("pack send");
                try
                {
                    ServerSocket server = new ServerSocket(count);
                    count++;
                    Socket client=server.accept();
                    InputStream is=client.getInputStream();
                    int i=is.read(data);
                    String process=new String(data);
                    frame.p1.removeAll();
                    frame.p2.removeAll();
                    frame.p1.add(new Label("IP:"+client.getInetAddress().toString()));
                    frame.p2.add(new Label("Name:"+add.getHostName()));
                    frame.p1.add(new JLabel("Process Name"));
                    frame.p2.add(new JLabel("Process Size"));
                    frame.p1.add(new JLabel("------------"));
                    frame.p2.add(new JLabel("------------"));
                    processFormat(process);
                }
                catch (IOException ex) {
                    Logger.getLogger(MenuHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        }
        else if(action_command.equals("Start Monitoring"))
        {
            
            if(frame.com_list.isSelectionEmpty())
            {
                JOptionPane.showMessageDialog(frame,"Please select IPAddress","Error",JOptionPane.ERROR_MESSAGE);
            }
            else
            {
                int index=frame.com_list.getSelectedIndex();
                InetAddress add=(InetAddress)frame.listmodel.getElementAt(index);
                int tabno=frame.tabbedPane.getTabCount();
                
                if(f==0)
                {
                    pane[no]=new JPanel();
                    ImageReceive imageReceive;
                    try
                    {
                        DatagramSocket serverSocket=new DatagramSocket();
                        byte[] hello=new byte[4];
                        String po=port+"";
                        hello=po.getBytes();
                        DatagramPacket helloPacket=new DatagramPacket(hello, hello.length,add,5555);
                        serverSocket.send(helloPacket);
                        System.out.println("port send");

                        imageReceive = new ImageReceive(frame,add,port);
                        Thread serviceImage=new Thread(imageReceive);
                        serviceImage.start();
                        no++;
                        port+=1;
                        System.out.println("no increment to:"+no);
                    }
                    catch(IOException ex)
                    {
                        Logger.getLogger(MenuHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

         }
    }
    void processFormat(String process)
    {
        StringTokenizer st=new StringTokenizer(process,"&");
        int n=st.countTokens(),i=0;
        char a='"';
        String b=Character.toString(a);
        while(i<n-2)
        {
            String n1=st.nextToken();
            StringTokenizer s=new StringTokenizer(n1,b);
            String pn=s.nextToken();
            s.nextToken();
            s.nextToken();
            s.nextToken();
            s.nextToken();
            s.nextToken();
            s.nextToken();
            s.nextToken();
            String ps=s.nextToken();
            i++;
            addRow(pn, ps);
            System.out.println("pn="+pn+" ps"+ps);
        }
    }
    public void addRow(String d1,String d2)
    {
        frame.p1.add(new JLabel(d1));
        frame.p2.add(new JLabel(d2));
        frame.panel2.setVisible(false);
        frame.panel2.setVisible(true);
    }
    static public void sendEndPacket(InetAddress add) throws SocketException, IOException
    {
        DatagramSocket serverSocket=new DatagramSocket();
        byte[] end=new byte[3];
        end="end".getBytes();
        DatagramPacket endPacket=new DatagramPacket(end, end.length,add,5556);
        serverSocket.send(endPacket);
        System.out.println("end packet send");
    }
}

/*class KeyListHandler extends KeyAdapter
{
    Myframe frame;
    int index;
    KeyListHandler(Myframe frame)
    {
        this.frame=frame;
    }
    @Override
    public void keyPressed(KeyEvent e)
    {
        if(e.getKeyCode()==KeyEvent.VK_ENTER)
        {
            index=frame.com_list.getSelectedIndex();
            InetAddress add=(InetAddress)frame.listmodel.getElementAt(index);
            frame.tabbedPane.addTab(""+add,new JPanel());
        }
    }
}*/




/*class MouseListHandler extends MouseAdapter
{
    Myframe frame;
    int index;
    MouseListHandler(Myframe frame)
    {
        this.frame=frame;
    }
    
    @Override
    public void mouseClicked(MouseEvent e)
    {
        //if(e.getKeyCode()==KeyEvent.VK_ENTER)
        {
            index=frame.com_list.getSelectedIndex();
            InetAddress add=(InetAddress)frame.listmodel.getElementAt(index);
            frame.tabbedPane.addTab("TAB " + add,new JPanel());
        }
    }
}*/

/*class ListHandler implements ListSelectionListener
{
    Myframe frame;
    int index;
    ListHandler(Myframe frame)
    {
        this.frame=frame;
    }
    public void valueChanged(ListSelectionEvent e)
    {

        index=frame.com_list.getSelectedIndex();
        InetAddress add=(InetAddress)frame.listmodel.getElementAt(index);
         frame.tabbedPane.addTab("TAB " + add,new JPanel());
    }
}*/
/*class PacketPrinter implements PacketReceiver
{
    public jpcap.packet.TCPPacket ip;

    public PacketPrinter() {
        System.out.println("PacketPrinter");
    }

    public void receivePacket(Packet packet)
    {
        System.out.println("Packet:"+packet);
        if(packet.data.length>0)
        {
            ip=(jpcap.packet.TCPPacket)packet;
        }
        System.out.println("IPAddress:"+ip.src_ip+"  PortNo:"+ip.src_port);
    }
}*/
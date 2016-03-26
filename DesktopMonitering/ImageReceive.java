/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DesktopMonitering;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 *
 * @author Jemish
 */
public class ImageReceive implements Runnable
{
    Myframe frame;
    static DatagramSocket serverSocket;
    byte[] Data,Buffer,hello;
    DatagramPacket imagePacket,helloPacket;
    InetAddress IPAddress,address;
    JPanel panel;
    Socket client;
    InputStream is;
    int flag=1;
    int index;
    int port;
    ServerSocket server;
    JPanel p;
    public ImageReceive(Myframe frame,InetAddress address,int port) throws SocketException, IOException
    {
        this.frame=frame;
        this.port=port;
        server=new ServerSocket(this.port);
        p=new JPanel();
        System.out.println(client);
        Buffer=new byte[100000];
        Data = new byte[100000];
        hello=new byte[5];
        imagePacket = new DatagramPacket(Buffer,Buffer.length);
        hello="hello".getBytes();
        this.address=address;
    }

    public synchronized void run()
    {
        frame.tabbedPane.addTab(""+address,p);
        popUp pop=new popUp(frame,address);
        try
        {
            Socket client = server.accept();
            is=client.getInputStream();
        }
        catch (IOException ex)
        {
            Logger.getLogger(ImageReceive.class.getName()).log(Level.SEVERE, null, ex);
        }

        while(true)
        {
            try
            {
                is.read(Data);
                ByteArrayInputStream bis = new ByteArrayInputStream(Data);
                BufferedImage image = ImageIO.read(bis);
                pop.show(image);
            }
            catch (IOException ex)
            {
                Logger.getLogger(ImageReceive.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public synchronized void DisplayImage(BufferedImage image)
    {

        if(flag==1)
        {
            System.out.println("first time");
            p.add(BorderLayout.CENTER, new JLabel(new ImageIcon(image)));
            flag++;
        }
        else
        {
            p.removeAll();
            p.add(BorderLayout.CENTER, new JLabel(new ImageIcon(image)));
            p.setVisible(false);
            p.setVisible(true);
            
        }
    }
}

class popUp extends JDialog
{
    JPanel panel1,panel2;
    JButton close;
    public popUp(Myframe frame,InetAddress add)
    {
        super(frame,""+add);
        setVisible(true);
        setSize(600, 400);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout(10,10));
        panel1=new JPanel();
        panel2=new JPanel();
        close=new JButton("Close");
        add(panel1,BorderLayout.CENTER);
        add(panel2,BorderLayout.SOUTH);
        panel2.add(close);
        close.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                InetAddress add=null;
                String ad=popUp.this.getTitle();
                String a=null;

                a=ad.substring(0,0)+ad.substring(0+1);
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
                popUp.this.dispose();
                System.out.println("Dialog closed");
                
            }
        });
    }
    void show(BufferedImage image)
    {
        panel1.removeAll();
        panel1.add(BorderLayout.CENTER, new JLabel(new ImageIcon(image)));
        panel1.setVisible(false);
        panel1.setVisible(true);
    }
}

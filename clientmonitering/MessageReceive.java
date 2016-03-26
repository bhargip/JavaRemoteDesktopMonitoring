/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package clientmonitering;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Jemish
 */
public class MessageReceive implements Runnable
{
    static DatagramSocket serverSocket;
    byte[] receiveData;
    byte[] sendData;
    JFrame frame;
    DatagramPacket receivePacket;
    InetAddress IPAddress;
    public MessageReceive(JFrame frame) throws SocketException
    {
        this.frame=frame;
        serverSocket = new DatagramSocket(9876);
        receiveData = new byte[50];
        receivePacket = new DatagramPacket(receiveData, receiveData.length);
    }
    public void run()
    {
        while(true)
        {
            try
            {
                serverSocket.receive(receivePacket);
            }
            catch (IOException ex)
            {
                Logger.getLogger(MessageReceive.class.getName()).log(Level.SEVERE, null, ex);
            }
            String sentence = new String( receivePacket.getData());
            IPAddress = receivePacket.getAddress();
            JOptionPane.showMessageDialog(frame,sentence,"Message From Agent "+IPAddress,JOptionPane.INFORMATION_MESSAGE);
            for(int i=0;i<50;i++)
            {
                receiveData[i]=0;
            }
        }
    }

}

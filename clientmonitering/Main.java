/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package clientmonitering;

import java.awt.AWTException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import javax.swing.JFrame;


public class Main
{
    static MessageReceive messageReceive;
    static JFrame frame;
    static ImageSend imageSend;
    static EndSending es;
    static ProcessSend ps;
    static DatagramSocket socket;
    static InetAddress ServerAddress;
    static String addressOfServer="192.168.31.250";
    public static void main(String[] args) throws SocketException, IOException, AWTException
    {
        frame = new JFrame("Message");
        //frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        messageReceive=new MessageReceive(frame);
        Thread threadMessageReceive=new Thread(messageReceive);
        threadMessageReceive.start();

        imageSend=new ImageSend(frame,addressOfServer);
        Thread threadImageSend=new Thread(imageSend);
        threadImageSend.start();

        es=new EndSending();
        Thread end=new Thread(es);
        end.start();

        ps=new ProcessSend(addressOfServer);
        Thread threadProcessSend=new Thread(ps);
        threadProcessSend.start();

        
    }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DesktopMonitering;

/**
 *
 * @author Jemish
 */
import java.io.*;
import java.net.*;
import javax.swing.JFrame;

public class MessageSend
{
    String message;
    InetAddress address;
    public MessageSend(String message,InetAddress address)
    {
        this.message=message;
        this.address=address;
    }
    public void sendMessage() throws SocketException, UnknownHostException, IOException
    {
        DatagramSocket socket = new DatagramSocket();
        byte[] send_data = new byte[1024];
        for(int i=0;i<send_data.length;i++)
        {
            send_data[i]=0;
        }
        
        send_data = message.getBytes();
        System.out.println("length="+send_data.length);
        DatagramPacket sendPacket = new DatagramPacket(send_data, send_data.length,address,9876);
        socket.send(sendPacket);
    }
    public void clear(byte[] send_data)
    {
        for(int i=0;i<send_data.length;i++)
        {
            send_data[i]=0;
        }
    }
}

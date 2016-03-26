package clientmonitering;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jemish
 */
public class EndSending implements Runnable
{
    DatagramSocket socket;
    byte[] end;
    DatagramPacket endPacket;
    public void run()
    {
        try
        {
            socket = new DatagramSocket(5556);
        }
        catch (SocketException ex)
        {
            Logger.getLogger(EndSending.class.getName()).log(Level.SEVERE, null, ex);
        }
        end=new byte[3];
        DatagramPacket endPacket = new DatagramPacket(end,end.length);
        try
        {
            socket.receive(endPacket);
        }
        catch (IOException ex)
        {
            Logger.getLogger(EndSending.class.getName()).log(Level.SEVERE, null, ex);
        }
        String ends = new String(endPacket.getData());
        if(ends.equals("end"))
        {
            ImageSend.setFlag();
        }
    }
}

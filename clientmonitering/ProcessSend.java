/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package clientmonitering;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jemish
 */
public class ProcessSend implements Runnable
{
    InetAddress IPAddress,ServerAddress;
    int lastIP,size,Port;
    DatagramSocket socket;
    Socket s;
    OutputStream os;
    String addressOfServer;
    static int flag=0;
    private final byte[] hello;
    public ProcessSend(String addressOfServer) throws SocketException, UnknownHostException
    {
        this.addressOfServer=addressOfServer;
        socket=new DatagramSocket(9998);
        hello=new byte[4];
        ServerAddress=InetAddress.getByName(addressOfServer);
    }
    public void run()
    {
        while(true)
        {
            DatagramPacket helloPacket = new DatagramPacket(hello,hello.length);
            try
            {
                System.out.println("Waiting for packet");
                socket.receive(helloPacket);
                String port=new String(helloPacket.getData());
                int po=Integer.parseInt(port);
                s=new Socket(addressOfServer,po);
                os=s.getOutputStream();
            }
            catch (IOException ex)
            {
                Logger.getLogger(ImageSend.class.getName()).log(Level.SEVERE, null, ex);
            }

            String p=getProcess();
            try
            {
                os.write(p.getBytes());
                s.close();
            }
            catch (IOException ex) {
                Logger.getLogger(ProcessSend.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
    public String getProcess()
    {
        Process p;
        Runtime runTime;
        String process = null;
        try
        {
            runTime = Runtime.getRuntime();
            p = runTime.exec("tasklist.exe /fo csv /nh");
            InputStream inputStream = p.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            process = "";
            while (line != null)
            {
                line = bufferedReader.readLine();
                process += line + "&";
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            System.out.println("Processes are read.");

        }
        catch (IOException e)
        {
            System.out.println("Exception arise during the read Processes");
            e.printStackTrace();
        }
        return process;
    }
    public String formetString(String pro)
    {
        String p=null;
        String line;
        StringTokenizer st=new StringTokenizer(pro,",");
        p="";
        while(st.hasMoreTokens())
        {
            line=st.nextToken()+",";
            p+=line;
            line=st.nextToken()+",";
            p+=line;
            st.nextToken();
            st.nextToken();
            line=st.nextToken()+",";
            p+=line;
        }
        System.out.println("Formet string:"+p);
        return p;
    }
}

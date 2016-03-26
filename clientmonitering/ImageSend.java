/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package clientmonitering;

import com.sun.org.apache.xalan.internal.xsltc.cmdline.getopt.GetOpt;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 *
 * @author Jemish
 */
public class ImageSend implements Runnable
{
    private static int COLOUR_OUTPUT=BufferedImage.TYPE_INT_RGB;
    JFrame frame;
    Robot robot;
    BufferedImage image = null;
    Dimension screenDim;
    private RenderedImage i;
    byte[] imageByteArray;
    byte[] data,hello;
    byte[] ip;
    public static String OUTPUT_FORMAT = "jpg";
    InetAddress IPAddress,ServerAddress;
    int lastIP,size,Port;
    DatagramSocket socket;
    Socket s;
    OutputStream os;
    String addressOfServer;
    static int flag=0;
    public ImageSend(JFrame frame,String addressOfServer) throws AWTException, UnknownHostException, SocketException, IOException
    {
        this.frame=frame;
        this.addressOfServer=addressOfServer;
        screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        robot = new Robot();
        socket=new DatagramSocket(5555);
        hello=new byte[4];
        ServerAddress=InetAddress.getByName(addressOfServer);
        IPAddress=InetAddress.getLocalHost();
        ip = IPAddress.getAddress();
        lastIP=(int)ip[3];
    }
    public void run()
    {
        while(true)
        {
            DatagramPacket helloPacket = new DatagramPacket(hello,hello.length);
            try
            {
                System.out.println("Waiting for port");
                socket.receive(helloPacket);
                String hello = new String(helloPacket.getData());
                int port=Integer.parseInt(hello);
                s=new Socket(addressOfServer,port);
                os=s.getOutputStream();
            }
            catch (IOException ex)
            {
                Logger.getLogger(ImageSend.class.getName()).log(Level.SEVERE, null, ex);
            }

            while(flag==0)
            {
                image = robot.createScreenCapture(new Rectangle(0, 0, (int) screenDim.getWidth(), (int) screenDim.getHeight()));
                image=shrink(image,0.5);
                try
                {
                    imageByteArray = bufferedImageToByteArray(image, OUTPUT_FORMAT);
                    sendImage(imageByteArray, ServerAddress, Port);
                    image=null;
                }
                catch (IOException ex)
                {
                    Logger.getLogger(ImageSend.class.getName()).log(Level.SEVERE, null, ex);
                }
                try
                {
                    Thread.sleep(10);
                }
                catch (InterruptedException ex)
                {
                    Logger.getLogger(ImageSend.class.getName()).log(Level.SEVERE, null, ex);
                }
            
            }
            System.out.println("End Sending");
        }

    }

    public synchronized void sendImage(byte[] Data,InetAddress ServerAddress,int Port) throws IOException
    {
        System.out.println("Image Send");
        os.write(Data);
        os.flush();
    }
    public static BufferedImage shrink(BufferedImage source, double factor)
    {
		int w = (int) (source.getWidth() * factor);
		int h = (int) (source.getHeight() * factor);
		return scale(source, w, h);
    }
    public static BufferedImage scale(BufferedImage source, int w, int h)
    {
		Image image = source.getScaledInstance(w, h, Image.SCALE_AREA_AVERAGING);
		BufferedImage result = new BufferedImage(w, h, COLOUR_OUTPUT);
		Graphics2D g = result.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return result;
    }
    public static byte[] bufferedImageToByteArray(BufferedImage image, String format) throws IOException
    {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, format, baos);
		return baos.toByteArray();
    }
    public static void setFlag()
    {
        flag=1;
    }
}

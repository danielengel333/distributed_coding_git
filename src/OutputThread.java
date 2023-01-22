import java.net.*;
import java.util.*;
import java.io.*;

public class OutputThread extends Thread
{
    private int port;
    private Object message;
    private Node node;
    public OutputThread(int port, Object message, Node node)
    {
        this.port = port;
        this.message = message;
        this.node = node;
    }
    @Override
    public void run()
    {
        try
        {
            // getting localhost ip
            InetAddress ip = InetAddress.getByName("localhost");

            // establish the connection with server port
            Socket s = new Socket(ip, port);

            // obtaining out streams
            ObjectOutputStream output_stream = new ObjectOutputStream(s.getOutputStream());

            output_stream.writeObject(message);

            // closing resources
            output_stream.close();
            s.close();

            //System.out.println("SENT MESSAGE");

        }
        catch(Exception e)
        {
            System.out.println("Output thread error");
            e.printStackTrace();
        }
    }
}


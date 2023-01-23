import java.net.*;
import java.util.*;
import java.io.*;

public class OutputThread extends Thread
{
    private int socket_index;
    private Object message;
    private Node node;
    public OutputThread(int socket_index, Object message, Node node)
    {
        this.socket_index = socket_index;
        this.message = message;
        this.node = node;
    }
    @Override
    public void run()
    {
        try
        {
            //System.out.println(this.node.getNeighbors_output_port()[this.socket_index]);
            //get access from lock
            //System.out.println("Momento 1");
            this.node.getSocket_semaphores()[this.socket_index].acquire();
            //System.out.println("Momento 2");

            //change restricted area
            InetAddress ip = InetAddress.getByName("localhost");
            Socket s = new Socket(ip, this.node.getNeighbors_output_port()[this.socket_index]);
            //System.out.println("Momento 3");
            ObjectOutputStream output_stream = new ObjectOutputStream(s.getOutputStream());
            output_stream.writeObject(message);

            output_stream.close();
            s.close();

            //free lock
            this.node.getSocket_semaphores()[this.socket_index].release();

            //System.out.println("SENT MESSAGE");

        }
        catch(Exception e)
        {
            System.out.println("Output thread error");
            e.printStackTrace();
        }
    }
}


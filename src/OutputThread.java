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
            //get access from lock
            this.node.getSocket_semaphores()[this.socket_index].acquire();

            //change restricted area
            Socket s = this.node.getSockets()[this.socket_index];
            ObjectOutputStream output_stream = new ObjectOutputStream(s.getOutputStream());
            output_stream.writeObject(message);
            output_stream.close();

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


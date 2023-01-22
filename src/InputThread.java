import java.net.*;
import java.util.*;
import java.io.*;

public class InputThread extends Thread
{
    private int port;
    private Node node;
    public InputThread(int port, Node node)
    {
        this.port = port;
        this.node = node;
    }
    @Override
    public void run()
    {
        try {
            ServerSocket ss = new ServerSocket(port);

            // running infinite loop for getting client request
            while (true)
            {
                Socket s = null;

                // if received info from all nodes, kill server
                if (this.node.getNum_visited() == node.getNum_of_nodes())
                {
                    break;
                }

                try
                {
                    // socket object to receive incoming client requests

                    s = ss.accept();

                    System.out.println("A new client is connected : " + s);

                    Thread t = new InputThreadHandler(s, node);
                    t.start();
                }
                catch (Exception e)
                {
                    System.out.println("error caught in InputThread");
                    s.close();
                    e.printStackTrace();
                    break;
                }
            }
        }
        catch (IOException e)
        {
            System.out.println("error caught in InputThread");
            e.printStackTrace();
        }
    }
}


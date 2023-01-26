import java.net.*;
import java.util.*;
import java.io.*;

public class InputThread extends Thread
{
    private static int count;
    private int count_self;
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

            //Checker checker = new Checker(this.node, ss);
            //checker.start();

            // running infinite loop for getting client request
            while (true)
            {
                // socket object to receive incoming client requests
                Socket s = ss.accept();
                Thread t = new InputThreadHandler(s, this.node, ss);
                t.start();
            }
        }
        catch (Exception e)
        {
            //System.out.println("error caught in InputThread");
            //e.printStackTrace();
        }
        System.out.println(count++);
    }
}


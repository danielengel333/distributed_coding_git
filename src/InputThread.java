import java.net.*;
import java.util.*;
import java.io.*;

public class InputThread extends Thread
{
    private static int count;
    private boolean isAlive;
    private int count_alive;
    private int port;
    private Node node;
    public InputThread(int port, Node node)
    {
        this.port = port;
        this.node = node;
        this.isAlive = true;
        //System.out.println("amount of input threads: " + count++);
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
                this.count_alive += 1;
                Thread t = new InputThreadHandler(s, this.node, ss);
                t.start();
            }
        }
        catch (Exception e)
        {
            //System.out.println("error caught in InputThread");
            //e.printStackTrace();
        }
        this.isAlive = false;
        System.out.println("amount of servers dead: " + (++count));

    }
}


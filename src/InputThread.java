import java.net.*;
import java.util.*;
import java.io.*;

public class InputThread extends Thread
{
    private int port;
    public InputThread(int port)
    {
        this.port = port;
    }
    @Override
    public void run()
    {
        try {

            ServerSocket ss = new ServerSocket(port);

            // running infinite loop for getting
            // client request
            while (true) {
                Socket s = null;

                try {
                    // socket object to receive incoming client requests
                    s = ss.accept();

                    System.out.println("A new client is connected : " + s);

                    // obtaining input and out streams
                    DataInputStream dis = new DataInputStream(s.getInputStream());
                    DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                    System.out.println("Assigning new thread for this client");

                    // create a new thread object
                    Thread t = new InputThreadHandler(s, dis, dos);

                    // Invoking the start() method
                    t.start();

                } catch (Exception e) {
                    s.close();
                    e.printStackTrace();
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}


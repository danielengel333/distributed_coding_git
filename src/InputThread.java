import java.net.*;
import java.util.*;
import java.io.*;

public class InputThread extends Thread
{
    private int[] neighbor_output_port;
    private int port;
    public InputThread(int port, int[] neighbor_output_port)
    {
        this.port = port;
        this.neighbor_output_port = neighbor_output_port;
    }
    @Override
    public void run()
    {
        try {

            ServerSocket ss = new ServerSocket(port);
            String sent = "asf";

            // running infinite loop for getting
            // client request
            while (true) {
                Socket s = null;

                try {
                    // socket object to receive incoming client requests

                    s = ss.accept();

                    System.out.println("A new client is connected : " + s);
                    //System.out.println(port);

                    // obtaining input and out streams
                    DataInputStream dis = new DataInputStream(s.getInputStream());
                    DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                    System.out.println("Assigning new thread for this client " + this.port);

                    // create a new thread object
                    //Thread t = new InputThreadHandler(s ,dos ,dis);

                    // Invoking the start() method
                    //t.start();
                    /*
                    sent = dis.readInt();
                    System.out.println(sent);
                    if (sent.equals("exit")){
                        s.close();
                        break;
                    }
                     */
                    System.out.println(dis.readInt());
                    s.close();
                    break;
                } catch (Exception e) {
                    System.out.println("InputThread Exception");
                    s.close();
                    e.printStackTrace();
                    break;
                }
            }
        }
        catch (IOException e)
        {
            System.out.println("InputThread Exception");
            e.printStackTrace();
        }
    }
}


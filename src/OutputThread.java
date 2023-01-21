import java.net.*;
import java.util.*;
import java.io.*;

public class OutputThread extends Thread
{
    private int port;
    private int message;
    public OutputThread(int port, int message)
    {
        this.port = port;
        this.message = message;
    }
    @Override
    public void run()
    {
        try
        {

            Scanner scn = new Scanner(System.in);

            // getting localhost ip
            InetAddress ip = InetAddress.getByName("localhost");

            // establish the connection with server port 5056
            Socket s = new Socket(ip, port);
            //System.out.println("HI");
            // obtaining input and out streams
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            // the following loop performs the exchange of
            // information between client and client handler
            while (true)
            {
                //System.out.println(dis.readUTF());
                //String tosend = scn.nextLine();
                int tosend = this.message;
                dos.writeInt(tosend);

                // If client sends exit,close this connection
                // and then break from the while loop
                if(tosend == this.message)
                {
                    System.out.println(tosend);
                    System.out.println("Closing this connection : " + s);
                    s.close();
                    System.out.println("Connection closed");
                    break;
                }

                // printing date or time as requested by client
                String received = dis.readUTF();
                System.out.println(received);
            }

            // closing resources
            scn.close();
            dis.close();
            dos.close();
            System.out.println("SENT MESSAGE");
        }catch(Exception e){
            System.out.println("Output thread error");
            e.printStackTrace();
        }
    }
}


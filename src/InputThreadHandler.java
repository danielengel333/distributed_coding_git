import java.net.*;
import java.util.*;
import java.io.*;
public class InputThreadHandler extends Thread
{
    private Socket s;
    private DataOutputStream outStream;
    private DataInputStream inputStream;
    private int[] outputPorts;
    public InputThreadHandler(Socket soc, DataOutputStream oStream, DataInputStream iStream){
        this.s = soc;
        this.outStream = oStream;
        this.inputStream =iStream;
    }
    @Override
    public void run(){
        while(true){
            try {
                String recived = this.inputStream.readUTF();
                if (recived.equals("exist")) {
                    System.out.println("Got it");
                    break;
                }
            }
            catch (Exception e){
                System.out.println("OOPs");
                break;
            }
        }
    }

}

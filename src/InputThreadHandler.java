import java.net.*;
import java.util.*;
import java.io.*;

public class InputThreadHandler extends Thread
{
    private Socket s;
    private Node node;
    private DataOutputStream outStream;
    private DataInputStream inputStream;
    private int[] outputPorts;
    public InputThreadHandler(Socket s, Node node)
    {
        this.s = s;
        this.node = node;
    }
    @Override
    public void run(){
        try
        {
            ObjectInputStream input_stream = new ObjectInputStream(s.getInputStream());
            Pair<Integer, Object> input = (Pair<Integer, Object>) input_stream.readObject();

            input_stream.close();
            s.close();

            int source_id = input.getKey();
            Object source_message = input.getValue();

            //check if we've already received information from the source
            if (this.node.getVisited()[source_id - 1] == 0)
            {
                //get access from lock
                this.node.getVisited_semaphore().acquire();


                //change restricted area
                this.node.getVisited()[source_id - 1] = 1;
                this.node.setNum_visited(this.node.getNum_visited() + 1);


                //free lock
                this.node.getVisited_semaphore().release();


                for (int output_port : this.node.getNeighbors_output_port())
                {
                    Thread t = new OutputThread(output_port, source_message, node);
                    t.start();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}

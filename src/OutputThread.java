import java.net.*;
import java.util.*;
import java.io.*;

public class OutputThread extends Thread
{
    private static int count;
    private int socket_index;
    private Object message;
    private Node node;
    private  ServerSocket ss;
    public OutputThread(int socket_index, Object message, Node node, ServerSocket ss)
    {
        this.socket_index = socket_index;
        this.message = message;
        this.node = node;
        this.ss = ss;
        System.out.println("amount of output threads: " + count++);
    }
    @Override
    public void run()
    {
        try
        {
            /*if (this.node.getNum_visited() == node.getNum_of_nodes())
            {
                this.ss.close();
            }*/

            int[][] sent_pair_matrix = this.node.getSent_pairs();
            int source_id = ((Pair<Integer,Object>)this.message).getKey();
            int dest_id = this.node.getNeighbors_id()[this.socket_index];

            if (sent_pair_matrix[source_id - 1][dest_id - 1] == 0)
            {
                /*System.out.println("Output thread: " + this.toString() + ", message from " +
                        ((Pair<Integer,Object>)this.message).getKey() + " to " +
                        this.node.getNeighbors_id()[this.socket_index] + " STARTING");*/

                //get access from lock
                this.node.getSent_pair_semaphore().acquire();

                //change restricted area
                sent_pair_matrix[source_id - 1][dest_id - 1] = 1;

                //free lock
                this.node.getSent_pair_semaphore().release();



                //get access from lock
                this.node.getSocket_semaphores()[this.socket_index].acquire();

                //change restricted area
                InetAddress ip = InetAddress.getByName("localhost");
                Socket s = new Socket(ip, this.node.getNeighbors_output_port()[this.socket_index]);
                ObjectOutputStream output_stream = new ObjectOutputStream(s.getOutputStream());
                output_stream.writeObject(message);

                output_stream.close();
                s.close();

                //free lock
                this.node.getSocket_semaphores()[this.socket_index].release();
            }
        }
        catch(Exception e)
        {
            this.node.getSocket_semaphores()[this.socket_index].release();
            //System.out.println("Output thread ERROR: " + this.toString());
            //e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "port: " + this.node.neighbors_output_port[this.socket_index] + ", node: " + this.node.getNodeId();
    }
}


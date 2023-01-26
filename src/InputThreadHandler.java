import java.net.*;
import java.util.*;
import java.io.*;

public class InputThreadHandler extends Thread
{
    private Socket s;
    private Node node;
    private ServerSocket ss;
    private int[] outputPorts;
    public InputThreadHandler(Socket s, Node node, ServerSocket ss)
    {
        this.s = s;
        this.node = node;
        this.ss = ss;
    }
    @Override
    public void run(){
        try
        {
            ObjectInputStream input_stream = new ObjectInputStream(s.getInputStream());
            Object o = input_stream.readObject();
            Pair<Integer, Object> input = (Pair<Integer, Object>)o;

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


                // update weight matrix

                //get access from lock
                this.node.getWeight_matrix_semaphore().acquire();

                //change restricted area
                double[][] mat = this.node.getWeight_matrix();
                for (Object pair : (Object[]) source_message)
                {
                    int id1 = ((Pair<Pair<Integer, Integer>,Double>)pair).getKey().getKey();
                    int id2 = ((Pair<Pair<Integer, Integer>,Double>)pair).getKey().getValue();
                    double weight = ((Pair<Pair<Integer, Integer>,Double>)pair).getValue();

                    mat[id1 - 1][id2 - 1] = weight;
                    mat[id2 - 1][id1 - 1] = weight;

                    /*double[] edges = this.node.getEdges();
                    int[] neighbors = this.node.getNeighbors_id();
                    for (int i = 0; i < edges.length; i++)
                    {
                        if ((this.node.getNodeId() == id1 && neighbors[i] == id2) ||
                                (this.node.getNodeId() == id2 && neighbors[i] == id1))
                        {
                            edges[i] = weight;
                        }
                    }*/
                }
                //free lock
                this.node.getWeight_matrix_semaphore().release();
            }

            Thread[] arr = new Thread[this.node.getNeighbors_output_port().length];
            for (int i = 0; i < this.node.getNeighbors_output_port().length; i++)
            {
                Thread t = new OutputThread(i, input, node, ss);
                arr[i] = t;
                t.start();
            }
            if (this.node.getNum_visited() == node.getNum_of_nodes())
            {
                this.ss.close();
            }

            /*while (true)
            {
                //System.out.println(this.node.getNodeId());
                if (this.node.getNum_visited() == node.getNum_of_nodes())
                {
                    this.ss.close();
                    break;
                }
            }*/

            /*for (int i = 0; i < this.node.getNeighbors_output_port().length; i++)
            {
                arr[i].join();
            }*/
        }
        catch (Exception e)
        {
            System.out.println("error caught in InputThreadHandler");
            e.printStackTrace();
        }

    }

}

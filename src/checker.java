import java.net.ServerSocket;
import java.net.*;
import java.util.*;
import java.io.*;

public class Checker extends Thread
{
    private Node node;
    private ServerSocket ss;
    public Checker(Node node, ServerSocket ss)
    {
        this.node = node;
        this.ss = ss;
    }

    @Override
    public void run()
    {
        try
        {
            while (true)
            {
                if (this.node.getNum_visited() == node.getNum_of_nodes())
                {
                    //System.out.println("node " + this.node.getNodeId() + " got all information");
                    this.ss.close();
                    break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

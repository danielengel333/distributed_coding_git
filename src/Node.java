import java.net.*;
import java.util.*;
import java.io.*;

public class Node extends Thread
{
    private int id;
    private int[] neighbors_id;
    private double[] edges;
    private int[] neighbors_input_port;
    private int[] neighbors_output_port;
    private int num_of_nodes;
    private int num_of_neighbors;
    private double[][] weight_matrix;

    public Node(int id, int[] neighbors_id, double[] edges, int[] neighbors_input_port, int[] neighbors__output_port, int num_of_nodes)
    {
        this.id = id;
        this.neighbors_id = neighbors_id;
        this.edges = edges;
        this.neighbors_input_port = neighbors_input_port;
        this.neighbors_output_port = neighbors__output_port;
        this.num_of_nodes = num_of_nodes;
        this.num_of_neighbors = this.edges.length;

        this.weight_matrix = new double[num_of_nodes][num_of_nodes];
        for (int i = 0; i < this.num_of_nodes; i++)
        {
            for (int j = 0; j < this.num_of_nodes; j++)
            {
                weight_matrix[i][j] = -1;
            }
        }
        for (int i = 0; i < this.edges.length; i++)
        {
            weight_matrix[this.id - 1][this.neighbors_id[i] - 1] = this.edges[i];
            weight_matrix[this.neighbors_id[i] - 1][this.id - 1] = this.edges[i];
        }
    }

    public void update_edge(int id2, double weight)
    {
        for (int i = 0; i < this.edges.length; i++)
        {
            if (neighbors_id[i] == id2)
            {
                this.edges[i] = weight;
                break;
            }
        }
    }

    public void print_graph()
    {
        for (int i = 0; i < this.num_of_nodes; i++)
        {
            for (int j = 0; j < this.num_of_nodes - 1; j++)
            {
                System.out.print(weight_matrix[i][j] + ", ");
            }
            System.out.println(weight_matrix[i][this.num_of_nodes - 1]);
        }
    }

    @Override
    public void run()
    {
        InputThread[] servers = new InputThread[num_of_neighbors];
        for (int i = 0; i < num_of_neighbors; i++)
        {
            InputThread server = new InputThread(neighbors_input_port[i]);
            servers[i] = server;
            server.run();
        }

        OutputThread[] clients = new OutputThread[num_of_neighbors];
        for (int i = 0; i < num_of_neighbors; i++)
        {
            OutputThread client = new OutputThread(neighbors_output_port[i]);
            clients[i] = client;
            client.run();
        }
    }
}


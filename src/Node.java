import java.net.*;
import java.util.*;
import java.io.*;
import java.util.concurrent.Semaphore;

public class Node extends Thread
{
    protected int id;
    protected int[] neighbors_id;
    protected double[] edges;
    protected int[] neighbors_input_port;
    protected int[] neighbors_output_port;
    protected int num_of_nodes;
    protected int num_of_neighbors;
    protected double[][] weight_matrix;
    protected Pair<Integer, Object> linkedState;
    protected int[] visited;
    protected int num_visited;
    protected Semaphore visited_semaphore;
    protected Semaphore weight_matrix_semaphore;
    protected Semaphore[] socket_semaphores;
    protected Socket[] sockets;

    public Node(int id, int[] neighbors_id, double[] edges, int[] neighbors_input_port, int[] neighbors__output_port, int num_of_nodes)
    {
        this.id = id;
        this.neighbors_id = neighbors_id;
        this.edges = edges;
        this.neighbors_input_port = neighbors_input_port;
        this.neighbors_output_port = neighbors__output_port;
        this.num_of_nodes = num_of_nodes;
        this.num_of_neighbors = this.edges.length;

        this.visited = new int[this.num_of_nodes];
        for (int i = 0; i < this.num_of_nodes; i++)
        {
            this.visited[i] = 0;
        }
        this.visited[this.id - 1] = 1;

        this.num_visited = 1;

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


        Object[] arr = new Object[this.edges.length];
        for (int i = 0; i < this.edges.length; i++)
        {
            arr[i] = new Pair<Pair<Integer, Integer>,Double>(new Pair<>(this.id, this.neighbors_id[i]), this.edges[i]);
        }
        this.linkedState = new Pair<>(this.id, arr);

        this.visited_semaphore = new Semaphore(1);

        this.socket_semaphores = new Semaphore[this.neighbors_output_port.length];
        for (int i = 0; i < this.neighbors_output_port.length; i++)
        {
            this.socket_semaphores[i] = new Semaphore(1);
        }

        try
        {
            InetAddress ip = InetAddress.getByName("localhost");
            this.sockets = new Socket[this.neighbors_output_port.length];
            for (int i = 0; i < this.neighbors_output_port.length; i++)
            {
                this.sockets[i] = new Socket(ip, this.neighbors_output_port[i]);
            }
        }
        catch (Exception e)
        {
            System.out.println("error caught in Node");
            e.printStackTrace();
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

    public void printId()
    {
        System.out.println(this.id);
    }

    @Override
    public void run()
    {
        InputThread[] servers = new InputThread[num_of_neighbors];
        for (int i = 0; i < num_of_neighbors; i++)
        {
            InputThread server = new InputThread(this.neighbors_input_port[i], this);
            servers[i] = server;
            server.start();
        }

        OutputThread[] clients = new OutputThread[num_of_neighbors];
        for (int i = 0; i < num_of_neighbors; i++)
        {
            OutputThread client = new OutputThread(this.neighbors_output_port[i],
                    this.linkedState, this);
            clients[i] = client;
            client.start();
        }

        // wait for all threads to die
        for (int i = 0; i < num_of_neighbors; i++)
        {
            try
            {
                servers[i].join();
                clients[i].join();
            }
            catch (InterruptedException e)
            {
                System.out.println("error caught in Node");
                e.printStackTrace();
            }
        }
        for (int i = 0; i < this.neighbors_output_port.length; i++)
        {
            try
            {
                this.sockets[i].close();
            }
            catch (IOException e)
            {
                System.out.println("error caught in Node");
                e.printStackTrace();
            }
        }
    }

    public int getNum_of_nodes()
    {
        return this.num_of_nodes;
    }
    public int getNum_visited()
    {
        return this.num_visited;
    }
    public int[] getVisited()
    {
        return this.visited;
    }
    public Semaphore getVisited_semaphore()
    {
        return this.visited_semaphore;
    }
    public int[] getNeighbors_output_port()
    {
        return this.neighbors_output_port;
    }
    public void setNum_visited(int val)
    {
        this.num_visited = val;
    }
    public Semaphore getWeight_matrix_semaphore()
    {
        return this.weight_matrix_semaphore;
    }

    public double[][] getWeight_matrix()
    {
        return this.weight_matrix;
    }

    public Socket[] getSockets()
    {
        return this.sockets;
    }

    public Semaphore[] getSocket_semaphores()
    {
        return socket_semaphores;
    }
}


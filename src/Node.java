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
    protected int[][] sent_pairs;
    protected int num_visited;
    protected Semaphore visited_semaphore;
    protected Semaphore weight_matrix_semaphore;
    protected Semaphore[] socket_semaphores;
    protected Semaphore sent_pair_semaphore;
    protected Socket[] sockets;
    protected boolean ready_to_die;
    protected boolean die;
    protected InputThread[] servers;

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

        this.sent_pairs = new int[this.num_of_nodes][this.num_of_nodes];
        for (int i = 0; i < this.num_of_nodes; i++)
        {
            for (int j = 0; j < this.num_of_nodes; j++)
            {
                this.sent_pairs[i][j] = 0;
            }
        }
        for (int j = 0; j < this.num_of_nodes; j++)
        {
            this.sent_pairs[j][j] = 1;
        }

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

        this.linkedState = this.createLinkedState();

        this.visited_semaphore = new Semaphore(1);

        this.sent_pair_semaphore = new Semaphore(1);

        this.weight_matrix_semaphore = new Semaphore(1);

        this.socket_semaphores = new Semaphore[this.neighbors_output_port.length];
        for (int i = 0; i < this.neighbors_output_port.length; i++)
        {
            this.socket_semaphores[i] = new Semaphore(1);
        }

        this.ready_to_die = false;
        this.die = false;

        try
        {
            InetAddress ip = InetAddress.getByName("localhost");
            this.sockets = new Socket[this.neighbors_output_port.length];
        }
        catch (Exception e)
        {
            System.out.println("error caught in Node constructor creating sockets");
            e.printStackTrace();
        }
    }

    public Pair createLinkedState()
    {
        Object[] arr = new Object[this.edges.length];
        for (int i = 0; i < this.edges.length; i++)
        {
            arr[i] = new Pair<>(new Pair<>(this.id, this.neighbors_id[i]), this.edges[i]);
        }
        return new Pair<>(this.id, arr);
    }

    public void update_edge(int id2, double weight)
    {
        for (int i = 0; i < this.edges.length; i++)
        {
            if (neighbors_id[i] == id2)
            {
                this.edges[i] = weight;
                this.weight_matrix[this.id - 1][id2 - 1] = weight;
                this.weight_matrix[id2 - 1][this.id - 1] = weight;
                this.linkedState = createLinkedState();
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
                System.out.print(this.weight_matrix[i][j] + ", ");
            }
            System.out.println(this.weight_matrix[i][this.num_of_nodes - 1]);
        }
    }

    public void printId()
    {
        System.out.println(this.id);
    }

    @Override
    public void run()
    {
        this.servers = new InputThread[num_of_neighbors];
        for (int i = 0; i < num_of_neighbors; i++)
        {
            InputThread server = new InputThread(this.neighbors_input_port[i], this);
            this.servers[i] = server;
            server.start();
        }

        OutputThread[] clients = new OutputThread[num_of_neighbors];
        for (int i = 0; i < num_of_neighbors; i++)
        {
            OutputThread client = new OutputThread(i, this.linkedState, this, null);
            clients[i] = client;
            client.start();
        }

        // wait for all threads to die

        for (int i = 0; i < num_of_neighbors; i++)
        {
            try
            {
                clients[i].join();
            }
            catch (Exception e)
            {
                System.out.println("error caught in Node join");
                e.printStackTrace();
            }
        }
        for (int i = 0; i < num_of_neighbors; i++)
        {
            try
            {
                servers[i].join();
            }
            catch (Exception e)
            {
                System.out.println("error caught in Node join");
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
        return this.socket_semaphores;
    }

    public int getNodeId()
    {
        return this.id;
    }

    public int[] getNeighbors_id() {
        return neighbors_id;
    }

    public Semaphore getSent_pair_semaphore() {
        return this.sent_pair_semaphore;
    }

    public int[][] getSent_pairs() {
        return this.sent_pairs;
    }

    public boolean isDie() {
        return die;
    }

    public boolean isReady_to_die() {
        return ready_to_die;
    }

    public void setReady_to_die(boolean ready_to_die) {
        this.ready_to_die = ready_to_die;
    }

    public void setDie(boolean die) {
        this.die = die;
    }

    public double[] getEdges() {
        return edges;
    }

    public int[] getNeighbors_input_port() {
        return neighbors_input_port;
    }

}


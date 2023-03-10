import java.net.*;
import java.util.*;
import java.io.*;


public class main {
    public static void main(String[] args) throws FileNotFoundException {
        String[] paths = {"input_1.txt"}; //enter the path to the files you want to run here.
        for(String path: paths) {
            ExManager m = new ExManager(path);
            m.read_txt();

            int num_of_nodes = m.getNum_of_nodes();

            Scanner scanner = new Scanner(new File(path));
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                if(line.contains("start")){
                    m.start();
                    /*for (int i = 0; i < m.getNum_of_nodes(); i++)
                    {
                        Node n = m.get_node(1 + i);
                        n.print_graph();
                        System.out.println();
                    }*/
                    Node n = m.get_node(1 + (int)(Math.random() * num_of_nodes));
                    n.print_graph();
                    System.out.println();
                }

                if(line.contains("update")){
                    //System.out.println("update");
                    String[] data = line.split(" ");
                    m.update_edge(Integer.parseInt(data[1]), Integer.parseInt(data[2]), Double.parseDouble(data[3]));
                }
            }
        }
    }
}

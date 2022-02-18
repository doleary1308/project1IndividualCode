import java.util.*;
import java.lang.*;
import java.io.*;
public class ClientList implements Serializable {
    private static final long serialVerionUID =1L;
    private static List clients = new LinkedList();
    private static ClientList clientList;
    public ClientList(){};
    public static ClientList instance() {
        if (clientList == null) {
          return (clientList = new ClientList());
        } else {
          return clientList;
        }
      }
    public static boolean insertClient(Client client){
        clients.add(client);
        return true;
    }
    public static Iterator getClients(){
        return clients.iterator();
    }
    


    
}

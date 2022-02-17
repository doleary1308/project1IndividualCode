import java.util.*;
import java.lang.*;
import java.io.*;
public class ClientList implements Serializable {
    private static final long serialVerionUID =1L;
    private List clients = new LinkedList();
    private static ClientList clientList;
    private ClientList(){};
    public static ClientList instance() {
        if (clientList == null) {
          return (clientList = new ClientList());
        } else {
          return clientList;
        }
      }
    public boolean insertClient(Client client){
        clients.add(client);
        return true;
    }
    public Iterator getClients(){
        return clients.iterator();
    }
    


    
}

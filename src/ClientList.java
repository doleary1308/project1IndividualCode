import java.util.*;
import java.lang.*;
import java.io.*;

public class ClientList implements Serializable {
    private static final long serialVersionUID =1L;
    private List clients = new LinkedList();
    private static ClientList clientList;

    public ClientList(){}

    public static ClientList instance() {
        if (clientList == null) {
          return (clientList = new ClientList());
        }
        else {
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
    public Client checkAgainstClientList(String id)
    {
        Client searchedClient = null;
        Client tempClient;
        for (int i=0; i<clients.size(); i++) //Check against existing client IDs
        {
            tempClient = (Client) clients.get(i);
            if(id.equals(tempClient.getId() ) ){searchedClient = tempClient;}
        }
        return searchedClient;
    }


    
}

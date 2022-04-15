import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ClientList implements Serializable {
  private static final long serialVersionUID = 1L;
  private List clients = new LinkedList();
  private static ClientList clientList;
  private ClientList() {
  }
  public static ClientList instance() {
    if (clientList == null) {
      return (clientList = new ClientList());
    } else {
      return clientList;
    }
  }
  public Client search(String clientId) {
    for (Iterator iterator = clients.iterator(); iterator.hasNext(); ) {
      Client client = (Client) iterator.next();
      if (client.getId().equals(clientId)) {
        return client;
      }
    }
    return null;
  }
  public String clientsOutstanding()
  {
    String listOfOutstanding = "";
    for (Iterator iterator = clients.iterator(); iterator.hasNext(); ) {
      Client client = (Client) iterator.next();
      if (client.getBalance() < 0) {
        listOfOutstanding += client.toString();
      }
    }
    return listOfOutstanding;
  }
  public String clientsInactive()
  {
    String listOfInactive = "";
    for (Iterator iterator = clients.iterator(); iterator.hasNext(); ) {
      Client client = (Client) iterator.next();
      if (client.isInactive()) {
        listOfInactive += client.toString();
      }
    }
    return listOfInactive;
  }
  public boolean insertClient(Client client) {
    clients.add(client);
    return true;
  }
  private void writeObject(ObjectOutputStream output) {
    try {
      output.defaultWriteObject();
      output.writeObject(clientList);
    } catch(IOException ioe) {
      ioe.printStackTrace();
    }
  }
  private void readObject(ObjectInputStream input) {
    try {
      if (clientList != null) {
        return;
      } else {
        input.defaultReadObject();
        if (clientList == null) {
          clientList = (ClientList) input.readObject();
        } else {
          input.readObject();
        }
      }
    } catch(IOException ioe) {
      ioe.printStackTrace();
    } catch(ClassNotFoundException cnfe) {
      cnfe.printStackTrace();
    }
  }
  public String toString() {
    return clients.toString();
  }
}
import java.util.*;
import java.io.*;
public class Warehouse implements Serializable {
  private static final long serialVersionUID = 1L;
  public static final int PRODUCT_NOT_FOUND  = 1;
  public static final int PRODUCT_NOT_ISSUED  = 2;
  public static final int PRODUCT_HAS_HOLD  = 3;
  public static final int PRODUCT_ISSUED  = 4;
  public static final int HOLD_PLACED  = 5;
  public static final int NO_HOLD_FOUND  = 6;
  public static final int OPERATION_COMPLETED= 7;
  public static final int OPERATION_FAILED= 8;
  public static final int NO_SUCH_CLIENT = 9;
  private ProductList productList;
  private ClientList clientList;
  private static Warehouse warehouse;
  private Warehouse() {
    productList = ProductList.instance();
    clientList = ClientList.instance();
  }
  public static Warehouse instance() {
    if (warehouse == null) {
      ClientIdServer.instance(); // instantiate all singletons
      return (warehouse = new Warehouse());
    } else {
      return warehouse;
    }
  }
  public Product addProduct(String title, String author, String id) {
    Product product = new Product(title, author, id);
    if (productList.insertProduct(product)) {
      return (product);
    }
    return null;
  }
  public Client addClient(String name, String address, String phone) {
    Client client = new Client(name, address, phone);
    if (clientList.insertClient(client)) {
      return (client);
    }
    return null;
  }
  public int placeHold(String clientId, String productId, int duration) {
    Product product = productList.search(productId);
    if (product == null) {
      return(PRODUCT_NOT_FOUND);
    }
    if (product.getBorrower() == null) {
      return(PRODUCT_NOT_ISSUED);
    }
    Client client = clientList.search(clientId);
    if (client == null) {
      return(NO_SUCH_CLIENT);
    }
    Hold hold = new Hold(client, product, duration);
    product.placeHold(hold);
    client.placeHold(hold);
    return(HOLD_PLACED);
  }
  public Client searchMembership(String clientId) {
    return clientList.search(clientId);
  }
  public Client processHold(String productId) {
    Product product = productList.search(productId);
    if (product == null) {
      return (null);
    }
    Hold hold = product.getNextHold();
    if (hold == null) {
      return (null);
    }
    hold.getClient().removeHold(productId);
    hold.getProduct().removeHold(hold.getClient().getId());
    return (hold.getClient());
  }
  public int removeHold(String clientId, String productId) {
    Client client = clientList.search(clientId);
    if (client == null) {
      return (NO_SUCH_CLIENT);
    }
    Product product = productList.search(productId);
    if (product == null) {
      return(PRODUCT_NOT_FOUND);
    }
    return client.removeHold(productId) && product.removeHold(clientId)? OPERATION_COMPLETED: NO_HOLD_FOUND;
  }
  public void removeInvalidHolds() {
    for (Iterator catalogIterator = productList.getProducts(); catalogIterator.hasNext(); ) {
      for (Iterator iterator = ((Product) catalogIterator.next()).getHolds(); iterator.hasNext(); ) {
        Hold hold = (Hold) iterator.next();
        if (!hold.isValid()) {
          hold.getProduct().removeHold(hold.getClient().getId());
          hold.getClient().removeHold(hold.getProduct().getId());
        }
      }
    }
  }
  public Product issueProduct(String clientId, String productId) {
    Product product = productList.search(productId);
    if (product == null) {
      return(null);
    }
    if (product.getBorrower() != null) {
      return(null);
    }
    Client client = clientList.search(clientId);
    if (client == null) {
      return(null);
    }
    if (!(product.issue(client) && client.issue(product))) {
      return null;
    }
    return(product);
  }
  public Product renewProduct(String productId, String clientId) {
    Product product = productList.search(productId);
    if (product == null) {
      return(null);
    }
    Client client = clientList.search(clientId);
    if (client == null) {
      return(null);
    }
    if ((product.renew(client) && client.renew(product))) {
      return(product);
    }
    return(null);
  }
  public Iterator getProducts(String clientId) {
    Client client = clientList.search(clientId);
    if (client == null) {
      return(null);
    } else {
      return (client.getProductsIssued());
    }
  }
  public int removeProduct(String productId) {
    Product product = productList.search(productId);
    if (product == null) {
      return(PRODUCT_NOT_FOUND);
    }
    if (product.hasHold()) {
      return(PRODUCT_HAS_HOLD);
    }
    if ( product.getBorrower() != null) {
      return(PRODUCT_ISSUED);
    }
    if (productList.removeProduct(productId)) {
      return (OPERATION_COMPLETED);
    }
    return (OPERATION_FAILED);
  }
  public int returnProduct(String productId) {
    Product product = productList.search(productId);
    if (product == null) {
      return(PRODUCT_NOT_FOUND);
    }
    Client client = product.returnProduct();
    if (client == null) {
      return(PRODUCT_NOT_ISSUED);
    }
    if (!(client.returnProduct(product))) {
      return(OPERATION_FAILED);
    }
    if (product.hasHold()) {
      return(PRODUCT_HAS_HOLD);
    }
    return(OPERATION_COMPLETED);
  }
  public Iterator getInvoices(String clientId, Calendar date) {
    Client client = clientList.search(clientId);
    if (client == null) {
      return(null);
    }
    return client.getInvoices(date);
  }
  public static Warehouse retrieve() {
    try {
      FileInputStream file = new FileInputStream("WarehouseData");
      ObjectInputStream input = new ObjectInputStream(file);
      input.readObject();
      ClientIdServer.retrieve(input);
      return warehouse;
    } catch(IOException ioe) {
      ioe.printStackTrace();
      return null;
    } catch(ClassNotFoundException cnfe) {
      cnfe.printStackTrace();
      return null;
    }
  }
  public static  boolean save() {
    try {
      FileOutputStream file = new FileOutputStream("WarehouseData");
      ObjectOutputStream output = new ObjectOutputStream(file);
      output.writeObject(warehouse);
      output.writeObject(ClientIdServer.instance());
      return true;
    } catch(IOException ioe) {
      ioe.printStackTrace();
      return false;
    }
  }
  private void writeObject(java.io.ObjectOutputStream output) {
    try {
      output.defaultWriteObject();
      output.writeObject(warehouse);
    } catch(IOException ioe) {
      System.out.println(ioe);
    }
  }
  private void readObject(java.io.ObjectInputStream input) {
    try {
      input.defaultReadObject();
      if (warehouse == null) {
        warehouse = (Warehouse) input.readObject();
      } else {
        input.readObject();
      }
    } catch(IOException ioe) {
      ioe.printStackTrace();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
  public String toString() {
    return productList + "\n" + clientList;
  }
}
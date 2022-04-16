import java.io.*;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;

public class Warehouse implements Serializable {
  private static final long serialVersionUID = 1L;
  public static final int PRODUCT_NOT_FOUND  = 1;
  public static final int PRODUCT_NOT_ISSUED  = 2;
  public static final int PRODUCT_HAS_WAIT  = 3;
  public static final int PRODUCT_ISSUED  = 4;
  public static final int WAIT_PLACED  = 5;
  public static final int NO_WAIT_FOUND  = 6;
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
  public Product addProduct(String name, int quantity, float price) {
    Product product = new Product(name, quantity, price);
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
  public Client searchMembership(String clientId) {
    return clientList.search(clientId);
  }
  public void printClients()
  {
    System.out.print(clientList.toString());
  }
  public String clientsOutstanding()
  {
    return clientList.clientsOutstanding();
  }
  public String clientsInactive()
  {
    return clientList.clientsInactive();
  }

  public Product searchProducts(String productId) {
    return productList.search(productId);
  }
  public int placeWait(String clientId, String productId/*, int quantity*/) {
    Product product = productList.search(productId);
    if (product == null) {
      return(PRODUCT_NOT_FOUND);
    }
    if (product.notInAnyCarts()) {
      return(PRODUCT_NOT_ISSUED);
    }
    Client client = clientList.search(clientId);
    if (client == null) {
      return(NO_SUCH_CLIENT);
    }
    Wait wait = new Wait(client, product/*, quantity*/);
    product.placeWait(wait);
    client.placeWait(wait);
    return(WAIT_PLACED);
  }
  public Iterator acceptShipment(String productId, int quantityToAdd) {
    Product product = productList.search(productId);
    LinkedList returnStatement = new LinkedList();
    if (product == null) {
      return (null);
    }
    product.stock(quantityToAdd);
    do {
      Wait wait = product.getNextWait();
      if (wait == null) {
        break;
      }
      wait.getClient().removeWait(productList.search(productId));
      wait.getProduct().removeWait(wait.getClient().getId());
      returnStatement.add(wait.getClient());
      product.stock(-1);
    } while(true && product.getQuantity() > 0);
    return returnStatement.iterator();
  }
  public int removeWait(String clientId, String productId) {
    Client client = clientList.search(clientId);
    if (client == null) {
      return (NO_SUCH_CLIENT);
    }
    Product product = productList.search(productId);
    if (product == null) {
      return(PRODUCT_NOT_FOUND);
    }
    return client.removeWait(productList.search(productId)) && product.removeWait(clientId)? OPERATION_COMPLETED: NO_WAIT_FOUND;
  }
  public void removeInvalidWaits() {
    for (Iterator catalogIterator = productList.getProducts(); catalogIterator.hasNext(); ) {
      for (Iterator iterator = ((Product) catalogIterator.next()).getWaits(); iterator.hasNext(); ) {
        Wait wait = (Wait) iterator.next();
        if (!wait.isValid()) {
          wait.getProduct().removeWait(wait.getClient().getId());
          wait.getClient().removeWait(wait.getProduct());
        }
      }
    }
  }
  public Product issueProduct(String clientId, String productId) {
    Product product = productList.search(productId);
    if (product == null) {
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
  public Product checkOut(String productId, String clientId) {
    Product product = productList.search(productId);
    if (product == null) {
      /**/System.out.print("Check: product == null");
      return(null);
    }
    Client client = clientList.search(clientId);
    if (client == null) {
      /**/System.out.print("Check: client == null");
      return(null);
    }
    boolean ableToSell;
    if(!(product.checkOut(client))) {
      placeWait(clientId, productId);
      ableToSell = false;
    } else {ableToSell = true;}
    if (ableToSell && client.checkOut(product)) {
      return(product);
    }
    return(null);
  }
  public String getClientCartProducts(String clientID) {
    Client client = clientList.search(clientID);
    if (client == null) {
      return(null);
    } else {
      return (client.getProductsIssued());
    }
  }
  public Iterator getClientCartData(String clientID)
  {
    Client client = clientList.search(clientID);
    if (client == null) {
      return(null);
    } else {
      return (client.getCartData());
    }
  }
  public Product changeProductQuantity(String clientID, String productID, int addReduce)
  {
    Product product = productList.search(productID);
    if (product == null) {
      return(null);
    }
    if (product.notInAnyCarts()) {
      return(null);
    }
    Client client = clientList.search(clientID);
    if (client == null) {
      return(null);
    }
    if (!(product.changeClientCartQuantity(client, addReduce) && client.changeQuantity(product, addReduce))) {
      return null;
    }
    return(product);
  }
  public Client acceptClientPayment(String clientID, float payment)
  {
    Client client = clientList.search(clientID);
    if (client == null) {
      return(null);
    }
    client.addBalance(payment);

    return client;
  }
  public void printProducts()
  {
    System.out.print(productList.toString());
  }
  public String printProductWaitlist(String productID)
  {
    return warehouse.productList.search(productID).printWaits();
  }
  public int removeProduct(String productID) {
    Product product = productList.search(productID);
    if (product == null) {
      return(PRODUCT_NOT_FOUND);
    }
    if (product.hasWait()) {
      return(PRODUCT_HAS_WAIT);
    }
    if ( product.notInAnyCarts()) {
      return(PRODUCT_ISSUED);
    }
    if (productList.removeProduct(productID)) {
      return (OPERATION_COMPLETED);
    }
    return (OPERATION_FAILED);
  }
  public int returnProduct(String clientID, String productID) {
    Product product = productList.search(productID);
    if (product == null) {
      return(PRODUCT_NOT_FOUND);
    }
    Client client = clientList.search(clientID);
    product.returnProduct(client);
    if (client == null) {
      return(PRODUCT_NOT_ISSUED);
    }
    if (!(client.returnProduct(product))) {
      return(OPERATION_FAILED);
    }
    if (product.hasWait()) {
      return(PRODUCT_HAS_WAIT);
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
  private void writeObject(ObjectOutputStream output) {
    try {
      output.defaultWriteObject();
      output.writeObject(warehouse);
    } catch(IOException ioe) {
      System.out.println(ioe);
    }
  }
  private void readObject(ObjectInputStream input) {
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
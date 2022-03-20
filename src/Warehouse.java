import java.util.*;
import java.io.*;
public class Warehouse implements Serializable {
  private static final long serialVersionUID = 1L;
  public static final int PRODUCT_NOT_FOUND = 1;
  public static final int PRODUCT_NOT_ISSUED = 2;
  public static final int PRODUCT_HAS_WISHLIST = 3;
  public static final int PRODUCT_ISSUED = 4;     //WAS BOOK ISSUED
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
    if (productList.insertBook(product)) {
      return (product);
    }
    return null;
  }
  public Client addClient(String name, String address, String phone) {
    Client client = new Client(name, address, phone);
    if (clientList.insertMember(client)) {
      return (client);
    }
    return null;
  }
  public int addToWishList(String memberId, String bookId, int quantity) {
    Product product = productList.search(bookId);
    if (product == null) {
      return(PRODUCT_NOT_FOUND);
    }
    if (product.getBorrower() == null) {
      return(PRODUCT_NOT_ISSUED);
    }
    Client client = clientList.search(memberId);
    if (client == null) {
      return(NO_SUCH_CLIENT);
    }
    WaitList waitList = new WaitList(client, product, quantity);
    product.placeHold(waitList);
    client.placeHold(waitList);
    return(HOLD_PLACED);
  }
  public Client searchMembership(String memberId) {
    return clientList.search(memberId);
  }
  public Client processHold(String bookId) {
    Product product = productList.search(bookId);
    if (product == null) {
      return (null);
    }
    WaitList waitList = product.getNextHold();
    if (waitList == null) {
      return (null);
    }
    waitList.getClient().removeHold(bookId);
    waitList.getProduct().removeHold(waitList.getClient().getId());
    return (waitList.getClient());
  }
  public int removeHold(String memberId, String productId) {
    Client client = clientList.search(memberId);
    if (client == null) {
      return (NO_SUCH_CLIENT);
    }
    Product product = productList.search(productId);
    if (product == null) {
      return(PRODUCT_NOT_FOUND);
    }
    return client.removeHold(productId) && product.removeHold(memberId)? OPERATION_COMPLETED: NO_HOLD_FOUND;
  }
  public void removeInvalidHolds() {
    for (Iterator catalogIterator = productList.getBooks(); catalogIterator.hasNext(); ) {
      for (Iterator iterator = ((Product) catalogIterator.next()).getHolds(); iterator.hasNext(); ) {
        WaitList waitList = (WaitList) iterator.next();
        if (!waitList.isValid()) {
          waitList.getProduct().removeHold(waitList.getClient().getId());
          waitList.getClient().removeHold(waitList.getProduct().getId());
        }
      }
    }
  }
  public Product issueBook(String memberId, String bookId) {
    Product product = productList.search(bookId);
    if (product == null) {
      return(null);
    }
    if (product.getBorrower() != null) {
      return(null);
    }
    Client client = clientList.search(memberId);
    if (client == null) {
      return(null);
    }
    if (!(product.issue(client) && client.issue(product))) {
      return null;
    }
    return(product);
  }
  public Product renewBook(String bookId, String memberId) {  //it was renewBook
    Product product = productList.search(bookId);
    if (product == null) {
      return(null);
    }
    Client client = clientList.search(memberId);
    if (client == null) {
      return(null);
    }
    if ((product.renew(client) && client.renew(product))) {
      return(product);
    }
    return(null);
  }
  public Iterator getProducts(String memberId) {
    Client client = clientList.search(memberId);
    if (client == null) {
      return(null);
    } else {
      return (client.getBooksIssued());
    }
  }
  public int removeProduct(String bookId) {  //removeBook
    Product product = productList.search(bookId);
    if (product == null) {
      return(PRODUCT_NOT_FOUND);
    }
    if (product.hasHold()) {
      return(PRODUCT_HAS_WISHLIST);
    }
    if ( product.getBorrower() != null) {
      return(PRODUCT_ISSUED);
    }
    if (productList.removeBook(bookId)) {
      return (OPERATION_COMPLETED);
    }
    return (OPERATION_FAILED);
  }
  public int returnBook(String bookId) {
    Product product = productList.search(bookId);
    if (product == null) {
      return(PRODUCT_NOT_FOUND);
    }
    Client client = product.returnBook();
    if (client == null) {
      return(PRODUCT_NOT_ISSUED);
    }
    if (!(client.returnBook(product))) {
      return(OPERATION_FAILED);
    }
    if (product.hasHold()) {
      return(PRODUCT_HAS_WISHLIST);
    }
    return(OPERATION_COMPLETED);
  }
  public Iterator getTransactions(String memberId, Calendar date) {
    Client client = clientList.search(memberId);
    if (client == null) {
      return(null);
    }
    return client.getTransactions(date);
  }
  public static Warehouse retrieve() {
    try {
      FileInputStream file = new FileInputStream("LibraryData");
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
      FileOutputStream file = new FileOutputStream("LibraryData");
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
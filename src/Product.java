import java.io.Serializable;
import java.util.*;

public class Product implements Serializable {
  private static final long serialVersionUID = 1L;
  private String name;
  private int quantity;
  private String id;
  private float price;
  private List inCartOf = new LinkedList();
  private List waits = new LinkedList();
  //private Calendar dueDate;  ///Pretty sure this is unnecessary
  private static final String PRODUCT_STRING = "P";

  public Product(String name, int quantity, float price) {
    this.name = name;
    this.quantity = quantity;
    this.price = price;
    id = PRODUCT_STRING + (ProductIdServer.instance()).getId();
  }
  public boolean issue(Client client) {
    inCartOf.add(client);
    Collections.sort(inCartOf);
    /*dueDate = new GregorianCalendar();
    dueDate.setTimeInMillis(System.currentTimeMillis());
    dueDate.add(Calendar.MONTH, 1);*/
    return true;
  }
  public Client returnProduct(Client client) {
    if (inCartOf == null) {
      return null;
    } else {
      inCartOf.remove(client);
      return client;
    }
  }
  public boolean checkOut(Client client) {
    if (quantity <= 0) {
      return false;
    }
    if (inCartOf.contains(client) && quantity > 0) {
      inCartOf.remove(client);
      quantity--;
      return true;
    }
    return false;
  }
  public void placeWait(Wait wait) {
    waits.add(wait);
  }
  public boolean removeWait(String clientId) {
    for (ListIterator iterator = waits.listIterator(); iterator.hasNext(); ) {
      Wait wait = (Wait) iterator.next();
      String id = wait.getClient().getId();
      if (id.equals(clientId)) {
        iterator.remove();
        return true;
      }
    }
    return false;
  }
  public Wait getNextWait() {
    for (ListIterator iterator = waits.listIterator(); iterator.hasNext(); ) {
      Wait wait = (Wait) iterator.next();
      iterator.remove();
      if (wait.isValid()) {
        return wait;
      }
    }
    return null;
  }
  public boolean hasWait() {
    ListIterator iterator = waits.listIterator();
    if (iterator.hasNext()) {
      return true;
    }
    return false;
  }
  public Iterator getWaits() {
    return waits.iterator();
  }
  public boolean changeClientCartQuantity(Client client, int addReduce)
  {
    boolean success = false;
    if(addReduce>0) //"The client wants to add to the product quantity"
    {
      while (addReduce>0) {
        if(inCartOf.add(client)) {
          addReduce--;
          success = true;
        } else {return success;}
      }
      Collections.sort(inCartOf);
    }
    if(addReduce<0) //"The client wants to reduce the product quantity"
    {
      while (addReduce < 0) {
        if(inCartOf.remove(client)) {
          addReduce++;
          success = true;
        }
        else {return success;}
      }
    }
    return success;
  }
  public int getQuantity() {
    return quantity;
  }
  public String getName() {
    return name;
  }
  public String getId() {
    return id;
  }
  public float getPrice() {
    return price;
  }
  public boolean notInAnyCarts() {
    return inCartOf.isEmpty();
  }

  //public String getDueDate() {return (dueDate.getTime().toString());}

  public String toString() { //Implicitly check context to see how to display products
    String returnStatement = "Name: " + name + " | ID: " + id;
    if((WarehouseContext.instance()).getLogin() == WarehouseContext.IsClerk)
    { returnStatement += " | Quantity: " + getQuantity() + " |  In Cart: " + inCartOf; }
    return returnStatement;
  }
}
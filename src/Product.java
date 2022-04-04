import java.io.Serializable;
import java.util.*;

public class Product implements Serializable {
  private static final long serialVersionUID = 1L;
  private String name;
  private String quantity;
  private String id;
  private String price;
  private Client wishlistedBy;
  private List waits = new LinkedList();
  private Calendar dueDate;
  private static final String PRODUCT_STRING = "P";

  public Product(String name, String quantity, String price) {
    this.name = name;
    this.quantity = quantity;
    this.price=price;
    id = PRODUCT_STRING + (ProductIdServer.instance()).getId();
  }
  public boolean issue(Client client) {
    wishlistedBy = client;
    dueDate = new GregorianCalendar();
    dueDate.setTimeInMillis(System.currentTimeMillis());
    dueDate.add(Calendar.MONTH, 1);
    return true;
  }
  public Client returnProduct() {
    if (wishlistedBy == null) {
      return null;
    } else {
      Client wishlister = wishlistedBy;
      wishlistedBy = null;
      return wishlister;
    }
  }
  public boolean checkOut(Client client) {
    if (hasWait()) {
      return false;
    }
    if ((client.getId()).equals(wishlistedBy.getId())) {
      return (issue(client));
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
  public String getQuantity() {
    return quantity;
  }
  public String getName() {
    return name;
  }
  public String getId() {
    return id;
  }
  public Client getWishlister() {
    return wishlistedBy;
  }
  public String getDueDate() {
      return (dueDate.getTime().toString());
  }

  public String toString() {
    return "Name: " + name + " | Quantity: " + quantity + " | ID: " + id + " | Price: " + price + " | Wishlisted By: " + wishlistedBy.getName() + "\n";
  }
}
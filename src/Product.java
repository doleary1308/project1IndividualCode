import java.util.*;
import java.lang.*;
import java.io.*;
public class Product implements Serializable {
  private static final long serialVersionUID = 1L;
  private String name;
  private String author;
  private String id;
  private Client borrowedBy;
  private List holds = new LinkedList();
  private Calendar dueDate;

  public Product(String name, String author, String id) {
    this.name = name;
    this.author = author;
    this.id = id;
  }
  public boolean issue(Client client) {
    borrowedBy = client;
    dueDate = new GregorianCalendar();
    dueDate.setTimeInMillis(System.currentTimeMillis());
    dueDate.add(Calendar.MONTH, 1);
    return true;
  }
  public Client returnProduct() {
    if (borrowedBy == null) {
      return null;
    } else {
      Client borrower = borrowedBy;
      borrowedBy = null;
      return borrower;
    }
  }
  public boolean renew(Client client) {
    if (hasHold()) {
      return false;
    }
    if ((client.getId()).equals(borrowedBy.getId())) {
      return (issue(client));
    }
    return false;
  }
  public void placeHold(Hold hold) {
    holds.add(hold);
  }
  public boolean removeHold(String clientId) {
    for (ListIterator iterator = holds.listIterator(); iterator.hasNext(); ) {
      Hold hold = (Hold) iterator.next();
      String id = hold.getClient().getId();
      if (id.equals(clientId)) {
        iterator.remove();
        return true;
      }
    }
    return false;
  }
  public Hold getNextHold() {
    for (ListIterator iterator = holds.listIterator(); iterator.hasNext(); ) {
      Hold hold = (Hold) iterator.next();
      iterator.remove();
      if (hold.isValid()) {
        return hold;
      }
    }
    return null;
  }
  public boolean hasHold() {
    ListIterator iterator = holds.listIterator();
    if (iterator.hasNext()) {
      return true;
    }
    return false;
  }
  public Iterator getHolds() {
    return holds.iterator();
  }
  public String getAuthor() {
    return author;
  }
  public String getName() {
    return name;
  }
  public String getId() {
    return id;
  }
  public Client getBorrower() {
    return borrowedBy;
  }
  public String getDueDate() {
      return (dueDate.getTime().toString());
  }
  public String toString() {
    return "name " + name + " author " + author + " id " + id + " borrowed by " + borrowedBy;
  }
}
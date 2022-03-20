import java.util.*;
import java.lang.*;
import java.io.*;
public class Product implements Serializable {
  private static final long serialVersionUID = 1L;
  private String title;
  private String author;
  private String id;
  private Client borrowedBy;
  private List holds = new LinkedList();
  private Calendar dueDate;

  public Product(String title, String author, String id) {
    this.title = title;
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
  public Client returnBook() {
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
  public void placeHold(WaitList waitList) {
    holds.add(waitList);
  }
  public boolean removeHold(String memberId) {
    for (ListIterator iterator = holds.listIterator(); iterator.hasNext(); ) {
      WaitList waitList = (WaitList) iterator.next();
      String id = waitList.getClient().getId();
      if (id.equals(memberId)) {
        iterator.remove();
        return true;
      }
    }
    return false;
  }
  public WaitList getNextHold() {
    for (ListIterator iterator = holds.listIterator(); iterator.hasNext(); ) {
      WaitList waitList = (WaitList) iterator.next();
      iterator.remove();
      if (waitList.isValid()) {
        return waitList;
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
  public String getTitle() {
    return title;
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
    return "title " + title + " author " + author + " id " + id + " borrowed by " + borrowedBy;
  }
}
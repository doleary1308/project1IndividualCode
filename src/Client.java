import java.util.*;
import java.io.*;
public class Client implements Serializable {
  private static final long serialVersionUID = 1L;
  private String name;
  private String address;
  private String phone;
  private String id;
  private static final String MEMBER_STRING = "M";
  private List booksBorrowed = new LinkedList();
  private List booksOnHold = new LinkedList();
  private List transactions = new LinkedList();
  public Client(String name, String address, String phone) {
    this.name = name;
    this.address = address;
    this.phone = phone;
    id = MEMBER_STRING + (ClientIdServer.instance()).getId();
  }
  public boolean issue(Product product) {
    if (booksBorrowed.add(product)) {
      transactions.add(new Invoice("Book issued ", product.getTitle()));
      return true;
    }
    return false;
  }
  public boolean returnBook(Product product) {
    if ( booksBorrowed.remove(product)){
      transactions.add(new Invoice("Book returned ", product.getTitle()));
      return true;
    }
    return false;
  }

  public boolean renew(Product product) {
    for (ListIterator iterator = booksBorrowed.listIterator(); iterator.hasNext(); ) {
      Product aProduct = (Product) iterator.next();
      String id = aProduct.getId();
      if (id.equals(product.getId())) {
        transactions.add(new Invoice("Book renewed ",  product.getTitle()));
        return true;
      }
    }
    return false;
  }
  public Iterator getBooksIssued() {
    return (booksBorrowed.listIterator());
  }
  public void placeHold(WaitList waitList) {
    transactions.add(new Invoice("Hold Placed ", waitList.getProduct().getTitle()));
    booksOnHold.add(waitList);
  }
  public boolean removeHold(String bookId) {
    for (ListIterator iterator = booksOnHold.listIterator(); iterator.hasNext(); ) {
      WaitList waitList = (WaitList) iterator.next();
      String id = waitList.getProduct().getId();
      if (id.equals(bookId)) {
        transactions.add(new Invoice("Hold Removed ", waitList.getProduct().getTitle()));
        iterator.remove();
        return true;
      }
    }
    return false;
  }
  public Iterator getTransactions(Calendar date) {
    List result = new LinkedList();
    for (Iterator iterator = transactions.iterator(); iterator.hasNext(); ) {
      Invoice invoice = (Invoice) iterator.next();
      if (invoice.onDate(date)) {
        result.add(invoice);
      }
    }
    return (result.iterator());
  }
  public String getName() {
    return name;
  }
  public String getPhone() {
    return phone;
  }
  public String getAddress() {
    return address;
  }
  public String getId() {
    return id;
  }
  public void setName(String newName) {
    name = newName;
  }
  public void setAddress(String newAddress) {
    address = newAddress;
  }
  public void setPhone(String newPhone) {
    phone = newPhone;
  }
  public boolean equals(String id) {
    return this.id.equals(id);
  }
  public String toString() {
    String string = "Member name " + name + " address " + address + " id " + id + "phone " + phone;
    string += " borrowed: [";
    for (Iterator iterator = booksBorrowed.iterator(); iterator.hasNext(); ) {
      Product product = (Product) iterator.next();
      string += " " + product.getTitle();
    }
    string += "] holds: [";
    for (Iterator iterator = booksOnHold.iterator(); iterator.hasNext(); ) {
      WaitList waitList = (WaitList) iterator.next();
      string += " " + waitList.getProduct().getTitle();
    }
    string += "] transactions: [";
    for (Iterator iterator = transactions.iterator(); iterator.hasNext(); ) {
      string += (Invoice) iterator.next();
    }
    string += "]";
    return string;
  }
}
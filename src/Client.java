import java.io.Serializable;
import java.util.*;

public class Client implements Serializable {
  private static final long serialVersionUID = 1L;
  private String name;
  private String address;
  private String phone;
  private String id;
  private static final String CLIENT_STRING = "C";
  private List productsInWishlist = new LinkedList();
  private List productsOnWait = new LinkedList();
  private List invoices = new LinkedList();
  public Client(String name, String address, String phone) {
    this.name = name;
    this.address = address;
    this.phone = phone;
    id = CLIENT_STRING + (ClientIdServer.instance()).getId();
  }



  public boolean issue(Product product) {
    if (productsInWishlist.add(product)) {
      invoices.add(new Invoice("Product issued ", product.getName()));
      return true;
    }
    return false;
  }
  public boolean returnProduct(Product product) {
    if ( productsInWishlist.remove(product)){
    //  invoices.add(new Invoice("Product returned ", product.getName()));
      return true;
    }
    return false;
  }

  public boolean checkOut(Product product) {
    for (ListIterator iterator = productsInWishlist.listIterator(); iterator.hasNext(); ) {
      Product aProduct = (Product) iterator.next();
      String id = aProduct.getId();
      if (id.equals(product.getId())) {
        invoices.add(new Invoice("Product Checked Out: ",  product.getName()));
        return true;
      }
    }
    return false;
  }
  public Iterator getProductsIssued() {
    return (productsInWishlist.listIterator());
  }
  public void placeWait(Wait wait) {
    invoices.add(new Invoice("Wait Placed On: ", wait.getProduct().getName()));
    productsOnWait.add(wait);
  }
  public boolean removeWait(String productId) {
    for (ListIterator iterator = productsOnWait.listIterator(); iterator.hasNext(); ) {
      Wait wait = (Wait) iterator.next();
      String id = wait.getProduct().getId();
      if (id.equals(productId)) {
        invoices.add(new Invoice("Wait Removed From: ", wait.getProduct().getName()));
        iterator.remove();
        return true;
      }
    }
    return false;
  }
  public Iterator getInvoices(Calendar date) {
    List result = new LinkedList();
    for (Iterator iterator = invoices.iterator(); iterator.hasNext(); ) {
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
    String string = "Client Name: " + name + " | Address: " + address + " | ID: " + id + " | Phone: " + phone;
    string += "\n   Wishlist: [";
    for (Iterator iterator = productsInWishlist.iterator(); iterator.hasNext(); ) {
      Product product = (Product) iterator.next();
      string += " " + product.getName();
    }
    string += "]\n   Waits: [";
    for (Iterator iterator = productsOnWait.iterator(); iterator.hasNext(); ) {
      Wait wait = (Wait) iterator.next();
      string += " " + wait.getProduct().getName();
    }
    string += "]\n   Invoices: [";
    for (Iterator iterator = invoices.iterator(); iterator.hasNext(); ) {
      string += (Invoice) iterator.next();
    }
    string += "]";
    return string;
  }

  public String showWaitlist() {
    String string = "";
    for (Iterator iterator = productsOnWait.iterator(); iterator.hasNext(); ) {
      Wait wait = (Wait) iterator.next();
      string += " " + wait.getProduct().getName() + ",\n";
    }
    return string;
  }

  public String clientInfoToString() {
    String string = "Client Name: " + name + " | Address: " + address + " | ID: " + id + " | Phone: " + phone +"\n";
    return string;
  }
}
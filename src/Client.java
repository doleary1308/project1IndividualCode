import java.util.*;
import java.io.*;
public class Client implements Serializable {
  private static final long serialVersionUID = 1L;
  private String name;
  private String address;
  private String phone;
  private String id;
  private static final String CLIENT_STRING = "M";
  private List productsBorrowed = new LinkedList();
  private List productsOnHold = new LinkedList();
  private List invoices = new LinkedList();
  public Client(String name, String address, String phone) {
    this.name = name;
    this.address = address;
    this.phone = phone;
    id = CLIENT_STRING + (ClientIdServer.instance()).getId();
  }
  public boolean issue(Product product) {
    if (productsBorrowed.add(product)) {
      invoices.add(new Invoice("Product issued ", product.getName()));
      return true;
    }
    return false;
  }
  public boolean returnProduct(Product product) {
    if ( productsBorrowed.remove(product)){
      invoices.add(new Invoice("Product returned ", product.getName()));
      return true;
    }
    return false;
  }

  public boolean renew(Product product) {
    for (ListIterator iterator = productsBorrowed.listIterator(); iterator.hasNext(); ) {
      Product aProduct = (Product) iterator.next();
      String id = aProduct.getId();
      if (id.equals(product.getId())) {
        invoices.add(new Invoice("Product renewed ",  product.getName()));
        return true;
      }
    }
    return false;
  }
  public Iterator getProductsIssued() {
    return (productsBorrowed.listIterator());
  }
  public void placeHold(Hold hold) {
    invoices.add(new Invoice("Hold Placed ", hold.getProduct().getName()));
    productsOnHold.add(hold);
  }
  public boolean removeHold(String productId) {
    for (ListIterator iterator = productsOnHold.listIterator(); iterator.hasNext(); ) {
      Hold hold = (Hold) iterator.next();
      String id = hold.getProduct().getId();
      if (id.equals(productId)) {
        invoices.add(new Invoice("Hold Removed ", hold.getProduct().getName()));
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
    String string = "Client name " + name + " address " + address + " id " + id + "phone " + phone;
    string += " borrowed: [";
    for (Iterator iterator = productsBorrowed.iterator(); iterator.hasNext(); ) {
      Product product = (Product) iterator.next();
      string += " " + product.getName();
    }
    string += "] holds: [";
    for (Iterator iterator = productsOnHold.iterator(); iterator.hasNext(); ) {
      Hold hold = (Hold) iterator.next();
      string += " " + hold.getProduct().getName();
    }
    string += "] invoices: [";
    for (Iterator iterator = invoices.iterator(); iterator.hasNext(); ) {
      string += (Invoice) iterator.next();
    }
    string += "]";
    return string;
  }
}
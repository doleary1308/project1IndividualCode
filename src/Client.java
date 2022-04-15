import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

public class Client implements Serializable,Comparable<Client> {
  private static final long serialVersionUID = 1L;
  private String name;
  private String address;
  private String phone;
  private String id;
  public float balance;
  private static final String CLIENT_STRING = "C";
  private List productsInCart = new LinkedList();
  private List productsOnWait = new LinkedList();
  private List invoices = new LinkedList();
  public Client(String name, String address, String phone) {
    this.name = name;
    this.address = address;
    this.phone = phone;
    this.balance = 0;
    id = CLIENT_STRING + (ClientIdServer.instance()).getId();
  }
  public boolean issue(Product product) {
    if (productsInCart.add(product)) {
      //invoices.add(new Invoice("Product issued ", product.getName()));
      Collections.sort(productsInCart, Comparator.comparing(Product::getId));
      return true;
    }
    return false;
  }
  public boolean changeQuantity(Product product, int addReduce)
  {
    boolean success = false;
    if(addReduce>0) //"The client wants to add to the product quantity"
    {
      while (addReduce>0) {
        if(productsInCart.add(product)) {
          addReduce--;
          success = true;
        } else {return success;}
      }
    }
    if(addReduce<0) //"The client wants to reduce the product quantity"
    {
      while (addReduce < 0) {
        if(productsInCart.remove(product)) {
          addReduce++;
          success = true;
        }
        else {return success;}
      }
    }
    return success;
  }
  public boolean returnProduct(Product product) {
    if ( productsInCart.remove(product)){
      invoices.add(new Invoice("Product returned ", product.getName()));
      return true;
    }
    return false;
  }

  public boolean checkOut(Product product) {
    for (ListIterator iterator = productsInCart.listIterator(); iterator.hasNext(); ) {
      Product aProduct = (Product) iterator.next();
      String id = aProduct.getId();
      if (id.equals(product.getId())) {
        balance = balance - product.getPrice();
        invoices.add(new Invoice("Product Checked Out: ",  product.getName()));
        return true;
      }
    }
    return false;
  }
  public void addBalance(float payment)
  {
    balance = balance + payment;
  }
  public String getProductsIssued() {
    String string = "";
    Product temp = null;
    int currentCount = 1; //Minimum amount of an item present in a cart is 1

    for (Iterator iterator = productsInCart.iterator(); iterator.hasNext(); ) {
      Product product = (Product) iterator.next();
      if(product==temp) //If the current is the same as the last
      {
        currentCount++; //Increase the count to show amount in the cart
      }
      else              //If the current is different from the last
      {                 //Add the last to the string, with its count
        if(temp != null)//Don't add when temp is null aka when looking at the first item
        {
          string += " | " + temp.getName() + ": " + currentCount + "\n"; //Break line to format
          currentCount = 1;
        }
      }
      //Load the current into temp, to compare to the next during next loop
      temp = product;
    }
    //The above for() loop can't append the final product into the string.
    //So we need to do that manually after the loop is done iterating
    //Still ensure temp is not null, since temp will never get a value on client creation
    if(temp != null){ string += " | " + temp.getName() + ": " + currentCount + "\n"; }

    return string;
  }
  public Iterator getCartData()
  {
    return productsInCart.iterator();
  }
  public void placeWait(Wait wait) {
    invoices.add(new Invoice("Wait Placed On: ", wait.getProduct().getName()));
    productsOnWait.add(wait);
  }
  public boolean removeWait(Product product) {
    for (ListIterator iterator = productsOnWait.listIterator(); iterator.hasNext(); ) {
      Wait wait = (Wait) iterator.next();
      String id = wait.getProduct().getId();
      if (id.equals(product.getId())) {
        balance = balance - product.getPrice();
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
  public boolean isInactive()
  {
    Calendar today = new GregorianCalendar();
    today.setTimeInMillis(System.currentTimeMillis());
    for(int i = 180;i<0;i--)
    {
      today.add(Calendar.DAY_OF_YEAR,-1);
      if(getInvoices(today) != null) { return false; }
    }
    return true;
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
  public float getBalance() {
    return balance;
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
    String string = "Client Name: " + name + " | Address: " + address + " | ID: " + id + " | Phone: " + phone + " | Balance: " + balance;
    if((WarehouseContext.instance()).getLogin() == WarehouseContext.IsClerk) {
      string += getProductsIssued();
      string += "\n   Waits: [";
      for (Iterator iterator = productsOnWait.iterator(); iterator.hasNext(); ) {
        Wait wait = (Wait) iterator.next();
        string += " " + wait.getProduct().getName();
      }
      string += "]\n   Invoices: [";
      for (Iterator iterator = invoices.iterator(); iterator.hasNext(); ) {
        string += (Invoice) iterator.next();
      }
      string += "]\n";
    }
    return string;
  }

  @Override
  public int compareTo(Client o) {
    return 0;
  }
}
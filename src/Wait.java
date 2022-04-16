import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Wait implements Serializable {
  private Product product;
  private Client client;
  private Calendar date;
  private int quantity;
  public Wait(Client client, Product product/*, int quantity*/) {
    this.product = product;
    this.client = client;
    //this.quantity = quantity;
    /*date = new GregorianCalendar();
    date.setTimeInMillis(System.currentTimeMillis());
    date.add(Calendar.DATE, duration);*/
  }
  public Client getClient() {
    return client;
  }
  public Product getProduct() {
    return product;
  }
  public Calendar getDate() {
    return date;
  }
  public boolean isValid(){
    return (System.currentTimeMillis() < date.getTimeInMillis());
  }
  public String toString() { //Implicitly check context to see how to display products
    return product + " waitlisted by " + client + " on " + date + "\n";
  }
}


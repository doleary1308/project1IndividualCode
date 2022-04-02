import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Wait implements Serializable {
  private Product product;
  private Client client;
  private Calendar date;
  public Wait(Client client, Product product, int duration) {
    this.product = product;
    this.client = client;
    date = new GregorianCalendar();
    date.setTimeInMillis(System.currentTimeMillis());
    date.add(Calendar.DATE, duration);
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
}


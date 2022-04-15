import java.util.*;
import java.text.*;
import java.io.*;
public class ClientState extends WarehouseState {
  private static ClientState clientstate;
  private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
  private static Warehouse warehouse;
  private static final int EXIT = 0;
  private static final int SHOW_CLIENT_DETAILS = 1;
  private static final int CLIENTCARTSTATE = 2;
  private static final int SHOW_SITE_PRODUCTS = 3;
  private static final int GET_INVOICES = 4;
  private static final int HELP = 5;
  private ClientState() {
    warehouse = Warehouse.instance();
  }

  public static ClientState instance() {
    if (clientstate == null) {
      return clientstate = new ClientState();
    } else {
      return clientstate;
    }
  }
  public String getToken(String prompt) {
    do {
      try {
        System.out.println(prompt);
        String line = reader.readLine();
        StringTokenizer tokenizer = new StringTokenizer(line,"\n\r\f");
        if (tokenizer.hasMoreTokens()) {
          return tokenizer.nextToken();
        }
      } catch (IOException ioe) {
        System.exit(0);
      }
    } while (true);
  }
  private boolean yesOrNo(String prompt) {
    String more = getToken(prompt + " (Y|y)[es] or anything else for no");
    if (more.charAt(0) != 'y' && more.charAt(0) != 'Y') {
      return false;
    }
    return true;
  }
  public int getNumber(String prompt) {
    do {
      try {
        String item = getToken(prompt);
        Integer num = Integer.valueOf(item);
        return num.intValue();
      } catch (NumberFormatException nfe) {
        System.out.println("Please input a number ");
      }
    } while (true);
  }
  public Calendar getDate(String prompt) {
    do {
      try {
        Calendar date = new GregorianCalendar();
        String item = getToken(prompt);
        DateFormat df = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
        date.setTime(df.parse(item));
        return date;
      } catch (Exception fe) {
        System.out.println("Please input a date as mm/dd/yy");
      }
    } while (true);
  }
  public int getCommand() {
    do {
      try {
        int value = Integer.parseInt(getToken("Enter command:" + HELP + " for help"));
        if (value >= EXIT && value <= HELP) {
          return value;
        }
      } catch (NumberFormatException nfe) {
        System.out.println("Enter a number");
      }
    } while (true);
  }

  public void help() {
    System.out.println("Enter a number as explained below:");
    System.out.println(EXIT + " to Exit");
    System.out.println(SHOW_CLIENT_DETAILS + " to view your details");
    System.out.println(CLIENTCARTSTATE + " to view your cart and its actions");
    System.out.println(SHOW_SITE_PRODUCTS + " to show the products available");
    System.out.println(GET_INVOICES + " to print invoices");
    System.out.println(HELP + " for help");
  }

  public void getClientDetails()
  {
    Client result;
    String clientID = WarehouseContext.instance().getUser();
    result = warehouse.searchMembership(clientID);
    if (result == null) {
      System.out.println("Invalid Client ID");
    } else {
      System.out.println(result);
    }

  }
  public void clientCartState()
  {
    (WarehouseContext.instance()).changeState(4);
  }

  public void getSiteProducts()
  {
    warehouse.printProducts();
  }

  public void getInvoices() {
    Iterator result;
    String clientID = WarehouseContext.instance().getUser();
    Calendar date  = getDate("Please enter the date for which you want records as mm/dd/yy");
    result = warehouse.getInvoices(clientID,date);
    if (result == null) {
      System.out.println("Invalid Client ID");
    } else {
      while(result.hasNext()) {
        Invoice invoice = (Invoice) result.next();
        System.out.println(invoice.getType() + "   "   + invoice.getName() + "\n");
      }
      System.out.println("\n  There are no more invoices \n" );
    }
  }

  public void process() {
    int command;
    help();
    while ((command = getCommand()) != EXIT) {
      switch (command) {

        case SHOW_CLIENT_DETAILS:  getClientDetails();
          break;
        case CLIENTCARTSTATE:  clientCartState();
          break;
        case SHOW_SITE_PRODUCTS:  getSiteProducts();
          break;
        case GET_INVOICES:  getInvoices();
                                break;
        case HELP:              help();
                                break;
      }
    }
    logout();
  }

  public void run() {
    process();
  }

  public void logout()
  {
    if (/**/((WarehouseContext.instance()).getLogin() == WarehouseContext.IsClerk) ||
            ((WarehouseContext.instance()).getLogin() == WarehouseContext.IsManager))
    { System.out.println(" going to clerk \n ");
      (WarehouseContext.instance()).changeState(1); // exit with a code 1
    }
    else if (WarehouseContext.instance().getLogin() == WarehouseContext.IsUser)
    {  System.out.println(" going to login \n");
      (WarehouseContext.instance()).changeState(0); // exit with a code 2
    }
    else
      (WarehouseContext.instance()).changeState(2); // exit code 2, indicates error
  }
}
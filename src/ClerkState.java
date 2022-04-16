//The omnipresent Clerk
import java.util.*;
import java.text.*;
import java.io.*;
public class ClerkState extends WarehouseState {
  private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
  private static Warehouse warehouse;
  private WarehouseContext context;
  private static ClerkState instance;
  private static final int EXIT = 0;
  private static final int ADD_CLIENT = 1;
  private static final int QUERY_CLIENTS = 2;
  private static final int ACCEPT_CLIENT_PAYMENT = 3;
  private static final int SHOW_WAREHOUSE_PRODUCTS = 4;
  private static final int ADD_PRODUCTS_TO_WAREHOUSE = 5;
  private static final int REMOVE_PRODUCTS = 6;
  private static final int SHOW_PRODUCT_WAITLIST = 7;
  private static final int CLIENTMENU = 8;
  private static final int HELP = 9;
  private ClerkState() {
      super();
      warehouse = Warehouse.instance();
      //context = LibContext.instance();
  }

  public static ClerkState instance() {
    if (instance == null) {
      instance = new ClerkState();
    }
    return instance;
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
    System.out.println(ADD_CLIENT + " to add a client");
    System.out.println(QUERY_CLIENTS + " to perform a client query");
    System.out.println(ACCEPT_CLIENT_PAYMENT + " to accept a client payment");
    System.out.println(SHOW_WAREHOUSE_PRODUCTS + " to show the products in the warehouse and their details");
    System.out.println(ADD_PRODUCTS_TO_WAREHOUSE + " to add products to warehouse");
    System.out.println(REMOVE_PRODUCTS + " to remove products from warehouse");
    System.out.println(SHOW_PRODUCT_WAITLIST + " to show the waits for a specific product");
    System.out.println(CLIENTMENU + " to switch to the client menu");
    System.out.println(HELP + " for help");
  }

  public void addClient() {
    String name = getToken("Enter member name");
    String address = getToken("Enter address");
    String phone = getToken("Enter phone");
    Client result;
    result = warehouse.addClient(name, address, phone);
    if (result == null) {
      System.out.println("Could not add member");
    }
    System.out.println(result);
  }

  public void queryClients()
  {
    (WarehouseContext.instance()).changeState(5);
  }

  public void acceptClientPayment()
  {
    String clientID = getToken("Enter client ID");
    float payment = Float.parseFloat(getToken("Enter payment amount"));
    Client result = warehouse.acceptClientPayment(clientID, payment);
    if(result == null)
    { System.out.println("Payment could not be accepted"); }
    else
    { System.out.println("Payment accepted for: \n" + result + "\nClient's current balance is " + result.getBalance()); }
  }

  public void getWareHouseProducts()
  {
    warehouse.printProducts();
  }

  public void addProductsToWarehouse() {
    Product result;
    do {
      String name = getToken("Enter product name");
      int quantity = Integer.parseInt(getToken("Enter quantity to stock"));
      float price = Float.parseFloat(getToken("Enter price per unit"));
      result = warehouse.addProduct(name, quantity, price);
      if (result != null) {
        System.out.println(result);
      } else {
        System.out.println("Product could not be added");
      }
      if (!yesOrNo("Add more products?")) {
        break;
      }
    } while (true);
  }

  public void removeProducts() {
    int result;
    do {
      String productID = getToken("Enter product id");
      result = warehouse.removeProduct(productID);
      switch(result){
        case Warehouse.PRODUCT_NOT_FOUND:
          System.out.println("No such Product in Warehouse");
          break;
        case Warehouse.PRODUCT_ISSUED:
          System.out.println("Product is currently in a cart");
          break;
        case Warehouse.PRODUCT_HAS_WAIT:
          System.out.println("Product is waitlisted");
          break;
        case Warehouse.OPERATION_FAILED:
          System.out.println("Product could not be removed");
          break;
        case Warehouse.OPERATION_COMPLETED:
          System.out.println(" Product has been removed");
          break;
        default:
          System.out.println("An error has occurred");
      }
      if (!yesOrNo("Remove more products?")) {
        break;
      }
    } while (true);
  }

  public void showProductWaitlist()
  {
    String productID = getToken("Enter product id");
    if(warehouse.searchProducts(productID) != null){
      System.out.print(warehouse.printProductWaitlist(productID));
    } else { System.out.print("Product not found"); }
  }

  public void clientMenu()
  {
    String userID = getToken("Please input the client id: ");
    if (Warehouse.instance().searchMembership(userID) != null){
      (WarehouseContext.instance()).setUser(userID);
      (WarehouseContext.instance()).changeState(1);
    }
    else 
      System.out.println("Invalid user id."); 
  }

  public void logout()
  {
    if ((WarehouseContext.instance()).getLogin() == WarehouseContext.IsManager)
    { System.out.println(" going to manager \n ");
      (WarehouseContext.instance()).changeState(3); // exit with a code 1
    }
    else if (WarehouseContext.instance().getLogin() == WarehouseContext.IsClerk)
    {  System.out.println(" going to login \n");
      (WarehouseContext.instance()).changeState(2); // exit with a code 2
    }
    else
      (WarehouseContext.instance()).changeState(2); // exit code 2, indicates error
  }
 

  public void process() {
    int command;
    help();
    while ((command = getCommand()) != EXIT) {
      switch (command) {
        case ADD_CLIENT:        addClient();
                                break;
        case QUERY_CLIENTS:        queryClients();
          break;
        case ACCEPT_CLIENT_PAYMENT:        acceptClientPayment();
          break;
        case ADD_PRODUCTS_TO_WAREHOUSE:         addProductsToWarehouse();
                                break;
        case SHOW_WAREHOUSE_PRODUCTS:         getWareHouseProducts();
          break;
        case REMOVE_PRODUCTS:      removeProducts();
                                break;
        case SHOW_PRODUCT_WAITLIST:      showProductWaitlist();
                                break;
        case CLIENTMENU:          clientMenu();
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
}

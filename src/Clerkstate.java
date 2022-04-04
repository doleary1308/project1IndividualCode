import java.util.*;
import java.text.*;
import java.io.*;
public class Clerkstate extends WarehouseState {
  private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
  private static Warehouse warehouse;
  private WarehouseContext context;
  private static Clerkstate instance;
  private static final int EXIT = 0;
  private static final int ADD_CLIENT = 1;
  private static final int SHOW_CLIENTS = 2;
  private static final int SHOW_PRODUCTS = 3;
  //private static final int RETURN_PRODUCTS = 4;
  private static final int USERMENU = 4;
  private static final int HELP = 5;
  private Clerkstate() {
      super();
      warehouse = Warehouse.instance();
      //context = WarehouseContext.instance();
  }

  public static Clerkstate instance() {
    if (instance == null) {
      instance = new Clerkstate();
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
    System.out.println(SHOW_CLIENTS+ " show all clients");

    System.out.println(SHOW_PRODUCTS + " to show the list of products in the warehouse");
  //  System.out.println(RETURN_PRODUCTS + " to return products");
  //  System.out.println(REMOVE_PRODUCTS + " to remove products from warehouse");

    System.out.println(USERMENU + " to switch to the client menu");
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


  public void addProductsToWarehouse() {
    Product result;
    do {
      String name = getToken("Enter name");
      String quantity = getToken("Enter quantity");
      String price = getToken("Enter price");
      result = warehouse.addProduct(name, quantity,price);
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
          System.out.println("Product is currently in a wishlist");
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

  public void showClients(){warehouse.showCLientList();}
  public void showProducts() { warehouse.showProductList();}

  public void usermenu()
  {
    String userID = getToken("Please input the user id: ");
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
        case SHOW_CLIENTS:        showClients();
                                break;

        case SHOW_PRODUCTS:     showProducts();
                                break;

        //case REMOVE_PRODUCTS:      removeProducts();
          //                      break;

        case USERMENU:          usermenu();
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

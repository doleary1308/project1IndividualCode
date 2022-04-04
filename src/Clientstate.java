import java.util.*;
import java.text.*;
import java.io.*;
public class Clientstate extends WarehouseState {
  private static Clientstate clientstate;
  private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
  private static Warehouse warehouse;
  private static final int EXIT = 0;
  private static final int RETURN_PRODUCTS = 1;
  private static final int ADD_PRODUCTS_TO_WISHLIST = 2;
  private static final int CHECK_OUT = 3;
  private static final int PLACE_WAIT = 4;
  private static final int REMOVE_WAIT = 5;
  private static final int GET_INVOICES = 6;
  private static final int GET_INFO = 7;
  private static final int SHOW_WAITLIST=8;
  private static final int SHOW_PRODUCTS = 9;
  private static final int HELP = 10;
  private Clientstate() {
    warehouse = Warehouse.instance();
  }

  public static Clientstate instance() {
    if (clientstate == null) {
      return clientstate = new Clientstate();
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
    System.out.println(RETURN_PRODUCTS + "to remove products from wishlist");
    System.out.println(ADD_PRODUCTS_TO_WISHLIST + " to add products to a client's wishlist");
    System.out.println(CHECK_OUT + " to check out products ");
    System.out.println(PLACE_WAIT + " to place a wait on a product");
    System.out.println(REMOVE_WAIT + " to remove a wait on a product");
    System.out.println(GET_INVOICES + " to print invoices");
    System.out.println(GET_INFO+ " to get client info");
    System.out.println(SHOW_WAITLIST+ " to get client waitlist");
    System.out.println(SHOW_PRODUCTS + " to show the list of products in the warehouse");
    System.out.println(HELP + " for help");
  }


  public void addProductsToClientWishlist() {
    Product result;
    String clientID = WarehouseContext.instance().getUser();
    do {
      String productID = getToken("Enter product id");
      result = warehouse.issueProduct(clientID, productID);
      if (result != null){
        System.out.println(result.getName()+ "   " +  result.getDueDate());
      } else {
          System.out.println("Product could not be added");
      }
      if (!yesOrNo("Add more products?")) {
        break;
      }
    } while (true);
  }

  public void returnProducts() {
    int result;
    do {
      String productID = getToken("Enter product id");
      result = warehouse.returnProduct(productID);
      switch(result) {
        case Warehouse.PRODUCT_NOT_FOUND:
          System.out.println("No such Product in Warehouse");
          break;
        case Warehouse.PRODUCT_NOT_ISSUED:
          System.out.println(" Product was not checked out");
          break;
        case Warehouse.PRODUCT_HAS_WAIT:
          System.out.println("Product is waitlisted");
          break;
        case Warehouse.OPERATION_FAILED:
          System.out.println("Product could not be returned");
          break;
        case Warehouse.OPERATION_COMPLETED:
          System.out.println(" Product has been returned");
          break;
        default:
          System.out.println("An error has occurred");
      }
      if (!yesOrNo("Return more products?")) {
        break;
      }
    } while (true);
  }

  public void checkOut() {
    Product result;
    String clientID = WarehouseContext.instance().getUser();
    Iterator wishlistedProducts = warehouse.getProducts(clientID);
    while (wishlistedProducts.hasNext()){
      Product product = (Product)(wishlistedProducts.next());
      if (yesOrNo(product.getName())) {
        result = warehouse.checkOut(product.getId(), clientID);
        if (result != null){
          System.out.println(result.getName()+ "   " + result.getDueDate());
        } else {
          System.out.println("Product is not able to be checked out");
        }
      }
    }
  }


  public void placeWait() {
    String clientID = WarehouseContext.instance().getUser();
    String productID = getToken("Enter product id");
    int duration = getNumber("Enter duration of wait");
    int result = warehouse.placeWait(clientID, productID, duration);
    switch(result){
      case Warehouse.PRODUCT_NOT_FOUND:
        System.out.println("No such Product in Warehouse");
        break;
      case Warehouse.PRODUCT_NOT_ISSUED:
        System.out.println("Product is not in a wishlist");
        break;
      case Warehouse.NO_SUCH_CLIENT:
        System.out.println("Not a valid client ID");
        break;
      case Warehouse.WAIT_PLACED:
        System.out.println("A wait has been placed");
        break;
      default:
        System.out.println("An error has occurred");
    }
  }

  public void removeWait() {
    String clientID = WarehouseContext.instance().getUser();
    String productID = getToken("Enter product id");
    int result = warehouse.removeWait(clientID, productID);
    switch(result){
      case Warehouse.PRODUCT_NOT_FOUND:
        System.out.println("No such Product in Warehouse");
        break;
      case Warehouse.NO_SUCH_CLIENT:
        System.out.println("Not a valid client ID");
        break;
      case Warehouse.OPERATION_COMPLETED:
        System.out.println("The wait has been removed");
        break;
      default:
        System.out.println("An error has occurred");
    }
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
  public void showClientDetails(){        //added method
    warehouse.showClientDetails();

  }

  public void showWaitlist() {

    System.out.print(warehouse.showWaitlist());
  }
  public void showProducts() { warehouse.showProductList();}

  public void process() {
    int command;
    help();
    while ((command = getCommand()) != EXIT) {
      switch (command) {
        case RETURN_PRODUCTS:      returnProducts(); //not sure what this is supposed to be
                                break;
        case ADD_PRODUCTS_TO_WISHLIST:       addProductsToClientWishlist();
                                break;
        case CHECK_OUT:       checkOut();
                                break;
        case PLACE_WAIT:        placeWait();
                                break;
        case REMOVE_WAIT:       removeWait();
                                break;
        case GET_INVOICES:  getInvoices();
                                break;
        case GET_INFO:          showClientDetails();
                                break;
        case SHOW_WAITLIST:     showWaitlist();
                                break;
        case SHOW_PRODUCTS:     showProducts();
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
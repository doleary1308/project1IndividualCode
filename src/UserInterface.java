import java.util.*;
import java.text.*;
import java.io.*;
public class UserInterface {
  private static UserInterface userInterface;
  private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
  private static Warehouse warehouse;
  private static final int EXIT = 0;
  private static final int ADD_CLIENT = 1;        //Add Client
  private static final int ADD_PRODUCTS = 2;         //Add Product
  private static final int ISSUE_PRODUCTS = 3;       //Add Product to Wishlist
  private static final int RETURN_PRODUCTS = 4;      //Return Product
  private static final int RENEW_PRODUCTS = 5;       //Checkout (only applicable method with hasNext, NEED TO CHANGE)
  private static final int REMOVE_PRODUCTS = 6;      //Remove Product
  private static final int PLACE_HOLD = 7;        //Add Product to Waitlist
  private static final int REMOVE_HOLD = 8;       //Remove Product from Waitlist
  private static final int PROCESS_HOLD = 9;      //Accept Shipment (At least part of, NEEDS SOME CHANGE)
  private static final int GET_INVOICES = 10; //Get Invoices
  private static final int SHOW_PRODUCTS = 11;       //Show Products
  private static final int SAVE = 12;             //Save
  private static final int RETRIEVE = 13;         //Retrieve
  private static final int HELP = 14;             //Help :(
  private UserInterface() {
    if (yesOrNo("Look for saved data and use it?")) {
      retrieve();
    } else {
      warehouse = Warehouse.instance();
    }
  }
  public static UserInterface instance() {
    if (userInterface == null) {
      return userInterface = new UserInterface();
    } else {
      return userInterface;
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
    System.out.println(EXIT + " to Exit\n");
    System.out.println(ADD_CLIENT + " to add a client");
    System.out.println(ADD_PRODUCTS + " to add products");
    System.out.println(ISSUE_PRODUCTS + " to issue products to a client");
    System.out.println(RETURN_PRODUCTS + " to return products ");
    System.out.println(RENEW_PRODUCTS + " to renew products ");
    System.out.println(REMOVE_PRODUCTS + " to remove products");
    System.out.println(PLACE_HOLD + " to place a hold on a product");
    System.out.println(REMOVE_HOLD + " to remove a hold on a product");
    System.out.println(PROCESS_HOLD + " to process holds");
    System.out.println(GET_INVOICES + " to print invoices");
    System.out.println(SAVE + " to save data");
    System.out.println(RETRIEVE + " to retrieve");
    System.out.println(HELP + " for help");
  }

  public void addClient() {
    String name = getToken("Enter client name");
    String address = getToken("Enter address");
    String phone = getToken("Enter phone");
    Client result;
    result = warehouse.addClient(name, address, phone);
    if (result == null) {
      System.out.println("Could not add client");
    }
    System.out.println(result);
  }

  public void addProducts() {
    Product result;
    do {
      String name = getToken("Enter name");
      String productID = getToken("Enter id");
      String author = getToken("Enter author");
      result = warehouse.addProduct(name, author, productID);
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
  public void issueProducts() {
    Product result;
    String clientID = getToken("Enter client id");
    if (warehouse.searchMembership(clientID) == null) {
      System.out.println("No such client");
      return;
    }
    do {
      String productID = getToken("Enter product id");
      result = warehouse.issueProduct(clientID, productID);
      if (result != null){
        System.out.println(result.getName()+ "   " +  result.getDueDate());
      } else {
          System.out.println("Product could not be issued");
      }
      if (!yesOrNo("Issue more products?")) {
        break;
      }
    } while (true);
  }
  public void renewProducts() {
    Product result;
    String clientID = getToken("Enter client id");
    if (warehouse.searchMembership(clientID) == null) {
      System.out.println("No such client");
      return;
    }
    Iterator issuedProducts = warehouse.getProducts(clientID);
    while (issuedProducts.hasNext()){
      Product product = (Product)(issuedProducts.next());
      if (yesOrNo(product.getName())) {
        result = warehouse.renewProduct(product.getId(), clientID);
        if (result != null){
          System.out.println(result.getName()+ "   " + result.getDueDate());
        } else {
          System.out.println("Product is not renewable");
        }
      }
    }
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
        case Warehouse.PRODUCT_HAS_HOLD:
          System.out.println("Product has a hold");
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
          System.out.println(" Product is currently checked out");
          break;
        case Warehouse.PRODUCT_HAS_HOLD:
          System.out.println("Product has a hold");
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
  public void placeHold() {
    String clientID = getToken("Enter client id");
    String productID = getToken("Enter product id");
    int duration = getNumber("Enter duration of hold");
    int result = warehouse.placeHold(clientID, productID, duration);
    switch(result){
      case Warehouse.PRODUCT_NOT_FOUND:
        System.out.println("No such Product in Warehouse");
        break;
      case Warehouse.PRODUCT_NOT_ISSUED:
        System.out.println("Product is not checked out");
        break;
      case Warehouse.NO_SUCH_CLIENT:
        System.out.println("Not a valid client ID");
        break;
      case Warehouse.HOLD_PLACED:
        System.out.println("A hold has been placed");
        break;
      default:
        System.out.println("An error has occurred");
    }
  }
  public void removeHold() {
    String clientID = getToken("Enter client id");
    String productID = getToken("Enter product id");
    int result = warehouse.removeHold(clientID, productID);
    switch(result){
      case Warehouse.PRODUCT_NOT_FOUND:
        System.out.println("No such Product in Warehouse");
        break;
      case Warehouse.NO_SUCH_CLIENT:
        System.out.println("Not a valid client ID");
        break;
      case Warehouse.OPERATION_COMPLETED:
        System.out.println("The hold has been removed");
        break;
      default:
        System.out.println("An error has occurred");
    }
  }
  public void processHolds() {
    Client result;
    do {
      String productID = getToken("Enter product id");
      result = warehouse.processHold(productID);
      if (result != null) {
        System.out.println(result);
      } else {
        System.out.println("No valid holds left");
      }
      if (!yesOrNo("Process more products?")) {
        break;
      }
    } while (true);
  }
  public void getInvoices() {
    Iterator result;
    String clientID = getToken("Enter client id");
    Calendar date  = getDate("Please enter the date for which you want records as mm/dd/yy");
    result = warehouse.getInvoices(clientID,date);
    if (result == null) {
      System.out.println("Invalid Client ID");
    } else {
      while(result.hasNext()) {
        Invoice invoice = (Invoice) result.next();
        System.out.println(invoice.getType() + "   "   + invoice.getName() + "\n");
      }
      System.out.println("\n There are no more invoices \n" );
    }
  }
  public void showProducts() {
    String clientID = getToken("Enter client id");  //////
    Iterator allProducts = warehouse.getProducts(clientID);
    while (allProducts.hasNext()){
      Product product = (Product)(allProducts.next());
      System.out.println(product.toString());
    }
  }
  private void save() {
    if (warehouse.save()) {
      System.out.println(" The warehouse has been successfully saved in the file WarehouseData \n" );
    } else {
      System.out.println(" There has been an error in saving \n" );
    }
  }
  private void retrieve() {
    try {
      Warehouse tempWarehouse = Warehouse.retrieve();
      if (tempWarehouse != null) {
        System.out.println(" The Warehouse has been successfully retrieved from the file WarehouseData \n" );
        warehouse = tempWarehouse;
      } else {
        System.out.println("File doesn't exist; creating new warehouse" );
        warehouse = Warehouse.instance();
      }
    } catch(Exception cnfe) {
      cnfe.printStackTrace();
    }
  }
  public void process() {
    int command;
    help();
    while ((command = getCommand()) != EXIT) {
      switch (command) {
        case ADD_CLIENT:        addClient();
                                break;
        case ADD_PRODUCTS:         addProducts();
                                break;
        case ISSUE_PRODUCTS:       issueProducts();
                                break;
        case RETURN_PRODUCTS:      returnProducts();
                                break;
        case REMOVE_PRODUCTS:      removeProducts();
                                break;
        case RENEW_PRODUCTS:       renewProducts();
                                break;
        case PLACE_HOLD:        placeHold();
                                break;
        case REMOVE_HOLD:       removeHold();
                                break;
        case PROCESS_HOLD:      processHolds();
                                break;
        case GET_INVOICES:  getInvoices();
                                break;
        case SHOW_PRODUCTS:	    showProducts();
                                break;
        case SAVE:              save();
                                break;
        case RETRIEVE:          retrieve();
                                break;
        case HELP:              help();
                                break;
      }
    }
  }
  public static void main(String[] s) {
    UserInterface.instance().process();
  }
}
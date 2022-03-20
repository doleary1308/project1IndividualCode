import java.util.*;
import java.text.*;
import java.io.*;
public class UserInterface {
  private static UserInterface userInterface;
  private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
  private static Warehouse warehouse;
  private static final int EXIT = 0;
  private static final int ADD_CLIENT = 1;
  private static final int ADD_PRODUCTS = 2;
  private static final int VIEW_WISHLIST = 3;
  private static final int ACCEPT_SHIPMENT = 4;
  private static final int SHOW_CLIENT_WISHLIST = 5;
  private static final int REMOVE_PRODUCTS = 6;
  private static final int ADD_TO_WISHLIST = 7;
  private static final int REMOVE_FROM_WISHLIST = 8;
  private static final int CHECKOUT = 9;
  private static final int GET_INVOICES = 10;
  private static final int SAVE = 11;
  private static final int RETRIEVE = 12;
  private static final int HELP = 13;
  private UserInterface() {
    if (yesOrNo("Look for saved data and  use it?")) {
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
    System.out.println("Enter a number between 0 and 12 as explained below:");
    System.out.println(EXIT + " to Exit\n");
    System.out.println(ADD_CLIENT + " to add a client");
    System.out.println(ADD_PRODUCTS + " to  add products" +
            "");
    System.out.println(VIEW_WISHLIST + " to  list products" +
            " in a  client's wishlist");
    System.out.println(ACCEPT_SHIPMENT + " to  accept shipment" +   //likely not accept shipment
            " ");
    System.out.println(SHOW_CLIENT_WISHLIST + " to  show client wishlists" +
            " ");
    System.out.println(REMOVE_PRODUCTS + " to  remove products" +
            "");
    System.out.println(ADD_TO_WISHLIST + " to  add to wishlist");
    System.out.println(REMOVE_FROM_WISHLIST + " to  remove from wishlist");
    System.out.println(CHECKOUT + " to  checkout");
    System.out.println(GET_INVOICES + " to  print transactions");
    System.out.println(SAVE + " to  save data");
    System.out.println(RETRIEVE + " to  retrieve");
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
      String productName = getToken("Enter  productName");
      String productID = getToken("Enter id");
      String quantity = getToken("Enter quantity");
      result = warehouse.addProduct(productName, quantity, productID);
      if (result != null) {
        System.out.println(result);
      } else {
        System.out.println("product could not be added");
      }
      if (!yesOrNo("Add more products" +
              "?")) {
        break;
      }
    } while (true);
  }
  public void listProducts() {            //was issueBooks
    Product result;
    String clientID = getToken("Enter client id");
    if (warehouse.searchMembership(clientID) == null) {
      System.out.println("No such client");
      return;
    }
    do {
      String productID = getToken("Enter product id");
      result = warehouse.issueBook(clientID, productID);
      if (result != null){
        System.out.println(result.getTitle()+ "   " +  result.getDueDate());
      } else {
          System.out.println("product could not be issued");
      }
      if (!yesOrNo("Issue more products" +
              "?")) {
        break;
      }
    } while (true);
  }
  public void showClientWishList() {    //was renewBooks
    Product result;
    String memberID = getToken("Enter client id");
    if (warehouse.searchMembership(memberID) == null) {
      System.out.println("No such client");
      return;
    }
    Iterator issuedBooks = warehouse.getProducts(memberID);
    while (issuedBooks.hasNext()){
      Product product = (Product)(issuedBooks.next());
      if (yesOrNo(product.getTitle())) {
        result = warehouse.renewBook(product.getId(), memberID);
        if (result != null){
          System.out.println(result.getTitle()+ "   " + result.getDueDate());
        } else {
          System.out.println("product is not renewable");
        }
      }
    }
  }
  public void acceptShipment() {         //from return products
    // to accept shipment
    int result;
    do {
      String bookID = getToken("Enter product id");
      result = warehouse.returnBook(bookID);
      switch(result) {
        case Warehouse.PRODUCT_NOT_FOUND:
          System.out.println("No such product in Library");
          break;
        case Warehouse.PRODUCT_NOT_ISSUED:
          System.out.println(" product  was not checked out");
          break;
        case Warehouse.PRODUCT_HAS_WISHLIST:
          System.out.println("product has a hold");
          break;
        case Warehouse.OPERATION_FAILED:
          System.out.println("product could not be returned");
          break;
        case Warehouse.OPERATION_COMPLETED:
          System.out.println(" product has been returned");
          break;
        default:
          System.out.println("An error has occurred");
      }
      if (!yesOrNo("Return more products" +
              "?")) {
        break;
      }
    } while (true);
  }
  public void removeProducts() {       //was removeBooks
    int result;
    do {
      String bookID = getToken("Enter product id");
      result = warehouse.removeProduct(bookID);
      switch(result){
        case Warehouse.PRODUCT_NOT_FOUND:
          System.out.println("No such product in Library");
          break;
        case Warehouse.PRODUCT_ISSUED:
          System.out.println(" product is currently checked out");
          break;
        case Warehouse.PRODUCT_HAS_WISHLIST:
          System.out.println("product has a hold");
          break;
        case Warehouse.OPERATION_FAILED:
          System.out.println("product could not be removed");
          break;
        case Warehouse.OPERATION_COMPLETED:
          System.out.println(" product has been removed");
          break;
        default:
          System.out.println("An error has occurred");
      }
      if (!yesOrNo("Remove more products" +
              "?")) {
        break;
      }
    } while (true);
  }
  public void addWishList() {           //was place hold
    String memberID = getToken("Enter client id");
    String bookID = getToken("Enter product id");
    int duration = getNumber("Enter duration of hold");   //might be quantity
    int result = warehouse.addToWishList(memberID, bookID, duration);
    switch(result){
      case Warehouse.PRODUCT_NOT_FOUND:
        System.out.println("No such product in Library");
        break;
      case Warehouse.PRODUCT_NOT_ISSUED:
        System.out.println(" product is not checked out");
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
  public void removeFromWishList() {      //was remove hold
    String memberID = getToken("Enter client id");
    String bookID = getToken("Enter product id");
    int result = warehouse.removeHold(memberID, bookID);
    switch(result){
      case Warehouse.PRODUCT_NOT_FOUND:
        System.out.println("No such product in Library");
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
  public void checkout() {  //was processHolds
    Client result;
    do {
      String bookID = getToken("Enter product id");
      result = warehouse.processHold(bookID);
      if (result != null) {
        System.out.println(result);
      } else {
        System.out.println("No valid holds left");
      }
      if (!yesOrNo("Process more products" +
              "?")) {
        break;
      }
    } while (true);
  }
  public void getInvoices() {     //was transactions
    Iterator result;
    String memberID = getToken("Enter client id");
    Calendar date  = getDate("Please enter the date for which you want records as mm/dd/yy");
    result = warehouse.getTransactions(memberID,date);
    if (result == null) {
      System.out.println("Invalid client ID");
    } else {
      while(result.hasNext()) {
        Invoice invoice = (Invoice) result.next();
        System.out.println(invoice.getType() + "   "   + invoice.getTitle() + "\n");
      }
      System.out.println("\n  There are no more transactions \n" );
    }
  }
  private void save() {
    if (warehouse.save()) {
      System.out.println(" The library has been successfully saved in the file LibraryData \n" );
    } else {
      System.out.println(" There has been an error in saving \n" );
    }
  }
  private void retrieve() {
    try {
      Warehouse tempWarehouse = Warehouse.retrieve();
      if (tempWarehouse != null) {
        System.out.println(" The library has been successfully retrieved from the file LibraryData \n" );
        warehouse = tempWarehouse;
      } else {
        System.out.println("File doesnt exist; creating new library" );
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
        case VIEW_WISHLIST:       listProducts();
                                break;
        case ACCEPT_SHIPMENT:      acceptShipment();
                                break;
        case REMOVE_PRODUCTS:      removeProducts();
                                break;
        case SHOW_CLIENT_WISHLIST:       showClientWishList();
                                break;
        case ADD_TO_WISHLIST:        addWishList();
                                break;
        case REMOVE_FROM_WISHLIST:       removeFromWishList();
                                break;
        case CHECKOUT:      checkout();
                                break;
        case GET_INVOICES:  getInvoices();
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
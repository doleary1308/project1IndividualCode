import java.util.*;
import java.text.*;
import java.io.*;
public class UserInterface {
  private static UserInterface userInterface;
  private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
  private static Warehouse warehouse;
  private static final int EXIT = 0;
  private static final int ADD_MEMBER = 1;
  private static final int ADD_BOOKS = 2;
  private static final int ISSUE_BOOKS = 3;
  private static final int RETURN_BOOKS = 4;
  private static final int RENEW_BOOKS = 5;
  private static final int REMOVE_BOOKS = 6;
  private static final int PLACE_HOLD = 7;
  private static final int REMOVE_HOLD = 8;
  private static final int PROCESS_HOLD = 9;
  private static final int GET_TRANSACTIONS = 10;
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
    System.out.println(ADD_MEMBER + " to add a member");
    System.out.println(ADD_BOOKS + " to  add books");
    System.out.println(ISSUE_BOOKS + " to  issue books to a  member");
    System.out.println(RETURN_BOOKS + " to  return books ");
    System.out.println(RENEW_BOOKS + " to  renew books ");
    System.out.println(REMOVE_BOOKS + " to  remove books");
    System.out.println(PLACE_HOLD + " to  place a hold on a book");
    System.out.println(REMOVE_HOLD + " to  remove a hold on a book");
    System.out.println(PROCESS_HOLD + " to  process holds");
    System.out.println(GET_TRANSACTIONS + " to  print transactions");
    System.out.println(SAVE + " to  save data");
    System.out.println(RETRIEVE + " to  retrieve");
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

  public void addProducts() {
    Product result;
    do {
      String title = getToken("Enter  title");
      String bookID = getToken("Enter id");
      String author = getToken("Enter author");
      result = warehouse.addProduct(title, author, bookID);
      if (result != null) {
        System.out.println(result);
      } else {
        System.out.println("Book could not be added");
      }
      if (!yesOrNo("Add more books?")) {
        break;
      }
    } while (true);
  }
  public void listProducts() {            //was issueBooks
    Product result;
    String memberID = getToken("Enter member id");
    if (warehouse.searchMembership(memberID) == null) {
      System.out.println("No such member");
      return;
    }
    do {
      String bookID = getToken("Enter book id");
      result = warehouse.issueBook(memberID, bookID);
      if (result != null){
        System.out.println(result.getTitle()+ "   " +  result.getDueDate());
      } else {
          System.out.println("Book could not be issued");
      }
      if (!yesOrNo("Issue more books?")) {
        break;
      }
    } while (true);
  }
  public void showClientWishList() {    //was renewBooks
    Product result;
    String memberID = getToken("Enter member id");
    if (warehouse.searchMembership(memberID) == null) {
      System.out.println("No such member");
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
          System.out.println("Book is not renewable");
        }
      }
    }
  }
  public void acceptShipment() {         //from return books to accept shipment
    int result;
    do {
      String bookID = getToken("Enter book id");
      result = warehouse.returnBook(bookID);
      switch(result) {
        case Warehouse.PRODUCT_NOT_FOUND:
          System.out.println("No such Book in Library");
          break;
        case Warehouse.PRODUCT_NOT_ISSUED:
          System.out.println(" Book  was not checked out");
          break;
        case Warehouse.PRODUCT_HAS_HOLD:
          System.out.println("Book has a hold");
          break;
        case Warehouse.OPERATION_FAILED:
          System.out.println("Book could not be returned");
          break;
        case Warehouse.OPERATION_COMPLETED:
          System.out.println(" Book has been returned");
          break;
        default:
          System.out.println("An error has occurred");
      }
      if (!yesOrNo("Return more books?")) {
        break;
      }
    } while (true);
  }
  public void removeProducts() {       //was removeBooks
    int result;
    do {
      String bookID = getToken("Enter book id");
      result = warehouse.removeProduct(bookID);
      switch(result){
        case Warehouse.PRODUCT_NOT_FOUND:
          System.out.println("No such Book in Library");
          break;
        case Warehouse.PRODUCT_ISSUED:
          System.out.println(" Book is currently checked out");
          break;
        case Warehouse.PRODUCT_HAS_HOLD:
          System.out.println("Book has a hold");
          break;
        case Warehouse.OPERATION_FAILED:
          System.out.println("Book could not be removed");
          break;
        case Warehouse.OPERATION_COMPLETED:
          System.out.println(" Book has been removed");
          break;
        default:
          System.out.println("An error has occurred");
      }
      if (!yesOrNo("Remove more books?")) {
        break;
      }
    } while (true);
  }
  public void addWishList() {           //was place hold
    String memberID = getToken("Enter member id");
    String bookID = getToken("Enter book id");
    int duration = getNumber("Enter duration of hold");   //might be quantity
    int result = warehouse.addToWishList(memberID, bookID, duration);
    switch(result){
      case Warehouse.PRODUCT_NOT_FOUND:
        System.out.println("No such Book in Library");
        break;
      case Warehouse.PRODUCT_NOT_ISSUED:
        System.out.println(" Book is not checked out");
        break;
      case Warehouse.NO_SUCH_CLIENT:
        System.out.println("Not a valid member ID");
        break;
      case Warehouse.HOLD_PLACED:
        System.out.println("A hold has been placed");
        break;
      default:
        System.out.println("An error has occurred");
    }
  }
  public void removeFromWishList() {      //was remove hold
    String memberID = getToken("Enter member id");
    String bookID = getToken("Enter book id");
    int result = warehouse.removeHold(memberID, bookID);
    switch(result){
      case Warehouse.PRODUCT_NOT_FOUND:
        System.out.println("No such Book in Library");
        break;
      case Warehouse.NO_SUCH_CLIENT:
        System.out.println("Not a valid member ID");
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
      String bookID = getToken("Enter book id");
      result = warehouse.processHold(bookID);
      if (result != null) {
        System.out.println(result);
      } else {
        System.out.println("No valid holds left");
      }
      if (!yesOrNo("Process more books?")) {
        break;
      }
    } while (true);
  }
  public void getInvoices() {     //was transactions
    Iterator result;
    String memberID = getToken("Enter member id");
    Calendar date  = getDate("Please enter the date for which you want records as mm/dd/yy");
    result = warehouse.getTransactions(memberID,date);
    if (result == null) {
      System.out.println("Invalid Member ID");
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
        case ADD_MEMBER:        addClient();
                                break;
        case ADD_BOOKS:         addProducts();
                                break;
        case ISSUE_BOOKS:       listProducts();
                                break;
        case RETURN_BOOKS:      acceptShipment();
                                break;
        case REMOVE_BOOKS:      removeProducts();
                                break;
        case RENEW_BOOKS:       showClientWishList();
                                break;
        case PLACE_HOLD:        addWishList();
                                break;
        case REMOVE_HOLD:       removeFromWishList();
                                break;
        case PROCESS_HOLD:      checkout();
                                break;
        case GET_TRANSACTIONS:  getInvoices();
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
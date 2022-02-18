import java.util.*;
import java.io.*;
import java.lang.*;

public class UserInterface{
    private static UserInterface userInterface;
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static Warehouse warehouse;
    private static final int EXIT = 0;
    private static final int ADD_CLIENT = 1;
    private static final int ADD_PRODUCTS = 2;
    private static final int ADD_PRODUCT_TO_WISHLIST = 3;
    private static final int DISPLAY_PRODUCTS = 4;
    private static final int DISPLAY_CLIENTS = 5;
    private static final int DISPLAY_CLIENT_WISHLIST = 6;
    private static final int SAVE = 7;
    private static final int RETRIEVE = 8;
    private static final int HELP = 9;

    private UserInterface() {
        if (yesOrNo("Look for saved data and  use it?")) {
          retrieve();
        } 
        else {
          warehouse = Warehouse.instance();
        }
    }

    public static UserInterface instance() {
        if (userInterface == null) {
          return userInterface = new UserInterface();
        } 
        else {
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

    public int getCommand() {
        do {
          try {
            int value = Integer.parseInt(getToken("Enter command:" + HELP + " for help"));
            if (value >= EXIT && value <= HELP) {
              return value;
            }
          } 
          catch (NumberFormatException nfe) {
            System.out.println("Enter a number");
          }
        } 
        while (true);
    }
    
    public void help() {
        System.out.println("Enter a number between 0 and 12 as explained below:");
        System.out.println(EXIT + " to Exit\n");
        System.out.println(ADD_CLIENT + " to add a client");
        System.out.println(ADD_PRODUCTS + " to  add products");
        System.out.println(ADD_PRODUCT_TO_WISHLIST + " to add products to client wishlist");
        System.out.println(DISPLAY_PRODUCTS + " to display products");
        System.out.println(DISPLAY_CLIENTS + " to display clients");
        System.out.println(DISPLAY_CLIENT_WISHLIST + " to display client wishlist");
        System.out.println(SAVE + " to  save data");
        System.out.println(RETRIEVE + " to  retrieve");
        System.out.println(HELP + " for help");
      }

    public void addClient() {
        String name = getToken("Enter client name");
        String address = getToken("Enter address");
        String id = getToken("Enter id");
        Client result;
        result = warehouse.addClient(name, address, id);
        if (result == null) {
          System.out.println("Could not add client");
        }
        System.out.println(result);
      }

    public void addProducts() {
        Product result;
        do {
          String name = getToken("Enter  name");
          String qty = getToken("Enter qty");
          String salePrice = getToken("Enter sale price");
          String id = getToken("Enter product id");
          result = warehouse.addProduct(name, qty, salePrice, id);
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

    public void showProducts() {
        Iterator allProducts = warehouse.getProducts();
        while (allProducts.hasNext()){
        Product product = (Product)(allProducts.next());
            System.out.println(product.toString());
        }
    }

    public void showClients() {
        Iterator allClients = warehouse.getClients();
        while (allClients.hasNext()){
        Client client = (Client)(allClients.next());
            System.out.println(client.toString());
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
            System.out.println(" The warehouse has been successfully retrieved from the file WarehouseData \n" );
            warehouse = tempWarehouse;
          } else {
            System.out.println("File doesnt exist; creating new warehouse" );
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
            case ADD_PRODUCTS:      addProducts();
                                    break;
            case ADD_PRODUCT_TO_WISHLIST:       issueBooks();
                                    break;
            case DISPLAY_PRODUCTS:  showProducts();
                                    break;
            case DISPLAY_CLIENTS:   showClients();
                                    break;
            case DISPLAY_CLIENT_WISHLIST: renewBooks();
                                    break;
            case PLACE_HOLD:        placeHold();
                                    break;
            case REMOVE_HOLD:       removeHold();
                                    break;
            case PROCESS_HOLD:      processHolds();
                                    break;
            case GET_TRANSACTIONS:  getTransactions();
                                    break;
            case SAVE:              save();
                                    break;
            case RETRIEVE:          retrieve();
                                    break;
            case SHOW_MEMBERS:	showMembers();
                                    break; 		
            case SHOW_BOOKS:	showBooks();
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

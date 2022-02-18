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
            int value = Integer.parseInt(getToken("Enter command: " + HELP + " for help"));
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
        System.out.println("Enter a number between 0 and 9 as explained below:");
        System.out.println(EXIT + " to Exit\n");
        System.out.println(ADD_CLIENT + " to add a client");
        System.out.println(ADD_PRODUCTS + " to add products");
        System.out.println(ADD_PRODUCT_TO_WISHLIST + " to add products to client wishlist");
        System.out.println(DISPLAY_PRODUCTS + " to display products");
        System.out.println(DISPLAY_CLIENTS + " to display clients");
        System.out.println(DISPLAY_CLIENT_WISHLIST + " to display client wishlist");
        System.out.println(SAVE + " to save data");
        System.out.println(RETRIEVE + " to retrieve");
        System.out.println(HELP + " for help");
      }

    public void addClient() {
        String name = getToken("Enter client name");
        String address = getToken("Enter address");
        String id = warehouse.clientIDGen();
        System.out.println("A new ID was generated for the client");
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
          String name = getToken("Enter product name");
          String qty = getToken("Enter product quantity");
          String salePrice = getToken("Enter product sale price");
          String id = getToken("Enter product ID");
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

    public void addWishlistItem()
    {
        Client clientPass = null;
        Product productPass = null;

        boolean check1 = false;
        boolean check2 = false;
        do {
            String clientID = getToken("Enter client ID");
            clientPass = warehouse.checkAgainstClientList(clientID); //Check against existing client IDs
            if( clientPass == null ) { System.out.println("No client found with that ID. Enter again or, if you wish to exit, enter 0"); }
                else check1 = true;
        } while (!check1);
        do {
            String productID = getToken("Enter product ID");
            productPass = warehouse.checkAgainstProductList(productID); //Check against existing product IDs
            if( productPass == null ) { System.out.println("No product found with that ID. Enter again or, if you wish to exit, enter 0"); }
                else check2 = true;
        } while (!check2);

        Client.addProductWishlist(productPass, clientPass);
    }

    public void getWishList()
    {
        Client clientPass = null;
        boolean check0 = false;
        do {
            String clientID = getToken("Enter client ID");
            clientPass = warehouse.checkAgainstClientList(clientID); //Check against existing client IDs
            if( clientPass == null ) { System.out.println("No client found with that ID. Enter again or, if you wish to exit, enter 0"); }
            else check0 = true;
        } while (!check0);

        Client.getWishlist(clientPass);
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
            case ADD_PRODUCT_TO_WISHLIST: addWishlistItem();
                                    break;
            case DISPLAY_PRODUCTS:  showProducts();
                                    break;
            case DISPLAY_CLIENTS:   showClients();
                                    break;
            case DISPLAY_CLIENT_WISHLIST: /*Not defined yet*/
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

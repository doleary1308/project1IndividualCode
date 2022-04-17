//The omnipresent Clerk
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.text.*;
import java.io.*;
public class ClerkState extends WarehouseState implements ActionListener {
  private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
  private static Warehouse warehouse;
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

  public JFrame frame = new JFrame("Clerk Menu");
  public JTextArea ta = new JTextArea();

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
    String name = (String)JOptionPane.showInputDialog(
            frame,
            "Enter the client's name\n",
            "Add Client",
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            "");
    String address = (String)JOptionPane.showInputDialog(
            frame,
            "Enter the client's address\n",
            "Add Client",
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            "");
    String phone = (String)JOptionPane.showInputDialog(
            frame,
            "Enter the client's phone number\n",
            "Add Client",
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            "");
    Client result;
    result = warehouse.addClient(name, address, phone);
    if (result == null) {
      JOptionPane.showMessageDialog(frame,
              "Could not add client",
              "Error",
              JOptionPane.ERROR_MESSAGE);
    }
    ta.append(String.valueOf(result) + "\n");
  }

  public void queryClients()
  {
    frame.setVisible(false);
    (WarehouseContext.instance()).changeState(5);
  }

  public void acceptClientPayment() {
    String clientID = (String)JOptionPane.showInputDialog(
            frame,
            "Enter the client ID\n",
            "Accept Payment",
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            "C1");
    float payment = Float.parseFloat((String) JOptionPane.showInputDialog(
            frame,
            "Enter the payment amount\n",
            "Accept Payment",
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            ""));
    Client result = warehouse.acceptClientPayment(clientID, payment);
    if(result == null) {
      JOptionPane.showMessageDialog(frame,
              "Payment could not be accepted",
              "Error",
              JOptionPane.ERROR_MESSAGE);
    } else {
      JOptionPane.showMessageDialog(frame,
              "Payment accepted for: \n" + result +
                      "\nClient's current balance is " + result.getBalance()); }
  }

  public void getWareHouseProducts()
  {
    ta.setText(warehouse.printProducts());
  }

  public void addProductsToWarehouse() {
    Product result;
    String name = (String)JOptionPane.showInputDialog(
            frame,
            "Enter the product name\n",
            "Add Product",
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            "");
    int quantity = Integer.parseInt((String)JOptionPane.showInputDialog(
            frame,
            "Enter the quantity to stock\n",
            "Add Product",
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            ""));
    float price = Float.parseFloat((String)JOptionPane.showInputDialog(
            frame,
            "Enter the price per unit\n",
            "Add Product",
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            "P1"));
    result = warehouse.addProduct(name, quantity, price);
    if (result != null) {
      ta.append(result + "\n");
    } else {
      JOptionPane.showMessageDialog(frame,
              "Product could not be added",
              "Error",
              JOptionPane.ERROR_MESSAGE);
    }
  }

  public void removeProducts() {
    int result;
    String productID = (String)JOptionPane.showInputDialog(
            frame,
            "Enter the product ID\n",
            "Remove Product",
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            "");
    result = warehouse.removeProduct(productID);
    switch(result){
      case Warehouse.PRODUCT_NOT_FOUND:
        JOptionPane.showMessageDialog(frame,
                "No such Product in Warehouse",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        break;
      case Warehouse.PRODUCT_ISSUED:
        JOptionPane.showMessageDialog(frame,
                "Product is currently in a cart",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        break;
      case Warehouse.PRODUCT_HAS_WAIT:
        JOptionPane.showMessageDialog(frame,
                "Product is waitlisted",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        break;
      case Warehouse.OPERATION_FAILED:
        JOptionPane.showMessageDialog(frame,
                "Product could not be removed",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        break;
      case Warehouse.OPERATION_COMPLETED:
        JOptionPane.showMessageDialog(frame,
                " Product has been removed");
        break;
      default:
        JOptionPane.showMessageDialog(frame,
                "An error has occurred",
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
  }

  public void showProductWaitlist() {
    String productID = (String)JOptionPane.showInputDialog(
            frame,
            "Enter the product ID\n",
            "Show Waitlist",
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            "P1");
    if(warehouse.searchProducts(productID) != null){
      ta.setText(warehouse.printProductWaitlist(productID));
    } else {
      JOptionPane.showMessageDialog(frame,
              "Product not found",
              "Error",
              JOptionPane.ERROR_MESSAGE); }
  }

  public void clientMenu() {
    String userID = (String)JOptionPane.showInputDialog(
            frame,
            "Enter the client's ID\n",
            "Become Client",
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            "C1");
    if (Warehouse.instance().searchMembership(userID) != null){
      (WarehouseContext.instance()).setUser(userID);
      frame.setVisible(false);
      (WarehouseContext.instance()).changeState(1);
    } else {
      JOptionPane.showMessageDialog(frame,
              "Invalid client ID",
              "Error",
              JOptionPane.ERROR_MESSAGE); }
  }

  public void logout() {
    if ((WarehouseContext.instance()).getLogin() == WarehouseContext.IsManager)
    { managerState(); }
    else if (WarehouseContext.instance().getLogin() == WarehouseContext.IsClerk)
    { loginState(); }
    else
    loginState(); // exit code 2, indicates error
  }
  public void loginState()
  {
    System.out.println(" going to login \n");
    frame.setVisible(false);
    (WarehouseContext.instance()).changeState(2); // exit with a code 2
  }
  public void managerState()
  {
    System.out.println(" going to manager \n ");
    frame.setVisible(false);
    (WarehouseContext.instance()).changeState(3); // exit with a code 1
  }

  public void run() {
    //frame.setDefaultCloseOperation();
    frame.setSize(1100, 250);

    //Creating the panel at bottom and adding components
    JPanel panel = new JPanel(); // the panel is not visible in output
    JButton back = new JButton("Back");
    back.addActionListener(this);
    back.setActionCommand(String.valueOf(EXIT));

    JButton addClient = new JButton("Add a Client");
    addClient.addActionListener(this);
    addClient.setActionCommand(String.valueOf(ADD_CLIENT));

    JButton queryClients = new JButton("Query Clients");
    queryClients.addActionListener(this);
    queryClients.setActionCommand(String.valueOf(QUERY_CLIENTS));

    JButton acceptClientPayment = new JButton("Accept a Payment");
    acceptClientPayment.addActionListener(this);
    acceptClientPayment.setActionCommand(String.valueOf(ACCEPT_CLIENT_PAYMENT));

    JButton addProduct = new JButton("Add a Product");
    addProduct.addActionListener(this);
    addProduct.setActionCommand(String.valueOf(ADD_PRODUCTS_TO_WAREHOUSE));

    JButton showProducts = new JButton("Show Products");
    showProducts.addActionListener(this);
    showProducts.setActionCommand(String.valueOf(SHOW_WAREHOUSE_PRODUCTS));

    JButton removeProduct = new JButton("Remove Products");
    removeProduct.addActionListener(this);
    removeProduct.setActionCommand(String.valueOf(REMOVE_PRODUCTS));

    JButton showWaitlist = new JButton("Show a Waitlist");
    showWaitlist.addActionListener(this);
    showWaitlist.setActionCommand(String.valueOf(SHOW_PRODUCT_WAITLIST));

    JButton clientMenu = new JButton("Become a Client");
    clientMenu.addActionListener(this);
    clientMenu.setActionCommand(String.valueOf(CLIENTMENU));

    panel.add(back); // Components Added using Flow Layout
    panel.add(addClient);
    panel.add(queryClients);
    panel.add(acceptClientPayment);
    panel.add(addProduct);
    panel.add(showProducts);
    panel.add(removeProduct);
    panel.add(showWaitlist);
    panel.add(clientMenu);

    //Adding Components to the frame.
    frame.getContentPane().add(BorderLayout.SOUTH, panel);
    //frame.getContentPane().add(BorderLayout.NORTH, mb);
    frame.getContentPane().add(BorderLayout.CENTER, ta);
    frame.setVisible(true);
  }

  @Override
  public void actionPerformed(ActionEvent ae) {
    int command = Integer.parseInt(ae.getActionCommand());
    switch (command) {
      case EXIT:       logout();
        break;
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
      /*case HELP:              help();
        break;*/
    }

  }
}

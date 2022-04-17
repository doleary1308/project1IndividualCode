import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JFormattedTextField.AbstractFormatter;
import java.text.*;
import java.io.*;
public class ClientState extends WarehouseState implements ActionListener {
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

  public JDialog frame = new JDialog(/*"Client Menu"*/);
  public JTextArea ta = new JTextArea();

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
        JOptionPane.showMessageDialog(frame,
                prompt);
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
        JOptionPane.showMessageDialog(frame,
                "Please input a number ",
                "Error",
                JOptionPane.ERROR_MESSAGE);
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
        JOptionPane.showMessageDialog(frame,
                "Please input a date as mm/dd/yy",
                "Error",
                JOptionPane.ERROR_MESSAGE);
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
        JOptionPane.showMessageDialog(frame,
                "Please input a number ",
                "Error",
                JOptionPane.ERROR_MESSAGE);
      }
    } while (true);
  }

  /*public void help() {
    System.out.println("Enter a number as explained below:");
    System.out.println(EXIT + " to Exit");
    System.out.println(SHOW_CLIENT_DETAILS + " to view your details");
    System.out.println(CLIENTCARTSTATE + " to view your cart and its actions");
    System.out.println(SHOW_SITE_PRODUCTS + " to show the products available");
    System.out.println(GET_INVOICES + " to print invoices");
    System.out.println(HELP + " for help");
  }*/

  public void getClientDetails() {
    Client result;
    String clientID = WarehouseContext.instance().getUser();
    result = warehouse.searchMembership(clientID);
    if (result == null) {
      JOptionPane.showMessageDialog(frame,
              "Invalid Client ID",
              "Error",
              JOptionPane.ERROR_MESSAGE);
    } else {
      ta.setText(result.toString());
    }

  }

  public void clientCartState() {
    frame.setVisible(false);
    (WarehouseContext.instance()).changeState(4);
  }

  public void getSiteProducts()
  {
    ta.setText(warehouse.printProducts());
  }

  public void getInvoices() {
    Iterator result;
    String textAreaFill = "";
    String clientID = WarehouseContext.instance().getUser();

    int year = Integer.parseInt ((String)JOptionPane.showInputDialog(
            frame,
            "For which year?\n",
            "Get Invoices",
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            ""));
    int month = Integer.parseInt ((String)JOptionPane.showInputDialog(
            frame,
            "For which month?\n",
            "Get Invoices",
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            ""));
    month--;
    if(month==0){month=12;}
    int day = Integer.parseInt ((String)JOptionPane.showInputDialog(
            frame,
            "For which day?\n",
            "Get Invoices",
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            ""));
    Calendar date = new GregorianCalendar(year, month, day);
    result = warehouse.getInvoices(clientID,date);
    if (result == null) {
      JOptionPane.showMessageDialog(frame,
              "Invalid Client ID",
              "Error",
              JOptionPane.ERROR_MESSAGE);
    } else {
      while(result.hasNext()) {
        Invoice invoice = (Invoice) result.next();
        textAreaFill += (invoice.getType() + "   "   + invoice.getName() + "\n");
      }
      ta.setText(textAreaFill);
      JOptionPane.showMessageDialog(frame,
              "\n  Completed invoice query \n");
    }
  }


  public void run() {
    //frame.setDefaultCloseOperation();
    frame.setSize(630, 150);

    //Creating the panel at bottom and adding components
    JPanel panel = new JPanel(); // the panel is not visible in output
    JButton back = new JButton("Back");
    back.addActionListener(this);
    back.setActionCommand(String.valueOf(EXIT));

    JButton showClientDetails = new JButton("My Details");
    showClientDetails.addActionListener(this);
    showClientDetails.setActionCommand(String.valueOf(SHOW_CLIENT_DETAILS));

    JButton clientCartState = new JButton("My Cart");
    clientCartState.addActionListener(this);
    clientCartState.setActionCommand(String.valueOf(CLIENTCARTSTATE));

    JButton showProducts = new JButton("Show Products");
    showProducts.addActionListener(this);
    showProducts.setActionCommand(String.valueOf(SHOW_SITE_PRODUCTS));

    JButton getInvoices = new JButton("My Invoices");
    getInvoices.addActionListener(this);
    getInvoices.setActionCommand(String.valueOf(GET_INVOICES));

    panel.add(back); // Components Added using Flow Layout
    panel.add(showClientDetails);
    panel.add(clientCartState);
    panel.add(showProducts);
    panel.add(getInvoices);

    // Text Area at the Center
    getClientDetails();

    //Adding Components to the frame.
    frame.getContentPane().add(BorderLayout.SOUTH, panel);
    //frame.getContentPane().add(BorderLayout.NORTH, mb);
    frame.getContentPane().add(BorderLayout.CENTER, ta);
    frame.setVisible(true);
  }

  public void logout()
  {
    if (  ((WarehouseContext.instance()).getLogin() == WarehouseContext.IsClerk) ||
            ((WarehouseContext.instance()).getLogin() == WarehouseContext.IsManager))
    { clerkState(); }
    else if (WarehouseContext.instance().getLogin() == WarehouseContext.IsUser)
    { loginState(); }
    else
      loginState();
  }
  public void loginState()
  {
    System.out.println(" going to login \n");
    frame.setVisible(false);
    (WarehouseContext.instance()).changeState(2); // exit with a code 2
  }
  public void clerkState()
  {
    System.out.println(" going to clerk \n ");
    frame.setVisible(false);
    (WarehouseContext.instance()).changeState(0);
  }

  @Override
  public void actionPerformed(ActionEvent ae) {
    int command = Integer.parseInt(ae.getActionCommand());
    switch (command) {
      case EXIT:       logout();
        break;
      case SHOW_CLIENT_DETAILS:       getClientDetails();
        break;
      case CLIENTCARTSTATE:       clientCartState();
        break;
      case SHOW_SITE_PRODUCTS:       getSiteProducts();
        break;
      case GET_INVOICES:       getInvoices();
        break;
      /*case HELP:              help();
        break;*/
    }

  }
}
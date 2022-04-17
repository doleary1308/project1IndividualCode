import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.text.*;
import java.io.*;
public class ManagerState extends WarehouseState implements ActionListener {
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static Warehouse warehouse;
    private static ManagerState instance;
    private static final int EXIT = 0;
    private static final int ADD_PRODUCTS_TO_WAREHOUSE = 1;
    private static final int ACCEPT_SHIPMENT = 2;
    private static final int CLERKMENU = 3;
    private static final int HELP = 4;
    private ManagerState() {
        super();
        warehouse = Warehouse.instance();
       // context = WarehouseContext.instance();
    }

    public JFrame frame = new JFrame("Manager Menu");
    public JTextArea ta = new JTextArea();

    public static ManagerState instance() {
        if (instance == null) {
            instance = new ManagerState();
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
        System.out.println(ADD_PRODUCTS_TO_WAREHOUSE + " to add products to the warehouse");
        System.out.println(ACCEPT_SHIPMENT + " to accept a shipment and process associated waits");
        System.out.println(CLERKMENU + " to switch to the clerk menu");
        System.out.println(HELP + " for help");
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
    public void acceptShipment() {
        Iterator result;
        String productID = (String)JOptionPane.showInputDialog(
                frame,
                "Enter the product ID\n",
                "Accept Shipment",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "P1");
        int quantity = Integer.parseInt((String)JOptionPane.showInputDialog(
                frame,
                "Enter the amount to stock\n",
                "Accept Shipment",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "P1"));
        result = warehouse.acceptShipment(productID, quantity);
        if (result != null) {
            JOptionPane.showMessageDialog(frame,
                    "Removed waits for: " + result +
                            "Stock remaining for product: " +
                            warehouse.searchProducts(productID));
        } else {
            JOptionPane.showMessageDialog(frame,
                    "No valid waits left");
        }
    }

    public void clerkMenu()
    {
        (WarehouseContext.instance()).changeState(0);
    }

    public void logout()
    {
        frame.setVisible(false);
        (WarehouseContext.instance()).changeState(2);
    }

    public void run() {
        //frame.setDefaultCloseOperation();
        frame.setSize(480, 150);

        //Creating the panel at bottom and adding components
        JPanel panel = new JPanel(); // the panel is not visible in output
        JButton back = new JButton("Back");
        back.addActionListener(this);
        back.setActionCommand(String.valueOf(EXIT));

        JButton addProducts = new JButton("Add a Product");
        addProducts.addActionListener(this);
        addProducts.setActionCommand(String.valueOf(ADD_PRODUCTS_TO_WAREHOUSE));

        JButton acceptShipment = new JButton("Accept a Shipment");
        acceptShipment.addActionListener(this);
        acceptShipment.setActionCommand(String.valueOf(ACCEPT_SHIPMENT));

        JButton clerkMenu = new JButton("Become a Clerk");
        clerkMenu.addActionListener(this);
        clerkMenu.setActionCommand(String.valueOf(CLERKMENU));

        panel.add(back); // Components Added using Flow Layout
        panel.add(addProducts);
        panel.add(acceptShipment);
        panel.add(clerkMenu);

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
            case ADD_PRODUCTS_TO_WAREHOUSE:       addProductsToWarehouse();
                break;
            case ACCEPT_SHIPMENT:       acceptShipment();
                break;
            case CLERKMENU:       clerkMenu();
                break;
            /*case HELP:              help();
        break;*/
        }

    }
}
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.*;
import java.text.*;
import java.io.*;
public class ClientCartState extends WarehouseState implements ActionListener {
    private static ClientCartState clientcartstate;
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static Warehouse warehouse;
    private static final int EXIT = 0;
    private static final int ADD_PRODUCTS_TO_CART = 1;
    private static final int REMOVE_PRODUCTS_FROM_CART = 2;
    private static final int CHANGE_QUANTITY = 3;
    private static final int SHOW_CLIENT_CART = 4;
    private static final int CHECK_OUT = 5;
    private static final int HELP = 6;
    private ClientCartState() {
        warehouse = Warehouse.instance();
    }

    public JFrame frame = new JFrame("Your Cart");
    public JTextArea ta = new JTextArea();

    public static ClientCartState instance() {

        if (clientcartstate == null) {
            return clientcartstate = new ClientCartState();
        } else {
            return clientcartstate;
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
        System.out.println(EXIT + " to Exit your cart");
        System.out.println(ADD_PRODUCTS_TO_CART + " to add products to your cart");
        System.out.println(REMOVE_PRODUCTS_FROM_CART + " to remove products from your cart");
        System.out.println(CHANGE_QUANTITY + " to change the quantities of products your cart");
        System.out.println(SHOW_CLIENT_CART + " to display the contents of your cart");
        System.out.println(CHECK_OUT + " to check out products ");
        System.out.println(HELP + " for help");
    }

    public void addProductsToClientCart() {
        Product result;
        String clientID = WarehouseContext.instance().getUser();
        String productID = (String)JOptionPane.showInputDialog(
                frame,
                "Enter the product ID\n",
                "Add Product",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "P1");
        result = warehouse.issueProduct(clientID, productID);
        if (result != null){
            JOptionPane.showMessageDialog(frame,
                    "Successfully added " + result.getName() + " to cart");
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Product could not be added",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void removeProductsFromClientCart() {
        int result = 0;
        String clientID = WarehouseContext.instance().getUser();
        String productID = (String)JOptionPane.showInputDialog(
                frame,
                "Enter the product ID\n",
                "Add Product",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "P1");
        result = warehouse.returnProduct(clientID, productID);
        if (result != 0){
            JOptionPane.showMessageDialog(frame,
                    "Successfully removed the product");
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Product could not be removed",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void changeQuantity() {
        Product result;
        String clientID = WarehouseContext.instance().getUser();
        String productID = (String)JOptionPane.showInputDialog(
                frame,
                "Enter the product ID\n",
                "Change Quantity",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "P1");
        int addReduce = Integer.parseInt((String) JOptionPane.showInputDialog(
                frame,
                "Enter the amount to change the product by.\n"
                        + "If you wish to reduce, enter a negative number",
                "Change Quantity",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "1"));
        result = warehouse.changeProductQuantity(clientID, productID, addReduce);
        if (result != null){
            JOptionPane.showMessageDialog(frame,
                    "Successfully adjusted " + result.getName());
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Product quantity could not be adjusted",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    public String showCart() {
        String clientID = WarehouseContext.instance().getUser();
        return warehouse.getClientCartProducts(clientID);
    }

    public void updateCartDisplay() {
         ta.setText(showCart());
    }

    public void checkOut() {
        Product result;
        String clientID = WarehouseContext.instance().getUser();
        Iterator cartProducts = warehouse.getClientCartData(clientID);
        while (cartProducts.hasNext()){
            Product product = (Product)(cartProducts.next());
            if ((JOptionPane.showConfirmDialog(
                    frame,
                    "Confirm 1 " + product.getName() + "?",
                    "Confirm Checkout",
                    JOptionPane.YES_NO_OPTION)) == 0) {
                result = warehouse.checkOut(product.getId(), clientID);
                if (result != null){
                    JOptionPane.showMessageDialog(frame,
                            "Successfully checked out " + result.getName());
                } else {
                    JOptionPane.showMessageDialog(frame,
                            "Product is not able to be checked out",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public void actionPerformed(ActionEvent ae) {
        int command = Integer.parseInt(ae.getActionCommand());
        switch (command) {
            case EXIT:       logout();
                break;
            case ADD_PRODUCTS_TO_CART:       addProductsToClientCart(); updateCartDisplay();
                break;
            case REMOVE_PRODUCTS_FROM_CART:       removeProductsFromClientCart(); updateCartDisplay();
                break;
            case CHANGE_QUANTITY:       changeQuantity(); updateCartDisplay();
                break;
            case SHOW_CLIENT_CART:       showCart();
                break;
            case CHECK_OUT:       checkOut();
                break;
            /*case HELP:              help();
        break;*/
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

        JButton addProducts = new JButton("Add to Cart");
        addProducts.addActionListener(this);
        addProducts.setActionCommand(String.valueOf(ADD_PRODUCTS_TO_CART));

        JButton removeProducts = new JButton("Remove from Cart");
        removeProducts.addActionListener(this);
        removeProducts.setActionCommand(String.valueOf(REMOVE_PRODUCTS_FROM_CART));

        JButton changeQuantities = new JButton("Change Product Quantity");
        changeQuantities.addActionListener(this);
        changeQuantities.setActionCommand(String.valueOf(CHANGE_QUANTITY));

        JButton checkOut = new JButton("Check Out");
        checkOut.addActionListener(this);
        checkOut.setActionCommand(String.valueOf(CHECK_OUT));

        panel.add(back); // Components Added using Flow Layout
        panel.add(addProducts);
        panel.add(removeProducts);
        panel.add(changeQuantities);
        panel.add(checkOut);

        // Text Area at the Center
        updateCartDisplay();

        //Adding Components to the frame.
        frame.getContentPane().add(BorderLayout.SOUTH, panel);
        //frame.getContentPane().add(BorderLayout.NORTH, mb);
        frame.getContentPane().add(BorderLayout.CENTER, ta);
        frame.setVisible(true);

    }

    public void logout()
    {
        frame.setVisible(false);
        (WarehouseContext.instance()).changeState(1); // exit with a code 2
    }
}

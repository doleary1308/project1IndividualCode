import java.util.*;
import java.text.*;
import java.io.*;
public class ClientCartState extends WarehouseState {
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
        do {
            String productID = getToken("Enter product id");
            result = warehouse.issueProduct(clientID, productID);
            if (result != null){
                System.out.println("Successfully added " + result.getName() + " to cart");
            } else {
                System.out.println("Product could not be added");
            }
            if (!yesOrNo("Add more products?")) {
                break;
            }
        } while (true);
    }

    public void removeProductsFromClientCart() {
        int result = 0;
        String clientID = WarehouseContext.instance().getUser();
        do {
            String productID = getToken("Enter product id");
            result = warehouse.returnProduct(clientID, productID);
            if (result != 0){
                System.out.println(" Successfully removed the product");
            } else {
                System.out.println("Product could not be removed");
            }
            if (!yesOrNo("Remove more products?")) {
                break;
            }
        } while (true);
    }

    public void changeQuantity()
    {
        Product result;
        String clientID = WarehouseContext.instance().getUser();
        String productID = getToken("Enter product id");
        int addReduce = Integer.parseInt(getToken("Enter the amount to change the product by.\n"
                                         + "If you wish to reduce, enter a negative number"));
        result = warehouse.changeProductQuantity(clientID, productID, addReduce);
        if (result != null){
            System.out.println(" Successfully adjusted " + result.getName());
        } else {
            System.out.println("Product quantity could not be adjusted");
        }
    }

    public void showCart()
    {
        String clientID = WarehouseContext.instance().getUser();
        System.out.print(warehouse.getClientCartProducts(clientID));
    }

    public void checkOut() {
        Product result;
        String clientID = WarehouseContext.instance().getUser();
        Iterator cartProducts = warehouse.getClientCartData(clientID);
        while (cartProducts.hasNext()){
            Product product = (Product)(cartProducts.next());
            if (yesOrNo(product.getName())) {
                result = warehouse.checkOut(product.getId(), clientID);
                if (result != null){
                    System.out.println("Successfully checked out " + result.getName());
                } else {
                    System.out.println("Product is not able to be checked out");
                }
            }
        }
    }

    public void process() {
        int command;
        help();
        while ((command = getCommand()) != EXIT) {
            switch (command) {

                case ADD_PRODUCTS_TO_CART:       addProductsToClientCart();
                    break;
                case REMOVE_PRODUCTS_FROM_CART:       removeProductsFromClientCart();
                    break;
                case CHANGE_QUANTITY:       changeQuantity();
                    break;
                case SHOW_CLIENT_CART:       showCart();
                    break;
                case CHECK_OUT:       checkOut();
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
        (WarehouseContext.instance()).changeState(1); // exit with a code 2
    }
}

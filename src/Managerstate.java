import java.util.*;
import java.text.*;
import java.io.*;
public class Managerstate extends WarehouseState {
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static Warehouse warehouse;
    private WarehouseContext context;
    private static Managerstate instance;
    private static final int EXIT = 0;
    private static final int ADD_PRODUCTS_TO_WAREHOUSE = 1;
    private static final int ACCEPT_SHIPMENT = 2;
    private static final int CLERKMENU = 3;
    private static final int HELP = 4;
    private Managerstate() {
        super();
        warehouse = Warehouse.instance();
        context = WarehouseContext.instance();
    }

    public static Managerstate instance() {
        if (instance == null) {
            instance = new Managerstate();
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
        System.out.println(EXIT + " to Exit\n");

        System.out.println(CLERKMENU + " to switch to the clerk menu");
        System.out.println(HELP + " for help");
    }

    public void addProductsToWarehouse() {
        Product result;
        do {
            String name = getToken("Enter name");
            String quantity = getToken("Enter quantity");
            result = warehouse.addProduct(name, quantity);
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
    public void acceptShipment() {
        Client result;
        do {
            String productID = getToken("Enter product id");
            result = warehouse.acceptShipment(productID);
            if (result != null) {
                System.out.println(result);
            } else {
                System.out.println("No valid waits left");
            }
            if (!yesOrNo("Process more products?")) {
                break;
            }
        } while (true);
    }

    public void clerkmenu()
    {
        (WarehouseContext.instance()).changeState(0);
    }

    public void logout()
    {
        (WarehouseContext.instance()).changeState(0); // exit with a code 0
    }


    public void process() {
        int command;
        help();
        while ((command = getCommand()) != EXIT) {
            switch (command) {
                case ADD_PRODUCTS_TO_WAREHOUSE:         addProductsToWarehouse();
                    break;
                case ACCEPT_SHIPMENT:      acceptShipment();
                    break;
                case CLERKMENU:          clerkmenu();
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
}
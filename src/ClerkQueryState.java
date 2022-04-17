import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.text.*;
import java.io.*;
public class ClerkQueryState extends WarehouseState implements ActionListener {
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static Warehouse warehouse;
    private WarehouseContext context;
    private static ClerkQueryState instance;
    private static final int EXIT = 0;
    private static final int ALL_CLIENTS = 1;
    private static final int CLIENTS_OUTSTANDING = 2;
    private static final int CLIENTS_INACTIVE = 3;
    private static final int HELP = 4;
    private ClerkQueryState() {
        super();
        warehouse = Warehouse.instance();
        //context = LibContext.instance();
    }

    public JFrame frame = new JFrame("Client Query");
    public JTextArea ta = new JTextArea();

    public static ClerkQueryState instance() {
        if (instance == null) {
            instance = new ClerkQueryState();
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
        System.out.println(EXIT + " to go back");
        System.out.println(ALL_CLIENTS + " to see all clients");
        System.out.println(CLIENTS_OUTSTANDING + " to see all clients with outstanding balance");
        System.out.println(CLIENTS_INACTIVE + " to see all clients with no transaction activity for 6 months (180 days)");
        System.out.println(HELP + " for help");
    }

    public void queryAll()
    {
        ta.setText(warehouse.printClients());
    }

    public void queryOutstanding()
    {
        ta.setText(warehouse.clientsOutstanding());
    }

    public void queryInactive()
    {
        ta.setText(warehouse.clientsInactive());
    }


    public void logout()
    {
        frame.setVisible(false);
        (WarehouseContext.instance()).changeState(0);
    }


    public void run() {
        //frame.setDefaultCloseOperation();
        frame.setSize(580, 300);

        //Creating the panel at bottom and adding components
        JPanel panel = new JPanel(); // the panel is not visible in output
        JButton back = new JButton("Back");
        back.addActionListener(this);
        back.setActionCommand(String.valueOf(EXIT));

        JButton allClients = new JButton("All Clients");
        allClients.addActionListener(this);
        allClients.setActionCommand(String.valueOf(ALL_CLIENTS));

        JButton clientsOutstanding = new JButton("W/ Outstanding Balance");
        clientsOutstanding.addActionListener(this);
        clientsOutstanding.setActionCommand(String.valueOf(CLIENTS_OUTSTANDING));

        JButton clientsInactive = new JButton("Inactive Clients");
        clientsInactive.addActionListener(this);
        clientsInactive.setActionCommand(String.valueOf(CLIENTS_INACTIVE));

        panel.add(back); // Components Added using Flow Layout
        panel.add(allClients);
        panel.add(clientsOutstanding);
        panel.add(clientsInactive);

        ta.setText("Press a button to perform a client query");

        //Adding Components to the frame.
        frame.getContentPane().add(BorderLayout.SOUTH, panel);
        frame.getContentPane().add(BorderLayout.CENTER, ta);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        int command = Integer.parseInt(ae.getActionCommand());
        switch (command) {
            case EXIT:       logout();
                break;
            case ALL_CLIENTS:       queryAll();
                break;
            case CLIENTS_OUTSTANDING:       queryOutstanding();
                break;
            case CLIENTS_INACTIVE:       queryInactive();
                break;
            /*case HELP:              help();
        break;*/
        }

    }
}
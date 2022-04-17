import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.text.*;
import java.io.*;
public class LoginState extends WarehouseState implements ActionListener {
    private static final int CLERK_LOGIN = 0;
    private static final int CLIENT_LOGIN = 1;
    private static final int MANAGER_LOGIN = 2;
    private static final int EXIT = 3;            //getting around command issue
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private WarehouseContext context;
    private static LoginState instance;
    private LoginState() {
        super();
        //  context = WarehouseContext.instance();
    }

    public JFrame frame = new JFrame("Login");

    public static LoginState instance() {
        if (instance == null) {
            instance = new LoginState();
        }
        return instance;
    }

    public int getCommand() {
        do {
            try {
                int value = Integer.parseInt(getToken("Enter command:" ));
                if (value <= EXIT && value >= CLERK_LOGIN) {
                    return value;
                }
            } catch (NumberFormatException nfe) {
                System.out.println("Enter a number");
            }
        } while (true);
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
    private void manager(){
        (WarehouseContext.instance()).setLogin(WarehouseContext.IsManager);
        frame.setVisible(false);
        (WarehouseContext.instance()).changeState(3);
    }

    private void clerk(){
        (WarehouseContext.instance()).setLogin(WarehouseContext.IsClerk);
        frame.setVisible(false);
        (WarehouseContext.instance()).changeState(0);
    }

    private void client(){
        String clientID = (String) JOptionPane.showInputDialog(
                frame,
                "Enter the client ID\n",
                "Client",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "C1");
        if (Warehouse.instance().searchMembership(clientID) != null){
            (WarehouseContext.instance()).setLogin(WarehouseContext.IsUser);
            frame.setVisible(false);
            (WarehouseContext.instance()).setUser(clientID);
            (WarehouseContext.instance()).changeState(1);
        }
        else
            JOptionPane.showMessageDialog(frame,
                    "Invalid client ID",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
    }

    public void run() {
        //frame.setDefaultCloseOperation();
        frame.setSize(400, 80);

        //Creating the panel at bottom and adding components
        JPanel panel = new JPanel(); // the panel is not visible in output
        JButton back = new JButton("Exit");
        back.addActionListener(this);
        back.setActionCommand(String.valueOf(EXIT));

        JButton clientLogin = new JButton("Client");
        clientLogin.addActionListener(this);
        clientLogin.setActionCommand(String.valueOf(CLIENT_LOGIN));

        JButton managerLogin = new JButton("Manager");
        managerLogin.addActionListener(this);
        managerLogin.setActionCommand(String.valueOf(MANAGER_LOGIN));

        JButton clerkLogin = new JButton("Clerk");
        clerkLogin.addActionListener(this);
        clerkLogin.setActionCommand(String.valueOf(CLERK_LOGIN));

        panel.add(back); // Components Added using Flow Layout
        panel.add(clientLogin);
        panel.add(clerkLogin);
        panel.add(managerLogin);

        //Adding Components to the frame.
        frame.getContentPane().add(BorderLayout.SOUTH, panel);
        //frame.getContentPane().add(BorderLayout.NORTH, mb);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        int command = Integer.parseInt(ae.getActionCommand());
        switch (command) {
            case EXIT:       System.exit(0);
                break;
            case CLIENT_LOGIN:       client();
                break;
            case MANAGER_LOGIN:       manager();
                break;
            case CLERK_LOGIN:       clerk();
                break;
        }

    }
}

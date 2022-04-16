import java.util.*;
import java.io.*;
public class WarehouseContext {
  
  private int currentState;
  private static Warehouse warehouse;
  private static WarehouseContext context;
  private int currentUser;
  private String userID;
  private BufferedReader reader = new BufferedReader(new 
                                      InputStreamReader(System.in));
  public static final int IsClerk = 0;
  public static final int IsUser = 1;
  public static final int IsManager = 2;
  private WarehouseState[] states;
  private int[][] nextState;

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

  private void retrieve() {
    try {
      Warehouse tempWarehouse = Warehouse.retrieve();
      if (tempWarehouse != null) {
        System.out.println(" The Warehouse has been successfully retrieved from the file WarehouseData \n" );
        warehouse = tempWarehouse;
      } else {
        System.out.println("File doesn't exist; creating new Warehouse" );
        warehouse = Warehouse.instance();
      }
    } catch(Exception cnfe) {
      cnfe.printStackTrace();
    }
  }

  public void setLogin(int code)
  {currentUser = code;}

  public void setUser(String uID)
  { userID = uID;}

  public int getLogin()
  { return currentUser;}

  public String getUser()
  { return userID;}

  private WarehouseContext() { //constructor
    System.out.println("In WarehouseContext constructor");
    if (yesOrNo("Look for saved data and use it?")) {
      retrieve();
    } else {
      warehouse = Warehouse.instance();
    }
    // set up the FSM and transition table;
    states = new WarehouseState[6];
    states[0] = ClerkState.instance();
    states[1] = ClientState.instance();
    states[2] = LoginState.instance();
    states[3] = ManagerState.instance();
    states[4] = ClientCartState.instance();
    states[5] = ClerkQueryState.instance();
    nextState = new int[6][6];
    //Ct = Client
    //Ck = Clerk
    //Mg = Manager
    //Lg = Login
    //Cc = Client Cart
    //Cq = Clerk Query
    //NA = Not Applicable
    //CV = Varies by Context
    nextState[0][0] =-2;nextState[0][1] = 1;nextState[0][2] = 2;nextState[0][3] = 3;nextState[0][4] =-2;nextState[0][5] = 5;
    //Ck->Ck: NA          Ck->Ct: Cl          Ck->Lg: CV          Ck->Mg              Ck->Cc: NA          Ck->Cq: Cq
    nextState[1][0] = 2;nextState[1][1] = 0;nextState[1][2] =-2;nextState[1][3] =-2;nextState[1][4] = 4;nextState[1][5] =-2;
    //Ct->Ck: CV          Ct->Ct: NA          Ct->Lg: CV          Ct->Mg: NA          Ct->Cc: Cc          Ct->Cq: NA
    nextState[2][0] = 0;nextState[2][1] = 1;nextState[2][2] =-1;nextState[2][3] = 3;nextState[2][4] =-2;nextState[2][5] =-2;
    //Lg->Ck: Ck          Lg->Ct: Cl          Lg->Lg: NA          Lg->Mg: Mg          Lg->Cc: NA          Lg->Cq: NA
    nextState[3][0] = 0;nextState[3][1] =-2;nextState[3][2] = 2;nextState[3][3] = 2;nextState[3][4] =-2;nextState[3][5] =-2;
    //Mg->Ck: Ck          Mg->Ct: NA          Mg->Lg: CV/Lg?      Mg->Mg: NA          Mg->Cc: NA          Mg->Cq: NA
    nextState[4][0] =-2;nextState[4][1] = 1;nextState[4][2] =-2;nextState[4][3] =-2;nextState[4][4] =-2;nextState[4][5] =-2;
    //Cc->Ck: NA          Cc->Ct: Ct          Cc->Lg: NA          Cc->Mg: NA          Cc->Cc: NA          Cc->Cq: NA
    nextState[5][0] = 0;nextState[5][1] = 1;nextState[5][2] =-2;nextState[5][3] =-2;nextState[5][4] =-2;nextState[5][5] =-2;
    //Cq->Ck: Ck          Cq->Ct: NA          Cq->Lg: NA          Cq->Mg: NA          Cq->Cc: NA          Cq->Cq: NA
    // nextState[3][2] seems confused?
    //Whatever, it works ¯\_(ツ)_/¯

    currentState = 2;
  }

  public void changeState(int transition)
  {
    //System.out.println("current state " + currentState + " \n \n ");
    currentState = nextState[currentState][transition];
    if (currentState == -2) 
      {System.out.println("Error has occurred"); terminate();}
    if (currentState == -1) 
      terminate();
    //System.out.println("current state " + currentState + " \n \n ");
    states[currentState].run();
  }

  private void terminate()
  {
   if (yesOrNo("Save data?")) {
      if (warehouse.save()) {
         System.out.println(" The Warehouse has been successfully saved in the file WarehouseData \n" );
       } else {
         System.out.println(" There has been an error in saving \n" );
       }
     }
   System.out.println(" Goodbye \n "); System.exit(0);
  }

  public static WarehouseContext instance() {
    if (context == null) {
       System.out.println("calling constructor");
      context = new WarehouseContext();
    }
    return context;
  }

  public void process(){
    states[currentState].run();
  }
  
  public static void main (String[] args){
    WarehouseContext.instance().process();
  }


}

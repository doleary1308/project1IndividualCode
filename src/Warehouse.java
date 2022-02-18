import java.util.*;
import java.lang.*;
import java.io.*;

public class Warehouse implements Serializable {
    private static final long serialVerionUID = 1L;
    public static final int PRODUCT_NOT_FOUND = 1;
    public static final int PRODUCT_NOT_ADDED = 2;
    public static final int PRODUCT_ADDED = 3;
    public static final int OPERATION_COMPLETED = 4;
    public static final int OPERATION_FAILED = 5;
    public static final int NO_SUCH_CLIENT = 6;

    private ProductList productList;
    private ClientList clientList;
    private static Warehouse warehouse;

    private Warehouse(){
        productList = new ProductList();
        clientList = new ClientList();
    }
    
    public static Warehouse instance(){
        if(warehouse == null){
            ClientIdServer.instance();
            return (warehouse = new Warehouse());
        }
        else{
            return warehouse;
        }
    }

    public Product addProduct(String name, String qty, String salePrice, String id){
        Product product = new Product(name, qty, salePrice, id);
        if(productList.insertProduct(product)){
        return(product);
        }
    return null;
    }

    public Client addClient(String name, String address, String id){
        Client client = new Client(name, address, id);
        if(ClientList.insertClient(client)){
            return(client);
        }
        return null;
    }

    public Product checkAgainstProductList(String id) {return productList.checkAgainstProductList(id);}
    public Client checkAgainstClientList(String id) {return clientList.checkAgainstClientList(id);}

    public Iterator getProducts(){
        return productList.getProducts();
    }

    public Iterator getClients(){
        return ClientList.getClients();
    }

    public String newIDGen()
    {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedString = buffer.toString();

        return generatedString;
    }

    public static Warehouse retrieve() {
        try {
          FileInputStream file = new FileInputStream("WarehouseData");
          ObjectInputStream input = new ObjectInputStream(file);
          input.readObject();
          ClientIdServer.retrieve(input);
          return warehouse;
        } catch(IOException ioe) {
          ioe.printStackTrace();
          return null;
        } catch(ClassNotFoundException cnfe) {
          cnfe.printStackTrace();
          return null;
        }
      }

      public static  boolean save() {
        try {
          FileOutputStream file = new FileOutputStream("WarehouseData");
          ObjectOutputStream output = new ObjectOutputStream(file);
          output.writeObject(warehouse);
          output.writeObject(ClientIdServer.instance());
          return true;
        } catch(IOException ioe) {
          ioe.printStackTrace();
          return false;
        }
      }

      private void writeObject(java.io.ObjectOutputStream output) {
        try {
          output.defaultWriteObject();
          output.writeObject(warehouse);
        } catch(IOException ioe) {
          System.out.println(ioe);
        }
      }

      private void readObject(java.io.ObjectInputStream input) {
        try {
          input.defaultReadObject();
          if (warehouse == null) {
            warehouse = (Warehouse) input.readObject();
          } else {
            input.readObject();
          }
        } catch(IOException ioe) {
          ioe.printStackTrace();
        } catch(Exception e) {
          e.printStackTrace();
        }
      }

      public String toString() {
        return warehouse + "\n" + clientList;
      }

}

import java.util.*;
import java.lang.*;
import java.io.*;

public class Client implements Serializable{

    private String name;
    private String address;
    private String id;
    public List wishlist = new LinkedList();

    public Client(String name, String address, String id){
        
        this.name = name;
        this.address = address;
        this.id = id;
    }
    public static boolean addProductWishlist(Product product,Client client){
        client.wishlist.add(product);
        return true;
    }
    public static Iterator getWishlist(Client client){
        return client.wishlist.iterator();
    }

    public String getName(){
        return name;
    }

    public String getAddress(){
        return address;
    }

    public String getId(){
        return id;
    }

    public String toString(){
        return "Name: " + name + " | Address: " + address + " | ID: " + id;
    }

}
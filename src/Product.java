import java.util.*;
import java.io.*;
import java.lang.*;

public class Product implements Serializable {
    private String name;
    public String qty;
    private String salePrice;
    private String id;

    public Product(String name, String qty, String salePrice, String id){
        this.name = name;
        this.qty = qty;
        this.salePrice = salePrice;
        this.id = id; 
    }
    
    public String getName(){
        return name;
    }
    public String getQty(){
        return qty;
    }
    public String getSalePrice(){
        return salePrice;
    }

    public String getId(){
        return id;
    }
    public String toString(){
        String string = "ID: " + id + " | Name: " + name + " | Sale Price: " + salePrice + " | Qty Avbl: " + qty;
        return string;
    }
    
}

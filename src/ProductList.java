import java.lang.*;
import java.io.*;
import java.util.*;

public class ProductList implements Serializable {
    private static final long serialVersionUID = 1L;
    private List products = new LinkedList();
    private static ProductList productList;

    private ProductList(){
    }

    public static ProductList instance(){
        if(productList == null){
            return(productList= new ProductList());
        }
        else{
            return productList;
        }
    }

    public boolean insertProduct(Product product){
        products.add(product);
        return true;
    }

    public Iterator getProducts(){
        return products.iterator();
    }

}

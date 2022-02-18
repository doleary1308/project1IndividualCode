import java.util.*;
import java.lang.*;
import java.io.*;

/*public class test{

    public static void main(String[] s){

        Client c1 = new Client("Dillon", " 235 Spooner St.", "123456");
        Client c2 = new Client("Alan", " 235 Sycamore St.", "23908");

        ClientList clients = ClientList.instance();
        clients.insertClient(c1);
        clients.insertClient(c2);

        Product p1 = new Product("3/16 screw","456" , "$0.12", "34590");
        ProductList products = ProductList.instance();
        products.insertProduct(p1);

        Client.addProductWishlist(p1, c1);
        
        System.out.println("New Client: " + c1.toString());
        System.out.println("New product: " + p1.toString());
        System.out.println("Client name: " + c1.getName());
        System.out.println("Client address" + c1.getAddress());
        System.out.println("Client id: " + c1.getId());

        Iterator client = clients.getClients();
        Iterator wishlist = Client.getWishlist(c1);
        Iterator product = products.getProducts();

        System.out.println("Products in productList");
        while(product.hasNext()){
            System.out.println(product.next());
        }

        System.out.println("Clients in clientList");
        while (client.hasNext()){
            System.out.println(client.next());
          }
          while (wishlist.hasNext()){
            System.out.println("product in wishlist: "+ wishlist.next());
        }

    }
}*/
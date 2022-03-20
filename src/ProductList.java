import java.util.*;
import java.lang.*;
import java.io.*;
public class ProductList implements Serializable {
  private static final long serialVersionUID = 1L;
  private List books = new LinkedList();
  private static ProductList productList;
  private ProductList() {
  }
  public static ProductList instance() {
    if (productList == null) {
      return (productList = new ProductList());
    } else {
      return productList;
    }
  }
  public Product search(String bookId) {
    for (Iterator iterator = books.iterator(); iterator.hasNext(); ) {
      Product product = (Product) iterator.next();
      if (product.getId().equals(bookId)) {
        return product;
      }
    }
    return null;
  }
  public boolean removeBook(String bookId) {
    Product product = search(bookId);
    if (product == null) {
      return false;
    } else {
      return books.remove(product);
    }
  }
  public boolean insertBook(Product product) {
    books.add(product);
    return true;
  }
  public Iterator getBooks() {
    return books.iterator();
  }
  private void writeObject(java.io.ObjectOutputStream output) {
    try {
      output.defaultWriteObject();
      output.writeObject(productList);
    } catch(IOException ioe) {
      System.out.println(ioe);
    }
  }
  private void readObject(java.io.ObjectInputStream input) {
    try {
      if (productList != null) {
        return;
      } else {
        input.defaultReadObject();
        if (productList == null) {
          productList = (ProductList) input.readObject();
        } else {
          input.readObject();
        }
      }
    } catch(IOException ioe) {
      System.out.println("in Catalog readObject \n" + ioe);
    } catch(ClassNotFoundException cnfe) {
      cnfe.printStackTrace();
    }
  }
  public String toString() {
    return books.toString();
  }
}

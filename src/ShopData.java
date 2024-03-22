import java.text.SimpleDateFormat;
import java.util.Date;

public class ShopData<T extends Comparable<T>> implements Comparable<ShopData<T>>, Node<ShopData<T>>{

    T key;
    String name;
    Date date;
    String productName;

    Node<ShopData<T>> next;
    public ShopData(T key,String name, Date date, String productName) {
        this.key = key;
        this.name = name;
        this.date = date;
        this.productName = productName;
    }



    public T getKey() {
        return key;
    }

    public void setKey(T key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String dateToText(){
        String[] temp = new SimpleDateFormat("dd-MM-yyyy").format(date).split("-");
        return temp[0]+"/"+temp[1]+"/"+(Integer.parseInt(temp[2])-1900);
    }
    @Override
    public String toString() {
        return  "ShopData{name='"+ name +
                "', date='" + dateToText()+
                "', productName='" + productName + '\'' +
                '}';
    }


    // Date'leri karşılaştırmak için kullanılan başka bir compareTo metodu
    public int compareTo(ShopData<T> other) {
        return this.date.compareTo(other.date);
    }

    @Override
    public ShopData<T> getData() {
        return null;
    }

    @Override
    public void setData(ShopData<T> data) {

    }

    @Override
    public Node<ShopData<T>> getNext() {
        return this.next;
    }

    @Override
    public void setNext(Node<ShopData<T>> next) {
    this.next = next;
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }


   /* @Override
    public int compareTo(ShopData<T> other) {
        return this.key.compareTo(other.key);
    }*/

   /* @Override
    public int compareTo(ShopData<T> other) {
        // Önce key'e göre karşılaştırma
        int keyComparison = this.key.compareTo(other.key);
        if (keyComparison != 0) {
            return keyComparison;
        }

        // Eğer key'ler eşitse, date'e göre karşılaştırma
        return this.date.compareTo(other.date);
    }*/
}

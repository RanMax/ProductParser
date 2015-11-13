import java.util.HashSet;

/**
 * Created by Максим on 01.11.2015.
 */
public class Product{
    String articul;
    String name;
    String count;
    String price;
    String manufacturer;
    String parentCategory;
    String category;

    HashSet<String> categories = new HashSet();

    public String toString(){
        //String str = new String();
        //for (String ctg : categories){
        //    str = str + ":"+ctg;
        //}
        //if (str.length() > 2) str = str.substring(1);
        return articul + ":" + name + ":" + count + ":" + price + ":" + manufacturer+"/"+parentCategory+"/"+category;
    }

    public void addCategory(String category){
        this.categories.add(category);
    }

    public String getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(String parentCategory) {
        this.parentCategory = parentCategory;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getArticul() {
        return articul;
    }

    public void setArticul(String articul) {
        this.articul = articul;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

}
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by Максим on 01.11.2015.
 */
public class Model {
    private static Model model;
    private Connection conn;

    public void saveBufferProducts(ArrayList<Product> products){
        String query = new String();
        Integer rowNum = 0;
        for (Product product: products){
            query = query + ",('" +
                    product.getArticul() + "','" +
                    product.getName() + "','" +
                    product.getManufacturer() + "','" +
                    product.getCategory() + "','" +
                    product.getParentCategory() + "','" +
                    product.getPrice() + "','" +
                    product.getCount() + "')";


            if (rowNum == 1000){
                rowNum = 0;
                query = query.substring(1);
                this.executeStatement("INSERT INTO buffer_products_java(articul,name,manufacturer,category,parent_category,price,value) values"+query);
                query = "";
            }
            rowNum++;
        }
        query = query.substring(1);
        this.executeStatement("INSERT INTO buffer_products_java(articul,name,manufacturer,category,parent_category,price,value) values"+query);

    }

    public void executeStatement(String statement){
        System.out.println(statement);
        try {
            Statement st = conn.createStatement();
            st.execute(statement);
        } catch (Exception ex){ex.printStackTrace();}
    }

    private Model(String host, String user, String passwd, String database){
        //Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://"+host+"/?user="+user+"&password="+passwd);
        } catch (Exception ex){ex.printStackTrace();}
        try {

            Statement st = conn.createStatement();

            st.execute("use " + database);
        }catch (Exception ex){ex.printStackTrace();}

    }

    public static Model getModel(String host, String user, String passwd, String db){
        if (model == null) model = new Model(host, user, passwd,db);
        return model;
    }
}

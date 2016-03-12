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
        query = "delete from src_products";
        this.executeStatement(query);
        query = "";
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
                this.executeStatement("INSERT INTO src_products(articul,name,manufacturer,category,parent_category,price,value) values"+query);
                query = "";
            }
            rowNum++;
        }
        query = query.substring(1);
        this.executeStatement("INSERT INTO src_products(articul,name,manufacturer,category,parent_category,price,value) values"+query);

        //Проставление производителя
        query = "update src_products sp join triplets4 t on t.value = sp.manufacturer and t.prop_id = 50154 set sp.manufacturer_id = t.subj_id";
        this.executeStatement(query);

        //Проставление нулевой цены
        query = "update src_products set price_int = 0 where trim(price) = 'звоните / узнавайте'";
        this.executeStatement(query);

        //Проставление не нулевых цен
        query = "update src_products set price_int = cast(price as SIGNED) where price_int is null and manufacturer_id is not null";
        this.executeStatement(query);
        //Проставление количества
        query = "update src_products set value_int = 0 where trim(value) = 'Нет'";
        this.executeStatement(query);
        query = "update src_products set value_int = -1 where trim(value) = 'Есть'";
        this.executeStatement(query);

        query = "delete from etl_products";
        this.executeStatement(query);

        //Загрузка продуктов в etl-таблицу
        query = "insert into etl_products(articul,show_name, manufacturer_id, provider_id,image,buy_cost,sell_cost,start_date,cnt)\n" +
                "select p.articul, max(p.name), max(p.manufacturer_id), 15161, null, max(p.price_int),null,'2015-11-01',min(p.value_int)\n" +
                "from src_products p where p.manufacturer_id is not null and p.articul != ''\n" +
                "group by p.articul";
        this.executeStatement(query);

        //Обнуление наценки и цены продажи
        query = "update products set add_cost = null";
        this.executeStatement(query);
        query = "update products set sell_cost = null";
        this.executeStatement(query);

        // Обновление закупочной цены
        query ="update products p\n" +
                "        join etl_products b on p.articul = b.articul\n" +
                "        set p.buy_cost = b.buy_cost";
        this.executeStatement(query);

        //Проставление наценки в общем случае
        query ="update products p\n" +
                "        join add_prices ap on ap.pruduct_category = 1525170 and p.buy_cost >= ap.start_price and p.buy_cost < ap.end_price\n" +
                "        set p.add_cost = ap.add_cost\n" +
                "        where p.buy_cost != 0";
        this.executeStatement(query);
        //Проставление наценки в частных случаях
        query = "update products p\n" +
                "  join triplets2 t on t.subj_id =p.id\n" +
                "  join triplets3 t3 on t3.subj_id = t.obj_id and t3.prop_id = 5061\n" +
                "  join product_categories pc on pc.id = t.obj_id\n" +
                "  join add_prices ap on ap.pruduct_category = t3.obj_id and p.buy_cost >= ap.start_price and p.buy_cost < ap.end_price\n" +
                "set p.add_cost = ap.add_cost\n" +
                "where p.buy_cost != 0";
        this.executeStatement(query);

        //Проставление цены продажи
        query = "update products set sell_cost = buy_cost+add_cost where buy_cost != 0";
        this.executeStatement(query);
        query = "update products p set sell_cost = 0 where sell_cost is null";
        this.executeStatement(query);
        query = "update products p set add_cost = 0 where add_cost is null";
        this.executeStatement(query);

        //Обновление цены в магазине
        query = "update sghyd_hikashop_price pp \n" +
                "  join sghyd_hikashop_product p on pp.price_product_id = p.product_id\n" +
                "  join products bp on p.base_id = bp.id\n" +
                "set pp.price_value = bp.sell_cost";
        this.executeStatement(query);


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


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;


/**
 * Created by Максим on 31.10.2015.
 */
public class ProductParser {
    public static void main(String[] args) {
        //downloadFiles("http://www.part33.ru/prices/index.php?word=&brand=all&t_type=all&z_group=all&search=%CF%EE%E8%F1%EA","e:/input2.html",100);

        ProductParser parser = new ProductParser();
        ArrayList<Product> result = parser.execute2();
        Model model = Model.getModel("localhost","root","","kb1");
        model.saveBufferProducts(result);
        //parser.save();

        //try {
        //    convert("e:/input2.html", "e:/input3.html", "cp1251", "utf8");
        //} catch (Exception ex){ex.printStackTrace();}

    }

    public ArrayList<Product> execute2(){
        ArrayList<Product> list = new ArrayList();
        HashSet<String> categories = new HashSet<>();
        HashSet<String> parentCategories = new HashSet<>();
        HashSet<String> manufacturers = new HashSet<>();
        try {
            File input = new File("e:/input2.html");
            Document doc = Jsoup.parse(input, "cp1251", "http://example.com/");
            Product currentProduct = new Product();
            for (Element table : doc.select("table[class=text abc]")) { //this will work if your doc contains only one table element
                for (Element row : table.select("tr")) {

                    Elements tds = row.select("td");
                    int size = tds.size();
                    if (size == 7) {
                        Product tmp = new Product();
                        currentProduct = tmp;
                        //String str = tds.get(0).text() + "-" + tds.get(1).text() + "-" + tds.get(2).text() + "-" + tds.get(3).text() + "-" + tds.get(4).text() + "-" + tds.get(5).text() + "-" + tds.get(6).text();
                        //System.out.println(str);
                        tmp.setArticul(tds.get(2).text());
                        tmp.setName(tds.get(3).text());
                        tmp.setPrice(tds.get(6).text());
                        tmp.setCount(tds.get(5).text());
                        list.add(tmp);
                    } else {
                        currentProduct.setManufacturer(tds.get(0).text());
                        currentProduct.setParentCategory(tds.get(1).text());
                        currentProduct.setCategory(tds.get(2).text());

                        parentCategories.add(tds.get(1).text());
                        categories.add(tds.get(2).text());
                        manufacturers.add(tds.get(0).text());
                        //System.out.println(tds.get(0).text() + "-" + tds.get(1).text() + "-" + tds.get(2).text());
                    }

                }
            }
        } catch(Exception ex){ex.printStackTrace();}

        for (Product product: list){
            System.out.println(product);
        }
        for (String category: categories){
            System.out.println(category);
        }

        for (String category: parentCategories){
            System.out.println(category);
        }

        for (String manufacturer: manufacturers){
            System.out.println(manufacturer);
        }
        return list;

    }


    public static void downloadFiles(String strURL, String strPath, int buffSize) {
        try {
            URL connection = new URL(strURL);
            HttpURLConnection urlconn;
            urlconn = (HttpURLConnection) connection.openConnection();
            urlconn.setRequestMethod("GET");
            urlconn.connect();
            InputStream in = null;
            in = urlconn.getInputStream();
            //InputStreamReader inp = new InputStreamReader(in);
            OutputStream writer = new FileOutputStream(strPath);

            OutputStreamWriter w = new OutputStreamWriter(writer,StandardCharsets.UTF_8);

            byte buffer[] = new byte[buffSize];
            byte bufferUtf[];
            //char buffer[] = new char[buffSize];
            int c = in.read(buffer);
            while (c > 0) {
                //String str = new String(buffer, "cp1251");
                //bufferUtf = str.getBytes("utf-8");
                writer.write(buffer, 0, c);
                c = in.read(buffer);
            }
            writer.flush();
            writer.close();
            in.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void convert(String infile, String outfile, String from, String to)
            throws IOException, UnsupportedEncodingException
    {
        // set up byte streams
        InputStream in;
        if(infile != null)
            in=new FileInputStream(infile);
        else
            in=System.in;
        OutputStream out;
        if(outfile != null)
            out=new FileOutputStream(outfile);
        else
            out=System.out;

        // Use default encoding if no encoding is specified.
        if(from == null) from=System.getProperty("file.encoding");
        if(to == null) to=System.getProperty("file.encoding");

        // Set up character stream
        Reader r=new BufferedReader(new InputStreamReader(in, from));
        Writer w=new BufferedWriter(new OutputStreamWriter(out, to));

        // Copy characters from input to output.  The InputStreamReader
        // converts from the input encoding to Unicode,, and the OutputStreamWriter
        // converts from Unicode to the output encoding.  Characters that cannot be
        // represented in the output encoding are output as '?'
        char[] buffer=new char[4096];
        int len;
        while((len=r.read(buffer)) != -1)
            w.write(buffer, 0, len);
        r.close();
        w.flush();
        w.close();
    }


}



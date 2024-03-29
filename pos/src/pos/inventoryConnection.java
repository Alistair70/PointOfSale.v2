package pos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;


public class inventoryConnection
{
    public static Connection getConnection() throws Exception{                  // Connects to the database, in this case on local machine 
        try
        {
            String driver = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/javadatabase";
            String username = "root";
            String password = "root";
            Class.forName(driver);
            Connection conn = DriverManager.getConnection(url,username,password);
            System.out.println("Connected");
            return conn;
        } catch(ClassNotFoundException | SQLException e){System.out.println(e);}
        return null;
    }
    
        public static String[] getProd(String id)                               // Accesses the databse and obtains the prodcut information of the item with the
        {                                                                       // given barcode.
        String[] prod = new String[4];
            try
            {
                Connection con = getConnection();
                PreparedStatement query = con.prepareStatement("Select * from "
                        + "productInventory where product_id = "+ id +"");
                ResultSet res = query.executeQuery();

                while(res.next())
                {
                    prod[0] = res.getString("product_id"); 
                    prod[1] = res.getString("product_Name"); 
                    prod[2] = res.getString("product_categeory");
                    prod[3] = Integer.toString(res.getInt("product_Price"));
                }        
                con.close();            
            }catch(Exception e){System.out.println(e);}
        return prod;
        }
        
        public static void executeQuery(String q)                               // Executes the query to the databse.
        {
            try
            {
                Connection con = getConnection();
                PreparedStatement query = con.prepareStatement(q);
                query.executeUpdate();
            }catch(Exception e){System.out.println(e);}
        finally{System.out.println("Query execution complete");}
        } 

        public static String[] getCols()                                        // Returns all the unique categories of which all products fall under.
        {   
            int colCount = 0;
            ResultSetMetaData md = null;
            try
            {
                Connection con = getConnection();
                PreparedStatement query = con.prepareStatement("Select * from salesRecord");
                ResultSet res = query.executeQuery();
                md = res.getMetaData();
                colCount = md.getColumnCount();
               

            }catch(Exception e){System.out.println(e);}

            String[] cols = new String[colCount];
            
            cols[0] = "";

            for(int i = 1; i < colCount; i++)
            {
                try
                {  
                    cols[i] = md.getColumnName(i+1);
                }catch(Exception e){System.out.println(e);}
            }    
            return cols;
        }
}
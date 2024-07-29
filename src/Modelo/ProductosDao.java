
package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.OutputStream;
/**/
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**/


public class ProductosDao {
    Connection con;
    Conexion cn = new Conexion();
    PreparedStatement ps;
    ResultSet rs;
    
    private static final String GET_URL = "http://44.203.31.118:8080/productos";
    
    
    
    public boolean RegistrarProductos(Productos pro){    	
        // Reemplaza con tus propias credenciales y URL de la cola
        String accessKey = "";
        String secretKey = "";
        String queueUrl = "https://sqs.us-east-1.amazonaws.com/010438488420/windows_to_inventory_queue_name";
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);

        // Creación del cliente SQS
        AmazonSQS sqs = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion("us-east-1") // Reemplaza con tu región
                .build();

        /*******************************************/
        ObjectMapper objectMapper = new ObjectMapper();
        
        try {
            // Convertir el objeto a JSON
            String jsonString = objectMapper.writeValueAsString(pro);

            // Imprimir el JSON resultante
            System.out.println(jsonString);
            
            SendMessageRequest send_msg_request = new SendMessageRequest()
                    .withQueueUrl(queueUrl)
                    .withMessageBody(jsonString)
                    .withDelaySeconds(5);        

            // Envío del mensaje
            sqs.sendMessage(send_msg_request);

            System.out.println("Mensaje enviado correctamente");
            
            
        } catch (JsonProcessingException e) {
            /// e.printStackTrace();
        }
        /*******************************************/

    	return true;
    }
    
    public boolean RegistrarProductosLegado(Productos pro){
        String sql = "INSERT INTO productos (codigo, nombre, proveedor, stock, precio) VALUES (?,?,?,?,?)";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, pro.getCodigo());
            ps.setString(2, pro.getNombre());
            ps.setInt(3, pro.getProveedor());
            ps.setInt(4, pro.getStock());
            ps.setDouble(5, pro.getPrecio());
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        }    	
    }
    
    public List ListarProductos() {
    	List<Productos> Listapro = new ArrayList();

    	try {
            // Create a URL object with the endpoint URL
            URL url = new URL(GET_URL);
            
            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            // Set the request method to GET
            connection.setRequestMethod("GET");
            
            // Set request headers if needed
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            // Get the response code
            int responseCode = connection.getResponseCode();

            // Read the response if the response code is HTTP_OK (200)
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                
                String jsonString = response.toString();
                JSONArray jsonArray = new JSONArray(jsonString);

                // Recorrer el array y extraer elementos de cada JSONObject
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int id = jsonObject.getInt("id");
                    String codigo = jsonObject.getString("codigo");
                    String nombre = jsonObject.getString("nombre");
                    double precio = jsonObject.getDouble("precio");
                    int stock = jsonObject.getInt("stock");
                    int id_proveedor = jsonObject.getInt("id_proveedor");
                    String nombre_proveedor = jsonObject.getString("nombre_proveedor");

                    Productos pro = new Productos();
                    pro.setId(id);
                    pro.setCodigo(codigo);
                    pro.setNombre(nombre);
                    pro.setProveedor(id_proveedor);
                    pro.setProveedorPro(nombre_proveedor);
                    pro.setStock(stock);
                    pro.setPrecio(precio);
                    Listapro.add(pro);
                }
            } else {
                System.out.println("GET request not worked");
            }            
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return Listapro;
    } 
    
    public List ListarProductosLegado(){    	
       List<Productos> Listapro = new ArrayList();
       String sql = "SELECT pr.id AS id_proveedor, pr.nombre AS nombre_proveedor, p.* FROM proveedor pr INNER JOIN productos p ON pr.id = p.proveedor ORDER BY p.id DESC";
       try {
           con = cn.getConnection();
           ps = con.prepareStatement(sql);
           rs = ps.executeQuery();
           while (rs.next()) {               
               Productos pro = new Productos();
               pro.setId(rs.getInt("id"));
               pro.setCodigo(rs.getString("codigo"));
               pro.setNombre(rs.getString("nombre"));
               pro.setProveedor(rs.getInt("id_proveedor"));
               pro.setProveedorPro(rs.getString("nombre_proveedor"));
               pro.setStock(rs.getInt("stock"));
               pro.setPrecio(rs.getDouble("precio"));
               Listapro.add(pro);
           }
       } catch (SQLException e) {
           System.out.println(e.toString());
       }
       return Listapro;
   }
    
    public boolean EliminarProductos(int id){
       String sql = "DELETE FROM productos WHERE id = ?";
       try {
           ps = con.prepareStatement(sql);
           ps.setInt(1, id);
           ps.execute();
           return true;
       } catch (SQLException e) {
           System.out.println(e.toString());
           return false;
       }finally{
           try {
               con.close();
           } catch (SQLException ex) {
               System.out.println(ex.toString());
           }
       }
   }
    
    public boolean ModificarProductos(Productos pro){
       String sql = "UPDATE productos SET codigo=?, nombre=?, proveedor=?, stock=?, precio=? WHERE id=?";
       try {
           ps = con.prepareStatement(sql);
           ps.setString(1, pro.getCodigo());
           ps.setString(2, pro.getNombre());
           ps.setInt(3, pro.getProveedor());
           ps.setInt(4, pro.getStock());
           ps.setDouble(5, pro.getPrecio());
           ps.setInt(6, pro.getId());
           ps.execute();
           return true;
       } catch (SQLException e) {
           System.out.println(e.toString());
           return false;
       }finally{
           try {
               con.close();
           } catch (SQLException e) {
               System.out.println(e.toString());
           }
       }
   }
    
    public Productos BuscarPro(String cod){
        Productos producto = new Productos();
        String sql = "SELECT * FROM productos WHERE codigo = ?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, cod);
            rs = ps.executeQuery();
            if (rs.next()) {
                producto.setId(rs.getInt("id"));
                producto.setNombre(rs.getString("nombre"));
                producto.setPrecio(rs.getDouble("precio"));
                producto.setStock(rs.getInt("stock"));
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return producto;
    }
    public Productos BuscarId(int id){
        Productos pro = new Productos();
        String sql = "SELECT pr.id AS id_proveedor, pr.nombre AS nombre_proveedor, p.* FROM proveedor pr INNER JOIN productos p ON p.proveedor = pr.id WHERE p.id = ?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                pro.setId(rs.getInt("id"));
                pro.setCodigo(rs.getString("codigo"));
                pro.setNombre(rs.getString("nombre"));
                pro.setProveedor(rs.getInt("proveedor"));
                pro.setProveedorPro(rs.getString("nombre_proveedor"));
                pro.setStock(rs.getInt("stock"));
                pro.setPrecio(rs.getDouble("precio"));
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return pro;
    }
    public Proveedor BuscarProveedor(String nombre){
        Proveedor pr = new Proveedor();
        String sql = "SELECT * FROM proveedor WHERE nombre = ?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, nombre);
            rs = ps.executeQuery();
            if (rs.next()) {
                pr.setId(rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return pr;
    }
    public Config BuscarDatos(){
        Config conf = new Config();
        String sql = "SELECT * FROM config";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                conf.setId(rs.getInt("id"));
                conf.setRuc(rs.getString("ruc"));
                conf.setNombre(rs.getString("nombre"));
                conf.setTelefono(rs.getString("telefono"));
                conf.setDireccion(rs.getString("direccion"));
                conf.setMensaje(rs.getString("mensaje"));
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return conf;
    }
    
    public boolean ModificarDatos(Config conf){
       String sql = "UPDATE config SET ruc=?, nombre=?, telefono=?, direccion=?, mensaje=? WHERE id=?";
       try {
           ps = con.prepareStatement(sql);
           ps.setString(1, conf.getRuc());
           ps.setString(2, conf.getNombre());
           ps.setString(3, conf.getTelefono());
           ps.setString(4, conf.getDireccion());
           ps.setString(5, conf.getMensaje());
           ps.setInt(6, conf.getId());
           ps.execute();
           return true;
       } catch (SQLException e) {
           System.out.println(e.toString());
           return false;
       }finally{
           try {
               con.close();
           } catch (SQLException e) {
               System.out.println(e.toString());
           }
       }
   }
}

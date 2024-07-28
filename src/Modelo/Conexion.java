package Modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    Connection con;

    public Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");

            String myBD = "jdbc:postgresql://misodb.cd2u2qma0z9z.us-east-1.rds.amazonaws.com:5432/misodb";
            String usuario = "misoAdmin";
            String contraseña = "admin1234";
            
            con = DriverManager.getConnection(myBD, usuario, contraseña);
            return con;
        } catch (SQLException e) {
            System.out.println(e.toString());
        } catch (ClassNotFoundException e) {
            System.out.println("Driver de PostgreSQL no encontrado.");
            e.printStackTrace();
        }
        return null;
    }

}
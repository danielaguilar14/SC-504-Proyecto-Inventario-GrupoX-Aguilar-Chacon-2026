/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.tiquiciatech;

import cr.ac.tiquiciatech.config.ConexionOracle;
import java.sql.Connection;

public class TestConexion {

    public static void main(String[] args) {
        try (Connection conn = ConexionOracle.getConnection()) {
            System.out.println("==================================");
            System.out.println("Conexion exitosa con Oracle");
            System.out.println("Base de datos: " + conn.getMetaData().getDatabaseProductName());
            System.out.println("URL: " + conn.getMetaData().getURL());
            System.out.println("==================================");
        } catch (Exception e) {
            System.out.println("Error al conectar con Oracle:");
            e.printStackTrace();
        }
    }
}

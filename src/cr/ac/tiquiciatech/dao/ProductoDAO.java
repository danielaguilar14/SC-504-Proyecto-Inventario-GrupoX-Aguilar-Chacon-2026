package cr.ac.tiquiciatech.dao;

import cr.ac.tiquiciatech.config.ConexionOracle;
import cr.ac.tiquiciatech.model.Producto;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import oracle.jdbc.OracleTypes;

public class ProductoDAO {

    public void insertarProducto(Producto producto) {
        String sql = "{ call sp_insertar_producto(?, ?, ?, ?, ?, ?, ?) }";

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, producto.getIdCategoria());
            cs.setInt(2, producto.getIdProveedor());
            cs.setString(3, producto.getSku());
            cs.setString(4, producto.getNombre());
            cs.setString(5, producto.getDescripcion());
            cs.setDouble(6, producto.getPrecio());
            cs.setString(7, producto.getEstado());

            cs.execute();
            conn.commit();

            System.out.println("Producto insertado correctamente.");

        } catch (Exception e) {
            System.out.println("Error al insertar producto:");
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    public List<Producto> listarProductos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "{ call sp_listar_productos(?) }";

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    Producto producto = new Producto();
                    producto.setIdProducto(rs.getInt("id_producto"));
                    producto.setIdCategoria(rs.getInt("id_categoria"));
                    producto.setIdProveedor(rs.getInt("id_proveedor"));
                    producto.setSku(rs.getString("sku"));
                    producto.setNombre(rs.getString("nombre"));
                    producto.setDescripcion(rs.getString("descripcion"));
                    producto.setPrecio(rs.getDouble("precio"));
                    producto.setEstado(rs.getString("estado"));
                    producto.setFechaCreacion(rs.getDate("fecha_creacion"));
                    lista.add(producto);
                }
            }

        } catch (Exception e) {
            System.out.println("Error al listar productos:");
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }

        return lista;
    }

    public void actualizarProducto(Producto producto) {
        String sql = "{ call sp_actualizar_producto(?, ?, ?, ?, ?, ?, ?, ?) }";

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, producto.getIdProducto());
            cs.setInt(2, producto.getIdCategoria());
            cs.setInt(3, producto.getIdProveedor());
            cs.setString(4, producto.getSku());
            cs.setString(5, producto.getNombre());
            cs.setString(6, producto.getDescripcion());
            cs.setDouble(7, producto.getPrecio());
            cs.setString(8, producto.getEstado());

            cs.execute();
            conn.commit();

            System.out.println("Producto actualizado correctamente.");

        } catch (Exception e) {
            System.out.println("Error al actualizar producto:");
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    public void eliminarProducto(int idProducto) {
        String sql = "{ call sp_eliminar_producto(?) }";

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, idProducto);
            cs.execute();
            conn.commit();

            System.out.println("Producto eliminado correctamente.");

        } catch (Exception e) {
            System.out.println("Error al eliminar producto:");
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }
}
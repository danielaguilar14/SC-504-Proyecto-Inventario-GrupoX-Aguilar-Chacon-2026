package cr.ac.tiquiciatech.dao;

import cr.ac.tiquiciatech.config.ConexionOracle;
import cr.ac.tiquiciatech.model.Inventario;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import oracle.jdbc.OracleTypes;

public class InventarioDAO {

    public void insertarInventario(Inventario inv) {
        String sql = "{ call sp_insertar_inventario(?, ?, ?, ?) }";

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, inv.getIdProducto());
            cs.setInt(2, inv.getStockActual());
            cs.setInt(3, inv.getStockMinimo());
            cs.setObject(4, inv.getStockMaximo());

            cs.execute();
            conn.commit();

            System.out.println("Inventario insertado correctamente.");

        } catch (Exception e) {
            System.out.println("Error al insertar inventario:");
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    public List<Inventario> listarInventario() {
        List<Inventario> lista = new ArrayList<>();
        String sql = "{ call sp_listar_inventario(?) }";

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    Inventario inv = new Inventario();
                    inv.setIdInventario(rs.getInt("id_inventario"));
                    inv.setIdProducto(rs.getInt("id_producto"));
                    inv.setStockActual(rs.getInt("stock_actual"));
                    inv.setStockMinimo(rs.getInt("stock_minimo"));
                    inv.setStockMaximo((Integer) rs.getObject("stock_maximo"));
                    lista.add(inv);
                }
            }

        } catch (Exception e) {
            System.out.println("Error al listar inventario:");
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }

        return lista;
    }

    public void actualizarInventario(Inventario inv) {
        String sql = "{ call sp_actualizar_inventario(?, ?, ?, ?) }";

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, inv.getIdInventario());
            cs.setInt(2, inv.getStockActual());
            cs.setInt(3, inv.getStockMinimo());
            cs.setObject(4, inv.getStockMaximo());

            cs.execute();
            conn.commit();

            System.out.println("Inventario actualizado correctamente.");

        } catch (Exception e) {
            System.out.println("Error al actualizar inventario:");
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    public void eliminarInventario(int idInventario) {
        String sql = "{ call sp_eliminar_inventario(?) }";

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, idInventario);
            cs.execute();
            conn.commit();

            System.out.println("Inventario eliminado correctamente.");

        } catch (Exception e) {
            System.out.println("Error al eliminar inventario:");
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }
}
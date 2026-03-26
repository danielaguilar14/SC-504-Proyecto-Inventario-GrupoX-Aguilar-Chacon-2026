package cr.ac.tiquiciatech.dao;

import cr.ac.tiquiciatech.config.ConexionOracle;
import cr.ac.tiquiciatech.model.Proveedor;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes;

public class ProveedorDAO {

    public void insertarProveedor(Proveedor proveedor) {
        String sql = "{ call sp_insertar_proveedor(?, ?, ?, ?, ?) }";

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, proveedor.getNombre());
            cs.setString(2, proveedor.getTelefono());
            cs.setString(3, proveedor.getEmail());
            cs.setString(4, proveedor.getDireccion());
            cs.setString(5, proveedor.getEstado());

            cs.execute();
            conn.commit();

            System.out.println("Proveedor insertado correctamente.");

        } catch (Exception e) {
            System.out.println("Error al insertar proveedor:");
            e.printStackTrace();
        }
    }

    public List<Proveedor> listarProveedores() {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "{ call sp_listar_proveedores(?) }";

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    Proveedor proveedor = new Proveedor();
                    proveedor.setIdProveedor(rs.getInt("id_proveedor"));
                    proveedor.setNombre(rs.getString("nombre"));
                    proveedor.setTelefono(rs.getString("telefono"));
                    proveedor.setEmail(rs.getString("email"));
                    proveedor.setDireccion(rs.getString("direccion"));
                    proveedor.setEstado(rs.getString("estado"));
                    lista.add(proveedor);
                }
            }

        } catch (Exception e) {
            System.out.println("Error al listar proveedores:");
            e.printStackTrace();
        }

        return lista;
    }

    public void actualizarProveedor(Proveedor proveedor) {
        String sql = "{ call sp_actualizar_proveedor(?, ?, ?, ?, ?, ?) }";

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, proveedor.getIdProveedor());
            cs.setString(2, proveedor.getNombre());
            cs.setString(3, proveedor.getTelefono());
            cs.setString(4, proveedor.getEmail());
            cs.setString(5, proveedor.getDireccion());
            cs.setString(6, proveedor.getEstado());

            cs.execute();
            conn.commit();

            System.out.println("Proveedor actualizado correctamente.");

        } catch (Exception e) {
            System.out.println("Error al actualizar proveedor:");
            e.printStackTrace();
        }
    }

    public void eliminarProveedor(int idProveedor) {
        String sql = "{ call sp_eliminar_proveedor(?) }";

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, idProveedor);
            cs.execute();
            conn.commit();

            System.out.println("Proveedor eliminado correctamente.");

        } catch (Exception e) {
            System.out.println("Error al eliminar proveedor:");
            e.printStackTrace();
        }
    }
}
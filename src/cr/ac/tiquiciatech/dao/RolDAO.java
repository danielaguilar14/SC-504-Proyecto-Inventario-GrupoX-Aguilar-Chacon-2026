package cr.ac.tiquiciatech.dao;

import cr.ac.tiquiciatech.config.ConexionOracle;
import cr.ac.tiquiciatech.model.Rol;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import oracle.jdbc.OracleTypes;

public class RolDAO {

    public void insertarRol(Rol rol) {
        String sql = "{ call sp_insertar_rol(?) }";

        try (Connection conn = ConexionOracle.getConnection(); CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, rol.getNombre());
            cs.execute();
            conn.commit();

            System.out.println("Rol insertado correctamente.");

        } catch (Exception e) {
            System.out.println("Error al insertar rol:");
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    public List<Rol> listarRoles() {
        List<Rol> lista = new ArrayList<>();
        String sql = "{ call sp_listar_roles(?) }";
        try (Connection conn = ConexionOracle.getConnection(); CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    Rol rol = new Rol();
                    rol.setIdRol(rs.getInt("id_rol"));
                    rol.setNombre(rs.getString("nombre"));
                    lista.add(rol);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error completo: " + e.getClass().getName() + "\n" + e.getMessage());
        }
        return lista;
    }

    public void actualizarRol(Rol rol) {
        String sql = "{ call sp_actualizar_rol(?, ?) }";

        try (Connection conn = ConexionOracle.getConnection(); CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, rol.getIdRol());
            cs.setString(2, rol.getNombre());
            cs.execute();
            conn.commit();

            System.out.println("Rol actualizado correctamente.");

        } catch (Exception e) {
            System.out.println("Error al actualizar rol:");
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    public void eliminarRol(int idRol) {
        String sql = "{ call sp_eliminar_rol(?) }";

        try (Connection conn = ConexionOracle.getConnection(); CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, idRol);
            cs.execute();
            conn.commit();

            System.out.println("Rol eliminado correctamente.");

        } catch (Exception e) {
            System.out.println("Error al eliminar rol:");
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }
}

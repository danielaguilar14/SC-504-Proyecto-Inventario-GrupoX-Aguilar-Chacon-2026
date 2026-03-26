package cr.ac.tiquiciatech.dao;

import cr.ac.tiquiciatech.config.ConexionOracle;
import cr.ac.tiquiciatech.model.Usuario;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes;

public class UsuarioDAO {

    public void insertarUsuario(Usuario usuario) {
        String sql = "{ call sp_insertar_usuario(?, ?, ?, ?, ?) }";

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, usuario.getIdRol());
            cs.setString(2, usuario.getNombre());
            cs.setString(3, usuario.getEmail());
            cs.setString(4, usuario.getPasswordHash());
            cs.setString(5, usuario.getEstado());

            cs.execute();
            conn.commit();

            System.out.println("Usuario insertado correctamente.");

        } catch (Exception e) {
            System.out.println("Error al insertar usuario:");
            e.printStackTrace();
        }
    }

    public List<Usuario> listarUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "{ call sp_listar_usuarios(?) }";

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setIdRol(rs.getInt("id_rol"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setPasswordHash(rs.getString("password_hash"));
                    usuario.setEstado(rs.getString("estado"));
                    usuario.setFechaCreacion(rs.getDate("fecha_creacion"));
                    lista.add(usuario);
                }
            }

        } catch (Exception e) {
            System.out.println("Error al listar usuarios:");
            e.printStackTrace();
        }

        return lista;
    }

    public void actualizarUsuario(Usuario usuario) {
        String sql = "{ call sp_actualizar_usuario(?, ?, ?, ?, ?, ?) }";

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, usuario.getIdUsuario());
            cs.setInt(2, usuario.getIdRol());
            cs.setString(3, usuario.getNombre());
            cs.setString(4, usuario.getEmail());
            cs.setString(5, usuario.getPasswordHash());
            cs.setString(6, usuario.getEstado());

            cs.execute();
            conn.commit();

            System.out.println("Usuario actualizado correctamente.");

        } catch (Exception e) {
            System.out.println("Error al actualizar usuario:");
            e.printStackTrace();
        }
    }

    public void eliminarUsuario(int idUsuario) {
        String sql = "{ call sp_eliminar_usuario(?) }";

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, idUsuario);
            cs.execute();
            conn.commit();

            System.out.println("Usuario eliminado correctamente.");

        } catch (Exception e) {
            System.out.println("Error al eliminar usuario:");
            e.printStackTrace();
        }
    }
}
package cr.ac.tiquiciatech.dao;

import cr.ac.tiquiciatech.config.ConexionOracle;
import cr.ac.tiquiciatech.model.Categoria;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes;

public class CategoriaDAO {

    public void insertarCategoria(Categoria categoria) {
        String sql = "{ call sp_insertar_categoria(?, ?, ?) }";

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, categoria.getNombre());
            cs.setString(2, categoria.getDescripcion());
            cs.setString(3, categoria.getEstado());

            cs.execute();
            conn.commit();

            System.out.println("Categoría insertada correctamente.");

        } catch (Exception e) {
            System.out.println("Error al insertar categoría:");
            e.printStackTrace();
        }
    }

    public List<Categoria> listarCategorias() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "{ call sp_listar_categorias(?) }";

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    Categoria categoria = new Categoria();
                    categoria.setIdCategoria(rs.getInt("id_categoria"));
                    categoria.setNombre(rs.getString("nombre"));
                    categoria.setDescripcion(rs.getString("descripcion"));
                    categoria.setEstado(rs.getString("estado"));
                    lista.add(categoria);
                }
            }

        } catch (Exception e) {
            System.out.println("Error al listar categorías:");
            e.printStackTrace();
        }

        return lista;
    }

    public void actualizarCategoria(Categoria categoria) {
        String sql = "{ call sp_actualizar_categoria(?, ?, ?, ?) }";

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, categoria.getIdCategoria());
            cs.setString(2, categoria.getNombre());
            cs.setString(3, categoria.getDescripcion());
            cs.setString(4, categoria.getEstado());

            cs.execute();
            conn.commit();

            System.out.println("Categoría actualizada correctamente.");

        } catch (Exception e) {
            System.out.println("Error al actualizar categoría:");
            e.printStackTrace();
        }
    }

    public void eliminarCategoria(int idCategoria) {
        String sql = "{ call sp_eliminar_categoria(?) }";

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, idCategoria);
            cs.execute();
            conn.commit();

            System.out.println("Categoría eliminada correctamente.");

        } catch (Exception e) {
            System.out.println("Error al eliminar categoría:");
            e.printStackTrace();
        }
    }
}
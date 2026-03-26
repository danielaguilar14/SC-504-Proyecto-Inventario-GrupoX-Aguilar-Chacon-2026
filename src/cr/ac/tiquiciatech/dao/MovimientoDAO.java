package cr.ac.tiquiciatech.dao;

import cr.ac.tiquiciatech.config.ConexionOracle;
import cr.ac.tiquiciatech.model.Movimiento;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes;

public class MovimientoDAO {

    public void insertarMovimiento(Movimiento movimiento) {
        String sql = "{ call sp_insertar_movimiento(?, ?, ?) }";

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, movimiento.getIdUsuario());
            cs.setString(2, movimiento.getTipo());
            cs.setString(3, movimiento.getObservacion());

            cs.execute();
            conn.commit();

            System.out.println("Movimiento insertado correctamente.");

        } catch (Exception e) {
            System.out.println("Error al insertar movimiento:");
            e.printStackTrace();
        }
    }

    public List<Movimiento> listarMovimientos() {
        List<Movimiento> lista = new ArrayList<>();
        String sql = "{ call sp_listar_movimientos(?) }";

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    Movimiento movimiento = new Movimiento();
                    movimiento.setIdMovimiento(rs.getInt("id_movimiento"));
                    movimiento.setIdUsuario(rs.getInt("id_usuario"));
                    movimiento.setTipo(rs.getString("tipo"));
                    movimiento.setFecha(rs.getDate("fecha"));
                    movimiento.setObservacion(rs.getString("observacion"));
                    lista.add(movimiento);
                }
            }

        } catch (Exception e) {
            System.out.println("Error al listar movimientos:");
            e.printStackTrace();
        }

        return lista;
    }

    public void actualizarMovimiento(Movimiento movimiento) {
        String sql = "{ call sp_actualizar_movimiento(?, ?, ?, ?) }";

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, movimiento.getIdMovimiento());
            cs.setInt(2, movimiento.getIdUsuario());
            cs.setString(3, movimiento.getTipo());
            cs.setString(4, movimiento.getObservacion());

            cs.execute();
            conn.commit();

            System.out.println("Movimiento actualizado correctamente.");

        } catch (Exception e) {
            System.out.println("Error al actualizar movimiento:");
            e.printStackTrace();
        }
    }

    public void eliminarMovimiento(int idMovimiento) {
        String sql = "{ call sp_eliminar_movimiento(?) }";

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, idMovimiento);
            cs.execute();
            conn.commit();

            System.out.println("Movimiento eliminado correctamente.");

        } catch (Exception e) {
            System.out.println("Error al eliminar movimiento:");
            e.printStackTrace();
        }
    }
}
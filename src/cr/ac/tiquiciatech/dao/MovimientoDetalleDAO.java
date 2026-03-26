package cr.ac.tiquiciatech.dao;

import cr.ac.tiquiciatech.config.ConexionOracle;
import cr.ac.tiquiciatech.model.MovimientoDetalle;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import oracle.jdbc.OracleTypes;

public class MovimientoDetalleDAO {

    public void insertarMovimientoDetalle(MovimientoDetalle detalle) {
        String sql = "{ call sp_insertar_movimiento_detalle(?, ?, ?, ?) }";

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, detalle.getIdMovimiento());
            cs.setInt(2, detalle.getIdProducto());
            cs.setInt(3, detalle.getCantidad());
            cs.setDouble(4, detalle.getCostoUnitario());

            cs.execute();
            conn.commit();

            System.out.println("Detalle de movimiento insertado correctamente.");

        } catch (Exception e) {
            System.out.println("Error al insertar detalle de movimiento:");
            e.printStackTrace();
        }
    }

    public List<MovimientoDetalle> listarMovimientoDetalle() {
        List<MovimientoDetalle> lista = new ArrayList<>();
        String sql = "{ call sp_listar_movimiento_detalle(?) }";

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    MovimientoDetalle detalle = new MovimientoDetalle();
                    detalle.setIdDetalle(rs.getInt("id_detalle"));
                    detalle.setIdMovimiento(rs.getInt("id_movimiento"));
                    detalle.setIdProducto(rs.getInt("id_producto"));
                    detalle.setCantidad(rs.getInt("cantidad"));
                    detalle.setCostoUnitario(rs.getDouble("costo_unitario"));
                    lista.add(detalle);
                }
            }

        } catch (Exception e) {
            System.out.println("Error al listar detalle de movimientos:");
            e.printStackTrace();
        }

        return lista;
    }

    public void actualizarMovimientoDetalle(MovimientoDetalle detalle) {
        String sql = "{ call sp_actualizar_movimiento_detalle(?, ?, ?, ?, ?) }";

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, detalle.getIdDetalle());
            cs.setInt(2, detalle.getIdMovimiento());
            cs.setInt(3, detalle.getIdProducto());
            cs.setInt(4, detalle.getCantidad());
            cs.setDouble(5, detalle.getCostoUnitario());

            cs.execute();
            conn.commit();

            System.out.println("Detalle de movimiento actualizado correctamente.");

        } catch (Exception e) {
            System.out.println("Error al actualizar detalle de movimiento:");
            e.printStackTrace();
        }
    }

    public void eliminarMovimientoDetalle(int idDetalle) {
        String sql = "{ call sp_eliminar_movimiento_detalle(?) }";

        try (Connection conn = ConexionOracle.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, idDetalle);
            cs.execute();
            conn.commit();

            System.out.println("Detalle de movimiento eliminado correctamente.");

        } catch (Exception e) {
            System.out.println("Error al eliminar detalle de movimiento:");
            e.printStackTrace();
        }
    }
}
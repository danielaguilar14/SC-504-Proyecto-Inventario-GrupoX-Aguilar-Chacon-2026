package cr.ac.tiquiciatech.model;

import java.sql.Date;

public class Movimiento {
    private int idMovimiento;
    private int idUsuario;
    private String tipo;
    private Date fecha;
    private String observacion;

    public Movimiento() {
    }

    public Movimiento(int idMovimiento, int idUsuario, String tipo, Date fecha, String observacion) {
        this.idMovimiento = idMovimiento;
        this.idUsuario = idUsuario;
        this.tipo = tipo;
        this.fecha = fecha;
        this.observacion = observacion;
    }

    public int getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    @Override
    public String toString() {
        return "Movimiento{" +
                "idMovimiento=" + idMovimiento +
                ", idUsuario=" + idUsuario +
                ", tipo='" + tipo + '\'' +
                ", fecha=" + fecha +
                ", observacion='" + observacion + '\'' +
                '}';
    }
}
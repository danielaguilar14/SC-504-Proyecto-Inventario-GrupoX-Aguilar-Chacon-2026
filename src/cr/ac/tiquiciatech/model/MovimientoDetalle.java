package cr.ac.tiquiciatech.model;

public class MovimientoDetalle {
    private int idDetalle;
    private int idMovimiento;
    private int idProducto;
    private int cantidad;
    private double costoUnitario;

    public MovimientoDetalle() {
    }

    public MovimientoDetalle(int idDetalle, int idMovimiento, int idProducto, int cantidad, double costoUnitario) {
        this.idDetalle = idDetalle;
        this.idMovimiento = idMovimiento;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.costoUnitario = costoUnitario;
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public int getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getCostoUnitario() {
        return costoUnitario;
    }

    public void setCostoUnitario(double costoUnitario) {
        this.costoUnitario = costoUnitario;
    }

    @Override
    public String toString() {
        return "MovimientoDetalle{" +
                "idDetalle=" + idDetalle +
                ", idMovimiento=" + idMovimiento +
                ", idProducto=" + idProducto +
                ", cantidad=" + cantidad +
                ", costoUnitario=" + costoUnitario +
                '}';
    }
}
package cr.ac.tiquiciatech.model;

public class Inventario {

    private int idInventario;
    private int idProducto;
    private int stockActual;
    private int stockMinimo;
    private Integer stockMaximo;

    public Inventario() {
    }

    public Inventario(int idInventario, int idProducto, int stockActual, int stockMinimo, Integer stockMaximo) {
        this.idInventario = idInventario;
        this.idProducto = idProducto;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.stockMaximo = stockMaximo;
    }

    public int getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public Integer getStockMaximo() {
        return stockMaximo;
    }

    public void setStockMaximo(Integer stockMaximo) {
        this.stockMaximo = stockMaximo;
    }

    @Override
    public String toString() {
        return "Inventario{" +
                "idInventario=" + idInventario +
                ", idProducto=" + idProducto +
                ", stockActual=" + stockActual +
                ", stockMinimo=" + stockMinimo +
                ", stockMaximo=" + stockMaximo +
                '}';
    }
}
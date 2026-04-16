package cr.ac.tiquiciatech.ui;

import cr.ac.tiquiciatech.dao.ProductoDAO;
import cr.ac.tiquiciatech.model.Producto;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Panel CRUD para la tabla PRODUCTO.
 */
public class ProductoPanel extends JPanel {

    private ProductoDAO productoDAO = new ProductoDAO();
    private DefaultTableModel tableModel;
    private JTable tabla;
    private JTextField txtIdCategoria, txtIdProveedor, txtSku, txtNombre, txtDescripcion, txtPrecio;
    private JComboBox<String> cmbEstado;

    public ProductoPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 140, 200)),
            "Datos del Producto", 0, 0,
            new Font("Segoe UI", Font.BOLD, 12), new Color(30, 60, 114)));
        formPanel.setBackground(new Color(250, 252, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.anchor = GridBagConstraints.WEST;

        txtIdCategoria = new JTextField(8);
        txtIdProveedor = new JTextField(8);
        txtSku         = new JTextField(15);
        txtNombre      = new JTextField(20);
        txtDescripcion = new JTextField(20);
        txtPrecio      = new JTextField(10);
        cmbEstado      = new JComboBox<>(new String[]{"ACTIVO", "INACTIVO"});

        // Dos columnas de campos
        String[] labelsL = {"ID Categoría:", "ID Proveedor:", "SKU:", "Estado:"};
        Component[] fieldsL = {txtIdCategoria, txtIdProveedor, txtSku, cmbEstado};
        String[] labelsR = {"Nombre:", "Descripción:", "Precio (₡):"};
        Component[] fieldsR = {txtNombre, txtDescripcion, txtPrecio};

        for (int i = 0; i < labelsL.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(new JLabel(labelsL[i]), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.4;
            formPanel.add(fieldsL[i], gbc);

            if (i < labelsR.length) {
                gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
                formPanel.add(new JLabel(labelsR[i]), gbc);
                gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.6;
                formPanel.add(fieldsR[i], gbc);
            }
        }

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnPanel.setBackground(new Color(250, 252, 255));
        JButton btnInsertar  = crearBoton("Insertar",   new Color(34, 139, 34));
        JButton btnActualizar = crearBoton("Actualizar", new Color(30, 100, 180));
        JButton btnEliminar  = crearBoton("Eliminar",   new Color(180, 40, 40));
        JButton btnLimpiar   = crearBoton("Limpiar",    new Color(120, 120, 120));
        JButton btnRefrescar = crearBoton("Refrescar",  new Color(60, 160, 160));
        btnPanel.add(btnInsertar); btnPanel.add(btnActualizar);
        btnPanel.add(btnEliminar); btnPanel.add(btnLimpiar); btnPanel.add(btnRefrescar);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(250, 252, 255));
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(btnPanel, BorderLayout.SOUTH);

        tableModel = new DefaultTableModel(
            new String[]{"ID", "Cat.", "Prov.", "SKU", "Nombre", "Descripción", "Precio", "Estado", "Creado"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(tableModel);
        estilizarTabla(tabla);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                int row = tabla.getSelectedRow();
                txtIdCategoria.setText(str(tableModel.getValueAt(row, 1)));
                txtIdProveedor.setText(str(tableModel.getValueAt(row, 2)));
                txtSku.setText(str(tableModel.getValueAt(row, 3)));
                txtNombre.setText(str(tableModel.getValueAt(row, 4)));
                txtDescripcion.setText(str(tableModel.getValueAt(row, 5)));
                txtPrecio.setText(str(tableModel.getValueAt(row, 6)));
                cmbEstado.setSelectedItem(tableModel.getValueAt(row, 7));
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 140, 200)),
            "Lista de Productos", 0, 0,
            new Font("Segoe UI", Font.BOLD, 12), new Color(30, 60, 114)));

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        btnInsertar.addActionListener(e -> insertarProducto());
        btnActualizar.addActionListener(e -> actualizarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnRefrescar.addActionListener(e -> cargarDatos());
    }

    private String str(Object o) { return o == null ? "" : o.toString(); }

    private void estilizarTabla(JTable t) {
        t.setRowHeight(24); t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.getTableHeader().setBackground(new Color(30, 60, 114));
        t.getTableHeader().setForeground(Color.WHITE);
        t.setSelectionBackground(new Color(173, 214, 255));
        t.setGridColor(new Color(210, 210, 210));
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false); btn.setBorderPainted(false); btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 32));
        return btn;
    }

    private void cargarDatos() {
        tableModel.setRowCount(0);
        List<Producto> lista = productoDAO.listarProductos();
        for (Producto p : lista)
            tableModel.addRow(new Object[]{
                p.getIdProducto(), p.getIdCategoria(), p.getIdProveedor(),
                p.getSku(), p.getNombre(), p.getDescripcion(),
                p.getPrecio(), p.getEstado(), p.getFechaCreacion()});
    }

    private void insertarProducto() {
        try {
            Producto p = buildProducto();
            productoDAO.insertarProducto(p);
            JOptionPane.showMessageDialog(this, "Producto insertado correctamente.");
            limpiarFormulario(); cargarDatos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void actualizarProducto() {
        int row = tabla.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione un producto."); return; }
        try {
            Producto p = buildProducto();
            p.setIdProducto((int) tableModel.getValueAt(row, 0));
            productoDAO.actualizarProducto(p);
            JOptionPane.showMessageDialog(this, "Producto actualizado.");
            limpiarFormulario(); cargarDatos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private Producto buildProducto() {
        String sku    = txtSku.getText().trim();
        String nombre = txtNombre.getText().trim();
        if (sku.isEmpty() || nombre.isEmpty()) throw new IllegalArgumentException("SKU y nombre son obligatorios.");
        Producto p = new Producto();
        p.setIdCategoria(Integer.parseInt(txtIdCategoria.getText().trim()));
        p.setIdProveedor(Integer.parseInt(txtIdProveedor.getText().trim()));
        p.setSku(sku); p.setNombre(nombre);
        p.setDescripcion(txtDescripcion.getText().trim());
        p.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));
        p.setEstado((String) cmbEstado.getSelectedItem());
        return p;
    }

    private void eliminarProducto() {
        int row = tabla.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione un producto."); return; }
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar este producto?", "Confirmar",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            productoDAO.eliminarProducto((int) tableModel.getValueAt(row, 0));
            JOptionPane.showMessageDialog(this, "Producto eliminado.");
            limpiarFormulario(); cargarDatos();
        }
    }

    private void limpiarFormulario() {
        txtIdCategoria.setText(""); txtIdProveedor.setText(""); txtSku.setText("");
        txtNombre.setText(""); txtDescripcion.setText(""); txtPrecio.setText("");
        cmbEstado.setSelectedIndex(0); tabla.clearSelection();
    }
}

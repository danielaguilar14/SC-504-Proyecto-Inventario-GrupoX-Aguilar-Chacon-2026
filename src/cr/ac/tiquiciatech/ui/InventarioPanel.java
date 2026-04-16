package cr.ac.tiquiciatech.ui;

import cr.ac.tiquiciatech.dao.InventarioDAO;
import cr.ac.tiquiciatech.model.Inventario;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Panel CRUD para la tabla INVENTARIO.
 */
public class InventarioPanel extends JPanel {

    private InventarioDAO inventarioDAO = new InventarioDAO();
    private DefaultTableModel tableModel;
    private JTable tabla;
    private JTextField txtIdProducto, txtStockActual, txtStockMinimo, txtStockMaximo;

    public InventarioPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 140, 200)),
            "Datos de Inventario", 0, 0,
            new Font("Segoe UI", Font.BOLD, 12), new Color(30, 60, 114)));
        formPanel.setBackground(new Color(250, 252, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.anchor = GridBagConstraints.WEST;

        txtIdProducto  = new JTextField(10);
        txtStockActual = new JTextField(10);
        txtStockMinimo = new JTextField(10);
        txtStockMaximo = new JTextField(10);

        String[] labels = {"ID Producto:", "Stock Actual:", "Stock Mínimo:", "Stock Máximo (opcional):"};
        JTextField[] fields = {txtIdProducto, txtStockActual, txtStockMinimo, txtStockMaximo};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            formPanel.add(fields[i], gbc);
        }

        // Indicador de alerta de stock
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel nota = new JLabel("  ℹ Alerta: stock_actual < stock_mínimo genera advertencia en sistema.");
        nota.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        nota.setForeground(new Color(0, 100, 150));
        formPanel.add(nota, gbc);

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
            new String[]{"ID Inventario", "ID Producto", "Stock Actual", "Stock Mínimo", "Stock Máximo"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(tableModel);
        estilizarTabla(tabla);

        // Colorear filas con stock bajo
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) {
                    Object actual  = tableModel.getValueAt(row, 2);
                    Object minimo  = tableModel.getValueAt(row, 3);
                    if (actual != null && minimo != null) {
                        int a = (int) actual, m = (int) minimo;
                        c.setBackground(a <= m ? new Color(255, 230, 230) : Color.WHITE);
                    }
                }
                return c;
            }
        });

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                int row = tabla.getSelectedRow();
                txtIdProducto.setText(str(tableModel.getValueAt(row, 1)));
                txtStockActual.setText(str(tableModel.getValueAt(row, 2)));
                txtStockMinimo.setText(str(tableModel.getValueAt(row, 3)));
                txtStockMaximo.setText(str(tableModel.getValueAt(row, 4)));
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 140, 200)),
            "Lista de Inventario  (rojo = stock bajo mínimo)", 0, 0,
            new Font("Segoe UI", Font.BOLD, 12), new Color(30, 60, 114)));

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        btnInsertar.addActionListener(e -> insertarInventario());
        btnActualizar.addActionListener(e -> actualizarInventario());
        btnEliminar.addActionListener(e -> eliminarInventario());
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
        List<Inventario> lista = inventarioDAO.listarInventario();
        for (Inventario inv : lista)
            tableModel.addRow(new Object[]{
                inv.getIdInventario(), inv.getIdProducto(),
                inv.getStockActual(), inv.getStockMinimo(), inv.getStockMaximo()});
    }

    private void insertarInventario() {
        try {
            Inventario inv = buildInventario();
            inventarioDAO.insertarInventario(inv);
            JOptionPane.showMessageDialog(this, "Inventario insertado correctamente.");
            limpiarFormulario(); cargarDatos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void actualizarInventario() {
        int row = tabla.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione un registro."); return; }
        try {
            Inventario inv = buildInventario();
            inv.setIdInventario((int) tableModel.getValueAt(row, 0));
            inventarioDAO.actualizarInventario(inv);
            JOptionPane.showMessageDialog(this, "Inventario actualizado.");
            limpiarFormulario(); cargarDatos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private Inventario buildInventario() {
        Inventario inv = new Inventario();
        inv.setIdProducto(Integer.parseInt(txtIdProducto.getText().trim()));
        inv.setStockActual(Integer.parseInt(txtStockActual.getText().trim()));
        inv.setStockMinimo(Integer.parseInt(txtStockMinimo.getText().trim()));
        String maxStr = txtStockMaximo.getText().trim();
        inv.setStockMaximo(maxStr.isEmpty() ? null : Integer.parseInt(maxStr));
        return inv;
    }

    private void eliminarInventario() {
        int row = tabla.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione un registro."); return; }
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar este registro?", "Confirmar",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            inventarioDAO.eliminarInventario((int) tableModel.getValueAt(row, 0));
            JOptionPane.showMessageDialog(this, "Registro eliminado.");
            limpiarFormulario(); cargarDatos();
        }
    }

    private void limpiarFormulario() {
        txtIdProducto.setText(""); txtStockActual.setText("");
        txtStockMinimo.setText(""); txtStockMaximo.setText("");
        tabla.clearSelection();
    }
}

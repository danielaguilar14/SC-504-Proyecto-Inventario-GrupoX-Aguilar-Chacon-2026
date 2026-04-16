package cr.ac.tiquiciatech.ui;

import cr.ac.tiquiciatech.dao.ProveedorDAO;
import cr.ac.tiquiciatech.model.Proveedor;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Panel CRUD para la tabla PROVEEDOR.
 */
public class ProveedorPanel extends JPanel {

    private ProveedorDAO proveedorDAO = new ProveedorDAO();
    private DefaultTableModel tableModel;
    private JTable tabla;
    private JTextField txtNombre, txtTelefono, txtEmail, txtDireccion;
    private JComboBox<String> cmbEstado;

    public ProveedorPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 140, 200)),
            "Datos del Proveedor", 0, 0,
            new Font("Segoe UI", Font.BOLD, 12), new Color(30, 60, 114)));
        formPanel.setBackground(new Color(250, 252, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.anchor = GridBagConstraints.WEST;

        String[] labels = {"Nombre:","Teléfono:","Email:","Dirección:","Estado:"};
        txtNombre    = new JTextField(20);
        txtTelefono  = new JTextField(20);
        txtEmail     = new JTextField(20);
        txtDireccion = new JTextField(20);
        cmbEstado    = new JComboBox<>(new String[]{"ACTIVO", "INACTIVO"});

        Component[] fields = {txtNombre, txtTelefono, txtEmail, txtDireccion, cmbEstado};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            formPanel.add(fields[i], gbc);
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
            new String[]{"ID", "Nombre", "Teléfono", "Email", "Dirección", "Estado"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(tableModel);
        estilizarTabla(tabla);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                int row = tabla.getSelectedRow();
                txtNombre.setText(str(tableModel.getValueAt(row, 1)));
                txtTelefono.setText(str(tableModel.getValueAt(row, 2)));
                txtEmail.setText(str(tableModel.getValueAt(row, 3)));
                txtDireccion.setText(str(tableModel.getValueAt(row, 4)));
                cmbEstado.setSelectedItem(tableModel.getValueAt(row, 5));
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 140, 200)),
            "Lista de Proveedores", 0, 0,
            new Font("Segoe UI", Font.BOLD, 12), new Color(30, 60, 114)));

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        btnInsertar.addActionListener(e -> insertarProveedor());
        btnActualizar.addActionListener(e -> actualizarProveedor());
        btnEliminar.addActionListener(e -> eliminarProveedor());
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
        List<Proveedor> lista = proveedorDAO.listarProveedores();
        for (Proveedor p : lista)
            tableModel.addRow(new Object[]{
                p.getIdProveedor(), p.getNombre(), p.getTelefono(),
                p.getEmail(), p.getDireccion(), p.getEstado()});
    }

    private void insertarProveedor() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) { JOptionPane.showMessageDialog(this, "El nombre es obligatorio."); return; }
        Proveedor p = new Proveedor();
        p.setNombre(nombre); p.setTelefono(txtTelefono.getText().trim());
        p.setEmail(txtEmail.getText().trim()); p.setDireccion(txtDireccion.getText().trim());
        p.setEstado((String) cmbEstado.getSelectedItem());
        proveedorDAO.insertarProveedor(p);
        JOptionPane.showMessageDialog(this, "Proveedor insertado correctamente.");
        limpiarFormulario(); cargarDatos();
    }

    private void actualizarProveedor() {
        int row = tabla.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione un proveedor."); return; }
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) { JOptionPane.showMessageDialog(this, "El nombre es obligatorio."); return; }
        Proveedor p = new Proveedor();
        p.setIdProveedor((int) tableModel.getValueAt(row, 0));
        p.setNombre(nombre); p.setTelefono(txtTelefono.getText().trim());
        p.setEmail(txtEmail.getText().trim()); p.setDireccion(txtDireccion.getText().trim());
        p.setEstado((String) cmbEstado.getSelectedItem());
        proveedorDAO.actualizarProveedor(p);
        JOptionPane.showMessageDialog(this, "Proveedor actualizado.");
        limpiarFormulario(); cargarDatos();
    }

    private void eliminarProveedor() {
        int row = tabla.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione un proveedor."); return; }
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar este proveedor?", "Confirmar",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            proveedorDAO.eliminarProveedor((int) tableModel.getValueAt(row, 0));
            JOptionPane.showMessageDialog(this, "Proveedor eliminado.");
            limpiarFormulario(); cargarDatos();
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText(""); txtTelefono.setText(""); txtEmail.setText("");
        txtDireccion.setText(""); cmbEstado.setSelectedIndex(0); tabla.clearSelection();
    }
}

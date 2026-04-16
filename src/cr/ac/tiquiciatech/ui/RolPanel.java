package cr.ac.tiquiciatech.ui;

import cr.ac.tiquiciatech.dao.RolDAO;
import cr.ac.tiquiciatech.model.Rol;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Panel CRUD para la tabla ROL.
 */
public class RolPanel extends JPanel {

    private RolDAO rolDAO = new RolDAO();
    private DefaultTableModel tableModel;
    private JTable tabla;
    private JTextField txtNombre;

    public RolPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        // --- Formulario ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 140, 200), 1),
            "Datos del Rol", 0, 0,
            new Font("Segoe UI", Font.BOLD, 12), new Color(30, 60, 114)));
        formPanel.setBackground(new Color(250, 252, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nombre del Rol:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtNombre = new JTextField(20);
        formPanel.add(txtNombre, gbc);

        // --- Botones ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnPanel.setBackground(new Color(250, 252, 255));

        JButton btnInsertar  = crearBoton("Insertar",   new Color(34, 139, 34));
        JButton btnActualizar = crearBoton("Actualizar", new Color(30, 100, 180));
        JButton btnEliminar  = crearBoton("Eliminar",   new Color(180, 40, 40));
        JButton btnLimpiar   = crearBoton("Limpiar",    new Color(120, 120, 120));
        JButton btnRefrescar = crearBoton("Refrescar",  new Color(60, 160, 160));

        btnPanel.add(btnInsertar);
        btnPanel.add(btnActualizar);
        btnPanel.add(btnEliminar);
        btnPanel.add(btnLimpiar);
        btnPanel.add(btnRefrescar);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(250, 252, 255));
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(btnPanel, BorderLayout.SOUTH);

        // --- Tabla ---
        tableModel = new DefaultTableModel(new String[]{"ID", "Nombre"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(tableModel);
        tabla.setRowHeight(24);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(30, 60, 114));
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.setSelectionBackground(new Color(173, 214, 255));
        tabla.setGridColor(new Color(210, 210, 210));

        // Selección de fila → llena formulario
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                int row = tabla.getSelectedRow();
                txtNombre.setText((String) tableModel.getValueAt(row, 1));
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 140, 200)),
            "Lista de Roles", 0, 0,
            new Font("Segoe UI", Font.BOLD, 12), new Color(30, 60, 114)));

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Acciones
        btnInsertar.addActionListener(e -> insertarRol());
        btnActualizar.addActionListener(e -> actualizarRol());
        btnEliminar.addActionListener(e -> eliminarRol());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnRefrescar.addActionListener(e -> cargarDatos());
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 32));
        return btn;
    }

    private void cargarDatos() {
        tableModel.setRowCount(0);
        List<Rol> lista = rolDAO.listarRoles();
        for (Rol r : lista) {
            tableModel.addRow(new Object[]{r.getIdRol(), r.getNombre()});
        }
    }

    private void insertarRol() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) { JOptionPane.showMessageDialog(this, "El nombre es obligatorio."); return; }
        Rol rol = new Rol();
        rol.setNombre(nombre);
        rolDAO.insertarRol(rol);
        JOptionPane.showMessageDialog(this, "Rol insertado correctamente.");
        limpiarFormulario();
        cargarDatos();
    }

    private void actualizarRol() {
        int row = tabla.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione un rol de la tabla."); return; }
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) { JOptionPane.showMessageDialog(this, "El nombre es obligatorio."); return; }
        Rol rol = new Rol();
        rol.setIdRol((int) tableModel.getValueAt(row, 0));
        rol.setNombre(nombre);
        rolDAO.actualizarRol(rol);
        JOptionPane.showMessageDialog(this, "Rol actualizado correctamente.");
        limpiarFormulario();
        cargarDatos();
    }

    private void eliminarRol() {
        int row = tabla.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione un rol de la tabla."); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar este rol?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            rolDAO.eliminarRol((int) tableModel.getValueAt(row, 0));
            JOptionPane.showMessageDialog(this, "Rol eliminado correctamente.");
            limpiarFormulario();
            cargarDatos();
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        tabla.clearSelection();
    }
}

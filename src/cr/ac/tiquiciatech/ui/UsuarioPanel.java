package cr.ac.tiquiciatech.ui;

import cr.ac.tiquiciatech.dao.UsuarioDAO;
import cr.ac.tiquiciatech.model.Usuario;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Panel CRUD para la tabla USUARIO.
 */
public class UsuarioPanel extends JPanel {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private DefaultTableModel tableModel;
    private JTable tabla;
    private JTextField txtIdRol, txtNombre, txtEmail;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbEstado;

    public UsuarioPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 140, 200)),
            "Datos del Usuario", 0, 0,
            new Font("Segoe UI", Font.BOLD, 12), new Color(30, 60, 114)));
        formPanel.setBackground(new Color(250, 252, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.anchor = GridBagConstraints.WEST;

        txtIdRol    = new JTextField(5);
        txtNombre   = new JTextField(20);
        txtEmail    = new JTextField(20);
        txtPassword = new JPasswordField(20);
        cmbEstado   = new JComboBox<>(new String[]{"ACTIVO", "INACTIVO"});

        String[] labels = {"ID Rol:", "Nombre:", "Email:", "Contraseña:", "Estado:"};
        Component[] fields = {txtIdRol, txtNombre, txtEmail, txtPassword, cmbEstado};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            formPanel.add(fields[i], gbc);
        }

        // Nota de seguridad
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel nota = new JLabel("  ⚠ La contraseña se almacena como hash en la BD.");
        nota.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        nota.setForeground(new Color(150, 80, 0));
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
            new String[]{"ID", "ID Rol", "Nombre", "Email", "Estado", "Fecha Creación"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(tableModel);
        estilizarTabla(tabla);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                int row = tabla.getSelectedRow();
                txtIdRol.setText(str(tableModel.getValueAt(row, 1)));
                txtNombre.setText(str(tableModel.getValueAt(row, 2)));
                txtEmail.setText(str(tableModel.getValueAt(row, 3)));
                txtPassword.setText("");
                cmbEstado.setSelectedItem(tableModel.getValueAt(row, 4));
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 140, 200)),
            "Lista de Usuarios", 0, 0,
            new Font("Segoe UI", Font.BOLD, 12), new Color(30, 60, 114)));

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        btnInsertar.addActionListener(e -> insertarUsuario());
        btnActualizar.addActionListener(e -> actualizarUsuario());
        btnEliminar.addActionListener(e -> eliminarUsuario());
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
        List<Usuario> lista = usuarioDAO.listarUsuarios();
        for (Usuario u : lista)
            tableModel.addRow(new Object[]{
                u.getIdUsuario(), u.getIdRol(), u.getNombre(),
                u.getEmail(), u.getEstado(), u.getFechaCreacion()});
    }

    private void insertarUsuario() {
        try {
            int idRol = Integer.parseInt(txtIdRol.getText().trim());
            String nombre = txtNombre.getText().trim();
            String email  = txtEmail.getText().trim();
            String pass   = new String(txtPassword.getPassword()).trim();
            if (nombre.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nombre y email son obligatorios."); return;
            }
            Usuario u = new Usuario();
            u.setIdRol(idRol); u.setNombre(nombre); u.setEmail(email);
            u.setPasswordHash(pass); u.setEstado((String) cmbEstado.getSelectedItem());
            usuarioDAO.insertarUsuario(u);
            JOptionPane.showMessageDialog(this, "Usuario insertado correctamente.");
            limpiarFormulario(); cargarDatos();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El ID de Rol debe ser un número.");
        }
    }

    private void actualizarUsuario() {
        int row = tabla.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione un usuario."); return; }
        try {
            Usuario u = new Usuario();
            u.setIdUsuario((int) tableModel.getValueAt(row, 0));
            u.setIdRol(Integer.parseInt(txtIdRol.getText().trim()));
            u.setNombre(txtNombre.getText().trim()); u.setEmail(txtEmail.getText().trim());
            u.setPasswordHash(new String(txtPassword.getPassword()).trim());
            u.setEstado((String) cmbEstado.getSelectedItem());
            usuarioDAO.actualizarUsuario(u);
            JOptionPane.showMessageDialog(this, "Usuario actualizado.");
            limpiarFormulario(); cargarDatos();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El ID de Rol debe ser un número.");
        }
    }

    private void eliminarUsuario() {
        int row = tabla.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione un usuario."); return; }
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar este usuario?", "Confirmar",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            usuarioDAO.eliminarUsuario((int) tableModel.getValueAt(row, 0));
            JOptionPane.showMessageDialog(this, "Usuario eliminado.");
            limpiarFormulario(); cargarDatos();
        }
    }

    private void limpiarFormulario() {
        txtIdRol.setText(""); txtNombre.setText(""); txtEmail.setText("");
        txtPassword.setText(""); cmbEstado.setSelectedIndex(0); tabla.clearSelection();
    }
}

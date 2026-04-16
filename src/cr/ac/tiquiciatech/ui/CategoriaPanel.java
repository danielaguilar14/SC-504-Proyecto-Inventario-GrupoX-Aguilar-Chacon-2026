package cr.ac.tiquiciatech.ui;

import cr.ac.tiquiciatech.dao.CategoriaDAO;
import cr.ac.tiquiciatech.model.Categoria;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Panel CRUD para la tabla CATEGORIA.
 */
public class CategoriaPanel extends JPanel {

    private CategoriaDAO categoriaDAO = new CategoriaDAO();
    private DefaultTableModel tableModel;
    private JTable tabla;
    private JTextField txtNombre;
    private JTextField txtDescripcion;
    private JComboBox<String> cmbEstado;

    public CategoriaPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 140, 200)),
            "Datos de Categoría", 0, 0,
            new Font("Segoe UI", Font.BOLD, 12), new Color(30, 60, 114)));
        formPanel.setBackground(new Color(250, 252, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtNombre = new JTextField(20);
        formPanel.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtDescripcion = new JTextField(20);
        formPanel.add(txtDescripcion, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        cmbEstado = new JComboBox<>(new String[]{"ACTIVO", "INACTIVO"});
        formPanel.add(cmbEstado, gbc);

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
            new String[]{"ID", "Nombre", "Descripción", "Estado"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(tableModel);
        estilizarTabla(tabla);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                int row = tabla.getSelectedRow();
                txtNombre.setText((String) tableModel.getValueAt(row, 1));
                txtDescripcion.setText((String) tableModel.getValueAt(row, 2));
                cmbEstado.setSelectedItem(tableModel.getValueAt(row, 3));
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 140, 200)),
            "Lista de Categorías", 0, 0,
            new Font("Segoe UI", Font.BOLD, 12), new Color(30, 60, 114)));

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        btnInsertar.addActionListener(e -> insertarCategoria());
        btnActualizar.addActionListener(e -> actualizarCategoria());
        btnEliminar.addActionListener(e -> eliminarCategoria());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnRefrescar.addActionListener(e -> cargarDatos());
    }

    private void estilizarTabla(JTable t) {
        t.setRowHeight(24);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
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
        List<Categoria> lista = categoriaDAO.listarCategorias();
        for (Categoria c : lista)
            tableModel.addRow(new Object[]{c.getIdCategoria(), c.getNombre(), c.getDescripcion(), c.getEstado()});
    }

    private void insertarCategoria() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) { JOptionPane.showMessageDialog(this, "El nombre es obligatorio."); return; }
        Categoria cat = new Categoria();
        cat.setNombre(nombre);
        cat.setDescripcion(txtDescripcion.getText().trim());
        cat.setEstado((String) cmbEstado.getSelectedItem());
        categoriaDAO.insertarCategoria(cat);
        JOptionPane.showMessageDialog(this, "Categoría insertada correctamente.");
        limpiarFormulario(); cargarDatos();
    }

    private void actualizarCategoria() {
        int row = tabla.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione una categoría."); return; }
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) { JOptionPane.showMessageDialog(this, "El nombre es obligatorio."); return; }
        Categoria cat = new Categoria();
        cat.setIdCategoria((int) tableModel.getValueAt(row, 0));
        cat.setNombre(nombre);
        cat.setDescripcion(txtDescripcion.getText().trim());
        cat.setEstado((String) cmbEstado.getSelectedItem());
        categoriaDAO.actualizarCategoria(cat);
        JOptionPane.showMessageDialog(this, "Categoría actualizada.");
        limpiarFormulario(); cargarDatos();
    }

    private void eliminarCategoria() {
        int row = tabla.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione una categoría."); return; }
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar esta categoría?", "Confirmar",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            categoriaDAO.eliminarCategoria((int) tableModel.getValueAt(row, 0));
            JOptionPane.showMessageDialog(this, "Categoría eliminada.");
            limpiarFormulario(); cargarDatos();
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText(""); txtDescripcion.setText("");
        cmbEstado.setSelectedIndex(0); tabla.clearSelection();
    }
}

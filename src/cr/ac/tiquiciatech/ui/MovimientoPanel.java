package cr.ac.tiquiciatech.ui;

import cr.ac.tiquiciatech.dao.MovimientoDAO;
import cr.ac.tiquiciatech.dao.MovimientoDetalleDAO;
import cr.ac.tiquiciatech.model.Movimiento;
import cr.ac.tiquiciatech.model.MovimientoDetalle;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Panel CRUD para MOVIMIENTO y MOVIMIENTO_DETALLE.
 * Maneja ambas tablas en una vista dividida (JSplitPane).
 */
public class MovimientoPanel extends JPanel {

    private MovimientoDAO movimientoDAO = new MovimientoDAO();
    private MovimientoDetalleDAO detalleDAO = new MovimientoDetalleDAO();

    // Movimiento
    private DefaultTableModel modelMovimiento;
    private JTable tablaMovimiento;
    private JTextField txtIdUsuario, txtObservacion;
    private JComboBox<String> cmbTipo;

    // Detalle
    private DefaultTableModel modelDetalle;
    private JTable tablaDetalle;
    private JTextField txtIdMovimiento, txtIdProducto, txtCantidad, txtCostoUnitario;

    public MovimientoPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        cargarMovimientos();
    }

    private void initComponents() {
        // ====== PANEL MOVIMIENTO ======
        JPanel movPanel = new JPanel(new BorderLayout(5, 5));
        movPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(30, 60, 114), 2),
            "Movimientos", 0, 0,
            new Font("Segoe UI", Font.BOLD, 13), new Color(30, 60, 114)));

        JPanel formMov = new JPanel(new GridBagLayout());
        formMov.setBackground(new Color(250, 252, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8); gbc.anchor = GridBagConstraints.WEST;

        txtIdUsuario  = new JTextField(8);
        cmbTipo       = new JComboBox<>(new String[]{"ENTRADA", "SALIDA", "AJUSTE"});
        txtObservacion = new JTextField(20);

        String[] lM = {"ID Usuario:", "Tipo:", "Observación:"};
        Component[] fM = {txtIdUsuario, cmbTipo, txtObservacion};
        for (int i = 0; i < lM.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formMov.add(new JLabel(lM[i]), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            formMov.add(fM[i], gbc);
        }

        JPanel btnMov = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 3));
        btnMov.setBackground(new Color(250, 252, 255));
        JButton bMIns = crearBoton("Insertar",   new Color(34, 139, 34));
        JButton bMAct = crearBoton("Actualizar", new Color(30, 100, 180));
        JButton bMDel = crearBoton("Eliminar",   new Color(180, 40, 40));
        JButton bMLim = crearBoton("Limpiar",    new Color(120, 120, 120));
        JButton bMRef = crearBoton("Refrescar",  new Color(60, 160, 160));
        btnMov.add(bMIns); btnMov.add(bMAct); btnMov.add(bMDel); btnMov.add(bMLim); btnMov.add(bMRef);

        JPanel topMov = new JPanel(new BorderLayout());
        topMov.setBackground(new Color(250, 252, 255));
        topMov.add(formMov, BorderLayout.CENTER); topMov.add(btnMov, BorderLayout.SOUTH);

        modelMovimiento = new DefaultTableModel(
            new String[]{"ID", "ID Usuario", "Tipo", "Fecha", "Observación"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaMovimiento = new JTable(modelMovimiento);
        estilizarTabla(tablaMovimiento);

        tablaMovimiento.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaMovimiento.getSelectedRow() >= 0) {
                int row = tablaMovimiento.getSelectedRow();
                txtIdUsuario.setText(str(modelMovimiento.getValueAt(row, 1)));
                cmbTipo.setSelectedItem(modelMovimiento.getValueAt(row, 2));
                txtObservacion.setText(str(modelMovimiento.getValueAt(row, 4)));
            }
        });

        JScrollPane scrollMov = new JScrollPane(tablaMovimiento);
        movPanel.add(topMov, BorderLayout.NORTH);
        movPanel.add(scrollMov, BorderLayout.CENTER);

        // ====== PANEL DETALLE ======
        JPanel detPanel = new JPanel(new BorderLayout(5, 5));
        detPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 140, 200), 2),
            "Detalle del Movimiento", 0, 0,
            new Font("Segoe UI", Font.BOLD, 13), new Color(30, 60, 114)));

        JPanel formDet = new JPanel(new GridBagLayout());
        formDet.setBackground(new Color(250, 252, 255));
        GridBagConstraints gbcD = new GridBagConstraints();
        gbcD.insets = new Insets(4, 8, 4, 8); gbcD.anchor = GridBagConstraints.WEST;

        txtIdMovimiento  = new JTextField(8);
        txtIdProducto    = new JTextField(8);
        txtCantidad      = new JTextField(8);
        txtCostoUnitario = new JTextField(10);

        String[] lD = {"ID Movimiento:", "ID Producto:", "Cantidad:", "Costo Unitario:"};
        JTextField[] fD = {txtIdMovimiento, txtIdProducto, txtCantidad, txtCostoUnitario};
        for (int i = 0; i < lD.length; i++) {
            gbcD.gridx = 0; gbcD.gridy = i; gbcD.fill = GridBagConstraints.NONE; gbcD.weightx = 0;
            formDet.add(new JLabel(lD[i]), gbcD);
            gbcD.gridx = 1; gbcD.fill = GridBagConstraints.HORIZONTAL; gbcD.weightx = 1.0;
            formDet.add(fD[i], gbcD);
        }

        JPanel btnDet = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 3));
        btnDet.setBackground(new Color(250, 252, 255));
        JButton bDIns = crearBoton("Insertar",   new Color(34, 139, 34));
        JButton bDAct = crearBoton("Actualizar", new Color(30, 100, 180));
        JButton bDDel = crearBoton("Eliminar",   new Color(180, 40, 40));
        JButton bDLim = crearBoton("Limpiar",    new Color(120, 120, 120));
        JButton bDRef = crearBoton("Refrescar",  new Color(60, 160, 160));
        btnDet.add(bDIns); btnDet.add(bDAct); btnDet.add(bDDel); btnDet.add(bDLim); btnDet.add(bDRef);

        JPanel topDet = new JPanel(new BorderLayout());
        topDet.setBackground(new Color(250, 252, 255));
        topDet.add(formDet, BorderLayout.CENTER); topDet.add(btnDet, BorderLayout.SOUTH);

        modelDetalle = new DefaultTableModel(
            new String[]{"ID Detalle", "ID Movimiento", "ID Producto", "Cantidad", "Costo Unitario"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaDetalle = new JTable(modelDetalle);
        estilizarTabla(tablaDetalle);

        tablaDetalle.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaDetalle.getSelectedRow() >= 0) {
                int row = tablaDetalle.getSelectedRow();
                txtIdMovimiento.setText(str(modelDetalle.getValueAt(row, 1)));
                txtIdProducto.setText(str(modelDetalle.getValueAt(row, 2)));
                txtCantidad.setText(str(modelDetalle.getValueAt(row, 3)));
                txtCostoUnitario.setText(str(modelDetalle.getValueAt(row, 4)));
            }
        });

        JScrollPane scrollDet = new JScrollPane(tablaDetalle);
        detPanel.add(topDet, BorderLayout.NORTH);
        detPanel.add(scrollDet, BorderLayout.CENTER);

        // Split
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, movPanel, detPanel);
        split.setResizeWeight(0.5);
        split.setDividerSize(6);
        add(split, BorderLayout.CENTER);

        // Acciones movimiento
        bMIns.addActionListener(e -> insertarMovimiento());
        bMAct.addActionListener(e -> actualizarMovimiento());
        bMDel.addActionListener(e -> eliminarMovimiento());
        bMLim.addActionListener(e -> limpiarMovimiento());
        bMRef.addActionListener(e -> cargarMovimientos());

        // Acciones detalle
        bDIns.addActionListener(e -> insertarDetalle());
        bDAct.addActionListener(e -> actualizarDetalle());
        bDDel.addActionListener(e -> eliminarDetalle());
        bDLim.addActionListener(e -> limpiarDetalle());
        bDRef.addActionListener(e -> cargarDetalles());
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
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setFocusPainted(false); btn.setBorderPainted(false); btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(95, 28));
        return btn;
    }

    // --- Movimiento CRUD ---
    private void cargarMovimientos() {
        modelMovimiento.setRowCount(0);
        List<Movimiento> lista = movimientoDAO.listarMovimientos();
        for (Movimiento m : lista)
            modelMovimiento.addRow(new Object[]{
                m.getIdMovimiento(), m.getIdUsuario(), m.getTipo(), m.getFecha(), m.getObservacion()});
    }

    private void insertarMovimiento() {
        try {
            Movimiento m = new Movimiento();
            m.setIdUsuario(Integer.parseInt(txtIdUsuario.getText().trim()));
            m.setTipo((String) cmbTipo.getSelectedItem());
            m.setObservacion(txtObservacion.getText().trim());
            movimientoDAO.insertarMovimiento(m);
            JOptionPane.showMessageDialog(this, "Movimiento insertado.");
            limpiarMovimiento(); cargarMovimientos();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
    }

    private void actualizarMovimiento() {
        int row = tablaMovimiento.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione un movimiento."); return; }
        try {
            Movimiento m = new Movimiento();
            m.setIdMovimiento((int) modelMovimiento.getValueAt(row, 0));
            m.setIdUsuario(Integer.parseInt(txtIdUsuario.getText().trim()));
            m.setTipo((String) cmbTipo.getSelectedItem());
            m.setObservacion(txtObservacion.getText().trim());
            movimientoDAO.actualizarMovimiento(m);
            JOptionPane.showMessageDialog(this, "Movimiento actualizado.");
            limpiarMovimiento(); cargarMovimientos();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
    }

    private void eliminarMovimiento() {
        int row = tablaMovimiento.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione un movimiento."); return; }
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar este movimiento?", "Confirmar",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            movimientoDAO.eliminarMovimiento((int) modelMovimiento.getValueAt(row, 0));
            JOptionPane.showMessageDialog(this, "Movimiento eliminado.");
            limpiarMovimiento(); cargarMovimientos();
        }
    }

    private void limpiarMovimiento() {
        txtIdUsuario.setText(""); cmbTipo.setSelectedIndex(0); txtObservacion.setText("");
        tablaMovimiento.clearSelection();
    }

    // --- Detalle CRUD ---
    private void cargarDetalles() {
        modelDetalle.setRowCount(0);
        List<MovimientoDetalle> lista = detalleDAO.listarMovimientoDetalle();
        for (MovimientoDetalle d : lista)
            modelDetalle.addRow(new Object[]{
                d.getIdDetalle(), d.getIdMovimiento(), d.getIdProducto(),
                d.getCantidad(), d.getCostoUnitario()});
    }

    private void insertarDetalle() {
        try {
            MovimientoDetalle d = buildDetalle();
            detalleDAO.insertarMovimientoDetalle(d);
            JOptionPane.showMessageDialog(this, "Detalle insertado.");
            limpiarDetalle(); cargarDetalles();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
    }

    private void actualizarDetalle() {
        int row = tablaDetalle.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione un detalle."); return; }
        try {
            MovimientoDetalle d = buildDetalle();
            d.setIdDetalle((int) modelDetalle.getValueAt(row, 0));
            detalleDAO.actualizarMovimientoDetalle(d);
            JOptionPane.showMessageDialog(this, "Detalle actualizado.");
            limpiarDetalle(); cargarDetalles();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
    }

    private MovimientoDetalle buildDetalle() {
        MovimientoDetalle d = new MovimientoDetalle();
        d.setIdMovimiento(Integer.parseInt(txtIdMovimiento.getText().trim()));
        d.setIdProducto(Integer.parseInt(txtIdProducto.getText().trim()));
        d.setCantidad(Integer.parseInt(txtCantidad.getText().trim()));
        d.setCostoUnitario(Double.parseDouble(txtCostoUnitario.getText().trim()));
        return d;
    }

    private void eliminarDetalle() {
        int row = tablaDetalle.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione un detalle."); return; }
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar este detalle?", "Confirmar",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            detalleDAO.eliminarMovimientoDetalle((int) modelDetalle.getValueAt(row, 0));
            JOptionPane.showMessageDialog(this, "Detalle eliminado.");
            limpiarDetalle(); cargarDetalles();
        }
    }

    private void limpiarDetalle() {
        txtIdMovimiento.setText(""); txtIdProducto.setText("");
        txtCantidad.setText(""); txtCostoUnitario.setText("");
        tablaDetalle.clearSelection();
    }
}

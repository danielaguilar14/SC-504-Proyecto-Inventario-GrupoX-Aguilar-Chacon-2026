package cr.ac.tiquiciatech.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal del Sistema de Gestión de Inventarios TiquiciaTech.
 * Contiene pestañas para cada tabla de la base de datos.
 */
public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("TiquiciaTech - Sistema de Gestión de Inventarios");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        // Icono y apariencia
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Usar look and feel por defecto si falla
        }

        initComponents();
    }

    private void initComponents() {
        // Panel de encabezado
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 60, 114));
        headerPanel.setPreferredSize(new Dimension(0, 60));

        JLabel titleLabel = new JLabel("  TiquiciaTech – Sistema de Gestión de Inventarios");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JLabel subLabel = new JLabel("SC-504 Lenguajes de Base de Datos   ");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(new Color(180, 210, 255));
        headerPanel.add(subLabel, BorderLayout.EAST);

        // Panel de pestañas
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        tabbedPane.addTab("🏷️ Roles",        new RolPanel());
        tabbedPane.addTab("📦 Categorías",    new CategoriaPanel());
        tabbedPane.addTab("🏭 Proveedores",   new ProveedorPanel());
        tabbedPane.addTab("👤 Usuarios",      new UsuarioPanel());
        tabbedPane.addTab("🛒 Productos",     new ProductoPanel());
        tabbedPane.addTab("📊 Inventario",    new InventarioPanel());
        tabbedPane.addTab("🔄 Movimientos",   new MovimientoPanel());

        // Barra de estado
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.setBackground(new Color(245, 245, 245));
        JLabel statusLabel = new JLabel("  Listo. Conectado a Oracle Database – TiquiciaTech");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(80, 80, 80));
        statusBar.add(statusLabel, BorderLayout.WEST);

        // Layout principal
        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}

package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class VistaMenuPrincipal extends JPanel {
    private CtrlPresentacio iCtrlPresentacio;
    private VistaPrincipal vistaPrincipal;

    // Colores del tema
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color PRIMARY_HOVER = new Color(52, 152, 219);
    private static final Color SECONDARY_COLOR = new Color(149, 165, 166);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);

    private JButton btnNueva = new JButton("Nova enquesta");
    private JButton btnImportar = new JButton("Importar enquesta");
    private JButton btnGestionar = new JButton("Gestionar les meves enquestes");
    private JButton btnAnalisi = new JButton("Anàlisi de Clustering");
    private JButton btnRespondre = new JButton("Respondre Enquesta");
    private JButton btnGestionarRespostes = new JButton("Gestionar Les Meves Respostes");
    private JButton btnLogout = new JButton("Tancar sessió");

    public VistaMenuPrincipal(CtrlPresentacio ctrlPresentacio, VistaPrincipal vistaPrincipal) {
        this.iCtrlPresentacio = ctrlPresentacio;
        this.vistaPrincipal = vistaPrincipal;
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        this.setBackground(BACKGROUND_COLOR);
        this.setLayout(new GridBagLayout());

        // Panel tarjeta
        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(CARD_COLOR);
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 3, 3, new Color(0, 0, 0, 30)),
            BorderFactory.createCompoundBorder(
                new LineBorder(new Color(189, 195, 199), 1, true),
                new EmptyBorder(40, 50, 40, 50)
            )
        ));

        // Icono
        JLabel iconLabel = new JLabel("📊");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(iconLabel);
        cardPanel.add(Box.createVerticalStrut(10));

        // Título
        JLabel title = new JLabel("Gestor d'Enquestes");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(title);

        // Subtítulo
        JLabel subtitle = new JLabel("Què vols fer avui?");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(SECONDARY_COLOR);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(subtitle);
        cardPanel.add(Box.createVerticalStrut(30));

        // Sección: Crear enquestes
        cardPanel.add(createSectionLabel("Crear"));
        cardPanel.add(Box.createVerticalStrut(8));
        styleButton(btnNueva, PRIMARY_COLOR);
        cardPanel.add(btnNueva);
        cardPanel.add(Box.createVerticalStrut(8));
        styleButton(btnImportar, PRIMARY_COLOR);
        cardPanel.add(btnImportar);
        cardPanel.add(Box.createVerticalStrut(20));

        // Sección: Gestionar
        cardPanel.add(createSectionLabel("Gestionar"));
        cardPanel.add(Box.createVerticalStrut(8));
        styleButton(btnGestionar, SUCCESS_COLOR);
        cardPanel.add(btnGestionar);
        cardPanel.add(Box.createVerticalStrut(8));
        styleButton(btnAnalisi, SUCCESS_COLOR);
        cardPanel.add(btnAnalisi);
        cardPanel.add(Box.createVerticalStrut(20));

        // Sección: Respondre
        cardPanel.add(createSectionLabel("Respondre"));
        cardPanel.add(Box.createVerticalStrut(8));
        styleButton(btnRespondre, WARNING_COLOR);
        cardPanel.add(btnRespondre);
        cardPanel.add(Box.createVerticalStrut(8));
        styleButton(btnGestionarRespostes, WARNING_COLOR);
        cardPanel.add(btnGestionarRespostes);
        cardPanel.add(Box.createVerticalStrut(25));

        // Separador
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(300, 1));
        cardPanel.add(separator);
        cardPanel.add(Box.createVerticalStrut(15));

        // Botón logout
        styleButton(btnLogout, new Color(231, 76, 60));
        cardPanel.add(btnLogout);

        // Listeners
        btnNueva.addActionListener(e -> mostrarDialogoCrear());
        btnImportar.addActionListener(e -> importarEnquesta());
        btnGestionar.addActionListener(e -> vistaPrincipal.mostrarVista("GESTION"));
        btnAnalisi.addActionListener(e -> vistaPrincipal.mostrarVista("ANALISI"));
        btnRespondre.addActionListener(e -> mostrarDialogoResponder());
        btnGestionarRespostes.addActionListener(e -> mostrarDialogoGestionarRespostes());
        btnLogout.addActionListener(e -> {
            iCtrlPresentacio.logout();
            vistaPrincipal.mostrarVista("LOGIN");
        });

        this.add(cardPanel);
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(SECONDARY_COLOR);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setMaximumSize(new Dimension(300, 45));
        button.setPreferredSize(new Dimension(300, 45));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        Color hoverColor = color.brighter();
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { button.setBackground(hoverColor); }
            public void mouseExited(java.awt.event.MouseEvent e) { button.setBackground(color); }
        });
    }

    private void mostrarDialogoCrear() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        DialogoEnquesta dialogo = new DialogoEnquesta(parentFrame, "Nova enquesta", true);
        dialogo.setVisible(true);

        if (dialogo.isConfirmado()) {
            String resultado = iCtrlPresentacio.crearEnquesta(dialogo.getId(), dialogo.getTitol(), dialogo.getDesc());
            JOptionPane.showMessageDialog(this, resultado);
        }
    }

    private void importarEnquesta() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            String resultado = iCtrlPresentacio.importarEnquesta(selectedFile.getAbsolutePath());
            JOptionPane.showMessageDialog(this, resultado);
        }
    }

    private void mostrarDialogoResponder() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        DialogoSeleccionarEnquesta dialogoSel = new DialogoSeleccionarEnquesta(parentFrame, iCtrlPresentacio);
        dialogoSel.setVisible(true);

        if (dialogoSel.isConfirmado()) {
            String idEnquesta = dialogoSel.getSelectedId();
            DialogoResponderEnquesta dialogoResp = new DialogoResponderEnquesta(parentFrame, iCtrlPresentacio,
                    idEnquesta);
            dialogoResp.setVisible(true);
        }
    }

    private void mostrarDialogoGestionarRespostes() {
        vistaPrincipal.mostrarVista("GESTION_RESPOSTES");
    }
}

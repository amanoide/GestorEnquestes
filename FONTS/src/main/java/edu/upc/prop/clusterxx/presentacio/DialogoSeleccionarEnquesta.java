package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;
import edu.upc.prop.clusterxx.domini.classes.Enquesta;

public class DialogoSeleccionarEnquesta extends JDialog {
    // Colores del tema
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color PRIMARY_HOVER = new Color(52, 152, 219);
    private static final Color SECONDARY_COLOR = new Color(149, 165, 166);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(44, 62, 80);

    private CtrlPresentacio iCtrlPresentacio;
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> listEnquestes = new JList<>(listModel);
    private boolean confirmado = false;
    private String selectedId = null;

    public DialogoSeleccionarEnquesta(Frame owner, CtrlPresentacio ctrlPresentacio) {
        super(owner, "Seleccionar Enquesta", true);
        this.iCtrlPresentacio = ctrlPresentacio;
        inicializar();
        cargarEnquestes();
    }

    private void inicializar() {
        setSize(450, 400);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(CARD_COLOR);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Icono
        JLabel iconLabel = new JLabel("📋");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(iconLabel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Título
        JLabel titleLabel = new JLabel("Seleccionar Enquesta");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        // Subtítulo
        JLabel subtitle = new JLabel("Tria l'enquesta que vols respondre");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(SECONDARY_COLOR);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(subtitle);
        mainPanel.add(Box.createVerticalStrut(20));

        // Lista estilizada
        listEnquestes.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        listEnquestes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listEnquestes.setSelectionBackground(PRIMARY_COLOR);
        listEnquestes.setSelectionForeground(Color.WHITE);
        listEnquestes.setFixedCellHeight(35);
        listEnquestes.setBorder(new EmptyBorder(5, 10, 5, 10));

        JScrollPane scroll = new JScrollPane(listEnquestes);
        scroll.setMaximumSize(new Dimension(350, 150));
        scroll.setPreferredSize(new Dimension(350, 150));
        scroll.setBorder(new LineBorder(new Color(189, 195, 199), 1, true));
        scroll.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(scroll);
        mainPanel.add(Box.createVerticalStrut(25));

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.setMaximumSize(new Dimension(350, 50));

        JButton btnCancel = new JButton("Cancel·lar");
        styleButton(btnCancel, false);
        buttonPanel.add(btnCancel);

        JButton btnOk = new JButton("Seleccionar");
        styleButton(btnOk, true);
        buttonPanel.add(btnOk);

        mainPanel.add(buttonPanel);

        setContentPane(mainPanel);

        // Listeners
        btnOk.addActionListener(e -> {
            String selected = listEnquestes.getSelectedValue();
            if (selected != null) {
                selectedId = selected.split(":")[0].trim();
                confirmado = true;
                setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "⚠ Has de seleccionar una enquesta.", "Selecció requerida", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> setVisible(false));

        // Doble clic para seleccionar
        listEnquestes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    btnOk.doClick();
                }
            }
        });
    }

    private void styleButton(JButton button, boolean isPrimary) {
        button.setFont(new Font("Segoe UI", isPrimary ? Font.BOLD : Font.PLAIN, 13));
        button.setPreferredSize(new Dimension(120, 40));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (isPrimary) {
            button.setForeground(Color.WHITE);
            button.setBackground(PRIMARY_COLOR);
            button.setBorderPainted(false);
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) { button.setBackground(PRIMARY_HOVER); }
                public void mouseExited(java.awt.event.MouseEvent e) { button.setBackground(PRIMARY_COLOR); }
            });
        } else {
            button.setForeground(TEXT_COLOR);
            button.setBackground(CARD_COLOR);
            button.setBorder(new LineBorder(SECONDARY_COLOR, 1, true));
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) { button.setBackground(BACKGROUND_COLOR); }
                public void mouseExited(java.awt.event.MouseEvent e) { button.setBackground(CARD_COLOR); }
            });
        }
    }

    private void cargarEnquestes() {
        listModel.clear();
        ArrayList<Enquesta> enquestes = iCtrlPresentacio.getAllEnquestes();
        if (enquestes.isEmpty()) {
            listModel.addElement("(No hi ha enquestes disponibles)");
            listEnquestes.setEnabled(false);
        } else {
            for (Enquesta e : enquestes) {
                listModel.addElement(e.getId() + ": " + e.getTitol());
            }
        }
    }

    public boolean isConfirmado() { return confirmado; }
    public String getSelectedId() { return selectedId; }
}

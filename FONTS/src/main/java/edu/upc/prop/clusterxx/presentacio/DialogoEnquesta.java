package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class DialogoEnquesta extends JDialog {
    // Colores del tema
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color PRIMARY_HOVER = new Color(52, 152, 219);
    private static final Color SECONDARY_COLOR = new Color(149, 165, 166);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color ERROR_COLOR = new Color(231, 76, 60);

    private JTextField textId = new JTextField(20);
    private JTextField textTitol = new JTextField(20);
    private JTextArea textDesc = new JTextArea(4, 20);
    private boolean confirmado = false;

    public DialogoEnquesta(Frame owner, String title, boolean isCreating) {
        super(owner, title, true);
        inicializarComponentes(isCreating);
    }

    private void inicializarComponentes(boolean isCreating) {
        this.setSize(450, 550);
        this.setLocationRelativeTo(getOwner());
        this.setResizable(false);
        this.getContentPane().setBackground(BACKGROUND_COLOR);

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(CARD_COLOR);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Icono
        JLabel iconLabel = new JLabel(isCreating ? "📝" : "✏️");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(iconLabel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Título
        JLabel titleLabel = new JLabel(isCreating ? "Nova Enquesta" : "Editar Enquesta");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        // Subtítulo
        JLabel subtitle = new JLabel(isCreating ? "Introdueix les dades de l'enquesta" : "Modifica les dades");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(SECONDARY_COLOR);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(subtitle);
        mainPanel.add(Box.createVerticalStrut(25));

        // Campo ID/Nom
        mainPanel.add(createLabel("Identificador" + (isCreating ? "" : " (no editable)")));
        mainPanel.add(Box.createVerticalStrut(5));
        styleTextField(textId);
        textId.setEditable(isCreating);
        if (!isCreating) {
            textId.setBackground(new Color(245, 245, 245));
        }
        mainPanel.add(textId);
        mainPanel.add(Box.createVerticalStrut(15));

        // Campo Título
        mainPanel.add(createLabel("Títol"));
        mainPanel.add(Box.createVerticalStrut(5));
        styleTextField(textTitol);
        mainPanel.add(textTitol);
        mainPanel.add(Box.createVerticalStrut(15));

        // Campo Descripción
        mainPanel.add(createLabel("Descripció"));
        mainPanel.add(Box.createVerticalStrut(5));
        textDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textDesc.setLineWrap(true);
        textDesc.setWrapStyleWord(true);
        textDesc.setBorder(new EmptyBorder(8, 12, 8, 12));
        JScrollPane scrollDesc = new JScrollPane(textDesc);
        scrollDesc.setMaximumSize(new Dimension(350, 80));
        scrollDesc.setPreferredSize(new Dimension(350, 80));
        scrollDesc.setBorder(new LineBorder(new Color(189, 195, 199), 1, true));
        scrollDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(scrollDesc);
        mainPanel.add(Box.createVerticalStrut(25));

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.setMaximumSize(new Dimension(350, 50));

        JButton btnCancelar = new JButton("Cancel·lar");
        styleButton(btnCancelar, false);
        buttonPanel.add(btnCancelar);

        JButton btnConfirmar = new JButton(isCreating ? "Crear Enquesta" : "Guardar Canvis");
        styleButton(btnConfirmar, true);
        buttonPanel.add(btnConfirmar);

        mainPanel.add(buttonPanel);

        this.setContentPane(mainPanel);

        // Listeners
        btnConfirmar.addActionListener(e -> {
            if (validarCampos()) {
                confirmado = true;
                setVisible(false);
            }
        });

        btnCancelar.addActionListener(e -> {
            confirmado = false;
            setVisible(false);
        });

        // Enter para confirmar
        textTitol.addActionListener(e -> textDesc.requestFocus());
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(TEXT_COLOR);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(350, 40));
        field.setPreferredSize(new Dimension(350, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void styleButton(JButton button, boolean isPrimary) {
        button.setFont(new Font("Segoe UI", isPrimary ? Font.BOLD : Font.PLAIN, 13));
        button.setPreferredSize(new Dimension(140, 40));
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

    private boolean validarCampos() {
        if (textId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠ L'identificador és obligatori", "Camp requerit", JOptionPane.WARNING_MESSAGE);
            textId.requestFocus();
            return false;
        }
        if (textTitol.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠ El títol és obligatori", "Camp requerit", JOptionPane.WARNING_MESSAGE);
            textTitol.requestFocus();
            return false;
        }
        return true;
    }

    public void setDatos(String id, String titol, String desc) {
        textId.setText(id);
        textTitol.setText(titol);
        textDesc.setText(desc);
    }

    public String getId() { return textId.getText().trim(); }
    public String getTitol() { return textTitol.getText().trim(); }
    public String getDesc() { return textDesc.getText().trim(); }
    public boolean isConfirmado() { return confirmado; }
}

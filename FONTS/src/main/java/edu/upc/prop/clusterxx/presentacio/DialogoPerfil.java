package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class DialogoPerfil extends JDialog {

    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);

    private JTextArea textAreaPerfil;
    private JButton btnTancar;

    public DialogoPerfil(Frame parent, String infoPerfil) {
        super(parent, "El Meu Perfil", true);
        inicializarComponentes(infoPerfil);
    }

    private void inicializarComponentes(String infoPerfil) {
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(BACKGROUND_COLOR);
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel superior con título e icono
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(CARD_COLOR);
        topPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(189, 195, 199), 1, true),
                new EmptyBorder(20, 20, 20, 20)));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(CARD_COLOR);

        JLabel iconLabel = new JLabel("👤");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(iconLabel);
        headerPanel.add(Box.createVerticalStrut(10));

        JLabel titulo = new JLabel("El Meu Perfil");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(TEXT_COLOR);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(titulo);

        JLabel subtitulo = new JLabel("Perfils generats per anàlisi de clustering");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(new Color(127, 140, 141));
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(subtitulo);

        topPanel.add(headerPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Panel central con la información del perfil
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(BACKGROUND_COLOR);
        centerPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        textAreaPerfil = new JTextArea(infoPerfil);
        textAreaPerfil.setEditable(false);
        textAreaPerfil.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textAreaPerfil.setLineWrap(true);
        textAreaPerfil.setWrapStyleWord(true);
        textAreaPerfil.setBackground(CARD_COLOR);
        textAreaPerfil.setBorder(new EmptyBorder(15, 15, 15, 15));
        textAreaPerfil.setForeground(TEXT_COLOR);

        JScrollPane scrollPane = new JScrollPane(textAreaPerfil);
        scrollPane.setBorder(new CompoundBorder(
                new LineBorder(new Color(189, 195, 199), 1, true),
                new EmptyBorder(0, 0, 0, 0)));
        scrollPane.setPreferredSize(new Dimension(550, 350));

        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Panel inferior con botón
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        bottomPanel.setBackground(BACKGROUND_COLOR);

        btnTancar = new JButton("Tancar");
        btnTancar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTancar.setForeground(Color.WHITE);
        btnTancar.setBackground(SUCCESS_COLOR);
        btnTancar.setPreferredSize(new Dimension(120, 40));
        btnTancar.setContentAreaFilled(true);
        btnTancar.setOpaque(true);
        btnTancar.setFocusPainted(false);
        btnTancar.setBorderPainted(false);
        btnTancar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTancar.addActionListener(e -> dispose());

        Color hoverColor = SUCCESS_COLOR.brighter();
        btnTancar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnTancar.setBackground(hoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btnTancar.setBackground(SUCCESS_COLOR);
            }
        });

        bottomPanel.add(btnTancar);
        add(bottomPanel, BorderLayout.SOUTH);

        // Configuración del diálogo
        pack();
        setSize(600, 550);
        setLocationRelativeTo(getParent());
        setResizable(false);
    }
}

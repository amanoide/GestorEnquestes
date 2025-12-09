package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;

public class DialogoParticipants extends JDialog {

    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color CARD_COLOR = Color.WHITE;

    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> listParticipants = new JList<>(listModel);
    private JLabel lblTotal = new JLabel();
    private JButton btnTancar = new JButton("Tancar");

    public DialogoParticipants(Frame parent, String enquestaTitol, ArrayList<String> participants) {
        super(parent, "Participants de l'enquesta", true);
        inicializarComponentes(enquestaTitol, participants);
    }

    private void inicializarComponentes(String enquestaTitol, ArrayList<String> participants) {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BACKGROUND_COLOR);
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel superior con título
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(CARD_COLOR);
        topPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(189, 195, 199), 1, true),
                new EmptyBorder(15, 15, 15, 15)));

        JLabel titulo = new JLabel("👥 Participants");
        titulo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        titulo.setForeground(TEXT_COLOR);
        topPanel.add(titulo, BorderLayout.NORTH);

        JLabel subtitulo = new JLabel("<html><i>" + enquestaTitol + "</i></html>");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(new Color(127, 140, 141));
        topPanel.add(subtitulo, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // Panel central con lista
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBackground(BACKGROUND_COLOR);
        centerPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        if (participants.isEmpty()) {
            JLabel msgEmpty = new JLabel("Encara ningú ha respost aquesta enquesta");
            msgEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            msgEmpty.setForeground(new Color(127, 140, 141));
            msgEmpty.setHorizontalAlignment(SwingConstants.CENTER);
            msgEmpty.setBorder(new EmptyBorder(50, 20, 50, 20));
            centerPanel.add(msgEmpty, BorderLayout.CENTER);
        } else {
            // Poblar lista
            for (int i = 0; i < participants.size(); i++) {
                listModel.addElement((i + 1) + ". " + participants.get(i));
            }

            listParticipants.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            listParticipants.setFixedCellHeight(35);
            listParticipants.setBackground(CARD_COLOR);
            listParticipants.setBorder(new EmptyBorder(5, 10, 5, 10));

            JScrollPane scrollPane = new JScrollPane(listParticipants);
            scrollPane.setBorder(new LineBorder(new Color(189, 195, 199), 1, true));
            centerPanel.add(scrollPane, BorderLayout.CENTER);

            // Total
            lblTotal.setText("Total: " + participants.size() + " participant(s)");
            lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblTotal.setForeground(PRIMARY_COLOR);
            lblTotal.setHorizontalAlignment(SwingConstants.CENTER);
            lblTotal.setBorder(new EmptyBorder(10, 0, 0, 0));
            centerPanel.add(lblTotal, BorderLayout.SOUTH);
        }

        add(centerPanel, BorderLayout.CENTER);

        // Botón Tancar
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(BACKGROUND_COLOR);

        btnTancar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTancar.setForeground(Color.WHITE);
        btnTancar.setBackground(new Color(149, 165, 166));
        btnTancar.setPreferredSize(new Dimension(120, 35));
        btnTancar.setContentAreaFilled(true);
        btnTancar.setOpaque(true);
        btnTancar.setFocusPainted(false);
        btnTancar.setBorderPainted(false);
        btnTancar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTancar.addActionListener(e -> dispose());

        bottomPanel.add(btnTancar);
        add(bottomPanel, BorderLayout.SOUTH);

        // Configuración del diálogo
        setSize(450, 500);
        setLocationRelativeTo(getParent());
        setResizable(false);
    }
}

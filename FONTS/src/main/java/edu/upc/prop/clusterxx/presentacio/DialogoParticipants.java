package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Diàleg modal encarregat de presentar la llista d'usuaris que han participat
 * en una enquesta.
 * 
 * Aquesta classe mostra una finestra emergent amb un llistat numerat de noms
 * d'usuari
 * i un resum del total de participants. És útil per monitoritzar la
 * participació i
 * verificar qui ha respost a una enquesta específica.
 * 
 * Gestiona dos estats principals:
 * Informar que no hi ha cap participant si la llista està buida.
 * Mostrar la llista detallada amb scroll si hi ha respostes.
 */
public class DialogoParticipants extends JDialog {

    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> listParticipants = new JList<>(listModel);
    private JLabel lblTotal = new JLabel();
    private JButton btnTancar = new JButton("Tancar");

    /**
     * Constructor de la classe DialogoParticipants.
     * 
     * Inicialitza el diàleg, configura les seves propietats com a finestra modal
     * i construeix la interfície gràfica amb les dades proporcionades.
     *
     * @param parent        Finestra (Frame) propietària del diàleg, bloquejada
     *                      mentre aquest està obert.
     * @param enquestaTitol El títol de l'enquesta, utilitzat per donar context a la
     *                      llista.
     * @param participants  Col·lecció (ArrayList) amb els noms dels usuaris que han
     *                      participat.
     */
    public DialogoParticipants(Frame parent, String enquestaTitol, ArrayList<String> participants) {
        super(parent, "Participants de l'enquesta", true);
        inicializarComponentes(enquestaTitol, participants);
    }

    /**
     * Construeix i organitza tots els components visuals del diàleg.
     * 
     * Defineix un disseny de tres parts:
     * Capçalera: Mostra el títol "Participants" i el nom de l'enquesta.
     * Cos central: Mostra la llista d'usuaris o un missatge si està buida.
     * Peu: Conté el botó per tancar la finestra i el comptador total.
     * 
     * @param enquestaTitol Títol de l'enquesta a mostrar a la capçalera.
     * @param participants  Llista de noms d'usuari per omplir el component JList.
     */
    private void inicializarComponentes(String enquestaTitol, ArrayList<String> participants) {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(UIStyles.BACKGROUND_COLOR);
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel superior con título
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(UIStyles.CARD_COLOR);
        topPanel.setBorder(new CompoundBorder(
                new LineBorder(UIStyles.BORDER_COLOR, 1, true),
                new EmptyBorder(15, 15, 15, 15)));

        JLabel titulo = new JLabel("👥 Participants");
        titulo.setFont(UIStyles.FONT_ENQUESTA_TITLE);
        titulo.setForeground(UIStyles.TEXT_COLOR);
        topPanel.add(titulo, BorderLayout.NORTH);

        JLabel subtitulo = new JLabel("<html><i>" + enquestaTitol + "</i></html>");
        subtitulo.setFont(UIStyles.FONT_DIALOG_SUBTITLE);
        subtitulo.setForeground(UIStyles.TEXT_SECONDARY);
        topPanel.add(subtitulo, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // Panel central con lista
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBackground(UIStyles.BACKGROUND_COLOR);
        centerPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        if (participants.isEmpty()) {
            JLabel msgEmpty = new JLabel("Encara ningú ha respost aquesta enquesta");
            msgEmpty.setFont(UIStyles.FONT_EMPTY_MESSAGE);
            msgEmpty.setForeground(UIStyles.TEXT_SECONDARY);
            msgEmpty.setHorizontalAlignment(SwingConstants.CENTER);
            msgEmpty.setBorder(new EmptyBorder(50, 20, 50, 20));
            centerPanel.add(msgEmpty, BorderLayout.CENTER);
        } else {
            // Poblar lista
            for (int i = 0; i < participants.size(); i++) {
                listModel.addElement((i + 1) + ". " + participants.get(i));
            }

            listParticipants.setFont(UIStyles.FONT_NORMAL);
            listParticipants.setFixedCellHeight(35);
            listParticipants.setBackground(UIStyles.CARD_COLOR);
            listParticipants.setBorder(new EmptyBorder(5, 10, 5, 10));

            JScrollPane scrollPane = new JScrollPane(listParticipants);
            scrollPane.setBorder(new LineBorder(UIStyles.BORDER_COLOR, 1, true));
            centerPanel.add(scrollPane, BorderLayout.CENTER);

            // Total
            lblTotal.setText("Total: " + participants.size() + " participant(s)");
            lblTotal.setFont(UIStyles.FONT_STATS);
            lblTotal.setForeground(UIStyles.PRIMARY_COLOR);
            lblTotal.setHorizontalAlignment(SwingConstants.CENTER);
            lblTotal.setBorder(new EmptyBorder(10, 0, 0, 0));
            centerPanel.add(lblTotal, BorderLayout.SOUTH);
        }

        add(centerPanel, BorderLayout.CENTER);

        // Botón Tancar
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(UIStyles.BACKGROUND_COLOR);

        btnTancar.setFont(UIStyles.FONT_BUTTON_PRIMARY);
        btnTancar.setForeground(Color.WHITE);
        btnTancar.setBackground(UIStyles.SECONDARY_COLOR);
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

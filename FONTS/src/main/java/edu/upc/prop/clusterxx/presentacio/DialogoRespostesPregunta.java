package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

/**
 * Diàleg que mostra totes les respostes d'una pregunta específica.
 * 
 * Aquest diàleg permet visualitzar qui ha respost una pregunta concreta i quin
 * text ha proporcionat cada usuari. És útil per analitzar les respostes
 * individuals d'una pregunta dins d'una enquesta.
 * 
 * Funcionalitats:
 * 
 * Mostra el nombre total de respostes rebudes.
 * Lista cada resposta amb l'usuari que l'ha donat.
 * Indica quan no hi ha respostes disponibles.
 * Interfície amb scroll per acomodar moltes respostes.
 * 
 */
public class DialogoRespostesPregunta extends JDialog {
    /** Controlador de presentació per obtenir les dades de respostes. */
    private CtrlPresentacio ctrlPresentacio;
    /** Identificador de la pregunta de la qual es mostren les respostes. */
    private String idPregunta;

    /**
     * Constructor del diàleg de visualització de respostes d'una pregunta.
     * 
     * Inicialitza el diàleg, carrega totes les respostes de la pregunta i
     * construeix la interfície visual.
     *
     * @param parent     Diàleg propietari d'aquest diàleg.
     * @param ctrl       Controlador de presentació per obtenir les respostes.
     * @param idPregunta Identificador de la pregunta a consultar.
     */
    public DialogoRespostesPregunta(Dialog parent, CtrlPresentacio ctrl, String idPregunta) {
        super(parent, "Respostes de la Pregunta: " + idPregunta, true);
        this.ctrlPresentacio = ctrl;
        this.idPregunta = idPregunta;

        inicialitzarComponents();
        setSize(600, 400);
        setLocationRelativeTo(parent);
    }

    /**
     * Inicialitza i configura tots els components gràfics del diàleg.
     * 
     * Crea un panel amb scroll que conté:
     * 
     * Comptador del total de respostes rebudes.
     * Llista de targetes amb cada resposta (usuari + text).
     * Missatge informatiu si no hi ha respostes.
     * Botó de tancament.
     * 
     * 
     * Cada resposta es mostra en una targeta independent amb el nom de l'usuari
     * en negreta i el text de la resposta.
     */
    private void inicialitzarComponents() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(UIStyles.BACKGROUND_COLOR);

        // Títol
        JLabel titulo = new JLabel("Respostes de la pregunta");
        titulo.setFont(UIStyles.FONT_SECTION);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBorder(new EmptyBorder(15, 10, 15, 10));
        add(titulo, BorderLayout.NORTH);

        // Panel principal amb scroll
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(UIStyles.BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        // Obtenir les respostes
        ArrayList<ArrayList<String>> respostes = ctrlPresentacio.consultarRespostesPregunta(idPregunta);

        if (respostes.isEmpty()) {
            JLabel lblNoRespostes = new JLabel("Aquesta pregunta encara no té respostes.");
            lblNoRespostes.setFont(UIStyles.FONT_NORMAL);
            lblNoRespostes.setForeground(UIStyles.TEXT_SECONDARY);
            lblNoRespostes.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(Box.createVerticalStrut(20));
            mainPanel.add(lblNoRespostes);
        } else {
            // Mostrar el número de respostes
            JLabel lblTotal = new JLabel("Total de respostes: " + respostes.size());
            lblTotal.setFont(UIStyles.FONT_STATS);
            lblTotal.setForeground(UIStyles.PRIMARY_COLOR);
            lblTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
            mainPanel.add(lblTotal);
            mainPanel.add(Box.createVerticalStrut(10));

            // Mostrar cada resposta
            for (ArrayList<String> resposta : respostes) {
                JPanel respostaPanel = new JPanel(new BorderLayout(10, 5));
                respostaPanel.setBackground(UIStyles.RESPONSE_BACKGROUND);
                respostaPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UIStyles.BORDER_LIGHT, 1),
                        new EmptyBorder(8, 12, 8, 12)));
                respostaPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
                respostaPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

                // Usuari
                JLabel lblUsuari = new JLabel(resposta.get(0));
                lblUsuari.setFont(UIStyles.FONT_NORMAL.deriveFont(Font.BOLD));
                lblUsuari.setForeground(UIStyles.TEXT_COLOR);

                // Text de la resposta
                JLabel lblText = new JLabel(resposta.get(1));
                lblText.setFont(UIStyles.FONT_NORMAL);
                lblText.setForeground(UIStyles.TEXT_COLOR);

                respostaPanel.add(lblUsuari, BorderLayout.WEST);
                respostaPanel.add(lblText, BorderLayout.CENTER);

                mainPanel.add(respostaPanel);
                mainPanel.add(Box.createVerticalStrut(5));
            }
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // Botó tancar
        JPanel panelBotons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBotons.setBackground(UIStyles.BACKGROUND_COLOR);

        JButton btnTancar = UIComponents.createColorButton("✖ Tancar", UIStyles.SECONDARY_COLOR);
        btnTancar.addActionListener(e -> dispose());
        panelBotons.add(btnTancar);

        add(panelBotons, BorderLayout.SOUTH);
    }
}

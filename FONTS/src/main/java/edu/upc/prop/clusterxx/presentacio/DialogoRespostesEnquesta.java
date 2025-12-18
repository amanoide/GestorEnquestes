package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import edu.upc.prop.clusterxx.domini.classes.Resposta;

/**
 * Diàleg que mostra totes les respostes d'una enquesta agrupades per pregunta.
 * Només accessible pel creador de l'enquesta.
 */
public class DialogoRespostesEnquesta extends JDialog {
    private CtrlPresentacio ctrlPresentacio;
    private String idEnquesta;

    public DialogoRespostesEnquesta(Frame parent, CtrlPresentacio ctrl, String idEnquesta) {
        super(parent, "Respostes de l'Enquesta: " + idEnquesta, true);
        this.ctrlPresentacio = ctrl;
        this.idEnquesta = idEnquesta;

        inicialitzarComponents();
        pack();
        setLocationRelativeTo(parent);
    }

    private void inicialitzarComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIStyles.BACKGROUND_COLOR);

        // Panel principal con scroll
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(UIStyles.BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Obtenir totes les respostes
        HashMap<String, ArrayList<Resposta>> respostesPerPregunta = ctrlPresentacio
                .consultarRespostesEnquesta(idEnquesta);

        if (respostesPerPregunta.isEmpty()) {
            JLabel lblNoRespostes = new JLabel("Aquesta enquesta encara no té respostes.");
            lblNoRespostes.setFont(UIStyles.FONT_NORMAL);
            lblNoRespostes.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(lblNoRespostes);
        } else {
            // Obtenir les preguntes de l'enquesta
            ArrayList<ArrayList<Object>> preguntes = ctrlPresentacio.getPreguntesEnquestaRaw(idEnquesta);

            if (preguntes != null && !preguntes.isEmpty()) {
                // Per cada pregunta de l'enquesta
                for (ArrayList<Object> pregunta : preguntes) {
                    String idPregunta = (String) pregunta.get(0);
                    ArrayList<Resposta> respostes = respostesPerPregunta.get(idPregunta);

                    // Panel per aquesta pregunta
                    JPanel preguntaPanel = new JPanel();
                    preguntaPanel.setLayout(new BoxLayout(preguntaPanel, BoxLayout.Y_AXIS));
                    preguntaPanel.setBackground(Color.WHITE);
                    preguntaPanel.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(UIStyles.PRIMARY_COLOR, 2),
                            new EmptyBorder(10, 10, 10, 10)));
                    preguntaPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

                    // Títol de la pregunta
                    JLabel lblPregunta = new JLabel("Pregunta: " + pregunta.get(1));
                    lblPregunta.setFont(UIStyles.FONT_SUBTITLE);
                    lblPregunta.setForeground(UIStyles.PRIMARY_COLOR);
                    lblPregunta.setAlignmentX(Component.LEFT_ALIGNMENT);
                    preguntaPanel.add(lblPregunta);

                    preguntaPanel.add(Box.createVerticalStrut(10));

                    // Mostrar totes les respostes a aquesta pregunta
                    if (respostes != null && !respostes.isEmpty()) {
                        JLabel lblNumRespostes = new JLabel(respostes.size() + " resposta(es):");
                        lblNumRespostes.setFont(UIStyles.FONT_NORMAL);
                        lblNumRespostes.setAlignmentX(Component.LEFT_ALIGNMENT);
                        preguntaPanel.add(lblNumRespostes);

                        preguntaPanel.add(Box.createVerticalStrut(5));

                        for (Resposta resposta : respostes) {
                            JPanel respostaPanel = new JPanel(new BorderLayout(5, 5));
                            respostaPanel.setBackground(UIStyles.RESPONSE_BACKGROUND);
                            respostaPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
                            respostaPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

                            JLabel lblUsuari = new JLabel(resposta.getUsernameUsuari() + ":");
                            lblUsuari.setFont(UIStyles.FONT_NORMAL.deriveFont(Font.BOLD));

                            JLabel lblText = new JLabel(resposta.getTextResposta());
                            lblText.setFont(UIStyles.FONT_NORMAL);

                            respostaPanel.add(lblUsuari, BorderLayout.WEST);
                            respostaPanel.add(lblText, BorderLayout.CENTER);

                            preguntaPanel.add(respostaPanel);
                            preguntaPanel.add(Box.createVerticalStrut(3));
                        }
                    } else {
                        JLabel lblNoRespostes = new JLabel("Cap resposta encara.");
                        lblNoRespostes.setFont(UIStyles.FONT_NORMAL);
                        lblNoRespostes.setForeground(Color.GRAY);
                        lblNoRespostes.setAlignmentX(Component.LEFT_ALIGNMENT);
                        preguntaPanel.add(lblNoRespostes);
                    }

                    mainPanel.add(preguntaPanel);
                    mainPanel.add(Box.createVerticalStrut(15));
                }
            }
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setPreferredSize(new Dimension(700, 500));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Botó tancar
        JPanel panelBotons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBotons.setBackground(UIStyles.BACKGROUND_COLOR);

        JButton btnTancar = UIComponents.createColorButton("Tancar", UIStyles.SECONDARY_COLOR);
        btnTancar.addActionListener(e -> dispose());
        panelBotons.add(btnTancar);

        add(panelBotons, BorderLayout.SOUTH);
    }
}

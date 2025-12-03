package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import edu.upc.prop.clusterxx.domini.classes.Pregunta;

public class DialogoGestionarRespostes extends JDialog {
    private CtrlPresentacio iCtrlPresentacio;
    private String idEnquesta;
    private ArrayList<Pregunta> preguntes;
    private HashMap<String, String> respostesUsuari;

    private JPanel panelContent;

    public DialogoGestionarRespostes(Frame owner, CtrlPresentacio ctrlPresentacio, String idEnquesta) {
        super(owner, "Gestionar Respostes - " + idEnquesta, true);
        this.iCtrlPresentacio = ctrlPresentacio;
        this.idEnquesta = idEnquesta;
        cargarDatos();
        inicializar();
    }

    private void cargarDatos() {
        this.preguntes = iCtrlPresentacio.getPreguntesEnquestaObjects(idEnquesta);
        this.respostesUsuari = iCtrlPresentacio.getRespostesUsuariEnquesta(idEnquesta);
    }

    private void inicializar() {
        setLayout(new BorderLayout());
        setSize(700, 600);
        setLocationRelativeTo(getOwner());

        panelContent = new JPanel();
        panelContent.setLayout(new BoxLayout(panelContent, BoxLayout.Y_AXIS));
        panelContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        refrescarPanelContent();

        JScrollPane scroll = new JScrollPane(panelContent);
        add(scroll, BorderLayout.CENTER);

        JPanel panelBotons = new JPanel();
        JButton btnEsborrarTot = new JButton("Esborrar Totes les Respostes");
        btnEsborrarTot.setForeground(Color.RED);
        JButton btnTancar = new JButton("Tancar");

        btnEsborrarTot.addActionListener(e -> esborrarTotesRespostes());
        btnTancar.addActionListener(e -> setVisible(false));

        panelBotons.add(btnEsborrarTot);
        panelBotons.add(btnTancar);
        add(panelBotons, BorderLayout.SOUTH);
    }

    private void refrescarPanelContent() {
        panelContent.removeAll();

        if (respostesUsuari.isEmpty()) {
            panelContent.add(new JLabel("No has respost aquesta enquesta."));
        } else {
            for (Pregunta p : preguntes) {
                if (respostesUsuari.containsKey(p.getId())) {
                    JPanel panelPregunta = new JPanel(new BorderLayout());
                    panelPregunta.setBorder(BorderFactory.createTitledBorder(p.getText()));
                    panelPregunta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

                    String respostaActual = respostesUsuari.get(p.getId());
                    JLabel lblResposta = new JLabel("<html><b>La teva resposta:</b> " + respostaActual + "</html>");
                    lblResposta.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                    JButton btnModificar = new JButton("Modificar");
                    btnModificar.addActionListener(e -> modificarResposta(p));

                    panelPregunta.add(lblResposta, BorderLayout.CENTER);
                    panelPregunta.add(btnModificar, BorderLayout.EAST);

                    panelContent.add(panelPregunta);
                    panelContent.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }
        }

        panelContent.revalidate();
        panelContent.repaint();
    }

    private void modificarResposta(Pregunta p) {
        String novaResposta = JOptionPane.showInputDialog(this,
                "Introdueix la nova resposta per a: " + p.getText() + "\n" +
                        "(Format: " + p.getTipus() + ")",
                respostesUsuari.get(p.getId()));

        if (novaResposta != null && !novaResposta.trim().isEmpty()) {
            String resultat = iCtrlPresentacio.modificarResposta(idEnquesta, p.getId(), novaResposta);
            JOptionPane.showMessageDialog(this, resultat);
            if (resultat.contains("correctament")) {
                cargarDatos();
                refrescarPanelContent();
            }
        }
    }

    private void esborrarTotesRespostes() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Estàs segur que vols esborrar TOTES les teves respostes a aquesta enquesta?\nAquesta acció no es pot desfer.",
                "Confirmar esborrat",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String resultat = iCtrlPresentacio.esborrarRespostesEnquesta(idEnquesta);
            JOptionPane.showMessageDialog(this, resultat);
            if (resultat.contains("correctament")) {
                setVisible(false);
            }
        }
    }
}

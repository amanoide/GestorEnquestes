package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import edu.upc.prop.clusterxx.domini.classes.Pregunta;
import edu.upc.prop.clusterxx.domini.classes.Opcio;
import edu.upc.prop.clusterxx.domini.classes.TipusPregunta;

public class DialogoResponderEnquesta extends JDialog {
    private CtrlPresentacio iCtrlPresentacio;
    private String idEnquesta;
    private ArrayList<Pregunta> preguntes;

    // Map per guardar els components d'entrada per recuperar els valors després
    private Map<String, JComponent> inputComponents = new HashMap<>();

    public DialogoResponderEnquesta(Frame owner, CtrlPresentacio ctrlPresentacio, String idEnquesta) {
        super(owner, "Respondre Enquesta - " + idEnquesta, true);
        this.iCtrlPresentacio = ctrlPresentacio;
        this.idEnquesta = idEnquesta;
        this.preguntes = iCtrlPresentacio.getPreguntesEnquestaObjects(idEnquesta);
        inicializar();
    }

    private void inicializar() {
        setLayout(new BorderLayout());
        setSize(600, 500);
        setLocationRelativeTo(getOwner());

        JPanel panelContent = new JPanel();
        panelContent.setLayout(new BoxLayout(panelContent, BoxLayout.Y_AXIS));
        panelContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (Pregunta p : preguntes) {
            JPanel panelPregunta = new JPanel(new BorderLayout());
            panelPregunta.setBorder(BorderFactory.createTitledBorder(p.getText()));
            panelPregunta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100)); // Altura fixa aprox

            JComponent input = crearComponentInput(p);

            // Afegir etiqueta amb instruccions
            String instruccions = "";
            switch (p.getTipus()) {
                case NUMERICA:
                    instruccions = "[Min: " + p.getValorMinim() + ", Max: " + p.getValorMaxim() + "]";
                    break;
                case TEXT_LLIURE:
                    instruccions = "[Text lliure]";
                    break;
                case QUALITATIVA_ORDENADA:
                case QUALITATIVA_NO_ORDENADA_SIMPLE:
                    instruccions = "[Selecciona una opció]";
                    break;
                case QUALITATIVA_NO_ORDENADA_MULTIPLE:
                    instruccions = "[Selecciona múltiples opcions (Max: " + p.getMaxSeleccions() + ")]";
                    break;
            }

            JLabel lblInstruccions = new JLabel(instruccions);
            lblInstruccions.setForeground(Color.GRAY);
            lblInstruccions.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));

            panelPregunta.add(lblInstruccions, BorderLayout.NORTH);
            panelPregunta.add(input, BorderLayout.CENTER);

            inputComponents.put(p.getId(), input);
            panelContent.add(panelPregunta);
            panelContent.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JScrollPane scroll = new JScrollPane(panelContent);
        add(scroll, BorderLayout.CENTER);

        JPanel panelBotons = new JPanel();
        JButton btnEnviar = new JButton("Enviar Respostes");
        JButton btnCancel = new JButton("Cancel·lar");

        btnEnviar.addActionListener(e -> enviarRespostes());
        btnCancel.addActionListener(e -> setVisible(false));

        panelBotons.add(btnEnviar);
        panelBotons.add(btnCancel);
        add(panelBotons, BorderLayout.SOUTH);
    }

    private JComponent crearComponentInput(Pregunta p) {
        TipusPregunta tp = p.getTipus();
        switch (tp) {
            case NUMERICA:
                SpinnerNumberModel model = new SpinnerNumberModel(p.getValorMinim(), p.getValorMinim(),
                        p.getValorMaxim(), Double.valueOf(1.0));
                return new JSpinner(model);

            case TEXT_LLIURE:
                return new JTextField();

            case QUALITATIVA_ORDENADA:
            case QUALITATIVA_NO_ORDENADA_SIMPLE:
                JComboBox<String> combo = new JComboBox<>();
                for (Opcio o : p.getOpcions()) {
                    combo.addItem(o.getText());
                }
                return combo;

            case QUALITATIVA_NO_ORDENADA_MULTIPLE:
                // Per múltiple, usem un panell amb checkboxes
                JPanel panelChecks = new JPanel(new GridLayout(0, 1));
                // Guardem referència al panell, però necessitarem iterar els seus fills
                // Per simplificar, guardarem el panell com a component
                // I afegirem una propietat client per saber que és múltiple
                panelChecks.putClientProperty("isMultiple", true);
                for (Opcio o : p.getOpcions()) {
                    JCheckBox cb = new JCheckBox(o.getText());
                    cb.setName(String.valueOf(o.getId())); // Guardem ID al nom per recuperar-lo
                    panelChecks.add(cb);
                }
                return panelChecks;

            default:
                return new JLabel("Tipus no suportat");
        }
    }

    private void enviarRespostes() {
        HashMap<String, String> respostes = new HashMap<>();

        for (Map.Entry<String, JComponent> entry : inputComponents.entrySet()) {
            String idPregunta = entry.getKey();
            JComponent comp = entry.getValue();
            String valor = obtenerValor(comp);

            if (valor == null || valor.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Si us plau, respon totes les preguntes.");
                return;
            }
            respostes.put(idPregunta, valor);
        }

        String resultat = iCtrlPresentacio.contestarEnquesta(idEnquesta, respostes);
        JOptionPane.showMessageDialog(this, resultat);

        if (resultat.contains("correctament")) {
            setVisible(false);
        }
    }

    private String obtenerValor(JComponent comp) {
        if (comp instanceof JSpinner) {
            return String.valueOf(((JSpinner) comp).getValue());
        } else if (comp instanceof JTextField) {
            return ((JTextField) comp).getText();
        } else if (comp instanceof JComboBox) {
            return (String) ((JComboBox<?>) comp).getSelectedItem();
        } else if (comp instanceof JPanel && Boolean.TRUE.equals(comp.getClientProperty("isMultiple"))) {
            JPanel panel = (JPanel) comp;
            StringBuilder sb = new StringBuilder();
            for (Component c : panel.getComponents()) {
                if (c instanceof JCheckBox) {
                    JCheckBox cb = (JCheckBox) c;
                    if (cb.isSelected()) {
                        if (sb.length() > 0)
                            sb.append(",");
                        // Necessitem l'ID de l'opció per al format esperat pel domini?
                        // El driver usa IDs (0,1,2) separats per comes.
                        // Aquí hem de mapejar el text a l'ID o usar l'índex.
                        // Per simplificar, assumim que el domini accepta el text o l'ID.
                        // Re-mirant el driver: "Escull opcions (IDs ex: 0,1,2)"
                        // Així que necessitem els IDs.
                        // En crearComponentInput, no tenim fàcil accés als IDs originals de les opcions
                        // si només guardem el text al checkbox.
                        // Millor recuperar l'ID.
                        // Anem a assumir que el text del checkbox és suficient O que hem de buscar
                        // l'ID.
                        // Per ara enviem el text i veiem si falla, o millor, enviem l'ID si podem.
                        // En el loop de creació: cb.setActionCommand(String.valueOf(o.getId()));
                        // Però JCheckBox no té actionCommand visible fàcilment sense listener.
                        // Usem el text per ara, si falla, ho arreglarem.
                        // EDIT: El driver diu "Escull opcions (IDs ex: 0,1,2)".
                        // El mètode contestarEnquesta rep un String.
                        // Si el domini espera IDs, hem d'enviar IDs.
                        // Tornem a mirar com recuperar l'ID.
                        // En el loop de creació, podem posar l'ID al nom del component o similar.
                        // Però esperem, el driver diu: respostes.put(p.getId(), opcionsEsc); on
                        // opcionsEsc són "0,1,2".
                        // Així que sí, necessitem els IDs.
                        // Modificaré crearComponentInput per posar l'ID al name del checkbox.
                        if (c.getName() != null) {
                            sb.append(c.getName());
                        }
                    }
                }
            }
            return sb.toString();
        }
        return "";
    }

    // Sobreescrivim per arreglar lo dels IDs en multiple
    // Aquesta classe interna o mètode privat hauria de ser més net, però per ara ho
    // fem així.
}

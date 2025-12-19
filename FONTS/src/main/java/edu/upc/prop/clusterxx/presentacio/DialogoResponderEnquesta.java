package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Diàleg interactiu per permetre a l'usuari respondre a una enquesta.
 * 
 * Aquesta classe és clau en la interacció de l'usuari, ja que genera
 * dinàmicament
 * els camps del formulari basant-se en les definicions de les preguntes de
 * l'enquesta.
 * 
 * Funcionalitats:
 * Generació dinàmica d'inputs (Spinners per numèriques, TextFields per text,
 * ComboBoxes i CheckBoxes per opcions).
 * Validació de respostes obligatòries abans de l'enviament.
 * Recollida i empaquetament de respostes per enviar-les al controlador.
 */
public class DialogoResponderEnquesta extends JDialog {

    private CtrlPresentacio iCtrlPresentacio;
    private String idEnquesta;
    private ArrayList<ArrayList<Object>> preguntes;

    // Map per guardar els components d'entrada per recuperar els valors després
    private Map<String, JComponent> inputComponents = new HashMap<>();

    /**
     * Constructor del diàleg de resposta d'enquestes.
     * 
     * Inicialitza el diàleg, carrega les preguntes de l'enquesta especificada
     * mitjançant
     * el controlador i construeix la interfície dinàmica.
     *
     * @param owner           Finestra propietària del diàleg.
     * @param ctrlPresentacio Instància del controlador per recuperar preguntes i
     *                        guardar respostes.
     * @param idEnquesta      Identificador únic de l'enquesta que es vol respondre.
     */
    public DialogoResponderEnquesta(Frame owner, CtrlPresentacio ctrlPresentacio, String idEnquesta) {
        super(owner, "Respondre Enquesta - " + idEnquesta, true);
        this.iCtrlPresentacio = ctrlPresentacio;
        this.idEnquesta = idEnquesta;
        this.preguntes = iCtrlPresentacio.getPreguntesEnquestaRaw(idEnquesta);
        inicializar();
    }

    /**
     * Mètode principal de construcció de la interfície.
     * 
     * Crea el layout del diàleg amb tres seccions:
     * 
     * Adalt: Títol de l'enquesta.
     * Mig: Panel amb scroll que conté els camps de formulari generats
     * dinàmicament.
     * Abaix: Botons d'acció (Enviar i Cancel·lar).
     * 
     * 
     * Genera dinàmicament un component d'entrada per cada pregunta de l'enquesta.
     */
    private void inicializar() {
        setLayout(new BorderLayout(10, 10));
        setSize(650, 550);
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(UIStyles.BACKGROUND_COLOR);
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));

        // Título
        JLabel titulo = new JLabel("📝 Respondre Enquesta: " + idEnquesta);
        titulo.setFont(UIStyles.FONT_ENQUESTA_TITLE);
        titulo.setForeground(UIStyles.TEXT_COLOR);
        add(titulo, BorderLayout.NORTH);

        JPanel panelContent = new JPanel();
        panelContent.setLayout(new BoxLayout(panelContent, BoxLayout.Y_AXIS));
        panelContent.setBackground(UIStyles.BACKGROUND_COLOR);
        panelContent.setBorder(new EmptyBorder(10, 5, 10, 5));

        // [0] ID (String), [1] Text (String), [2] Tipus (String), [3] Min (Double),
        // [4] Max (Double), [5] Opcions (ArrayList<ArrayList<String>>), [6] MaxSel
        // (Integer)
        for (ArrayList<Object> p : preguntes) {
            JPanel panelPregunta = new JPanel(new BorderLayout(5, 5));
            panelPregunta.setBackground(UIStyles.CARD_COLOR);
            panelPregunta.setBorder(new CompoundBorder(
                    new LineBorder(UIStyles.BORDER_LIGHT, 1, true),
                    new EmptyBorder(12, 12, 12, 12)));
            panelPregunta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

            JComponent input = crearComponentInput(p);

            String tipus = (String) p.get(2);
            String text = (String) p.get(1);
            String id = (String) p.get(0);

            // Pregunta con emoji según tipo
            String emoji = getEmojiTipus(tipus);
            JLabel lblPregunta = new JLabel(emoji + " " + text);
            lblPregunta.setFont(UIStyles.FONT_PREGUNTA_LABEL);
            lblPregunta.setForeground(UIStyles.TEXT_COLOR);

            String instruccions = getInstruccions(p);
            JLabel lblInstruccions = new JLabel(instruccions);
            lblInstruccions.setFont(UIStyles.FONT_INSTRUCTIONS);
            lblInstruccions.setForeground(UIStyles.TEXT_SECONDARY);

            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(UIStyles.CARD_COLOR);
            headerPanel.add(lblPregunta, BorderLayout.NORTH);
            headerPanel.add(lblInstruccions, BorderLayout.SOUTH);

            panelPregunta.add(headerPanel, BorderLayout.NORTH);
            panelPregunta.add(input, BorderLayout.CENTER);

            inputComponents.put(id, input);
            panelContent.add(panelPregunta);
            panelContent.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JScrollPane scroll = new JScrollPane(panelContent);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UIStyles.BACKGROUND_COLOR);
        add(scroll, BorderLayout.CENTER);

        JPanel panelBotons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotons.setBackground(UIStyles.BACKGROUND_COLOR);

        JButton btnEnviar = UIComponents.createColorButton("✅ Enviar Respostes", UIStyles.SUCCESS_COLOR);
        JButton btnCancel = UIComponents.createColorButton("✖ Cancel·lar", UIStyles.SECONDARY_COLOR);

        btnEnviar.addActionListener(e -> enviarRespostes());
        btnCancel.addActionListener(e -> setVisible(false));

        panelBotons.add(btnEnviar);
        panelBotons.add(btnCancel);
        add(panelBotons, BorderLayout.SOUTH);
    }

    /**
     * Obté l'emoji corresponent segons el tipus de pregunta.
     * 
     * @param tp Tipus de pregunta (NUMERICA, TEXT_LLIURE, QUALITATIVA_*).
     * @return Emoji representatiu del tipus de pregunta.
     */
    private String getEmojiTipus(String tp) {
        if ("NUMERICA".equals(tp))
            return "🔢";
        if ("TEXT_LLIURE".equals(tp))
            return "✏️";
        if ("QUALITATIVA_ORDENADA".equals(tp) || "QUALITATIVA_NO_ORDENADA_SIMPLE".equals(tp))
            return "📋";
        if ("QUALITATIVA_NO_ORDENADA_MULTIPLE".equals(tp))
            return "☑️";
        return "❓";
    }

    /**
     * Genera el text d'instruccions específic per a cada tipus de pregunta.
     * 
     * Mostra informació útil per a l'usuari com rangs numèrics, nombre màxim de
     * seleccions, etc.
     * 
     * @param p ArrayList amb les dades de la pregunta ([0]=ID, [1]=Text,
     *          [2]=Tipus, etc.).
     * @return Text d'instruccions per a la pregunta.
     */
    private String getInstruccions(ArrayList<Object> p) {
        String tipus = (String) p.get(2);
        if ("NUMERICA".equals(tipus)) {
            return "Valor entre " + p.get(3) + " i " + p.get(4);
        }
        if ("TEXT_LLIURE".equals(tipus))
            return "Text lliure";
        if ("QUALITATIVA_ORDENADA".equals(tipus) || "QUALITATIVA_NO_ORDENADA_SIMPLE".equals(tipus))
            return "Selecciona una opció";
        if ("QUALITATIVA_NO_ORDENADA_MULTIPLE".equals(tipus))
            return "Selecciona fins a " + p.get(6) + " opcions";
        return "";
    }

    /**
     * Crea el component d'entrada adequat segons el tipus de pregunta.
     * 
     * Tipus de components generats:
     * 
     * NUMERICA: JSpinner amb rang mínim-màxim.
     * TEXT_LLIURE: JTextField.
     * QUALITATIVA_ORDENADA/SIMPLE: JComboBox amb opcions.
     * QUALITATIVA_MULTIPLE: JPanel amb JCheckBox per cada opció.
     * 
     * 
     * @param p ArrayList amb les dades de la pregunta ([0]=ID, [1]=Text,
     *          [2]=Tipus, [3]=Min, [4]=Max, [5]=Opcions, [6]=MaxSel).
     * @return Component d'entrada configurat per a la pregunta.
     */
    private JComponent crearComponentInput(ArrayList<Object> p) {
        String tp = (String) p.get(2);

        if ("NUMERICA".equals(tp)) {
            Double min = (Double) p.get(3);
            Double max = (Double) p.get(4);
            SpinnerNumberModel model = new SpinnerNumberModel(min, min, max, Double.valueOf(1.0));
            JSpinner spinner = new JSpinner(model);
            spinner.setFont(UIStyles.FONT_INPUT);
            return spinner;
        }

        if ("TEXT_LLIURE".equals(tp)) {
            JTextField textField = new JTextField();
            textField.setFont(UIStyles.FONT_INPUT);
            textField.setBorder(new CompoundBorder(
                    new LineBorder(UIStyles.BORDER_MEDIUM, 1, true),
                    new EmptyBorder(8, 10, 8, 10)));
            return textField;
        }

        if ("QUALITATIVA_ORDENADA".equals(tp) || "QUALITATIVA_NO_ORDENADA_SIMPLE".equals(tp)) {
            JComboBox<String> combo = new JComboBox<>();
            combo.setFont(UIStyles.FONT_INPUT);
            ArrayList<ArrayList<String>> opcions = (ArrayList<ArrayList<String>>) p.get(5);
            for (ArrayList<String> o : opcions) {
                combo.addItem(o.get(1)); // Text
            }
            return combo;
        }

        if ("QUALITATIVA_NO_ORDENADA_MULTIPLE".equals(tp)) {
            JPanel panelChecks = new JPanel(new GridLayout(0, 2, 5, 5));
            panelChecks.setBackground(UIStyles.CARD_COLOR);
            panelChecks.putClientProperty("isMultiple", true);
            ArrayList<ArrayList<String>> opcions = (ArrayList<ArrayList<String>>) p.get(5);
            for (ArrayList<String> o : opcions) {
                JCheckBox cb = new JCheckBox(o.get(1)); // Text
                cb.setFont(UIStyles.FONT_CHECKBOX);
                cb.setBackground(UIStyles.CARD_COLOR);
                cb.setName(o.get(0)); // ID
                panelChecks.add(cb);
            }
            return panelChecks;
        }

        return new JLabel("Tipus no suportat");
    }

    /**
     * Processa l'enviament del formulari.
     * 
     * Recorre tots els components d'entrada generats, extreu el valor seleccionat
     * o introduït per l'usuari i realitza:
     * Validació: Comprova que no hi hagi respostes buides.
     * Recollida: Emmagatzema la resposta al mapa de resultats.
     * Enviament: Transmet les dades al controlador.
     * Feedback: Mostra el resultat de l'operació i tanca el diàleg si és exitós.
     */
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

    /**
     * Mètode auxiliar polimòrfic per extreure el valor en text de qualsevol
     * component d'entrada suportat.
     * 
     * Gestiona la lògica específica per obtenir dades de JSpinner, JTextField,
     * JComboBox i
     * grups de JCheckBox (construint una cadena separada per comes per a seleccions
     * múltiples).
     *
     * @param comp El component d'entrada del qual es vol llegir el valor.
     * @return Una cadena de text representant la resposta de l'usuari.
     */
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
                if (c instanceof JCheckBox && ((JCheckBox) c).isSelected() && c.getName() != null) {
                    if (sb.length() > 0)
                        sb.append(",");
                    sb.append(c.getName());
                }
            }
            return sb.toString();
        }
        return "";
    }
}

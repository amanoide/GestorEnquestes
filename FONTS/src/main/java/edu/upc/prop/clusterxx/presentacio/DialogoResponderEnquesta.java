package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import edu.upc.prop.clusterxx.domini.classes.Pregunta;
import edu.upc.prop.clusterxx.domini.classes.Opcio;
import edu.upc.prop.clusterxx.domini.classes.TipusPregunta;

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

    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color CARD_COLOR = Color.WHITE;

    private CtrlPresentacio iCtrlPresentacio;
    private String idEnquesta;
    private ArrayList<Pregunta> preguntes;

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
        this.preguntes = iCtrlPresentacio.getPreguntesEnquestaObjects(idEnquesta);
        inicializar();
    }

    /**
     * Mètode principal de construcció de la interfície.
     * 
     * Itera sobre la llista de preguntes carregades i, per a cadascuna:
     * Crea un panell contenidor.
     * Afegeix el text de la pregunta i les instruccions.
     * Genera el component d'entrada adequat (Input field) mitjançant
     * {@link #crearComponentInput(Pregunta)}.
     * Afegeix el component a un mapa per referència posterior.
     * 
     * També afegeix els botons d'acció "Enviar" i "Cancel·lar" al peu del diàleg.
     */
    private void inicializar() {
        setLayout(new BorderLayout(10, 10));
        setSize(650, 550);
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(BACKGROUND_COLOR);
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));

        // Título
        JLabel titulo = new JLabel("📝 Respondre Enquesta: " + idEnquesta);
        titulo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        titulo.setForeground(TEXT_COLOR);
        add(titulo, BorderLayout.NORTH);

        JPanel panelContent = new JPanel();
        panelContent.setLayout(new BoxLayout(panelContent, BoxLayout.Y_AXIS));
        panelContent.setBackground(BACKGROUND_COLOR);
        panelContent.setBorder(new EmptyBorder(10, 5, 10, 5));

        for (Pregunta p : preguntes) {
            JPanel panelPregunta = new JPanel(new BorderLayout(5, 5));
            panelPregunta.setBackground(CARD_COLOR);
            panelPregunta.setBorder(new CompoundBorder(
                    new LineBorder(new Color(220, 220, 220), 1, true),
                    new EmptyBorder(12, 12, 12, 12)));
            panelPregunta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

            JComponent input = crearComponentInput(p);

            // Pregunta con emoji según tipo
            String emoji = getEmojiTipus(p.getTipus());
            JLabel lblPregunta = new JLabel(emoji + " " + p.getText());
            lblPregunta.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
            lblPregunta.setForeground(TEXT_COLOR);

            String instruccions = getInstruccions(p);
            JLabel lblInstruccions = new JLabel(instruccions);
            lblInstruccions.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            lblInstruccions.setForeground(new Color(149, 165, 166));

            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(CARD_COLOR);
            headerPanel.add(lblPregunta, BorderLayout.NORTH);
            headerPanel.add(lblInstruccions, BorderLayout.SOUTH);

            panelPregunta.add(headerPanel, BorderLayout.NORTH);
            panelPregunta.add(input, BorderLayout.CENTER);

            inputComponents.put(p.getId(), input);
            panelContent.add(panelPregunta);
            panelContent.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JScrollPane scroll = new JScrollPane(panelContent);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BACKGROUND_COLOR);
        add(scroll, BorderLayout.CENTER);

        JPanel panelBotons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotons.setBackground(BACKGROUND_COLOR);

        JButton btnEnviar = crearBoton("✅ Enviar Respostes", new Color(39, 174, 96));
        JButton btnCancel = crearBoton("✖ Cancel·lar", new Color(149, 165, 166));

        btnEnviar.addActionListener(e -> enviarRespostes());
        btnCancel.addActionListener(e -> setVisible(false));

        panelBotons.add(btnEnviar);
        panelBotons.add(btnCancel);
        add(panelBotons, BorderLayout.SOUTH);
    }

    /**
     * Retorna un emoji visual que representa gràficament el tipus de pregunta.
     * Ajuda a l'usuari a identificar ràpidament com ha de respondre.
     *
     * @param tp El tipus de la pregunta (NUMERICA, TEXT_LLIURE, etc.).
     * @return Un string que conté l'emoji corresponent.
     */
    private String getEmojiTipus(TipusPregunta tp) {
        switch (tp) {
            case NUMERICA:
                return "🔢";
            case TEXT_LLIURE:
                return "✏️";
            case QUALITATIVA_ORDENADA:
            case QUALITATIVA_NO_ORDENADA_SIMPLE:
                return "📋";
            case QUALITATIVA_NO_ORDENADA_MULTIPLE:
                return "☑️";
            default:
                return "❓";
        }
    }

    /**
     * Genera un text descriptiu amb instruccions sobre com respondre la pregunta.
     * Per exemple, per a preguntes numèriques indica el rang permès.
     *
     * @param p La pregunta objecte de la qual extreure les restriccions.
     * @return Una cadena amb les instruccions formatades.
     */
    private String getInstruccions(Pregunta p) {
        switch (p.getTipus()) {
            case NUMERICA:
                return "Valor entre " + p.getValorMinim() + " i " + p.getValorMaxim();
            case TEXT_LLIURE:
                return "Text lliure";
            case QUALITATIVA_ORDENADA:
            case QUALITATIVA_NO_ORDENADA_SIMPLE:
                return "Selecciona una opció";
            case QUALITATIVA_NO_ORDENADA_MULTIPLE:
                return "Selecciona fins a " + p.getMaxSeleccions() + " opcions";
            default:
                return "";
        }
    }

    private JButton crearBoton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Fàbrica de components d'entrada: crea el widget de Swing adequat segons el
     * tipus de pregunta.
     * 
     * JSpinner: per a preguntes numèriques (configurat amb min/max/step).
     * JTextField: per a preguntes de text lliure.
     * JComboBox: per a preguntes de selecció simple (opcions desplegables).
     * JPanel amb JCheckBox: per a preguntes de selecció múltiple.
     *
     * @param p L'objecte Pregunta que defineix el tipus i les opcions disponibles.
     * @return Un JComponent configurat i llest per ser afegit a la interfície.
     */
    private JComponent crearComponentInput(Pregunta p) {
        TipusPregunta tp = p.getTipus();
        switch (tp) {
            case NUMERICA:
                SpinnerNumberModel model = new SpinnerNumberModel(p.getValorMinim(), p.getValorMinim(),
                        p.getValorMaxim(), Double.valueOf(1.0));
                JSpinner spinner = new JSpinner(model);
                spinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                return spinner;

            case TEXT_LLIURE:
                JTextField textField = new JTextField();
                textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                textField.setBorder(new CompoundBorder(
                        new LineBorder(new Color(200, 200, 200), 1, true),
                        new EmptyBorder(8, 10, 8, 10)));
                return textField;

            case QUALITATIVA_ORDENADA:
            case QUALITATIVA_NO_ORDENADA_SIMPLE:
                JComboBox<String> combo = new JComboBox<>();
                combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                for (Opcio o : p.getOpcions()) {
                    combo.addItem(o.getText());
                }
                return combo;

            case QUALITATIVA_NO_ORDENADA_MULTIPLE:
                JPanel panelChecks = new JPanel(new GridLayout(0, 2, 5, 5));
                panelChecks.setBackground(CARD_COLOR);
                panelChecks.putClientProperty("isMultiple", true);
                for (Opcio o : p.getOpcions()) {
                    JCheckBox cb = new JCheckBox(o.getText());
                    cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    cb.setBackground(CARD_COLOR);
                    cb.setName(String.valueOf(o.getId()));
                    panelChecks.add(cb);
                }
                return panelChecks;

            default:
                return new JLabel("Tipus no suportat");
        }
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

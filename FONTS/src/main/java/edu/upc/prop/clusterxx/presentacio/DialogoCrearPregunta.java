package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Diàleg per a la creació i edició de preguntes d'enquesta.
 * 
 * Aquest diàleg permet als usuaris crear noves preguntes o modificar preguntes
 * existents d'una enquesta. Ofereix suport per a diferents tipus de preguntes
 * amb controls específics per a cada tipus.
 * 
 * Tipus de preguntes suportats:
 * 
 * TEXT_LLIURE: Resposta de text obert sense restriccions.
 * NUMERICA: Resposta numèrica amb rang mínim i màxim.
 * QUALITATIVA_ORDENADA: Opcions predefinides amb ordre (ex: escala
 * Likert).
 * QUALITATIVA_NO_ORDENADA_SIMPLE: Opcions predefinides sense ordre,
 * selecció única.
 * QUALITATIVA_NO_ORDENADA_MULTIPLE: Opcions predefinides sense ordre,
 * selecció múltiple.
 * 
 * 
 * Funcionalitats clau:
 * 
 * Interfície adaptativa que mostra controls específics segons el tipus de
 * pregunta.
 * Validació de camps obligatoris (ID i text de la pregunta).
 * Suport per edició de preguntes existents (ID no editable).
 * Configuració de rangs per preguntes numèriques.
 * Gestió d'opcions per preguntes qualitatives amb múltiples seleccions.
 * 
 */
public class DialogoCrearPregunta extends JDialog {

    /** Indica si l'usuari ha confirmat la creació/edició de la pregunta. */
    private boolean confirmado = false;
    /** Camp de text per a l'identificador de la pregunta. */
    private JTextField textId = new JTextField(20);
    /** Camp de text per al text de la pregunta. */
    private JTextField textPregunta = new JTextField(20);
    /** ComboBox per seleccionar el tipus de pregunta. */
    private JComboBox<String> comboTipus;
    /** Panel dinàmic que conté controls específics segons el tipus de pregunta. */
    private JPanel panelOpcions;
    /** Camp de text per al valor mínim (preguntes numèriques). */
    private JTextField textMin = new JTextField(8);
    /** Camp de text per al valor màxim (preguntes numèriques). */
    private JTextField textMax = new JTextField(8);
    /** Àrea de text per introduir opcions (preguntes qualitatives). */
    private JTextArea areaOpcions = new JTextArea(4, 20);
    /**
     * Spinner per configurar el nombre màxim de seleccions (preguntes múltiples).
     */
    private JSpinner spinnerMaxSeleccions = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

    /**
     * Constructor per crear un diàleg de nova pregunta amb Frame com a propietari.
     * 
     * @param owner Finestra propietària del diàleg (per centrar-lo).
     */
    public DialogoCrearPregunta(Frame owner) {
        super(owner, "Nova Pregunta", true);
        inicializar();
    }

    /**
     * Inicialitza i configura tots els components gràfics del diàleg.
     * 
     * Crea la interfície amb tres seccions:
     * 
     * Adalt: Títol del diàleg amb icona.
     * Mig: Formulari amb camps per ID, text, tipus i opcions específiques.
     * Abaix: Botons d'acció (Crear/OK i Cancel·lar).
     * 
     * 
     * El panel central utilitza CardLayout per mostrar controls específics segons
     * el tipus de pregunta seleccionat (camp buit, rangs numèrics o opcions
     * qualitatives).
     */
    private void inicializar() {
        setSize(500, 520);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(UIStyles.BACKGROUND_COLOR);

        // Título centrado
        JLabel titulo = new JLabel("❓ Nova Pregunta");
        titulo.setFont(UIStyles.FONT_ENQUESTA_TITLE);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBorder(new EmptyBorder(15, 0, 15, 0));
        add(titulo, BorderLayout.NORTH);

        // Panel central con formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(10, 30, 10, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        // ID
        gbc.gridy = 0;
        formPanel.add(UIComponents.createLabel("Identificador"), gbc);
        gbc.gridy = 1;
        textId.setPreferredSize(new Dimension(400, 30));
        formPanel.add(textId, gbc);

        // Text
        gbc.gridy = 2;
        formPanel.add(UIComponents.createLabel("Text de la pregunta"), gbc);
        gbc.gridy = 3;
        textPregunta.setPreferredSize(new Dimension(400, 30));
        formPanel.add(textPregunta, gbc);

        // Tipus
        gbc.gridy = 4;
        formPanel.add(UIComponents.createLabel("Tipus"), gbc);
        gbc.gridy = 5;
        String[] tipus = { "TEXT_LLIURE", "NUMERICA", "QUALITATIVA_ORDENADA",
                "QUALITATIVA_NO_ORDENADA_SIMPLE", "QUALITATIVA_NO_ORDENADA_MULTIPLE" };
        comboTipus = new JComboBox<>(tipus);
        comboTipus.setPreferredSize(new Dimension(400, 30));
        formPanel.add(comboTipus, gbc);

        // Panel dinámico
        panelOpcions = new JPanel(new CardLayout());
        panelOpcions.setPreferredSize(new Dimension(400, 160));
        panelOpcions.setBackground(Color.WHITE);

        JPanel panelBuit = new JPanel();
        panelBuit.setBackground(Color.WHITE);
        panelOpcions.add(panelBuit, "BUIT");

        // Panel numérico
        JPanel panelNumeric = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelNumeric.setBackground(Color.WHITE);
        panelNumeric.add(new JLabel("Mínim:"));
        textMin.setPreferredSize(new Dimension(80, 30));
        panelNumeric.add(textMin);
        panelNumeric.add(new JLabel("Màxim:"));
        textMax.setPreferredSize(new Dimension(80, 30));
        panelNumeric.add(textMax);
        panelOpcions.add(panelNumeric, "NUMERIC");

        // Panel qualitativo
        JPanel panelQualitatiu = new JPanel();
        panelQualitatiu.setLayout(new BoxLayout(panelQualitatiu, BoxLayout.Y_AXIS));
        panelQualitatiu.setBackground(Color.WHITE);
        panelQualitatiu.add(UIComponents.createLabel("Opcions (una per línia)"));
        areaOpcions.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane scrollOpcions = new JScrollPane(areaOpcions);
        scrollOpcions.setPreferredSize(new Dimension(400, 100));
        scrollOpcions.setMaximumSize(new Dimension(400, 100));
        panelQualitatiu.add(scrollOpcions);
        JPanel panelMaxSel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panelMaxSel.setBackground(Color.WHITE);
        panelMaxSel.add(new JLabel("Màxim seleccions:"));
        panelMaxSel.add(spinnerMaxSeleccions);
        panelQualitatiu.add(panelMaxSel);
        panelOpcions.add(panelQualitatiu, "QUALITATIU");

        gbc.gridy = 6;
        gbc.insets = new Insets(10, 0, 10, 0);
        formPanel.add(panelOpcions, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnOk = UIComponents.createColorButton("✓ Crear", UIStyles.PRIMARY_COLOR);
        JButton btnCancel = UIComponents.createColorButton("✖ Cancel·lar", UIStyles.SECONDARY_COLOR);

        buttonPanel.add(btnOk);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);

        // Listeners
        btnOk.addActionListener(e -> {
            if (validar()) {
                confirmado = true;
                setVisible(false);
            }
        });
        btnCancel.addActionListener(e -> setVisible(false));
        comboTipus.addItemListener(e -> actualizarPanelOpcions((String) comboTipus.getSelectedItem()));
    }

    /**
     * Actualitza el panel d'opcions per mostrar els controls específics del tipus
     * de pregunta seleccionat.
     * 
     * Utilitza CardLayout per canviar entre diferents panells:
     * 
     * NUMERICA: Mostra camps per mínim i màxim.
     * QUALITATIVA_*: Mostra àrea de text per opcions i spinner de màxim
     * seleccions.
     * TEXT_LLIURE: Mostra panel buit (sense opcions addicionals).
     * 
     * 
     * @param tipus Tipus de pregunta seleccionat al ComboBox.
     */
    private void actualizarPanelOpcions(String tipus) {
        CardLayout cl = (CardLayout) panelOpcions.getLayout();
        if (tipus.equals("NUMERICA")) {
            cl.show(panelOpcions, "NUMERIC");
        } else if (tipus.startsWith("QUALITATIVA")) {
            cl.show(panelOpcions, "QUALITATIU");
            spinnerMaxSeleccions.setEnabled(tipus.contains("MULTIPLE"));
        } else {
            cl.show(panelOpcions, "BUIT");
        }
    }

    /**
     * Valida que els camps obligatoris (ID i text de pregunta) no estiguin buits.
     * 
     * Mostra missatges d'error si hi ha problemes de validació.
     * 
     * @return true si la validació és correcta, false si hi ha errors.
     */
    private boolean validar() {
        if (textId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "L'identificador és obligatori");
            return false;
        }
        if (textPregunta.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El text de la pregunta és obligatori");
            return false;
        }
        return true;
    }

    /**
     * Indica si l'usuari ha confirmat la creació/edició prement el botó OK.
     * 
     * @return true si s'ha confirmat, false si s'ha cancel·lat.
     */
    public boolean isConfirmado() {
        return confirmado;
    }

    /**
     * Obté l'identificador de la pregunta introduït per l'usuari.
     * 
     * @return Identificador de la pregunta (sense espais al principi/final).
     */
    public String getId() {
        return textId.getText().trim();
    }

    /**
     * Obté el text de la pregunta introduït per l'usuari.
     * 
     * @return Text de la pregunta (sense espais al principi/final).
     */
    public String getPreguntaText() {
        return textPregunta.getText().trim();
    }

    /**
     * Obté el tipus de pregunta seleccionat al ComboBox.
     * 
     * @return Tipus de pregunta (TEXT_LLIURE, NUMERICA, QUALITATIVA_*).
     */
    public String getTipus() {
        return (String) comboTipus.getSelectedItem();
    }

    /**
     * Obté el valor mínim per a preguntes numèriques.
     * 
     * @return Valor mínim com a Double, o null si no s'ha introduït o és
     *         invàlid.
     */
    public Double getMin() {
        try {
            return Double.parseDouble(textMin.getText());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Obté el valor màxim per a preguntes numèriques.
     * 
     * @return Valor màxim com a Double, o null si no s'ha introduït o és
     *         invàlid.
     */
    public Double getMax() {
        try {
            return Double.parseDouble(textMax.getText());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Obté la llista d'opcions per a preguntes qualitatives.
     * 
     * Les opcions s'introdueixen a l'àrea de text amb una opció per línia.
     * 
     * @return ArrayList amb les opcions (una per línia), o null si l'àrea està
     *         buida.
     */
    public ArrayList<String> getOpcions() {
        String texto = areaOpcions.getText();
        if (texto.trim().isEmpty())
            return null;
        return new ArrayList<>(Arrays.asList(texto.split("\\n")));
    }

    /**
     * Obté el nombre màxim de seleccions per a preguntes qualitatives múltiples.
     * 
     * @return Valor del spinner (mínim 1, màxim 10).
     */
    public int getMaxSeleccions() {
        return (Integer) spinnerMaxSeleccions.getValue();
    }

    /**
     * Omple el formulari amb les dades d'una pregunta existent per a la seva
     * edició.
     * 
     * Aquest mètode s'utilitza quan es vol modificar una pregunta ja creada. L'ID
     * es marca com a no editable per evitar canvis en l'identificador.
     * 
     * @param id      Identificador de la pregunta (no editable).
     * @param text    Text de la pregunta.
     * @param tipus   Tipus de pregunta.
     * @param min     Valor mínim (preguntes numèriques), pot ser null.
     * @param max     Valor màxim (preguntes numèriques), pot ser null.
     * @param opcions Llista d'opcions (preguntes qualitatives), pot ser null.
     * @param maxSel  Nombre màxim de seleccions (preguntes múltiples).
     */
    public void setDades(String id, String text, String tipus, Double min, Double max,
            ArrayList<String> opcions, int maxSel) {
        textId.setText(id);
        textId.setEditable(false);
        textPregunta.setText(text);
        comboTipus.setSelectedItem(tipus);

        if (min != null)
            textMin.setText(String.valueOf(min));
        if (max != null)
            textMax.setText(String.valueOf(max));

        if (opcions != null && !opcions.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String op : opcions) {
                sb.append(op).append("\n");
            }
            areaOpcions.setText(sb.toString().trim());
        }

        spinnerMaxSeleccions.setValue(maxSel > 0 ? maxSel : 1);
        actualizarPanelOpcions(tipus);
    }
}

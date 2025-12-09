package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class DialogoCrearPregunta extends JDialog {
    
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    
    private boolean confirmado = false;
    private JTextField textId = new JTextField(20);
    private JTextField textPregunta = new JTextField(20);
    private JComboBox<String> comboTipus;
    private JPanel panelOpcions;
    private JTextField textMin = new JTextField(8);
    private JTextField textMax = new JTextField(8);
    private JTextArea areaOpcions = new JTextArea(4, 20);
    private JSpinner spinnerMaxSeleccions = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

    public DialogoCrearPregunta(Frame owner) {
        super(owner, "Nova Pregunta", true);
        inicializar();
    }

    public DialogoCrearPregunta(Dialog owner) {
        super(owner, "Nova Pregunta", true);
        inicializar();
    }

    private void inicializar() {
        setSize(500, 520);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Título centrado
        JLabel titulo = new JLabel("❓ Nova Pregunta");
        titulo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
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
        formPanel.add(crearLabel("Identificador"), gbc);
        gbc.gridy = 1;
        textId.setPreferredSize(new Dimension(400, 30));
        formPanel.add(textId, gbc);

        // Text
        gbc.gridy = 2;
        formPanel.add(crearLabel("Text de la pregunta"), gbc);
        gbc.gridy = 3;
        textPregunta.setPreferredSize(new Dimension(400, 30));
        formPanel.add(textPregunta, gbc);

        // Tipus
        gbc.gridy = 4;
        formPanel.add(crearLabel("Tipus"), gbc);
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
        panelQualitatiu.add(crearLabel("Opcions (una per línia)"));
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
        
        JButton btnOk = crearBoton("✓ Crear", PRIMARY_COLOR);
        JButton btnCancel = crearBoton("✖ Cancel·lar", new Color(149, 165, 166));
        
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

    private JLabel crearLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return label;
    }

    private JButton crearBoton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

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

    public boolean isConfirmado() { return confirmado; }
    public String getId() { return textId.getText().trim(); }
    public String getPreguntaText() { return textPregunta.getText().trim(); }
    public String getTipus() { return (String) comboTipus.getSelectedItem(); }
    public Double getMin() {
        try { return Double.parseDouble(textMin.getText()); } 
        catch (Exception e) { return null; }
    }
    public Double getMax() {
        try { return Double.parseDouble(textMax.getText()); } 
        catch (Exception e) { return null; }
    }
    public ArrayList<String> getOpcions() {
        String texto = areaOpcions.getText();
        if (texto.trim().isEmpty()) return null;
        return new ArrayList<>(Arrays.asList(texto.split("\\n")));
    }
    public int getMaxSeleccions() { return (Integer) spinnerMaxSeleccions.getValue(); }

    public void setDades(String id, String text, String tipus, Double min, Double max,
            ArrayList<edu.upc.prop.clusterxx.domini.classes.Opcio> opcions, int maxSel) {
        textId.setText(id);
        textId.setEditable(false);
        textPregunta.setText(text);
        comboTipus.setSelectedItem(tipus);

        if (min != null) textMin.setText(String.valueOf(min));
        if (max != null) textMax.setText(String.valueOf(max));

        if (opcions != null && !opcions.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (edu.upc.prop.clusterxx.domini.classes.Opcio op : opcions) {
                sb.append(op.getText()).append("\n");
            }
            areaOpcions.setText(sb.toString().trim());
        }

        spinnerMaxSeleccions.setValue(maxSel > 0 ? maxSel : 1);
        actualizarPanelOpcions(tipus);
    }
}

package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class DialogoCrearPregunta extends JDialog {
    private boolean confirmado = false;

    private JTextField textId = new JTextField(10);
    private JTextField textPregunta = new JTextField(20);
    private JComboBox<String> comboTipus;

    // Camps específics
    private JPanel panelOpcions;
    private JTextField textMin = new JTextField(5);
    private JTextField textMax = new JTextField(5);
    private JTextArea areaOpcions = new JTextArea(3, 20); // Una opció per línia
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
        setLayout(new BorderLayout());

        // Panel Central
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelForm.add(new JLabel("ID Pregunta:"), gbc);
        gbc.gridx = 1;
        panelForm.add(textId, gbc);

        // Text
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelForm.add(new JLabel("Text:"), gbc);
        gbc.gridx = 1;
        panelForm.add(textPregunta, gbc);

        // Tipus
        gbc.gridx = 0;
        gbc.gridy = 2;
        panelForm.add(new JLabel("Tipus:"), gbc);

        String[] tipus = { "TEXT_LLIURE", "NUMERICA", "QUALITATIVA_ORDENADA", "QUALITATIVA_NO_ORDENADA_SIMPLE",
                "QUALITATIVA_NO_ORDENADA_MULTIPLE" };
        comboTipus = new JComboBox<>(tipus);
        gbc.gridx = 1;
        panelForm.add(comboTipus, gbc);

        // Panel dinàmic per opcions extra
        panelOpcions = new JPanel(new CardLayout());

        // Panel Buit (Text lliure)
        panelOpcions.add(new JPanel(), "BUIT");

        // Panel Numèric
        JPanel panelNumeric = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelNumeric.add(new JLabel("Min:"));
        panelNumeric.add(textMin);
        panelNumeric.add(new JLabel("Max:"));
        panelNumeric.add(textMax);
        panelOpcions.add(panelNumeric, "NUMERIC");

        // Panel Qualitatiu
        JPanel panelQualitatiu = new JPanel(new BorderLayout());
        panelQualitatiu.add(new JLabel("Opcions (una per línia):"), BorderLayout.NORTH);
        panelQualitatiu.add(new JScrollPane(areaOpcions), BorderLayout.CENTER);

        JPanel panelMultiple = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelMultiple.add(new JLabel("Max Seleccions:"));
        panelMultiple.add(spinnerMaxSeleccions);
        panelQualitatiu.add(panelMultiple, BorderLayout.SOUTH);

        panelOpcions.add(panelQualitatiu, "QUALITATIU");

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panelForm.add(panelOpcions, gbc);

        add(panelForm, BorderLayout.CENTER);

        // Botons
        JPanel panelBotons = new JPanel();
        JButton btnOk = new JButton("Confirmar");
        JButton btnCancel = new JButton("Cancel·lar");

        btnOk.addActionListener(e -> {
            if (validar()) {
                confirmado = true;
                setVisible(false);
            }
        });

        btnCancel.addActionListener(e -> setVisible(false));

        panelBotons.add(btnOk);
        panelBotons.add(btnCancel);
        add(panelBotons, BorderLayout.SOUTH);

        // Listener combo
        comboTipus.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                actualizarPanelOpcions((String) e.getItem());
            }
        });

        pack();
        setLocationRelativeTo(getOwner());
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
        if (textId.getText().trim().isEmpty() || textPregunta.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID i Text són obligatoris.");
            return false;
        }
        return true;
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public String getId() {
        return textId.getText();
    }

    public String getPreguntaText() {
        return textPregunta.getText();
    }

    public String getTipus() {
        return (String) comboTipus.getSelectedItem();
    }

    public Double getMin() {
        try {
            return Double.parseDouble(textMin.getText());
        } catch (Exception e) {
            return null;
        }
    }

    public Double getMax() {
        try {
            return Double.parseDouble(textMax.getText());
        } catch (Exception e) {
            return null;
        }
    }

    public ArrayList<String> getOpcions() {
        String texto = areaOpcions.getText();
        if (texto.trim().isEmpty())
            return null;
        return new ArrayList<>(Arrays.asList(texto.split("\\n")));
    }

    public void setDades(String id, String text, String tipus, Double min, Double max,
            ArrayList<edu.upc.prop.clusterxx.domini.classes.Opcio> opcions, int maxSel) {
        textId.setText(id);
        textId.setEditable(false); // No es pot canviar l'ID en modificar
        textPregunta.setText(text);
        comboTipus.setSelectedItem(tipus);

        if (min != null)
            textMin.setText(String.valueOf(min));
        if (max != null)
            textMax.setText(String.valueOf(max));

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

    public int getMaxSeleccions() {
        return (Integer) spinnerMaxSeleccions.getValue();
    }
}

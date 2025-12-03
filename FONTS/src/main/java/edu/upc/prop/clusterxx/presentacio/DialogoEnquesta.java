package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import java.awt.*;

public class DialogoEnquesta extends JDialog {
    private JTextField textId = new JTextField();
    private JTextField textTitol = new JTextField();
    private JTextArea textDesc = new JTextArea(5, 20);
    private boolean confirmado = false;

    public DialogoEnquesta(Frame owner, String title, boolean isCreating) {
        super(owner, title, true); // Modal
        inicializarComponentes(isCreating);
    }

    private void inicializarComponentes(boolean isCreating) {
        this.setLayout(new BorderLayout());
        this.setSize(400, 300);
        this.setLocationRelativeTo(getOwner());

        JPanel panelCampos = new JPanel(new GridLayout(3, 2, 10, 10));
        panelCampos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelCampos.add(new JLabel("Nom:"));
        panelCampos.add(textId);
        panelCampos.add(new JLabel("Títol:"));
        panelCampos.add(textTitol);
        panelCampos.add(new JLabel("Descripció:"));
        panelCampos.add(new JScrollPane(textDesc));

        // ID solo editable si estamos creando
        textId.setEditable(isCreating);

        JPanel panelBotones = new JPanel();
        JButton btnConfirmar = new JButton("Confirmar");
        JButton btnCancelar = new JButton("Cancelar");

        panelBotones.add(btnConfirmar);
        panelBotones.add(btnCancelar);

        this.add(panelCampos, BorderLayout.CENTER);
        this.add(panelBotones, BorderLayout.SOUTH);

        // Listeners
        btnConfirmar.addActionListener(e -> {
            confirmado = true;
            setVisible(false);
        });

        btnCancelar.addActionListener(e -> {
            confirmado = false;
            setVisible(false);
        });
    }

    public void setDatos(String id, String titol, String desc) {
        textId.setText(id);
        textTitol.setText(titol);
        textDesc.setText(desc);
    }

    public String getId() {
        return textId.getText();
    }

    public String getTitol() {
        return textTitol.getText();
    }

    public String getDesc() {
        return textDesc.getText();
    }

    public boolean isConfirmado() {
        return confirmado;
    }
}

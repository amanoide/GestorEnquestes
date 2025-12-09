package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;

public class DialogoGestionPreguntes extends JDialog {
    
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    
    private CtrlPresentacio iCtrlPresentacio;
    private String idEnquesta;

    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> listPreguntes = new JList<>(listModel);

    public DialogoGestionPreguntes(Frame owner, CtrlPresentacio ctrlPresentacio, String idEnquesta) {
        super(owner, "Gestionar Preguntes - " + idEnquesta, true);
        this.iCtrlPresentacio = ctrlPresentacio;
        this.idEnquesta = idEnquesta;
        inicializar();
        cargarPreguntes();
    }

    private void inicializar() {
        setLayout(new BorderLayout(10, 10));
        setSize(550, 450);
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(BACKGROUND_COLOR);
        ((JPanel)getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));

        // Título
        JLabel titulo = new JLabel("📋 Preguntes de l'enquesta: " + idEnquesta);
        titulo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        titulo.setForeground(TEXT_COLOR);
        add(titulo, BorderLayout.NORTH);

        // Lista
        listPreguntes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        listPreguntes.setFixedCellHeight(40);
        listPreguntes.setSelectionBackground(new Color(52, 152, 219, 80));
        JScrollPane scroll = new JScrollPane(listPreguntes);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        add(scroll, BorderLayout.CENTER);

        // Botones
        JPanel panelBotons = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        panelBotons.setBackground(BACKGROUND_COLOR);
        
        JButton btnAfegir = crearBoton("➕ Afegir", new Color(39, 174, 96));
        JButton btnModificar = crearBoton("✏️ Modificar", PRIMARY_COLOR);
        JButton btnEliminar = crearBoton("🗑️ Eliminar", new Color(231, 76, 60));
        JButton btnTancar = crearBoton("✖ Tancar", new Color(149, 165, 166));

        btnAfegir.addActionListener(e -> afegirPregunta());
        btnModificar.addActionListener(e -> modificarPregunta());
        btnEliminar.addActionListener(e -> eliminarPregunta());
        btnTancar.addActionListener(e -> setVisible(false));

        panelBotons.add(btnAfegir);
        panelBotons.add(btnModificar);
        panelBotons.add(btnEliminar);
        panelBotons.add(btnTancar);
        add(panelBotons, BorderLayout.SOUTH);
    }

    private JButton crearBoton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void cargarPreguntes() {
        listModel.clear();
        ArrayList<edu.upc.prop.clusterxx.domini.classes.Pregunta> preguntas = iCtrlPresentacio
                .getPreguntesEnquestaObjects(idEnquesta);
        for (edu.upc.prop.clusterxx.domini.classes.Pregunta p : preguntas) {
            listModel.addElement(p.getId() + ": " + p.getText() + " [" + p.getTipus() + "]");
        }
    }

    private void afegirPregunta() {
        DialogoCrearPregunta dialogo = new DialogoCrearPregunta(this);
        dialogo.setVisible(true);

        if (dialogo.isConfirmado()) {
            String resultado = iCtrlPresentacio.afegirPregunta(
                    idEnquesta,
                    dialogo.getId(),
                    dialogo.getPreguntaText(),
                    dialogo.getTipus(),
                    dialogo.getMin(),
                    dialogo.getMax(),
                    dialogo.getOpcions(),
                    dialogo.getMaxSeleccions());
            JOptionPane.showMessageDialog(this, resultado);
            cargarPreguntes();
        }
    }

    private void modificarPregunta() {
        String selected = listPreguntes.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una pregunta per modificar.");
            return;
        }

        String idPregunta = selected.split(":")[0];
        edu.upc.prop.clusterxx.domini.classes.Pregunta p = iCtrlPresentacio.getDadesPregunta(idEnquesta, idPregunta);

        if (p == null) {
            JOptionPane.showMessageDialog(this, "Error al recuperar dades de la pregunta.");
            return;
        }

        DialogoCrearPregunta dialogo = new DialogoCrearPregunta(this);
        dialogo.setTitle("Modificar Pregunta");
        dialogo.setDades(p.getId(), p.getText(), p.getTipus().toString(),
                p.getValorMinim(), p.getValorMaxim(), p.getOpcions(), p.getMaxSeleccions());
        dialogo.setVisible(true);

        if (dialogo.isConfirmado()) {
            String resultado = iCtrlPresentacio.modificarPregunta(
                    idEnquesta,
                    dialogo.getId(),
                    dialogo.getPreguntaText(),
                    dialogo.getTipus(),
                    dialogo.getMin(),
                    dialogo.getMax(),
                    dialogo.getOpcions(),
                    dialogo.getMaxSeleccions());
            JOptionPane.showMessageDialog(this, resultado);
            cargarPreguntes();
        }
    }

    private void eliminarPregunta() {
        String selected = listPreguntes.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una pregunta per eliminar.");
            return;
        }

        String idPregunta = selected.split(":")[0];

        int confirm = JOptionPane.showConfirmDialog(this, "Eliminar la pregunta " + idPregunta + "?",
            "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String resultado = iCtrlPresentacio.eliminarPregunta(idEnquesta, idPregunta);
            JOptionPane.showMessageDialog(this, resultado);
            cargarPreguntes();
        }
    }
}

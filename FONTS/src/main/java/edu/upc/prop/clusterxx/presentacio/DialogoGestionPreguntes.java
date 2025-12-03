package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DialogoGestionPreguntes extends JDialog {
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
        setLayout(new BorderLayout());
        setSize(500, 400);
        setLocationRelativeTo(getOwner());

        // Llista
        JScrollPane scroll = new JScrollPane(listPreguntes);
        scroll.setBorder(BorderFactory.createTitledBorder("Preguntes de l'enquesta"));
        add(scroll, BorderLayout.CENTER);

        // Botons
        JPanel panelBotons = new JPanel();
        JButton btnAfegir = new JButton("Afegir Pregunta");
        JButton btnModificar = new JButton("Modificar Pregunta"); // Nuevo botón
        JButton btnEliminar = new JButton("Eliminar Seleccionada");
        JButton btnTancar = new JButton("Tancar");

        btnAfegir.addActionListener(e -> afegirPregunta());
        btnModificar.addActionListener(e -> modificarPregunta()); // Listener
        btnEliminar.addActionListener(e -> eliminarPregunta());
        btnTancar.addActionListener(e -> setVisible(false));

        panelBotons.add(btnAfegir);
        panelBotons.add(btnModificar); // Añadir al panel
        panelBotons.add(btnEliminar);
        panelBotons.add(btnTancar);
        add(panelBotons, BorderLayout.SOUTH);
    }

    private void cargarPreguntes() {
        listModel.clear();
        ArrayList<String> preguntes = iCtrlPresentacio.getPreguntesEnquesta(idEnquesta);
        for (String p : preguntes) {
            listModel.addElement(p);
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

        String idPregunta = selected.split(":")[0]; // Assumint format "ID: Text..."

        int confirm = JOptionPane.showConfirmDialog(this, "¿Segur que vols eliminar la pregunta " + idPregunta + "?");
        if (confirm == JOptionPane.YES_OPTION) {
            String resultado = iCtrlPresentacio.eliminarPregunta(idEnquesta, idPregunta);
            JOptionPane.showMessageDialog(this, resultado);
            cargarPreguntes();
        }
    }
}

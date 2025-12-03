package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class VistaGestionEnquestes extends JPanel {
    private CtrlPresentacio iCtrlPresentacio;
    private VistaPrincipal vistaPrincipal;

    // Lista de encuestas (CENTER)
    private DefaultListModel<String> listModelEnquestes = new DefaultListModel<>();
    private JList<String> listEnquestes = new JList<>(listModelEnquestes);

    // Botones de Acción Principal (SOUTH)
    private JPanel panelBotonesMain = new JPanel();
    private JButton btnEditar = new JButton("Modificar Seleccionada");
    private JButton btnGestionarPreguntes = new JButton("Gestionar Preguntes"); // Nuevo botón
    private JButton btnEliminar = new JButton("Eliminar Seleccionada");
    private JButton btnVolver = new JButton("Tornar al menu principal");

    private JLabel labelStatusEnquesta = new JLabel("Gestió d'Encuestes");

    public VistaGestionEnquestes(CtrlPresentacio ctrlPresentacio, VistaPrincipal vistaPrincipal) {
        this.iCtrlPresentacio = ctrlPresentacio;
        this.vistaPrincipal = vistaPrincipal;
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        this.setLayout(new BorderLayout());

        // Lista en el centro (Grande)
        JScrollPane scrollList = new JScrollPane(listEnquestes);
        scrollList.setBorder(BorderFactory.createTitledBorder("Les meves enquestes"));
        this.add(scrollList, BorderLayout.CENTER);

        // Botones abajo (Acciones principales)
        panelBotonesMain.add(btnEditar);
        panelBotonesMain.add(btnGestionarPreguntes); // Añadir al panel
        panelBotonesMain.add(btnEliminar);
        panelBotonesMain.add(btnVolver);
        this.add(panelBotonesMain, BorderLayout.SOUTH);

        this.add(labelStatusEnquesta, BorderLayout.NORTH);

        // Listeners
        btnEditar.addActionListener(e -> mostrarDialogoEditar());
        btnGestionarPreguntes.addActionListener(e -> mostrarDialogoPreguntes()); // Listener
        btnEliminar.addActionListener(e -> eliminarEnquesta());
        btnVolver.addActionListener(e -> vistaPrincipal.mostrarVista("MENU"));

        // Listener de selección de lista
        listEnquestes.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean selected = !listEnquestes.isSelectionEmpty();
                btnEditar.setEnabled(selected);
                btnEliminar.setEnabled(selected);
            }
        });

        // Estado inicial botones
        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }

    public void actualizarLista() {
        refreshEnquestesList();
    }

    // --- Métodos Auxiliares UI ---

    private void mostrarDialogoEditar() {
        String selected = listEnquestes.getSelectedValue();
        if (selected == null)
            return;

        String id = selected.split(":")[0];

        // Obtener datos actuales
        String currentTitle = "";
        String currentDesc = "";

        java.util.ArrayList<edu.upc.prop.clusterxx.domini.classes.Enquesta> enquestes = iCtrlPresentacio
                .getEnquestesUsuari();
        for (edu.upc.prop.clusterxx.domini.classes.Enquesta e : enquestes) {
            if (e.getId().equals(id)) {
                currentTitle = e.getTitol();
                currentDesc = e.getDescripcio();
                break;
            }
        }

        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        DialogoEnquesta dialogo = new DialogoEnquesta(parentFrame, "Modificar Encuesta", false);
        dialogo.setDatos(id, currentTitle, currentDesc);
        dialogo.setVisible(true);

        if (dialogo.isConfirmado()) {
            String resultado = iCtrlPresentacio.modificarEnquesta(id, dialogo.getTitol(), dialogo.getDesc());
            JOptionPane.showMessageDialog(this, resultado);
            if (resultado.contains("correctament")) {
                refreshEnquestesList();
            }
        }
    }

    // --- Métodos de Acción ---

    /**
     * Gestiona l'acció d'eliminar l'enquesta seleccionada.
     */
    public void actionPerformed_btnEliminar(ActionEvent event) {
        String selected = listEnquestes.getSelectedValue();
        if (selected == null)
            return;

        String id = selected.split(":")[0];

        int confirm = JOptionPane.showConfirmDialog(this, "¿Segur que vols eliminar la enquesta " + id + "?");
        if (confirm == JOptionPane.YES_OPTION) {
            String resultado = iCtrlPresentacio.esborrarEnquesta(id);
            JOptionPane.showMessageDialog(this, resultado);
            if (resultado.contains("correctament")) {
                refreshEnquestesList();
            }
        }
    }

    // --- Métodos de Acción ---

    /**
     * Abre un diálogo para gestionar las preguntas de la encuesta seleccionada.
     */
    private void mostrarDialogoPreguntes() {
        String selected = listEnquestes.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una enquesta per gestionar les seves preguntes.");
            return;
        }

        String id = selected.split(":")[0];
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        DialogoGestionPreguntes dialogo = new DialogoGestionPreguntes(parentFrame, iCtrlPresentacio, id);
        dialogo.setVisible(true);
    }

    private void eliminarEnquesta() {
        String selected = listEnquestes.getSelectedValue();
        if (selected == null)
            return;

        String id = selected.split(":")[0];

        int confirm = JOptionPane.showConfirmDialog(this, "¿Segur que vols eliminar la enquesta " + id + "?");
        if (confirm == JOptionPane.YES_OPTION) {
            String resultado = iCtrlPresentacio.esborrarEnquesta(id);
            JOptionPane.showMessageDialog(this, resultado);
            if (resultado.contains("correctament")) {
                refreshEnquestesList();
            }
        }
    }

    /**
     * Actualitza la llista visual d'enquestes recuperant les dades del controlador.
     */
    private void refreshEnquestesList() {
        listModelEnquestes.clear();
        java.util.ArrayList<edu.upc.prop.clusterxx.domini.classes.Enquesta> enquestes = iCtrlPresentacio
                .getEnquestesUsuari();
        for (edu.upc.prop.clusterxx.domini.classes.Enquesta e : enquestes) {
            listModelEnquestes.addElement(e.getId() + ": " + e.getTitol());
        }
    }
}

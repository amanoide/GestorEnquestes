package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Diàleg per gestionar (afegir, modificar i eliminar) les preguntes d'una
 * enquesta.
 * 
 * Aquest diàleg mostra una llista de totes les preguntes d'una enquesta i
 * ofereix operacions CRUD (Create, Read, Update, Delete) sobre elles.
 * 
 * Funcionalitats clau:
 * 
 * Visualització de totes les preguntes amb ID, text i tipus.
 * Creació de noves preguntes mitjançant DialogoCrearPregunta.
 * Modificació de preguntes existents (ID no editable).
 * Eliminació de preguntes amb confirmació.
 * Actualització automàtica de la llista després de cada operació.
 * Validació de selecció abans de modificar/eliminar.
 * 
 * 
 * El diàleg és modal i s'actualitza dinàmicament per reflectir els canvis
 * al sistema.
 */
public class DialogoGestionPreguntes extends JDialog {

    /** Controlador de presentació per executar operacions sobre preguntes. */
    private CtrlPresentacio iCtrlPresentacio;
    /** Identificador de l'enquesta de la qual es gestionen les preguntes. */
    private String idEnquesta;

    /** Model de dades per a la llista de preguntes. */
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    /** Component visual que mostra la llista de preguntes. */
    private JList<String> listPreguntes = new JList<>(listModel);

    /**
     * Constructor del diàleg de gestió de preguntes.
     * 
     * Inicialitza el diàleg, construeix la interfície i carrega la llista de
     * preguntes de l'enquesta especificada.
     *
     * @param owner           Finestra propietària del diàleg (per centrar-lo).
     * @param ctrlPresentacio Controlador de presentació per gestionar les
     *                        operacions.
     * @param idEnquesta      Identificador de l'enquesta a gestionar.
     */
    public DialogoGestionPreguntes(Frame owner, CtrlPresentacio ctrlPresentacio, String idEnquesta) {
        super(owner, "Gestionar Preguntes - " + idEnquesta, true);
        this.iCtrlPresentacio = ctrlPresentacio;
        this.idEnquesta = idEnquesta;
        inicializar();
        cargarPreguntes();
    }

    /**
     * Inicialitza i configura tots els components gràfics del diàleg.
     * 
     * Crea una interfície amb tres seccions:
     * 
     * Adalt: Títol amb icona i nom de l'enquesta.
     * medio: Llista desplaçable de preguntes.
     * Abajo: Botons d'acció (Afegir, Modificar, Eliminar, Tancar).
     * 
     * 
     * Cada pregunta es mostra amb el format: "ID: Text [Tipus]".
     */
    private void inicializar() {
        setLayout(new BorderLayout(10, 10));
        setSize(700, 450);
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(UIStyles.BACKGROUND_COLOR);
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));

        // Título
        JLabel titulo = new JLabel("Preguntes de l'enquesta: " + idEnquesta);
        titulo.setFont(UIStyles.FONT_SECTION);
        titulo.setForeground(UIStyles.TEXT_COLOR);
        add(titulo, BorderLayout.NORTH);

        // Lista
        listPreguntes.setFont(UIStyles.FONT_INPUT);
        listPreguntes.setFixedCellHeight(40);
        listPreguntes.setSelectionBackground(UIStyles.SELECTION_COLOR);
        JScrollPane scroll = new JScrollPane(listPreguntes);
        scroll.setBorder(BorderFactory.createLineBorder(UIStyles.BORDER_LIGHT));
        add(scroll, BorderLayout.CENTER);

        // Botones
        JPanel panelBotons = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        panelBotons.setBackground(UIStyles.BACKGROUND_COLOR);

        JButton btnAfegir = UIComponents.createColorButton("➕ Afegir", UIStyles.SUCCESS_COLOR);
        JButton btnModificar = UIComponents.createColorButton("✏️ Modificar", UIStyles.PRIMARY_COLOR);
        JButton btnEliminar = UIComponents.createColorButton("🗑️ Eliminar", UIStyles.ERROR_COLOR);
        JButton btnVeureRespostes = UIComponents.createColorButton("📊 Veure Respostes", UIStyles.WARNING_COLOR);
        JButton btnTancar = UIComponents.createColorButton("✖ Tancar", UIStyles.SECONDARY_COLOR);

        MyActionListener listener = new MyActionListener(iCtrlPresentacio, null, this);

        btnAfegir.setActionCommand(MyActionListener.Action.CREAR_PREGUNTA.name());
        btnAfegir.addActionListener(listener);

        btnModificar.setActionCommand(MyActionListener.Action.MODIFICAR_PREGUNTA.name());
        btnModificar.addActionListener(listener);

        btnEliminar.setActionCommand(MyActionListener.Action.ELIMINAR_PREGUNTA.name());
        btnEliminar.addActionListener(listener);

        btnVeureRespostes.setActionCommand(MyActionListener.Action.VEURE_RESPOSTES_PREGUNTA.name());
        btnVeureRespostes.addActionListener(listener);

        btnTancar.addActionListener(e -> setVisible(false));

        panelBotons.add(btnAfegir);
        panelBotons.add(btnModificar);
        panelBotons.add(btnEliminar);
        panelBotons.add(btnVeureRespostes);
        panelBotons.add(btnTancar);
        add(panelBotons, BorderLayout.SOUTH);
    }

    /**
     * Carrega la llista de preguntes de l'enquesta des del controlador.
     * 
     * Neteja el model de la llista i afegeix totes les preguntes amb el format:
     * "ID: Text [Tipus]".
     * 
     * Aquest mètode s'invoca després de cada operació (afegir, modificar,
     * eliminar) per actualitzar la visualització.
     */
    void cargarPreguntes() {
        listModel.clear();
        ArrayList<edu.upc.prop.clusterxx.domini.classes.Pregunta> preguntas = iCtrlPresentacio
                .getPreguntesEnquestaObjects(idEnquesta);
        for (edu.upc.prop.clusterxx.domini.classes.Pregunta p : preguntas) {
            listModel.addElement(p.getId() + ": " + p.getText() + " [" + p.getTipus() + "]");
        }
    }

    public String getIdEnquesta() {
        return idEnquesta;
    }

    public String getSelectedPreguntaId() {
        String selected = listPreguntes.getSelectedValue();
        if (selected == null) return null;
        return selected.split(":")[0].trim();
    }


}

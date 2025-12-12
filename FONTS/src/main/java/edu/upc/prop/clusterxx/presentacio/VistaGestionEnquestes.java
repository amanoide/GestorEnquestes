package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Vista per gestionar (modificar, eliminar i veure) les enquestes creades per
 * l'usuari.
 * 
 * Aquesta vista mostra una llista de totes les enquestes creades per l'usuari
 * autenticat i ofereix operacions de gestió sobre elles.
 * 
 * Funcionalitats clau:
 * 
 * Visualització de totes les enquestes creades per l'usuari.
 * Modificació de títol i descripció d'enquestes existents.
 * Gestió de preguntes de cada enquesta (obre DialogoGestionPreguntes).
 * Visualització de participants que han respost (obre DialogoParticipants).
 * Eliminació d'enquestes amb confirmació.
 * Deshabilitació de botons quan no hi ha selecció.
 * Actualització dinàmica de la llista després de cada operació.
 * 
 * 
 * La vista s'actualitza automàticament mitjançant el mètode
 * {@link #actualizarLista()} cada vegada que es mostra des de
 * {@link VistaPrincipal}.
 */
public class VistaGestionEnquestes extends JPanel {

    /** Controlador de presentació per executar operacions sobre enquestes. */
    private CtrlPresentacio iCtrlPresentacio;
    /** Referència a la vista principal per permetre la navegació. */
    private VistaPrincipal vistaPrincipal;

    /** Model de dades per a la llista d'enquestes. */
    private DefaultListModel<String> listModelEnquestes = new DefaultListModel<>();
    /** Component visual que mostra la llista d'enquestes. */
    JList<String> listEnquestes = new JList<>(listModelEnquestes);
    /** Botó per modificar una enquesta seleccionada. */
    private JButton btnEditar = new JButton("✏️ Modificar");
    /** Botó per gestionar les preguntes d'una enquesta. */
    private JButton btnGestionarPreguntes = new JButton("📝 Gestionar Preguntes");
    /** Botó per veure els participants que han respost. */
    private JButton btnVeureParticipants = new JButton("👥 Veure Participants");
    /** Botó per eliminar una enquesta seleccionada. */
    private JButton btnEliminar = new JButton("🗑️ Eliminar");
    /** Botó per tornar al menú principal. */
    private JButton btnVolver = new JButton("← Tornar");

    /**
     * Constructor de la vista de gestió d'enquestes.
     * 
     * Inicialitza la vista, enllaça amb el controlador i la vista principal,
     * i construeix la interfície d'usuari.
     *
     * @param ctrlPresentacio Controlador de presentació per a les operacions
     *                        sobre enquestes.
     * @param vistaPrincipal  Referència a la finestra principal per a la
     *                        navegació.
     */
    public VistaGestionEnquestes(CtrlPresentacio ctrlPresentacio, VistaPrincipal vistaPrincipal) {
        this.iCtrlPresentacio = ctrlPresentacio;
        this.vistaPrincipal = vistaPrincipal;
        inicializarComponentes();
    }

    /**
     * Inicialitza i configura tots els components gràfics de la vista.
     * 
     * Crea una interfície amb tres seccions:
     * 
     * Adalt: Títol amb icona de gestió (📋).
     * MIG: Llista desplaçable d'enquestes de l'usuari.
     * Abaix: Botons d'acció (Modificar, Gestionar Preguntes, Veure
     *   Participants, Eliminar, Tornar).
     * 
     * 
     * Configura els listeners per habilitar/deshabilitar els botons segons la
     * selecció de la llista. Tots els botons estan deshabiltats inicialment.
     */
    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIStyles.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Título
        JLabel titulo = new JLabel("📋 Gestió d'Enquestes");
        titulo.setFont(UIStyles.FONT_VIEW_TITLE);
        titulo.setForeground(UIStyles.TEXT_COLOR);
        add(titulo, BorderLayout.NORTH);

        // Lista
        listEnquestes.setFont(UIStyles.FONT_NORMAL);
        listEnquestes.setFixedCellHeight(40);
        listEnquestes.setSelectionBackground(UIStyles.SELECTION_COLOR);
        JScrollPane scrollList = new JScrollPane(listEnquestes);
        scrollList.setBorder(BorderFactory.createTitledBorder("Les meves enquestes"));
        add(scrollList, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.setBackground(UIStyles.BACKGROUND_COLOR);

        UIComponents.styleButton(btnEditar, UIStyles.PRIMARY_COLOR);
        UIComponents.styleButton(btnGestionarPreguntes, UIStyles.SUCCESS_COLOR);
        UIComponents.styleButton(btnVeureParticipants, UIStyles.WARNING_COLOR);
        UIComponents.styleButton(btnEliminar, UIStyles.ERROR_COLOR);
        UIComponents.styleButton(btnVolver, UIStyles.SECONDARY_COLOR);

        panelBotones.add(btnEditar);
        panelBotones.add(btnGestionarPreguntes);
        panelBotones.add(btnVeureParticipants);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnVolver);
        add(panelBotones, BorderLayout.SOUTH);

        // Listeners
        btnEditar.setActionCommand(MyActionListener.Action.MODIFICAR_ENQUESTA.name());
        btnEditar.addActionListener(new MyActionListener(iCtrlPresentacio, vistaPrincipal, this));
        
        btnGestionarPreguntes.setActionCommand(MyActionListener.Action.GESTIONAR_PREGUNTES.name());
        btnGestionarPreguntes.addActionListener(new MyActionListener(iCtrlPresentacio, vistaPrincipal, this));
        
        btnVeureParticipants.setActionCommand(MyActionListener.Action.VEURE_PARTICIPANTS.name());
        btnVeureParticipants.addActionListener(new MyActionListener(iCtrlPresentacio, vistaPrincipal, this));
        
        btnEliminar.setActionCommand(MyActionListener.Action.ELIMINAR_ENQUESTA.name());
        btnEliminar.addActionListener(new MyActionListener(iCtrlPresentacio, vistaPrincipal, this));
        
        btnVolver.setActionCommand(MyActionListener.Action.TORNAR_MENU.name());
        btnVolver.addActionListener(new MyActionListener(iCtrlPresentacio, vistaPrincipal, this));

        listEnquestes.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean selected = !listEnquestes.isSelectionEmpty();
                btnEditar.setEnabled(selected);
                btnGestionarPreguntes.setEnabled(selected);
                btnVeureParticipants.setEnabled(selected);
                btnEliminar.setEnabled(selected);
            }
        });

        btnEditar.setEnabled(false);
        btnGestionarPreguntes.setEnabled(false);
        btnVeureParticipants.setEnabled(false);
        btnEliminar.setEnabled(false);
    }

    /**
     * Actualitza la llista d'enquestes amb les enquestes de l'usuari actual.
     * 
     * Aquest mètode és invocat des de {@link VistaPrincipal#mostrarVista(String)}
     * cada vegada que es mostra aquesta vista.
     */
    public void actualizarLista() {
        refreshEnquestesList();
    }

    /**
     * Refresca el contingut de la llista d'enquestes.
     * 
     * Neteja el model de la llista i afegeix totes les enquestes de l'usuari
     * obtingudes del controlador amb el format "ID: Títol".
     * 
     * Aquest mètode s'invoca després de cada operació de modificació o
     * eliminació.
     */
    void refreshEnquestesList() {
        listModelEnquestes.clear();
        for (edu.upc.prop.clusterxx.domini.classes.Enquesta e : iCtrlPresentacio.getEnquestesUsuari()) {
            listModelEnquestes.addElement(e.getId() + ": " + e.getTitol());
        }
    }
}

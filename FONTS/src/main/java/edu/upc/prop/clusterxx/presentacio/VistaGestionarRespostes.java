package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Vista per gestionar les respostes de l'usuari a les enquestes contestades.
 * 
 * Aquesta vista mostra una llista de totes les enquestes que l'usuari ha
 * contestat, permetent visualitzar/modificar les respostes individuals o
 * esborrar totes les respostes d'una enquesta.
 * 
 * Funcionalitats clau:
 * 
 * Llista de totes les enquestes contestades per l'usuari autenticat.
 * Botó per veure i modificar respostes (obre DialogoGestionarRespostes).
 * Botó per esborrar totes les respostes d'una enquesta amb confirmació.
 * Deshabilitació de botons quan no hi ha selecció.
 * Actualització dinàmica de la llista després de cada operació.
 * Navegació de tornada al menú principal.
 * 
 * 
 * La vista s'actualitza automàticament mitjançant el mètode
 * {@link #actualizarLista()} cada vegada que es mostra des de
 * {@link VistaPrincipal}.
 */
public class VistaGestionarRespostes extends JPanel {

    /** Controlador de presentació per executar operacions sobre respostes. */
    private CtrlPresentacio iCtrlPresentacio;
    /** Referència a la vista principal per permetre la navegació. */
    private VistaPrincipal vistaPrincipal;

    /** Model de dades per a la llista d'enquestes contestades. */
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    /** Component visual que mostra la llista d'enquestes contestades. */
    JList<String> listEnquestes = new JList<>(listModel);
    /** Botons d'acció de la vista. */
    private JButton btnModificar, btnEsborrar, btnImportar, btnVeurePerfilEnquesta, btnVeureTotsPerfils, btnTornar;

    /**
     * Constructor de la vista de gestió de respostes.
     * 
     * Inicialitza la vista, enllaça amb el controlador i la vista principal,
     * i construeix la interfície d'usuari.
     *
     * @param ctrlPresentacio Controlador de presentació per a les operacions
     *                        sobre respostes.
     * @param vistaPrincipal  Referència a la finestra principal per a la
     *                        navegació.
     */
    public VistaGestionarRespostes(CtrlPresentacio ctrlPresentacio, VistaPrincipal vistaPrincipal) {
        this.iCtrlPresentacio = ctrlPresentacio;
        this.vistaPrincipal = vistaPrincipal;
        inicializarComponentes();
    }

    /**
     * Inicialitza i configura tots els components gràfics de la vista.
     * 
     * Crea una interfície amb tres seccions:
     * 
     * Adalt: Títol amb icona d'enquestes contestades (📋).
     * Mig: Llista desplaçable d'enquestes contestades per l'usuari.
     * Abaix: Botons d'acció (Veure/Modificar, Esborrar Totes, Tornar).
     * 
     * 
     * Configura els listeners per habilitar/deshabilitar els botons segons la
     * selecció de la llista.
     */
    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIStyles.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Título
        JLabel lblTitol = new JLabel("📋 Les Meves Enquestes Contestades");
        lblTitol.setFont(UIStyles.FONT_VIEW_TITLE);
        lblTitol.setForeground(UIStyles.TEXT_COLOR);
        add(lblTitol, BorderLayout.NORTH);

        // Lista
        listEnquestes.setFont(UIStyles.FONT_NORMAL);
        listEnquestes.setFixedCellHeight(40);
        listEnquestes.setSelectionBackground(UIStyles.SELECTION_COLOR);
        JScrollPane scroll = new JScrollPane(listEnquestes);
        scroll.setBorder(BorderFactory.createTitledBorder("Selecciona una enquesta"));
        add(scroll, BorderLayout.CENTER);

        // Botones - organizados en dos filas
        JPanel panelBotonsContainer = new JPanel();
        panelBotonsContainer.setLayout(new BoxLayout(panelBotonsContainer, BoxLayout.Y_AXIS));
        panelBotonsContainer.setBackground(UIStyles.BACKGROUND_COLOR);

        // Primera fila - Acciones sobre respostes
        JPanel panelBotons1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelBotons1.setBackground(UIStyles.BACKGROUND_COLOR);

        btnModificar = UIComponents.createColorButton("👁️ Veure/Modificar", UIStyles.PRIMARY_COLOR);
        btnEsborrar = UIComponents.createColorButton("🗑️ Esborrar Totes", UIStyles.ERROR_COLOR);
        btnImportar = UIComponents.createColorButton("📥 Importar Resposta", UIStyles.FOREST_GREEN);

        panelBotons1.add(btnModificar);
        panelBotons1.add(btnEsborrar);
        panelBotons1.add(btnImportar);

        // Segona fila - Perfils i navegació
        JPanel panelBotons2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelBotons2.setBackground(UIStyles.BACKGROUND_COLOR);

        btnVeurePerfilEnquesta = UIComponents.createColorButton("👤 Veure el Meu Perfil", UIStyles.SUCCESS_COLOR);
        btnVeureTotsPerfils = UIComponents.createColorButton("📋 Tots els Meus Perfils", UIStyles.WARNING_COLOR);
        btnTornar = UIComponents.createColorButton("← Tornar", UIStyles.SECONDARY_COLOR);

        panelBotons2.add(btnVeurePerfilEnquesta);
        panelBotons2.add(btnVeureTotsPerfils);
        panelBotons2.add(btnTornar);

        panelBotonsContainer.add(panelBotons1);
        panelBotonsContainer.add(panelBotons2);
        add(panelBotonsContainer, BorderLayout.SOUTH);

        // Estado inicial
        btnModificar.setEnabled(false);
        btnEsborrar.setEnabled(false);
        btnVeurePerfilEnquesta.setEnabled(false);

        // Listeners
        listEnquestes.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean selected = !listEnquestes.isSelectionEmpty();
                btnModificar.setEnabled(selected);
                btnEsborrar.setEnabled(selected);
                btnVeurePerfilEnquesta.setEnabled(selected);
            }
        });

        btnModificar.setActionCommand(MyActionListener.Action.MODIFICAR_RESPOSTA.name());
        btnModificar.addActionListener(new MyActionListener(iCtrlPresentacio, vistaPrincipal, this));

        btnEsborrar.setActionCommand(MyActionListener.Action.ELIMINAR_RESPOSTA.name());
        btnEsborrar.addActionListener(new MyActionListener(iCtrlPresentacio, vistaPrincipal, this));

        btnImportar.setActionCommand(MyActionListener.Action.IMPORTAR_RESPOSTA.name());
        btnImportar.addActionListener(new MyActionListener(iCtrlPresentacio, vistaPrincipal, this));

        btnVeurePerfilEnquesta.setActionCommand(MyActionListener.Action.VEURE_PERFIL_ENQUESTA.name());
        btnVeurePerfilEnquesta.addActionListener(new MyActionListener(iCtrlPresentacio, vistaPrincipal, this));

        btnVeureTotsPerfils.setActionCommand(MyActionListener.Action.VEURE_TOTS_PERFILS.name());
        btnVeureTotsPerfils.addActionListener(new MyActionListener(iCtrlPresentacio, vistaPrincipal, this));

        btnTornar.setActionCommand(MyActionListener.Action.TORNAR_MENU.name());
        btnTornar.addActionListener(new MyActionListener(iCtrlPresentacio, vistaPrincipal, this));
    }

    /**
     * Actualitza la llista d'enquestes amb les enquestes contestades per
     * l'usuari actual.
     * 
     * Neteja el model de la llista i afegeix totes les enquestes contestades
     * obtingudes del controlador. Si l'usuari no ha contestat cap enquesta,
     * mostra un missatge indicatiu i deshabilita la llista.
     * 
     * Aquest mètode és invocat des de {@link VistaPrincipal#mostrarVista(String)}
     * cada vegada que es mostra aquesta vista, i també després d'esborrar
     * respostes.
     */
    public void actualizarLista() {
        listModel.clear();
        ArrayList<ArrayList<String>> contestades = iCtrlPresentacio.getEnquestesContestades();
        if (contestades.isEmpty()) {
            listModel.addElement("No has contestat cap enquesta encara.");
            listEnquestes.setEnabled(false);
        } else {
            listEnquestes.setEnabled(true);
            // [0] ID, [1] Title
            for (ArrayList<String> e : contestades) {
                listModel.addElement(e.get(0) + ": " + e.get(1));
            }
        }
    }
}

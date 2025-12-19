package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Vista per a l'anàlisi de clustering de les enquestes de l'usuari.
 * 
 * Aquesta vista permet a l'usuari seleccionar les seves enquestes creades i
 * executar algorismes de clustering sobre les respostes rebudes. També ofereix
 * la possibilitat de consultar el perfil de clustering generat per a l'usuari.
 * 
 * Funcionalitats clau:
 * 
 * Llista de totes les enquestes creades per l'usuari autenticat.
 * Botó per analitzar una enquesta seleccionada (obre DialogoAnalisi).
 * Botó per veure el perfil de clustering de l'usuari (obre DialogoPerfil).
 * Deshabilitació del botó "Analitzar" quan no hi ha selecció.
 * Actualització dinàmica de la llista quan es mostra la vista.
 * Navegació de tornada al menú principal.
 * 
 * 
 * La vista s'actualitza automàticament mitjançant el mètode
 * {@link #actualizarLista()} cada vegada que es mostra des de
 * {@link VistaPrincipal}.
 */
public class VistaAnalisi extends JPanel {

    /** Controlador de presentació per executar operacions d'anàlisi. */
    private CtrlPresentacio iCtrlPresentacio;
    /** Referència a la vista principal per permetre la navegació. */
    private VistaPrincipal vistaPrincipal;

    /** Model de dades per a la llista d'enquestes. */
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    /** Component visual que mostra la llista d'enquestes. */
    JList<String> listEnquestes = new JList<>(listModel);
    /** Botons d'acció de la vista. */
    private JButton btnAnalitzar, btnVeureAnalisi, btnTornar;

    /**
     * Constructor de la vista d'anàlisi de clustering.
     * 
     * Inicialitza la vista, enllaça amb el controlador i la vista principal,
     * i construeix la interfície d'usuari.
     *
     * @param ctrlPresentacio Controlador de presentació per a les operacions
     *                        d'anàlisi.
     * @param vistaPrincipal  Referència a la finestra principal per a la
     *                        navegació.
     */
    public VistaAnalisi(CtrlPresentacio ctrlPresentacio, VistaPrincipal vistaPrincipal) {
        this.iCtrlPresentacio = ctrlPresentacio;
        this.vistaPrincipal = vistaPrincipal;
        inicializarComponentes();
    }

    /**
     * Inicialitza i configura tots els components gràfics de la vista.
     * 
     * Crea una interfície amb tres seccions:
     * 
     * Adalt: Títol amb icona de clustering (📊).
     * Mig: Llista desplaçable d'enquestes de l'usuari.
     * Abaix: Botons d'acció (Analitzar, Veure Perfil, Tornar).
     * 
     * 
     * Configura els listeners per habilitar/deshabilitar el botó d'analitzar
     * segons la selecció de la llista.
     */
    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIStyles.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Título
        JLabel lblTitol = new JLabel("📊 Anàlisi de Clustering");
        lblTitol.setFont(UIStyles.FONT_VIEW_TITLE);
        lblTitol.setForeground(UIStyles.TEXT_COLOR);
        add(lblTitol, BorderLayout.NORTH);

        // Lista
        listEnquestes.setFont(UIStyles.FONT_NORMAL);
        listEnquestes.setFixedCellHeight(40);
        listEnquestes.setSelectionBackground(UIStyles.SELECTION_COLOR);
        JScrollPane scroll = new JScrollPane(listEnquestes);
        scroll.setBorder(BorderFactory.createTitledBorder("Les meves enquestes"));
        add(scroll, BorderLayout.CENTER);

        // Botones
        JPanel panelBotons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotons.setBackground(UIStyles.BACKGROUND_COLOR);

        btnAnalitzar = UIComponents.createColorButton("📈 Analitzar Enquesta", UIStyles.PRIMARY_COLOR);
        btnVeureAnalisi = UIComponents.createColorButton("📁 Veure Anàlisi", UIStyles.SUCCESS_COLOR);
        btnTornar = UIComponents.createColorButton("← Tornar", UIStyles.SECONDARY_COLOR);

        panelBotons.add(btnAnalitzar);
        panelBotons.add(btnVeureAnalisi);
        panelBotons.add(btnTornar);
        add(panelBotons, BorderLayout.SOUTH);

        // Estado inicial
        btnAnalitzar.setEnabled(false);
        btnVeureAnalisi.setEnabled(false);

        // Listeners
        listEnquestes.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = !listEnquestes.isSelectionEmpty();
                btnAnalitzar.setEnabled(hasSelection);
                btnVeureAnalisi.setEnabled(hasSelection);
            }
        });

        btnAnalitzar.setActionCommand(MyActionListener.Action.ANALITZAR_ENQUESTA.name());
        btnAnalitzar.addActionListener(new MyActionListener(iCtrlPresentacio, vistaPrincipal, this));

        btnVeureAnalisi.setActionCommand(MyActionListener.Action.VEURE_ANALISI_ENQUESTA.name());
        btnVeureAnalisi.addActionListener(new MyActionListener(iCtrlPresentacio, vistaPrincipal, this));

        btnTornar.setActionCommand(MyActionListener.Action.TORNAR_MENU.name());
        btnTornar.addActionListener(new MyActionListener(iCtrlPresentacio, vistaPrincipal, this));
    }

    /**
     * Actualitza la llista d'enquestes amb les enquestes creades per l'usuari
     * actual.
     * 
     * Neteja el model de la llista i afegeix totes les enquestes obtingudes
     * del controlador. Si l'usuari no té enquestes, mostra un missatge indicatiu
     * i deshabilita la llista.
     * 
     * Aquest mètode és invocat des de {@link VistaPrincipal#mostrarVista(String)}
     * cada vegada que es mostra aquesta vista.
     */
    public void actualizarLista() {
        listModel.clear();
        ArrayList<ArrayList<String>> enquestes = iCtrlPresentacio.getEnquestesUsuari();
        if (enquestes.isEmpty()) {
            listModel.addElement("No tens enquestes creades.");
            listEnquestes.setEnabled(false);
        } else {
            listEnquestes.setEnabled(true);
            for (ArrayList<String> e : enquestes) {
                listModel.addElement(e.get(0) + ": " + e.get(1));
            }
        }
    }
}

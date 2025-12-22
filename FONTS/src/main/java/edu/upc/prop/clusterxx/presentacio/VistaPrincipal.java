package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import java.awt.*;

/**
 * Vista principal de l'aplicació que actua com a contenidor mestre i gestor de
 * navegació.
 * 
 * Aquesta classe esten la funcionalitat bàsica de la interfície gràfica
 * proporcionant un
 * marc (JFrame) que conté totes les sub-vistes de l'aplicació. Utilitza un
 * esquema de disseny
 * {@link CardLayout} per alternar entre les diferents pantalles (Login,
 * Registre, Menú, Gestió, etc.)
 * sense necessitat d'obrir múltiples finestres.
 * 
 * Responsabilitats principals:
 * 
 * Inicialitzar i mantenir les referències a totes les vistes
 * secundàries.
 * Gestionar la barra de menú superior global (Tancar sessió, Sortir,
 * etc.).
 * Coordinar la navegació centralitzada mitjançant el mètode
 * {@link #mostrarVista(String)}.
 */
public class VistaPrincipal {
    /** Referència al controlador de la capa de presentació. */
    private CtrlPresentacio iCtrlPresentacio;
    /** El marc principal (finestra) de l'aplicació. */
    private JFrame frameVista = new JFrame("Gestor d'Enquestes");

    // Panel principal con CardLayout para cambiar entre pantallas
    /**
     * Panell contenidor que gestiona les diverses vistes amb un layout de targetes.
     */
    private JPanel panelContenidos = new JPanel(new CardLayout());
    /** Gestor de layout per alternar la visibilitat de les vistes (CardLayout). */
    private CardLayout cardLayout;

    private JMenuBar menubarVista = new JMenuBar();
    private JMenu menuFile = new JMenu("File");
    private JMenuItem menuitemLogout = new JMenuItem("Tancar sessió");
    private JMenuItem menuitemDeleteAccount = new JMenuItem("Esborrar compte"); // Nuevo item
    private JMenuItem menuitemQuit = new JMenuItem("Sortir");

    // Vistas secundarias
    private VistaLogin vistaLogin;
    private VistaRegistro vistaRegistro;
    private VistaMenuPrincipal vistaMenuPrincipal;
    private VistaGestionEnquestes vistaGestionEnquestes;
    private VistaGestionarRespostes vistaGestionarRespostes; // Nueva vista
    private VistaAnalisi vistaAnalisi;

    /**
     * Construeix la VistaPrincipal i prepara l'entorn gràfic de l'aplicació.
     *
     * @param pCtrlPresentacio Instància del controlador de presentació principal
     *                         que s'utilitzarà
     *                         per comunicar accions globals.
     */
    public VistaPrincipal(CtrlPresentacio pCtrlPresentacio) {
        iCtrlPresentacio = pCtrlPresentacio;
        inicializarComponentes();
    }

    /**
     * Fa visible la finestra principal de l'aplicació a l'usuari.
     * 
     * Aquest mètode s'ha de cridar un cop tota la inicialització ha finalitzat.
     * Ajusta la mida del contingut (pack), defineix una mida fixa inicial (900x700)
     * i centra la finestra a la pantalla de l'usuari.
     */
    public void hacerVisible() {
        frameVista.pack();
        frameVista.setSize(900, 800);
        frameVista.setLocationRelativeTo(null);
        frameVista.setVisible(true);
    }

    /**
     * Mètode privat encarregat de la inicialització exhaustiva de tots els
     * components de la GUI.
     * 
     * Realitza les següents tasques:
     * 
     * Configura el comportament de tancament de la finestra.
     * Instància totes les vistes secundàries (Login, Registre, Menú, etc.)
     * passant-los
     * les referències necessàries (Controlador i aquesta VistaPrincipal).
     * Afegeix aquestes vistes al panell principal amb identificadors de cadena
     * únics
     * per al {@link CardLayout} ("LOGIN", "MENU", etc.).
     * Construeix i configura la barra de menú superior amb les opcions
     * globals.
     * Assigna els {@link java.awt.event.ActionListener} als elements del
     * menú.
     */
    private void inicializarComponentes() {
        // Configuración del Frame
        frameVista.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Configuración del CardLayout
        cardLayout = (CardLayout) panelContenidos.getLayout();

        // Inicializar Vistas
        vistaLogin = new VistaLogin(iCtrlPresentacio, this);
        vistaRegistro = new VistaRegistro(iCtrlPresentacio, this);
        vistaMenuPrincipal = new VistaMenuPrincipal(iCtrlPresentacio, this);
        vistaGestionEnquestes = new VistaGestionEnquestes(iCtrlPresentacio, this);
        vistaGestionarRespostes = new VistaGestionarRespostes(iCtrlPresentacio, this); // Nueva vista
        vistaAnalisi = new VistaAnalisi(iCtrlPresentacio, this);

        // Añadir paneles al CardLayout
        panelContenidos.add(vistaLogin, "LOGIN");
        panelContenidos.add(vistaRegistro, "REGISTER");
        panelContenidos.add(vistaMenuPrincipal, "MENU");
        panelContenidos.add(vistaGestionEnquestes, "GESTION");
        panelContenidos.add(vistaGestionarRespostes, "GESTION_RESPOSTES"); // Añadir al layout
        panelContenidos.add(vistaAnalisi, "ANALISI");

        frameVista.setContentPane(panelContenidos);

        // Configuración del Menú
        menuFile.add(menuitemLogout);
        menuFile.add(menuitemDeleteAccount); // Añadir al menú
        menuFile.addSeparator();
        menuFile.add(menuitemQuit);
        menubarVista.add(menuFile);
        frameVista.setJMenuBar(menubarVista);

        // --- Listeners ---
        menuitemLogout.setActionCommand(MyActionListener.Action.LOGOUT.name());
        menuitemLogout.addActionListener(new MyActionListener(iCtrlPresentacio, this));

        menuitemDeleteAccount.setActionCommand(MyActionListener.Action.ELIMINAR_COMPTE.name());
        menuitemDeleteAccount.addActionListener(new MyActionListener(iCtrlPresentacio, this));

        menuitemQuit.addActionListener(e -> System.exit(0));
    }

    /**
     * Realitza el canvi de pantalla visible utilitzant el {@link CardLayout}.
     * 
     * Aquest és el mètode central per a la navegació dins de l'aplicació. A més de
     * canviar
     * la vista visible, s'encarrega d'invocar mètodes d'actualització específics
     * (`actualizarLista()`) en vistes com la de gestió o anàlisi, assegurant que
     * les dades
     * mostrades estiguin sempre al dia en accedir-hi.
     *
     * @param nombreVista Identificador de cadena de la vista destí. Valors vàlids
     *                    inclouen:
     *                    "LOGIN": Pantalla d'accés.
     *                    "REGISTER": Pantalla de registre.
     *                    "MENU": Menú principal.
     *                    "GESTION": Gestió d'enquestes pròpies.
     *                    "GESTION_RESPOSTES": Gestió de respostes de
     *                    l'usuari.
     *                    "ANALISI": Vista d'algorismes de clustering.
     */
    public void mostrarVista(String nombreVista) {
        cardLayout.show(panelContenidos, nombreVista);
        if ("GESTION".equals(nombreVista)) {
            vistaGestionEnquestes.actualizarLista();
        } else if ("GESTION_RESPOSTES".equals(nombreVista)) {
            vistaGestionarRespostes.actualizarLista();
        } else if ("ANALISI".equals(nombreVista)) {
            vistaAnalisi.actualizarLista();
        } else if ("LOGIN".equals(nombreVista)) {
            vistaLogin.limpiarCampos();
        } else if ("REGISTER".equals(nombreVista)) {
            vistaRegistro.limpiarCampos();
        }
    }
}

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
        frameVista.setSize(900, 700);
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
        menuitemLogout.addActionListener(e -> cerrarSesion());
        menuitemDeleteAccount.addActionListener(e -> eliminarCuenta());
        menuitemQuit.addActionListener(e -> System.exit(0));
    }

    /**
     * Executa el procés de tancament de sessió de l'usuari.
     * 
     * Notifica al controlador per realitzar el logout lògic i, posteriorment,
     * redirigeix la interfície gràfica a la vista d'inici de sessió ("LOGIN").
     * Aquest mètode neteja l'estat visual necessari per garantir que el següent
     * usuari no vegi dades residuals.
     */
    private void cerrarSesion() {
        iCtrlPresentacio.logout();
        // Limpiar la vista de gestión (opcional, pero recomendable)
        // vistaGestionEnquestes.limpiar(); // Si tuviéramos un método limpiar
        mostrarVista("LOGIN");
    }

    /**
     * Gestiona el procés crític d'eliminació del compte de l'usuari actual.
     * 
     * Mostra un diàleg de confirmació per evitar esborrats accidentals.
     * Si l'usuari confirma, sol·licita al controlador l'eliminació permanent de les
     * dades.
     * En cas d'èxit, redirigeix automàticament a la pantalla de login.
     */
    private void eliminarCuenta() {
        int confirm = JOptionPane.showConfirmDialog(frameVista,
                "¿Estàs segur de que vols esborrar el teu compte? Aquesta acció és irreversible.",
                "Eliminar compte", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String resultado = iCtrlPresentacio.esborrarUsuariActual();
            JOptionPane.showMessageDialog(frameVista, resultado);
            if (resultado.contains("correctament")) {
                mostrarVista("LOGIN");
            }
        }
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
        }
    }
}

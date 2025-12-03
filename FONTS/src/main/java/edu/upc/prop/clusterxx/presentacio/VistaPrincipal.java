package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import java.awt.*;

public class VistaPrincipal {
    private CtrlPresentacio iCtrlPresentacio;
    private JFrame frameVista = new JFrame("Gestor d'Enquestes");

    // Panel principal con CardLayout para cambiar entre pantallas
    private JPanel panelContenidos = new JPanel(new CardLayout());
    private CardLayout cardLayout;

    private JMenuBar menubarVista = new JMenuBar();
    private JMenu menuFile = new JMenu("File");
    private JMenuItem menuitemLogout = new JMenuItem("Logout");
    private JMenuItem menuitemDeleteAccount = new JMenuItem("Esborrar compte"); // Nuevo item
    private JMenuItem menuitemQuit = new JMenuItem("Quit");

    // Vistas secundarias
    private VistaLogin vistaLogin;
    private VistaRegistro vistaRegistro;
    private VistaMenuPrincipal vistaMenuPrincipal;
    private VistaGestionEnquestes vistaGestionEnquestes;

    public VistaPrincipal(CtrlPresentacio pCtrlPresentacio) {
        iCtrlPresentacio = pCtrlPresentacio;
        inicializarComponentes();
    }

    public void hacerVisible() {
        frameVista.pack();
        frameVista.setSize(900, 600);
        frameVista.setLocationRelativeTo(null);
        frameVista.setVisible(true);
    }

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

        // Añadir paneles al CardLayout
        panelContenidos.add(vistaLogin, "LOGIN");
        panelContenidos.add(vistaRegistro, "REGISTER");
        panelContenidos.add(vistaMenuPrincipal, "MENU");
        panelContenidos.add(vistaGestionEnquestes, "GESTION");

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
     * Cierra la sesión actual y vuelve a la pantalla de login.
     */
    private void cerrarSesion() {
        iCtrlPresentacio.logout();
        // Limpiar la vista de gestión (opcional, pero recomendable)
        // vistaGestionEnquestes.limpiar(); // Si tuviéramos un método limpiar
        mostrarVista("LOGIN");
    }

    /**
     * Elimina la cuenta del usuario actual.
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
     * Método para cambiar de vista desde las sub-vistas.
     * 
     * @param nombreVista Nombre de la vista ("LOGIN", "REGISTER", "MENU",
     *                    "GESTION")
     */
    public void mostrarVista(String nombreVista) {
        cardLayout.show(panelContenidos, nombreVista);
        if ("GESTION".equals(nombreVista)) {
            vistaGestionEnquestes.actualizarLista();
        }
    }
}

package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import java.awt.*;

/**
 * Vista central de navegació i funcionalitats principals de l'aplicació.
 * 
 * Aquesta classe actua com a "hub" o centre de comandament un cop l'usuari ha
 * iniciat sessió.
 * Proporciona un accés clar i categoritzat a totes les funcions del sistema,
 * com ara:
 * Crear noves enquestes (manualment o per importació).
 * Gestionar les enquestes pròpies.
 * Respondre enquestes d'altres usuaris.
 * Realitzar anàlisis de dades (clustering).
 * 
 * Utilitza un disseny de botons grans i categoritzats per colors per facilitar
 * la usabilitat.
 */
public class VistaMenuPrincipal extends JPanel {
    /** Controlador de presentació per gestionar les accions del menú. */
    private CtrlPresentacio iCtrlPresentacio;
    /** Referència a la vista principal per canviar entre pantalles. */
    private VistaPrincipal vistaPrincipal;

    private JButton btnNueva = new JButton("Nova enquesta");
    private JButton btnImportar = new JButton("Importar enquesta");
    private JButton btnGestionar = new JButton("Gestionar les meves enquestes");
    private JButton btnAnalisi = new JButton("Anàlisi de Clustering");
    private JButton btnRespondre = new JButton("Respondre Enquesta");
    private JButton btnGestionarRespostes = new JButton("Gestionar Les Meves Respostes");
    private JButton btnLogout = new JButton("Tancar sessió");
    private JButton btnEliminarCompte = new JButton("Eliminar el meu compte");

    /**
     * Constructor de la classe VistaMenuPrincipal.
     * 
     * Inicialitza els components visuals i estableix les dependències necessàries.
     *
     * @param ctrlPresentacio Controlador de presentació per delegar la lògica de
     *                        negoci.
     * @param vistaPrincipal  Marc principal de l'aplicació per a la navegació entre
     *                        vistes.
     */
    public VistaMenuPrincipal(CtrlPresentacio ctrlPresentacio, VistaPrincipal vistaPrincipal) {
        this.iCtrlPresentacio = ctrlPresentacio;
        this.vistaPrincipal = vistaPrincipal;
        inicializarComponentes();
    }

    /**
     * Construeix la interfície gràfica del menú principal.
     * 
     * Organitza les funcionalitats en seccions lògiques ("Crear", "Gestionar",
     * "Respondre")
     * utilitzant un disseny vertical net i espaiat. Aplica estils visuals
     * diferenciats
     * per grups de funcionalitats (blau per creació, verd per gestió, taronja per
     * accions d'usuari).
     */
    private void inicializarComponentes() {
        this.setBackground(UIStyles.BACKGROUND_COLOR);
        this.setLayout(new GridBagLayout());

        // Panel tarjeta
        JPanel cardPanel = UIComponents.createCardPanel();

        // Icono
        cardPanel.add(UIComponents.createIconLabel("📊"));
        cardPanel.add(Box.createVerticalStrut(10));

        // Título
        cardPanel.add(UIComponents.createTitleLabel("Gestor d'Enquestes"));

        // Subtítulo
        JLabel subtitle = new JLabel("Què vols fer avui?");
        subtitle.setFont(UIStyles.FONT_SUBTITLE);
        subtitle.setForeground(UIStyles.SECONDARY_COLOR);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(subtitle);
        cardPanel.add(Box.createVerticalStrut(30));

        // Sección: Crear enquestes
        cardPanel.add(UIComponents.createSectionLabel("Crear"));
        cardPanel.add(Box.createVerticalStrut(8));
        UIComponents.styleButton(btnNueva, UIStyles.PRIMARY_COLOR);
        btnNueva.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(btnNueva);
        cardPanel.add(Box.createVerticalStrut(8));
        UIComponents.styleButton(btnImportar, UIStyles.PRIMARY_COLOR);
        btnImportar.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(btnImportar);
        cardPanel.add(Box.createVerticalStrut(20));

        // Sección: Gestionar
        cardPanel.add(UIComponents.createSectionLabel("Gestionar"));
        cardPanel.add(Box.createVerticalStrut(8));
        UIComponents.styleButton(btnGestionar, UIStyles.SUCCESS_COLOR);
        btnGestionar.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(btnGestionar);
        cardPanel.add(Box.createVerticalStrut(8));
        UIComponents.styleButton(btnAnalisi, UIStyles.SUCCESS_COLOR);
        btnAnalisi.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(btnAnalisi);
        cardPanel.add(Box.createVerticalStrut(20));

        // Sección: Respondre
        cardPanel.add(UIComponents.createSectionLabel("Respondre"));
        cardPanel.add(Box.createVerticalStrut(8));
        UIComponents.styleButton(btnRespondre, UIStyles.WARNING_COLOR);
        btnRespondre.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(btnRespondre);
        cardPanel.add(Box.createVerticalStrut(8));
        UIComponents.styleButton(btnGestionarRespostes, UIStyles.WARNING_COLOR);
        btnGestionarRespostes.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(btnGestionarRespostes);
        cardPanel.add(Box.createVerticalStrut(25));

        // Separador
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(300, 1));
        separator.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(separator);
        cardPanel.add(Box.createVerticalStrut(15));

        // Botón logout
        UIComponents.styleButton(btnLogout, UIStyles.ERROR_COLOR);
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(btnLogout);
        cardPanel.add(Box.createVerticalStrut(8));

        // Botón eliminar compte
        UIComponents.styleButton(btnEliminarCompte, UIStyles.DARK_RED);
        btnEliminarCompte.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(btnEliminarCompte);

        // Listeners
        btnNueva.setActionCommand(MyActionListener.Action.CREAR_ENQUESTA.name());
        btnNueva.addActionListener(new MyActionListener(iCtrlPresentacio, vistaPrincipal, this));

        btnImportar.setActionCommand(MyActionListener.Action.IMPORTAR_ENQUESTA.name());
        btnImportar.addActionListener(new MyActionListener(iCtrlPresentacio, vistaPrincipal, this));

        btnGestionar.setActionCommand(MyActionListener.Action.GESTIONAR_ENQUESTES.name());
        btnGestionar.addActionListener(new MyActionListener(iCtrlPresentacio, vistaPrincipal, this));

        btnAnalisi.addActionListener(e -> vistaPrincipal.mostrarVista("ANALISI"));

        btnRespondre.setActionCommand(MyActionListener.Action.RESPONDRE_ENQUESTA.name());
        btnRespondre.addActionListener(new MyActionListener(iCtrlPresentacio, vistaPrincipal, this));

        btnGestionarRespostes.addActionListener(e -> vistaPrincipal.mostrarVista("GESTION_RESPOSTES"));

        btnLogout.setActionCommand(MyActionListener.Action.LOGOUT.name());
        btnLogout.addActionListener(new MyActionListener(iCtrlPresentacio, vistaPrincipal, this));

        btnEliminarCompte.setActionCommand(MyActionListener.Action.ELIMINAR_COMPTE.name());
        btnEliminarCompte.addActionListener(new MyActionListener(iCtrlPresentacio, vistaPrincipal, this));

        this.add(cardPanel);
    }
}

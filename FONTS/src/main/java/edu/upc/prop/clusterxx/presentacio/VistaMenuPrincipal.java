package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import javax.swing.border.*;
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

    // Colores del tema
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color PRIMARY_HOVER = new Color(52, 152, 219);
    private static final Color SECONDARY_COLOR = new Color(149, 165, 166);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);

    private JButton btnNueva = new JButton("Nova enquesta");
    private JButton btnImportar = new JButton("Importar enquesta");
    private JButton btnGestionar = new JButton("Gestionar les meves enquestes");
    private JButton btnAnalisi = new JButton("Anàlisi de Clustering");
    private JButton btnRespondre = new JButton("Respondre Enquesta");
    private JButton btnGestionarRespostes = new JButton("Gestionar Les Meves Respostes");
    private JButton btnLogout = new JButton("Tancar sessió");

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
        this.setBackground(BACKGROUND_COLOR);
        this.setLayout(new GridBagLayout());

        // Panel tarjeta
        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(CARD_COLOR);
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 3, new Color(0, 0, 0, 30)),
                BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(189, 195, 199), 1, true),
                        new EmptyBorder(40, 50, 40, 50))));

        // Icono
        JLabel iconLabel = new JLabel("📊");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(iconLabel);
        cardPanel.add(Box.createVerticalStrut(10));

        // Título
        JLabel title = new JLabel("Gestor d'Enquestes");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(title);

        // Subtítulo
        JLabel subtitle = new JLabel("Què vols fer avui?");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(SECONDARY_COLOR);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(subtitle);
        cardPanel.add(Box.createVerticalStrut(30));

        // Sección: Crear enquestes
        cardPanel.add(createSectionLabel("Crear"));
        cardPanel.add(Box.createVerticalStrut(8));
        styleButton(btnNueva, PRIMARY_COLOR);
        cardPanel.add(btnNueva);
        cardPanel.add(Box.createVerticalStrut(8));
        styleButton(btnImportar, PRIMARY_COLOR);
        cardPanel.add(btnImportar);
        cardPanel.add(Box.createVerticalStrut(20));

        // Sección: Gestionar
        cardPanel.add(createSectionLabel("Gestionar"));
        cardPanel.add(Box.createVerticalStrut(8));
        styleButton(btnGestionar, SUCCESS_COLOR);
        cardPanel.add(btnGestionar);
        cardPanel.add(Box.createVerticalStrut(8));
        styleButton(btnAnalisi, SUCCESS_COLOR);
        cardPanel.add(btnAnalisi);
        cardPanel.add(Box.createVerticalStrut(20));

        // Sección: Respondre
        cardPanel.add(createSectionLabel("Respondre"));
        cardPanel.add(Box.createVerticalStrut(8));
        styleButton(btnRespondre, WARNING_COLOR);
        cardPanel.add(btnRespondre);
        cardPanel.add(Box.createVerticalStrut(8));
        styleButton(btnGestionarRespostes, WARNING_COLOR);
        cardPanel.add(btnGestionarRespostes);
        cardPanel.add(Box.createVerticalStrut(25));

        // Separador
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(300, 1));
        cardPanel.add(separator);
        cardPanel.add(Box.createVerticalStrut(15));

        // Botón logout
        styleButton(btnLogout, new Color(231, 76, 60));
        cardPanel.add(btnLogout);

        // Listeners
        btnNueva.addActionListener(e -> mostrarDialogoCrear());
        btnImportar.addActionListener(e -> importarEnquesta());
        btnGestionar.addActionListener(e -> vistaPrincipal.mostrarVista("GESTION"));
        btnAnalisi.addActionListener(e -> vistaPrincipal.mostrarVista("ANALISI"));
        btnRespondre.addActionListener(e -> mostrarDialogoResponder());
        btnGestionarRespostes.addActionListener(e -> mostrarDialogoGestionarRespostes());
        btnLogout.addActionListener(e -> {
            iCtrlPresentacio.logout();
            vistaPrincipal.mostrarVista("LOGIN");
        });

        this.add(cardPanel);
    }

    /**
     * Mètode d'utilitat per crear capçaleres de secció estilitzades.
     *
     * @param text El títol de la secció (ex: "CREAR", "GESTIONAR").
     * @return Una {@link JLabel} configurada amb l'estil de títol de secció petit.
     */
    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(SECONDARY_COLOR);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    /**
     * Aplica l'estil visual personalitzat a un botó, incloent el color de fons i
     * efectes de ratolí.
     *
     * @param button El botó a configurar.
     * @param color  El color base del botó, que s'aclareix automàticament per a
     *               l'efecte "hover".
     */
    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setMaximumSize(new Dimension(300, 45));
        button.setPreferredSize(new Dimension(300, 45));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        Color hoverColor = color.brighter();
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(color);
            }
        });
    }

    /**
     * Obre un diàleg modal per a la creació d'una nova enquesta.
     * 
     * Espera a que l'usuari ompli les dades i confirmi. Si l'usuari confirma (botó
     * "Crear"),
     * recull les dades introduïdes i delega al controlador la creació real de
     * l'enquesta.
     * Finalment, mostra un missatge informatiu amb el resultat de l'operació.
     */
    private void mostrarDialogoCrear() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        DialogoEnquesta dialogo = new DialogoEnquesta(parentFrame, "Nova enquesta", true);
        dialogo.setVisible(true);

        if (dialogo.isConfirmado()) {
            String resultado = iCtrlPresentacio.crearEnquesta(dialogo.getId(), dialogo.getTitol(), dialogo.getDesc());
            JOptionPane.showMessageDialog(this, resultado);
        }
    }

    /**
     * Obre un selector de fitxers natiu (JFileChooser) per importar una enquesta.
     * 
     * Si l'usuari selecciona un fitxer vàlid, obté la seva ruta absoluta i crida al
     * controlador
     * per processar la importació. Mostra el resultat de l'operació a l'usuari.
     */
    private void importarEnquesta() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            String resultado = iCtrlPresentacio.importarEnquesta(selectedFile.getAbsolutePath());
            JOptionPane.showMessageDialog(this, resultado);
        }
    }

    /**
     * Inicia el flux de resposta a una enquesta.
     * 
     * El procés consta de dos passos:
     * 
     * Mostra un diàleg ({@link DialogoSeleccionarEnquesta}) perquè l'usuari
     * triï quina enquesta vol respondre.
     * Si es selecciona una enquesta, obre un segon diàleg
     * ({@link DialogoResponderEnquesta}) per a la resposta efectiva.
     */
    private void mostrarDialogoResponder() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        DialogoSeleccionarEnquesta dialogoSel = new DialogoSeleccionarEnquesta(parentFrame, iCtrlPresentacio);
        dialogoSel.setVisible(true);

        if (dialogoSel.isConfirmado()) {
            String idEnquesta = dialogoSel.getSelectedId();
            DialogoResponderEnquesta dialogoResp = new DialogoResponderEnquesta(parentFrame, iCtrlPresentacio,
                    idEnquesta);
            dialogoResp.setVisible(true);
        }
    }

    /**
     * Navega cap a la vista de gestió de les respostes realitzades per l'usuari.
     * Permet a l'usuari veure i administrar les enquestes que ha respost
     * anteriorment.
     */
    private void mostrarDialogoGestionarRespostes() {
        vistaPrincipal.mostrarVista("GESTION_RESPOSTES");
    }
}

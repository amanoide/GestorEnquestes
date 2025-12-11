package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Vista d'autenticació i punt d'entrada principal per als usuaris de
 * l'aplicació.
 * 
 * Aquesta classe gestiona el procés d'inici de sessió (login). És la primera
 * pantalla
 * que veu l'usuari (si no està ja autenticat).
 * 
 * Funcionalitats clau:
 * 
 * Recollida segura de credencials (nom d'usuari i contrasenya oculta).
 * Validació bàsica de camps buits.
 * Interacció amb el {@link CtrlPresentacio} per verificar la
 * identitat.
 * Redirecció al menú principal en cas d'èxit o al registre si es
 * sol·licita.
 */
public class VistaLogin extends JPanel {
    /** Controlador de presentació per gestionar l'autenticació. */
    private CtrlPresentacio iCtrlPresentacio;
    /**
     * Referència a la vista principal per navegar a altres pantalles (Menú,
     * Registre).
     */
    private VistaPrincipal vistaPrincipal;

    // Colores del tema
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color PRIMARY_HOVER = new Color(52, 152, 219);
    private static final Color SECONDARY_COLOR = new Color(149, 165, 166);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color ERROR_COLOR = new Color(231, 76, 60);

    private JTextField textUser = new JTextField(20);
    private JPasswordField textPass = new JPasswordField(20);
    private JButton btnLogin = new JButton("Iniciar sessió");
    private JButton btnGoToRegister = new JButton("Crear compte nou");
    private JLabel labelStatusLogin = new JLabel(" ");

    /**
     * Constructor de la classe VistaLogin.
     * 
     * Inicialitza la vista de login, enllaça amb el controlador i la vista mare,
     * i construeix la interfície d'usuari.
     *
     * @param ctrlPresentacio Controlador de presentació per a validar les
     *                        credencials.
     * @param vistaPrincipal  Referència a la finestra principal per permetre la
     *                        navegació.
     */
    public VistaLogin(CtrlPresentacio ctrlPresentacio, VistaPrincipal vistaPrincipal) {
        this.iCtrlPresentacio = ctrlPresentacio;
        this.vistaPrincipal = vistaPrincipal;
        inicializarComponentes();
    }

    /**
     * Configura i disposa els elements gràfics de la pantalla de login.
     * 
     * Crea un disseny centrat, net i modern utilitzant GridBagLayout i panells amb
     * vores compostes.
     * Afegeix icones, títols descripitius i els camps d'entrada necessaris.
     * També configura els listeners per als botons i l'acció d'enviar amb la tecla
     * Intro.
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
        JLabel iconLabel = new JLabel("🔐");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(iconLabel);
        cardPanel.add(Box.createVerticalStrut(10));

        // Título
        JLabel title = new JLabel("Benvingut!");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(title);

        // Subtítulo
        JLabel subtitle = new JLabel("Inicia sessió per continuar");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(SECONDARY_COLOR);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(subtitle);
        cardPanel.add(Box.createVerticalStrut(30));

        // Campo Usuario
        cardPanel.add(createLabel("Usuari"));
        cardPanel.add(Box.createVerticalStrut(5));
        styleTextField(textUser);
        cardPanel.add(textUser);
        cardPanel.add(Box.createVerticalStrut(15));

        // Campo Contraseña
        cardPanel.add(createLabel("Contrasenya"));
        cardPanel.add(Box.createVerticalStrut(5));
        styleTextField(textPass);
        cardPanel.add(textPass);
        cardPanel.add(Box.createVerticalStrut(25));

        // Botones
        styleButton(btnLogin, true);
        cardPanel.add(btnLogin);
        cardPanel.add(Box.createVerticalStrut(10));

        styleButton(btnGoToRegister, false);
        cardPanel.add(btnGoToRegister);
        cardPanel.add(Box.createVerticalStrut(15));

        // Status
        labelStatusLogin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelStatusLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(labelStatusLogin);

        // Listeners
        btnLogin.addActionListener(e -> actionPerformed_btnLogin(e));
        btnGoToRegister.addActionListener(e -> vistaPrincipal.mostrarVista("REGISTER"));
        textPass.addActionListener(e -> actionPerformed_btnLogin(e));

        this.add(cardPanel);
    }

    /**
     * Helper per crear etiquetes estilitzades.
     *
     * @param text Text de l'etiqueta.
     * @return JLabel configurat.
     */
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(TEXT_COLOR);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    /**
     * Aplica estils comuns als camps de text de login.
     *
     * @param field Camp de text a personalitzar.
     */
    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(280, 40));
        field.setPreferredSize(new Dimension(280, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(189, 195, 199), 1, true),
                new EmptyBorder(8, 12, 8, 12)));
    }

    /**
     * Estilitza els botons de la pantalla de login.
     *
     * @param button    Referència al botó.
     * @param isPrimary Defineix si és el botó principal (Login) o secundari (Crear
     *                  compte).
     */
    private void styleButton(JButton button, boolean isPrimary) {
        button.setFont(new Font("Segoe UI", isPrimary ? Font.BOLD : Font.PLAIN, 14));
        button.setMaximumSize(new Dimension(280, 45));
        button.setPreferredSize(new Dimension(280, 45));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (isPrimary) {
            button.setForeground(Color.WHITE);
            button.setBackground(PRIMARY_COLOR);
            button.setBorderPainted(false);
            button.setContentAreaFilled(true);
            button.setOpaque(true);
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    button.setBackground(PRIMARY_HOVER);
                }

                public void mouseExited(java.awt.event.MouseEvent e) {
                    button.setBackground(PRIMARY_COLOR);
                }
            });
        } else {
            button.setForeground(PRIMARY_COLOR);
            button.setBackground(CARD_COLOR);
            button.setBorder(new LineBorder(PRIMARY_COLOR, 1, true));
            button.setContentAreaFilled(true);
            button.setOpaque(true);
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    button.setBackground(new Color(235, 245, 251));
                }

                public void mouseExited(java.awt.event.MouseEvent e) {
                    button.setBackground(CARD_COLOR);
                }
            });
        }
    }

    /**
     * Gestiona la lògica d'inici de sessió en confirmar el formulari.
     * 
     * Passos:
     * 
     * Verifica que no hi hagi camps buits.
     * Crida a {@link CtrlPresentacio#login(String, String)} amb les dades.
     * Si el login és correcte, neteja errors i navega al menú principal
     * ("MENU").
     * Si és incorrecte, mostra un missatge d'error a l'usuari.
     *
     * @param event Esdeveniment generat pel botó o teclat.
     */
    public void actionPerformed_btnLogin(ActionEvent event) {
        String user = textUser.getText().trim();
        String pass = new String(textPass.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            labelStatusLogin.setText("⚠ Camps obligatoris");
            labelStatusLogin.setForeground(ERROR_COLOR);
            return;
        }

        if (iCtrlPresentacio.login(user, pass)) {
            labelStatusLogin.setText(" ");
            vistaPrincipal.mostrarVista("MENU");
        } else {
            labelStatusLogin.setText("⚠ Credencials incorrectes");
            labelStatusLogin.setForeground(ERROR_COLOR);
        }
    }
}

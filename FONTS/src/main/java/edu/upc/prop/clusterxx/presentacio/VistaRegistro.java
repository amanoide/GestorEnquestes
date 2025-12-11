package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Vista encarregada de la gestió del registre de nous usuaris dins l'aplicació.
 * 
 * Aquesta classe proporciona una interfície gràfica intuïtiva que permet als
 * usuaris introduir
 * les seves credencials (nom d'usuari i contrasenya) per crear un nou compte al
 * sistema.
 * 
 * Les seves responsabilitats principals inclouen:
 * 
 * Presentar un formulari clar per a la introducció de dades de registre.
 * Realitzar validacions prèvies a la part del client (camps buits,
 * coincidència de contrasenyes).
 * Comunicar-se amb el {@link CtrlPresentacio} per processar la lògica de
 * negoci del registre.
 * Gestionar la navegació de tornada a la vista de login o informar de
 * l'èxit/fracàs del procés.
 */
public class VistaRegistro extends JPanel {
    /** Controlador de presentació per delegar les accions de registre. */
    private CtrlPresentacio iCtrlPresentacio;
    /**
     * Referència a la vista principal per permetre la navegació entre pantalles.
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

    private JTextField textRegUser = new JTextField(20);
    private JPasswordField textRegPass = new JPasswordField(20);
    private JPasswordField textRegPassConfirm = new JPasswordField(20);
    private JButton btnRegistrar = new JButton("Crear Compte");
    private JButton btnBackToLogin = new JButton("Ja tinc un compte");
    private JLabel labelStatusRegistro = new JLabel(" ");

    /**
     * Constructor de la classe VistaRegistro.
     * 
     * Inicialitza la vista, assigna el controlador de presentació i la vista
     * principal,
     * i construeix tots els components de la interfície d'usuari.
     *
     * @param ctrlPresentacio Instància del controlador de presentació per a la
     *                        gestió d'usuaris.
     * @param vistaPrincipal  Referència a la finestra principal (JFrame) que conté
     *                        aquesta vista, utilitzada per a la navegació.
     */
    public VistaRegistro(CtrlPresentacio ctrlPresentacio, VistaPrincipal vistaPrincipal) {
        this.iCtrlPresentacio = ctrlPresentacio;
        this.vistaPrincipal = vistaPrincipal;
        inicializarComponentes();
    }

    /**
     * Inicialitza, configura i disposa els components gràfics de la vista.
     * 
     * Aquest mètode configura el layout (GridBagLayout per centrar el contingut),
     * aplica els colors
     * de fons corporatius i construeix el panell central "tipus targeta" que conté
     * el formulari.
     * També s'encarrega d'assignar els listeners als botons i camps de text per
     * gestionar la interactivitat.
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

        // Icona
        JLabel iconLabel = new JLabel("👤");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(iconLabel);
        cardPanel.add(Box.createVerticalStrut(10));

        // Títol
        JLabel title = new JLabel("Crear Compte");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(title);

        // Subtítol
        JLabel subtitle = new JLabel("Registra't per començar");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(SECONDARY_COLOR);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(subtitle);
        cardPanel.add(Box.createVerticalStrut(30));

        // Camp Usuari
        cardPanel.add(createLabel("Nom d'usuari (mínim 3 caràcters)"));
        cardPanel.add(Box.createVerticalStrut(5));
        styleTextField(textRegUser);
        cardPanel.add(textRegUser);
        cardPanel.add(Box.createVerticalStrut(15));

        // Camp Contrasenya
        cardPanel.add(createLabel("Contrasenya (mínim 4 caràcters)"));
        cardPanel.add(Box.createVerticalStrut(5));
        styleTextField(textRegPass);
        cardPanel.add(textRegPass);
        cardPanel.add(Box.createVerticalStrut(15));

        // Camp Confirmar Contrasenya
        cardPanel.add(createLabel("Confirmar Contrasenya"));
        cardPanel.add(Box.createVerticalStrut(5));
        styleTextField(textRegPassConfirm);
        cardPanel.add(textRegPassConfirm);
        cardPanel.add(Box.createVerticalStrut(25));

        // Botons
        styleButton(btnRegistrar, true);
        cardPanel.add(btnRegistrar);
        cardPanel.add(Box.createVerticalStrut(10));

        styleButton(btnBackToLogin, false);
        cardPanel.add(btnBackToLogin);
        cardPanel.add(Box.createVerticalStrut(15));

        // Etiqueta Status
        labelStatusRegistro.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelStatusRegistro.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(labelStatusRegistro);

        // Listeners
        btnRegistrar.addActionListener(e -> actionPerformed_btnRegistrar(e));
        btnBackToLogin.addActionListener(e -> vistaPrincipal.mostrarVista("LOGIN"));
        textRegPassConfirm.addActionListener(e -> actionPerformed_btnRegistrar(e));

        this.add(cardPanel);
    }

    /**
     * Mètode auxiliar per crear etiquetes (JLabel) amb un estil visual consistent.
     * S'utilitza per als títols dels camps del formulari.
     *
     * @param text El text que es mostrarà a l'etiqueta.
     * @return Un objecte {@link JLabel} configurat amb la font, color i alineació
     *         correctes.
     */
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(TEXT_COLOR);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    /**
     * Aplica l'estil visual estàndard als camps de text (entrades d'usuari).
     * Defineix la mida, la font i les vores dels camps per mantenir la coherència
     * visual de l'aplicació.
     *
     * @param field El component {@link JTextField} al qual s'ha d'aplicar l'estil.
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
     * Aplica l'estil visual als botons de la interfície.
     * 
     * Permet diferenciar entre botons d'acció principal (fons sòlid, destacats) i
     * secundaris (contorn, més subtils).
     * També configura efectes de "hover" (passar el ratolí per sobre) per millorar
     * la usabilitat.
     *
     * @param button    El botó {@link JButton} a estilitzar.
     * @param isPrimary Indica el tipus de botó: {@code true} per a accions
     *                  principals (ex: Registrar),
     *                  {@code false} per a secundàries (ex: Tornar).
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
     * Gestiona l'esdeveniment de fer clic al botó "Crear Compte" o prémer Intro al
     * camp de contrasenya.
     * 
     * Aquest mètode realitza les següents accions:
     * 
     * Recull les dades introduïdes pels usuaris.
     * Valida que els camps no estiguin buits.
     * Verifica que la contrasenya i la seva confirmació coincideixin.
     * Si les dades són vàlides localment, crida al mètode
     * {@link CtrlPresentacio#registrarUsuari(String, String)}.
     * Mostra feedback a l'usuari: missatges d'error en vermell o un diàleg
     * d'èxit si el registre funciona.
     *
     * @param event L'objecte {@link ActionEvent} generat per l'acció de l'usuari.
     */
    public void actionPerformed_btnRegistrar(ActionEvent event) {
        String user = textRegUser.getText().trim();
        String pass = new String(textRegPass.getPassword());
        String passConfirm = new String(textRegPassConfirm.getPassword());

        if (user.isEmpty() || pass.isEmpty() || passConfirm.isEmpty()) {
            labelStatusRegistro.setText("⚠ Camps obligatoris");
            labelStatusRegistro.setForeground(ERROR_COLOR);
            return;
        }

        if (!pass.equals(passConfirm)) {
            labelStatusRegistro.setText("⚠ Les contrasenyes no coincideixen");
            labelStatusRegistro.setForeground(ERROR_COLOR);
            return;
        }

        String resultado = iCtrlPresentacio.registrarUsuari(user, pass);
        if (resultado.contains("correctament")) {
            JOptionPane.showMessageDialog(this, "✓ " + resultado, "Registre Completat",
                    JOptionPane.INFORMATION_MESSAGE);
            vistaPrincipal.mostrarVista("LOGIN");
        } else {
            labelStatusRegistro.setText("⚠ " + resultado.replace("Error: ", ""));
            labelStatusRegistro.setForeground(ERROR_COLOR);
        }
    }
}

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
        this.setBackground(UIStyles.BACKGROUND_COLOR);
        this.setLayout(new GridBagLayout());

        // Panel tarjeta
        JPanel cardPanel = UIComponents.createCardPanel();

        // Icono
        cardPanel.add(UIComponents.createIconLabel("🔐"));
        cardPanel.add(Box.createVerticalStrut(10));

        // Título
        cardPanel.add(UIComponents.createTitleLabel("Benvingut!"));

        // Subtítulo
        cardPanel.add(UIComponents.createSubtitleLabel("Inicia sessió per continuar"));
        cardPanel.add(Box.createVerticalStrut(30));

        // Campo Usuario
        cardPanel.add(UIComponents.createLabel("Usuari"));
        cardPanel.add(Box.createVerticalStrut(5));
        UIComponents.styleTextField(textUser);
        cardPanel.add(textUser);
        cardPanel.add(Box.createVerticalStrut(15));

        // Campo Contraseña
        cardPanel.add(UIComponents.createLabel("Contrasenya"));
        cardPanel.add(Box.createVerticalStrut(5));
        UIComponents.styleTextField(textPass);
        cardPanel.add(textPass);
        cardPanel.add(Box.createVerticalStrut(25));

        // Botones
        UIComponents.styleButton(btnLogin, true);
        cardPanel.add(btnLogin);
        cardPanel.add(Box.createVerticalStrut(10));

        UIComponents.styleButton(btnGoToRegister, false);
        cardPanel.add(btnGoToRegister);
        cardPanel.add(Box.createVerticalStrut(15));

        // Status
        labelStatusLogin.setFont(UIStyles.FONT_LABEL);
        labelStatusLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(labelStatusLogin);

        // Listeners
        btnLogin.setActionCommand(MyActionListener.Action.LOGIN.name());
        btnLogin.addActionListener(new MyActionListener(iCtrlPresentacio, vistaPrincipal, this));
        
        btnGoToRegister.setActionCommand(MyActionListener.Action.MOSTRAR_REGISTER.name());
        btnGoToRegister.addActionListener(new MyActionListener(iCtrlPresentacio, vistaPrincipal, this));
        
        textPass.setActionCommand(MyActionListener.Action.LOGIN.name());
        textPass.addActionListener(new MyActionListener(iCtrlPresentacio, vistaPrincipal, this));

        this.add(cardPanel);
    }

    /**
     * Obté el nom d'usuari introduït al camp de login.
     *
     * @return Nom d'usuari (sense espais).
     */
    String getUsername() {
        return textUser.getText().trim();
    }

    /**
     * Obté la contrasenya introduïda al camp de login.
     *
     * @return Contrasenya com a String.
     */
    String getPassword() {
        return new String(textPass.getPassword());
    }
    /**
     * Mostra un missatge d'error a la interfície.
     *
     * @param missatge Missatge d'error a mostrar.
     */
    void mostrarError(String missatge) {
        labelStatusLogin.setText(missatge);
        labelStatusLogin.setForeground(UIStyles.ERROR_COLOR);
    }

    /**
     * Neteja el missatge d'error.
     */
    void netejarError() {
        labelStatusLogin.setText(" ");
    }}

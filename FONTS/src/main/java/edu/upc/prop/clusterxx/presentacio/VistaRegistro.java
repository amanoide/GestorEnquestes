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
        this.setBackground(UIStyles.BACKGROUND_COLOR);
        this.setLayout(new GridBagLayout());

        // Panel tarjeta
        JPanel cardPanel = UIComponents.createCardPanel();

        // Icona
        cardPanel.add(UIComponents.createIconLabel("👤"));
        cardPanel.add(Box.createVerticalStrut(10));

        // Títol
        cardPanel.add(UIComponents.createTitleLabel("Crear Compte"));

        // Subtítol
        cardPanel.add(UIComponents.createSubtitleLabel("Registra't per començar"));
        cardPanel.add(Box.createVerticalStrut(30));

        // Camp Usuari
        cardPanel.add(UIComponents.createLabel("Nom d'usuari (mínim 3 caràcters)"));
        cardPanel.add(Box.createVerticalStrut(5));
        UIComponents.styleTextField(textRegUser);
        cardPanel.add(textRegUser);
        cardPanel.add(Box.createVerticalStrut(15));

        // Camp Contrasenya
        cardPanel.add(UIComponents.createLabel("Contrasenya (mínim 4 caràcters)"));
        cardPanel.add(Box.createVerticalStrut(5));
        UIComponents.styleTextField(textRegPass);
        cardPanel.add(textRegPass);
        cardPanel.add(Box.createVerticalStrut(15));

        // Camp Confirmar Contrasenya
        cardPanel.add(UIComponents.createLabel("Confirmar Contrasenya"));
        cardPanel.add(Box.createVerticalStrut(5));
        UIComponents.styleTextField(textRegPassConfirm);
        cardPanel.add(textRegPassConfirm);
        cardPanel.add(Box.createVerticalStrut(25));

        // Botons
        UIComponents.styleButton(btnRegistrar, true);
        cardPanel.add(btnRegistrar);
        cardPanel.add(Box.createVerticalStrut(10));

        UIComponents.styleButton(btnBackToLogin, false);
        cardPanel.add(btnBackToLogin);
        cardPanel.add(Box.createVerticalStrut(15));

        // Etiqueta Status
        labelStatusRegistro.setFont(UIStyles.FONT_LABEL);
        labelStatusRegistro.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(labelStatusRegistro);

        // Listeners
        btnRegistrar.setActionCommand(MyActionListener.Action.REGISTER.name());
        btnRegistrar.addActionListener(new MyActionListener(iCtrlPresentacio, vistaPrincipal, this));
        
        btnBackToLogin.setActionCommand(MyActionListener.Action.MOSTRAR_LOGIN.name());
        btnBackToLogin.addActionListener(new MyActionListener(iCtrlPresentacio, vistaPrincipal, this));
        
        textRegPassConfirm.setActionCommand(MyActionListener.Action.REGISTER.name());
        textRegPassConfirm.addActionListener(new MyActionListener(iCtrlPresentacio, vistaPrincipal, this));

        this.add(cardPanel);
    }

    /**
     * Obté el nom d'usuari introduït al formulari de registre.
     *
     * @return Nom d'usuari (sense espais).
     */
    String getUsername() {
        return textRegUser.getText().trim();
    }

    /**
     * Obté la contrasenya introduïda al formulari de registre.
     *
     * @return Contrasenya com a String.
     */
    String getPassword() {
        return new String(textRegPass.getPassword());
    }

    /**
     * Obté la confirmació de contrasenya introduïda.
     *
     * @return Confirmació de contrasenya com a String.
     */
    String getPasswordConfirm() {
        return new String(textRegPassConfirm.getPassword());
    }

    /**
     * Mostra un missatge d'error a la interfície.
     *
     * @param missatge Missatge d'error a mostrar.
     */
    void mostrarError(String missatge) {
        labelStatusRegistro.setText(missatge);
        labelStatusRegistro.setForeground(UIStyles.ERROR_COLOR);
    }
}

package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class VistaLogin extends JPanel {
    private CtrlPresentacio iCtrlPresentacio;
    private VistaPrincipal vistaPrincipal;

    private JTextField textUser = new JTextField(15);
    private JPasswordField textPass = new JPasswordField(15);
    private JButton btnLogin = new JButton("Iniciar sessió");
    private JButton btnGoToRegister = new JButton("Registrar-se");
    private JLabel labelStatusLogin = new JLabel("");

    public VistaLogin(CtrlPresentacio ctrlPresentacio, VistaPrincipal vistaPrincipal) {
        this.iCtrlPresentacio = ctrlPresentacio;
        this.vistaPrincipal = vistaPrincipal;
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel title = new JLabel("Login");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 20, 10);
        this.add(title, gbc);

        // Reset insets
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridwidth = 1;

        // Usuario
        gbc.gridy++;
        gbc.gridx = 0;
        this.add(new JLabel("Usuari:"), gbc);
        gbc.gridx = 1;
        this.add(textUser, gbc);

        // Contraseña
        gbc.gridy++;
        gbc.gridx = 0;
        this.add(new JLabel("Contrasenya:"), gbc);
        gbc.gridx = 1;
        this.add(textPass, gbc);

        // Botones
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        // Botón Login (top margin 15, bottom 0)
        gbc.insets = new Insets(15, 5, 0, 5);
        this.add(btnLogin, gbc);

        gbc.gridy++;
        // Botón Registro (top margin 2, bottom 5) - Muy cerca del anterior
        gbc.insets = new Insets(2, 5, 5, 5);
        this.add(btnGoToRegister, gbc);

        // Status
        gbc.gridy++;
        labelStatusLogin.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(labelStatusLogin, gbc);

        // Listeners
        btnLogin.addActionListener(e -> actionPerformed_btnLogin(e));
        btnGoToRegister.addActionListener(e -> vistaPrincipal.mostrarVista("REGISTER"));
    }

    /**
     * Gestiona l'acció del botó de Login.
     * Intenta autenticar l'usuari amb les credencials introduïdes.
     * Si és correcte, redirigeix a la pantalla principal.
     * 
     * @param event L'event d'acció disparat pel botó.
     */
    public void actionPerformed_btnLogin(ActionEvent event) {
        String user = textUser.getText();
        String pass = new String(textPass.getPassword());

        boolean loginOk = iCtrlPresentacio.login(user, pass);

        if (loginOk) {
            vistaPrincipal.mostrarVista("MENU"); // Ir al Menú Principal
        } else {
            labelStatusLogin.setText("Error: Credencials incorrectes");
            labelStatusLogin.setForeground(Color.RED);
        }
    }
}

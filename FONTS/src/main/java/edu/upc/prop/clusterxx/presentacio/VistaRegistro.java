package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class VistaRegistro extends JPanel {
    private CtrlPresentacio iCtrlPresentacio;
    private VistaPrincipal vistaPrincipal;

    private JTextField textRegUser = new JTextField(15);
    private JPasswordField textRegPass = new JPasswordField(15);
    private JButton btnRegistrar = new JButton("Confirmar Registre");
    private JButton btnBackToLogin = new JButton("Tornar al Login");
    private JLabel labelStatusRegistro = new JLabel("");

    public VistaRegistro(CtrlPresentacio ctrlPresentacio, VistaPrincipal vistaPrincipal) {
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
        JLabel title = new JLabel("Registre");
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
        this.add(new JLabel("Nou Usuari:"), gbc);
        gbc.gridx = 1;
        this.add(textRegUser, gbc);

        // Contraseña
        gbc.gridy++;
        gbc.gridx = 0;
        this.add(new JLabel("Nova Contrasenya:"), gbc);
        gbc.gridx = 1;
        this.add(textRegPass, gbc);

        // Botones
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        // Botón Registrar (top margin 15, bottom 0)
        gbc.insets = new Insets(15, 5, 0, 5);
        this.add(btnRegistrar, gbc);

        gbc.gridy++;
        // Botón Volver (top margin 2, bottom 5)
        gbc.insets = new Insets(2, 5, 5, 5);
        this.add(btnBackToLogin, gbc);

        // Status
        gbc.gridy++;
        labelStatusRegistro.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(labelStatusRegistro, gbc);

        // Listeners
        btnRegistrar.addActionListener(e -> actionPerformed_btnRegistrar(e));
        btnBackToLogin.addActionListener(e -> vistaPrincipal.mostrarVista("LOGIN"));
    }

    /**
     * Gestiona l'acció del botó de Registre.
     * Crea un nou usuari amb les dades introduïdes.
     * 
     * @param event L'event d'acció disparat pel botó.
     */
    public void actionPerformed_btnRegistrar(ActionEvent event) {
        String user = textRegUser.getText();
        String pass = new String(textRegPass.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            labelStatusRegistro.setText("Error: Camps buits");
            labelStatusRegistro.setForeground(Color.RED);
            return;
        }

        String resultado = iCtrlPresentacio.registrarUsuari(user, pass);
        if (resultado.contains("correctament")) {
            JOptionPane.showMessageDialog(this, resultado);
            vistaPrincipal.mostrarVista("LOGIN");
        } else {
            labelStatusRegistro.setText(resultado);
            labelStatusRegistro.setForeground(Color.RED);
        }
    }
}

package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VistaPrincipal {
    private CtrlPresentacio iCtrlPresentacio;
    private JFrame frameVista = new JFrame("Gestor d'Enquestes");

    // Panel principal con CardLayout para cambiar entre pantallas
    private JPanel panelContenidos = new JPanel(new CardLayout());
    private CardLayout cardLayout;

    private JMenuBar menubarVista = new JMenuBar();
    private JMenu menuFile = new JMenu("File");
    private JMenuItem menuitemQuit = new JMenuItem("Quit");

    // --- Componentes Pantalla Login ---
    private JPanel panelLogin = new JPanel(new GridLayout(3, 2, 10, 10));
    private JTextField textUser = new JTextField();
    private JPasswordField textPass = new JPasswordField();
    private JButton btnLogin = new JButton("Login");
    private JLabel labelStatusLogin = new JLabel("");

    // --- Componentes Pantalla Crear Enquesta ---
    private JPanel panelCrearEnquesta = new JPanel(new BorderLayout());
    private JPanel formEnquesta = new JPanel(new GridLayout(4, 2, 10, 10));
    private JTextField textIdEnquesta = new JTextField();
    private JTextField textTitolEnquesta = new JTextField();
    private JTextArea textDescEnquesta = new JTextArea(5, 20);
    private JButton btnCrear = new JButton("Crear Enquesta");
    private JLabel labelStatusEnquesta = new JLabel("Usuario autenticado");

    public VistaPrincipal(CtrlPresentacio pCtrlPresentacio) {
        iCtrlPresentacio = pCtrlPresentacio;
        inicializarComponentes();
    }

    public void hacerVisible() {
        frameVista.pack();
        frameVista.setSize(500, 400); // Tamaño razonable inicial
        frameVista.setLocationRelativeTo(null); // Centrar en pantalla
        frameVista.setVisible(true);
    }

    private void inicializarComponentes() {
        // Configuración del Frame
        frameVista.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Configuración del CardLayout
        cardLayout = (CardLayout) panelContenidos.getLayout();

        // --- 1. Configurar Panel Login ---
        panelLogin.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        panelLogin.add(new JLabel("Usuario:"));
        panelLogin.add(textUser);
        panelLogin.add(new JLabel("Contraseña:"));
        panelLogin.add(textPass);
        panelLogin.add(labelStatusLogin);
        panelLogin.add(btnLogin);

        // --- 2. Configurar Panel Crear Enquesta ---
        formEnquesta.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formEnquesta.add(new JLabel("ID Enquesta:"));
        formEnquesta.add(textIdEnquesta);
        formEnquesta.add(new JLabel("Títol:"));
        formEnquesta.add(textTitolEnquesta);
        formEnquesta.add(new JLabel("Descripció:"));
        formEnquesta.add(new JScrollPane(textDescEnquesta));
        formEnquesta.add(new JLabel("")); // Espacio vacío
        formEnquesta.add(btnCrear);

        panelCrearEnquesta.add(labelStatusEnquesta, BorderLayout.NORTH);
        panelCrearEnquesta.add(formEnquesta, BorderLayout.CENTER);

        // Añadir paneles al CardLayout
        panelContenidos.add(panelLogin, "LOGIN");
        panelContenidos.add(panelCrearEnquesta, "MAIN");

        frameVista.setContentPane(panelContenidos);

        // Configuración del Menú
        menuFile.add(menuitemQuit);
        menubarVista.add(menuFile);
        frameVista.setJMenuBar(menubarVista);

        // --- Listeners ---
        menuitemQuit.addActionListener(e -> System.exit(0));

        // Listener Login
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionPerformed_btnLogin(e);
            }
        });

        // Listener Crear Enquesta
        btnCrear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionPerformed_btnCrear(e);
            }
        });
    }

    // --- Métodos de Acción ---

    public void actionPerformed_btnLogin(ActionEvent event) {
        String user = textUser.getText();
        String pass = new String(textPass.getPassword());

        boolean loginOk = iCtrlPresentacio.login(user, pass);

        if (loginOk) {
            labelStatusEnquesta.setText("Bienvenido, " + user);
            cardLayout.show(panelContenidos, "MAIN");
        } else {
            labelStatusLogin.setText("Error: Credenciales incorrectas");
            labelStatusLogin.setForeground(Color.RED);
        }
    }

    public void actionPerformed_btnCrear(ActionEvent event) {
        String id = textIdEnquesta.getText();
        String titol = textTitolEnquesta.getText();
        String desc = textDescEnquesta.getText();

        String resultado = iCtrlPresentacio.crearEnquesta(id, titol, desc);

        JOptionPane.showMessageDialog(frameVista, resultado);
    }
}

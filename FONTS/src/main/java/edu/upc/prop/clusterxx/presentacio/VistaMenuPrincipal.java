package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import java.awt.*;

public class VistaMenuPrincipal extends JPanel {
    private CtrlPresentacio iCtrlPresentacio;
    private VistaPrincipal vistaPrincipal;

    private JButton btnNueva = new JButton("Nova enquesta");
    private JButton btnImportar = new JButton("Importar enquesta");
    private JButton btnGestionar = new JButton("Gestionar les meves enquestes");
    private JButton btnRespondre = new JButton("Respondre Enquesta");
    private JButton btnGestionarRespostes = new JButton("Gestionar Les Meves Respostes"); // Nuevo botón
    private JLabel labelWelcome = new JLabel("Benvingut al Gestor d'Enquestes");

    public VistaMenuPrincipal(CtrlPresentacio ctrlPresentacio, VistaPrincipal vistaPrincipal) {
        this.iCtrlPresentacio = ctrlPresentacio;
        this.vistaPrincipal = vistaPrincipal;
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        labelWelcome.setFont(new Font("Arial", Font.BOLD, 24));
        labelWelcome.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        this.add(labelWelcome, gbc);

        // Botones
        gbc.gridy++;
        btnNueva.setPreferredSize(new Dimension(250, 50));
        this.add(btnNueva, gbc);

        gbc.gridy++;
        btnImportar.setPreferredSize(new Dimension(250, 50));
        this.add(btnImportar, gbc);

        gbc.gridy++;
        btnGestionar.setPreferredSize(new Dimension(250, 50));
        this.add(btnGestionar, gbc);

        gbc.gridy++;
        btnRespondre.setPreferredSize(new Dimension(250, 50)); // Añadir botón
        this.add(btnRespondre, gbc);

        gbc.gridy++;
        btnGestionarRespostes.setPreferredSize(new Dimension(250, 50)); // Añadir botón
        this.add(btnGestionarRespostes, gbc);

        // Listeners
        btnNueva.addActionListener(e -> mostrarDialogoCrear());
        btnImportar.addActionListener(e -> importarEnquesta());
        btnGestionar.addActionListener(e -> vistaPrincipal.mostrarVista("GESTION"));
        btnRespondre.addActionListener(e -> mostrarDialogoResponder());
        btnGestionarRespostes.addActionListener(e -> mostrarDialogoGestionarRespostes()); // Listener // Listener
    }

    private void mostrarDialogoCrear() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        DialogoEnquesta dialogo = new DialogoEnquesta(parentFrame, "Nova enquesta", true);
        dialogo.setVisible(true);

        if (dialogo.isConfirmado()) {
            String resultado = iCtrlPresentacio.crearEnquesta(dialogo.getId(), dialogo.getTitol(), dialogo.getDesc());
            JOptionPane.showMessageDialog(this, resultado);
        }
    }

    private void importarEnquesta() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            String resultado = iCtrlPresentacio.importarEnquesta(selectedFile.getAbsolutePath());
            JOptionPane.showMessageDialog(this, resultado);
        }
    }

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

    private void mostrarDialogoGestionarRespostes() {
        vistaPrincipal.mostrarVista("GESTION_RESPOSTES");
    }
}

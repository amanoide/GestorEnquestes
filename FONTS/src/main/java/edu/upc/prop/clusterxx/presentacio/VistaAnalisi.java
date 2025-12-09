package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;
import edu.upc.prop.clusterxx.domini.classes.Enquesta;

public class VistaAnalisi extends JPanel {

    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);

    private CtrlPresentacio iCtrlPresentacio;
    private VistaPrincipal vistaPrincipal;

    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> listEnquestes = new JList<>(listModel);
    private JButton btnAnalitzar, btnVeurePerfil, btnTornar;

    public VistaAnalisi(CtrlPresentacio ctrlPresentacio, VistaPrincipal vistaPrincipal) {
        this.iCtrlPresentacio = ctrlPresentacio;
        this.vistaPrincipal = vistaPrincipal;
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Título
        JLabel lblTitol = new JLabel("📊 Anàlisi de Clustering");
        lblTitol.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        lblTitol.setForeground(TEXT_COLOR);
        add(lblTitol, BorderLayout.NORTH);

        // Lista
        listEnquestes.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        listEnquestes.setFixedCellHeight(40);
        listEnquestes.setSelectionBackground(new Color(52, 152, 219, 80));
        JScrollPane scroll = new JScrollPane(listEnquestes);
        scroll.setBorder(BorderFactory.createTitledBorder("Les meves enquestes"));
        add(scroll, BorderLayout.CENTER);

        // Botones
        JPanel panelBotons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotons.setBackground(BACKGROUND_COLOR);

        btnAnalitzar = crearBoton("📈 Analitzar Enquesta", PRIMARY_COLOR);
        btnVeurePerfil = crearBoton("👤 Veure el Meu Perfil", new Color(39, 174, 96));
        btnTornar = crearBoton("← Tornar", new Color(149, 165, 166));

        panelBotons.add(btnAnalitzar);
        panelBotons.add(btnVeurePerfil);
        panelBotons.add(btnTornar);
        add(panelBotons, BorderLayout.SOUTH);

        // Estado inicial
        btnAnalitzar.setEnabled(false);

        // Listeners
        listEnquestes.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnAnalitzar.setEnabled(!listEnquestes.isSelectionEmpty());
            }
        });

        btnAnalitzar.addActionListener(e -> analitzarEnquesta());
        btnVeurePerfil.addActionListener(e -> veureMeuPerfil());
        btnTornar.addActionListener(e -> vistaPrincipal.mostrarVista("MENU"));
    }

    private JButton crearBoton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void actualizarLista() {
        listModel.clear();
        ArrayList<Enquesta> enquestes = iCtrlPresentacio.getEnquestesUsuari();
        if (enquestes.isEmpty()) {
            listModel.addElement("No tens enquestes creades.");
            listEnquestes.setEnabled(false);
        } else {
            listEnquestes.setEnabled(true);
            for (Enquesta e : enquestes) {
                listModel.addElement(e.getId() + ": " + e.getTitol());
            }
        }
    }

    private void analitzarEnquesta() {
        String selected = listEnquestes.getSelectedValue();
        if (selected == null || selected.startsWith("No tens"))
            return;

        String idEnquesta = selected.split(":")[0].trim();
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        new DialogoAnalisi(parentFrame, iCtrlPresentacio, idEnquesta).setVisible(true);
    }

    private void veureMeuPerfil() {
        String perfil = iCtrlPresentacio.consultarMeuPerfil();
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        new DialogoPerfil(parentFrame, perfil).setVisible(true);
    }
}

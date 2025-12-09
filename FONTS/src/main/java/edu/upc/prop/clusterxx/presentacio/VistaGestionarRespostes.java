package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;
import edu.upc.prop.clusterxx.domini.classes.Enquesta;

public class VistaGestionarRespostes extends JPanel {

    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);

    private CtrlPresentacio iCtrlPresentacio;
    private VistaPrincipal vistaPrincipal;

    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> listEnquestes = new JList<>(listModel);
    private JButton btnModificar, btnEsborrar, btnTornar;

    public VistaGestionarRespostes(CtrlPresentacio ctrlPresentacio, VistaPrincipal vistaPrincipal) {
        this.iCtrlPresentacio = ctrlPresentacio;
        this.vistaPrincipal = vistaPrincipal;
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Título
        JLabel lblTitol = new JLabel("📋 Les Meves Enquestes Contestades");
        lblTitol.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        lblTitol.setForeground(TEXT_COLOR);
        add(lblTitol, BorderLayout.NORTH);

        // Lista
        listEnquestes.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        listEnquestes.setFixedCellHeight(40);
        listEnquestes.setSelectionBackground(new Color(52, 152, 219, 80));
        JScrollPane scroll = new JScrollPane(listEnquestes);
        scroll.setBorder(BorderFactory.createTitledBorder("Selecciona una enquesta"));
        add(scroll, BorderLayout.CENTER);

        // Botones
        JPanel panelBotons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotons.setBackground(BACKGROUND_COLOR);

        btnModificar = crearBoton("👁️ Veure/Modificar", PRIMARY_COLOR);
        btnEsborrar = crearBoton("🗑️ Esborrar Totes", new Color(231, 76, 60));
        btnTornar = crearBoton("← Tornar", new Color(149, 165, 166));

        panelBotons.add(btnModificar);
        panelBotons.add(btnEsborrar);
        panelBotons.add(btnTornar);
        add(panelBotons, BorderLayout.SOUTH);

        // Estado inicial
        btnModificar.setEnabled(false);
        btnEsborrar.setEnabled(false);

        // Listeners
        listEnquestes.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean selected = !listEnquestes.isSelectionEmpty();
                btnModificar.setEnabled(selected);
                btnEsborrar.setEnabled(selected);
            }
        });

        btnModificar.addActionListener(e -> obrirDialegModificar());
        btnEsborrar.addActionListener(e -> esborrarRespostes());
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
        ArrayList<Enquesta> contestades = iCtrlPresentacio.getEnquestesContestades();
        if (contestades.isEmpty()) {
            listModel.addElement("No has contestat cap enquesta encara.");
            listEnquestes.setEnabled(false);
        } else {
            listEnquestes.setEnabled(true);
            for (Enquesta e : contestades) {
                listModel.addElement(e.getId() + ": " + e.getTitol());
            }
        }
    }

    private void obrirDialegModificar() {
        String selected = listEnquestes.getSelectedValue();
        if (selected == null || selected.startsWith("No has contestat"))
            return;

        String idEnquesta = selected.split(":")[0].trim();
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        new DialogoGestionarRespostes(parentFrame, iCtrlPresentacio, idEnquesta).setVisible(true);
        actualizarLista();
    }

    private void esborrarRespostes() {
        String selected = listEnquestes.getSelectedValue();
        if (selected == null || selected.startsWith("No has contestat"))
            return;

        String idEnquesta = selected.split(":")[0].trim();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Esborrar TOTES les respostes a l'enquesta " + idEnquesta + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String resultat = iCtrlPresentacio.esborrarRespostesEnquesta(idEnquesta);
            JOptionPane.showMessageDialog(this, resultat);
            if (resultat.contains("correctament"))
                actualizarLista();
        }
    }
}

package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import edu.upc.prop.clusterxx.domini.classes.Enquesta;

public class VistaGestionarRespostes extends JPanel {
    private CtrlPresentacio iCtrlPresentacio;
    private VistaPrincipal vistaPrincipal;

    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> listEnquestes = new JList<>(listModel);

    private JButton btnModificar = new JButton("Veure/Modificar Respostes");
    private JButton btnEsborrar = new JButton("Esborrar Totes les Respostes");
    private JButton btnTornar = new JButton("Tornar al Menú");

    public VistaGestionarRespostes(CtrlPresentacio ctrlPresentacio, VistaPrincipal vistaPrincipal) {
        this.iCtrlPresentacio = ctrlPresentacio;
        this.vistaPrincipal = vistaPrincipal;
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Títol
        JLabel lblTitol = new JLabel("Les Meves Enquestes Contestades");
        lblTitol.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitol.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitol, BorderLayout.NORTH);

        // Llista
        JScrollPane scroll = new JScrollPane(listEnquestes);
        scroll.setBorder(BorderFactory.createTitledBorder("Selecciona una enquesta per gestionar"));
        add(scroll, BorderLayout.CENTER);

        // Botons
        JPanel panelBotons = new JPanel();
        panelBotons.add(btnModificar);
        panelBotons.add(btnEsborrar);
        panelBotons.add(btnTornar);
        add(panelBotons, BorderLayout.SOUTH);

        // Estat inicial
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

        String idEnquesta = selected.split(":")[0];
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);

        // Reutilitzem el diàleg existent que ja funciona bé
        DialogoGestionarRespostes dialogo = new DialogoGestionarRespostes(parentFrame, iCtrlPresentacio, idEnquesta);
        dialogo.setVisible(true);

        // Al tornar, refresquem per si s'ha esborrat tot des del diàleg
        actualizarLista();
    }

    private void esborrarRespostes() {
        String selected = listEnquestes.getSelectedValue();
        if (selected == null || selected.startsWith("No has contestat"))
            return;

        String idEnquesta = selected.split(":")[0];

        int confirm = JOptionPane.showConfirmDialog(this,
                "Estàs segur que vols esborrar TOTES les teves respostes a l'enquesta " + idEnquesta + "?",
                "Confirmar esborrat",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String resultat = iCtrlPresentacio.esborrarRespostesEnquesta(idEnquesta);
            JOptionPane.showMessageDialog(this, resultat);
            if (resultat.contains("correctament")) {
                actualizarLista();
            }
        }
    }
}

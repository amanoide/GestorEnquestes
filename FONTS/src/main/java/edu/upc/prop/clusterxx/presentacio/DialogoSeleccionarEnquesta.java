package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import edu.upc.prop.clusterxx.domini.classes.Enquesta;

public class DialogoSeleccionarEnquesta extends JDialog {
    private CtrlPresentacio iCtrlPresentacio;
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> listEnquestes = new JList<>(listModel);
    private boolean confirmado = false;
    private String selectedId = null;

    public DialogoSeleccionarEnquesta(Frame owner, CtrlPresentacio ctrlPresentacio) {
        super(owner, "Seleccionar Enquesta", true);
        this.iCtrlPresentacio = ctrlPresentacio;
        inicializar();
        cargarEnquestes();
    }

    private void inicializar() {
        setLayout(new BorderLayout());
        setSize(400, 300);
        setLocationRelativeTo(getOwner());

        add(new JLabel("Selecciona una enquesta per respondre:"), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(listEnquestes);
        add(scroll, BorderLayout.CENTER);

        JPanel panelBotons = new JPanel();
        JButton btnOk = new JButton("Seleccionar");
        JButton btnCancel = new JButton("Cancel·lar");

        btnOk.addActionListener(e -> {
            String selected = listEnquestes.getSelectedValue();
            if (selected != null) {
                selectedId = selected.split(":")[0];
                confirmado = true;
                setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "Has de seleccionar una enquesta.");
            }
        });

        btnCancel.addActionListener(e -> setVisible(false));

        panelBotons.add(btnOk);
        panelBotons.add(btnCancel);
        add(panelBotons, BorderLayout.SOUTH);
    }

    private void cargarEnquestes() {
        listModel.clear();
        ArrayList<Enquesta> enquestes = iCtrlPresentacio.getAllEnquestes();
        for (Enquesta e : enquestes) {
            listModel.addElement(e.getId() + ": " + e.getTitol());
        }
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public String getSelectedId() {
        return selectedId;
    }
}

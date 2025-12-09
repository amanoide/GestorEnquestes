package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class VistaGestionEnquestes extends JPanel {
    
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    
    private CtrlPresentacio iCtrlPresentacio;
    private VistaPrincipal vistaPrincipal;

    private DefaultListModel<String> listModelEnquestes = new DefaultListModel<>();
    private JList<String> listEnquestes = new JList<>(listModelEnquestes);
    private JButton btnEditar = new JButton("✏️ Modificar");
    private JButton btnGestionarPreguntes = new JButton("📝 Gestionar Preguntes");
    private JButton btnVeureParticipants = new JButton("👥 Veure Participants");
    private JButton btnEliminar = new JButton("🗑️ Eliminar");
    private JButton btnVolver = new JButton("← Tornar");

    public VistaGestionEnquestes(CtrlPresentacio ctrlPresentacio, VistaPrincipal vistaPrincipal) {
        this.iCtrlPresentacio = ctrlPresentacio;
        this.vistaPrincipal = vistaPrincipal;
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Título
        JLabel titulo = new JLabel("📋 Gestió d'Enquestes");
        titulo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        titulo.setForeground(TEXT_COLOR);
        add(titulo, BorderLayout.NORTH);

        // Lista
        listEnquestes.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        listEnquestes.setFixedCellHeight(35);
        listEnquestes.setSelectionBackground(new Color(52, 152, 219, 80));
        JScrollPane scrollList = new JScrollPane(listEnquestes);
        scrollList.setBorder(BorderFactory.createTitledBorder("Les meves enquestes"));
        add(scrollList, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.setBackground(BACKGROUND_COLOR);
        
        styleButton(btnEditar, PRIMARY_COLOR);
        styleButton(btnGestionarPreguntes, new Color(39, 174, 96));
        styleButton(btnVeureParticipants, new Color(243, 156, 18));
        styleButton(btnEliminar, new Color(231, 76, 60));
        styleButton(btnVolver, new Color(149, 165, 166));
        
        panelBotones.add(btnEditar);
        panelBotones.add(btnGestionarPreguntes);
        panelBotones.add(btnVeureParticipants);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnVolver);
        add(panelBotones, BorderLayout.SOUTH);

        // Listeners
        btnEditar.addActionListener(e -> mostrarDialogoEditar());
        btnGestionarPreguntes.addActionListener(e -> mostrarDialogoPreguntes());
        btnVeureParticipants.addActionListener(e -> mostrarParticipants());
        btnEliminar.addActionListener(e -> eliminarEnquesta());
        btnVolver.addActionListener(e -> vistaPrincipal.mostrarVista("MENU"));

        listEnquestes.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean selected = !listEnquestes.isSelectionEmpty();
                btnEditar.setEnabled(selected);
                btnGestionarPreguntes.setEnabled(selected);
                btnVeureParticipants.setEnabled(selected);
                btnEliminar.setEnabled(selected);
            }
        });

        btnEditar.setEnabled(false);
        btnGestionarPreguntes.setEnabled(false);
        btnVeureParticipants.setEnabled(false);
        btnEliminar.setEnabled(false);
    }

    private void styleButton(JButton btn, Color color) {
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void actualizarLista() {
        refreshEnquestesList();
    }

    private void mostrarDialogoEditar() {
        String selected = listEnquestes.getSelectedValue();
        if (selected == null) return;

        String id = selected.split(":")[0].trim();
        String currentTitle = "", currentDesc = "";

        for (edu.upc.prop.clusterxx.domini.classes.Enquesta e : iCtrlPresentacio.getEnquestesUsuari()) {
            if (e.getId().equals(id)) {
                currentTitle = e.getTitol();
                currentDesc = e.getDescripcio();
                break;
            }
        }

        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        DialogoEnquesta dialogo = new DialogoEnquesta(parentFrame, "Modificar Enquesta", false);
        dialogo.setDatos(id, currentTitle, currentDesc);
        dialogo.setVisible(true);

        if (dialogo.isConfirmado()) {
            String resultado = iCtrlPresentacio.modificarEnquesta(id, dialogo.getTitol(), dialogo.getDesc());
            JOptionPane.showMessageDialog(this, resultado);
            if (resultado.contains("correctament")) refreshEnquestesList();
        }
    }

    private void mostrarDialogoPreguntes() {
        String selected = listEnquestes.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una enquesta.");
            return;
        }

        String id = selected.split(":")[0].trim();
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        new DialogoGestionPreguntes(parentFrame, iCtrlPresentacio, id).setVisible(true);
    }

    private void mostrarParticipants() {
        String selected = listEnquestes.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una enquesta.");
            return;
        }

        String id = selected.split(":")[0].trim();
        
        // Buscar la enquesta
        edu.upc.prop.clusterxx.domini.classes.Enquesta enquesta = null;
        for (edu.upc.prop.clusterxx.domini.classes.Enquesta e : iCtrlPresentacio.getEnquestesUsuari()) {
            if (e.getId().equals(id)) {
                enquesta = e;
                break;
            }
        }
        
        if (enquesta == null) {
            JOptionPane.showMessageDialog(this, "No s'ha trobat l'enquesta.");
            return;
        }
        
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        new DialogoParticipants(parentFrame, enquesta.getTitol(), enquesta.getParticipants()).setVisible(true);
    }

    private void eliminarEnquesta() {
        String selected = listEnquestes.getSelectedValue();
        if (selected == null) return;

        String id = selected.split(":")[0].trim();
        int confirm = JOptionPane.showConfirmDialog(this, "Eliminar l'enquesta " + id + "?", 
            "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String resultado = iCtrlPresentacio.esborrarEnquesta(id);
            JOptionPane.showMessageDialog(this, resultado);
            if (resultado.contains("correctament")) refreshEnquestesList();
        }
    }

    private void refreshEnquestesList() {
        listModelEnquestes.clear();
        for (edu.upc.prop.clusterxx.domini.classes.Enquesta e : iCtrlPresentacio.getEnquestesUsuari()) {
            listModelEnquestes.addElement(e.getId() + ": " + e.getTitol());
        }
    }
}

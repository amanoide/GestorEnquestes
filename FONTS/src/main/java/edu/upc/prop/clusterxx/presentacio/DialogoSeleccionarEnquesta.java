package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * Diàleg modal per a la selecció d'una enquesta dins d'una llista.
 * 
 * Aquesta classe presenta a l'usuari totes les enquestes disponibles al sistema
 * perquè en pugui triar una (per exemple, per respondre-la).
 * 
 * Funcionalitats:
 * Visualització en llista amb títols i IDs de les enquestes.
 * Selecció simple mitjançant clic o doble clic.
 * Validació que l'usuari hagi triat una opció abans de confirmar.
 */
public class DialogoSeleccionarEnquesta extends JDialog {
    private CtrlPresentacio iCtrlPresentacio;
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> listEnquestes = new JList<>(listModel);
    private boolean confirmado = false;
    private String selectedId = null;

    /**
     * Constructor del diàleg de selecció d'enquesta.
     * 
     * Configura el diàleg i carrega automàticament les dades de les enquestes
     * utilitzant el controlador proporcionat.
     *
     * @param owner           La finestra pare sobre la qual es mostrarà el diàleg.
     * @param ctrlPresentacio El controlador per accedir a la llista d'enquestes del
     *                        domini.
     */
    public DialogoSeleccionarEnquesta(Frame owner, CtrlPresentacio ctrlPresentacio) {
        super(owner, "Seleccionar Enquesta", true);
        this.iCtrlPresentacio = ctrlPresentacio;
        inicializar();
        cargarEnquestes();
    }

    /**
     * Inicialitza la interfície gràfica.
     * 
     * Configura el panell principal amb layout vertical, afegeix icones i títols
     * explicatius, configura la jList per mostrar les enquestes amb un estil net,
     * i prepara els botons de "Seleccionar" i "Cancel·lar" amb els seus respectius
     * listeners.
     */
    private void inicializar() {
        setSize(450, 400);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        getContentPane().setBackground(UIStyles.BACKGROUND_COLOR);

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(UIStyles.CARD_COLOR);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Icono
        JLabel iconLabel = new JLabel("📋");
        iconLabel.setFont(UIStyles.FONT_ICON_SMALL);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(iconLabel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Título
        JLabel titleLabel = new JLabel("Seleccionar Enquesta");
        titleLabel.setFont(UIStyles.FONT_DIALOG_TITLE);
        titleLabel.setForeground(UIStyles.TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        // Subtítulo
        JLabel subtitle = new JLabel("Tria una enquesta de la llista");
        subtitle.setFont(UIStyles.FONT_DIALOG_SUBTITLE);
        subtitle.setForeground(UIStyles.SECONDARY_COLOR);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(subtitle);
        mainPanel.add(Box.createVerticalStrut(20));

        // Lista estilizada
        listEnquestes.setFont(UIStyles.FONT_NORMAL);
        listEnquestes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listEnquestes.setSelectionBackground(UIStyles.PRIMARY_COLOR);
        listEnquestes.setSelectionForeground(Color.WHITE);
        listEnquestes.setFixedCellHeight(35);
        listEnquestes.setBorder(new EmptyBorder(5, 10, 5, 10));

        JScrollPane scroll = new JScrollPane(listEnquestes);
        scroll.setMaximumSize(new Dimension(350, 150));
        scroll.setPreferredSize(new Dimension(350, 150));
        scroll.setBorder(new LineBorder(UIStyles.BORDER_COLOR, 1, true));
        scroll.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(scroll);
        mainPanel.add(Box.createVerticalStrut(25));

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(UIStyles.CARD_COLOR);
        buttonPanel.setMaximumSize(new Dimension(350, 50));

        JButton btnCancel = UIComponents.createColorButton("Cancel·lar", UIStyles.SECONDARY_COLOR);
        buttonPanel.add(btnCancel);

        JButton btnOk = UIComponents.createColorButton("Seleccionar", UIStyles.PRIMARY_COLOR);
        buttonPanel.add(btnOk);

        mainPanel.add(buttonPanel);

        setContentPane(mainPanel);

        // Listeners
        btnOk.addActionListener(e -> {
            String selected = listEnquestes.getSelectedValue();
            if (selected != null) {
                selectedId = selected.split(":")[0].trim();
                confirmado = true;
                setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "⚠ Has de seleccionar una enquesta.", "Selecció requerida",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> setVisible(false));

        // Doble clic para seleccionar
        listEnquestes.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    btnOk.doClick();
                }
            }
        });
    }

    /**
     * Obté la llista d'enquestes des del domini i omple el model de la llista
     * visual.
     * Mostra un missatge especial si no hi ha enquestes disponibles.
     */
    private void cargarEnquestes() {
        listModel.clear();
        ArrayList<ArrayList<String>> enquestes = iCtrlPresentacio.getAllEnquestes();
        if (enquestes.isEmpty()) {
            listModel.addElement("(No hi ha enquestes disponibles)");
            listEnquestes.setEnabled(false);
        } else {
            for (ArrayList<String> e : enquestes) {
                // e.get(0) = ID, e.get(1) = Títol
                listModel.addElement(e.get(0) + ": " + e.get(1));
            }
        }
    }

    /**
     * Indica si l'usuari ha finalitzat el diàleg seleccionant una opció vàlida.
     *
     * @return true si s'ha premut "Seleccionar" amb una opció triada, false si s'ha
     *         cancel·lat.
     */
    public boolean isConfirmado() {
        return confirmado;
    }

    /**
     * Recupera l'ID de l'enquesta que l'usuari ha seleccionat.
     *
     * @return L'identificador (String) de l'enquesta o null si no hi ha selecció.
     */
    public String getSelectedId() {
        return selectedId;
    }
}

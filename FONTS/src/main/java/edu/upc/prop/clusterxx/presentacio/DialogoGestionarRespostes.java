package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import edu.upc.prop.clusterxx.domini.classes.Pregunta;

public class DialogoGestionarRespostes extends JDialog {
    // Colores del tema
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color PRIMARY_HOVER = new Color(52, 152, 219);
    private static final Color SECONDARY_COLOR = new Color(149, 165, 166);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);

    private CtrlPresentacio iCtrlPresentacio;
    private String idEnquesta;
    private ArrayList<Pregunta> preguntes;
    private HashMap<String, String> respostesUsuari;

    private JPanel panelContent;

    public DialogoGestionarRespostes(Frame owner, CtrlPresentacio ctrlPresentacio, String idEnquesta) {
        super(owner, "Gestionar Respostes", true);
        this.iCtrlPresentacio = ctrlPresentacio;
        this.idEnquesta = idEnquesta;
        cargarDatos();
        inicializar();
    }

    private void cargarDatos() {
        this.preguntes = iCtrlPresentacio.getPreguntesEnquestaObjects(idEnquesta);
        this.respostesUsuari = iCtrlPresentacio.getRespostesUsuariEnquesta(idEnquesta);
    }

    private void inicializar() {
        setSize(700, 600);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(CARD_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1, true),
            new EmptyBorder(20, 30, 20, 30)
        ));

        JLabel iconLabel = new JLabel("📋");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(iconLabel);
        headerPanel.add(Box.createVerticalStrut(8));

        JLabel titleLabel = new JLabel("Gestionar Respostes");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Enquesta: " + idEnquesta);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(SECONDARY_COLOR);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(subtitleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content panel
        panelContent = new JPanel();
        panelContent.setLayout(new BoxLayout(panelContent, BoxLayout.Y_AXIS));
        panelContent.setBackground(BACKGROUND_COLOR);
        panelContent.setBorder(new EmptyBorder(15, 0, 15, 0));

        refrescarPanelContent();

        JScrollPane scroll = new JScrollPane(panelContent);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BACKGROUND_COLOR);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(scroll, BorderLayout.CENTER);

        // Footer con botones
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        footerPanel.setBackground(BACKGROUND_COLOR);

        JButton btnEsborrarTot = new JButton("Esborrar Totes");
        styleButton(btnEsborrarTot, DANGER_COLOR);
        btnEsborrarTot.addActionListener(e -> esborrarTotesRespostes());
        footerPanel.add(btnEsborrarTot);

        JButton btnTancar = new JButton("Tancar");
        styleButton(btnTancar, SECONDARY_COLOR);
        btnTancar.addActionListener(e -> setVisible(false));
        footerPanel.add(btnTancar);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void refrescarPanelContent() {
        panelContent.removeAll();

        if (respostesUsuari.isEmpty()) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setBackground(CARD_COLOR);
            emptyPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(189, 195, 199), 1, true),
                new EmptyBorder(40, 40, 40, 40)
            ));
            
            
            JLabel emptyLabel = new JLabel("No has respost aquesta enquesta.");
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptyLabel.setForeground(SECONDARY_COLOR);
            emptyPanel.add(emptyLabel);
            
            panelContent.add(emptyPanel);
        } else {
            for (Pregunta p : preguntes) {
                if (respostesUsuari.containsKey(p.getId())) {
                    panelContent.add(crearPanelResposta(p));
                    panelContent.add(Box.createVerticalStrut(10));
                }
            }
        }

        panelContent.revalidate();
        panelContent.repaint();
    }

    private JPanel crearPanelResposta(Pregunta p) {
        JPanel panelPregunta = new JPanel(new BorderLayout(15, 0));
        panelPregunta.setBackground(CARD_COLOR);
        panelPregunta.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1, true),
            new EmptyBorder(15, 20, 15, 20)
        ));
        panelPregunta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // Panel izquierdo con pregunta y respuesta
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(CARD_COLOR);

        JLabel lblPregunta = new JLabel(p.getText());
        lblPregunta.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPregunta.setForeground(TEXT_COLOR);
        lblPregunta.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(lblPregunta);
        leftPanel.add(Box.createVerticalStrut(5));

        String respostaActual = respostesUsuari.get(p.getId());
        JLabel lblResposta = new JLabel( respostaActual);
        lblResposta.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblResposta.setForeground(SUCCESS_COLOR);
        lblResposta.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(lblResposta);

        JLabel lblTipus = new JLabel("Tipus: " + p.getTipus());
        lblTipus.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblTipus.setForeground(SECONDARY_COLOR);
        lblTipus.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(lblTipus);

        panelPregunta.add(leftPanel, BorderLayout.CENTER);

        // Botón modificar
        JButton btnModificar = new JButton("Modificar");
        styleButton(btnModificar, WARNING_COLOR);
        btnModificar.setPreferredSize(new Dimension(120, 35));
        btnModificar.addActionListener(e -> modificarResposta(p));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        rightPanel.setBackground(CARD_COLOR);
        rightPanel.add(btnModificar);
        panelPregunta.add(rightPanel, BorderLayout.EAST);

        return panelPregunta;
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color hoverColor = color.brighter();
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { button.setBackground(hoverColor); }
            public void mouseExited(java.awt.event.MouseEvent e) { button.setBackground(color); }
        });
    }

    private void modificarResposta(Pregunta p) {
        String novaResposta = JOptionPane.showInputDialog(this,
                "Introdueix la nova resposta per a:\n\n" + p.getText() + "\n\n" +
                        "Format esperat: " + p.getTipus(),
                respostesUsuari.get(p.getId()));

        if (novaResposta != null && !novaResposta.trim().isEmpty()) {
            String resultat = iCtrlPresentacio.modificarResposta(idEnquesta, p.getId(), novaResposta);
            JOptionPane.showMessageDialog(this, resultat);
            if (resultat.contains("correctament")) {
                cargarDatos();
                refrescarPanelContent();
            }
        }
    }

    private void esborrarTotesRespostes() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Estàs segur que vols esborrar TOTES les teves respostes?\n\nAquesta acció no es pot desfer.",
                "Confirmar esborrat",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            String resultat = iCtrlPresentacio.esborrarRespostesEnquesta(idEnquesta);
            JOptionPane.showMessageDialog(this, resultat);
            if (resultat.contains("correctament")) {
                setVisible(false);
            }
        }
    }
}

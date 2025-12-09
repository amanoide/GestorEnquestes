package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class DialogoAnalisi extends JDialog {

    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);

    private CtrlPresentacio iCtrlPresentacio;
    private String idEnquesta;

    private JRadioButton rbManual, rbAleatori, rbAutomatic;
    private JSpinner spinnerK;
    private JRadioButton rbKMeans, rbKMeansPlusPlus, rbKMedoids;

    public DialogoAnalisi(Frame owner, CtrlPresentacio ctrlPresentacio, String idEnquesta) {
        super(owner, "Analitzar Enquesta - " + idEnquesta, true);
        this.iCtrlPresentacio = ctrlPresentacio;
        this.idEnquesta = idEnquesta;
        inicializar();
    }

    private void inicializar() {
        setSize(500, 450);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Título
        JLabel titulo = new JLabel("📊 Configuració d'Anàlisi de Clustering");
        titulo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBorder(new EmptyBorder(15, 0, 15, 0));
        add(titulo, BorderLayout.NORTH);

        // Panel central
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Sección: Selección de K
        formPanel.add(crearLabel("Com vols escollir el nombre de clusters (k)?"));
        formPanel.add(Box.createVerticalStrut(10));

        ButtonGroup kGroup = new ButtonGroup();
        rbManual = new JRadioButton("Manual (tu esculls k)");
        rbAleatori = new JRadioButton("Aleatori (k entre 2 i √n)");
        rbAutomatic = new JRadioButton("Automàtic (millor k segons Silhouette)");

        rbManual.setBackground(Color.WHITE);
        rbAleatori.setBackground(Color.WHITE);
        rbAutomatic.setBackground(Color.WHITE);
        rbManual.setSelected(true);

        kGroup.add(rbManual);
        kGroup.add(rbAleatori);
        kGroup.add(rbAutomatic);

        formPanel.add(rbManual);
        formPanel.add(rbAleatori);
        formPanel.add(rbAutomatic);
        formPanel.add(Box.createVerticalStrut(10));

        // Spinner para K manual
        JPanel spinnerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        spinnerPanel.setBackground(Color.WHITE);
        spinnerPanel.add(new JLabel("Valor de k:"));
        spinnerK = new JSpinner(new SpinnerNumberModel(3, 2, 20, 1));
        spinnerK.setPreferredSize(new Dimension(60, 25));
        spinnerPanel.add(spinnerK);
        formPanel.add(spinnerPanel);
        formPanel.add(Box.createVerticalStrut(20));

        // Sección: Algoritmo
        formPanel.add(crearLabel("Algoritme de clustering:"));
        formPanel.add(Box.createVerticalStrut(10));

        ButtonGroup algGroup = new ButtonGroup();
        rbKMeans = new JRadioButton("KMeans (inicialització aleatòria)");
        rbKMeansPlusPlus = new JRadioButton("KMeans++ (recomanat)");
        rbKMedoids = new JRadioButton("KMedoids (robust a outliers)");

        rbKMeans.setBackground(Color.WHITE);
        rbKMeansPlusPlus.setBackground(Color.WHITE);
        rbKMedoids.setBackground(Color.WHITE);
        rbKMeansPlusPlus.setSelected(true);

        algGroup.add(rbKMeans);
        algGroup.add(rbKMeansPlusPlus);
        algGroup.add(rbKMedoids);

        formPanel.add(rbKMeans);
        formPanel.add(rbKMeansPlusPlus);
        formPanel.add(rbKMedoids);

        add(new JScrollPane(formPanel), BorderLayout.CENTER);

        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnAnalitzar = crearBoton("✓ Analitzar", PRIMARY_COLOR);
        JButton btnCancel = crearBoton("✖ Cancel·lar", new Color(149, 165, 166));

        buttonPanel.add(btnAnalitzar);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);

        // Listeners
        rbManual.addActionListener(e -> spinnerK.setEnabled(true));
        rbAleatori.addActionListener(e -> spinnerK.setEnabled(false));
        rbAutomatic.addActionListener(e -> spinnerK.setEnabled(false));

        btnAnalitzar.addActionListener(e -> executarAnalisi());
        btnCancel.addActionListener(e -> setVisible(false));
    }

    private JLabel crearLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return label;
    }

    private JButton crearBoton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setBorder(new EmptyBorder(8, 20, 8, 20));
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void executarAnalisi() {
        // Obtener configuración
        String modeK = rbManual.isSelected() ? "manual" : (rbAleatori.isSelected() ? "aleatori" : "automatic");
        int k = (Integer) spinnerK.getValue();
        String algoritme = rbKMeans.isSelected() ? "KMeans" : (rbKMeansPlusPlus.isSelected() ? "KMeans++" : "KMedoids");

        // Mostrar diálogo de progreso
        JDialog progressDialog = new JDialog(this, "Analitzant...", true);
        progressDialog.setLayout(new BorderLayout());
        JLabel lblProgress = new JLabel("⏳ Analitzant enquesta amb " + algoritme + "...", SwingConstants.CENTER);
        lblProgress.setBorder(new EmptyBorder(30, 30, 30, 30));
        progressDialog.add(lblProgress);
        progressDialog.setSize(350, 120);
        progressDialog.setLocationRelativeTo(this);

        // Ejecutar análisis en un thread separado
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                return iCtrlPresentacio.analitzarEnquesta(idEnquesta, modeK, k, algoritme);
            }

            @Override
            protected void done() {
                progressDialog.dispose();
                try {
                    String resultat = get();
                    mostrarResultat(resultat);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(DialogoAnalisi.this,
                            "Error durant l'anàlisi: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
        progressDialog.setVisible(true);
    }

    private void mostrarResultat(String resultat) {
        JTextArea textArea = new JTextArea(resultat);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(this, scrollPane,
                "Resultats de l'Anàlisi", JOptionPane.INFORMATION_MESSAGE);

        setVisible(false);
    }
}

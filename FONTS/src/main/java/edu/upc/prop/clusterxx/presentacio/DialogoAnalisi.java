package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Diàleg de configuració per a l'anàlisi de clustering d'enquestes.
 * 
 * Aquest diàleg permet a l'usuari configurar els paràmetres necessaris per
 * executar un algoritme de clustering sobre les respostes d'una enquesta
 * específica. Ofereix diferents modes de selecció del nombre de clusters (k) i
 * permet triar l'algoritme de clustering a utilitzar.
 * 
 * Funcionalitats clau:
 * 
 * Selecció del mode de determinació de k:
 *   - Manual: l'usuari especifica el valor de k directament.
 *   - Aleatori: es genera un k aleatori entre 2 i √n (arrel del nombre de
 *     participants).
 *   - Automàtic: s'escull el millor k utilitzant el coeficient de
 *     Silhouette.
 * Selecció de l'algoritme de clustering:
 *   - KMeans: inicialització aleatòria de centroides.
 *   - KMeans++: inicialització millorada (recomanat).
 *   - KMedoids: robust.
 * 
 * 
 */
public class DialogoAnalisi extends JDialog {

    /** Controlador de presentació per executar l'anàlisi de clustering. */
    private CtrlPresentacio iCtrlPresentacio;
    /** Identificador de l'enquesta a analitzar. */
    private String idEnquesta;

    /** Botó de radio per seleccionar el mode manual de k. */
    private JRadioButton rbManual, rbAleatori, rbAutomatic;
    /** Spinner per introduir el valor manual de k (nombre de clusters). */
    private JSpinner spinnerK;
    /** Botons de radio per seleccionar l'algoritme de clustering. */
    private JRadioButton rbKMeans, rbKMeansPlusPlus, rbKMedoids;

    /**
     * Constructor del diàleg d'anàlisi de clustering.
     * 
     * Inicialitza el diàleg amb els paràmetres necessaris i construeix la
     * interfície d'usuari amb tots els controls de configuració.
     *
     * @param ctrlPresentacio  Controlador de presentació per executar l'anàlisi.
     * @param idEnquesta       Identificador de l'enquesta a analitzar.
     */
    public DialogoAnalisi(Frame owner, CtrlPresentacio ctrlPresentacio, String idEnquesta) {
        super(owner, "Analitzar Enquesta - " + idEnquesta, true);
        this.iCtrlPresentacio = ctrlPresentacio;
        this.idEnquesta = idEnquesta;
        inicializar();
    }

    /**
     * Inicialitza i configura tots els components gràfics del diàleg.
     * 
     * Crea el layout principal amb tres seccions:
     * 
     * Adalt: Títol del diàleg amb icona.
     * Mig: Formulari amb opcions de configuració (mode de k i algoritme).
     * Abaix: Botons d'acció (Analitzar i Cancel·lar).
     * 
     * 
     * També configura els listeners per als botons de radio i els botons d'acció.
     */
    private void inicializar() {
        setSize(500, 450);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(UIStyles.BACKGROUND_COLOR);

        // Título
        JLabel titulo = new JLabel("📊 Configuració d'Anàlisi de Clustering");
        titulo.setFont(UIStyles.FONT_SECTION);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBorder(new EmptyBorder(15, 0, 15, 0));
        add(titulo, BorderLayout.NORTH);

        // Panel central
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Sección: Selección de K
        formPanel.add(UIComponents.createLabel("Com vols escollir el nombre de clusters (k)?"));
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
        formPanel.add(UIComponents.createLabel("Algoritme de clustering:"));
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

        JButton btnAnalitzar = UIComponents.createColorButton("📊 Analitzar", UIStyles.PRIMARY_COLOR);
        JButton btnCancelar = UIComponents.createColorButton("Cancel·lar", UIStyles.SECONDARY_COLOR);

        buttonPanel.add(btnAnalitzar);
        buttonPanel.add(btnCancelar);
        add(buttonPanel, BorderLayout.SOUTH);

        // Listeners
        rbManual.addActionListener(e -> spinnerK.setEnabled(true));
        rbAleatori.addActionListener(e -> spinnerK.setEnabled(false));
        rbAutomatic.addActionListener(e -> spinnerK.setEnabled(false));

        btnAnalitzar.addActionListener(e -> executarAnalisi());
        btnCancelar.addActionListener(e -> setVisible(false));
    }

    /**
     * Executa el procés d'anàlisi de clustering amb la configuració seleccionada.
     * 
     * Aquest mètode:
     * 
     * Recull la configuració de l'usuari (mode de k, valor de k, algoritme).
     * Crea un diàleg.
     * Mostra els resultats o errors en diàlegs apropiats.
     * 
     * 
     * L'execució en segon pla garanteix que la interfície es mantingui
     * responsiva durant el càlcul, que pot trigar diversos segons depenent
     * del nombre de participants i la complexitat de l'algoritme.
     */
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

    /**
     * Mostra els resultats de l'anàlisi de clustering en un diàleg de text.
     * 
     * Crea una àrea de text no editable amb font monoespaciada per mostrar
     * els resultats de forma llegible. Els resultats inclouen informació sobre
     * els clusters generats, el coeficient de Silhouette, i les assignacions
     * de perfils als usuaris.
     *
     * @param resultat Text amb els resultats de l'anàlisi en format llegible.
     */
    private void mostrarResultat(String resultat) {
        JTextArea textArea = new JTextArea(resultat);
        textArea.setEditable(false);
        textArea.setFont(UIStyles.FONT_MONOSPACED);
        textArea.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(this, scrollPane,
                "Resultats de l'Anàlisi", JOptionPane.INFORMATION_MESSAGE);

        setVisible(false);
    }
}

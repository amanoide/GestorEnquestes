package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Diàleg per gestionar (visualitzar, modificar i esborrar) les respostes de
 * l'usuari a una enquesta específica.
 * 
 * Aquest diàleg mostra totes les respostes que l'usuari actual ha donat a una
 * enquesta concreta, permetent modificar respostes individuals o esborrar totes
 * les respostes de cop.
 * 
 * Funcionalitats clau:
 * 
 * Visualització de totes les respostes de l'usuari amb pregunta i tipus.
 * Modificació individual de cada resposta mitjançant un diàleg d'input.
 * Esborrat massiu de totes les respostes amb confirmació.
 * Indicador visual quan no hi ha respostes.
 * Actualització dinàmica del contingut després de cada canvi.
 * Estils visuals diferenciats per a cada acció (modificar=taronja,
 * esborrar=vermell).
 * 
 * 
 * El diàleg és modal i es refresca automàticament després de cada modificació
 * per mostrar els canvis en temps real.
 */
public class DialogoGestionarRespostes extends JDialog {
    /** Controlador de presentació per executar les operacions sobre respostes. */
    private CtrlPresentacio iCtrlPresentacio;
    /** Identificador de l'enquesta de la qual es gestionen les respostes. */
    private String idEnquesta;
    /** Llista de preguntes de l'enquesta. */
    private ArrayList<ArrayList<Object>> preguntes;
    /** Mapa amb les respostes de l'usuari (clau: idPregunta, valor: resposta). */
    private HashMap<String, String> respostesUsuari;

    /** Panel principal que conté les targetes de respostes. */
    private JPanel panelContent;

    /**
     * Constructor del diàleg de gestió de respostes.
     * 
     * Inicialitza el diàleg, carrega les dades de l'enquesta i les respostes de
     * l'usuari, i construeix la interfície.
     *
     * @param owner           Finestra propietària del diàleg (per centrar-lo).
     * @param ctrlPresentacio Controlador de presentació per gestionar les
     *                        operacions.
     * @param idEnquesta      Identificador de l'enquesta a gestionar.
     */
    public DialogoGestionarRespostes(Frame owner, CtrlPresentacio ctrlPresentacio, String idEnquesta) {
        super(owner, "Gestionar Respostes", true);
        this.iCtrlPresentacio = ctrlPresentacio;
        this.idEnquesta = idEnquesta;
        cargarDatos();
        inicializar();
    }

    /**
     * Carrega les dades de l'enquesta (preguntes i respostes de l'usuari) des
     * del controlador.
     * 
     * Aquest mètode s'invoca a l'inicialització i després de cada modificació
     * per actualitzar les dades mostrades.
     */
    private void cargarDatos() {
        this.preguntes = iCtrlPresentacio.getPreguntesEnquestaRaw(idEnquesta);
        this.respostesUsuari = iCtrlPresentacio.getRespostesUsuariEnquesta(idEnquesta);
    }

    /**
     * Obté l'identificador de l'enquesta que s'està gestionant.
     * 
     * @return Identificador de l'enquesta.
     */
    public String getIdEnquesta() {
        return idEnquesta;
    }

    /**
     * Actualitza la vista recarregant les dades i refrescant el panel de
     * contingut.
     * 
     * Aquest mètode s'ha d'invocar després de cada modificació o esborrat de
     * respostes per reflectir els canvis visuals.
     */
    public void actualizarVista() {
        cargarDatos();
        refrescarPanelContent();
    }

    /**
     * Obté la resposta actual de l'usuari per a una pregunta específica.
     * 
     * @param idPregunta Identificador de la pregunta.
     * @return Text de la resposta, o null si no hi ha resposta per aquesta
     *         pregunta.
     */
    public String getResposta(String idPregunta) {
        return respostesUsuari.get(idPregunta);
    }

    /**
     * Inicialitza i configura tots els components gràfics del diàleg.
     * 
     * Crea una interfície amb tres seccions:
     * 
     * Adalt: Capçalera amb icona, títol i subtítol amb l'ID de l'enquesta.
     * Medio: Panel desplaçable amb targetes de respostes.
     * Abajo: Botons d'acció (Esborrar Totes i Tancar).
     * 
     */
    private void inicializar() {
        setSize(700, 600);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        getContentPane().setBackground(UIStyles.BACKGROUND_COLOR);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIStyles.BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(UIStyles.CARD_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UIStyles.BORDER_COLOR, 1, true),
                new EmptyBorder(20, 30, 20, 30)));

        JLabel iconLabel = new JLabel("📋");
        iconLabel.setFont(UIStyles.FONT_ICON_MEDIUM);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(iconLabel);
        headerPanel.add(Box.createVerticalStrut(8));

        JLabel titleLabel = new JLabel("Gestionar Respostes");
        titleLabel.setFont(UIStyles.FONT_DIALOG_TITLE);
        titleLabel.setForeground(UIStyles.TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Enquesta: " + idEnquesta);
        subtitleLabel.setFont(UIStyles.FONT_DIALOG_SUBTITLE_13);
        subtitleLabel.setForeground(UIStyles.SECONDARY_COLOR);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(subtitleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content panel
        panelContent = new JPanel();
        panelContent.setLayout(new BoxLayout(panelContent, BoxLayout.Y_AXIS));
        panelContent.setBackground(UIStyles.BACKGROUND_COLOR);
        panelContent.setBorder(new EmptyBorder(15, 0, 15, 0));

        refrescarPanelContent();

        JScrollPane scroll = new JScrollPane(panelContent);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UIStyles.BACKGROUND_COLOR);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(scroll, BorderLayout.CENTER);

        // Footer con botones
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        footerPanel.setBackground(UIStyles.BACKGROUND_COLOR);

        JButton btnEsborrarTot = UIComponents.createColorButton("Esborrar Totes", UIStyles.DANGER_COLOR);
        btnEsborrarTot.setActionCommand(MyActionListener.Action.ELIMINAR_RESPOSTA.name());
        btnEsborrarTot.addActionListener(new MyActionListener(iCtrlPresentacio, null, this));
        footerPanel.add(btnEsborrarTot);

        JButton btnTancar = UIComponents.createColorButton("Tancar", UIStyles.SECONDARY_COLOR);
        btnTancar.addActionListener(e -> setVisible(false));
        footerPanel.add(btnTancar);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    /**
     * Refresca el contingut del panel amb les respostes actualitzades.
     * 
     * Neteja el panel i torna a crear totes les targetes de respostes. Si
     * l'usuari no ha respost l'enquesta, mostra un missatge indicatiu.
     * 
     * Aquest mètode s'invoca després de cada modificació o esborrat de
     * respostes.
     */
    private void refrescarPanelContent() {
        panelContent.removeAll();

        if (respostesUsuari.isEmpty()) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setBackground(UIStyles.CARD_COLOR);
            emptyPanel.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(UIStyles.BORDER_COLOR, 1, true),
                    new EmptyBorder(40, 40, 40, 40)));

            JLabel emptyLabel = new JLabel("No has respost aquesta enquesta.");
            emptyLabel.setFont(UIStyles.FONT_NORMAL);
            emptyLabel.setForeground(UIStyles.SECONDARY_COLOR);
            emptyPanel.add(emptyLabel);

            panelContent.add(emptyPanel);
        } else {
            // [0] ID, [1] Text, [2] Tipus
            for (ArrayList<Object> p : preguntes) {
                String idPregunta = (String) p.get(0);
                if (respostesUsuari.containsKey(idPregunta)) {
                    panelContent.add(crearPanelResposta(p));
                    panelContent.add(Box.createVerticalStrut(10));
                }
            }
        }

        panelContent.revalidate();
        panelContent.repaint();
    }

    /**
     * Crea una targeta visual per a una resposta individual.
     * 
     * La targeta mostra:
     * 
     * Text de la pregunta (negreta).
     * Resposta actual de l'usuari (verd).
     * Tipus de pregunta (cursiva, gris).
     * Botó "Modificar" (taronja) a la dreta.
     * 
     *
     * @param p Pregunta (ArrayList amb [0]=ID, [1]=Text, [2]=Tipus) de la qual
     *          es mostra la resposta.
     * @return Panel JPanel amb la targeta completa.
     */
    private JPanel crearPanelResposta(ArrayList<Object> p) {
        JPanel panelPregunta = new JPanel(new BorderLayout(15, 0));
        panelPregunta.setBackground(UIStyles.CARD_COLOR);
        panelPregunta.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UIStyles.BORDER_COLOR, 1, true),
                new EmptyBorder(15, 20, 15, 20)));
        panelPregunta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        String id = (String) p.get(0);
        String text = (String) p.get(1);
        String tipus = (String) p.get(2);

        // Panel izquierdo con pregunta y respuesta
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(UIStyles.CARD_COLOR);

        JLabel lblPregunta = new JLabel(text);
        lblPregunta.setFont(UIStyles.FONT_NORMAL);
        lblPregunta.setForeground(UIStyles.TEXT_COLOR);
        lblPregunta.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(lblPregunta);
        leftPanel.add(Box.createVerticalStrut(5));

        String respostaActual = respostesUsuari.get(id);
        JLabel lblResposta = new JLabel(respostaActual);
        lblResposta.setFont(UIStyles.FONT_INPUT);
        lblResposta.setForeground(UIStyles.SUCCESS_COLOR);
        lblResposta.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(lblResposta);

        JLabel lblTipus = new JLabel("Tipus: " + tipus);
        lblTipus.setFont(UIStyles.FONT_INSTRUCTIONS);
        lblTipus.setForeground(UIStyles.SECONDARY_COLOR);
        lblTipus.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(lblTipus);

        panelPregunta.add(leftPanel, BorderLayout.CENTER);

        // Botón modificar
        JButton btnModificar = UIComponents.createColorButton("Modificar", UIStyles.WARNING_COLOR);
        btnModificar.setPreferredSize(new Dimension(120, 35));
        btnModificar.setActionCommand(MyActionListener.Action.MODIFICAR_RESPOSTA_INDIVIDUAL.name());
        // Pass the raw question list
        btnModificar.addActionListener(new MyActionListener(iCtrlPresentacio, null, new Object[] { this, p }));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        rightPanel.setBackground(UIStyles.CARD_COLOR);
        rightPanel.add(btnModificar);
        panelPregunta.add(rightPanel, BorderLayout.EAST);

        return panelPregunta;
    }
}

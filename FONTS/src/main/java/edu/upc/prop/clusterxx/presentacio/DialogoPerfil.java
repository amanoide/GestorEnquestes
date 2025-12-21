package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;

/**
 * Diàleg modal dissenyat per presentar informació detallada del perfil de
 * l'usuari.
 * 
 * Aquesta classe s'utilitza principalment per mostrar els resultats de
 * l'anàlisi de clustering,
 * permetent a l'usuari veure a quin grup o perfil ha estat assignat basant-se
 * en les seves
 * respostes.
 * 
 * Característiques:
 * Presentació clara amb una icona i títol destacats.
 * Àrea de text de només lectura per mostrar descripcions extenses del perfil.
 * Botó de tancament intuïtiu.
 */
public class DialogoPerfil extends JDialog {

    private JTextArea textAreaPerfil;
    private JButton btnTancar;

    /**
     * Constructor de la classe DialogoPerfil.
     * 
     * Crea una nova instància del diàleg, bloquejant la interacció amb la finestra
     * pare
     * fins que es tanqui.
     *
     * @param parent     Finestra (Frame) que actua com a propietària del diàleg.
     * @param infoPerfil Cadena de text que conté la informació completa del perfil
     *                   a visualitzar.
     */
    public DialogoPerfil(Frame parent, String infoPerfil) {
        super(parent, "El Meu Perfil", true);
        inicializarComponentes(infoPerfil);
    }

    /**
     * Configura la interfície gràfica de l'usuari per al diàleg.
     * 
     * Estructura del diàleg:
     * Capçalera: Icona d'usuari i titol "El Meu Perfil".
     * Cos: Àrea de text amb scroll (JScrollPane) per acomodar textos llargs sobre
     * el perfil.
     * Peu: Botó "Tancar" amb efecte de color en passar el ratolí.
     * 
     * @param infoPerfil El text descriptiu del perfil que s'inserirà a l'àrea de
     *                   text.
     */
    private void inicializarComponentes(String infoPerfil) {
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(UIStyles.BACKGROUND_COLOR);
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel superior con título e icono
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(UIStyles.CARD_COLOR);
        topPanel.setBorder(new CompoundBorder(
                new LineBorder(UIStyles.BORDER_COLOR, 1, true),
                new EmptyBorder(20, 20, 20, 20)));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(UIStyles.CARD_COLOR);

        JLabel iconLabel = new JLabel("👤");
        iconLabel.setFont(UIStyles.FONT_ICON_LARGE);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(iconLabel);
        headerPanel.add(Box.createVerticalStrut(10));

        JLabel titulo = new JLabel("El Meu Perfil");
        titulo.setFont(UIStyles.FONT_DIALOG_TITLE);
        titulo.setForeground(UIStyles.TEXT_COLOR);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(titulo);

        JLabel subtitulo = new JLabel("Perfils generats per anàlisi de clustering");
        subtitulo.setFont(UIStyles.FONT_DIALOG_SUBTITLE);
        subtitulo.setForeground(UIStyles.TEXT_SECONDARY);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(subtitulo);

        topPanel.add(headerPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Panel central con la información del perfil
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(UIStyles.BACKGROUND_COLOR);
        centerPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        textAreaPerfil = new JTextArea(infoPerfil);
        textAreaPerfil.setEditable(false);
        textAreaPerfil.setFont(UIStyles.FONT_TEXT_AREA);
        textAreaPerfil.setLineWrap(true);
        textAreaPerfil.setWrapStyleWord(true);
        textAreaPerfil.setBackground(UIStyles.CARD_COLOR);
        textAreaPerfil.setBorder(new EmptyBorder(15, 15, 15, 15));
        textAreaPerfil.setForeground(UIStyles.TEXT_COLOR);

        JScrollPane scrollPane = new JScrollPane(textAreaPerfil);
        scrollPane.setBorder(new CompoundBorder(
                new LineBorder(UIStyles.BORDER_COLOR, 1, true),
                new EmptyBorder(0, 0, 0, 0)));
        scrollPane.setPreferredSize(new Dimension(550, 350));

        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Panel inferior con botón
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        bottomPanel.setBackground(UIStyles.BACKGROUND_COLOR);

        btnTancar = new JButton("Tancar");
        btnTancar.setFont(UIStyles.FONT_BUTTON_PRIMARY);
        btnTancar.setForeground(Color.WHITE);
        btnTancar.setBackground(UIStyles.SUCCESS_COLOR);
        btnTancar.setPreferredSize(new Dimension(120, 40));
        btnTancar.setContentAreaFilled(true);
        btnTancar.setOpaque(true);
        btnTancar.setFocusPainted(false);
        btnTancar.setBorderPainted(false);
        btnTancar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTancar.addActionListener(e -> dispose());

        Color hoverColor = UIStyles.SUCCESS_COLOR.brighter();
        btnTancar.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnTancar.setBackground(hoverColor);
            }

            public void mouseExited(MouseEvent e) {
                btnTancar.setBackground(UIStyles.SUCCESS_COLOR);
            }
        });

        bottomPanel.add(btnTancar);
        add(bottomPanel, BorderLayout.SOUTH);

        // Configuración del diálogo
        pack();
        setSize(600, 550);
        setLocationRelativeTo(getParent());
        setResizable(false);
    }
}

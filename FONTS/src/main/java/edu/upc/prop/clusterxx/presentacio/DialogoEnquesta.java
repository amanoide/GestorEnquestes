package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Diàleg per a la creació i edició d'enquestes.
 * 
 * Aquest diàleg permet als usuaris crear noves enquestes o modificar enquestes
 * existents. Proporciona una interfície visual amb camps per introduir
 * l'identificador, títol i descripció de l'enquesta.
 * 
 * Funcionalitats clau:
 * 
 * Mode creació: Permet introduir ID, títol i descripció d'una nova enquesta.
 * Mode edició: Permet modificar títol i descripció (ID no editable).
 * Validació de camps obligatoris (ID i títol).
 * Interfície adaptativa amb icona i text diferents segons el mode.
 * Estils visuals consistents amb la resta de l'aplicació.
 * Efectes hover en botons per millorar la interacció.
 * 
 * 
 * El diàleg és modal, el que significa que bloqueja la finestra propietària
 * fins que l'usuari confirmi o cancel·li l'operació.
 */
public class DialogoEnquesta extends JDialog {
    /** Camp de text per a l'identificador de l'enquesta. */
    private JTextField textId = new JTextField(20);
    /** Camp de text per al títol de l'enquesta. */
    private JTextField textTitol = new JTextField(20);
    /** Àrea de text per a la descripció de l'enquesta. */
    private JTextArea textDesc = new JTextArea(4, 20);
    /** Indica si l'usuari ha confirmat la creació/edició de l'enquesta. */
    private boolean confirmado = false;

    /**
     * Constructor del diàleg d'enquesta.
     * 
     * Crea un diàleg modal per crear o editar una enquesta. La interfície
     * s'adapta segons si és mode creació o edició.
     *
     * @param owner      Finestra propietària del diàleg (per centrar-lo i mantenir
     *                   modalitat).
     * @param title      Títol que apareix a la barra del diàleg.
     * @param isCreating true per a mode creació (ID editable), false per a mode
     *                   edició (ID no editable).
     */
    public DialogoEnquesta(Frame owner, String title, boolean isCreating) {
        super(owner, title, true);
        inicializarComponentes(isCreating);
    }

    /**
     * Inicialitza i configura tots els components gràfics del diàleg.
     * 
     * Crea una interfície amb disseny vertical (BoxLayout) que conté:
     * 
     * Icona (📝 per crear, ✏️ per editar)
     * Títol i subtítol adaptatiu
     * Camps de formulari (ID, títol, descripció)
     * Botons d'acció (Cancel·lar i Confirmar/Crear)
     * 
     * 
     * En mode edició, el camp ID es deshabilita i es mostra amb fons gris per
     * indicar que no es pot modificar.
     *
     * @param isCreating true per a mode creació (ID editable), false per a mode
     *                   edició (ID no editable).
     */
    private void inicializarComponentes(boolean isCreating) {
        this.setSize(450, 550);
        this.setLocationRelativeTo(getOwner());
        this.setResizable(false);
        this.getContentPane().setBackground(UIStyles.BACKGROUND_COLOR);

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(UIStyles.CARD_COLOR);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Icono
        JLabel iconLabel = new JLabel(isCreating ? "📝" : "✏️");
        iconLabel.setFont(UIStyles.FONT_ICON_SMALL);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(iconLabel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Título
        JLabel titleLabel = new JLabel(isCreating ? "Nova Enquesta" : "Editar Enquesta");
        titleLabel.setFont(UIStyles.FONT_DIALOG_TITLE);
        titleLabel.setForeground(UIStyles.TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        // Subtítulo
        JLabel subtitle = new JLabel(isCreating ? "Introdueix les dades de l'enquesta" : "Modifica les dades");
        subtitle.setFont(UIStyles.FONT_DIALOG_SUBTITLE);
        subtitle.setForeground(UIStyles.SECONDARY_COLOR);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(subtitle);
        mainPanel.add(Box.createVerticalStrut(25));

        // Campo ID/Nom
        mainPanel.add(UIComponents.createLabel("Identificador" + (isCreating ? "" : " (no editable)")));
        mainPanel.add(Box.createVerticalStrut(5));
        UIComponents.styleTextField(textId);
        textId.setEditable(isCreating);
        if (!isCreating) {
            textId.setBackground(UIStyles.DISABLED_BACKGROUND);
        }
        mainPanel.add(textId);
        mainPanel.add(Box.createVerticalStrut(15));

        // Campo Título
        mainPanel.add(UIComponents.createLabel("Títol"));
        mainPanel.add(Box.createVerticalStrut(5));
        UIComponents.styleTextField(textTitol);
        mainPanel.add(textTitol);
        mainPanel.add(Box.createVerticalStrut(15));

        // Campo Descripción
        mainPanel.add(UIComponents.createLabel("Descripció"));
        mainPanel.add(Box.createVerticalStrut(5));
        textDesc.setFont(UIStyles.FONT_NORMAL);
        textDesc.setLineWrap(true);
        textDesc.setWrapStyleWord(true);
        textDesc.setBorder(new EmptyBorder(8, 12, 8, 12));
        JScrollPane scrollDesc = new JScrollPane(textDesc);
        scrollDesc.setMaximumSize(new Dimension(350, 80));
        scrollDesc.setPreferredSize(new Dimension(350, 80));
        scrollDesc.setBorder(new LineBorder(UIStyles.BORDER_COLOR, 1, true));
        scrollDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(scrollDesc);
        mainPanel.add(Box.createVerticalStrut(25));

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(UIStyles.CARD_COLOR);
        buttonPanel.setMaximumSize(new Dimension(350, 50));

        JButton btnCancelar = UIComponents.createColorButton("Cancel·lar", UIStyles.SECONDARY_COLOR);
        buttonPanel.add(btnCancelar);

        JButton btnConfirmar = UIComponents.createColorButton(isCreating ? "Crear Enquesta" : "Guardar Canvis", UIStyles.PRIMARY_COLOR);
        buttonPanel.add(btnConfirmar);

        mainPanel.add(buttonPanel);

        this.setContentPane(mainPanel);

        // Listeners
        btnConfirmar.addActionListener(e -> {
            if (validarCampos()) {
                confirmado = true;
                setVisible(false);
            }
        });

        btnCancelar.addActionListener(e -> {
            confirmado = false;
            setVisible(false);
        });

        // Enter para confirmar
        textTitol.addActionListener(e -> textDesc.requestFocus());
    }

    /**
     * Valida que els camps obligatoris (ID i títol) no estiguin buits.
     * 
     * Mostra missatges d'advertència si hi ha camps buits i posa el focus al
     * camp problemàtic.
     *
     * @return true si la validació és correcta, false si hi ha errors.
     */
    private boolean validarCampos() {
        if (textId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠ L'identificador és obligatori", "Camp requerit",
                    JOptionPane.WARNING_MESSAGE);
            textId.requestFocus();
            return false;
        }
        if (textTitol.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠ El títol és obligatori", "Camp requerit",
                    JOptionPane.WARNING_MESSAGE);
            textTitol.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * Omple el formulari amb les dades d'una enquesta existent.
     * 
     * Aquest mètode s'utilitza en mode edició per carregar les dades actuals
     * de l'enquesta als camps del formulari.
     *
     * @param id    Identificador de l'enquesta.
     * @param titol Títol de l'enquesta.
     * @param desc  Descripció de l'enquesta.
     */
    public void setDatos(String id, String titol, String desc) {
        textId.setText(id);
        textTitol.setText(titol);
        textDesc.setText(desc);
    }

    /**
     * Obté l'identificador de l'enquesta introduït per l'usuari.
     *
     * @return Identificador de l'enquesta (sense espais al principi/final).
     */
    public String getId() {
        return textId.getText().trim();
    }

    /**
     * Obté el títol de l'enquesta introduït per l'usuari.
     *
     * @return Títol de l'enquesta (sense espais al principi/final).
     */
    public String getTitol() {
        return textTitol.getText().trim();
    }

    /**
     * Obté la descripció de l'enquesta introduïda per l'usuari.
     *
     * @return Descripció de l'enquesta (sense espais al principi/final).
     */
    public String getDesc() {
        return textDesc.getText().trim();
    }

    /**
     * Indica si l'usuari ha confirmat la creació/edició prement el botó
     * corresponent.
     *
     * @return true si s'ha confirmat, false si s'ha cancel·lat.
     */
    public boolean isConfirmado() {
        return confirmado;
    }
}

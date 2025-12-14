package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Classe d'utilitat amb factory methods per crear components UI estilitzats.
 * 
 * Centralitza la creació i estilització de components Swing per mantenir
 * consistència visual i reducir duplicació de codi.
 */
public class UIComponents {

    // Constructor privat per evitar instanciació
    private UIComponents() {
        throw new AssertionError("UIComponents no es pot instanciar");
    }

    // ========== LABELS ==========

    /**
     * Crea una etiqueta estilitzada amb font negreta.
     *
     * @param text Text de l'etiqueta
     * @return JLabel estilitzat i centrat
     */
    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIStyles.FONT_LABEL);
        label.setForeground(UIStyles.TEXT_COLOR);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    /**
     * Crea una etiqueta de secció amb font més gran.
     *
     * @param text Text de la secció
     * @return JLabel de secció estilitzat
     */
    public static JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIStyles.FONT_SECTION);
        label.setForeground(UIStyles.TEXT_COLOR);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    /**
     * Crea una etiqueta de títol gran.
     *
     * @param text Text del títol
     * @return JLabel de títol centrat
     */
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIStyles.FONT_TITLE);
        label.setForeground(UIStyles.TEXT_COLOR);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    /**
     * Crea una etiqueta de subtítol.
     *
     * @param text Text del subtítol
     * @return JLabel de subtítol centrat amb color secundari
     */
    public static JLabel createSubtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIStyles.FONT_SUBTITLE);
        label.setForeground(UIStyles.SECONDARY_COLOR);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    /**
     * Crea una etiqueta amb icona emoji gran.
     *
     * @param icon Emoji a mostrar
     * @return JLabel amb icona gran centrada
     */
    public static JLabel createIconLabel(String icon) {
        JLabel label = new JLabel(icon);
        label.setFont(UIStyles.FONT_ICON_LARGE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    // ========== TEXT FIELDS ==========

    /**
     * Aplica l'estil estàndard a un camp de text.
     *
     * @param field Camp de text a estilitzar
     */
    public static void styleTextField(JTextField field) {
        field.setFont(UIStyles.FONT_NORMAL);
        field.setMaximumSize(UIStyles.TEXTFIELD_SIZE);
        field.setPreferredSize(UIStyles.TEXTFIELD_SIZE);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UIStyles.BORDER_COLOR, 1, true),
                new EmptyBorder(8, 12, 8, 12)));
    }

    /**
     * Crea i retorna un JTextField estilitzat.
     *
     * @param columns Nombre de columnes
     * @return JTextField estilitzat
     */
    public static JTextField createTextField(int columns) {
        JTextField field = new JTextField(columns);
        styleTextField(field);
        return field;
    }

    /**
     * Crea i retorna un JPasswordField estilitzat.
     *
     * @param columns Nombre de columnes
     * @return JPasswordField estilitzat
     */
    public static JPasswordField createPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        styleTextField(field);
        return field;
    }

    // ========== BUTTONS ==========

    /**
     * Estilitza un botó com a primari o secundari.
     *
     * @param button    Botó a estilitzar
     * @param isPrimary Si és cert, aplica estil primari; altrament, secundari
     */
    public static void styleButton(JButton button, boolean isPrimary) {
        button.setFont(isPrimary ? UIStyles.FONT_BUTTON_PRIMARY : UIStyles.FONT_BUTTON_SECONDARY);
        button.setMaximumSize(UIStyles.BUTTON_SIZE);
        button.setPreferredSize(UIStyles.BUTTON_SIZE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (isPrimary) {
            button.setForeground(Color.WHITE);
            button.setBackground(UIStyles.PRIMARY_COLOR);
            button.setBorderPainted(false);
            button.setContentAreaFilled(true);
            button.setOpaque(true);
            addHoverEffect(button, UIStyles.PRIMARY_COLOR, UIStyles.PRIMARY_HOVER);
        } else {
            button.setForeground(UIStyles.PRIMARY_COLOR);
            button.setBackground(UIStyles.CARD_COLOR);
            button.setBorder(new LineBorder(UIStyles.PRIMARY_COLOR, 1, true));
            button.setContentAreaFilled(true);
            button.setOpaque(true);
            addHoverEffect(button, UIStyles.CARD_COLOR, UIStyles.HOVER_LIGHT);
        }
    }

    /**
     * Crea un botó estilitzat amb un color personalitzat.
     *
     * @param text  Text del botó
     * @param color Color de fons del botó
     * @return JButton estilitzat
     */
    public static JButton createColorButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(UIStyles.FONT_BUTTON_ICON);
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        addHoverEffect(btn, color, color.brighter());
        return btn;
    }

    /**
     * Estilitza un botó amb un color específic (versió alternativa).
     *
     * @param button Botó a estilitzar
     * @param color  Color de fons
     */
    public static void styleButton(JButton button, Color color) {
        button.setFont(UIStyles.FONT_BUTTON_ICON);
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        addHoverEffect(button, color, color.brighter());
    }

    /**
     * Crea un botó amb mida personalitzada.
     *
     * @param text      Text del botó
     * @param width     Amplada
     * @param height    Alçada
     * @param isPrimary Si és botó primari
     * @return JButton estilitzat
     */
    public static JButton createButton(String text, int width, int height, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
        styleButton(button, isPrimary);
        return button;
    }

    // ========== PANELS ==========

    /**
     * Crea un panell tipus targeta amb vores i ombra.
     *
     * @return JPanel estilitzat com a targeta
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(UIStyles.CARD_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 3, UIStyles.SHADOW_COLOR),
                BorderFactory.createCompoundBorder(
                        new LineBorder(UIStyles.BORDER_COLOR, 1, true),
                        new EmptyBorder(40, 50, 40, 50))));
        return panel;
    }

    /**
     * Crea un panell tipus targeta amb padding personalitzat.
     *
     * @param top    Padding superior
     * @param left   Padding esquerre
     * @param bottom Padding inferior
     * @param right  Padding dret
     * @return JPanel estilitzat com a targeta
     */
    public static JPanel createCardPanel(int top, int left, int bottom, int right) {
        JPanel panel = new JPanel();
        panel.setBackground(UIStyles.CARD_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 3, UIStyles.SHADOW_COLOR),
                BorderFactory.createCompoundBorder(
                        new LineBorder(UIStyles.BORDER_COLOR, 1, true),
                        new EmptyBorder(top, left, bottom, right))));
        return panel;
    }

    // ========== EFECTES ==========

    /**
     * Afegeix efecte hover a un botó canviant el color de fons.
     *
     * @param button      Botó al qual afegir l'efecte
     * @param normalColor Color normal
     * @param hoverColor  Color en hover
     */
    public static void addHoverEffect(JButton button, Color normalColor, Color hoverColor) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(normalColor);
            }
        });
    }
}

package edu.upc.prop.clusterxx.presentacio;

import java.awt.*;

/**
 * Classe que centralitza tots els estils visuals de l'aplicació.
 * 
 * Defineix colors, fonts i dimensions estàndard per mantenir
 * una interfície consistent en totes les vistes i diàlegs.
 */
public class UIStyles {
    
    // ========== COLORS ==========
    
    /** Color primari de l'aplicació (blau) */
    public static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    
    /** Color primari en estat hover (blau clar) */
    public static final Color PRIMARY_HOVER = new Color(52, 152, 219);
    
    /** Color secundari (gris) */
    public static final Color SECONDARY_COLOR = new Color(149, 165, 166);
    
    /** Color de fons de l'aplicació (gris clar) */
    public static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    
    /** Color de targetes i panells (blanc) */
    public static final Color CARD_COLOR = Color.WHITE;
    
    /** Color de text principal (gris fosc) */
    public static final Color TEXT_COLOR = new Color(44, 62, 80);
    
    /** Color per missatges d'error (vermell) */
    public static final Color ERROR_COLOR = new Color(231, 76, 60);
    
    /** Color per missatges d'èxit (verd) */
    public static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    
    /** Color per avisos (taronja) */
    public static final Color WARNING_COLOR = new Color(243, 156, 18);
    
    /** Color per accions perilloses (vermell) */
    public static final Color DANGER_COLOR = new Color(231, 76, 60);
    
    /** Color de vores (gris clar) */
    public static final Color BORDER_COLOR = new Color(189, 195, 199);
    
    /** Color de fons hover secundari (blau molt clar) */
    public static final Color HOVER_LIGHT = new Color(235, 245, 251);
    
    /** Color per text secundari/subtítols (gris mitjà) */
    public static final Color TEXT_SECONDARY = new Color(127, 140, 141);
    
    /** Color per fons desactivat (gris molt clar) */
    public static final Color DISABLED_BACKGROUND = new Color(245, 245, 245);
    
    /** Color per vores secundàries (gris) */
    public static final Color BORDER_LIGHT = new Color(220, 220, 220);
    
    /** Color de selecció per llistes (blau amb transparència) */
    public static final Color SELECTION_COLOR = new Color(52, 152, 219, 80);
    
    /** Color per vores mitjanes (gris mitjà) */
    public static final Color BORDER_MEDIUM = new Color(200, 200, 200);
    
    /** Color per fons de respostes (blau cel clar) */
    public static final Color RESPONSE_BACKGROUND = new Color(240, 248, 255);
    
    /** Color per botó eliminar compte (vermell fosc) */
    public static final Color DARK_RED = new Color(139, 0, 0);
    
    /** Color per botó importar (verd bosc) */
    public static final Color FOREST_GREEN = new Color(34, 139, 34);
    
    /** Color d'ombra (negre amb transparència) */
    public static final Color SHADOW_COLOR = new Color(0, 0, 0, 30);
    
    // ========== FONTS ==========
    
    /** Font per títols principals */
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    
    /** Font per subtítols */
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 14);
    
    /** Font per etiquetes (negreta) */
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 12);
    
    /** Font normal per text */
    public static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    
    /** Font per botons principals */
    public static final Font FONT_BUTTON_PRIMARY = new Font("Segoe UI", Font.BOLD, 14);
    
    /** Font per botons secundaris */
    public static final Font FONT_BUTTON_SECONDARY = new Font("Segoe UI", Font.PLAIN, 14);
    
    /** Font per botons amb icona emoji */
    public static final Font FONT_BUTTON_ICON = new Font("Segoe UI Emoji", Font.BOLD, 12);
    
    /** Font per icones grans (emoji) */
    public static final Font FONT_ICON_LARGE = new Font("Segoe UI Emoji", Font.PLAIN, 48);
    
    /** Font per secció de títols */
    public static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 16);
    
    /** Font per títols de diàlegs */
    public static final Font FONT_DIALOG_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    
    /** Font per títols de vistes */
    public static final Font FONT_VIEW_TITLE = new Font("Segoe UI Emoji", Font.BOLD, 20);
    
    /** Font per icones mitjanes */
    public static final Font FONT_ICON_MEDIUM = new Font("Segoe UI Emoji", Font.PLAIN, 36);
    
    /** Font per icones petites */
    public static final Font FONT_ICON_SMALL = new Font("Segoe UI Emoji", Font.PLAIN, 40);
    
    /** Font per subtítols de diàlegs */
    public static final Font FONT_DIALOG_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 12);
    
    /** Font per subtítols de diàlegs (variant 13px) */
    public static final Font FONT_DIALOG_SUBTITLE_13 = new Font("Segoe UI", Font.PLAIN, 13);
    
    /** Font per títols d'enquesta/pregunta */
    public static final Font FONT_ENQUESTA_TITLE = new Font("Segoe UI Emoji", Font.BOLD, 18);
    
    /** Font per etiquetes de pregunta (bold 13px) */
    public static final Font FONT_PREGUNTA_LABEL = new Font("Segoe UI Emoji", Font.BOLD, 13);
    
    /** Font per instruccions (italic 11px) */
    public static final Font FONT_INSTRUCTIONS = new Font("Segoe UI", Font.ITALIC, 11);
    
    /** Font per camps de text i inputs */
    public static final Font FONT_INPUT = new Font("Segoe UI", Font.PLAIN, 13);
    
    /** Font per llistes */
    public static final Font FONT_LIST = new Font("Segoe UI", Font.PLAIN, 14);
    
    /** Font per llistes petites */
    public static final Font FONT_LIST_SMALL = new Font("Segoe UI", Font.PLAIN, 13);
    
    /** Font per checkbox */
    public static final Font FONT_CHECKBOX = new Font("Segoe UI", Font.PLAIN, 12);
    
    /** Font per text monoespaciat (resultats) */
    public static final Font FONT_MONOSPACED = new Font("Monospaced", Font.PLAIN, 12);
    
    /** Font per text àrea perfil */
    public static final Font FONT_TEXT_AREA = new Font("Segoe UI", Font.PLAIN, 13);
    
    /** Font per missatges buits (italic) */
    public static final Font FONT_EMPTY_MESSAGE = new Font("Segoe UI", Font.ITALIC, 14);
    
    /** Font per totals/estadístiques (bold 13px) */
    public static final Font FONT_STATS = new Font("Segoe UI", Font.BOLD, 13);
    
    // ========== DIMENSIONS ==========
    
    /** Mida estàndard per camps de text */
    public static final Dimension TEXTFIELD_SIZE = new Dimension(280, 40);
    
    /** Mida estàndard per botons */
    public static final Dimension BUTTON_SIZE = new Dimension(280, 45);
    
    /** Mida per botons petits */
    public static final Dimension BUTTON_SMALL_SIZE = new Dimension(120, 35);
    
    /** Mida per botons grans */
    public static final Dimension BUTTON_LARGE_SIZE = new Dimension(200, 50);
    
    // Constructor privat per evitar instanciació
    private UIStyles() {
        throw new AssertionError("UIStyles no es pot instanciar");
    }
}

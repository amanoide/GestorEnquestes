package edu.upc.prop.clusterxx.domini.classes;

/**
 * Enumeració que representa els diferents tipus de preguntes.
 */
public enum TipusPregunta {
    /**
     * Variable quantitativa o numèrica (emmagatzema un sol valor numèric).
     */
    NUMERICA,
    
    /**
     * Variable qualitativa ordenada (ex: molt-poc/poc/normal/força/molt).
     * Les modalitats són ordenades.
     */
    QUALITATIVA_ORDENADA,
    
    /**
     * Variable qualitativa no ordenada (ex: groc/blau/verd/vermell/lila/marró).
     * Emmagatzema un sol valor, les modalitats no són ordenades.
     */
    QUALITATIVA_NO_ORDENADA_SIMPLE,
    
    /**
     * Variable qualitativa no ordenada múltiple (ex: {groc, verd, lila}).
     * Emmagatzema un conjunt de p valors d'un màxim de q modalitats (1 ≤ p ≤ q).
     */
    QUALITATIVA_NO_ORDENADA_MULTIPLE,
    
    /**
     * Variable string en format lliure (qualsevol text).
     * No existeix cap valor predeterminat.
     */
    TEXT_LLIURE
}

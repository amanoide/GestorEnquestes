package edu.upc.prop.clusterxx.domini.classes;

/**
 * Representa una opció de resposta dins d'una pregunta qualitativa d'una enquesta.
 * Cada opció té un identificador, un text descriptiu i, opcionalment, un valor d'ordre
 * per a preguntes de tipus qualitativa ordenada.
 */
public class Opcio {
    private int id;
    private String text;
    private Integer ordre; // Per a opcions ordenades (opcional)

    /**
     * Constructor per a opcions no ordenades.
     * Inicialitza una opció amb un ID i un text, sense un ordre específic.
     *
     * @param id L'identificador numèric de l'opció.
     * @param text El text descriptiu de l'opció.
     */
    public Opcio(int id, String text) {
        this.id = id;
        this.text = text;
        this.ordre = null; 
    }

    /**
     * Constructor per a opcions ordenades.
     * Inicialitza una opció amb un ID, un text i un valor d'ordre.
     *
     * @param id L'identificador numèric de l'opció.
     * @param text El text descriptiu de l'opció.
     * @param ordre El valor numèric que indica la posició de l'opció en una escala.
     */
    public Opcio(int id, String text, Integer ordre) {
        this.id = id;
        this.text = text;
        setOrdre(ordre);
    }

    /**
     * Retorna l'identificador de l'opció.
     *
     * @return L'ID numèric de l'opció.
     */
    public int getId() {
        return id;
    }

    /**
     * Retorna el text descriptiu de l'opció.
     *
     * @return El text de l'opció.
     */
    public String getText() {
        return text;
    }

    /**
     * Retorna el valor d'ordre de l'opció.
     *
     * @return L'enter que representa l'ordre, o `null` si l'opció no és ordenada.
     */
    public Integer getOrdre() {
        return ordre;
    }

    /**
     * Estableix un nou text per a l'opció.
     *
     * @param text El nou text descriptiu.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Estableix el valor d'ordre de l'opció.
     * Si l'ordre és null o menor o igual a zero, es considera no ordenada (ordre = null).
     *
     * @param ordre El nou valor d'ordre.
     */
    public void setOrdre(Integer ordre) {
        if (ordre == null || ordre <= 0) {
            this.ordre = null;
        } else {
            this.ordre = ordre;
        }
    }

    /**
     * Comprova si l'opció té un valor d'ordre assignat.
     *
     * @return `true` si l'opció és ordenada (té un ordre), `false` en cas contrari.
     */
    public boolean esOrdenada() {
        return ordre != null;
    }

    /**
     * Retorna una representació en format String de l'objecte Opcio.
     * Inclou l'ordre si està definit.
     *
     * @return Una cadena de text que representa l'opció.
     */
    @Override
    public String toString() {
        if (ordre != null) {
            return "Opcio{id=" + id + ", text='" + text + "', ordre=" + ordre + "}";
        }
        return "Opcio{id=" + id + ", text='" + text + "'}";
    }
}

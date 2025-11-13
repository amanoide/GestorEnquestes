package edu.upc.prop.clusterxx.domini.classes;

/**
 * Representa una opció dins d'una pregunta d'una enquesta.
 */
public class Opcio {
    private int id;
    private String text;
    private Integer ordre; // Per a opcions ordenades (opcional)

    public Opcio(int id, String text) {
        this.id = id;
        this.text = text;
        this.ordre = null; 
    }

    public Opcio(int id, String text, int ordre) {
        this.id = id;
        this.text = text;
        this.ordre = ordre;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Integer getOrdre() {
        return ordre;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setOrdre(int ordre) {
        this.ordre = ordre;
    }

    public boolean esOrdenada() {
        return ordre != null;
    }

    @Override
    public String toString() {
        if (ordre != null) {
            return "Opcio{id=" + id + ", text='" + text + "', ordre=" + ordre + "}";
        }
        return "Opcio{id=" + id + ", text='" + text + "'}";
    }
}

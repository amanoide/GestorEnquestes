package edu.upc.prop.clusterxx.domini.classes;

/**
 * Representa la resposta d'un usuari a una pregunta específica d'una enquesta.
 * <p>
 * Aquesta classe emmagatzema el contingut d'una resposta, vinculant-la a un usuari
 * i a una pregunta mitjançant els seus identificadors. És una entitat simple
 * que conté les dades bàsiques d'una resposta.
 * </p>
 *
 * @see Usuari
 * @see Pregunta
 */
public class Resposta {
    private String id;
    private String idPregunta;
    private String textResposta;
    private String usernameUsuari;

    /**
     * Constructor de la classe Resposta.
     *
     * @param id L'identificador únic de la resposta.
     * @param idPregunta L'identificador de la pregunta a la qual respon.
     * @param textResposta El contingut de la resposta en format de text.
     * @param usuari L'objecte {@link Usuari} que ha donat la resposta.
     */
    public Resposta(String id, String idPregunta, String textResposta, Usuari usuari) {
        this.id = id;
        this.idPregunta = idPregunta;
        this.textResposta = textResposta;
        this.usernameUsuari = usuari.getUsername();
    }

    /**
     * Retorna l'identificador únic de la resposta.
     * @return L'ID de la resposta.
     */
    public String getId() {
        return id;
    }

    /**
     * Retorna l'identificador de la pregunta associada a aquesta resposta.
     * @return L'ID de la pregunta.
     */
    public String getIdPregunta() {
        return idPregunta;
    }

    /**
     * Retorna el contingut de la resposta.
     * @return El text de la resposta.
     */
    public String getTextResposta() {
        return textResposta;
    }

    /**
     * Retorna el nom d'usuari de qui ha donat la resposta.
     * @return El nom de l'usuari.
     */
    public String getUsernameUsuari() {
        return usernameUsuari;
    }

    /**
     * Modifica el text d'aquesta resposta.
     * @param nouText El nou contingut per a la resposta.
     */
    public void modificarResposta(String nouText) {
        this.textResposta = nouText;
    }

    /**
     * Retorna una representació en format String de l'objecte Resposta.
     * @return Una cadena de text que representa la resposta.
     */
    @Override
    public String toString() {
        return "Resposta{" +
                "id='" + id + '\'' +
                ", idPregunta='" + idPregunta + '\'' +
                ", textResposta='" + textResposta + '\'' +
                ", usuari='" + usernameUsuari + '\'' +
                '}';
    }
}

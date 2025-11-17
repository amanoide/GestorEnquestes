package edu.upc.prop.clusterxx.domini.classes;

/**
 * Classe que representa una Resposta feta per un usuari i associada a una pregunta ¿i a una enquesta?.
 */

public class Resposta {
    private String id;
    private String idPregunta;
    private String textResposta;
    private String usernameUsuari;

    
    public Resposta(String id, String idPregunta, String textResposta, Usuari usuari) {
        this.id = id;
        this.idPregunta = idPregunta;
        this.textResposta = textResposta;
        this.usernameUsuari = usuari.getUsername();
    }

    public String getId() {
        return id;
    }

    public String getIdPregunta() {
        return idPregunta;
    }

    public String getTextResposta() {
        return textResposta;
    }

    public String getUsernameUsuari() {
        return usernameUsuari;
    }
    
    public void modificarResposta(String nouText) {
        this.textResposta = nouText;
    }

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

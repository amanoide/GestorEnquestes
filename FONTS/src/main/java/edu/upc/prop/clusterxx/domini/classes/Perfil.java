package edu.upc.prop.clusterxx.domini.classes;

/**
 * Classe que representa un perfil d'usuari.
 * Conté informació per agrupar usuaris en clusters.
 * @author ClusterXX
 */
public class Perfil {
    /** Identificador únic del perfil */
    private Integer id;
    /** Descripció del tipus de perfil per al clustering */
    private String descripcion;

    /**
     * Constructor de la classe Perfil
     * @param id Identificador únic del perfil
     * @param descripcion Descripció del perfil
     */
    public Perfil(Integer id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    /**
     * Obté l'identificador del perfil
     * @return Identificador únic del perfil
     */
    public Integer getId() {
        return id;
    }

    /**
     * Modifica l'identificador del perfil
     * @param id Nou identificador únic per al perfil
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Obté la descripció del perfil
     * @return Descripció detallada del perfil
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Modifica la descripció del perfil
     * @param descripcion Nova descripció per al perfil
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}

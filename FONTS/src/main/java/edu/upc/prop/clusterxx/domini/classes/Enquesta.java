package edu.upc.prop.clusterxx.domini.classes;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Representa una enquesta en el sistema. Conté un identificador, títol, descripció,
 * el creador, un conjunt de preguntes i la llista de participants que l'han contestat.
 */
public class Enquesta {
    // Atributs
    private String id;
    private String titol;
    private String descripcio;
    private String idCreador;
    private HashMap<String, Pregunta> preguntes;
    private ArrayList<String> participants; // ← NUEVO: lista de usernames que han contestado

    /**
     * Constructor de la classe Enquesta.
     * Inicialitza una nova enquesta amb els paràmetres proporcionats.
     *
     * @param id Identificador únic de l'enquesta.
     * @param titol Títol de l'enquesta.
     * @param descripcio Descripció de l'enquesta.
     * @param creador Objecte Usuari que crea l'enquesta.
     */
    public Enquesta(String id, String titol, String descripcio, Usuari creador) {
        this.id = id;
        this.titol = titol;
        this.descripcio = descripcio;
        this.idCreador = creador.getUsername();
        this.preguntes = new HashMap<>();
        this.participants = new ArrayList<>(); // ← NUEVO
    }

    /**
     * Retorna l'identificador de l'enquesta.
     *
     * @return L'ID de l'enquesta.
     */
    public String getId() {
        return id;
    }

    /**
     * Retorna el títol de l'enquesta.
     *
     * @return El títol de l'enquesta.
     */
    public String getTitol() {
        return titol;
    }

    /**
     * Estableix un nou títol per a l'enquesta.
     *
     * @param titol El nou títol de l'enquesta.
     */
    public void setTitol(String titol) {
        this.titol = titol;
    }

    /**
     * Retorna la descripció de l'enquesta.
     *
     * @return La descripció de l'enquesta.
     */
    public String getDescripcio() {
        return descripcio;
    }

    /**
     * Estableix una nova descripció per a l'enquesta.
     *
     * @param descripcio La nova descripció de l'enquesta.
     */
    public void setDescripcio(String descripcio) {
        this.descripcio = descripcio;
    }

    /**
     * Retorna l'identificador de l'usuari creador de l'enquesta.
     *
     * @return L'ID de l'usuari creador.
     */
    public String getIdCreador() {
        return idCreador;
    }

    /**
     * Estableix un nou identificador de creador per a l'enquesta.
     *
     * @param idCreador El nou ID de l'usuari creador.
     */
    public void setIdCreador(String idCreador) {
        this.idCreador = idCreador;
    }

    /**
     * Afegeix una pregunta a l'enquesta.
     *
     * @param pregunta La pregunta a afegir.
     */
    public void afegirPregunta(Pregunta pregunta) {
        this.preguntes.put(pregunta.getId(), pregunta);
    }

    /**
     * Elimina una pregunta de l'enquesta a partir del seu ID.
     *
     * @param idPregunta L'ID de la pregunta a eliminar.
     */
    public void eliminarPregunta(String idPregunta) {
        this.preguntes.remove(idPregunta);
    }

    /**
     * Retorna una pregunta específica a partir del seu ID.
     *
     * @param idPregunta L'ID de la pregunta a obtenir.
     * @return La pregunta corresponent a l'ID, o null si no existeix.
     */
    public Pregunta getPregunta(String idPregunta) {
        return this.preguntes.get(idPregunta);
    }

    /**
     * Retorna una llista amb totes les preguntes de l'enquesta.
     *
     * @return Un ArrayList amb totes les preguntes.
     */
    public ArrayList<Pregunta> getPreguntes() {
        return new ArrayList<>(preguntes.values());
    }
    
    

    /**
     * Modifica una pregunta existent a l'enquesta.
     * Si la pregunta amb l'ID especificat existeix, se substitueix per la nova.
     *
     * @param idPregunta L'ID de la pregunta a modificar.
     * @param nova La nova pregunta que substituirà l'antiga.
     */
    public void modificarPregunta(String idPregunta, Pregunta nova) {
        if (this.preguntes.containsKey(idPregunta)) {
            this.preguntes.put(idPregunta, nova);
        }
    }

    /**
     * Registra la participació d'un usuari a l'enquesta.
     * Afegeix el nom d'usuari a la llista de participants si no hi era prèviament.
     *
     * @param username El nom d'usuari a registrar.
     */
    public void registrarParticipacio(String username) {
        if (!participants.contains(username)) {
            participants.add(username);
        }
    }

    /**
     * Elimina la participació d'un usuari de l'enquesta.
     *
     * @param username El nom d'usuari a eliminar de la llista de participants.
     */
    public void eliminarParticipacio(String username) {
        participants.remove(username);
    }

    /**
     * Comprova si un usuari ha respost l'enquesta.
     *
     * @param username El nom d'usuari a comprovar.
     * @return `true` si l'usuari ha participat, `false` en cas contrari.
     */
    public boolean haRespostUsuari(String username) {
        return participants.contains(username);
    }

    /**
     * Retorna el nombre total de participants que han respost l'enquesta.
     *
     * @return El nombre de participants.
     */
    public int getNumParticipants() {
        return participants.size();
    }

    /**
     * Retorna una llista amb els noms d'usuari de tots els participants.
     *
     * @return Un ArrayList amb els noms d'usuari dels participants.
     */
    public ArrayList<String> getParticipants() {
        return new ArrayList<>(participants);
    }
}

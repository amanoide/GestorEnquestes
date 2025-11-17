package edu.upc.prop.clusterxx.domini.classes;

import java.util.ArrayList;
import java.util.HashMap;

public class Enquesta {
    // Atributs
    private String id;
    private String titol;
    private String descripcio;
    private String idCreador;
    private HashMap<String, Pregunta> preguntes;
    private ArrayList<String> participants; // ← NUEVO: lista de usernames que han contestado

    public Enquesta(String id, String titol, String descripcio, Usuari creador) {
        this.id = id;
        this.titol = titol;
        this.descripcio = descripcio;
        this.idCreador = creador.getUsername();
        this.preguntes = new HashMap<>();
        this.participants = new ArrayList<>(); // ← NUEVO
    }
    
    public String getId() {
        return id;
    }

    public String getTitol() {
        return titol;
    }

    public void setTitol(String titol) {
        this.titol = titol;
    }

    public String getDescripcio() {
        return descripcio;
    }

    public void setDescripcio(String descripcio) {
        this.descripcio = descripcio;
    }
    
    public String getIdCreador() {
        return idCreador;
    }

    public void setIdCreador(String idCreador) {
        this.idCreador = idCreador;
    }
    
    public void afegirPregunta(Pregunta pregunta) {
        this.preguntes.put(pregunta.getId(), pregunta);
    }
    
    public void eliminarPregunta(String idPregunta) {
        this.preguntes.remove(idPregunta);
    }
    
    public Pregunta getPregunta(String idPregunta) {
        return this.preguntes.get(idPregunta);
    }

    public ArrayList<Pregunta> getPreguntes() {
        return new ArrayList<>(preguntes.values());
    }
    
    

    
    public void modificarPregunta(String idPregunta, Pregunta nova) {
        if (this.preguntes.containsKey(idPregunta)) {
            this.preguntes.put(idPregunta, nova);
        }
    }

    
    public void registrarParticipacio(String username) {
        if (!participants.contains(username)) {
            participants.add(username);
        }
    }

    
    public void eliminarParticipacio(String username) {
        participants.remove(username);
    }

    public boolean haRespostUsuari(String username) {
        return participants.contains(username);
    }

    public int getNumParticipants() {
        return participants.size();
    }

    public ArrayList<String> getParticipants() {
        return new ArrayList<>(participants);
    }
}

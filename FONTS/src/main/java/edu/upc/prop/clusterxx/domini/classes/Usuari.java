package edu.upc.prop.clusterxx.domini.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Usuari {
    // Atributs estàtics
    private static Usuari usuariActual = null;

    // Atributs d'instància
    private String username;
    private String password;
    private List<Enquesta> enquestesCreades;
    private HashMap<String, Resposta> respostesUsuari; // idResposta -> Resposta
    private HashMap<String, Perfil> perfils; // idEnquesta -> Perfil (un perfil per enquesta analitzada)

    // Constructor
    public Usuari(String username, String password) {
        this.username = username;
        this.password = password;
        this.enquestesCreades = new ArrayList<>();
        this.respostesUsuari = new HashMap<>();
        this.perfils = new HashMap<>();
    }

    // Mètodes públics de negoci
    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public void addEnquestaCreada(Enquesta enquesta) {
        this.enquestesCreades.add(enquesta);
    }

    public void removeEnquestaCreada(Enquesta enquesta) {
        this.enquestesCreades.remove(enquesta);
    }

    public void afegirResposta(String idResposta, Resposta resposta) {
        this.respostesUsuari.put(idResposta, resposta);
    }

    public void eliminarResposta(String idResposta) {
        this.respostesUsuari.remove(idResposta);
    }

    public Resposta getResposta(String idResposta) {
        return this.respostesUsuari.get(idResposta);
    }

    public HashMap<String, Resposta> getRespostesUsuari() {
        return new HashMap<>(respostesUsuari);
    }
    
    // Mètodes per a gestió de perfils
    public void assignarPerfil(String idEnquesta, Perfil perfil) {
        this.perfils.put(idEnquesta, perfil);
    }
    
    public Perfil getPerfil(String idEnquesta) {
        return this.perfils.get(idEnquesta);
    }
    
    public HashMap<String, Perfil> getPerfils() {
        return new HashMap<>(perfils);
    }
    
    public boolean tePerfil(String idEnquesta) {
        return this.perfils.containsKey(idEnquesta);
    }
    
    public void eliminarPerfil(String idEnquesta) {
        this.perfils.remove(idEnquesta);
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public List<Enquesta> getEnquestesCreades() {
        return new ArrayList<>(enquestesCreades);
    }

    // Mètodes estàtics per a gestió de sessió
    public static void login(Usuari usuari) {
        usuariActual = usuari;
    }

    public static Usuari getUsuariActual() {
        return usuariActual;
    }

    public static void logout() {
        usuariActual = null;
    }
}


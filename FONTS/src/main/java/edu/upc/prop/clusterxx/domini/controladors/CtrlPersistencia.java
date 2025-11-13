package edu.upc.prop.clusterxx.domini.controladors;

import edu.upc.prop.clusterxx.domini.classes.Enquesta;
import edu.upc.prop.clusterxx.domini.classes.Perfil;
import edu.upc.prop.clusterxx.domini.classes.Pregunta;
import edu.upc.prop.clusterxx.domini.classes.Resposta;
import edu.upc.prop.clusterxx.domini.classes.Usuari;

import java.util.HashMap;
import java.util.ArrayList;

/**
 * Controlador de persistencia centralitzat que emmagatzema totes les dades del sistema en memòria.
 * Utilitza el patró Singleton per garantir una única instància.
 * Tots els controladors accedeixen a aquesta classe per obtenir i modificar dades.
 * @author ClusterXX
 */
public class CtrlPersistencia {
    private static CtrlPersistencia instance;
    
    // Repositoris centrals en memòria
    private ArrayList<Enquesta> enquestes;
    // Les respostes es guarden independentment per facilitar persistència
    // Cada Pregunta té les seves respostes, però també es guarden aquí per a disc
    // Estructura: id únic de resposta -> Resposta
    private HashMap<String, Resposta> respostes;
    private HashMap<String, Usuari> usuaris;
    private HashMap<String, Perfil> perfils;
    private HashMap<String, Pregunta> preguntes; // Totes les preguntes del sistema

    private CtrlPersistencia() {
        this.enquestes = new ArrayList<>();
        this.respostes = new HashMap<>();
        this.usuaris = new HashMap<>();
        this.perfils = new HashMap<>();
        this.preguntes = new HashMap<>();
    }

    public static CtrlPersistencia getInstance() {
        if (instance == null) instance = new CtrlPersistencia();
        return instance;
    }

    // ===========================================
    // ENQUESTES - Mètodes d'accés i modificació
    // ===========================================
    
    /**
     * Guarda/actualitza les enquestes (substitueix totes).
     * @param enquestes ArrayList d'enquestes
     */
    public void saveEnquestes(ArrayList<Enquesta> enquestes) {
        this.enquestes = new ArrayList<>(enquestes);
        System.out.println("✓ Guardades " + enquestes.size() + " enquestes a CtrlPersistencia");
    }

    /**
     * Carrega totes les enquestes.
     * @return ArrayList d'enquestes
     */
    public ArrayList<Enquesta> loadEnquestes() {
        System.out.println("ℹ Carregant " + enquestes.size() + " enquestes des de CtrlPersistencia");
        return new ArrayList<>(enquestes);
    }
    
    /**
     * Afegeix una enquesta.
     * @param enquesta L'enquesta a afegir
     */
    public void afegirEnquesta(Enquesta enquesta) {
        enquestes.add(enquesta);
    }
    
    /**
     * Elimina una enquesta per ID.
     * @param id ID de l'enquesta
     * @return true si s'ha eliminat, false si no existeix
     */
    public boolean eliminarEnquesta(String id) {
        return enquestes.removeIf(e -> e.getId().equals(id));
    }
    
    /**
     * Obté una enquesta per ID.
     * @param id ID de l'enquesta
     * @return L'enquesta o null si no existeix
     */
    public Enquesta getEnquesta(String id) {
        return enquestes.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Obté totes les enquestes.
     * @return ArrayList d'enquestes
     */
    public ArrayList<Enquesta> getAllEnquestes() {
        return new ArrayList<>(enquestes);
    }
    
    /**
     * Obté el nombre d'enquestes.
     * @return Nombre d'enquestes
     */
    public int getNumEnquestes() {
        return enquestes.size();
    }

    // ===========================================
    // RESPOSTES - Mètodes d'accés i modificació
    // ===========================================
    
    /**
     * Guarda/actualitza totes les respostes (substitueix totes).
     * Les respostes també es guarden a les Preguntes corresponents.
     * @param respostes HashMap de respostes (id únic -> Resposta)
     */
    public void saveRespostes(HashMap<String, Resposta> respostes) {
        this.respostes = new HashMap<>(respostes);
        System.out.println("✓ Guardades " + respostes.size() + " respostes a CtrlPersistencia");
    }

    /**
     * Carrega totes les respostes.
     * @return HashMap de respostes (id únic -> Resposta)
     */
    public HashMap<String, Resposta> loadRespostes() {
        System.out.println("ℹ Carregant " + respostes.size() + " respostes des de CtrlPersistencia");
        return new HashMap<>(respostes);
    }
    
    /**
     * Afegeix una resposta. També l'afegeix a la Pregunta corresponent.
     * @param idResposta ID únic de la resposta (p.ex. "idPregunta_username")
     * @param resposta La resposta
     * @param pregunta La pregunta a la qual pertany la resposta
     */
    public void afegirResposta(String idResposta, Resposta resposta, Pregunta pregunta) {
        respostes.put(idResposta, resposta);
        // També afegir a la pregunta (associació bidireccional)
        pregunta.afegirResposta(resposta.getUsernameUsuari(), resposta);
        // També afegir a l'usuari (associació bidireccional)
        Usuari usuari = getUsuari(resposta.getUsernameUsuari());
        if (usuari != null) {
            usuari.afegirResposta(idResposta, resposta);
        }
    }
    
    /**
     * Elimina una resposta específica.
     * @param idResposta ID de la resposta
     * @return La resposta eliminada o null si no existeix
     */
    public Resposta eliminarResposta(String idResposta) {
        Resposta resposta = respostes.remove(idResposta);
        if (resposta != null) {
            // També eliminar de l'usuari (associació bidireccional)
            Usuari usuari = getUsuari(resposta.getUsernameUsuari());
            if (usuari != null) {
                usuari.eliminarResposta(idResposta);
            }
        }
        return resposta;
    }
    
    /**
     * Obté una resposta per ID.
     * @param idResposta ID de la resposta
     * @return La resposta o null si no existeix
     */
    public Resposta getResposta(String idResposta) {
        return respostes.get(idResposta);
    }
    
    /**
     * Obté totes les respostes.
     * @return HashMap amb totes les respostes
     */
    public HashMap<String, Resposta> getAllRespostes() {
        return new HashMap<>(respostes);
    }

    // ===========================================
    // USUARIS - Mètodes d'accés i modificació
    // ===========================================
    
    /**
     * Guarda/actualitza tots els usuaris (substitueix tots).
     * @param usuaris HashMap d'usuaris
     */
    public void saveUsuaris(HashMap<String, Usuari> usuaris) {
        this.usuaris = new HashMap<>(usuaris);
        System.out.println("✓ Guardats " + usuaris.size() + " usuaris a CtrlPersistencia");
    }

    /**
     * Carrega tots els usuaris.
     * @return HashMap d'usuaris
     */
    public HashMap<String, Usuari> loadUsuaris() {
        System.out.println("ℹ Carregant " + usuaris.size() + " usuaris des de CtrlPersistencia");
        return new HashMap<>(usuaris);
    }
    
    /**
     * Afegeix un usuari.
     * @param username Username de l'usuari
     * @param usuari L'usuari
     */
    public void afegirUsuari(String username, Usuari usuari) {
        usuaris.put(username, usuari);
    }
    
    /**
     * Elimina un usuari.
     * @param username Username de l'usuari
     * @return L'usuari eliminat o null si no existeix
     */
    public Usuari eliminarUsuari(String username) {
        return usuaris.remove(username);
    }
    
    /**
     * Obté un usuari per username.
     * @param username Username de l'usuari
     * @return L'usuari o null si no existeix
     */
    public Usuari getUsuari(String username) {
        return usuaris.get(username);
    }
    
    /**
     * Verifica si un usuari existeix.
     * @param username Username de l'usuari
     * @return true si existeix, false si no
     */
    public boolean existeixUsuari(String username) {
        return usuaris.containsKey(username);
    }
    
    /**
     * Obté tots els usuaris.
     * @return HashMap d'usuaris
     */
    public HashMap<String, Usuari> getAllUsuaris() {
        return new HashMap<>(usuaris);
    }
    
    /**
     * Obté el nombre d'usuaris.
     * @return Nombre d'usuaris
     */
    public int getNumUsuaris() {
        return usuaris.size();
    }

    // ===========================================
    // PERFILS - Mètodes d'accés i modificació
    // ===========================================
    
    /**
     * Guarda/actualitza tots els perfils (substitueix tots).
     * @param perfils HashMap de perfils
     */
    public void savePerfils(HashMap<String, Perfil> perfils) {
        this.perfils = new HashMap<>(perfils);
        System.out.println("✓ Guardats " + perfils.size() + " perfils a CtrlPersistencia");
    }

    /**
     * Carrega tots els perfils.
     * @return HashMap de perfils
     */
    public HashMap<String, Perfil> loadPerfils() {
        System.out.println("ℹ Carregant " + perfils.size() + " perfils des de CtrlPersistencia");
        return new HashMap<>(perfils);
    }
    
    /**
     * Afegeix un perfil.
     * @param id ID del perfil
     * @param perfil El perfil
     */
    public void afegirPerfil(String id, Perfil perfil) {
        perfils.put(id, perfil);
    }
    
    /**
     * Elimina un perfil.
     * @param id ID del perfil
     * @return El perfil eliminat o null si no existeix
     */
    public Perfil eliminarPerfil(String id) {
        return perfils.remove(id);
    }
    
    /**
     * Obté un perfil per ID.
     * @param id ID del perfil
     * @return El perfil o null si no existeix
     */
    public Perfil getPerfil(String id) {
        return perfils.get(id);
    }
    
    /**
     * Obté tots els perfils.
     * @return HashMap de perfils
     */
    public HashMap<String, Perfil> getAllPerfils() {
        return new HashMap<>(perfils);
    }

    // ===========================================
    // PREGUNTES - Mètodes d'accés i modificació
    // ===========================================
    
    /**
     * Afegeix una pregunta al sistema.
     * @param id ID de la pregunta
     * @param pregunta La pregunta
     */
    public void afegirPregunta(String id, Pregunta pregunta) {
        preguntes.put(id, pregunta);
    }
    
    /**
     * Elimina una pregunta del sistema.
     * @param id ID de la pregunta
     * @return La pregunta eliminada o null si no existeix
     */
    public Pregunta eliminarPregunta(String id) {
        return preguntes.remove(id);
    }
    
    /**
     * Obté una pregunta per ID.
     * @param id ID de la pregunta
     * @return La pregunta o null si no existeix
     */
    public Pregunta getPregunta(String id) {
        return preguntes.get(id);
    }
    
    /**
     * Obté totes les preguntes del sistema.
     * @return HashMap de preguntes
     */
    public HashMap<String, Pregunta> getAllPreguntes() {
        return new HashMap<>(preguntes);
    }
}

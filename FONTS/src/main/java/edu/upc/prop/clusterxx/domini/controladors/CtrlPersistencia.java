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

    /**
     * Constructor privat per implementar el patró Singleton.
     * Inicialitza tots els repositoris de dades en memòria.
     */
    private CtrlPersistencia() {
        this.enquestes = new ArrayList<>();
        this.respostes = new HashMap<>();
        this.usuaris = new HashMap<>();
        this.perfils = new HashMap<>();
        this.preguntes = new HashMap<>();
    }

    /**
     * Obté la instància única de CtrlPersistencia.
     * Crea la instància si encara no existeix.
     * 
     * @return La instància única de CtrlPersistencia
     */
    public static CtrlPersistencia getInstance() {
        if (instance == null) instance = new CtrlPersistencia();
        return instance;
    }

    // ===========================================
    // ENQUESTES - Mètodes d'accés i modificació
    // ===========================================
    
    /**
     * Afegeix una nova enquesta al repositori del sistema.
     * 
     * @param enquesta L'enquesta a afegir
     */
    public void afegirEnquesta(Enquesta enquesta) {
        enquestes.add(enquesta);
    }
    
    /**
     * Elimina una enquesta del sistema pel seu identificador.
     * 
     * @param id Identificador de l'enquesta a eliminar
     * @return true si l'enquesta s'ha eliminat, false si no existeix
     */
    public boolean eliminarEnquesta(String id) {
        return enquestes.removeIf(e -> e.getId().equals(id));
    }
    
    /**
     * Obté una enquesta específica pel seu identificador.
     * 
     * @param id Identificador de l'enquesta
     * @return L'enquesta trobada o null si no existeix
     */
    public Enquesta getEnquesta(String id) {
        return enquestes.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Obté una còpia de totes les enquestes del sistema.
     * 
     * @return ArrayList amb totes les enquestes registrades
     */
    public ArrayList<Enquesta> getAllEnquestes() {
        return new ArrayList<>(enquestes);
    }
    
    /**
     * Obté el nombre total d'enquestes registrades.
     * 
     * @return Nombre d'enquestes al sistema
     */
    public int getNumEnquestes() {
        return enquestes.size();
    }

    // ===========================================
    // RESPOSTES - Mètodes d'accés i modificació
    // ===========================================

    
    /**
     * Afegeix una resposta al sistema mantenint la consistència bidireccional.
     * La resposta s'afegeix al repositori global, a la pregunta i a l'usuari.
     * 
     * @param idResposta Identificador únic de la resposta (format: "idPregunta_username")
     * @param resposta La resposta a afegir
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
     * Elimina una resposta del sistema mantenint la consistència.
     * La resposta s'elimina del repositori global i de l'usuari.
     * 
     * @param idResposta Identificador de la resposta a eliminar
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
     * Obté una resposta específica pel seu identificador.
     * 
     * @param idResposta Identificador de la resposta
     * @return La resposta trobada o null si no existeix
     */
    public Resposta getResposta(String idResposta) {
        return respostes.get(idResposta);
    }
    
    /**
     * Obté una còpia de totes les respostes del sistema.
     * 
     * @return HashMap amb totes les respostes (clau: idResposta, valor: Resposta)
     */
    public HashMap<String, Resposta> getAllRespostes() {
        return new HashMap<>(respostes);
    }

    // ===========================================
    // USUARIS - Mètodes d'accés i modificació
    // ===========================================
    
    /**
     * Guarda o actualitza tots els usuaris del sistema, substituint els existents.
     * 
     * @param usuaris HashMap amb tots els usuaris (clau: username, valor: Usuari)
     */
    public void saveUsuaris(HashMap<String, Usuari> usuaris) {
        this.usuaris = new HashMap<>(usuaris);
        System.out.println("✓ Guardats " + usuaris.size() + " usuaris a CtrlPersistencia");
    }
    
    /**
     * Afegeix o actualitza un usuari al sistema.
     * 
     * @param username Nom d'usuari (clau única)
     * @param usuari L'usuari a afegir
     */
    public void afegirUsuari(String username, Usuari usuari) {
        usuaris.put(username, usuari);
    }
    
    /**
     * Elimina un usuari del sistema pel seu nom d'usuari.
     * 
     * @param username Nom d'usuari a eliminar
     * @return L'usuari eliminat o null si no existeix
     */
    public Usuari eliminarUsuari(String username) {
        return usuaris.remove(username);
    }
    
    /**
     * Obté un usuari específic pel seu nom d'usuari.
     * 
     * @param username Nom d'usuari a buscar
     * @return L'usuari trobat o null si no existeix
     */
    public Usuari getUsuari(String username) {
        return usuaris.get(username);
    }
    
    /**
     * Verifica si existeix un usuari amb el nom especificat.
     * 
     * @param username Nom d'usuari a verificar
     * @return true si l'usuari existeix, false altrament
     */
    public boolean existeixUsuari(String username) {
        return usuaris.containsKey(username);
    }
    
    /**
     * Obté una còpia de tots els usuaris del sistema.
     * 
     * @return HashMap amb tots els usuaris (clau: username, valor: Usuari)
     */
    public HashMap<String, Usuari> getAllUsuaris() {
        return new HashMap<>(usuaris);
    }
    
    /**
     * Obté el nombre total d'usuaris registrats al sistema.
     * 
     * @return Nombre d'usuaris
     */
    public int getNumUsuaris() {
        return usuaris.size();
    }

    // ===========================================
    // PERFILS - Mètodes d'accés i modificació
    // ===========================================
    
    /**
     * Guarda o actualitza tots els perfils del sistema, substituint els existents.
     * 
     * @param perfils HashMap amb tots els perfils (clau: id, valor: Perfil)
     */
    public void savePerfils(HashMap<String, Perfil> perfils) {
        this.perfils = new HashMap<>(perfils);
        System.out.println("✓ Guardats " + perfils.size() + " perfils a CtrlPersistencia");
    }
    
    /**
     * Afegeix o actualitza un perfil al sistema.
     * 
     * @param id Identificador únic del perfil
     * @param perfil El perfil a afegir
     */
    public void afegirPerfil(String id, Perfil perfil) {
        perfils.put(id, perfil);
    }
    
    /**
     * Elimina un perfil del sistema pel seu identificador.
     * 
     * @param id Identificador del perfil a eliminar
     * @return El perfil eliminat o null si no existeix
     */
    public Perfil eliminarPerfil(String id) {
        return perfils.remove(id);
    }
    
    /**
     * Obté un perfil específic pel seu identificador.
     * 
     * @param id Identificador del perfil
     * @return El perfil trobat o null si no existeix
     */
    public Perfil getPerfil(String id) {
        return perfils.get(id);
    }
    
    /**
     * Obté una còpia de tots els perfils del sistema.
     * 
     * @return HashMap amb tots els perfils (clau: id, valor: Perfil)
     */
    public HashMap<String, Perfil> getAllPerfils() {
        return new HashMap<>(perfils);
    }

    // ===========================================
    // PREGUNTES - Mètodes d'accés i modificació
    // ===========================================
    
    /**
     * Afegeix o actualitza una pregunta al repositori global del sistema.
     * 
     * @param id Identificador únic de la pregunta
     * @param pregunta La pregunta a afegir
     */
    public void afegirPregunta(String id, Pregunta pregunta) {
        preguntes.put(id, pregunta);
    }
    
    /**
     * Elimina una pregunta del repositori global del sistema.
     * 
     * @param id Identificador de la pregunta a eliminar
     * @return La pregunta eliminada o null si no existeix
     */
    public Pregunta eliminarPregunta(String id) {
        return preguntes.remove(id);
    }
    
    /**
     * Obté una pregunta específica pel seu identificador.
     * 
     * @param id Identificador de la pregunta
     * @return La pregunta trobada o null si no existeix
     */
    public Pregunta getPregunta(String id) {
        return preguntes.get(id);
    }
}

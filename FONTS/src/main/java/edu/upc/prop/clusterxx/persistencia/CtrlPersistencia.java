package edu.upc.prop.clusterxx.persistencia;

import edu.upc.prop.clusterxx.domini.classes.Enquesta;
import edu.upc.prop.clusterxx.domini.classes.Perfil;
import edu.upc.prop.clusterxx.domini.classes.Pregunta;
import edu.upc.prop.clusterxx.domini.classes.Resposta;
import edu.upc.prop.clusterxx.domini.classes.Usuari;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Controlador de persistència que gestiona l'emmagatzematge de dades.
 * Utilitza el patró Singleton i delega en gestors específics per a fitxers.
 * Manté una còpia en memòria per a accés ràpid i guarda a disc en cada
 * modificació.
 */
public class CtrlPersistencia {
    private static CtrlPersistencia instance;

    // Gestors de persistència
    private GestorEnquestes gestorEnquestes;
    private GestorUsuaris gestorUsuaris;
    private GestorPerfils gestorPerfils;

    // Dades principals en memòria
    private HashMap<String, Enquesta> enquestes;
    private HashMap<String, Usuari> usuaris;
    private HashMap<String, Perfil> perfils;

    // Cache per accés ràpid a preguntes i respostes
    private HashMap<String, Pregunta> preguntes;
    private HashMap<String, Resposta> respostes;

    private CtrlPersistencia() {
        this.gestorEnquestes = new GestorEnquestes();
        this.gestorUsuaris = new GestorUsuaris();
        this.gestorPerfils = new GestorPerfils();

        this.enquestes = new HashMap<>();
        this.usuaris = new HashMap<>();
        this.perfils = new HashMap<>();
        this.preguntes = new HashMap<>();
        this.respostes = new HashMap<>();

        carregarDades();
    }

    // Patró Singleton
    public static CtrlPersistencia getInstance() {
        if (instance == null)
            instance = new CtrlPersistencia();
        return instance;
    }

    /**
     * Carrega totes les dades dels fitxers a memòria.
     */
    private void carregarDades() {
        try {
            // Carregar usuaris
            usuaris = gestorUsuaris.carregarUsuaris();

            // Carregar perfils
            perfils = gestorPerfils.carregarPerfils();

            // Carregar enquestes (ja carrega preguntes internament)
            enquestes = gestorEnquestes.carregarEnquestes(usuaris);

            // Inicialitzar cache de preguntes i respostes a partir de les enquestes carregades
            inicialitzarCache();
        } catch (IOException e) {
            System.err.println("Error carregant dades: " + e.getMessage());
        }
    }

    /**
     * Inicialitza la cache de preguntes i respostes a partir de les enquestes.
     */
    private void inicialitzarCache() {
        this.preguntes.clear();
        this.respostes.clear();

        for (Enquesta enquesta : enquestes.values()) {
            for (Pregunta pregunta : enquesta.getPreguntes()) {
                this.preguntes.put(pregunta.getId(), pregunta);
                for (Resposta resposta : pregunta.getRespostes().values()) {
                    this.respostes.put(resposta.getId(), resposta);
                }
            }
        }
    }

    /**
     * Guarda totes les dades a disc.
     */
    private void guardarDades() {
        try {
            gestorUsuaris.guardarUsuaris(usuaris);
            gestorPerfils.guardarPerfils(perfils);
            gestorEnquestes.guardarEnquestes(enquestes);
        } catch (IOException e) {
            System.err.println("Error guardant dades: " + e.getMessage());
        }
    }

    /**
     * Força el guardado de totes les dades (mètode públic per ús extern).
     * Utilitzar només quan sigui necessari guardar tot el sistema.
     */
    public void flush() {
        guardarDades();
    }

    /**
     * Guarda només les enquestes a disc (més eficient que guardarDades()).
     */
    private void guardarEnquestes() {
        try {
            gestorEnquestes.guardarEnquestes(enquestes);
        } catch (IOException e) {
            System.err.println("Error guardant enquestes: " + e.getMessage());
        }
    }

    /**
     * Guarda només els usuaris a disc (més eficient que guardarDades()).
     */
    private void guardarUsuaris() {
        try {
            gestorUsuaris.guardarUsuaris(usuaris);
        } catch (IOException e) {
            System.err.println("Error guardant usuaris: " + e.getMessage());
        }
    }

    /**
     * Guarda només els perfils a disc (més eficient que guardarDades()).
     */
    private void guardarPerfils() {
        try {
            gestorPerfils.guardarPerfils(perfils);
        } catch (IOException e) {
            System.err.println("Error guardant perfils: " + e.getMessage());
        }
    }

    // ===========================================
    // ENQUESTES
    // ===========================================

    /**
     * Afegeix una nova enquesta al sistema.
     * 
     * @param enquesta L'enquesta a afegir
     */
    public void afegirEnquesta(Enquesta enquesta) {
        enquestes.put(enquesta.getId(), enquesta);
        // Actualitzar índexs globals de preguntes
        for (Pregunta p : enquesta.getPreguntes()) {
            preguntes.put(p.getId(), p);
        }
        guardarEnquestes();
    }

    /**
     * Guarda una enquesta específica (actualitza la cache i persisteix).
     * 
     * @param enquesta L'enquesta a guardar
     */
    public void guardarEnquesta(Enquesta enquesta) {
        enquestes.put(enquesta.getId(), enquesta);
        // Actualitzar cache de preguntes i respostes
        for (Pregunta p : enquesta.getPreguntes()) {
            preguntes.put(p.getId(), p);
            for (Resposta r : p.getRespostes().values()) {
                respostes.put(r.getId(), r);
            }
        }
        guardarEnquestes();
    }

    /**
     * Elimina una enquesta del sistema.
     * 
     * @param id L'ID de l'enquesta a eliminar
     * @return true si s'ha eliminat, false si no existia
     */
    public boolean eliminarEnquesta(String id) {
        Enquesta removed = enquestes.remove(id);
        if (removed != null) {
            // Eliminar preguntes i respostes específiques de l'enquesta eliminada
            for (Pregunta p : removed.getPreguntes()) {
                preguntes.remove(p.getId());
                for (String idResposta : p.getRespostes().keySet()) {
                    respostes.remove(idResposta);
                }
            }
            gestorEnquestes.eliminarFitxerEnquesta(id);
            guardarEnquestes();
            return true;
        }
        return false;
    }

    /**
     * Obté una enquesta per ID.
     * 
     * @param id L'ID de l'enquesta
     * @return L'enquesta o null si no existeix
     */
    public Enquesta getEnquesta(String id) {
        return enquestes.get(id);
    }

    /**
     * Obté totes les enquestes.
     * 
     * @return ArrayList amb totes les enquestes
     */
    public ArrayList<Enquesta> getAllEnquestes() {
        return new ArrayList<>(enquestes.values());
    }

    /**
     * Obté el nombre total d'enquestes.
     */
    public int getNumEnquestes() {
        return enquestes.size();
    }

    // ===========================================
    // RESPOSTES
    // ===========================================

    /**
     * Afegeix una resposta al sistema.
     * 
     * @param resposta La resposta a afegir
     */
    public void afegirResposta(Resposta resposta) {
        respostes.put(resposta.getId(), resposta);

        // Afegir a la pregunta corresponent
        Pregunta pregunta = getPregunta(resposta.getIdPregunta());
        if (pregunta != null) {
            pregunta.afegirResposta(resposta.getUsernameUsuari(), resposta);
        }

        // Afegir a l'usuari
        Usuari usuari = getUsuari(resposta.getUsernameUsuari());
        if (usuari != null) {
            usuari.afegirResposta(resposta.getId(), resposta);
        }
        guardarEnquestes();
    }

    /**
     * Elimina una resposta del sistema.
     * 
     * @param idResposta L'ID de la resposta a eliminar
     * @return La resposta eliminada o null si no existia
     */
    public Resposta eliminarResposta(String idResposta) {
        Resposta resposta = respostes.remove(idResposta);
        if (resposta != null) {
            Usuari usuari = getUsuari(resposta.getUsernameUsuari());
            if (usuari != null) {
                usuari.eliminarResposta(idResposta);
            }
            Pregunta p = getPregunta(resposta.getIdPregunta());
            if (p != null) {
                p.eliminarResposta(resposta.getUsernameUsuari());
            }
            guardarEnquestes();
        }
        return resposta;
    }

    /**
     * Obté una resposta per ID.
     */
    public Resposta getResposta(String idResposta) {
        return respostes.get(idResposta);
    }

    /**
     * Obté totes les respostes.
     */
    public HashMap<String, Resposta> getAllRespostes() {
        return new HashMap<>(respostes);
    }

    // ===========================================
    // USUARIS
    // ===========================================

    /**
     * Guarda un conjunt d'usuaris substituint els existents.
     */
    public void saveUsuaris(HashMap<String, Usuari> usuaris) {
        this.usuaris = new HashMap<>(usuaris);
        guardarUsuaris();
    }

    /**
     * Afegeix un nou usuari.
     * 
     * @param usuari L'usuari a afegir
     */
    public void afegirUsuari(Usuari usuari) {
        usuaris.put(usuari.getUsername(), usuari);
        guardarUsuaris();
    }

    /**
     * Elimina un usuari.
     */
    public Usuari eliminarUsuari(String username) {
        Usuari u = usuaris.remove(username);
        if (u != null) {
            gestorUsuaris.eliminarFitxerUsuari(username);
            guardarUsuaris();
        }
        return u;
    }

    /**
     * Obté un usuari pel seu username.
     */
    public Usuari getUsuari(String username) {
        return usuaris.get(username);
    }

    /**
     * Comprova si un usuari existeix.
     */
    public boolean existeixUsuari(String username) {
        return usuaris.containsKey(username);
    }

    /**
     * Obté tots els usuaris.
     */
    public HashMap<String, Usuari> getAllUsuaris() {
        return new HashMap<>(usuaris);
    }

    /**
     * Obté el nombre total d'usuaris.
     */
    public int getNumUsuaris() {
        return usuaris.size();
    }

    // ===========================================
    // PERFILS
    // ===========================================

    /**
     * Guarda un conjunt de perfils substituint els existents.
     */
    public void savePerfils(HashMap<String, Perfil> perfils) {
        this.perfils = new HashMap<>(perfils);
        guardarPerfils();
    }

    /**
     * Afegeix un nou perfil.
     * 
     * @param perfil El perfil a afegir
     */
    public void afegirPerfil(Perfil perfil) {
        perfils.put(String.valueOf(perfil.getId()), perfil);
        guardarPerfils();
    }

    /**
     * Elimina un perfil.
     */
    public Perfil eliminarPerfil(String id) {
        Perfil p = perfils.remove(id);
        if (p != null) {
            gestorPerfils.eliminarFitxerPerfil(id);
            guardarPerfils();
        }
        return p;
    }

    /**
     * Obté un perfil pel seu ID.
     */
    public Perfil getPerfil(String id) {
        return perfils.get(id);
    }

    /**
     * Obté tots els perfils.
     */
    public HashMap<String, Perfil> getAllPerfils() {
        return new HashMap<>(perfils);
    }

    // ===========================================
    // PREGUNTES
    // ===========================================

    /**
     * Afegeix una pregunta a la cache global.
     * Nota: La pregunta ha d'estar prèviament vinculada a una enquesta.
     */
    public void afegirPregunta(String id, Pregunta pregunta) {
        preguntes.put(id, pregunta);
        guardarEnquestes();
    }

    /**
     * Elimina una pregunta de la cache global.
     */
    public Pregunta eliminarPregunta(String id) {
        Pregunta p = preguntes.remove(id);
        if (p != null)
            guardarEnquestes();
        return p;
    }

    /**
     * Obté una pregunta pel seu ID.
     */
    public Pregunta getPregunta(String id) {
        return preguntes.get(id);
    }
}

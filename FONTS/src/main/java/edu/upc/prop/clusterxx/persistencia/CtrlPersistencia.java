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
 * Manté una còpia en memòria per a accés ràpid i guarda a disc en cada modificació.
 */
public class CtrlPersistencia {
    private static CtrlPersistencia instance;

    private GestorEnquestes gestorEnquestes;
    private GestorUsuaris gestorUsuaris;
    private GestorPerfils gestorPerfils;

    // Dades que es guarden a disc
    private HashMap<String, Enquesta> enquestes; // Fitxer per cada enquesta + índex general d'enquestes
    private HashMap<String, Usuari> usuaris;     // Fitxer per tots els usuaris
    private HashMap<String, Perfil> perfils;     // Fitxer per tots els perfils
    
    // Índexs per accés ràpid s'obtenen des de les enquestes
    private HashMap<String, Pregunta> preguntes;
    private HashMap<String, Resposta> respostes;

    private CtrlPersistencia() {
        this.gestorEnquestes = new GestorEnquestes();
        this.gestorUsuaris = new GestorUsuaris();
        this.gestorPerfils = new GestorPerfils();

        this.enquestes = new HashMap<>();
        this.respostes = new HashMap<>();
        this.usuaris = new HashMap<>();
        this.perfils = new HashMap<>();
        this.preguntes = new HashMap<>();

        carregarDades();
    }

    // Patró Singleton
    public static CtrlPersistencia getInstance() {
        if (instance == null) {
            instance = new CtrlPersistencia();
        }
        return instance;
    }

    /**
     * Carrega totes les dades dels fitxers a memòria.
     * @exception IOException Si hi ha error de lectura
     */
    private void carregarDades() {
        try {
            // Carregar usuaris
            usuaris = gestorUsuaris.carregarUsuaris();

            // Carregar perfils
            perfils = gestorPerfils.carregarPerfils();

            // Carregar enquestes i vincular amb usuaris
            enquestes = gestorEnquestes.carregarEnquestes(usuaris);

            // Inicialitzar cache (preguntes i respostes)
            inicialitzarCache();
        } catch (IOException e) {
            System.err.println("Error carregant dades: " + e.getMessage());
        }
    }

    /**
     * Inicialitza la cache de preguntes i respostes.
     */
    private void inicialitzarCache() {
        // Buidar la cache
        this.preguntes.clear();
        this.respostes.clear();

        // Recarregar la cache, iterant per totes les enquests per a obtenir les preguntes i respostes
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
     * Guarda les dades d'usuaris, perfils i enquestes al disc.
     * @exception IOException Si hi ha error d'escriptura
     */
    public void guardarDades() {
        try {
            gestorUsuaris.guardarUsuaris(usuaris);
            gestorPerfils.guardarPerfils(perfils);
            gestorEnquestes.guardarEnquestes(enquestes);
        } catch (IOException e) {
            System.err.println("Error guardant dades: " + e.getMessage());
        }
    }

    // ===========================================
    //                ENQUESTES
    // ===========================================
    
    /**
     * Afegeix una nova enquesta i actualitza la cache de preguntes i respostes.
     * @param enquesta L'enquesta a afegir
     */
    public void afegirEnquesta(Enquesta enquesta) {
        enquestes.put(enquesta.getId(), enquesta);

        // Actualitzar índexs de preguntes i respostes per accés ràpid
        for (Pregunta p : enquesta.getPreguntes()) {
            preguntes.put(p.getId(), p);
            for (Resposta r : p.getRespostes().values()) {
                respostes.put(r.getId(), r);
            }
        }
    
        guardarEnquesta(enquesta);
    }

    /**
     * Elimina una enquesta i les preguntes i respostes associades.
     * @param id L'ID de l'enquesta a eliminar
     * @return true si s'ha eliminat correctament, false si no existia l'enquesta
     * @exception IOException Si hi ha error d'escriptura 
     */
    public boolean eliminarEnquesta(String id) {
        Enquesta removed = enquestes.remove(id);
        if (removed != null) {
            // Eliminar el fitxer individual de l'enquesta
            gestorEnquestes.eliminarFitxerEnquesta(id);

            // Esborrar preguntes i respostes d'aquesta enquesta de la cache
            inicialitzarCache();

            // Actualitzar l'índex general, fitxer que guarda resum dees les enquestes
            try {
                gestorEnquestes.guardarEnquestes(enquestes);
            } catch (IOException e) {
                System.err.println("Error actualitzant índex: " + e.getMessage());
            }
        }
        return removed != null;
    }

    /**
     * Guarda una enquesta específica a disc.
     * @param enquesta L'enquesta a guardar
     * @exception IOException Si hi ha error d'escriptura
    */
    public void guardarEnquesta(Enquesta enquesta) {
        try {
            // Actualitzar la cache de preguntes amb les preguntes de l'enquesta
            for (Pregunta p : enquesta.getPreguntes()) {
                preguntes.put(p.getId(), p);
            }
            
            gestorEnquestes.guardarEnquesta(enquesta);   // Guarda l'enquesta individual amb tot el contingut de preguntes, opcions i respostes
            gestorEnquestes.guardarEnquestes(enquestes); // Actualitza l'índex general d'enquestes
        } catch (IOException e) {
            System.err.println("Error guardant enquesta: " + e.getMessage());
        }
    }

    /**
     * Getter per una enquesta específica per ID.
     * @param id L'ID de l'enquesta
     * @return L'enquesta corresponent.
     */
    public Enquesta getEnquesta(String id) {
        return enquestes.get(id);
    }

    /**
     * Getter per totes les enquestes.
     */
    public ArrayList<Enquesta> getAllEnquestes() {
        return new ArrayList<>(enquestes.values());
    }

    /**
     * Getter per el nombre total d'enquestes.
     */
    public int getNumEnquestes() {
        return enquestes.size();
    }

    /**
     * Troba l'enquesta a la qual pertany una pregunta.
     * 
     * @param idPregunta L'ID de la pregunta
     * @return L'enquesta que conté la pregunta, o null si no es troba
     */
    private Enquesta getEnquestaDePregunta(String idPregunta) {
        for (Enquesta enquesta : enquestes.values()) {
            if (enquesta.getPregunta(idPregunta) != null) {
                return enquesta;
            }
        }
        return null;
    }

    // ===========================================
    //                 RESPOSTES
    // ===========================================

    /**
     * Afegeix una nova resposta i actualitza la pregunta i usuari corresponents.
     * @param resposta La resposta a afegir
     * @exception IOException Si hi ha error d'escriptura
     */
    public void afegirResposta(Resposta resposta) {
        // Obtenir la pregunta a partir de l'ID de la resposta
        Pregunta pregunta = getPregunta(resposta.getIdPregunta());
        if (pregunta == null) {
            System.err.println("Error: No existeix la pregunta " + resposta.getIdPregunta());
            return;
        }

        // Afegir a l'índex de respostes
        respostes.put(resposta.getId(), resposta);
        
        // Afegir a la pregunta
        pregunta.afegirResposta(resposta.getUsernameUsuari(), resposta);

        // Afegir a l'usuari
        Usuari usuari = getUsuari(resposta.getUsernameUsuari());
        if (usuari != null) {
            usuari.afegirResposta(resposta.getId(), resposta);
        }
        
        // Guardar només l'enquesta afectada i l'usuari
        Enquesta enquesta = getEnquestaDePregunta(pregunta.getId());
        if (enquesta != null) {
            guardarEnquesta(enquesta);
        }
        try {
            gestorUsuaris.guardarUsuaris(usuaris);
        } catch (IOException e) {
            System.err.println("Error guardant usuaris: " + e.getMessage());
        }
    }

    /**
     * Elimina una resposta específica.
     * @param idResposta L'ID de la resposta a eliminar
     * @return La resposta eliminada 
     * @exception IOException Si hi ha error d'escriptura
     */
    public Resposta eliminarResposta(String idResposta) {
        Resposta resposta = respostes.remove(idResposta);
        if (resposta != null) {
            Usuari usuari = getUsuari(resposta.getUsernameUsuari());
            if (usuari != null) {
                usuari.eliminarResposta(idResposta);
            }
            // Eliminar de la pregunta
            Pregunta p = getPregunta(resposta.getIdPregunta());
            if (p != null) {
                p.eliminarResposta(resposta.getUsernameUsuari());
                // Guardar només l'enquesta afectada
                Enquesta enquesta = getEnquestaDePregunta(p.getId());
                if (enquesta != null) {
                    guardarEnquesta(enquesta);
                }
            }
            try {
                gestorUsuaris.guardarUsuaris(usuaris);
            } catch (IOException e) {
                System.err.println("Error guardant usuaris: " + e.getMessage());
            }
        }
        return resposta;
    }

    /**
     * Getter per una resposta específica per ID.
     * @param idResposta L'ID de la resposta
     * @return La resposta corresponent.
     */
    public Resposta getResposta(String idResposta) {
        return respostes.get(idResposta);
    }

    /**
     * Getter per totes les respostes.
     */
    public HashMap<String, Resposta> getAllRespostes() {
        return new HashMap<>(respostes);
    }

    // ===========================================
    //                 USUARIS
    // ===========================================

    /**
     * Guarda tots els usuaris al fitxer JSON.
     * 
     * @param usuaris Mapa d'usuaris a guardar (username -> Usuari)
     * @throws IOException Si hi ha error d'escriptura
     */
    public void saveUsuaris(HashMap<String, Usuari> usuaris) {
        this.usuaris = new HashMap<>(usuaris);
        guardarDades();
    }

    /**
     * Afegeix un nou usuari.
     * @param usuari Objecte Usuari a afegir
     * @exception IOException Si hi ha error d'escriptura
     */
    public void afegirUsuari(Usuari usuari) {
        usuaris.put(usuari.getUsername(), usuari);
        guardarDades();
    }

    /**
     * Elimina un usuari.
     * @param username Nom d'usuari a eliminar
     * @return L'usuari eliminat
     */
    public Usuari eliminarUsuari(String username) {
        Usuari u = usuaris.remove(username);
        if (u != null)
            guardarDades();
        return u;
    }
    /**
     * Getter per un usuari específic pel seu nom d'usuari.
     * @param username Nom d'usuari a buscar
     * @return L'usuari corresponent.
     */
    public Usuari getUsuari(String username) {
        return usuaris.get(username);
    }

    /**
     * Comprova si un usuari existeix.
     * @param username Nom d'usuari a verificar
     * @return true si l'usuari existeix, false si no existeix
     */
    public boolean existeixUsuari(String username) {
        return usuaris.containsKey(username);
    }

    /**
     * Getter per tots els usuaris.
     * @return Mapa amb tots els usuaris (username -> Usuari)
     */
    public HashMap<String, Usuari> getAllUsuaris() {
        return new HashMap<>(usuaris);
    }

    /**
     * Getter pel nombre total d'usuaris.
     * @return Nombre d'usuaris
     */
    public int getNumUsuaris() {
        return usuaris.size();
    }

    // ===========================================
    //                  PERFILS
    // ===========================================

    /**
     * Guarda tots els perfils al fitxer JSON.
     * 
     * @param perfils Mapa de perfils a guardar (id -> Perfil)
     * @throws IOException Si hi ha error d'escriptura
     */
    public void savePerfils(HashMap<String, Perfil> perfils) {
        this.perfils = new HashMap<>(perfils);
        guardarDades();
    }

    /**
     * Afegeix un nou perfil.
     * @param perfil Objecte Perfil a afegir
     */
    public void afegirPerfil(Perfil perfil) {
        perfils.put(String.valueOf(perfil.getId()), perfil);
        guardarDades();
    }

    /**
     * Elimina un perfil.
     * @param id ID del perfil a eliminar
     * @return El perfil eliminat
     */
    public Perfil eliminarPerfil(String id) {
        Perfil p = perfils.remove(id);
        if (p != null)
            guardarDades();
        return p;
    }

    /**
     * Getter per un perfil específic pel seu ID.
     * @param id ID del perfil a buscar
     * @return El perfil corresponent.
     */
    public Perfil getPerfil(String id) {
        return perfils.get(id);
    }

    /**
     * Getter per tots els perfils.
     * @return Mapa amb tots els perfils (id -> Perfil)
     */
    public HashMap<String, Perfil> getAllPerfils() {
        return new HashMap<>(perfils);
    }

    // ===========================================
    //                  PREGUNTES
    // ===========================================
    
    /**
     * Elimina una pregunta.
     * @param id ID de la pregunta a eliminar
     * @return La pregunta eliminada
     */
    public Pregunta eliminarPregunta(String id) {
        Pregunta p = preguntes.remove(id);
        if (p != null)
            guardarDades();
        return p;
    }

    /**
     * Getter per una pregunta específica pel seu ID.
     * @param id ID de la pregunta a buscar
     * @return La pregunta corresponent.
     */
    public Pregunta getPregunta(String id) {
        return preguntes.get(id);
    }
}

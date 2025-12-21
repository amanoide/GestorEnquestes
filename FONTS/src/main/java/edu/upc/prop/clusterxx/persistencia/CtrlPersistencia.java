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

            // Vincular perfils amb usuaris
            vincularPerfilsAmbUsuaris();

            // Inicialitzar cache de preguntes i respostes a partir de les enquestes
            // carregades
            inicialitzarCache();
        } catch (IOException e) {
            System.err.println("Error carregant dades: " + e.getMessage());
        }
    }

    /**
     * Vincula els perfils carregats amb els usuaris que els tenen assignats.
     * Busca per cada perfil quins usuaris pertanyen al cluster i els assigna el
     * perfil.
     */
    private void vincularPerfilsAmbUsuaris() {
        // Obtenir el mapa temporal d'IDs de perfils del gestor d'usuaris
        HashMap<String, HashMap<String, Long>> perfilsTemporals = gestorUsuaris.getPerfilsTemporals();

        // Vincular els usuaris amb els seus perfils específics
        for (Usuari usuari : usuaris.values()) {
            String username = usuari.getUsername();

            // Comprovar si aquest usuari té perfils assignats
            if (perfilsTemporals.containsKey(username)) {
                HashMap<String, Long> perfilsUsuari = perfilsTemporals.get(username);

                for (java.util.Map.Entry<String, Long> entry : perfilsUsuari.entrySet()) {
                    String idEnquesta = entry.getKey();
                    Long perfilId = entry.getValue();

                    // Buscar el perfil corresponent per ID (convertir Long a String)
                    Perfil perfil = perfils.get(String.valueOf(perfilId));
                    if (perfil != null) {
                        usuari.assignarPerfil(idEnquesta, perfil);
                    }
                }
            }
        }

        // Netejar el mapa temporal després de la vinculació
        gestorUsuaris.netejarPerfilsTemporals();
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

    /**
     * Guarda un usuari individual a disc.
     * 
     * @param usuari L'usuari a guardar
     */
    public void guardarUsuari(Usuari usuari) {
        try {
            gestorUsuaris.guardarUsuari(usuari);
        } catch (IOException e) {
            System.err.println("Error guardant usuari: " + e.getMessage());
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
            // 1. Eliminar tots els perfils/clusters associats a aquesta enquesta
            eliminarPerfilsEnquesta(id);

            // 2. Eliminar preguntes i respostes específiques de l'enquesta eliminada
            for (Pregunta p : removed.getPreguntes()) {
                preguntes.remove(p.getId());
                for (String idResposta : p.getRespostes().keySet()) {
                    respostes.remove(idResposta);
                }
            }

            // 3. Eliminar el fitxer de l'enquesta
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
     * 
     * @return El nombre d'enquestes al sistema
     */
    public int getNumEnquestes() {
        return enquestes.size();
    }

    // ===========================================
    // RESPOSTES
    // ===========================================

    /**
     * Afegeix una resposta al sistema vinculant-la explícitament a una enquesta.
     * 
     * @param resposta   La resposta a afegir
     * @param idEnquesta L'ID de l'enquesta a la qual pertany
     */
    public void afegirResposta(Resposta resposta, String idEnquesta) {
        respostes.put(resposta.getId(), resposta);

        Enquesta enquesta = enquestes.get(idEnquesta);
        if (enquesta != null) {
            // Buscar la pregunta DINS d'aquesta enquesta específica
            Pregunta pregunta = enquesta.getPregunta(resposta.getIdPregunta());

            if (pregunta != null) {
                pregunta.afegirResposta(resposta.getUsernameUsuari(), resposta);
                enquesta.registrarParticipacio(resposta.getUsernameUsuari());
            }
        }

        // Afegir a l'usuari
        Usuari usuari = getUsuari(resposta.getUsernameUsuari());
        if (usuari != null) {
            usuari.afegirResposta(resposta.getId(), resposta);

            // Actualitzar llista d'enquestes participades (via perfils)
            if (enquesta != null && !usuari.tePerfil(idEnquesta)) {
                usuari.assignarPerfil(idEnquesta, null);
                try {
                    gestorUsuaris.guardarUsuari(usuari);
                } catch (IOException e) {
                    System.err.println("Error guardant usuari: " + e.getMessage());
                }
            }
        }

        // Guardar l'enquesta específica
        if (enquesta != null) {
            guardarEnquesta(enquesta);
        }
    }

    /**
     * Actualitza una resposta existent i guarda els canvis.
     * 
     * @param resposta La resposta modificada
     */
    public void actualitzarResposta(Resposta resposta) {
        // Com que treballem amb referències, l'objecte ja està actualitzat en memòria.
        // Només cal persistir els canvis.
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

                // Actualitzar llista de participants de l'enquesta
                for (Enquesta e : enquestes.values()) {
                    if (e.getPregunta(p.getId()) != null) {
                        boolean hasOtherAnswers = false;
                        for (Pregunta q : e.getPreguntes()) {
                            if (q.teResposta(resposta.getUsernameUsuari())) {
                                hasOtherAnswers = true;
                                break;
                            }
                        }
                        if (!hasOtherAnswers) {
                            e.eliminarParticipacio(resposta.getUsernameUsuari());

                            // Actualitzar llista d'enquestes participades de l'usuari
                            if (usuari != null) {
                                usuari.eliminarPerfil(e.getId());
                                try {
                                    gestorUsuaris.guardarUsuari(usuari);
                                } catch (IOException ex) {
                                    System.err.println("Error guardant usuari: " + ex.getMessage());
                                }
                            }
                        }
                        break;
                    }
                }
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
     * 
     * @return HashMap amb totes les respostes indexades per ID
     */
    public HashMap<String, Resposta> getAllRespostes() {
        return new HashMap<>(respostes);
    }

    // ===========================================
    // USUARIS
    // ===========================================

    /**
     * Guarda un conjunt d'usuaris substituint els existents.
     * 
     * @param usuaris HashMap amb els usuaris a guardar
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
     * Elimina un usuari i totes les seves dades associades.
     * - Elimina totes les enquestes creades per l'usuari
     * - Elimina totes les respostes de l'usuari en altres enquestes
     * - Elimina l'usuari de les participacions en enquestes
     * - Elimina el fitxer de l'usuari i l'actualitza de l'índex
     * 
     * @param username El nom d'usuari a eliminar
     * @return L'usuari eliminat o null si no existia
     */
    public Usuari eliminarUsuari(String username) {
        Usuari u = usuaris.get(username);
        if (u == null) {
            return null;
        }

        // 1. Eliminar totes les enquestes creades per l'usuari
        ArrayList<String> enquestesAEliminar = new ArrayList<>();
        for (Enquesta e : enquestes.values()) {
            if (e.getIdCreador().equals(username)) {
                enquestesAEliminar.add(e.getId());
            }
        }
        for (String idEnquesta : enquestesAEliminar) {
            eliminarEnquesta(idEnquesta);
        }

        // 2. Eliminar totes les respostes de l'usuari en altres enquestes
        ArrayList<String> respostesAEliminar = new ArrayList<>();
        for (Resposta r : respostes.values()) {
            if (r.getUsernameUsuari().equals(username)) {
                respostesAEliminar.add(r.getId());
            }
        }
        for (String idResposta : respostesAEliminar) {
            // Eliminar resposta de la pregunta
            Resposta resposta = respostes.get(idResposta);
            if (resposta != null) {
                Pregunta p = getPregunta(resposta.getIdPregunta());
                if (p != null) {
                    p.eliminarResposta(username);
                }
                respostes.remove(idResposta);
            }
        }

        // 3. Eliminar l'usuari de les participacions en enquestes
        for (Enquesta e : enquestes.values()) {
            if (e.haRespostUsuari(username)) {
                e.eliminarParticipacio(username);
            }
        }

        // 4. Guardar canvis a les enquestes
        guardarEnquestes();

        // 5. Eliminar l'usuari del HashMap i del disc
        usuaris.remove(username);
        gestorUsuaris.eliminarFitxerUsuari(username);
        guardarUsuaris();

        return u;
    }

    /**
     * Obté un usuari pel seu username.
     * 
     * @param username El nom d'usuari a buscar
     * @return L'usuari o null si no existeix
     */
    public Usuari getUsuari(String username) {
        return usuaris.get(username);
    }

    /**
     * Comprova si un usuari existeix.
     * 
     * @param username El nom d'usuari a comprovar
     * @return true si l'usuari existeix, false altrament
     */
    public boolean existeixUsuari(String username) {
        return usuaris.containsKey(username);
    }

    /**
     * Obté tots els usuaris.
     * 
     * @return HashMap amb tots els usuaris indexats per username
     */
    public HashMap<String, Usuari> getAllUsuaris() {
        return new HashMap<>(usuaris);
    }

    /**
     * Obté el nombre total d'usuaris.
     * 
     * @return El nombre d'usuaris al sistema
     */
    public int getNumUsuaris() {
        return usuaris.size();
    }

    // ===========================================
    // PERFILS
    // ===========================================

    /**
     * Guarda un conjunt de perfils substituint els existents.
     * 
     * @param perfils HashMap amb els perfils a guardar
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
     * Afegeix un perfil sense guardar immediatament (per a operacions en batch).
     * 
     * @param perfil El perfil a afegir
     */
    public void afegirPerfilSenseGuardar(Perfil perfil) {
        perfils.put(String.valueOf(perfil.getId()), perfil);
    }

    /**
     * Elimina un perfil.
     * 
     * @param id L'ID del perfil a eliminar
     * @return El perfil eliminat o null si no existia
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
     * Elimina tots els perfils associats a una enquesta específica.
     * També elimina les assignacions d'aquests perfils dels usuaris.
     * 
     * @param idEnquesta L'ID de l'enquesta
     */
    public void eliminarPerfilsEnquesta(String idEnquesta) {
        // Obtenir tots els perfils que pertanyen a aquesta enquesta
        java.util.List<String> perfilsAEliminar = new java.util.ArrayList<>();

        for (java.util.Map.Entry<String, Perfil> entry : perfils.entrySet()) {
            Perfil perfil = entry.getValue();
            if (perfil.teClustering() && idEnquesta.equals(perfil.getIdEnquesta())) {
                perfilsAEliminar.add(entry.getKey());
            }
        }

        // Eliminar els perfils del mapa i dels fitxers
        for (String perfilId : perfilsAEliminar) {
            perfils.remove(perfilId);
            gestorPerfils.eliminarFitxerPerfil(perfilId);
        }

        // Eliminar les assignacions d'aquests perfils dels usuaris
        for (Usuari usuari : usuaris.values()) {
            if (usuari.getPerfils().containsKey(idEnquesta)) {
                usuari.eliminarPerfil(idEnquesta);
            }
        }

        // Guardar els canvis
        if (!perfilsAEliminar.isEmpty()) {
            guardarPerfils();
        }
    }

    /**
     * Obté un perfil pel seu ID.
     * 
     * @param id L'ID del perfil a buscar
     * @return El perfil o null si no existeix
     */
    public Perfil getPerfil(String id) {
        return perfils.get(id);
    }

    /**
     * Obté tots els perfils.
     * 
     * @return HashMap amb tots els perfils indexats per ID
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
     * 
     * @param id       L'ID de la pregunta
     * @param pregunta La pregunta a afegir
     */
    public void afegirPregunta(String id, Pregunta pregunta) {
        preguntes.put(id, pregunta);
        guardarEnquestes();
    }

    /**
     * Elimina una pregunta de la cache global.
     * 
     * @param idEnquesta L'ID de l'enquesta que conté la pregunta
     * @param idPregunta L'ID de la pregunta a eliminar
     * @return La pregunta eliminada o null si no existia
     */
    public Pregunta eliminarPregunta(String idEnquesta, String idPregunta) {
        Pregunta p = preguntes.remove(idPregunta);
        if (p != null) {
            // Eliminar el archivo físico de la pregunta
            GestorPreguntes gestorPreguntes = new GestorPreguntes();
            gestorPreguntes.eliminarPregunta(idEnquesta, idPregunta);
            // Actualizar el index.json
            guardarEnquestes();
        }
        return p;
    }

    /**
     * Obté una pregunta pel seu ID.
     * 
     * @param id L'ID de la pregunta a buscar
     * @return La pregunta o null si no existeix
     */
    public Pregunta getPregunta(String id) {
        return preguntes.get(id);
    }
}

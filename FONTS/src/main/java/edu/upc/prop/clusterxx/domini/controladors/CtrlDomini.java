package edu.upc.prop.clusterxx.domini.controladors;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.json.JSONObject;
import org.json.JSONArray;

import edu.upc.prop.clusterxx.domini.classes.Enquesta;
import edu.upc.prop.clusterxx.domini.classes.Opcio;
import edu.upc.prop.clusterxx.domini.classes.Perfil;
import edu.upc.prop.clusterxx.domini.classes.Pregunta;
import edu.upc.prop.clusterxx.domini.classes.Resposta;
import edu.upc.prop.clusterxx.domini.classes.TipusPregunta;
import edu.upc.prop.clusterxx.domini.classes.Usuari;
import static edu.upc.prop.clusterxx.domini.classes.Exceptions.*;


public class CtrlDomini {
    private CtrlEnquesta ctrlEnquesta;
    private CtrlPregunta ctrlPregunta;
    private CtrlResposta ctrlResposta;
    private CtrlUsuari ctrlUsuari;
    private CtrlPerfil ctrlPerfil;
    private CtrlPersistencia ctrlPersistencia; // (jairo) NOU

    public CtrlDomini() {
        this.ctrlEnquesta = new CtrlEnquesta();
        this.ctrlPregunta = new CtrlPregunta();
        this.ctrlResposta = new CtrlResposta();
        this.ctrlUsuari = new CtrlUsuari(null); // Ajustado para usar CtrlUsuari
        this.ctrlPerfil = new CtrlPerfil();
        this.ctrlPersistencia = CtrlPersistencia.getInstance(); // (jairo) NOU - Singleton
    }

    // --- Casos de Uso: Gestió d'Enquestes ---

    /**
     * Crea una nova enquesta associada a un usuari.
     * @param usuari L'usuari creador.
     * @param id L'ID de la nova enquesta.
     * @param titol El títol de l'enquesta.
     * @param descripcio La descripció de l'enquesta.
     * @throws ParametreInvalidException Si algun paràmetre és invàlid (null o buit).
     * @throws EnquestaJaExisteixException Si ja existeix una enquesta amb aquest ID.
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat.
     */

    // (jairo)
    public void crearEnquesta(Usuari usuari, String id, String titol, String descripcio) 
            throws ParametreInvalidException, EnquestaJaExisteixException, UsuariNoAutenticatException {
        
        // Validació 1: Comprovar que l'usuari existeix i no és null
        if (usuari == null) {
            throw new UsuariNoAutenticatException("Cal estar autenticat per crear una enquesta.");
        }
        
        // Validació 2: Comprovar que l'ID no està buit
        if (id == null || id.trim().isEmpty()) {
            throw new ParametreInvalidException("L'ID de l'enquesta no pot estar buit.");
        }
        
        // Validació 3: Comprovar que el títol no està buit
        if (titol == null || titol.trim().isEmpty()) {
            throw new ParametreInvalidException("El títol de l'enquesta no pot estar buit.");
        }
        
        // Validació 4: Comprovar que l'enquesta no existeix ja
        if (ctrlEnquesta.getEnquesta(id) != null) {
            throw new EnquestaJaExisteixException(id);
        }
        
        // Validació 5: Comprovar que l'usuari està registrat al sistema
        if (ctrlUsuari.getUsuariActual() == null) {
            throw new UsuariNoAutenticatException("Cal estar autenticat per crear una enquesta.");
        }
        
        // Si totes les validacions passen, crear l'enquesta
        ctrlEnquesta.crearEnquesta(id, titol, descripcio, usuari);
    }

    /**
     * Esborra una enquesta.
     * Elimina (segons la lògica del domini):
     * - Les respostes de cada pregunta (Pregunta -> Respostes)
     * - Les preguntes de l'enquesta de persistència
     * - L'enquesta de persistència
     * - L'enquesta de la llista del creador
     * @param id L'ID de l'enquesta a esborrar.
     * @throws EnquestaNoExisteixException Si l'enquesta no existeix
     * @throws PermisDenegatException Si l'usuari no és el creador
     * @throws UsuariNoAutenticatException Si no hi ha usuari autenticat
     */
    //(jairo)
    public void esborrarEnquesta(String id) throws EnquestaNoExisteixException, PermisDenegatException, UsuariNoAutenticatException {
        // Verificar que hi ha un usuari autenticat
        Usuari usuariActual = getUsuariActual();
        if (usuariActual == null) {
            throw new UsuariNoAutenticatException("Cal estar autenticat per esborrar una enquesta.");
        }
        
        // Verificar que l'enquesta existeix
        Enquesta enquesta = ctrlPersistencia.getEnquesta(id);
        if (enquesta == null) {
            throw new EnquestaNoExisteixException(id);
        }
        
        String idCreador = enquesta.getIdCreador();
        
        // Verificar permisos
        if (!idCreador.equals(usuariActual.getUsername())) {
            throw new PermisDenegatException("Només el creador de l'enquesta pot esborrar-la.");
        }
        
        // 1. Eliminar les respostes de CADA pregunta de l'enquesta
        //    Usant l'associació directa: cada Pregunta té les seves Respostes
        ArrayList<Pregunta> preguntes = enquesta.getPreguntes();
        for (Pregunta pregunta : preguntes) {
            // Obtenir totes les respostes d'aquesta pregunta
            HashMap<String, Resposta> respostesPregunta = pregunta.getRespostes();
            
            // Eliminar cada resposta de persistència
            for (Resposta resposta : respostesPregunta.values()) {
                ctrlPersistencia.eliminarResposta(resposta.getId());
            }
            
            // Eliminar la pregunta de persistència global
            ctrlPersistencia.eliminarPregunta(pregunta.getId());
        }
        
        // 2. Eliminar l'enquesta de la llista del creador
        Usuari creador = ctrlPersistencia.getUsuari(idCreador);
        if (creador != null) {
            creador.removeEnquestaCreada(enquesta);
        }
        
        // 3. Eliminar l'enquesta de persistència
        ctrlEnquesta.eliminarEnquesta(id);
    }

    /**
     * Modifica el títol d'una enquesta.
     * @param idEnquesta L'ID de l'enquesta a modificar.
     * @param nouTitol El nou títol.
     */
    //(jairo)
    public void modificarTitolEnquesta(String idEnquesta, String nouTitol) throws EnquestaNoExisteixException, PermisDenegatException {
        String idCreador = ctrlEnquesta.getIdCreador(idEnquesta);
        if (idCreador == null) {
            throw new EnquestaNoExisteixException(idEnquesta);
        }
        if (!idCreador.equals(getUsuariActual().getUsername())) {
            throw new PermisDenegatException("Només el creador de l'enquesta pot modificar-la.");
        }
        ctrlEnquesta.modificarTitolEnquesta(idEnquesta, nouTitol);
    }

    /**
     * Modifica la descripció d'una enquesta.
     * @param idEnquesta L'ID de l'enquesta a modificar.
     * @param novaDescripcio La nova descripció.
     */
    //(jairo)
    public void modificarDescripcioEnquesta(String idEnquesta, String novaDescripcio) throws EnquestaNoExisteixException, PermisDenegatException {
        String idCreador = ctrlEnquesta.getIdCreador(idEnquesta);
        if (idCreador == null) {
            throw new EnquestaNoExisteixException(idEnquesta);
        }
        if (!idCreador.equals(getUsuariActual().getUsername())) {
            throw new PermisDenegatException("Només el creador de l'enquesta pot modificar-la.");
        }
        ctrlEnquesta.modificarDescripcioEnquesta(idEnquesta, novaDescripcio);
    }

    /**
     * Afegeix una pregunta a una enquesta.
     * @param idEnquesta L'ID de l'enquesta.
     * @param p La pregunta a afegir.
     */
    //(jairo)
    public void afegirPregunta(String idEnquesta, Pregunta p) throws EnquestaNoExisteixException, PermisDenegatException {
        String idCreador = ctrlEnquesta.getIdCreador(idEnquesta);
        if (idCreador == null) {
            throw new EnquestaNoExisteixException(idEnquesta);
        }
        if (!idCreador.equals(getUsuariActual().getUsername())) {
            throw new PermisDenegatException("Només el creador de l'enquesta pot afegir preguntes.");
        }
        ctrlEnquesta.afegirPregunta(idEnquesta, p);
    }

    /**
     * Elimina una pregunta d'una enquesta.
     * @param idEnquesta L'ID de l'enquesta.
     * @param idPregunta L'ID de la pregunta a eliminar.
     */
    //(jairo)
    public void eliminarPregunta(String idEnquesta, String idPregunta) throws EnquestaNoExisteixException, PermisDenegatException {
        String idCreador = ctrlEnquesta.getIdCreador(idEnquesta);
        if (idCreador == null) {
            throw new EnquestaNoExisteixException(idEnquesta);
        }
        if (!idCreador.equals(getUsuariActual().getUsername())) {
            throw new PermisDenegatException("Només el creador de l'enquesta pot eliminar preguntes.");
        }
        ctrlEnquesta.eliminarPregunta(idEnquesta, idPregunta);
    }

    /**
     * Modifica una pregunta existent en una enquesta.
     * @param idEnquesta L'ID de l'enquesta.
     * @param idPregunta L'ID de la pregunta a modificar.
     * @param nova La nova informació de la pregunta.
     */
    //(jairo)
    public void modificarPregunta(String idEnquesta, String idPregunta, Pregunta nova) throws EnquestaNoExisteixException, PermisDenegatException {
        String idCreador = ctrlEnquesta.getIdCreador(idEnquesta);
        if (idCreador == null) {
            throw new EnquestaNoExisteixException(idEnquesta);
        }
        if (!idCreador.equals(getUsuariActual().getUsername())) {
            throw new PermisDenegatException("Només el creador de l'enquesta pot modificar preguntes.");
        }
        ctrlEnquesta.modificarPregunta(idEnquesta, idPregunta, nova);
    }

    /**
     * Registra la participació d'un usuari en una enquesta.
     * @param idEnquesta L'ID de l'enquesta.
     * @param username El nom d'usuari del participant.
     */
    public void registrarParticipacio(String idEnquesta, String username) {
        ctrlEnquesta.registrarParticipacio(idEnquesta, username);
    }

    /**
     * Afegeix una opció a una pregunta d'una enquesta.
     * @param idEnquesta L'ID de l'enquesta.
     * @param idPregunta L'ID de la pregunta.
     * @param o L'opció a afegir.
     */
    //(jairo)
    public void afegirOpcioAPregunta(String idEnquesta, String idPregunta, Opcio o) throws EnquestaNoExisteixException, PermisDenegatException, PreguntaNoExisteixException {
        String idCreador = ctrlEnquesta.getIdCreador(idEnquesta);
        if (idCreador == null) {
            throw new EnquestaNoExisteixException(idEnquesta);
        }
        if (!idCreador.equals(getUsuariActual().getUsername())) {
            throw new PermisDenegatException("Només el creador de l'enquesta pot modificar preguntes.");
        }
        // Verificar que la pregunta existe
        Pregunta pregunta = ctrlEnquesta.getPregunta(idEnquesta, idPregunta);
        if (pregunta == null) {
            throw new PreguntaNoExisteixException(idPregunta);
        }
        // Delegar a CtrlEnquesta
        ctrlEnquesta.afegirOpcioAPregunta(idEnquesta, idPregunta, o);
    }
    

    /**
     * Elimina una opció d'una pregunta.
     * @param idEnquesta L'ID de l'enquesta.
     * @param idPregunta L'ID de la pregunta.
     * @param idOpcio L'ID de l'opció a eliminar.
     */
    //(jairo)
    public void eliminarOpcioDePregunta(String idEnquesta, String idPregunta, int idOpcio) throws EnquestaNoExisteixException, PermisDenegatException, PreguntaNoExisteixException {
        String idCreador = ctrlEnquesta.getIdCreador(idEnquesta);
        if (idCreador == null) {
            throw new EnquestaNoExisteixException(idEnquesta);
        }
        if (!idCreador.equals(getUsuariActual().getUsername())) {
            throw new PermisDenegatException("Només el creador de l'enquesta pot modificar preguntes.");
        }
        // Verificar que la pregunta existe
        Pregunta pregunta = ctrlEnquesta.getPregunta(idEnquesta, idPregunta);
        if (pregunta == null) {
            throw new PreguntaNoExisteixException(idPregunta);
        }
        // Delegar a CtrlEnquesta
        ctrlEnquesta.eliminarOpcioDepregunta(idEnquesta, idPregunta, idOpcio);
    }

    /**
     * Importa una enquesta des d'un fitxer JSON.
     * Format esperat amb nous tipus de preguntes:
     * {
     *   "id": "e1",
     *   "titol": "Enquesta exemple",
     *   "descripcio": "Descripció de l'enquesta",
     *   "preguntes": [
     *     {
     *       "id": "p1",
     *       "text": "Quina és la teva edat?",
     *       "tipus": "numerica",
     *       "min": 0,
     *       "max": 120
     *     },
     *     {
     *       "id": "p2",
     *       "text": "Com valores el servei?",
     *       "tipus": "qualitativa_ordenada",
     *       "opcions": [
     *         {"id": 1, "text": "Molt poc", "ordre": 1},
     *         {"id": 2, "text": "Poc", "ordre": 2},
     *         {"id": 3, "text": "Normal", "ordre": 3},
     *         {"id": 4, "text": "Força", "ordre": 4},
     *         {"id": 5, "text": "Molt", "ordre": 5}
     *       ]
     *     },
     *     {
     *       "id": "p3",
     *       "text": "Quins idiomes parles?",
     *       "tipus": "qualitativa_multiple",
     *       "max_seleccions": 3,
     *       "opcions": [
     *         {"id": 1, "text": "Català"},
     *         {"id": 2, "text": "Castellà"},
     *         {"id": 3, "text": "Anglès"}
     *       ]
     *     },
     *     {
     *       "id": "p4",
     *       "text": "Comentaris",
     *       "tipus": "text"
     *     }
     *   ]
     * }
     */
    //(jairo)
    public void importarEnquesta(String path) throws ErrorImportacioException {
        try {
            // Leer el archivo
            StringBuilder content = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(path))) {
                String line;
                while ((line = br.readLine()) != null) {
                    content.append(line);
                }
            }

            // Parsear JSON
            JSONObject json = new JSONObject(content.toString());
            
            String id = json.getString("id");
            String titol = json.getString("titol");
            String descripcio = json.getString("descripcio");
            
            // Verificar si ja existe
            if (ctrlEnquesta.getEnquesta(id) != null) {
                throw new ErrorImportacioException("Ja existeix una enquesta amb l'ID " + id);
            }
            
            // Crear la enquesta
            Usuari usuariActual = getUsuariActual();
            if (usuariActual == null) {
                throw new ErrorImportacioException("Cal estar autenticat per importar enquestes");
            }
            
            ctrlEnquesta.crearEnquesta(id, titol, descripcio, usuariActual);
            
            // Importar preguntes amb els nous tipus
            if (json.has("preguntes")) {
                JSONArray preguntes = json.getJSONArray("preguntes");
                for (int i = 0; i < preguntes.length(); i++) {
                    JSONObject preguntaJson = preguntes.getJSONObject(i);
                    
                    String idPregunta = preguntaJson.getString("id");
                    String textPregunta = preguntaJson.getString("text");
                    String tipusStr = preguntaJson.optString("tipus", "text");
                    
                    Pregunta pregunta = crearPreguntaPerTipus(preguntaJson, idPregunta, textPregunta, tipusStr);
                    
                    // Importar opcions si és necessari
                    if (pregunta.tipusAdmetOpcions() && preguntaJson.has("opcions")) {
                        JSONArray opcions = preguntaJson.getJSONArray("opcions");
                        for (int j = 0; j < opcions.length(); j++) {
                            JSONObject opcioJson = opcions.getJSONObject(j);
                            int idOpcio = opcioJson.getInt("id");
                            String textOpcio = opcioJson.getString("text");
                            
                            Opcio opcio;
                            if (opcioJson.has("ordre")) {
                                int ordre = opcioJson.getInt("ordre");
                                opcio = new Opcio(idOpcio, textOpcio, ordre);
                            } else {
                                opcio = new Opcio(idOpcio, textOpcio);
                            }
                            
                            pregunta.afegirOpcio(opcio);
                        }
                    }
                    
                    ctrlEnquesta.afegirPregunta(id, pregunta);
                }
            }
            
        } catch (IOException e) {
            throw new ErrorImportacioException("Error llegint el fitxer: " + e.getMessage());
        } catch (Exception e) {
            throw new ErrorImportacioException("Error processant l'enquesta: " + e.getMessage());
        }
    }

    /**
     * Crea una pregunta segons el seu tipus.
     * @param json Objecte JSON amb la informació de la pregunta
     * @param id ID de la pregunta
     * @param text Text de la pregunta
     * @param tipusStr Tipus de pregunta com a string
     * @return Objecte Pregunta creat
     */
    //(jairo)
    private Pregunta crearPreguntaPerTipus(JSONObject json, String id, String text, String tipusStr) {
        switch (tipusStr.toLowerCase()) {
            case "numerica":
                Double min = json.optDouble("min", 0.0);
                Double max = json.optDouble("max", 100.0);
                return new Pregunta(id, text, min, max);
                
            case "qualitativa_ordenada":
            case "ordenada":
                return new Pregunta(id, text, TipusPregunta.QUALITATIVA_ORDENADA, 1);
                
            case "qualitativa_simple":
            case "simple":
                return new Pregunta(id, text, TipusPregunta.QUALITATIVA_NO_ORDENADA_SIMPLE, 1);
                
            case "qualitativa_multiple":
            case "multiple":
                int maxSeleccions = json.optInt("max_seleccions", 3);
                return new Pregunta(id, text, TipusPregunta.QUALITATIVA_NO_ORDENADA_MULTIPLE, maxSeleccions);
                
            case "text":
            default:
                return new Pregunta(id, text);
        }
    }

    // --- Casos de Uso: Respostes ---

   
    /**
     * Processa la contestació d'una enquesta per part d'un usuari.
     * Guarda totes les respostes a les preguntes i registra la participació.
     * @param idEnquesta ID de l'enquesta a contestar
     * @param usuari Usuari que contesta l'enquesta
     * @param respostes HashMap amb idResposta -> textResposta
     * @param idsPreguntaPerResposta HashMap amb idResposta -> idPregunta
     * @throws UsuariNoAutenticatException Si no hi ha usuari autenticat
     * @throws EnquestaNoExisteixException Si l'enquesta no existeix
     * @throws PreguntaNoExisteixException Si alguna pregunta no existeix
     * @throws RespostaInvalidaException Si alguna resposta no és vàlida pel tipus de pregunta
     */
    public void contestarEnquesta(String idEnquesta, HashMap<String, String> respostes, HashMap<String, String> idsPreguntaPerResposta) 
            throws UsuariNoAutenticatException, EnquestaNoExisteixException, EnquestaJaContestadaException, PreguntaNoExisteixException, RespostaInvalidaException {
        
        // Verificar que hi ha un usuari autenticat
        Usuari usuari = getUsuariActual();
        if (usuari == null) {
            throw new UsuariNoAutenticatException("Cal estar autenticat per contestar una enquesta.");
        }
        
        // Verificar que l'enquesta existeix
        Enquesta enquesta = ctrlEnquesta.getEnquesta(idEnquesta);
        if (enquesta == null) {
            throw new EnquestaNoExisteixException(idEnquesta);
        }
        
        // Verificar que l'usuari NO ha contestat ja aquesta enquesta
        if (enquesta.haRespostUsuari(usuari.getUsername())) {
            throw new EnquestaJaContestadaException(idEnquesta, usuari.getUsername());
        }
        
        // PASO 1: Validar TODAS las respuestas ANTES de guardar nada (para evitar rollback)
        HashMap<String, Pregunta> preguntesValidades = new HashMap<>();
        
        for (HashMap.Entry<String, String> entry : respostes.entrySet()) {
            String idResposta = entry.getKey();
            String textResposta = entry.getValue();
            String idPregunta = idsPreguntaPerResposta.get(idResposta);
            
            if (idPregunta != null) {
                // Verificar que la pregunta existe
                Pregunta pregunta = enquesta.getPregunta(idPregunta);
                if (pregunta == null) {
                    throw new PreguntaNoExisteixException(idPregunta);
                }
                
                // Validar resposta segons el tipus de pregunta
                if (!pregunta.validarResposta(textResposta)) {
                    throw new RespostaInvalidaException(
                        "Resposta invàlida per la pregunta " + idPregunta + 
                        " (tipus: " + pregunta.getTipus() + "): " + textResposta
                    );
                }
                
                // Guardar la pregunta validada para usarla después
                preguntesValidades.put(idResposta, pregunta);
            }
        }
        
        // PASO 2: Si llegamos aquí, TODAS las respuestas son válidas → guardarlas
        for (HashMap.Entry<String, String> entry : respostes.entrySet()) {
            String idResposta = entry.getKey();
            String textResposta = entry.getValue();
            Pregunta pregunta = preguntesValidades.get(idResposta);
            
            if (pregunta != null) {
                String idPregunta = idsPreguntaPerResposta.get(idResposta);
                // Registrar la resposta (se guarda en CtrlPersistencia, Pregunta y Usuari)
                ctrlResposta.registrarResposta(idResposta, idPregunta, textResposta, usuari, pregunta);
            }
        }
        
        // PASO 3: Registrar participación
        ctrlEnquesta.registrarParticipacio(idEnquesta, usuari.getUsername());
    }
    
    /**
     * Modifica una resposta d'un usuari a una pregunta concreta.
     * @param usuari L'usuari que modifica la resposta.
     * @param idEnquesta L'ID de l'enquesta.
     * @param idPregunta L'ID de la pregunta.
     * @param novaResposta La nova resposta.
     * @throws EnquestaNoExisteixException Si l'enquesta no existeix.
     * @throws PreguntaNoExisteixException Si la pregunta no existeix.
     * @throws RespostaNoExisteixException Si l'usuari no ha respost aquesta pregunta.
     * @throws PermisDenegatException Si l'usuari no és el propietari de la resposta.
     */
    //(jairo)
    public void modificarResposta(Usuari usuari, String idEnquesta, String idPregunta, String novaResposta) 
            throws EnquestaNoExisteixException, PreguntaNoExisteixException, RespostaNoExisteixException, PermisDenegatException {
        
        // Verificar que l'enquesta existeix
        Enquesta enquesta = ctrlEnquesta.getEnquesta(idEnquesta);
        if (enquesta == null) {
            throw new EnquestaNoExisteixException(idEnquesta);
        }
        
        // Verificar que la pregunta existeix en l'enquesta
        Pregunta pregunta = ctrlEnquesta.getPregunta(idEnquesta, idPregunta);
        if (pregunta == null) {
            throw new PreguntaNoExisteixException(idPregunta);
        }
        
        // Intentar modificar la resposta
        int resultat = ctrlResposta.modificarResposta(usuari, pregunta, novaResposta);
        
        if (resultat == 1) {
            throw new RespostaNoExisteixException(
                "L'usuari " + usuari.getUsername() + " no ha respost la pregunta " + idPregunta + " de l'enquesta " + idEnquesta
            );
        } else if (resultat == 2) {
            throw new PermisDenegatException(
                "Només el propietari de la resposta pot modificar-la."
            );
        }
    }

    /**
     * Esborra la resposta d'un usuari a una pregunta.
     * @param usuari L'usuari que esborra la resposta.
     * @param idEnquesta L'ID de l'enquesta.
     * @param idPregunta L'ID de la pregunta.
     * @throws EnquestaNoExisteixException Si l'enquesta no existeix.
     * @throws RespostaNoExisteixException Si l'usuari no ha respost aquesta pregunta.
     * @throws PermisDenegatException Si l'usuari no és el propietari de la resposta.
     */
    //(jairo)
    public void esborrarResposta(Usuari usuari, String idEnquesta, String idPregunta) 
            throws EnquestaNoExisteixException, RespostaNoExisteixException, PermisDenegatException, PreguntaNoExisteixException {
        
        // Verificar que l'enquesta existeix
        Enquesta enquesta = ctrlEnquesta.getEnquesta(idEnquesta);
        if (enquesta == null) {
            throw new EnquestaNoExisteixException(idEnquesta);
        }
        
        // Verificar que la pregunta existeix en l'enquesta
        Pregunta pregunta = ctrlEnquesta.getPregunta(idEnquesta, idPregunta);
        if (pregunta == null) {
            throw new PreguntaNoExisteixException(idPregunta);
        }
        
        // Intentar esborrar la resposta
        int resultat = ctrlResposta.esborrarResposta(usuari, pregunta);
        
        if (resultat == 1) {
            throw new RespostaNoExisteixException(
                "L'usuari " + usuari.getUsername() + " no ha respost la pregunta " + idPregunta + " de l'enquesta " + idEnquesta
            );
        } else if (resultat == 2) {
            throw new PermisDenegatException(
                "Només el propietari de la resposta pot esborrar-la."
            );
        }
    }

   

    // --- Casos de Uso: Consultes ---

    /**
     * Consulta totes les enquestes disponibles.
     * @return Una llista de totes les enquestes.
     */
    public ArrayList<Enquesta> consultarEnquestes() {
        return ctrlEnquesta.llistarEnquestes();
    }

    /**
     * Consulta una enquesta específica per ID.
     * @param idEnquesta L'ID de l'enquesta.
     * @return L'enquesta o null si no existeix.
     */
    public Enquesta getEnquesta(String idEnquesta) {
        return ctrlEnquesta.getEnquesta(idEnquesta);
    }

    /**
     * Verifica si existeix una enquesta amb l'ID especificat.
     * @param idEnquesta L'ID de l'enquesta.
     * @return true si existeix, false altrament.
     */
    public boolean existeixEnquesta(String idEnquesta) {
        return ctrlEnquesta.getEnquesta(idEnquesta) != null;
    }

    /**
     * Consulta les respostes d'una enquesta específica.
     * @param idEnquesta L'ID de l'enquesta.
     * @return Un mapa d'usuaris (username) i les seves respostes.
     */
    public ArrayList<Resposta> consultarRespostesPregunta(String idPregunta) {
        ArrayList<Resposta> respostes = new ArrayList<>();
        HashMap<String, Resposta> totesRespostes = ctrlPersistencia.getAllRespostes();
        
        // Buscar totes les respostes que comencen amb "idPregunta_"
        for (HashMap.Entry<String, Resposta> entry : totesRespostes.entrySet()) {
            if (entry.getKey().startsWith(idPregunta + "_")) {
                respostes.add(entry.getValue());
            }
        }
        
        return respostes;
    }

    /**
     * Consulta totes les respostes d'una enquesta agrupades per usuari.
     * @param idEnquesta L'ID de l'enquesta.
     * @return Un mapa amb els noms d'usuari com a claus i les seves respostes com a valors.
     */
    public HashMap<String, ArrayList<Resposta>> consultarRespostesEnquesta(String idEnquesta) {
        HashMap<String, ArrayList<Resposta>> respostesPerUsuari = new HashMap<>();
        
        // Obtenir l'enquesta
        Enquesta enquesta = ctrlEnquesta.getEnquesta(idEnquesta);
        if (enquesta == null) {
            return respostesPerUsuari;
        }
        
        // Per cada pregunta de l'enquesta, obtenir les seves respostes
        for (Pregunta pregunta : enquesta.getPreguntes()) {
            ArrayList<Resposta> respostesPregunta = consultarRespostesPregunta(pregunta.getId());
            
            for (Resposta resposta : respostesPregunta) {
                String username = resposta.getUsernameUsuari();
                
                if (!respostesPerUsuari.containsKey(username)) {
                    respostesPerUsuari.put(username, new ArrayList<>());
                }
                
                respostesPerUsuari.get(username).add(resposta);
            }
        }
        
        return respostesPerUsuari;
    }

    /**
     * Registra una resposta per una pregunta d'una enquesta.
     * @param idEnquesta L'ID de l'enquesta.
     * @param usuari L'usuari que respon.
     * @param idPregunta L'ID de la pregunta.
     * @param textResposta El text de la resposta.
     */
    public void registrarResposta(String idEnquesta, Usuari usuari, String idPregunta, String textResposta) {
        // Obtenir la pregunta
        Pregunta pregunta = ctrlEnquesta.getPregunta(idEnquesta, idPregunta);
        if (pregunta == null) {
            return; // No es pot registrar si la pregunta no existeix
        }
        
        // Generar ID únic per la resposta
        String idResposta = idPregunta + "_" + usuari.getUsername();
        
        // Crear la resposta
        Resposta resposta = new Resposta(idResposta, idPregunta, textResposta, usuari);
        
        // Guardar a persistència
        ctrlPersistencia.afegirResposta(idResposta, resposta, pregunta);
    }

    /**
     * Obté les estadístiques del sistema.
     * @return Un HashMap amb les estadístiques del sistema:
     *         - "totalEnquestes": nombre total d'enquestes
     *         - "totalUsuaris": nombre total d'usuaris
     *         - "totalPreguntes": nombre total de preguntes
     *         - "totalParticipants": nombre total de participacions
     */
    public HashMap<String, Integer> getEstadistiques() {
        HashMap<String, Integer> stats = new HashMap<>();
        
        ArrayList<Enquesta> totes = ctrlEnquesta.llistarEnquestes();
        stats.put("totalEnquestes", totes.size());
        stats.put("totalUsuaris", ctrlUsuari.getNumUsuaris());
        
        int totalPreguntes = 0;
        int totalParticipants = 0;
        for (Enquesta e : totes) {
            totalPreguntes += e.getPreguntes().size();
            totalParticipants += e.getParticipants().size();
        }
        
        stats.put("totalPreguntes", totalPreguntes);
        stats.put("totalParticipants", totalParticipants);
        
        return stats;
    }

    // --- Anàlisi i Clustering ---

    public void analitzarRespostes(String idEnquesta) {
        // Lógica para analizar las respuestas y realizar el clustering
    }

    // --- Gestió d'Usuaris i Perfils (mètodes existents) ---

    public void registrarUsuari(String username, String password) throws UsuariJaExisteixException, ParametreInvalidException {
        // Validar que els paràmetres no siguin nuls o buits
        if (username == null || username.trim().isEmpty()) {
            throw new ParametreInvalidException("El nom d'usuari no pot estar buit.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new ParametreInvalidException("La contrasenya no pot estar buida.");
        }
        
        // Normalitzar el username (eliminar espais)
        String normalizedUsername = username.trim();
        
        // Validar longitud mínima
        if (normalizedUsername.length() < 3) {
            throw new ParametreInvalidException("El nom d'usuari ha de tenir almenys 3 caràcters.");
        }
        if (password.length() < 4) {
            throw new ParametreInvalidException("La contrasenya ha de tenir almenys 4 caràcters.");
        }
        
        // Comprovar si l'usuari ja existeix
        if (ctrlPersistencia.existeixUsuari(normalizedUsername)) {
            throw new UsuariJaExisteixException(normalizedUsername);
        }
        
        ctrlUsuari.registrarUsuari(normalizedUsername, password);
    }

    public void login(String username, String password) throws CredencialsIncorrectesException, ParametreInvalidException {
        // Validar que els paràmetres no siguin nuls o buits
        if (username == null || username.trim().isEmpty()) {
            throw new ParametreInvalidException("El nom d'usuari no pot estar buit.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new ParametreInvalidException("La contrasenya no pot estar buida.");
        }
        
        // Normalitzar el username (eliminar espais)
        String normalizedUsername = username.trim();
        
        // Comprovar si l'usuari existeix a persistència
        Usuari usuari = ctrlPersistencia.getUsuari(normalizedUsername);
        
        if (usuari == null) {
            throw new CredencialsIncorrectesException();
        }
        
        // Comprovar si la contrasenya és correcta
        if (!usuari.checkPassword(password)) {
            throw new CredencialsIncorrectesException();
        }
        
        // Si tot és correcte, establir l'usuari actual
        ctrlUsuari.login(usuari);
    }
    
    public Usuari getUsuariActual() {
        return ctrlUsuari.getUsuariActual();
    }

    public boolean checkPassword(String password) {
        return ctrlUsuari.checkPassword(password);
    }

    public void crearPerfil(String id, String descripcio) {
        ctrlPerfil.crearPerfil(id, descripcio);
    }

    public Perfil getPerfil(String id) {
        return ctrlPerfil.getPerfil(id);
    }

    // --- Persistència ---

    /**
     * Obté el controlador de persistència (per a funcionalitats avançades).
     * @return La instància de CtrlPersistencia
     */
    //(jairo)
    public CtrlPersistencia getCtrlPersistencia() {
        return ctrlPersistencia;
    }

    /**
     * Guarda només les enquestes.
     */
    //(jairo)
    public void guardarEnquestes() {
        ctrlPersistencia.saveEnquestes(ctrlEnquesta.getTotesEnquestes());
        System.out.println("Enquestes guardades.");
    }

    /**
     * Guarda només les respostes.
     */
    //(jairo)
    public void guardarRespostes() {
        ctrlPersistencia.saveRespostes(ctrlResposta.getTotesRespostes());
        System.out.println("Respostes guardades.");
    }

    /**
     * Carrega només les enquestes.
     */
    //(jairo)
    public void carregarEnquestes() {
        ArrayList<Enquesta> enquestesCarregades = ctrlPersistencia.loadEnquestes();
        ctrlEnquesta.setTotesEnquestes(enquestesCarregades);
        System.out.println("Enquestes carregades: " + enquestesCarregades.size());
    }

    /**
     * Carrega només les respostes.
     */
    //(jairo)
    public void carregarRespostes() {
        HashMap<String, Resposta> respostesCarregades = ctrlPersistencia.getAllRespostes();
        ctrlResposta.setTotesRespostes(respostesCarregades);
        System.out.println("Respostes carregades.");
    }
}

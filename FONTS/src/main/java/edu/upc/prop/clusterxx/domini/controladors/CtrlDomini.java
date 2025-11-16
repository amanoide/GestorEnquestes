package edu.upc.prop.clusterxx.domini.controladors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.json.JSONObject;

import edu.upc.prop.clusterxx.domini.classes.Enquesta;
import edu.upc.prop.clusterxx.domini.classes.Exceptions.CredencialsIncorrectesException;
import edu.upc.prop.clusterxx.domini.classes.Exceptions.EnquestaJaContestadaException;
import edu.upc.prop.clusterxx.domini.classes.Exceptions.EnquestaJaExisteixException;
import edu.upc.prop.clusterxx.domini.classes.Exceptions.EnquestaNoExisteixException;
import edu.upc.prop.clusterxx.domini.classes.Exceptions.ErrorImportacioException;
import edu.upc.prop.clusterxx.domini.classes.Exceptions.ParametreInvalidException;
import edu.upc.prop.clusterxx.domini.classes.Exceptions.PermisDenegatException;
import edu.upc.prop.clusterxx.domini.classes.Exceptions.PreguntaJaExisteixException;
import edu.upc.prop.clusterxx.domini.classes.Exceptions.PreguntaNoExisteixException;
import edu.upc.prop.clusterxx.domini.classes.Exceptions.RespostaInvalidaException;
import edu.upc.prop.clusterxx.domini.classes.Exceptions.RespostaNoExisteixException;
import edu.upc.prop.clusterxx.domini.classes.Exceptions.UsuariJaExisteixException;
import edu.upc.prop.clusterxx.domini.classes.Exceptions.UsuariNoAutenticatException;
import edu.upc.prop.clusterxx.domini.classes.Opcio;
import edu.upc.prop.clusterxx.domini.classes.Perfil;
import edu.upc.prop.clusterxx.domini.classes.Pregunta;
import edu.upc.prop.clusterxx.domini.classes.Resposta;
import edu.upc.prop.clusterxx.domini.classes.TipusPregunta;
import edu.upc.prop.clusterxx.domini.classes.Usuari;
import edu.upc.prop.clusterxx.domini.classes.Kluster;
import edu.upc.prop.clusterxx.domini.classes.DistanceCalculator;
import edu.upc.prop.clusterxx.domini.classes.ClusterEvaluator;
import static edu.upc.prop.clusterxx.domini.classes.Exceptions.*;

import org.json.JSONArray;


public class CtrlDomini {
    private CtrlEnquesta ctrlEnquesta;
    private CtrlPregunta ctrlPregunta;
    private CtrlResposta ctrlResposta;
    private CtrlUsuari ctrlUsuari;
    private CtrlPerfil ctrlPerfil;
    private CtrlAnalisi ctrlAnalisi; 
    private CtrlPersistencia ctrlPersistencia;

    public CtrlDomini() {
        this.ctrlEnquesta = new CtrlEnquesta();
        this.ctrlPregunta = new CtrlPregunta();
        this.ctrlResposta = new CtrlResposta();
        this.ctrlUsuari = new CtrlUsuari(null); 
        this.ctrlPerfil = new CtrlPerfil();
        this.ctrlAnalisi = new CtrlAnalisi();
        this.ctrlPersistencia = CtrlPersistencia.getInstance(); 
    }

    // --- Casos d'ús: Gestió d'Enquestes ---

    /**
     * Crea una nova enquesta associada a un usuari autenticat.
     * Aquest mètode crea una nova enquesta al sistema amb l'ID, títol i descripció especificats.
     * L'enquesta queda associada a l'usuari autenticat que la crea, qui serà el seu propietari
     * i l'únic amb permisos per modificar-la o eliminar-la.
    
     * Validacions realitzades:
     *   L'usuari no pot ser null
     *   L'ID de l'enquesta no pot estar buit
     *   El títol de l'enquesta no pot estar buit
     *   L'enquesta no pot existir prèviament amb el mateix ID
     *   Ha d'haver-hi un usuari autenticat al sistema
     * 
     * @param usuari L'usuari creador de l'enquesta
     * @param id L'identificador únic de la nova enquesta
     * @param titol El títol descriptiu de l'enquesta
     * @param descripcio La descripció detallada de l'enquesta (pot estar buida però no null)
     * @throws ParametreInvalidException Si algun paràmetre és null o buit (ID o títol)
     * @throws EnquestaJaExisteixException Si ja existeix una enquesta amb aquest ID al sistema
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat o l'usuari és null
     * @see CtrlEnquesta#crearEnquesta(String, String, String, Usuari)
     */

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
     * Esborra una enquesta existent del sistema.
     * 
     * Aquest mètode elimina completament una enquesta del sistema, juntament amb totes les seves
     * dades associades (preguntes, respostes i participacions). Només el creador de l'enquesta
     * té permís per esborrar-la. L'eliminació és irreversible i comporta la pèrdua de totes
     * les dades relacionades amb l'enquesta.
     * 
     * Validacions realitzades:
     * - Ha d'haver-hi un usuari autenticat al sistema
     * - L'enquesta ha d'existir al sistema
     * - L'usuari autenticat ha de ser el creador de l'enquesta
     * 
     * Eliminació en cascada: El mètode segueix aquest ordre d'eliminació per garantir
     * la integritat referencial:
     * 1. Per cada pregunta de l'enquesta:
     *    - Elimina totes les respostes associades a la pregunta del sistema de persistència
     *    - Elimina la pregunta del sistema de persistència global
     * 2. Elimina l'enquesta de la llista d'enquestes creades de l'usuari creador
     * 3. Elimina l'enquesta del sistema de persistència central
     * 
     * Nota: Aquest mètode elimina totes les participacions i respostes dels usuaris que han
     * contestat l'enquesta. Aquesta acció no es pot desfer.
     * 
     * @param id L'identificador únic de l'enquesta a esborrar
     * @throws EnquestaNoExisteixException Si no existeix cap enquesta amb l'ID especificat
     * @throws PermisDenegatException Si l'usuari autenticat no és el creador de l'enquesta
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat al sistema
     * @see CtrlEnquesta#eliminarEnquesta(String)
     */
    public void esborrarEnquesta(String id)
            throws EnquestaNoExisteixException, PermisDenegatException, UsuariNoAutenticatException {
        // Verificar que hi ha un usuari autenticat
        Usuari usuariActual = ctrlUsuari.getUsuariActual();
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
        // Usant l'associació directa: cada Pregunta té les seves Respostes
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
     * Modifica el títol d'una enquesta existent.
     * 
     * Aquest mètode permet canviar el títol d'una enquesta ja creada. Només el creador
     * de l'enquesta té permís per modificar-ne el títol. El mètode valida que l'enquesta
     * existeix, que l'usuari està autenticat, i que té els permisos necessaris abans
     * d'aplicar els canvis.
     * 
     * 
     * Validacions realitzades:
     * 
     * Ha d'haver-hi un usuari autenticat al sistema
     * L'ID de l'enquesta no pot estar buit
     * El nou títol no pot estar buit
     * L'enquesta ha d'existir al sistema
     * L'usuari autenticat ha de ser el creador de l'enquesta
     * 
     * @param idEnquesta L'identificador únic de l'enquesta a modificar
     * @param nouTitol El nou títol que es vol assignar a l'enquesta (no pot estar buit)
     * @throws ParametreInvalidException Si l'ID de l'enquesta o el nou títol són null o buits
     * @throws EnquestaNoExisteixException Si no existeix cap enquesta amb l'ID especificat
     * @throws PermisDenegatException Si l'usuari autenticat no és el creador de l'enquesta
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat al sistema
     * @see CtrlEnquesta#modificarTitolEnquesta(String, String)
     */
    public void modificarTitolEnquesta(String idEnquesta, String nouTitol)
            throws ParametreInvalidException, EnquestaNoExisteixException, PermisDenegatException,
            UsuariNoAutenticatException {
        // Validar que hi ha un usuari autenticat
        Usuari usuariActual = ctrlUsuari.getUsuariActual();
        if (usuariActual == null) {
            throw new UsuariNoAutenticatException("Cal estar autenticat per modificar una enquesta.");
        }

        // Validar paràmetres
        if (idEnquesta == null || idEnquesta.trim().isEmpty()) {
            throw new ParametreInvalidException("L'ID de l'enquesta no pot estar buit.");
        }
        if (nouTitol == null || nouTitol.trim().isEmpty()) {
            throw new ParametreInvalidException("El nou títol no pot estar buit.");
        }

        // Verificar que l'enquesta existeix
        String idCreador = ctrlEnquesta.getIdCreador(idEnquesta);
        if (idCreador == null) {
            throw new EnquestaNoExisteixException(idEnquesta);
        }

        // Verificar permisos
        if (!idCreador.equals(usuariActual.getUsername())) {
            throw new PermisDenegatException("Només el creador de l'enquesta pot modificar-la.");
        }

        ctrlEnquesta.modificarTitolEnquesta(idEnquesta, nouTitol);
    }

    /**
     * Modifica la descripció d'una enquesta existent.

     * Aquest mètode permet canviar la descripció d'una enquesta ja creada. Només el creador
     * de l'enquesta té permís per modificar-ne la descripció. El mètode valida que l'enquesta
     * existeix, que l'usuari està autenticat, i que té els permisos necessaris abans
     * d'aplicar els canvis.
    
     * Validacions realitzades:
     * Ha d'haver-hi un usuari autenticat al sistema
     * L'ID de l'enquesta no pot estar buit
     * La nova descripció no pot ser null (però pot estar buida)
     * L'enquesta ha d'existir al sistema
     * L'usuari autenticat ha de ser el creador de l'enquesta
    
     * 
     * @param idEnquesta L'identificador únic de l'enquesta a modificar
     * @param novaDescripcio La nova descripció que es vol assignar a l'enquesta
     * @throws ParametreInvalidException Si l'ID de l'enquesta és null/buit o si la nova descripció és null
     * @throws EnquestaNoExisteixException Si no existeix cap enquesta amb l'ID especificat
     * @throws PermisDenegatException Si l'usuari autenticat no és el creador de l'enquesta
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat al sistema
     * @see CtrlEnquesta#modificarDescripcioEnquesta(String, String)
     */
    public void modificarDescripcioEnquesta(String idEnquesta, String novaDescripcio)
            throws ParametreInvalidException, EnquestaNoExisteixException, PermisDenegatException,
            UsuariNoAutenticatException {
        // Validar que hi ha un usuari autenticat
        Usuari usuariActual = ctrlUsuari.getUsuariActual();
        if (usuariActual == null) {
            throw new UsuariNoAutenticatException("Cal estar autenticat per modificar una enquesta.");
        }

        // Validar paràmetres
        if (idEnquesta == null || idEnquesta.trim().isEmpty()) {
            throw new ParametreInvalidException("L'ID de l'enquesta no pot estar buit.");
        }
        if (novaDescripcio == null) {
            throw new ParametreInvalidException("La nova descripció no pot ser null.");
        }

        // Verificar que l'enquesta existeix
        String idCreador = ctrlEnquesta.getIdCreador(idEnquesta);
        if (idCreador == null) {
            throw new EnquestaNoExisteixException(idEnquesta);
        }

        // Verificar permisos
        if (!idCreador.equals(usuariActual.getUsername())) {
            throw new PermisDenegatException("Només el creador de l'enquesta pot modificar-la.");
        }

        ctrlEnquesta.modificarDescripcioEnquesta(idEnquesta, novaDescripcio);
    }

    /**
     * Afegeix una nova pregunta a una enquesta existent.
    
     * Aquest mètode permet afegir una pregunta a una enquesta prèviament creada. Només el creador
     * de l'enquesta té permís per afegir-hi preguntes. La pregunta s'afegeix amb tota la seva
     * configuració (tipus, opcions, validacions) i queda disponible per ser contestada pels usuaris.
     * 
    
     * Validacions realitzades:
     *
     *Ha d'haver-hi un usuari autenticat al sistema
     *L'ID de l'enquesta no pot estar buit
     *La pregunta no pot ser null
     *L'ID de la pregunta no pot estar buit
     *El text de la pregunta no pot estar buit
     *L'enquesta ha d'existir al sistema
     *La pregunta no pot existir ja a l'enquesta (ID únic)
     *L'usuari autenticat ha de ser el creador de l'enquesta
     *L'enquesta NO pot tenir participacions prèvies (respostes d'usuaris)
     * 
     * Restricció crítica: No es poden afegir preguntes a una enquesta que ja té
     * respostes d'usuaris. Això crearia inconsistència perquè els participants anteriors haurien
     * contestat amb menys preguntes que els nous participants.
     * 
     * @param idEnquesta L'identificador únic de l'enquesta on s'afegirà la pregunta
     * @param p L'objecte Pregunta a afegir, amb tot el seu contingut (text, tipus, opcions, etc.)
     * @throws ParametreInvalidException Si algun paràmetre és null o buit (idEnquesta, pregunta, ID o text)
     * @throws EnquestaNoExisteixException Si no existeix cap enquesta amb l'ID especificat
     * @throws PermisDenegatException Si l'usuari autenticat no és el creador de l'enquesta
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat al sistema
     * @throws PreguntaJaExisteixException Si ja existeix una pregunta amb aquest ID a l'enquesta
     * @throws RespostaInvalidaException Si l'enquesta ja té respostes/participacions d'usuaris
     * @see CtrlEnquesta#afegirPregunta(String, Pregunta)
     */
    public void afegirPregunta(String idEnquesta, Pregunta p)
            throws ParametreInvalidException, EnquestaNoExisteixException, PermisDenegatException,
            UsuariNoAutenticatException, PreguntaJaExisteixException, RespostaInvalidaException {
        // Validar que hi ha un usuari autenticat
        Usuari usuariActual = ctrlUsuari.getUsuariActual();
        if (usuariActual == null) {
            throw new UsuariNoAutenticatException("Cal estar autenticat per afegir preguntes a una enquesta.");
        }

        // Validar paràmetres
        if (idEnquesta == null || idEnquesta.trim().isEmpty()) {
            throw new ParametreInvalidException("L'ID de l'enquesta no pot estar buit.");
        }
        if (p == null) {
            throw new ParametreInvalidException("La pregunta no pot ser null.");
        }
        if (p.getId() == null || p.getId().trim().isEmpty()) {
            throw new ParametreInvalidException("L'ID de la pregunta no pot estar buit.");
        }
        if (p.getText() == null || p.getText().trim().isEmpty()) {
            throw new ParametreInvalidException("El text de la pregunta no pot estar buit.");
        }

        // Verificar que l'enquesta existeix
        Enquesta enquesta = ctrlEnquesta.getEnquesta(idEnquesta);
        if (enquesta == null) {
            throw new EnquestaNoExisteixException(idEnquesta);
        }

        // Verificar que la pregunta NO existeix ja a l'enquesta
        if (enquesta.getPregunta(p.getId()) != null) {
            throw new PreguntaJaExisteixException(p.getId(), idEnquesta);
        }

        // Verificar permisos
        String idCreador = enquesta.getIdCreador();
        if (!idCreador.equals(usuariActual.getUsername())) {
            throw new PermisDenegatException("Només el creador de l'enquesta pot afegir preguntes.");
        }

        // CRÍTICO: Si l'enquesta ja té respostes (participacions), NO es poden afegir
        // més preguntes
        // (crearia inconsistència: alguns usuaris haurien contestat amb menys
        // preguntes)
        if (!enquesta.getParticipants().isEmpty()) {
            throw new RespostaInvalidaException(
                    "No es pot afegir una pregunta a una enquesta que ja té respostes (" +
                            enquesta.getParticipants().size() + " participant/s). " +
                            "Afegir preguntes crearia inconsistència en les respostes existents.");
        }

        ctrlEnquesta.afegirPregunta(idEnquesta, p);
    }

    /**
     * Elimina una pregunta existent d'una enquesta.
     * 
     * Aquest mètode elimina una pregunta d'una enquesta prèviament creada, juntament amb
     * totes les respostes associades a aquesta pregunta. Només el creador de l'enquesta
     * té permís per eliminar-ne preguntes. L'eliminació és irreversible i comporta la
     * pèrdua de totes les dades de resposta relacionades.
     * 
     * Validacions realitzades:
     * 
     *Ha d'haver-hi un usuari autenticat al sistema
     *L'ID de l'enquesta no pot estar buit
     *L'ID de la pregunta no pot estar buit
     *L'enquesta ha d'existir al sistema
     *La pregunta ha d'existir a l'enquesta especificada
     *L'usuari autenticat ha de ser el creador de l'enquesta
     *Eliminació en cascada: Abans d'eliminar la pregunta, el mètode
     * elimina automàticament totes les respostes associades a aquesta pregunta de:
     *Les respostes locals de la pregunta
     *El sistema de persistència global
     *Els perfils dels usuaris que van respondre
     * Nota: Es pot eliminar una pregunta fins i tot si té respostes d'usuaris, però
     * aquestes respostes es perdran definitivament. A diferència d'afegir preguntes,
     * eliminar-les no crea inconsistències perquè els participants mantenen el mateix
     * conjunt de preguntes després de l'eliminació.
     * 
     * @param idEnquesta L'identificador únic de l'enquesta que conté la pregunta
     * @param idPregunta L'identificador únic de la pregunta a eliminar
     * @throws ParametreInvalidException Si l'ID de l'enquesta o de la pregunta són null o buits
     * @throws EnquestaNoExisteixException Si no existeix cap enquesta amb l'ID especificat
     * @throws PreguntaNoExisteixException Si la pregunta no existeix a l'enquesta especificada
     * @throws PermisDenegatException Si l'usuari autenticat no és el creador de l'enquesta
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat al sistema
     * @see CtrlEnquesta#eliminarPregunta(String, String)
     */
    public void eliminarPregunta(String idEnquesta, String idPregunta)
            throws ParametreInvalidException, EnquestaNoExisteixException, PreguntaNoExisteixException,
            PermisDenegatException, UsuariNoAutenticatException {
        // Validar que hi ha un usuari autenticat
        Usuari usuariActual = ctrlUsuari.getUsuariActual();
        if (usuariActual == null) {
            throw new UsuariNoAutenticatException("Cal estar autenticat per eliminar preguntes d'una enquesta.");
        }

        // Validar paràmetres
        if (idEnquesta == null || idEnquesta.trim().isEmpty()) {
            throw new ParametreInvalidException("L'ID de l'enquesta no pot estar buit.");
        }
        if (idPregunta == null || idPregunta.trim().isEmpty()) {
            throw new ParametreInvalidException("L'ID de la pregunta no pot estar buit.");
        }

        // Verificar que l'enquesta existeix
        Enquesta enquesta = ctrlEnquesta.getEnquesta(idEnquesta);
        if (enquesta == null) {
            throw new EnquestaNoExisteixException(idEnquesta);
        }

        // Verificar que la pregunta existeix a l'enquesta
        Pregunta pregunta = enquesta.getPregunta(idPregunta);
        if (pregunta == null) {
            throw new PreguntaNoExisteixException(
                    "La pregunta amb ID '" + idPregunta + "' no existeix a l'enquesta '" + idEnquesta + "'.");
        }

        // Verificar permisos
        String idCreador = enquesta.getIdCreador();
        if (!idCreador.equals(usuariActual.getUsername())) {
            throw new PermisDenegatException("Només el creador de l'enquesta pot eliminar preguntes.");
        }

        // IMPORTANT: Eliminar totes les respostes associades a aquesta pregunta abans
        // d'eliminar-la
        // Crear una llista temporal per evitar ConcurrentModificationException
        HashMap<String, Resposta> respostesPregunta = pregunta.getRespostes();
        ArrayList<String> idsRespostes = new ArrayList<>(respostesPregunta.keySet());

        for (String username : idsRespostes) {
            Resposta resposta = respostesPregunta.get(username);
            // 1. Eliminar de la pregunta (associació local)
            pregunta.eliminarResposta(username);
            // 2. Eliminar de persistència i usuari (associacions globals)
            ctrlPersistencia.eliminarResposta(resposta.getId());
        }

        // Ara podem eliminar la pregunta de manera segura
        ctrlEnquesta.eliminarPregunta(idEnquesta, idPregunta);
    }

    /**
     * Modifica una pregunta existent en una enquesta.
     * 
     * Aquest mètode permet canviar el contingut d'una pregunta ja creada (text, tipus, opcions, etc.).
     * Només el creador de l'enquesta té permís per modificar-ne les preguntes. La pregunta modificada
     * manté el mateix ID però pot canviar tots els seus altres atributs.
     * 
     * Validacions realitzades:
     * - Ha d'haver-hi un usuari autenticat al sistema
     * - L'ID de l'enquesta no pot estar buit
     * - L'ID de la pregunta a modificar no pot estar buit
     * - La nova pregunta no pot ser null
     * - El text de la nova pregunta no pot estar buit
     * - L'ID de la nova pregunta no pot estar buit
     * - L'enquesta ha d'existir al sistema
     * - La pregunta a modificar ha d'existir a l'enquesta
     * - L'usuari autenticat ha de ser el creador de l'enquesta
     * - L'ID de la nova pregunta ha de coincidir amb l'ID de la pregunta a modificar
     * - La pregunta NO pot tenir cap resposta d'usuari associada
     * 
     * Restricció crítica: Només es pot modificar una pregunta si NO té cap resposta
     * associada. Si la pregunta ja té respostes d'usuaris, qualsevol modificació podria invalidar
     * aquestes respostes o canviar-ne el significat. Per exemple, canviar una pregunta numèrica a
     * qualitativa invalidaria respostes numèriques existents.
     * 
     * Alternativa: Si necessites modificar una pregunta que ja té respostes, has de:
     * 1. Eliminar la pregunta existent (això eliminarà també les seves respostes)
     * 2. Crear una nova pregunta amb el contingut modificat
     * 
     * @param idEnquesta L'identificador únic de l'enquesta que conté la pregunta
     * @param idPregunta L'identificador únic de la pregunta a modificar
     * @param nova L'objecte Pregunta amb les noves dades (ha de mantenir el mateix ID)
     * @throws ParametreInvalidException Si algun paràmetre és null, buit o l'ID de la nova pregunta no coincideix
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat al sistema
     * @throws EnquestaNoExisteixException Si l'enquesta no existeix
     * @throws PreguntaNoExisteixException Si la pregunta no existeix a l'enquesta
     * @throws PermisDenegatException Si l'usuari autenticat no és el creador de l'enquesta
     * @throws RespostaInvalidaException Si la pregunta ja té respostes associades
     * @see CtrlEnquesta#modificarPregunta(String, String, Pregunta)
     */
    public void modificarPregunta(String idEnquesta, String idPregunta, Pregunta nova)
            throws ParametreInvalidException, UsuariNoAutenticatException, EnquestaNoExisteixException,
            PreguntaNoExisteixException, PermisDenegatException, RespostaInvalidaException {
        // Validar que hi ha un usuari autenticat
        Usuari usuariActual = ctrlUsuari.getUsuariActual();
        if (usuariActual == null) {
            throw new UsuariNoAutenticatException("Cal estar autenticat per modificar preguntes d'una enquesta.");
        }

        // Validar paràmetres
        if (idEnquesta == null || idEnquesta.trim().isEmpty()) {
            throw new ParametreInvalidException("L'ID de l'enquesta no pot estar buit.");
        }
        if (idPregunta == null || idPregunta.trim().isEmpty()) {
            throw new ParametreInvalidException("L'ID de la pregunta no pot estar buit.");
        }
        if (nova == null) {
            throw new ParametreInvalidException("La nova pregunta no pot ser null.");
        }
        if (nova.getText() == null || nova.getText().trim().isEmpty()) {
            throw new ParametreInvalidException("El text de la nova pregunta no pot estar buit.");
        }
        if (nova.getId() == null || nova.getId().trim().isEmpty()) {
            throw new ParametreInvalidException("L'ID de la nova pregunta no pot estar buit.");
        }

        // Verificar que l'enquesta existeix
        Enquesta enquesta = ctrlEnquesta.getEnquesta(idEnquesta);
        if (enquesta == null) {
            throw new EnquestaNoExisteixException(idEnquesta);
        }

        // Verificar que la pregunta existeix a l'enquesta
        Pregunta preguntaActual = enquesta.getPregunta(idPregunta);
        if (preguntaActual == null) {
            throw new PreguntaNoExisteixException(
                    "La pregunta amb ID '" + idPregunta + "' no existeix a l'enquesta '" + idEnquesta + "'.");
        }

        // Verificar permisos
        String idCreador = enquesta.getIdCreador();
        if (!idCreador.equals(usuariActual.getUsername())) {
            throw new PermisDenegatException("Només el creador de l'enquesta pot modificar preguntes.");
        }

        // Verificar que l'ID de la nova pregunta coincideix amb l'ID de la pregunta a
        // modificar
        if (!nova.getId().equals(idPregunta)) {
            throw new ParametreInvalidException(
                    "L'ID de la nova pregunta ('" + nova.getId()
                            + "') ha de coincidir amb l'ID de la pregunta a modificar ('" + idPregunta + "').");
        }

        // CRÍTICO: Si hi ha respostes, NO es pot modificar RES
        HashMap<String, Resposta> respostesExistents = preguntaActual.getRespostes();
        if (!respostesExistents.isEmpty()) {
            throw new RespostaInvalidaException(
                    "No es pot modificar una pregunta que ja té respostes (" + respostesExistents.size()
                            + " resposta/es). " +
                            "Per modificar-la, primer elimina la pregunta i crea-la de nou.");
        }

        // Si no hi ha respostes, podem modificar usant el mètode de CtrlEnquesta
        ctrlEnquesta.modificarPregunta(idEnquesta, idPregunta, nova);
    }

    /**
     * Afegeix una opció a una pregunta qualitativa d'una enquesta.
     * 
     * Aquest mètode permet afegir una nova opció de resposta a una pregunta que admet opcions
     * predefinides (preguntes qualitatives). Només el creador de l'enquesta té permís per
     * afegir opcions a les preguntes. L'opció queda disponible per ser seleccionada pels
     * usuaris quan responguin la pregunta.
     * 
     * Validacions realitzades:
     * - Ha d'haver-hi un usuari autenticat al sistema
     * - L'ID de l'enquesta no pot estar buit
     * - L'ID de la pregunta no pot estar buit
     * - L'opció no pot ser null
     * - El text de l'opció no pot estar buit
     * - L'enquesta ha d'existir al sistema
     * - La pregunta ha d'existir a l'enquesta
     * - L'usuari autenticat ha de ser el creador de l'enquesta
     * - El tipus de pregunta ha d'admetre opcions (només preguntes qualitatives)
     * - No pot existir ja una opció amb el mateix ID a la pregunta
     * - La pregunta NO pot tenir cap resposta d'usuari associada
     * 
     * Restricció: Només es poden afegir opcions a preguntes de tipus qualitativa
     * (ordenada, simple o múltiple). Les preguntes de text lliure o numèriques no admeten
     * opcions predefinides.
     * 
     * Restricció crítica: No es poden afegir opcions a una pregunta que ja té respostes
     * d'usuaris. Afegir noves opcions podria alterar el significat de les respostes existents
     * o crear inconsistències. Si necessites afegir opcions, has d'eliminar primer totes
     * les respostes de la pregunta.
     * 
     * @param idEnquesta L'identificador únic de l'enquesta que conté la pregunta
     * @param idPregunta L'identificador únic de la pregunta on s'afegirà l'opció
     * @param o L'objecte Opcio a afegir amb el seu ID i text
     * @throws ParametreInvalidException Si algun paràmetre és null o buit
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat al sistema
     * @throws EnquestaNoExisteixException Si l'enquesta no existeix
     * @throws PreguntaNoExisteixException Si la pregunta no existeix a l'enquesta
     * @throws PermisDenegatException Si l'usuari autenticat no és el creador de l'enquesta
     * @throws RespostaInvalidaException Si el tipus de pregunta no admet opcions, l'opció ja existeix, o la pregunta té respostes
     * @see CtrlEnquesta#afegirOpcioAPregunta(String, String, Opcio)
     */
    public void afegirOpcioAPregunta(String idEnquesta, String idPregunta, Opcio o)
            throws ParametreInvalidException, UsuariNoAutenticatException, EnquestaNoExisteixException,
            PreguntaNoExisteixException, PermisDenegatException, RespostaInvalidaException {
        // Validar que hi ha un usuari autenticat
        Usuari usuariActual = ctrlUsuari.getUsuariActual();
        if (usuariActual == null) {
            throw new UsuariNoAutenticatException("Cal estar autenticat per afegir opcions a preguntes.");
        }

        /**
         * FUNCIÓN TRIM()
         * Elimina los espacios en blanco iniciales y finales de esta cadena.
         * - Selecciona la implementación adecuada según la codificación interna (Latin1
         * o UTF-16).
         * - Si el helper devuelve null significa que no había nada que recortar,
         * por lo que se devuelve `this` para evitar crear un nuevo objeto.
         */

        // Validar paràmetres
        if (idEnquesta == null || idEnquesta.trim().isEmpty()) {
            throw new ParametreInvalidException("L'ID de l'enquesta no pot estar buit.");
        }
        if (idPregunta == null || idPregunta.trim().isEmpty()) {
            throw new ParametreInvalidException("L'ID de la pregunta no pot estar buit.");
        }
        if (o == null) {
            throw new ParametreInvalidException("L'opció no pot ser null.");
        }
        if (o.getText() == null || o.getText().trim().isEmpty()) {
            throw new ParametreInvalidException("El text de l'opció no pot estar buit.");
        }

        // Verificar que l'enquesta existeix
        Enquesta enquesta = ctrlEnquesta.getEnquesta(idEnquesta);
        if (enquesta == null) {
            throw new EnquestaNoExisteixException(idEnquesta);
        }

        // Verificar que la pregunta existeix
        Pregunta pregunta = enquesta.getPregunta(idPregunta);
        if (pregunta == null) {
            throw new PreguntaNoExisteixException(
                    "La pregunta amb ID '" + idPregunta + "' no existeix a l'enquesta '" + idEnquesta + "'.");
        }

        // Verificar permisos
        String idCreador = enquesta.getIdCreador();
        if (!idCreador.equals(usuariActual.getUsername())) {
            throw new PermisDenegatException("Només el creador de l'enquesta pot afegir opcions a preguntes.");
        }

        // Verificar que el tipus de pregunta admet opcions
        if (!pregunta.tipusAdmetOpcions()) {
            throw new RespostaInvalidaException(
                    "No es poden afegir opcions a preguntes de tipus " + pregunta.getTipus() +
                            ". Només les preguntes qualitatives admeten opcions predefinides.");
        }

        // Verificar que no existeix ja una opció amb aquest ID
        if (pregunta.getOpcio(o.getId()) != null) {
            throw new RespostaInvalidaException(
                    "Ja existeix una opció amb l'ID " + o.getId() + " a la pregunta '" + idPregunta + "'.");
        }

        // CRÍTICO: Si hi ha respostes, NO es pot afegir cap opció nova
        // (podria alterar la semàntica de les respostes existents)
        HashMap<String, Resposta> respostesExistents = pregunta.getRespostes();
        if (!respostesExistents.isEmpty()) {
            throw new RespostaInvalidaException(
                    "No es pot afegir una opció a una pregunta que ja té respostes (" + respostesExistents.size()
                            + " resposta/es). " +
                            "Afegir opcions podria alterar el significat de les respostes existents.");
        }

        // Si totes les validacions passen, afegir l'opció
        ctrlEnquesta.afegirOpcioAPregunta(idEnquesta, idPregunta, o);
    }
    

    /**
     * Elimina una opció existent d'una pregunta qualitativa d'una enquesta.
     * 
     * Aquest mètode permet eliminar una opció de resposta d'una pregunta que admet opcions
     * predefinides (preguntes qualitatives). Només el creador de l'enquesta té permís per
     * eliminar opcions de les preguntes. L'eliminació és irreversible.
     * 
     * Validacions realitzades:
     * - Ha d'haver-hi un usuari autenticat al sistema
     * - L'ID de l'enquesta no pot estar buit
     * - L'ID de la pregunta no pot estar buit
     * - L'ID de l'opció no pot ser negatiu
     * - L'enquesta ha d'existir al sistema
     * - La pregunta ha d'existir a l'enquesta
     * - L'usuari autenticat ha de ser el creador de l'enquesta
     * - El tipus de pregunta ha d'admetre opcions (només preguntes qualitatives)
     * - L'opció amb l'ID especificat ha d'existir a la pregunta
     * - La pregunta NO pot tenir cap resposta d'usuari associada
     * 
     * Restricció: Només es poden eliminar opcions de preguntes de tipus qualitativa
     * (ordenada, simple o múltiple). Les preguntes de text lliure o numèriques no tenen
     * opcions predefinides.
     * 
     * Restricció crítica: No es poden eliminar opcions d'una pregunta que ja té respostes
     * d'usuaris. Eliminar opcions invalidaria les respostes existents que podrien fer
     * referència a aquesta opció. Si necessites eliminar opcions, has d'eliminar primer
     * totes les respostes de la pregunta.
     * 
     * @param idEnquesta L'identificador únic de l'enquesta que conté la pregunta
     * @param idPregunta L'identificador únic de la pregunta que conté l'opció
     * @param idOpcio L'identificador numèric de l'opció a eliminar
     * @throws ParametreInvalidException Si algun paràmetre és null, buit o l'ID de l'opció és negatiu
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat al sistema
     * @throws EnquestaNoExisteixException Si l'enquesta no existeix
     * @throws PreguntaNoExisteixException Si la pregunta no existeix a l'enquesta
     * @throws PermisDenegatException Si l'usuari autenticat no és el creador de l'enquesta
     * @throws RespostaInvalidaException Si el tipus de pregunta no admet opcions, l'opció no existeix, o la pregunta té respostes
     * @see CtrlEnquesta#eliminarOpcioDepregunta(String, String, int)
     */
    public void eliminarOpcioDePregunta(String idEnquesta, String idPregunta, int idOpcio)
            throws ParametreInvalidException, UsuariNoAutenticatException, EnquestaNoExisteixException,
            PreguntaNoExisteixException, PermisDenegatException, RespostaInvalidaException {
        // Validar que hi ha un usuari autenticat
        Usuari usuariActual = ctrlUsuari.getUsuariActual();
        if (usuariActual == null) {
            throw new UsuariNoAutenticatException("Cal estar autenticat per eliminar opcions de preguntes.");
        }

        /**
         * FUNCIÓN TRIM()
         * Elimina los espacios en blanco iniciales y finales de esta cadena.
         * - Selecciona la implementación adecuada según la codificación interna (Latin1
         * o UTF-16).
         * - Si el helper devuelve null significa que no había nada que recortar,
         * por lo que se devuelve `this` para evitar crear un nuevo objeto.
         */

        // Validar paràmetres
        if (idEnquesta == null || idEnquesta.trim().isEmpty()) {
            throw new ParametreInvalidException("L'ID de l'enquesta no pot estar buit.");
        }
        if (idPregunta == null || idPregunta.trim().isEmpty()) {
            throw new ParametreInvalidException("L'ID de la pregunta no pot estar buit.");
        }
        if (idOpcio < 0) {
            throw new ParametreInvalidException("L'ID de l'opció no pot ser negatiu.");
        }

        // Verificar que l'enquesta existeix
        Enquesta enquesta = ctrlEnquesta.getEnquesta(idEnquesta);
        if (enquesta == null) {
            throw new EnquestaNoExisteixException(idEnquesta);
        }

        // Verificar que la pregunta existeix
        Pregunta pregunta = enquesta.getPregunta(idPregunta);
        if (pregunta == null) {
            throw new PreguntaNoExisteixException(
                    "La pregunta amb ID '" + idPregunta + "' no existeix a l'enquesta '" + idEnquesta + "'.");
        }

        // Verificar permisos
        String idCreador = enquesta.getIdCreador();
        if (!idCreador.equals(usuariActual.getUsername())) {
            throw new PermisDenegatException("Només el creador de l'enquesta pot eliminar opcions de preguntes.");
        }

        // Verificar que el tipus de pregunta admet opcions
        if (!pregunta.tipusAdmetOpcions()) {
            throw new RespostaInvalidaException(
                    "No es poden eliminar opcions de preguntes de tipus " + pregunta.getTipus() +
                            ". Només les preguntes qualitatives tenen opcions predefinides.");
        }

        // Verificar que l'opció existeix
        if (pregunta.getOpcio(idOpcio) == null) {
            throw new RespostaInvalidaException(
                    "No existeix cap opció amb l'ID " + idOpcio + " a la pregunta '" + idPregunta + "'.");
        }

        // CRÍTICO: Si hi ha respostes, NO es pot eliminar cap opció
        // (les respostes podrien referenciar aquesta opció)
        HashMap<String, Resposta> respostesExistents = pregunta.getRespostes();
        if (!respostesExistents.isEmpty()) {
            throw new RespostaInvalidaException(
                    "No es pot eliminar una opció d'una pregunta que ja té respostes (" + respostesExistents.size()
                            + " resposta/es). " +
                            "Eliminar opcions invalidaria les respostes existents.");
        }

        // Si totes les validacions passen, eliminar l'opció
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
            Usuari usuariActual = ctrlUsuari.getUsuariActual();
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

    /**
     * Importa respostes d'usuaris des d'un fitxer JSON.
     * Format esperat:
     * {
     *   "enquesta_id": "ID_ENQUESTA",
     *   "respostes": [
     *     {
     *       "username": "usuari1",
     *       "respostes": [
     *         {"pregunta_id": "P1", "resposta": "Text resposta"},
     *         {"pregunta_id": "P2", "resposta": "25"}
     *       ]
     *     }
     *   ]
     * }
     */
    public void importarRespostes(String path) throws ErrorImportacioException {
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
            
            String idEnquesta = json.getString("enquesta_id");
            
            // Verificar que l'enquesta existeix
            Enquesta enquesta = ctrlEnquesta.getEnquesta(idEnquesta);
            if (enquesta == null) {
                throw new ErrorImportacioException("No existeix cap enquesta amb l'ID " + idEnquesta);
            }
            
            // Primer, validar que TOTES les preguntes del JSON existeixen a l'enquesta
            if (json.has("respostes")) {
                JSONArray respostesArray = json.getJSONArray("respostes");
                
                for (int i = 0; i < respostesArray.length(); i++) {
                    JSONObject respostaUsuariJson = respostesArray.getJSONObject(i);
                    
                    if (respostaUsuariJson.has("respostes")) {
                        JSONArray respostesUsuari = respostaUsuariJson.getJSONArray("respostes");
                        
                        for (int j = 0; j < respostesUsuari.length(); j++) {
                            JSONObject respostaJson = respostesUsuari.getJSONObject(j);
                            String idPregunta = respostaJson.getString("pregunta_id");
                            
                            // Verificar que la pregunta existeix en l'enquesta
                            boolean preguntaExisteix = false;
                            for (Pregunta p : enquesta.getPreguntes()) {
                                if (p.getId().equals(idPregunta)) {
                                    preguntaExisteix = true;
                                    break;
                                }
                            }
                            
                            if (!preguntaExisteix) {
                                throw new ErrorImportacioException(
                                    "La pregunta amb ID '" + idPregunta + "' no existeix a l'enquesta '" + 
                                    idEnquesta + "'. Totes les preguntes del JSON han de coincidir amb l'enquesta.");
                            }
                        }
                    }
                }
            }
            
            // Si arribem aquí, totes les preguntes són vàlides. Procedir amb la importació
            if (json.has("respostes")) {
                JSONArray respostesArray = json.getJSONArray("respostes");
                int respostesImportades = 0;
                
                for (int i = 0; i < respostesArray.length(); i++) {
                    JSONObject respostaUsuariJson = respostesArray.getJSONObject(i);
                    
                    String username = respostaUsuariJson.getString("username");
                    
                    // Verificar que l'usuari existeix
                    Usuari usuari = ctrlPersistencia.getUsuari(username);
                    if (usuari == null) {
                        //IMPORTANTE: CREO EL USUARIO SI NO EXISTE PARA HACER LA PRUEBA DE IMPORTAR RESPOSTA PARA NO TENER QUE CREARLOS A MANO
                        //ESTO SE TIENE QUE QUITAR LUEGO
                        ctrlUsuari.registrarUsuari(username, "imported_password");
                        usuari = ctrlPersistencia.getUsuari(username);

                        //DE MOMENTO COMENTO ESTO PARA QUE NO SALGA EL AVISO LUEGO DEBEMOS QUITAR LA CREACION AUTOMATICA Y PONERLO OTRA VEZ
                        //System.out.println("⚠ Avís: L'usuari '" + username + "' no existeix, se saltarà.");
                        //continue;
                    }
                    
                    // Importar les respostes d'aquest usuari
                    if (respostaUsuariJson.has("respostes")) {
                        JSONArray respostesUsuari = respostaUsuariJson.getJSONArray("respostes");
                        boolean usuariTeRespostes = false;
                        
                        for (int j = 0; j < respostesUsuari.length(); j++) {
                            JSONObject respostaJson = respostesUsuari.getJSONObject(j);
                            
                            String idPregunta = respostaJson.getString("pregunta_id");
                            String textResposta = respostaJson.getString("resposta");
                            
                            // No cal verificar de nou, ja s'ha validat abans
                            
                            // Registrar la resposta
                            try {
                                registrarResposta(idEnquesta, usuari, idPregunta, textResposta);
                                usuariTeRespostes = true;
                            } catch (Exception e) {
                                System.out.println("⚠ Avís: Error registrant resposta de '" + username + 
                                                 "' per pregunta '" + idPregunta + "': " + e.getMessage());
                            }
                        }
                        
                        // Registrar participació si l'usuari ha respost almenys una pregunta
                        if (usuariTeRespostes) {
                            try {
                                registrarParticipacio(idEnquesta, username);
                                respostesImportades++;
                            } catch (Exception e) {
                                // Ignorar si ja estava registrat
                            }
                        }
                    }
                }
                
                if (respostesImportades == 0) {
                    throw new ErrorImportacioException("No s'ha pogut importar cap resposta vàlida");
                }
                
                System.out.println("✓ S'han importat respostes de " + respostesImportades + " participants");
            } else {
                throw new ErrorImportacioException("El fitxer JSON no conté l'array 'respostes'");
            }
            
        } catch (IOException e) {
            throw new ErrorImportacioException("Error llegint el fitxer: " + e.getMessage());
        } catch (Exception e) {
            throw new ErrorImportacioException("Error processant les respostes: " + e.getMessage());
        }
    }

    // --- Casos de Uso: Respostes ---

   
    /**
     * Processa la contestació d'una enquesta per part d'un usuari.
     * Guarda totes les respostes a les preguntes i registra la participació.
     * @param idEnquesta ID de l'enquesta a contestar
     * @param respostes HashMap amb idResposta -> textResposta
     * @param idsPreguntaPerResposta HashMap amb idResposta -> idPregunta
     * @throws UsuariNoAutenticatException Si no hi ha usuari autenticat
     * @throws EnquestaNoExisteixException Si l'enquesta no existeix
     * @throws PreguntaNoExisteixException Si alguna pregunta no existeix
     * @throws RespostaInvalidaException Si alguna resposta no és vàlida pel tipus de pregunta
     * @throws ParametreInvalidException Si algun paràmetre és null
     */
    public void contestarEnquesta(String idEnquesta, HashMap<String, String> respostes, HashMap<String, String> idsPreguntaPerResposta) 
            throws UsuariNoAutenticatException, EnquestaNoExisteixException, EnquestaJaContestadaException, 
                   PreguntaNoExisteixException, RespostaInvalidaException, ParametreInvalidException {
        
        // Validar paràmetres
        if (idEnquesta == null || idEnquesta.trim().isEmpty()) {
            throw new ParametreInvalidException("L'ID de l'enquesta no pot estar buit.");
        }
        if (respostes == null) {
            throw new ParametreInvalidException("El mapa de respostes no pot ser null.");
        }
        if (idsPreguntaPerResposta == null) {
            throw new ParametreInvalidException("El mapa d'idsPreguntaPerResposta no pot ser null.");
        }
        
        // Verificar que hi ha un usuari autenticat
        Usuari usuari = ctrlUsuari.getUsuariActual();
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
            
            if (idPregunta == null) {
                throw new ParametreInvalidException("No s'ha especificat idPregunta per la resposta " + idResposta);
            }
            
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
        
        // PASO 2: Si llegamos aquí, TODAS las respuestas son válidas → guardarlas
        for (HashMap.Entry<String, String> entry : respostes.entrySet()) {
            String idResposta = entry.getKey();
            String textResposta = entry.getValue();
            Pregunta pregunta = preguntesValidades.get(idResposta);
            String idPregunta = idsPreguntaPerResposta.get(idResposta);
            
            // Registrar la resposta (l'ID es genera automàticament dins)
            ctrlResposta.registrarResposta(idPregunta, textResposta, usuari, pregunta);
        }
        
        // PASO 3: Registrar participación
        ctrlEnquesta.registrarParticipacio(idEnquesta, usuari.getUsername());
    }
    
    /**
     * Modifica una resposta de l'usuari autenticat a una pregunta concreta.
     * Valida el format de la nova resposta segons el tipus de pregunta abans de modificar-la.
     * @param idEnquesta L'ID de l'enquesta.
     * @param idPregunta L'ID de la pregunta.
     * @param novaResposta La nova resposta.
     * @throws EnquestaNoExisteixException Si l'enquesta no existeix.
     * @throws PreguntaNoExisteixException Si la pregunta no existeix.
     * @throws RespostaNoExisteixException Si l'usuari no ha respost aquesta pregunta.
     * @throws RespostaInvalidaException Si la nova resposta no és vàlida pel tipus de pregunta.
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat.
     * @throws ParametreInvalidException Si algun paràmetre és invàlid.
     */
    //(jairo)
    public void modificarResposta(String idEnquesta, String idPregunta, String novaResposta) 
            throws EnquestaNoExisteixException, PreguntaNoExisteixException, RespostaNoExisteixException, 
                   RespostaInvalidaException, UsuariNoAutenticatException, ParametreInvalidException {
        
        // Validació 1: Verificar que hi ha un usuari autenticat
        Usuari usuariActual = ctrlUsuari.getUsuariActual();
        if (usuariActual == null) {
            throw new UsuariNoAutenticatException("Cal estar autenticat per modificar respostes.");
        }
        
        // Validació 2: Validar paràmetres
        if (idEnquesta == null || idEnquesta.trim().isEmpty()) {
            throw new ParametreInvalidException("L'ID de l'enquesta no pot estar buit.");
        }
        if (idPregunta == null || idPregunta.trim().isEmpty()) {
            throw new ParametreInvalidException("L'ID de la pregunta no pot estar buit.");
        }
        if (novaResposta == null) {
            throw new ParametreInvalidException("La nova resposta no pot ser null.");
        }
        
        // Validació 3: Verificar que la pregunta existeix en l'enquesta
        Pregunta pregunta = ctrlEnquesta.getPregunta(idEnquesta, idPregunta);
        if (pregunta == null) {
            throw new PreguntaNoExisteixException(idPregunta);
        }
        
        // Validació 4: Verificar que existeix la resposta de l'usuari autenticat a aquesta pregunta
        // Nota: getResposta() busca per username, per tant si existeix, sempre serà del mateix usuari
        Resposta respostaExistent = pregunta.getResposta(usuariActual.getUsername());
        if (respostaExistent == null) {
            throw new RespostaNoExisteixException(
                "L'usuari " + usuariActual.getUsername() + " no ha respost la pregunta " + idPregunta + " de l'enquesta " + idEnquesta
            );
        }
        
        // Validació 5: Validar el format de la nova resposta segons el tipus de pregunta
        if (!pregunta.validarResposta(novaResposta)) {
            throw new RespostaInvalidaException(
                "La nova resposta no és vàlida per la pregunta " + idPregunta + 
                " (tipus: " + pregunta.getTipus() + "): '" + novaResposta + "'"
            );
        }
        
        // Si totes les validacions passen, modificar la resposta directament
        ctrlResposta.modificarResposta(respostaExistent,novaResposta);
        
        // Nota: La persistència s'actualitza automàticament perquè l'objecte Resposta
        // es modifica directament i està guardat al HashMap de CtrlPersistencia
    }

    /**
     * Esborra totes les respostes de l'usuari autenticat a una enquesta.
     * Elimina totes les respostes associades a cada pregunta de l'enquesta i elimina
     * l'usuari de la llista de participants.
     * @param idEnquesta L'ID de l'enquesta.
     * @throws EnquestaNoExisteixException Si l'enquesta no existeix.
     * @throws RespostaNoExisteixException Si l'usuari no ha contestat aquesta enquesta.
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat.
     * @throws ParametreInvalidException Si l'ID de l'enquesta és invàlid.
     */
    //(jairo)
    public void esborrarResposta(String idEnquesta) 
            throws EnquestaNoExisteixException, RespostaNoExisteixException, UsuariNoAutenticatException, ParametreInvalidException {
        
        // Validació 1: Verificar que hi ha un usuari autenticat
        Usuari usuariActual = ctrlUsuari.getUsuariActual();
        if (usuariActual == null) {
            throw new UsuariNoAutenticatException("Cal estar autenticat per esborrar respostes.");
        }
        
        // Validació 2: Validar paràmetre
        if (idEnquesta == null || idEnquesta.trim().isEmpty()) {
            throw new ParametreInvalidException("L'ID de l'enquesta no pot estar buit.");
        }
        
        // Validació 3: Verificar que l'enquesta existeix
        Enquesta enquesta = ctrlEnquesta.getEnquesta(idEnquesta);
        if (enquesta == null) {
            throw new EnquestaNoExisteixException(idEnquesta);
        }
        
        // Validació 4: Verificar que l'usuari ha contestat aquesta enquesta
        if (!enquesta.haRespostUsuari(usuariActual.getUsername())) {
            throw new RespostaNoExisteixException(
                "L'usuari " + usuariActual.getUsername() + " no ha contestat l'enquesta " + idEnquesta
            );
        }
        
        // Obtenir totes les preguntes de l'enquesta
        ArrayList<Pregunta> preguntes = enquesta.getPreguntes();
        
        // Esborrar totes les respostes de l'usuari a cada pregunta de l'enquesta
        for (Pregunta pregunta : preguntes) {
            Resposta resposta = pregunta.getResposta(usuariActual.getUsername());
            
            if (resposta != null) {
                // DELEGATION: Delegar a CtrlResposta
                ctrlResposta.esborrarResposta(resposta, pregunta);
            }
        }
        
        // Eliminar l'usuari de la llista de participants de l'enquesta
        ctrlEnquesta.eliminarParticipacio(enquesta, usuariActual.getUsername());
    }

   

    // --- Casos de Uso: Consultes ---


    /**
     * Consulta la llista de totes les enquestes disponibles al sistema.
     * @return ArrayList amb totes les enquestes existents (mai null, pot estar buida)
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat
     */
    public ArrayList<Enquesta> consultarEnquestes() throws UsuariNoAutenticatException {
        // Validar que hi ha un usuari autenticat
        Usuari usuariActual = ctrlUsuari.getUsuariActual();
        if (usuariActual == null) {
            throw new UsuariNoAutenticatException("Cal estar autenticat per consultar les enquestes.");
        }
        
        // Obtenir i retornar la llista d'enquestes (mai null)
        return ctrlEnquesta.llistarEnquestes();
    }

    /**
     * Consulta una enquesta específica per ID.
     * @param idEnquesta L'ID de l'enquesta a consultar
     * @return L'enquesta trobada
     * @throws ParametreInvalidException Si l'ID és null o buit
     * @throws EnquestaNoExisteixException Si l'enquesta no existeix
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat
     */
    public Enquesta getEnquesta(String idEnquesta) throws ParametreInvalidException, EnquestaNoExisteixException, UsuariNoAutenticatException {
        // Validar que hi ha un usuari autenticat
        Usuari usuariActual = ctrlUsuari.getUsuariActual();
        if (usuariActual == null) {
            throw new UsuariNoAutenticatException("Cal estar autenticat per consultar una enquesta.");
        }
        
        // Validar paràmetres
        if (idEnquesta == null || idEnquesta.trim().isEmpty()) {
            throw new ParametreInvalidException("L'ID de l'enquesta no pot estar buit.");
        }
        
        // Buscar l'enquesta
        Enquesta enquesta = ctrlEnquesta.getEnquesta(idEnquesta);
        
        // Validar que existeix
        if (enquesta == null) {
            throw new EnquestaNoExisteixException(idEnquesta);
        }
        
        return enquesta;
    }

    
    /**
     * Consulta les respostes d'una pregunta específica.
     * @param idPregunta L'ID de la pregunta.
     * @return Un ArrayList amb totes les respostes de la pregunta (mai null, pot estar buit)
     * @throws ParametreInvalidException Si l'ID és null o buit
     * @throws PreguntaNoExisteixException Si la pregunta no existeix
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat
     */
    public ArrayList<Resposta> consultarRespostesPregunta(String idPregunta) 
            throws ParametreInvalidException, PreguntaNoExisteixException, UsuariNoAutenticatException {
        // Validar que hi ha un usuari autenticat
        Usuari usuariActual = ctrlUsuari.getUsuariActual();
        if (usuariActual == null) {
            throw new UsuariNoAutenticatException("Cal estar autenticat per consultar les respostes.");
        }
        
        // Validar paràmetres
        if (idPregunta == null || idPregunta.trim().isEmpty()) {
            throw new ParametreInvalidException("L'ID de la pregunta no pot estar buit.");
        }
        
        // Obtenir la pregunta de persistència
        Pregunta pregunta = ctrlPersistencia.getPregunta(idPregunta);
        if (pregunta == null) {
            throw new PreguntaNoExisteixException(idPregunta);
        }
        
        // Obtenir les respostes directament de la pregunta i retornar-les com ArrayList
        HashMap<String, Resposta> respostesMap = pregunta.getRespostes();
        return new ArrayList<>(respostesMap.values());
    }

    /**
     * Consulta totes les respostes d'una enquesta.
     * Per cada pregunta de l'enquesta, retorna totes les seves respostes.
     * @param idEnquesta L'ID de l'enquesta.
     * @return Un mapa amb idPregunta com a clau i totes les seves respostes com a valor (mai null, pot estar buit)
     * @throws ParametreInvalidException Si l'ID és null o buit
     * @throws EnquestaNoExisteixException Si l'enquesta no existeix
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat
     */
    public HashMap<String, ArrayList<Resposta>> consultarRespostesEnquesta(String idEnquesta) 
            throws ParametreInvalidException, EnquestaNoExisteixException, UsuariNoAutenticatException {
        // Validar que hi ha un usuari autenticat
        Usuari usuariActual = ctrlUsuari.getUsuariActual();
        if (usuariActual == null) {
            throw new UsuariNoAutenticatException("Cal estar autenticat per consultar les respostes.");
        }
        
        // Validar paràmetres
        if (idEnquesta == null || idEnquesta.trim().isEmpty()) {
            throw new ParametreInvalidException("L'ID de l'enquesta no pot estar buit.");
        }
        
        // Obtenir l'enquesta i validar que existeix
        Enquesta enquesta = ctrlEnquesta.getEnquesta(idEnquesta);
        if (enquesta == null) {
            throw new EnquestaNoExisteixException(idEnquesta);
        }
        
        HashMap<String, ArrayList<Resposta>> respostesPerPregunta = new HashMap<>();
        
        // Per cada pregunta de l'enquesta, obtenir totes les seves respostes
        for (Pregunta pregunta : enquesta.getPreguntes()) {
            String idPregunta = pregunta.getId();
            
            // Obtenir les respostes de la pregunta com ArrayList (ja fa còpia defensiva getRespostes())
            ArrayList<Resposta> llistaRespostes = new ArrayList<>(pregunta.getRespostes().values());
            respostesPerPregunta.put(idPregunta, llistaRespostes);
        }
        
        return respostesPerPregunta;
    }

    /**
     * Consulta les respostes pròpies de l'usuari actual a una enquesta específica.
     * Retorna només les respostes que l'usuari autenticat ha donat a les preguntes d'aquesta enquesta.
     * @param idEnquesta L'ID de l'enquesta
     * @return HashMap amb clau=idPregunta i valor=Resposta de l'usuari (mai null, pot estar buit)
     * @throws ParametreInvalidException Si l'ID és invàlid
     * @throws EnquestaNoExisteixException Si l'enquesta no existeix
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat
     */
    public HashMap<String, Resposta> consultarMevesRespostesEnquesta(String idEnquesta) 
            throws ParametreInvalidException, EnquestaNoExisteixException, UsuariNoAutenticatException {
        // Validar que hi ha un usuari autenticat
        Usuari usuariActual = ctrlUsuari.getUsuariActual();
        if (usuariActual == null) {
            throw new UsuariNoAutenticatException("Cal estar autenticat per consultar les teves respostes.");
        }
        
        // Validar paràmetres
        if (idEnquesta == null || idEnquesta.trim().isEmpty()) {
            throw new ParametreInvalidException("L'ID de l'enquesta no pot estar buit.");
        }
        
        // Obtenir l'enquesta i validar que existeix
        Enquesta enquesta = ctrlEnquesta.getEnquesta(idEnquesta);
        if (enquesta == null) {
            throw new EnquestaNoExisteixException(idEnquesta);
        }
        
        HashMap<String, Resposta> mevesRespostes = new HashMap<>();
        String username = usuariActual.getUsername();
        
        // Per cada pregunta de l'enquesta, obtenir la resposta de l'usuari si existeix
        for (Pregunta pregunta : enquesta.getPreguntes()) {
            Resposta resposta = pregunta.getResposta(username);
            if (resposta != null) {
                mevesRespostes.put(pregunta.getId(), resposta);
            }
        }
        
        return mevesRespostes;
    }

    /**
     * Registra una resposta per una pregunta d'una enquesta.
     * L'ID de la resposta es genera automàticament com: idPregunta + "_" + username
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
        
        // Registrar la resposta (l'ID es genera automàticament dins de CtrlResposta)
        ctrlResposta.registrarResposta(idPregunta, textResposta, usuari, pregunta);
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
    
    /**
     * Tanca la sessió de l'usuari actual.
     * Estableix l'usuari actual a null.
     */
    public void logout() {
        ctrlUsuari.logout();
    }
    
    public boolean checkPassword(String password) {
        return ctrlUsuari.checkPassword(password);
    }

    public void eliminarUsuari(String username) throws ParametreInvalidException {
        // Validar que el paràmetre no sigui nul o buit
        if (username == null || username.trim().isEmpty()) {
            throw new ParametreInvalidException("El nom d'usuari no pot estar buit.");
        }
        
        // Normalitzar el username (eliminar espais)
        String normalizedUsername = username.trim();

        Usuari usuariact = ctrlUsuari.getUsuariActual();

        if(usuariact != null && normalizedUsername.equals(usuariact.getUsername())) {
            ctrlUsuari.logout();
        }

        ctrlUsuari.eliminarUsuari(normalizedUsername);
    }

    public void crearPerfil(String id, String descripcio) {
        ctrlPerfil.crearPerfil(id, descripcio);
    }

    public Perfil getPerfil(String id) {
        return ctrlPerfil.getPerfil(id);
    }

    // --- Mètodes auxiliars ---

    /**
     * Obté l'usuari actualment autenticat.
     * @return L'usuari autenticat o null si no n'hi ha cap
     */
    public Usuari getUsuariActual() {
        return ctrlUsuari.getUsuariActual();
    }

    /**
     * Verifica si existeix una enquesta amb l'ID especificat.
     * @param idEnquesta L'ID de l'enquesta
     * @return true si existeix, false altrament
     */
    public boolean existeixEnquesta(String idEnquesta) {
        if (idEnquesta == null || idEnquesta.trim().isEmpty()) {
            return false;
        }
        return ctrlEnquesta.getEnquesta(idEnquesta) != null;
    }

    /**
     * Registra la participació d'un usuari en una enquesta.
     * @param idEnquesta L'ID de l'enquesta
     * @param username El nom d'usuari
     */
    public void registrarParticipacio(String idEnquesta, String username) {
        ctrlEnquesta.registrarParticipacio(idEnquesta, username);
    }

    // --- Anàlisi i Clustering ---

    /**
     * Encuentra el valor óptimo de k para clustering de una encuesta.
     * Vectoriza las respuestas y evalúa diferentes valores de k con Silhouette.
     * 
     * @param idEnquesta ID de la encuesta
     * @param kMin Valor mínimo de k a evaluar
     * @param kMax Valor máximo de k a evaluar
     * @param algoritmeNom Algoritmo a usar: "KMeans", "KMeans++", "KMedoids"
     * @param maxIters Máximo de iteraciones
     * @return Resultado con el mejor k y scores de Silhouette
     * @throws EnquestaNoExisteixException Si la encuesta no existe
     */
    public CtrlAnalisi.OptimalKResult trobarMillorK(String idEnquesta, int kMin, int kMax,
                                                     String algoritmeNom, int maxIters)
            throws EnquestaNoExisteixException {
        
        // 1. Obtenir l'enquesta
        Enquesta enquesta = ctrlEnquesta.getEnquesta(idEnquesta);
        if (enquesta == null) {
            throw new EnquestaNoExisteixException(idEnquesta);
        }
        
        // 2. Obtenir preguntes
        List<Pregunta> preguntes = enquesta.getPreguntes();
        if (preguntes.isEmpty()) {
            throw new IllegalStateException("L'enquesta no té preguntes per analitzar.");
        }
        
        // 3. Vectoritzar respostes
        HashMap<String, Resposta> totesRespostes = ctrlResposta.getTotesRespostes();
        List<String[]> dataVectors = new ArrayList<>();
        
        for (String username : ctrlPersistencia.getAllUsuaris().keySet()) {
            String[] vector = new String[preguntes.size()];
            boolean teRespostes = false;
            
            for (int i = 0; i < preguntes.size(); i++) {
                Pregunta p = preguntes.get(i);
                String clauResposta = p.getId() + "_" + username;
                Resposta resposta = totesRespostes.get(clauResposta);
                
                if (resposta != null) {
                    vector[i] = resposta.getTextResposta();
                    teRespostes = true;
                } else {
                    vector[i] = "";
                }
            }
            
            if (teRespostes) {
                dataVectors.add(vector);
            }
        }
        
        if (dataVectors.size() < kMin) {
            throw new IllegalStateException("No hi ha prou participants (" + dataVectors.size() + 
                                          ") per evaluar k=" + kMin);
        }
        
        // 4. Construir FeatureSpecs
        DistanceCalculator.FeatureSpec[] specs = ctrlAnalisi.buildSpecsFromPreguntas(preguntes);
        
        // 5. Delegar búsqueda de k óptimo a CtrlAnalisi
        return ctrlAnalisi.findOptimalK(dataVectors, kMin, kMax, algoritmeNom, maxIters, specs);
    }

    /**
     * Selecciona un valor de k aleatorio para una encuesta.
     * 
     * @param idEnquesta ID de la encuesta
     * @return Valor de k aleatorio
     * @throws EnquestaNoExisteixException Si la encuesta no existe
     */
    public int escollirKAleatori(String idEnquesta) throws EnquestaNoExisteixException {
        // Obtenir nombre de participants
        Enquesta enquesta = ctrlEnquesta.getEnquesta(idEnquesta);
        if (enquesta == null) {
            throw new EnquestaNoExisteixException(idEnquesta);
        }
        
        int numParticipants = enquesta.getParticipants().size();
        return ctrlAnalisi.selectRandomK(numParticipants);
    }

    /**
     * Suggereix un rang recomanat de valors per a k basat en el nombre de participants.
     * 
     * @param numParticipants Nombre de participants de l'enquesta
     * @return Array de dos elements [kMin, kMax] amb el rang recomanat
     */
    public int[] suggestKRange(int numParticipants) {
        return ctrlAnalisi.suggestKRange(numParticipants);
    }

    /**
     * Realitza clustering sobre els usuaris que han respost una enquesta.
     * Els perfils generats s'assignen automàticament als usuaris i es persisten.
     * 
     * @param idEnquesta ID de l'enquesta a analitzar
     * @param k Nombre de clusters
     * @param usePlusPlus true per usar KMeans++, false per KMeans estàndard (ignorat si algoritmeNom és especificat)
     * @param maxIters Màxim d'iteracions
     * @param algoritmeNom Nom de l'algoritme: "KMeans", "KMeans++", "KMedoids"
     * @return Resultats del clustering amb clusters, silhouette i perfils assignats
     * @throws EnquestaNoExisteixException Si l'enquesta no existeix
     */
    //(jairo)
    public ResultatClustering analitzarEnquesta(String idEnquesta, int k, boolean usePlusPlus, 
                                                 int maxIters, String algoritmeNom) 
            throws EnquestaNoExisteixException {
        
        // 1. Obtenir l'enquesta
        Enquesta enquesta = ctrlEnquesta.getEnquesta(idEnquesta);
        if (enquesta == null) {
            throw new EnquestaNoExisteixException(idEnquesta);
        }
        
        // 2. Obtenir preguntes
        List<Pregunta> preguntes = enquesta.getPreguntes();
        if (preguntes.isEmpty()) {
            throw new IllegalStateException("L'enquesta no té preguntes per analitzar.");
        }
        
        // 3. Vectoritzar respostes (obtenir usuaris que han respost)
        HashMap<String, Resposta> totesRespostes = ctrlResposta.getTotesRespostes();
        List<String> usernames = new ArrayList<>();
        List<String[]> dataVectors = new ArrayList<>();
        HashMap<String, Integer> vectorToIndex = new HashMap<>(); // Mapea contingut vector -> índex
        
        for (String username : ctrlPersistencia.getAllUsuaris().keySet()) {
            String[] vector = new String[preguntes.size()];
            boolean teRespostes = false;
            
            for (int i = 0; i < preguntes.size(); i++) {
                Pregunta p = preguntes.get(i);
                // CORREGIT: la clau és idPregunta_username (no username_idPregunta)
                String clauResposta = p.getId() + "_" + username;
                Resposta resposta = totesRespostes.get(clauResposta);
                
                if (resposta != null) {
                    vector[i] = resposta.getTextResposta();
                    teRespostes = true;
                } else {
                    vector[i] = ""; // Valor buit si no ha respost
                }
            }
            
            if (teRespostes) {
                int idx = dataVectors.size();
                usernames.add(username);
                dataVectors.add(vector);
                // Crear clau única pel vector per identificar-lo després
                String vectorKey = String.join("|", vector);
                vectorToIndex.put(vectorKey, idx);
            }
        }
        
        if (dataVectors.size() < k) {
            throw new IllegalStateException("No hi ha prou participants (" + dataVectors.size() + 
                                          ") per crear " + k + " clusters.");
        }
        
        // 4. Construir FeatureSpecs des de les preguntes
        DistanceCalculator.FeatureSpec[] specs = ctrlAnalisi.buildSpecsFromPreguntas(preguntes);
        
        // 5. Executar clustering amb l'algoritme especificat
        List<Kluster> clusters;
        if (algoritmeNom != null && (algoritmeNom.equalsIgnoreCase("KMedoids") || 
                                     algoritmeNom.equalsIgnoreCase("K-Medoids"))) {
            clusters = ctrlAnalisi.clusterWithAlgorithm(dataVectors, k, "KMedoids", maxIters, specs);
        } else {
            clusters = ctrlAnalisi.cluster(dataVectors, k, usePlusPlus, maxIters, specs);
        }
        
        // 6. Calcular Silhouette
        ClusterEvaluator evaluator = new ClusterEvaluator();
        double silhouette = evaluator.silhouetteScore(clusters, specs);
        double[] silhouettePerCluster = evaluator.silhouettePerCluster(clusters, specs);
        
        // 7. Crear i assignar perfils
        List<String> preguntesText = new ArrayList<>();
        for (Pregunta p : preguntes) {
            preguntesText.add(p.getText());
        }
        
        int perfilIdBase = (int) System.currentTimeMillis();
        
        for (int i = 0; i < clusters.size(); i++) {
            Kluster cluster = clusters.get(i);
            List<String[]> members = cluster.getMembers();
            String[] centroid = cluster.getCentroid();
            
            // Generar nom del cluster
            String clusterNom = generarNomCluster(i + 1, centroid, preguntes);
            
            // Crear perfil per aquest cluster
            Perfil perfilCluster = new Perfil(
                perfilIdBase + i,
                "Perfil generat per clustering: " + clusterNom,
                idEnquesta,
                i,
                clusterNom,
                members.size(),
                silhouettePerCluster[i],
                centroid,
                preguntesText,
                algoritmeNom
            );
            
            // Assignar perfil a cada usuari del cluster
            for (String[] memberVector : members) {
                // Buscar índex pel contingut del vector
                String vectorKey = String.join("|", memberVector);
                Integer memberIdx = vectorToIndex.get(vectorKey);
                
                if (memberIdx != null && memberIdx >= 0 && memberIdx < usernames.size()) {
                    String username = usernames.get(memberIdx);
                    Usuari usuari = ctrlPersistencia.getUsuari(username);
                    if (usuari != null) {
                        usuari.assignarPerfil(idEnquesta, perfilCluster);
                        // Els usuaris ja es guarden en la persistència
                    }
                }
            }
        }
        
        // 8. Retornar resultats
        return new ResultatClustering(clusters, silhouette, silhouettePerCluster, usernames, dataVectors, vectorToIndex);
    }
    
    /**
     * Genera un nom descriptiu per a un cluster basat en el seu centroide.
     */
    private String generarNomCluster(int index, String[] centroid, List<Pregunta> preguntes) {
        if (preguntes.isEmpty() || centroid.length == 0) {
            return "Cluster " + index;
        }
        
        // Buscar la primera pregunta significativa
        for (int i = 0; i < Math.min(preguntes.size(), centroid.length); i++) {
            Pregunta p = preguntes.get(i);
            String valor = centroid[i];
            
            if (valor != null && !valor.trim().isEmpty()) {
                switch (p.getTipus()) {
                    case NUMERICA:
                        try {
                            double num = Double.parseDouble(valor);
                            if (num < 15) return "Grup Baix";
                            else if (num < 30) return "Grup Mitjà";
                            else return "Grup Alt";
                        } catch (NumberFormatException e) {
                            // Continuar amb la següent pregunta
                        }
                        break;
                    case QUALITATIVA_ORDENADA:
                        return "Grup " + valor;
                    case QUALITATIVA_NO_ORDENADA_SIMPLE:
                    case QUALITATIVA_NO_ORDENADA_MULTIPLE:
                        String[] opcions = valor.split(",");
                        if (opcions.length > 0) {
                            return "Grup " + opcions[0].trim();
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        
        return "Cluster " + index;
    }
    
    /**
     * Classe auxiliar per retornar els resultats del clustering.
     */
    public static class ResultatClustering {
        public final List<Kluster> clusters;
        public final double silhouetteGlobal;
        public final double[] silhouettePerCluster;
        public final List<String> usernames;
        public final List<String[]> dataVectors;
        public final HashMap<String, Integer> vectorToIndex;
        
        public ResultatClustering(List<Kluster> clusters, double silhouetteGlobal, 
                                 double[] silhouettePerCluster, List<String> usernames, 
                                 List<String[]> dataVectors, HashMap<String, Integer> vectorToIndex) {
            this.clusters = clusters;
            this.silhouetteGlobal = silhouetteGlobal;
            this.silhouettePerCluster = silhouettePerCluster;
            this.usernames = usernames;
            this.dataVectors = dataVectors;
            this.vectorToIndex = vectorToIndex;
        }
    }
}


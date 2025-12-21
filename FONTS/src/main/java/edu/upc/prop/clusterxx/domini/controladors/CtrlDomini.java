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
import edu.upc.prop.clusterxx.domini.classes.Exceptions.PerfilNoTrobatException;
import edu.upc.prop.clusterxx.domini.classes.Exceptions.AnalisiNoRealitzatException;
import edu.upc.prop.clusterxx.domini.classes.Opcio;
import edu.upc.prop.clusterxx.domini.classes.Perfil;
import edu.upc.prop.clusterxx.domini.classes.Pregunta;
import edu.upc.prop.clusterxx.domini.classes.Resposta;
import edu.upc.prop.clusterxx.domini.classes.TipusPregunta;
import edu.upc.prop.clusterxx.domini.classes.Usuari;
import edu.upc.prop.clusterxx.domini.classes.Kluster;
import edu.upc.prop.clusterxx.domini.classes.DistanceCalculator;
import edu.upc.prop.clusterxx.domini.classes.ClusterEvaluator;

import org.json.JSONArray;

import edu.upc.prop.clusterxx.persistencia.CtrlPersistencia;

/**
 * Controlador principal del domini que coordina totes les operacions del
 * sistema.
 * Actua com a façana entre la capa de presentació i els controladors
 * específics,
 * delegant les peticions als controladors corresponents (enquestes, respostes,
 * usuaris, perfils i anàlisi).
 * Gestiona la lògica de negoci complexa que implica múltiples controladors.
 */
public class CtrlDomini {
    private CtrlEnquesta ctrlEnquesta;
    private CtrlResposta ctrlResposta;
    private CtrlUsuari ctrlUsuari;
    private CtrlPerfil ctrlPerfil;
    private CtrlAnalisi ctrlAnalisi;
    private CtrlPersistencia ctrlPersistencia;

    public CtrlDomini() {
        this.ctrlEnquesta = new CtrlEnquesta();
        this.ctrlResposta = new CtrlResposta();
        this.ctrlUsuari = new CtrlUsuari(null);
        this.ctrlPerfil = new CtrlPerfil();
        this.ctrlAnalisi = new CtrlAnalisi();
        this.ctrlPersistencia = CtrlPersistencia.getInstance();
    }

    // --- Casos d'ús: Gestió d'Enquestes ---

    /**
     * Crea una nova enquesta associada a un usuari autenticat.
     * Aquest mètode crea una nova enquesta al sistema amb l'ID, títol i descripció
     * especificats.
     * L'enquesta queda associada a l'usuari autenticat que la crea, qui serà el seu
     * propietari
     * i l'únic amb permisos per modificar-la o eliminar-la.
     * 
     * Validacions realitzades:
     * L'usuari no pot ser null
     * L'ID de l'enquesta no pot estar buit
     * El títol de l'enquesta no pot estar buit
     * L'enquesta no pot existir prèviament amb el mateix ID
     * Ha d'haver-hi un usuari autenticat al sistema
     * 
     * @param usuari     L'usuari creador de l'enquesta
     * @param id         L'identificador únic de la nova enquesta
     * @param titol      El títol descriptiu de l'enquesta
     * @param descripcio La descripció detallada de l'enquesta (pot estar buida però
     *                   no null)
     * @throws ParametreInvalidException   Si algun paràmetre és null o buit (ID o
     *                                     títol)
     * @throws EnquestaJaExisteixException Si ja existeix una enquesta amb aquest ID
     *                                     al sistema
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat o
     *                                     l'usuari és null
     * @see CtrlEnquesta#crearEnquesta(String, String, String, Usuari)
     */

    public void crearEnquesta(String id, String titol, String descripcio)
            throws ParametreInvalidException, EnquestaJaExisteixException, UsuariNoAutenticatException {

        Usuari usuari = ctrlUsuari.getUsuariActual();

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
        if (usuari == null) {
            throw new UsuariNoAutenticatException("Cal estar autenticat per crear una enquesta.");
        }

        // Si totes les validacions passen, crear l'enquesta
        ctrlEnquesta.crearEnquesta(id, titol, descripcio, usuari);
    }

    /**
     * Esborra una enquesta existent del sistema.
     * 
     * Aquest mètode elimina completament una enquesta del sistema, juntament amb
     * totes les seves
     * dades associades (preguntes, respostes i participacions). Només el creador de
     * l'enquesta
     * té permís per esborrar-la. L'eliminació és irreversible i comporta la pèrdua
     * de totes
     * les dades relacionades amb l'enquesta.
     * 
     * Validacions realitzades:
     * - Ha d'haver-hi un usuari autenticat al sistema
     * - L'enquesta ha d'existir al sistema
     * - L'usuari autenticat ha de ser el creador de l'enquesta
     * 
     * Eliminació en cascada: El mètode segueix aquest ordre d'eliminació per
     * garantir
     * la integritat referencial:
     * 1. Per cada pregunta de l'enquesta:
     * - Elimina totes les respostes associades a la pregunta del sistema de
     * persistència
     * - Elimina la pregunta del sistema de persistència global
     * 2. Elimina l'enquesta de la llista d'enquestes creades de l'usuari creador
     * 3. Elimina l'enquesta del sistema de persistència central
     * 
     * Nota: Aquest mètode elimina totes les participacions i respostes dels usuaris
     * que han
     * contestat l'enquesta. Aquesta acció no es pot desfer.
     * 
     * @param id L'identificador únic de l'enquesta a esborrar
     * @throws EnquestaNoExisteixException Si no existeix cap enquesta amb l'ID
     *                                     especificat
     * @throws PermisDenegatException      Si l'usuari autenticat no és el creador
     *                                     de l'enquesta
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat al
     *                                     sistema
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
            ctrlPersistencia.eliminarPregunta(id, pregunta.getId());
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
     * Aquest mètode permet canviar el títol d'una enquesta ja creada. Només el
     * creador
     * de l'enquesta té permís per modificar-ne el títol. El mètode valida que
     * l'enquesta
     * existeix, que l'usuari està autenticat, i que té els permisos necessaris
     * abans
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
     * @param nouTitol   El nou títol que es vol assignar a l'enquesta (no pot estar
     *                   buit)
     * @throws ParametreInvalidException   Si l'ID de l'enquesta o el nou títol són
     *                                     null o buits
     * @throws EnquestaNoExisteixException Si no existeix cap enquesta amb l'ID
     *                                     especificat
     * @throws PermisDenegatException      Si l'usuari autenticat no és el creador
     *                                     de l'enquesta
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat al
     *                                     sistema
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
     * Obté les dades d'una pregunta específica en format cru (sense objectes de
     * domini).
     * 
     * Retorna una llista amb:
     * [0] ID (String)
     * [1] Text (String)
     * [2] Tipus (String)
     * [3] Min (Double) - pot ser null
     * [4] Max (Double) - pot ser null
     * [5] Opcions (ArrayList<String>) - pot ser null/buit
     * [6] MaxSeleccions (Integer)
     * 
     * @param idEnquesta ID de l'enquesta
     * @param idPregunta ID de la pregunta
     * @return ArrayList amb les dades o null si no troba la pregunta
     */
    public ArrayList<Object> getDadesPregunta(String idEnquesta, String idPregunta) {
        try {
            Enquesta enquesta = ctrlEnquesta.getEnquesta(idEnquesta);
            if (enquesta == null)
                return null;

            Pregunta p = enquesta.getPregunta(idPregunta);
            if (p == null)
                return null;

            ArrayList<Object> dades = new ArrayList<>();
            dades.add(p.getId());
            dades.add(p.getText());
            dades.add(p.getTipus().toString());
            dades.add(p.getValorMinim());
            dades.add(p.getValorMaxim());

            ArrayList<String> opcionsStr = new ArrayList<>();
            if (p.getOpcions() != null) {
                for (Opcio o : p.getOpcions()) {
                    opcionsStr.add(o.getText());
                }
            }
            dades.add(opcionsStr);

            dades.add(p.getMaxSeleccions());

            return dades;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Obté totes les enquestes del sistema en format cru.
     * 
     * @return Llista de llistes amb [id, titol].
     */
    public ArrayList<ArrayList<String>> getAllEnquestesRaw() {
        ArrayList<ArrayList<String>> resultat = new ArrayList<>();
        try {
            ArrayList<Enquesta> enquestes = ctrlEnquesta.llistarEnquestes();
            for (Enquesta e : enquestes) {
                ArrayList<String> dadesEnquesta = new ArrayList<>();
                dadesEnquesta.add(e.getId());
                dadesEnquesta.add(e.getTitol());
                resultat.add(dadesEnquesta);
            }
        } catch (Exception e) {
            // Retorna llista buida si hi ha error
        }
        return resultat;
    }

    /**
     * Obté les enquestes de l'usuari actual en format cru.
     * 
     * @return Llista de llistes amb [id, titol, descripcio].
     */
    public ArrayList<ArrayList<String>> getEnquestesUsuariRaw() {
        ArrayList<ArrayList<String>> resultat = new ArrayList<>();
        try {
            List<Enquesta> enquestes = ctrlUsuari.getUsuariActual().getEnquestesCreades();
            for (Enquesta e : enquestes) {
                ArrayList<String> dadesEnquesta = new ArrayList<>();
                dadesEnquesta.add(e.getId());
                dadesEnquesta.add(e.getTitol());
                dadesEnquesta.add(e.getDescripcio());
                resultat.add(dadesEnquesta);
            }
        } catch (Exception e) {
            // Retorna llista buida si error
        }
        return resultat;
    }

    /**
     * Obté els participants d'una enquesta en format cru.
     * 
     * @param idEnquesta ID de l'enquesta.
     * @return Llista de noms d'usuari dels participants.
     */
    public ArrayList<String> getParticipantsEnquestaRaw(String idEnquesta) {
        ArrayList<String> participants = new ArrayList<>();
        try {
            Enquesta enquesta = ctrlEnquesta.getEnquesta(idEnquesta);
            if (enquesta != null) {
                for (String p : enquesta.getParticipants()) {
                    participants.add(p);
                }
            }
        } catch (Exception e) {
            // Retorna llista buida si error
        }
        return participants;
    }

    /**
     * Obté les preguntes d'una enquesta en format cru.
     * 
     * Retorna una llista on cada element és una llista amb:
     * [0] ID (String)
     * [1] Text (String)
     * [2] Tipus (String)
     * [3] Min (Double)
     * [4] Max (Double)
     * [5] Opcions (ArrayList<ArrayList<String>> -> [[id, text], ...])
     * [6] MaxSeleccions (Integer)
     * 
     * @param idEnquesta ID de l'enquesta
     * @return Llista de preguntes en format cru
     */
    public ArrayList<ArrayList<Object>> getPreguntesEnquestaRaw(String idEnquesta) {
        ArrayList<ArrayList<Object>> resultat = new ArrayList<>();
        try {
            Enquesta enquesta = ctrlEnquesta.getEnquesta(idEnquesta);
            if (enquesta != null) {
                for (Pregunta p : enquesta.getPreguntes()) {
                    ArrayList<Object> dadesP = new ArrayList<>();
                    dadesP.add(p.getId());
                    dadesP.add(p.getText());
                    dadesP.add(p.getTipus().toString());
                    dadesP.add(p.getValorMinim());
                    dadesP.add(p.getValorMaxim());

                    ArrayList<ArrayList<String>> opcions = new ArrayList<>();
                    if (p.getOpcions() != null) {
                        for (Opcio o : p.getOpcions()) {
                            ArrayList<String> dadaOpcio = new ArrayList<>();
                            dadaOpcio.add(String.valueOf(o.getId()));
                            dadaOpcio.add(o.getText());
                            opcions.add(dadaOpcio);
                        }
                    }
                    dadesP.add(opcions);
                    dadesP.add(p.getMaxSeleccions());

                    resultat.add(dadesP);
                }
            }
        } catch (Exception e) {
            // Retornar buit si error
        }
        return resultat;
    }

    /**
     * Obté les respostes d'un usuari a una enquesta en format cru.
     * 
     * @param idEnquesta ID de l'enquesta.
     * @param username   Nom de l'usuari.
     * @return Map amb idPregunta -> textResposta.
     */
    public HashMap<String, String> getRespostesUsuariEnquestaRaw(String idEnquesta, String username) {
        HashMap<String, String> respostesUsuari = new HashMap<>();
        try {
            Enquesta enquesta = ctrlEnquesta.getEnquesta(idEnquesta);
            if (enquesta != null && username != null) {
                for (Pregunta p : enquesta.getPreguntes()) {
                    if (p.teResposta(username)) {
                        respostesUsuari.put(p.getId(), p.getResposta(username).getTextResposta());
                    }
                }
            }
        } catch (Exception e) {
            // Retorna map buit si error
        }
        return respostesUsuari;
    }

    /**
     * Obté les enquestes contestades per un usuari en format cru.
     * 
     * @param username Nom de l'usuari.
     * @return Llista d'enquestes (ID, Títol).
     */
    public ArrayList<ArrayList<String>> getEnquestesContestadesRaw(String username) {
        ArrayList<ArrayList<String>> resultat = new ArrayList<>();
        try {
            if (username != null) {
                ArrayList<Enquesta> totes = ctrlEnquesta.llistarEnquestes();
                for (Enquesta e : totes) {
                    if (e.haRespostUsuari(username)) {
                        ArrayList<String> dadesEnquesta = new ArrayList<>();
                        dadesEnquesta.add(e.getId());
                        dadesEnquesta.add(e.getTitol());
                        resultat.add(dadesEnquesta);
                    }
                }
            }
        } catch (Exception e) {
            // Retorna llista buida si error
        }
        return resultat;
    }

    /**
     * Genera un informe d'anàlisi de clustering per a una enquesta.
     * 
     * @param idEnquesta ID de l'enquesta
     * @param modeK      Mode de selecció de K: "manual", "aleatori" o "automatic"
     * @param kManual    Valor de K (només si modeK = "manual")
     * @param algoritme  Algoritme: "kmeans", "kmeans++" o "kmedoids"
     * @return Text amb els resultats de l'anàlisi
     */
    public String generarInformeAnalisi(String idEnquesta, String modeK, int kManual, String algoritme) {
        try {
            int k = 0;
            String detallsK = "";

            // Obtenir el número de participants
            Enquesta enquesta = ctrlEnquesta.getEnquesta(idEnquesta);
            if (enquesta == null)
                return "Error: L'enquesta no existeix.";
            int numParticipants = enquesta.getNumParticipants();

            // Determinar el valor de K segons el mode
            if (modeK.equals("manual")) {
                k = kManual;
                detallsK = "K seleccionat manualment: " + k;
            } else if (modeK.equals("aleatori")) {
                k = this.escollirKAleatori(idEnquesta);
                detallsK = "K escollit aleatòriament: " + k;
            } else { // automatic
                int kMax = Math.min(10, numParticipants);
                if (kMax < 2) {
                    return "Error: Es necessiten almenys 2 participants per fer l'anàlisi automàtic.";
                }

                CtrlAnalisi.OptimalKResult optResult = this.trobarMillorK(idEnquesta, 2, kMax, algoritme, 100);
                k = optResult.bestK;
                detallsK = "K òptim trobat (Silhouette): " + k + " (coeficient: " +
                        String.format("%.4f", optResult.bestSilhouette) + ")";
            }

            // Executar l'anàlisi (utilitzant el mètode existent que ja gestiona
            // persistència)
            boolean usePlusPlus = algoritme.equals("kmeans++");
            ResultatClustering resultat = this.analitzarEnquesta(idEnquesta, k, usePlusPlus, 100, algoritme);

            // Formatar resultats
            StringBuilder sb = new StringBuilder();
            sb.append("=== RESULTATS DE L'ANÀLISI ===\n\n");
            sb.append("Enquesta: ").append(idEnquesta).append("\n");
            sb.append("Algoritme: ").append(algoritme.toUpperCase()).append("\n");
            sb.append(detallsK).append("\n");
            sb.append("Iteracions màximes: 100\n\n");

            sb.append("--- MÈTRIQUES GLOBALS ---\n");
            sb.append("Coeficient de Silhouette global: ").append(String.format("%.4f", resultat.silhouetteGlobal))
                    .append("\n\n");

            sb.append("--- CLUSTERS TROBATS ---\n");
            for (int i = 0; i < resultat.clusters.size(); i++) {
                Kluster cluster = resultat.clusters.get(i);
                sb.append("Cluster ").append(i).append(":\n");
                sb.append("  Mida: ").append(cluster.size()).append(" usuaris\n");
                sb.append("  Silhouette: ").append(String.format("%.4f", resultat.silhouettePerCluster[i]))
                        .append("\n");

                // Obtenir representant
                String usernameRep = resultat.getUsernameRepresentant(i);
                if (usernameRep != null) {
                    sb.append("  Representant: ").append(usernameRep).append("\n");

                    // Obtenir perfil del representant
                    try {
                        Perfil perfil = getPerfil(usernameRep); // Utilitzar mètode que ja tenim (wrapper de ctrlPerfil)
                        if (perfil == null) {
                            // Si no el trobem per ID (que sembla ser username), busquem si l'usuari té
                            // perfils assignats
                            // Però el metode getPerfil(id) potser espera un ID de perfil, no un username.
                            // Revisem: public Perfil getPerfil(String id) { return
                            // ctrlPerfil.getPerfil(id); }
                            // Normalment els perfils tenen un ID numèric o string.
                            // En la lògica de generació (linia 2837), el perfil es crea amb IDs basats en
                            // timestamp
                            // i s'assigna a l'usuari.
                            // 'usuari.assignarPerfil(perfil)'.

                            // Mirem com obtenir la descripció del perfil assignat a l'usuari per aquesta
                            // enquesta.
                            Usuari u = ctrlUsuari.getUsuari(usernameRep);
                            if (u != null) {
                                Perfil pAssignat = u.getPerfil(idEnquesta);
                                if (pAssignat != null) {
                                    sb.append("  Descripció perfil: ").append(pAssignat.getDescripcion()).append("\n");
                                }
                            }
                        } else {
                            sb.append("  Descripció perfil: ").append(perfil.getDescripcion()).append("\n");
                        }
                    } catch (Exception ignored) {
                    }
                }
                sb.append("\n");
            }

            return sb.toString();

        } catch (Exception e) {
            return "Error en l'anàlisi: " + e.getMessage();
        }
    }

    /**
     * Modifica la descripció d'una enquesta existent.
     * 
     * Aquest mètode permet canviar la descripció d'una enquesta ja creada. Només el
     * creador
     * de l'enquesta té permís per modificar-ne la descripció. El mètode valida que
     * l'enquesta
     * existeix, que l'usuari està autenticat, i que té els permisos necessaris
     * abans
     * d'aplicar els canvis.
     * 
     * Validacions realitzades:
     * Ha d'haver-hi un usuari autenticat al sistema
     * L'ID de l'enquesta no pot estar buit
     * La nova descripció no pot ser null (però pot estar buida)
     * L'enquesta ha d'existir al sistema
     * L'usuari autenticat ha de ser el creador de l'enquesta
     * 
     * 
     * @param idEnquesta     L'identificador únic de l'enquesta a modificar
     * @param novaDescripcio La nova descripció que es vol assignar a l'enquesta
     * @throws ParametreInvalidException   Si l'ID de l'enquesta és null/buit o si
     *                                     la nova descripció és null
     * @throws EnquestaNoExisteixException Si no existeix cap enquesta amb l'ID
     *                                     especificat
     * @throws PermisDenegatException      Si l'usuari autenticat no és el creador
     *                                     de l'enquesta
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat al
     *                                     sistema
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
     * 
     * Aquest mètode permet afegir una pregunta a una enquesta prèviament creada.
     * Només el creador
     * de l'enquesta té permís per afegir-hi preguntes. La pregunta s'afegeix amb
     * tota la seva
     * configuració (tipus, opcions, validacions) i queda disponible per ser
     * contestada pels usuaris.
     * 
     * 
     * Validacions realitzades:
     *
     * Ha d'haver-hi un usuari autenticat al sistema
     * L'ID de l'enquesta no pot estar buit
     * La pregunta no pot ser null
     * L'ID de la pregunta no pot estar buit
     * El text de la pregunta no pot estar buit
     * L'enquesta ha d'existir al sistema
     * La pregunta no pot existir ja a l'enquesta (ID únic)
     * L'usuari autenticat ha de ser el creador de l'enquesta
     * L'enquesta NO pot tenir participacions prèvies (respostes d'usuaris)
     * 
     * Restricció crítica: No es poden afegir preguntes a una enquesta que ja té
     * respostes d'usuaris. Això crearia inconsistència perquè els participants
     * anteriors haurien
     * contestat amb menys preguntes que els nous participants.
     * 
     * @param idEnquesta L'identificador únic de l'enquesta on s'afegirà la pregunta
     * @param p          L'objecte Pregunta a afegir, amb tot el seu contingut
     *                   (text, tipus, opcions, etc.)
     * @throws ParametreInvalidException   Si algun paràmetre és null o buit
     *                                     (idEnquesta, pregunta, ID o text)
     * @throws EnquestaNoExisteixException Si no existeix cap enquesta amb l'ID
     *                                     especificat
     * @throws PermisDenegatException      Si l'usuari autenticat no és el creador
     *                                     de l'enquesta
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat al
     *                                     sistema
     * @throws PreguntaJaExisteixException Si ja existeix una pregunta amb aquest ID
     *                                     a l'enquesta
     * @throws RespostaInvalidaException   Si l'enquesta ja té
     *                                     respostes/participacions d'usuaris
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

        // Validar que si és qualitativa tingui almenys 2 opcions
        if (p.tipusAdmetOpcions() && p.getOpcions().size() < 2) {
            throw new ParametreInvalidException("Les preguntes qualitatives han de tenir almenys 2 opcions.");
        }

        // Validar que maxSeleccions no superi el nombre d'opcions per a preguntes
        // múltiples
        if (p.getTipus() == TipusPregunta.QUALITATIVA_NO_ORDENADA_MULTIPLE
                && p.getMaxSeleccions() > p.getOpcions().size()) {
            throw new ParametreInvalidException(
                    "El nombre màxim de seleccions no pot ser superior al nombre d'opcions.");
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
     * Aquest mètode elimina una pregunta d'una enquesta prèviament creada,
     * juntament amb
     * totes les respostes associades a aquesta pregunta. Només el creador de
     * l'enquesta
     * té permís per eliminar-ne preguntes. L'eliminació és irreversible i comporta
     * la
     * pèrdua de totes les dades de resposta relacionades.
     * 
     * Validacions realitzades:
     * 
     * Ha d'haver-hi un usuari autenticat al sistema
     * L'ID de l'enquesta no pot estar buit
     * L'ID de la pregunta no pot estar buit
     * L'enquesta ha d'existir al sistema
     * La pregunta ha d'existir a l'enquesta especificada
     * L'usuari autenticat ha de ser el creador de l'enquesta
     * Eliminació en cascada: Abans d'eliminar la pregunta, el mètode
     * elimina automàticament totes les respostes associades a aquesta pregunta de:
     * Les respostes locals de la pregunta
     * El sistema de persistència global
     * Els perfils dels usuaris que van respondre
     * Nota: Es pot eliminar una pregunta fins i tot si té respostes d'usuaris, però
     * aquestes respostes es perdran definitivament. A diferència d'afegir
     * preguntes,
     * eliminar-les no crea inconsistències perquè els participants mantenen el
     * mateix
     * conjunt de preguntes després de l'eliminació.
     * 
     * @param idEnquesta L'identificador únic de l'enquesta que conté la pregunta
     * @param idPregunta L'identificador únic de la pregunta a eliminar
     * @throws ParametreInvalidException   Si l'ID de l'enquesta o de la pregunta
     *                                     són null o buits
     * @throws EnquestaNoExisteixException Si no existeix cap enquesta amb l'ID
     *                                     especificat
     * @throws PreguntaNoExisteixException Si la pregunta no existeix a l'enquesta
     *                                     especificada
     * @throws PermisDenegatException      Si l'usuari autenticat no és el creador
     *                                     de l'enquesta
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat al
     *                                     sistema
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

        // Verificar que la encuesta no tiene respuestas
        HashMap<String, Resposta> respostesPregunta = pregunta.getRespostes();
        if (!respostesPregunta.isEmpty()) {
            throw new IllegalStateException(
                    "No es pot eliminar la pregunta perquè l'enquesta ja té respostes. " +
                            "Aquesta pregunta té " + respostesPregunta.size() + " resposta(es).");
        }

        // IMPORTANT: Eliminar totes les respostes associades a aquesta pregunta abans
        // d'eliminar-la
        // Crear una llista temporal per evitar ConcurrentModificationException
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
     * Aquest mètode permet canviar el contingut d'una pregunta ja creada (text,
     * tipus, opcions, etc.).
     * Només el creador de l'enquesta té permís per modificar-ne les preguntes. La
     * pregunta modificada
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
     * - L'ID de la nova pregunta ha de coincidir amb l'ID de la pregunta a
     * modificar
     * - La pregunta NO pot tenir cap resposta d'usuari associada
     * 
     * Restricció crítica: Només es pot modificar una pregunta si NO té cap resposta
     * associada. Si la pregunta ja té respostes d'usuaris, qualsevol modificació
     * podria invalidar
     * aquestes respostes o canviar-ne el significat. Per exemple, canviar una
     * pregunta numèrica a
     * qualitativa invalidaria respostes numèriques existents.
     * 
     * Alternativa: Si necessites modificar una pregunta que ja té respostes, has
     * de:
     * 1. Eliminar la pregunta existent (això eliminarà també les seves respostes)
     * 2. Crear una nova pregunta amb el contingut modificat
     * 
     * @param idEnquesta L'identificador únic de l'enquesta que conté la pregunta
     * @param idPregunta L'identificador únic de la pregunta a modificar
     * @param nova       L'objecte Pregunta amb les noves dades (ha de mantenir el
     *                   mateix ID)
     * @throws ParametreInvalidException   Si algun paràmetre és null, buit o l'ID
     *                                     de la nova pregunta no coincideix
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat al
     *                                     sistema
     * @throws EnquestaNoExisteixException Si l'enquesta no existeix
     * @throws PreguntaNoExisteixException Si la pregunta no existeix a l'enquesta
     * @throws PermisDenegatException      Si l'usuari autenticat no és el creador
     *                                     de l'enquesta
     * @throws RespostaInvalidaException   Si la pregunta ja té respostes associades
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

        // Validar que si és qualitativa tingui almenys 2 opcions
        if (nova.tipusAdmetOpcions() && nova.getOpcions().size() < 2) {
            throw new ParametreInvalidException("Les preguntes qualitatives han de tenir almenys 2 opcions.");
        }

        // Validar que maxSeleccions no superi el nombre d'opcions per a preguntes
        // múltiples
        if (nova.getTipus() == TipusPregunta.QUALITATIVA_NO_ORDENADA_MULTIPLE
                && nova.getMaxSeleccions() > nova.getOpcions().size()) {
            throw new ParametreInvalidException(
                    "El nombre màxim de seleccions no pot ser superior al nombre d'opcions.");
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
     * Afegeix una nova pregunta a una enquesta creant l'objecte Pregunta
     * internament.
     *
     * @param idEnquesta    L'ID de l'enquesta.
     * @param idPregunta    L'ID de la nova pregunta.
     * @param textPregunta  El text de la pregunta.
     * @param tipus         El tipus de pregunta (NUMERICA, TEXT_LLIURE, etc.).
     * @param min           Valor mínim (només per a NUMERICA).
     * @param max           Valor màxim (només per a NUMERICA).
     * @param opcions       Llista d'opcions (només per a QUALITATIVA).
     * @param maxSeleccions Màxim de seleccions (només per a QUALITATIVA_MULTIPLE).
     * @throws Exception Si hi ha algun error en la creació o afegit de la pregunta.
     */
    public void afegirPregunta(String idEnquesta, String idPregunta, String textPregunta, String tipus,
            Double min, Double max, ArrayList<String> opcions, int maxSeleccions) throws Exception {

        Pregunta p = crearPreguntaInterna(idPregunta, textPregunta, tipus, min, max, opcions, maxSeleccions);

        if (p != null) {
            afegirPregunta(idEnquesta, p);
        } else {
            throw new ParametreInvalidException("No s'ha pogut crear la pregunta.");
        }
    }

    /**
     * Modifica una pregunta existent en una enquesta creant el nou objecte Pregunta
     * internament.
     *
     * @param idEnquesta    L'ID de l'enquesta.
     * @param idPregunta    L'ID de la pregunta a modificar.
     * @param textPregunta  El nou text de la pregunta.
     * @param tipus         El nou tipus de pregunta.
     * @param min           Nou valor mínim (només per a NUMERICA).
     * @param max           Nou valor màxim (només per a NUMERICA).
     * @param opcions       Nova llista d'opcions (només per a QUALITATIVA).
     * @param maxSeleccions Nou màxim de seleccions (només per a
     *                      QUALITATIVA_MULTIPLE).
     * @throws Exception Si hi ha algun error en la modificació de la pregunta.
     */
    public void modificarPregunta(String idEnquesta, String idPregunta, String textPregunta, String tipus,
            Double min, Double max, ArrayList<String> opcions, int maxSeleccions) throws Exception {

        Pregunta p = crearPreguntaInterna(idPregunta, textPregunta, tipus, min, max, opcions, maxSeleccions);

        if (p != null) {
            modificarPregunta(idEnquesta, idPregunta, p);
        } else {
            throw new ParametreInvalidException("No s'ha pogut crear l'objecte pregunta modificat.");
        }
    }

    /**
     * Mètode privat auxiliar per crear instàncies de Pregunta a partir de
     * paràmetres primitius.
     */
    private Pregunta crearPreguntaInterna(String idPregunta, String textPregunta, String tipus,
            Double min, Double max, ArrayList<String> opcions, int maxSeleccions) {

        Pregunta p = null;
        TipusPregunta tp = TipusPregunta.valueOf(tipus);

        switch (tp) {
            case NUMERICA:
                p = new Pregunta(idPregunta, textPregunta, min, max);
                break;
            case TEXT_LLIURE:
                p = new Pregunta(idPregunta, textPregunta);
                break;
            case QUALITATIVA_ORDENADA:
                p = new Pregunta(idPregunta, textPregunta, tp, maxSeleccions);
                if (opcions != null) {
                    int i = 1;
                    for (String opcioText : opcions) {
                        p.afegirOpcio(new Opcio(i, opcioText, i));
                        i++;
                    }
                }
                break;
            case QUALITATIVA_NO_ORDENADA_SIMPLE:
            case QUALITATIVA_NO_ORDENADA_MULTIPLE:
                p = new Pregunta(idPregunta, textPregunta, tp, maxSeleccions);
                if (opcions != null) {
                    int i = 1;
                    for (String opcioText : opcions) {
                        p.afegirOpcio(new Opcio(i++, opcioText));
                    }
                }
                break;
        }
        return p;
    }

    /**
     * Afegeix una opció a una pregunta qualitativa d'una enquesta.
     * 
     * Aquest mètode permet afegir una nova opció de resposta a una pregunta que
     * admet opcions
     * predefinides (preguntes qualitatives). Només el creador de l'enquesta té
     * permís per
     * afegir opcions a les preguntes. L'opció queda disponible per ser seleccionada
     * pels
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
     * (ordenada, simple o múltiple). Les preguntes de text lliure o numèriques no
     * admeten
     * opcions predefinides.
     * 
     * Restricció crítica: No es poden afegir opcions a una pregunta que ja té
     * respostes
     * d'usuaris. Afegir noves opcions podria alterar el significat de les respostes
     * existents
     * o crear inconsistències. Si necessites afegir opcions, has d'eliminar primer
     * totes
     * les respostes de la pregunta.
     * 
     * @param idEnquesta L'identificador únic de l'enquesta que conté la pregunta
     * @param idPregunta L'identificador únic de la pregunta on s'afegirà l'opció
     * @param o          L'objecte Opcio a afegir amb el seu ID i text
     * @throws ParametreInvalidException   Si algun paràmetre és null o buit
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat al
     *                                     sistema
     * @throws EnquestaNoExisteixException Si l'enquesta no existeix
     * @throws PreguntaNoExisteixException Si la pregunta no existeix a l'enquesta
     * @throws PermisDenegatException      Si l'usuari autenticat no és el creador
     *                                     de l'enquesta
     * @throws RespostaInvalidaException   Si el tipus de pregunta no admet opcions,
     *                                     l'opció ja existeix, o la pregunta té
     *                                     respostes
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
     * Aquest mètode permet eliminar una opció de resposta d'una pregunta que admet
     * opcions
     * predefinides (preguntes qualitatives). Només el creador de l'enquesta té
     * permís per
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
     * (ordenada, simple o múltiple). Les preguntes de text lliure o numèriques no
     * tenen
     * opcions predefinides.
     * 
     * Restricció crítica: No es poden eliminar opcions d'una pregunta que ja té
     * respostes
     * d'usuaris. Eliminar opcions invalidaria les respostes existents que podrien
     * fer
     * referència a aquesta opció. Si necessites eliminar opcions, has d'eliminar
     * primer
     * totes les respostes de la pregunta.
     * 
     * @param idEnquesta L'identificador únic de l'enquesta que conté la pregunta
     * @param idPregunta L'identificador únic de la pregunta que conté l'opció
     * @param idOpcio    L'identificador numèric de l'opció a eliminar
     * @throws ParametreInvalidException   Si algun paràmetre és null, buit o l'ID
     *                                     de l'opció és negatiu
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat al
     *                                     sistema
     * @throws EnquestaNoExisteixException Si l'enquesta no existeix
     * @throws PreguntaNoExisteixException Si la pregunta no existeix a l'enquesta
     * @throws PermisDenegatException      Si l'usuari autenticat no és el creador
     *                                     de l'enquesta
     * @throws RespostaInvalidaException   Si el tipus de pregunta no admet opcions,
     *                                     l'opció no existeix, o la pregunta té
     *                                     respostes
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
     * 
     * Aquest mètode permet carregar enquestes completes des de fitxers JSON
     * externs,
     * facilitant la creació massiva d'enquestes o la reutilització de plantilles.
     * El fitxer JSON ha de contenir tota l'estructura de l'enquesta: metadades,
     * preguntes amb els seus tipus específics, i opcions de resposta quan sigui
     * necessari.
     * 
     * Format JSON esperat:
     * {
     * "id": "ID_ENQUESTA",
     * "titol": "Títol de l'enquesta",
     * "descripcio": "Descripció detallada opcional",
     * "preguntes": [
     * {
     * "id": "ID_PREGUNTA",
     * "text": "Text descriptiu de la pregunta",
     * "tipus":
     * "numerica|text|qualitativa_ordenada|qualitativa_simple|qualitativa_multiple",
     * "min": 0,
     * "max": 100,
     * "max_seleccions": 3,
     * "opcions": [
     * {"id": 1, "text": "Opció 1", "ordre": 1},
     * {"id": 2, "text": "Opció 2", "ordre": 2}
     * ]
     * }
     * ]
     * }
     * 
     * Tipus de preguntes suportats:
     * - "text": Pregunta de resposta lliure en format text
     * - "numerica": Pregunta amb resposta numèrica dins d'un rang (min-max)
     * - "qualitativa_ordenada" o "ordenada": Opcions amb ordre de preferència
     * - "qualitativa_simple" o "simple": Selecció d'una única opció
     * - "qualitativa_multiple" o "multiple": Selecció de múltiples opcions (fins a
     * max_seleccions)
     * 
     * 
     * Validacions realitzades:
     * - Ha d'haver-hi un usuari autenticat al sistema
     * - El fitxer ha de ser accessible i llegible
     * - El contingut ha de ser JSON vàlid i ben format
     * - L'ID de l'enquesta no pot existir prèviament
     * - Cada pregunta ha de tenir ID i text obligatoris
     * - Les preguntes qualitatives han de tenir opcions definides
     * - Els IDs de preguntes i opcions han de ser únics
     * 
     * Comportament de creació:
     * 1. Llegeix i parseja el fitxer JSON
     * 2. Valida l'estructura i camps obligatoris
     * 3. Crea l'enquesta amb les metadades
     * 4. Crea cada pregunta segons el seu tipus
     * 5. Afegeix les opcions a les preguntes qualitatives
     * 6. Registra l'enquesta completa al sistema
     * 
     * Exemple d'ús:
     * importarEnquesta("/ruta/enquesta_satisfaccio.json");
     * 
     * @param path Ruta absoluta al fitxer JSON que conté la definició de l'enquesta
     * @throws ErrorImportacioException Si el fitxer no existeix, no es pot llegir,
     *                                  el JSON és invàlid,
     *                                  l'enquesta ja existeix, no hi ha usuari
     *                                  autenticat o el format és incorrecte
     */
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
     * Crea una pregunta segons el seu tipus a partir de dades JSON.
     * 
     * Aquest mètode privat auxiliar és utilitzat durant la importació d'enquestes
     * per interpretar el tipus de pregunta especificat en el JSON i crear l'objecte
     * Pregunta adequat amb tots els seus paràmetres específics. Cada tipus de
     * pregunta
     * pot requerir paràmetres diferents que s'extrauen del JSON.
     * 
     * Tipus de preguntes suportats i els seus constructors:
     * 
     * 1. "numerica":
     * - Crea: new Pregunta(id, text, min, max)
     * - Paràmetres JSON: "min" (Double, per defecte 0.0), "max" (Double, per
     * defecte 100.0)
     * - Exemple: Pregunta "Quina és la teva edat?" amb min=0, max=120
     * 
     * 2. "qualitativa_ordenada" o "ordenada":
     * - Crea: new Pregunta(id, text, TipusPregunta.QUALITATIVA_ORDENADA, 1)
     * - Les opcions s'afegeixen després amb ordre de preferència
     * - Exemple: Pregunta "Ordena les teves preferències" amb opcions ordenades
     * 
     * 3. "qualitativa_simple" o "simple":
     * - Crea: new Pregunta(id, text, TipusPregunta.QUALITATIVA_NO_ORDENADA_SIMPLE,
     * 1)
     * - Permet seleccionar només una opció de les disponibles
     * - Exemple: Pregunta "Quin és el teu color favorit?" amb opcions múltiples
     * però selecció única
     * 
     * 4. "qualitativa_multiple" o "multiple":
     * - Crea: new Pregunta(id, text,
     * TipusPregunta.QUALITATIVA_NO_ORDENADA_MULTIPLE, maxSeleccions)
     * - Paràmetres JSON: "max_seleccions" (int, per defecte 3)
     * - Permet seleccionar múltiples opcions fins al màxim especificat
     * - Exemple: Pregunta "Quins idiomes parles?" amb max_seleccions=5
     * 
     * 5. "text" o qualsevol altre:
     * - Crea: new Pregunta(id, text)
     * - Resposta de text lliure sense validacions específiques
     * - Per defecte si el tipus no coincideix amb cap dels anteriors
     * - Exemple: Pregunta "Comentaris addicionals?"
     * 
     * El mètode és case-insensitive per facilitar la compatibilitat amb
     * diferents formats de fitxers JSON. Si el tipus no es reconeix, es crea
     * per defecte una pregunta de text lliure.
     * 
     * @param json     Objecte JSON amb tota la informació de la pregunta (tipus,
     *                 min, max, max_seleccions, etc.)
     * @param id       Identificador únic de la pregunta dins de l'enquesta
     * @param text     Text descriptiu de la pregunta que veuran els usuaris
     * @param tipusStr Tipus de pregunta en format string (case-insensitive)
     * @return Objecte Pregunta completament configurat segons el tipus especificat
     */
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
     * 
     * Aquest mètode permet carregar respostes massives d'usuaris a una enquesta
     * existent,
     * facilitant la importació de dades recollides externament o la càrrega de
     * dades de prova.
     * El procés inclou validacions exhaustives per garantir la integritat de les
     * dades abans
     * de modificar l'estat del sistema.
     * 
     * Format JSON esperat:
     * {
     * "enquesta_id": "ID_ENQUESTA",
     * "respostes": [
     * {
     * "username": "usuari1",
     * "respostes": [
     * {"pregunta_id": "P1", "resposta": "Resposta en text lliure"},
     * {"pregunta_id": "P2", "resposta": "42"},
     * {"pregunta_id": "P3", "resposta": "Opció1,Opció2"}
     * ]
     * },
     * {
     * "username": "usuari2",
     * "respostes": [
     * {"pregunta_id": "P1", "resposta": "Una altra resposta"},
     * {"pregunta_id": "P2", "resposta": "38"}
     * ]
     * }
     * ]
     * }
     * 
     * Validacions i comportament:
     * 
     * 1. Validació prèvia (abans d'importar res):
     * - Verifica que l'enquesta especificada existeix al sistema
     * - Comprova que TOTES les preguntes referenciades al JSON existeixen a
     * l'enquesta
     * - Si alguna pregunta no existeix, llança excepció sense importar cap dada
     * 
     * 2. Validació per usuari:
     * - Si un usuari no existeix al sistema, s'omet amb un avís per consola però
     * continua
     * - Si un usuari ja ha contestat l'enquesta, les noves respostes s'ignoren per
     * aquest usuari
     * - Només es registren respostes per usuaris vàlids i registrats
     * 
     * 3. Validació per resposta:
     * - Cada resposta es valida segons el tipus de pregunta
     * - Si una resposta individual falla, s'omet amb avís però continua amb les
     * altres
     * - Preguntes numèriques: valida que sigui un número dins del rang
     * - Preguntes qualitatives: valida que les opcions existeixin
     * - Preguntes text: accepta qualsevol text
     * 
     * 4. Registre de participació:
     * - Un usuari es marca com a participant només si ha respost almenys una
     * pregunta vàlidament
     * - La participació s'afegeix a la llista de participants de l'enquesta
     * - Si l'usuari ja era participant, no es duplica
     * 
     * Flux d'execució:
     * 1. Llegeix i parseja el fitxer JSON
     * 2. Obté l'enquesta especificada
     * 3. Valida que totes les preguntes del JSON existeixen
     * 4. Per cada usuari:
     * a. Verifica que l'usuari existeix
     * b. Per cada resposta de l'usuari:
     * - Valida el format de la resposta
     * - Registra la resposta si és vàlida
     * c. Si l'usuari ha respost almenys una pregunta, registra la participació
     * 5. Mostra missatge de confirmació amb nombre de participants importats
     * 
     * 
     * Exemple d'ús:
     * importarRespostes("/ruta/respostes_enquesta_1.json");
     * // Sortida: ✓ S'han importat respostes de 47 participants
     * 
     * @param path Ruta absoluta al fitxer JSON que conté les respostes dels usuaris
     * @throws ErrorImportacioException Si el fitxer no existeix o no es pot llegir,
     *                                  si el JSON és invàlid, si l'enquesta no
     *                                  existeix, si alguna pregunta del JSON
     *                                  no existeix a l'enquesta, o si no s'ha pogut
     *                                  importar cap resposta vàlida
     * @see Pregunta#validarResposta(String)
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
                                                idEnquesta
                                                + "'. Totes les preguntes del JSON han de coincidir amb l'enquesta.");
                            }
                        }
                    }
                }
            }

            // Si arribem aquí, totes les preguntes són vàlides. Procedir amb la importació
            if (json.has("respostes")) {
                JSONArray respostesArray = json.getJSONArray("respostes");
                int respostesImportades = 0;
                int usuarisIgnorats = 0;

                for (int i = 0; i < respostesArray.length(); i++) {
                    JSONObject respostaUsuariJson = respostesArray.getJSONObject(i);

                    String username = respostaUsuariJson.getString("username");

                    // Verificar que l'usuari existeix
                    Usuari usuari = ctrlPersistencia.getUsuari(username);
                    if (usuari == null) {
                        usuarisIgnorats++;
                        continue;
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
                                // Guardar l'usuari per assegurar que les participacions es persisteixen
                                ctrlPersistencia.guardarUsuari(usuari);
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

                String missatge = "✓ S'han importat respostes de " + respostesImportades + " participants";
                if (usuarisIgnorats > 0) {
                    missatge += ". S'han ignorat " + usuarisIgnorats + " usuari(s) no registrat(s)";
                }
                System.out.println(missatge);
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
     * Processa la contestació completa d'una enquesta per part d'un usuari
     * autenticat.
     * 
     * Aquest mètode permet respondre totes les preguntes d'una enquesta d'una sola
     * vegada.
     * Valida exhaustivament totes les respostes abans de guardar-ne cap per
     * garantir
     * l'atomicitat (tot o res): si alguna resposta és invàlida, no es guarda cap.
     * 
     * Flux d'execució:
     * 1. Valida paràmetres i comprova que hi ha un usuari autenticat
     * 2. Verifica que l'enquesta existeix i l'usuari NO l'ha contestat prèviament
     * 3. Valida totes les respostes segons el tipus de cada pregunta
     * 4. Si totes les validacions passen, guarda totes les respostes
     * 5. Registra la participació de l'usuari a l'enquesta
     * 
     * 
     * 
     * Validacions segons tipus de pregunta:
     * - Numèrica: comprova que sigui un número dins del rang [min, max]
     * - Qualitativa simple: valida que l'opció existeixi
     * - Qualitativa múltiple: valida opcions i nombre màxim de seleccions
     * - Text lliure: accepta qualsevol text
     * 
     * @param idEnquesta             Identificador únic de l'enquesta a contestar
     * @param respostes              HashMap que mapeja identificadors de resposta a
     *                               text de resposta
     * @param idsPreguntaPerResposta HashMap que mapeja identificadors de resposta a
     *                               identificadors de pregunta
     * @throws UsuariNoAutenticatException   Si no hi ha cap usuari autenticat
     * @throws EnquestaNoExisteixException   Si l'enquesta no existeix
     * @throws EnquestaJaContestadaException Si l'usuari ja ha contestat aquesta
     *                                       enquesta
     * @throws PreguntaNoExisteixException   Si alguna pregunta referenciada no
     *                                       existeix
     * @throws RespostaInvalidaException     Si alguna resposta no és vàlida pel
     *                                       tipus de pregunta
     * @throws ParametreInvalidException     Si algun paràmetre és null o buit
     * @see modificarResposta(String, String, String)
     * @see esborrarResposta(String)
     */
    public void contestarEnquesta(String idEnquesta, HashMap<String, String> respostes,
            HashMap<String, String> idsPreguntaPerResposta)
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

        // PASO 1: Validar TODAS las respuestas ANTES de guardar nada (para evitar
        // rollback)
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
                                " (tipus: " + pregunta.getTipus() + "): " + textResposta);
            }

            // Guardar la pregunta validada para usarla después
            preguntesValidades.put(idResposta, pregunta);
        }

        // PASO 2: Si llegamos aquí, TODAS las respuestas son válidas → guardarlas
        for (HashMap.Entry<String, String> entry : respostes.entrySet()) {
            String idResposta = entry.getKey();
            String textResposta = entry.getValue();
            String idPregunta = idsPreguntaPerResposta.get(idResposta);

            // Registrar la resposta (l'ID es genera automàticament dins)
            ctrlResposta.registrarResposta(idEnquesta, idPregunta, textResposta, usuari);
        }

        // PASO 3: Registrar participación
        ctrlEnquesta.registrarParticipacio(idEnquesta, usuari.getUsername());
    }

    /**
     * Modifica una resposta prèvia de l'usuari autenticat a una pregunta d'una
     * enquesta.
     * 
     * Aquest mètode permet actualitzar el contingut d'una resposta que l'usuari ja
     * havia
     * donat anteriorment. Valida que la nova resposta compleix els requisits del
     * tipus de
     * pregunta abans d'aplicar la modificació.
     * 
     * Validacions realitzades:
     * - Ha d'haver-hi un usuari autenticat
     * - L'enquesta i la pregunta han d'existir
     * - L'usuari ha d'haver respost prèviament aquesta pregunta
     * - La nova resposta ha de ser vàlida segons el tipus de pregunta
     * 
     * Les modificacions es persisteixen automàticament al sistema.
     * 
     * @param idEnquesta   Identificador de l'enquesta
     * @param idPregunta   Identificador de la pregunta
     * @param novaResposta Nou text de la resposta
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat
     * @throws EnquestaNoExisteixException Si l'enquesta no existeix
     * @throws PreguntaNoExisteixException Si la pregunta no existeix
     * @throws RespostaNoExisteixException Si l'usuari no ha respost aquesta
     *                                     pregunta prèviament
     * @throws RespostaInvalidaException   Si la nova resposta no és vàlida pel
     *                                     tipus de pregunta
     * @throws ParametreInvalidException   Si algun paràmetre és null o buit
     */
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

        // Validació 4: Verificar que existeix la resposta de l'usuari autenticat a
        // aquesta pregunta
        // Nota: getResposta() busca per username, per tant si existeix, sempre serà del
        // mateix usuari
        Resposta respostaExistent = pregunta.getResposta(usuariActual.getUsername());
        if (respostaExistent == null) {
            throw new RespostaNoExisteixException(
                    "L'usuari " + usuariActual.getUsername() + " no ha respost la pregunta " + idPregunta
                            + " de l'enquesta " + idEnquesta);
        }

        // Validació 5: Validar el format de la nova resposta segons el tipus de
        // pregunta
        if (!pregunta.validarResposta(novaResposta)) {
            throw new RespostaInvalidaException(
                    "La nova resposta no és vàlida per la pregunta " + idPregunta +
                            " (tipus: " + pregunta.getTipus() + "): '" + novaResposta + "'");
        }

        // Si totes les validacions passen, modificar la resposta directament
        ctrlResposta.modificarResposta(respostaExistent, novaResposta);

        // Nota: La persistència s'actualitza automàticament perquè l'objecte Resposta
        // es modifica directament i està guardat al HashMap de CtrlPersistencia
    }

    /**
     * Esborra totes les respostes de l'usuari autenticat a una enquesta específica.
     * 
     * Aquest mètode elimina completament la participació de l'usuari actual en una
     * enquesta:
     * elimina totes les respostes donades a cada pregunta de l'enquesta i retira
     * l'usuari
     * de la llista de participants.
     * 
     * Després d'executar aquest mètode, és com si l'usuari mai hagués contestat
     * l'enquesta,
     * permetent-li tornar-la a respondre completament si ho desitja.
     * 
     * Validacions:
     * - Ha d'haver-hi un usuari autenticat
     * - L'enquesta ha d'existir
     * - L'usuari ha d'haver contestat prèviament aquesta enquesta
     * 
     * @param idEnquesta Identificador de l'enquesta
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat
     * @throws EnquestaNoExisteixException Si l'enquesta no existeix
     * @throws RespostaNoExisteixException Si l'usuari no ha contestat aquesta
     *                                     enquesta
     * @throws ParametreInvalidException   Si l'ID de l'enquesta és null o buit
     */
    public void esborrarResposta(String idEnquesta)
            throws EnquestaNoExisteixException, RespostaNoExisteixException, UsuariNoAutenticatException,
            ParametreInvalidException {

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
                    "L'usuari " + usuariActual.getUsername() + " no ha contestat l'enquesta " + idEnquesta);
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
     * 
     * @return ArrayList amb totes les enquestes existents (mai null, pot estar
     *         buida)
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat
     */
    /**
     * Consulta la llista de totes les enquestes disponibles al sistema.
     * 
     * Aquest mètode retorna un ArrayList amb totes les enquestes registrades.
     * Requereix que hi hagi un usuari autenticat al sistema.
     * 
     * @return ArrayList amb totes les enquestes del sistema. Mai retorna null;
     *         si no hi ha enquestes, retorna una llista buida.
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat al
     *                                     sistema
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
     * Consulta les enquestes creades per l'usuari autenticat.
     * 
     * Aquest mètode retorna només les enquestes on l'usuari actual és el creador.
     * És útil per veure les enquestes pròpies d'un usuari sense mostrar totes les
     * enquestes del sistema.
     * 
     * @return ArrayList amb les enquestes creades per l'usuari actual. Mai retorna
     *         null;
     *         si l'usuari no ha creat cap enquesta, retorna una llista buida.
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat al
     *                                     sistema
     */
    public List<Enquesta> consultarEnquestesDelUsuari() throws UsuariNoAutenticatException {
        // Validar que hi ha un usuari autenticat
        Usuari usuariActual = ctrlUsuari.getUsuariActual();
        if (usuariActual == null) {
            throw new UsuariNoAutenticatException("Cal estar autenticat per consultar les teves enquestes.");
        }

        // Obtenir les enquestes creades per l'usuari
        return usuariActual.getEnquestesCreades();
    }

    /**
     * Consulta una enquesta específica per ID.
     * 
     * @param idEnquesta L'ID de l'enquesta a consultar
     * @return L'enquesta trobada
     * @throws ParametreInvalidException   Si l'ID és null o buit
     * @throws EnquestaNoExisteixException Si l'enquesta no existeix
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat
     */
    public Enquesta getEnquesta(String idEnquesta)
            throws ParametreInvalidException, EnquestaNoExisteixException, UsuariNoAutenticatException {
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
     * 
     * @param idPregunta L'ID de la pregunta.
     * @return Un ArrayList amb totes les respostes de la pregunta (mai null, pot
     *         estar buit)
     * @throws ParametreInvalidException   Si l'ID és null o buit
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
     * Consulta totes les respostes d'una pregunta en format cru.
     * 
     * @param idPregunta L'ID de la pregunta.
     * @return Llista de [username, textResposta].
     */
    public ArrayList<ArrayList<String>> consultarRespostesPreguntaRaw(String idPregunta)
            throws ParametreInvalidException, PreguntaNoExisteixException, UsuariNoAutenticatException {
        ArrayList<Resposta> respostes = consultarRespostesPregunta(idPregunta);
        return formatejarRespostes(respostes);
    }

    /**
     * Consulta totes les respostes d'una enquesta.
     * Per cada pregunta de l'enquesta, retorna totes les seves respostes.
     * 
     * @param idEnquesta L'ID de l'enquesta.
     * @return Un mapa amb idPregunta com a clau i totes les seves respostes com a
     *         valor (mai null, pot estar buit)
     * @throws ParametreInvalidException   Si l'ID és null o buit
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

            // Obtenir les respostes de la pregunta com ArrayList (ja fa còpia defensiva
            // getRespostes())
            ArrayList<Resposta> llistaRespostes = new ArrayList<>(pregunta.getRespostes().values());
            respostesPerPregunta.put(idPregunta, llistaRespostes);
        }

        return respostesPerPregunta;
    }

    /**
     * Consulta totes les respostes d'una enquesta en format cru.
     * 
     * @param idEnquesta L'ID de l'enquesta.
     * @return Map amb idPregunta -> Llista de [username, textResposta].
     */
    public HashMap<String, ArrayList<ArrayList<String>>> consultarRespostesEnquestaRaw(String idEnquesta)
            throws ParametreInvalidException, EnquestaNoExisteixException, UsuariNoAutenticatException {

        HashMap<String, ArrayList<Resposta>> respostesOriginal = consultarRespostesEnquesta(idEnquesta);
        HashMap<String, ArrayList<ArrayList<String>>> respostesRaw = new HashMap<>();

        for (String idPregunta : respostesOriginal.keySet()) {
            respostesRaw.put(idPregunta, formatejarRespostes(respostesOriginal.get(idPregunta)));
        }
        return respostesRaw;
    }

    /**
     * Mètode auxiliar per formatejar una llista de respostes a format cru.
     * 
     * @param respostes Llista de respostes.
     * @return Llista de [username, text].
     */
    private ArrayList<ArrayList<String>> formatejarRespostes(ArrayList<Resposta> respostes) {
        ArrayList<ArrayList<String>> llistaRaw = new ArrayList<>();
        if (respostes != null) {
            for (Resposta r : respostes) {
                ArrayList<String> dadaResposta = new ArrayList<>();
                dadaResposta.add(r.getUsernameUsuari());
                dadaResposta.add(r.getTextResposta());
                llistaRaw.add(dadaResposta);
            }
        }
        return llistaRaw;
    }

    /**
     * Consulta les respostes pròpies de l'usuari actual a una enquesta específica.
     * Retorna només les respostes que l'usuari autenticat ha donat a les preguntes
     * d'aquesta enquesta.
     * 
     * @param idEnquesta L'ID de l'enquesta
     * @return HashMap amb clau=idPregunta i valor=Resposta de l'usuari (mai null,
     *         pot estar buit)
     * @throws ParametreInvalidException   Si l'ID és invàlid
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
     * 
     * @param idEnquesta   L'ID de l'enquesta.
     * @param usuari       L'usuari que respon.
     * @param idPregunta   L'ID de la pregunta.
     * @param textResposta El text de la resposta.
     */
    /**
     * Registra una resposta individual d'un usuari a una pregunta específica d'una
     * enquesta.
     * 
     * Aquest mètode s'utilitza principalment durant la importació de respostes des
     * de fitxers JSON.
     * Valida que tots els paràmetres siguin vàlids i que la resposta compleixi els
     * requisits
     * del tipus de pregunta abans de registrar-la al sistema.
     * 
     * Validacions realitzades:
     * - Tots els paràmetres han de ser no nuls
     * - La resposta no pot ser buida
     * - L'enquesta ha d'existir
     * - La pregunta ha d'existir dins l'enquesta
     * - La resposta ha de ser vàlida segons el tipus de pregunta
     * 
     * @param idEnquesta   Identificador de l'enquesta
     * @param usuari       L'usuari que registra la resposta
     * @param idPregunta   Identificador de la pregunta
     * @param textResposta Text de la resposta
     * @throws ParametreInvalidException   Si algun paràmetre és nul o la resposta
     *                                     és buida
     * @throws EnquestaNoExisteixException Si l'enquesta no existeix
     * @throws PreguntaNoExisteixException Si la pregunta no existeix a l'enquesta
     * @throws RespostaInvalidaException   Si la resposta no és vàlida pel tipus de
     *                                     pregunta
     */
    public void registrarResposta(String idEnquesta, Usuari usuari, String idPregunta, String textResposta)
            throws ParametreInvalidException,
            EnquestaNoExisteixException,
            PreguntaNoExisteixException,
            RespostaInvalidaException {

        // Validar paràmetres
        if (idEnquesta == null || idEnquesta.trim().isEmpty()) {
            throw new ParametreInvalidException("L'identificador de l'enquesta no pot ser nul o buit");
        }
        if (usuari == null) {
            throw new ParametreInvalidException("L'usuari no pot ser nul");
        }
        if (idPregunta == null || idPregunta.trim().isEmpty()) {
            throw new ParametreInvalidException("L'identificador de la pregunta no pot ser nul o buit");
        }
        if (textResposta == null || textResposta.trim().isEmpty()) {
            throw new ParametreInvalidException("La resposta no pot ser nul·la o buida");
        }

        // Verificar que l'enquesta existeix
        Enquesta enquesta = ctrlEnquesta.getEnquesta(idEnquesta);
        if (enquesta == null) {
            throw new EnquestaNoExisteixException(idEnquesta);
        }

        // Obtenir la pregunta
        Pregunta pregunta = ctrlEnquesta.getPregunta(idEnquesta, idPregunta);
        if (pregunta == null) {
            throw new PreguntaNoExisteixException(
                    "La pregunta amb ID '" + idPregunta + "' no existeix a l'enquesta '" + idEnquesta + "'");
        }

        // Validar que la resposta és vàlida segons el tipus de pregunta
        if (!pregunta.validarResposta(textResposta)) {
            throw new RespostaInvalidaException(
                    "La resposta '" + textResposta + "' no és vàlida per la pregunta '" +
                            pregunta.getText() + "' de tipus " + pregunta.getTipus());
        }

        // Registrar la resposta (l'ID es genera automàticament dins de CtrlResposta)
        ctrlResposta.registrarResposta(idEnquesta, idPregunta, textResposta, usuari);
    }

    // --- Anàlisi i Clustering ---

    // --- Gestió d'Usuaris i Perfils (mètodes existents) ---

    /**
     * Registra un nou usuari al sistema amb el nom d'usuari i contrasenya
     * especificats.
     * 
     * Aquest mètode crea un compte d'usuari nou al sistema. Valida que el nom
     * d'usuari
     * no estigui ja registrat i que tant el username com la contrasenya compleixin
     * els
     * requisits mínims de longitud i format.
     * 
     * Validacions:
     * - El nom d'usuari ha de tenir almenys 3 caràcters
     * - La contrasenya ha de tenir almenys 4 caràcters
     * - El nom d'usuari no pot estar ja registrat al sistema
     * - Ni el username ni la contrasenya poden ser null o buits
     * 
     * El nom d'usuari es normalitza eliminant espais als extrems.
     * 
     * @param username Nom d'usuari desitjat (mínim 3 caràcters)
     * @param password Contrasenya de l'usuari (mínim 4 caràcters)
     * @throws UsuariJaExisteixException Si ja existeix un usuari amb aquest nom
     * @throws ParametreInvalidException Si els paràmetres són invàlids o no
     *                                   compleixen els requisits
     * @see login(String, String)
     */
    public void registrarUsuari(String username, String password)
            throws UsuariJaExisteixException, ParametreInvalidException {
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

    /**
     * Autentica un usuari al sistema i inicia una nova sessió.
     * 
     * Aquest mètode verifica les credencials de l'usuari (nom d'usuari i
     * contrasenya)
     * i, si són correctes, estableix l'usuari com a usuari autenticat del sistema,
     * permetent-li accedir a totes les funcionalitats que requereixen autenticació.
     * 
     * El procés de login:
     * 1. Valida que els paràmetres no siguin null o buits
     * 2. Normalitza el nom d'usuari (elimina espais)
     * 3. Comprova que l'usuari existeix al sistema
     * 4. Verifica que la contrasenya és correcta
     * 5. Estableix l'usuari com a usuari actual del sistema
     * 
     * Si les credencials són incorrectes (usuari no existeix o contrasenya
     * incorrecta),
     * es llança una excepció i no s'inicia cap sessió.
     * 
     * @param username Nom d'usuari registrat al sistema
     * @param password Contrasenya de l'usuari
     * @throws CredencialsIncorrectesException Si l'usuari no existeix o la
     *                                         contrasenya és incorrecta
     * @throws ParametreInvalidException       Si el username o password són null o
     *                                         buits
     * @see logout()
     * @see registrarUsuari(String, String)
     */
    public void login(String username, String password)
            throws CredencialsIncorrectesException, ParametreInvalidException {
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
     * Tanca la sessió de l'usuari actualment autenticat.
     * 
     * Aquest mètode finalitza la sessió activa de l'usuari, establint l'usuari
     * actual
     * a null. Després d'executar logout, les operacions que requereixen
     * autenticació
     * llançaran UsuariNoAutenticatException fins que es torni a fer login.
     * 
     * És segur cridar aquest mètode encara que no hi hagi cap usuari autenticat.
     * 
     * @see login(String, String)
     */
    public void logout() {
        ctrlUsuari.logout();
    }

    /**
     * Verifica si una contrasenya proporcionada coincideix amb la de l'usuari
     * autenticat.
     * 
     * Aquest mètode comprova si la contrasenya donada correspon a l'usuari que té
     * la sessió activa actualment. És útil per confirmar la identitat de l'usuari
     * abans d'executar operacions sensibles (com eliminar el compte o modificar
     * dades).
     * 
     * @param password Contrasenya a verificar
     * @return true si la contrasenya coincideix amb la de l'usuari actual, false
     *         altrament
     */
    public boolean checkPassword(String password) {
        return ctrlUsuari.checkPassword(password);
    }

    /**
     * Elimina un usuari del sistema de forma permanent.
     * 
     * Aquest mètode esborra completament un compte d'usuari del sistema, incloent
     * totes les seves dades associades. Si l'usuari a eliminar és l'usuari
     * actualment
     * autenticat, es tanca automàticament la seva sessió abans de l'eliminació.
     * 
     * Advertència: Aquesta operació és irreversible i comporta la pèrdua de totes
     * les dades de l'usuari (respostes, perfils assignats, etc.).
     * 
     * Comportament idempotent: Si l'usuari no existeix, l'operació no fa res
     * però no genera cap error. Això permet cridar aquesta funció de forma segura
     * sense necessitat de comprovar prèviament l'existència de l'usuari.
     * 
     * Validacions realitzades:
     * - El nom d'usuari no pot ser null o buit
     * 
     * El nom d'usuari es normalitza (elimina espais) abans de processar
     * l'eliminació.
     * 
     * @param username Nom de l'usuari a eliminar
     * @throws ParametreInvalidException Si el username és null o buit
     * @see registrarUsuari(String, String)
     */
    public void eliminarUsuari(String username) throws ParametreInvalidException {
        // Validar que el paràmetre no sigui nul o buit
        if (username == null || username.trim().isEmpty()) {
            throw new ParametreInvalidException("El nom d'usuari no pot estar buit.");
        }

        // Normalitzar el username (eliminar espais)
        String normalizedUsername = username.trim();

        // Si l'usuari a eliminar és l'usuari actual, tancar sessió primer
        Usuari usuariact = ctrlUsuari.getUsuariActual();
        if (usuariact != null && normalizedUsername.equals(usuariact.getUsername())) {
            ctrlUsuari.logout();
        }

        // Eliminar l'usuari del sistema (operació idempotent)
        ctrlUsuari.eliminarUsuari(normalizedUsername);
    }

    /**
     * Crea un nou perfil al sistema amb un identificador i descripció especificats.
     * 
     * Aquest mètode permet crear perfils manualment, que poden ser assignats
     * posteriorment
     * a usuaris. Normalment els perfils es generen automàticament durant el procés
     * de
     * clustering, però aquest mètode permet crear-ne de personalitzats.
     * 
     * @param id         Identificador únic del perfil
     * @param descripcio Descripció del perfil
     * @see getPerfil(String)
     */
    public void crearPerfil(String id, String descripcio) {
        ctrlPerfil.crearPerfil(id, descripcio);
    }

    public Perfil getPerfil(String id) {
        return ctrlPerfil.getPerfil(id);
    }

    /**
     * Obté tots els perfils del sistema.
     * 
     * @return Mapa amb tots els perfils indexats per ID
     */
    public HashMap<String, Perfil> getAllPerfils() {
        return ctrlPersistencia.getAllPerfils();
    }

    // --- Mètodes auxiliars ---

    /**
     * Obté l'usuari actualment autenticat.
     * 
     * @return L'usuari autenticat o null si no n'hi ha cap
     */
    public Usuari getUsuariActual() {
        return ctrlUsuari.getUsuariActual();
    }

    /**
     * Verifica si existeix una enquesta amb l'ID especificat.
     * 
     * Aquest mètode comprova si una enquesta amb l'identificador donat està
     * registrada al sistema de persistència. És una operació ràpida i segura que
     * no modifica l'estat del sistema i pot ser cridada múltiples vegades sense
     * efectes secundaris.
     * 
     * 
     * Validacions realitzades:
     * - Si l'ID és null, retorna false
     * - Si l'ID està buit (després de trim), retorna false
     * - Si l'enquesta no està al sistema de persistència, retorna false
     * - Si l'enquesta existeix, retorna true
     * 
     * Nota: Aquest mètode NO requereix autenticació. És una consulta
     * de només lectura que pot ser utilitzada en qualsevol moment.
     * 
     * 
     * @param idEnquesta L'identificador únic de l'enquesta a verificar
     * @return true si existeix una enquesta amb aquest ID, false si no existeix,
     *         si l'ID és null o si l'ID està buit
     * @see CtrlEnquesta#getEnquesta(String)
     */
    public boolean existeixEnquesta(String idEnquesta) {
        if (idEnquesta == null || idEnquesta.trim().isEmpty()) {
            return false;
        }
        return ctrlEnquesta.getEnquesta(idEnquesta) != null;
    }

    /**
     * Registra la participació d'un usuari en una enquesta.
     * 
     * Aquest mètode afegeix un usuari a la llista de participants d'una enquesta,
     * marcant-lo com a persona que ha contestat o està contestant l'enquesta.
     * La participació és un registre essencial per a posteriors anàlisis de
     * clustering
     * i per controlar quins usuaris han interactuat amb cada enquesta.
     * 
     * 
     * Requisits per registrar participació:
     * - L'enquesta ha d'existir al sistema
     * - L'usuari ha d'estar registrat al sistema
     * 
     * Importància per al clustering:
     * Aquest registre és fonamental perquè:
     * - Determina quins usuaris s'inclouran en l'anàlisi de clustering
     * - Permet calcular el nombre mínim de participants necessaris per analitzar
     * - Facilita l'obtenció ràpida de tots els participants d'una enquesta
     * - Manté la coherència entre respostes i usuaris analitzats
     * 
     * Comportament amb duplicats:
     * Si es crida múltiples vegades amb el mateix username i idEnquesta,
     * només es registra una vegada. No es llança excepció per duplicats.
     * 
     * 
     * @param idEnquesta L'identificador únic de l'enquesta on l'usuari ha
     *                   participat
     * @param username   El nom d'usuari del participant a registrar
     * @see CtrlEnquesta#registrarParticipacio(String, String)
     * @see contestarEnquesta(String, HashMap, HashMap)
     */
    public void registrarParticipacio(String idEnquesta, String username) {
        ctrlEnquesta.registrarParticipacio(idEnquesta, username);
    }

    // --- Anàlisi i Clustering ---

    /**
     * Troba el valor òptim de k per al clustering d'una enquesta.
     * 
     * Aquest mètode determina automàticament el nombre ideal de clusters avaluant
     * múltiples
     * valors de k dins d'un rang especificat. Utilitza el coeficient de Silhouette
     * per
     * identificar quin valor produeix la millor separació i cohesió dels clusters.
     * 
     * El procés vectoritza les respostes dels participants, executa l'algoritme de
     * clustering
     * per cada valor de k entre kMin i kMax, calcula el coeficient de Silhouette de
     * cada
     * resultat, i retorna el k que ha obtingut la millor puntuació.
     * 
     * Validacions:
     * - L'enquesta ha d'existir i tenir preguntes
     * - Han d'haver-hi almenys kMax participants amb respostes
     * - kMin ha de ser mínim 2 i kMax no pot superar el nombre de participants
     * 
     * Per obtenir un rang recomanat de valors kMin/kMax, podeu utilitzar
     * suggestKRange().
     * Per una selecció ràpida sense anàlisi exhaustiva, considereu
     * escollirKAleatori().
     * 
     * @param idEnquesta   Identificador únic de l'enquesta a analitzar
     * @param kMin         Valor mínim de k a avaluar (mínim 2)
     * @param kMax         Valor màxim de k a avaluar (màxim = nombre de
     *                     participants)
     * @param algoritmeNom Algoritme a utilitzar: "KMeans", "KMeans++" o "KMedoids"
     * @param maxIters     Màxim nombre d'iteracions per cada execució de clustering
     * @return OptimalKResult amb bestK (òptim), bestScore (Silhouette) i allScores
     *         (tots els k avaluats)
     * @throws EnquestaNoExisteixException Si l'enquesta especificada no existeix al
     *                                     sistema
     * @throws IllegalStateException       Si l'enquesta no té preguntes o no hi ha
     *                                     prou participants
     * @see suggestKRange(int)
     * @see analitzarEnquesta(String, int, boolean, int, String)
     * @see CtrlAnalisi#findOptimalK(List, int, int, String, int,
     *      DistanceCalculator.FeatureSpec[])
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
     * Selecciona un valor de k aleatori adequat per a una enquesta.
     * 
     * Aquest mètode proporciona una manera ràpida d'obtenir un nombre de clusters
     * sense
     * necessitat de fer una anàlisi exhaustiva. El valor retornat està basat en el
     * nombre
     * total de participants de l'enquesta i segueix heurístiques generals de
     * clustering.
     * 
     * És útil per fer exploracions ràpides o quan no es requereix trobar el k
     * òptim.
     * Per obtenir el millor valor de k, utilitzeu trobarMillorK() que avalua
     * múltiples
     * valors i utilitza el coeficient de Silhouette.
     * 
     * Validacions:
     * - L'enquesta ha d'existir al sistema
     * - El nombre de participants determina el rang possible de k
     * 
     * @param idEnquesta Identificador únic de l'enquesta
     * @return Valor de k aleatori adequat segons el nombre de participants
     * @throws EnquestaNoExisteixException Si l'enquesta no existeix al sistema
     * @see trobarMillorK(String, int, int, String, int)
     * @see CtrlAnalisi#selectRandomK(int)
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
     * Suggereix un rang recomanat de valors per a k basat en el nombre de
     * participants.
     * 
     * @param numParticipants Nombre de participants de l'enquesta
     * @return Array de dos elements [kMin, kMax] amb el rang recomanat
     */
    public int[] suggestKRange(int numParticipants) {
        return ctrlAnalisi.suggestKRange(numParticipants);
    }

    /**
     * Obté els representants (punts reals més propers als centroides) per a un
     * clustering.
     * 
     * @param clusters Llista de clusters
     * @param specs    Especificacions de tipus per a cada dimensió
     * @return Llista amb el vector representant de cada cluster
     */
    public List<String[]> obtenirRepresentants(List<Kluster> clusters, DistanceCalculator.FeatureSpec[] specs) {
        if (clusters == null || specs == null) {
            throw new IllegalArgumentException("clusters i specs no poden ser null");
        }

        DistanceCalculator dc = new DistanceCalculator();
        List<String[]> representants = new ArrayList<>();

        for (Kluster cluster : clusters) {
            String[] representant = cluster.getRepresentant(specs, dc);
            representants.add(representant);
        }

        return representants;
    }

    /**
     * Obté informació llegible sobre els representants d'un clustering.
     * 
     * @param resultado Resultat del clustering
     * @param preguntes Llista de preguntes de l'enquesta
     * @return Llista de strings amb la informació formatejada de cada representant
     */
    public List<String> obtenirInfoRepresentants(ResultatClustering resultado, List<Pregunta> preguntes) {
        if (resultado == null || preguntes == null) {
            throw new IllegalArgumentException("resultado i preguntes no poden ser null");
        }

        List<String> info = new ArrayList<>();

        for (int i = 0; i < resultado.representants.size(); i++) {
            StringBuilder sb = new StringBuilder();
            sb.append("Cluster ").append(i + 1).append(":\n");

            String username = resultado.getUsernameRepresentant(i);
            if (username != null) {
                sb.append("  Usuari representant: ").append(username).append("\n");
            }

            String[] representant = resultado.representants.get(i);
            sb.append("  Respostes:\n");

            for (int j = 0; j < Math.min(representant.length, preguntes.size()); j++) {
                sb.append("    - ").append(preguntes.get(j).getText())
                        .append(": ").append(representant[j]).append("\n");
            }

            sb.append("  Mida del cluster: ").append(resultado.clusters.get(i).size()).append(" membres\n");
            sb.append("  Qualitat (Silhouette): ")
                    .append(String.format("%.3f", resultado.silhouettePerCluster[i])).append("\n");

            info.add(sb.toString());
        }

        return info;
    }

    /**
     * Realitza l'anàlisi de clustering sobre els usuaris que han respost una
     * enquesta.
     * 
     * Aquest mètode és el punt central per executar el procés complet de
     * clustering:
     * vectoritza les respostes dels participants, aplica l'algoritme de clustering
     * especificat, calcula les mètriques de qualitat (Silhouette), genera perfils
     * descriptius per cada cluster i els assigna automàticament als usuaris
     * corresponents.
     * 
     * Flux d'execució:
     * 1. Valida que l'enquesta existeix i té preguntes
     * 2. Recull i vectoritza les respostes de tots els participants
     * 3. Executa l'algoritme de clustering (KMeans, KMeans++ o KMedoids)
     * 4. Calcula el coeficient de Silhouette global i per cluster
     * 5. Genera un perfil descriptiu per cada cluster identificat
     * 6. Assigna cada perfil als usuaris del cluster corresponent
     * 7. Persisteix automàticament els perfils als usuaris
     * 
     * Els perfils generats contenen informació detallada sobre les característiques
     * del cluster, el seu centroide, la mida, la qualitat (Silhouette) i un nom
     * descriptiu basat en les respostes més representatives.
     * 
     * Validacions:
     * - L'enquesta ha d'existir i tenir preguntes
     * - Hi ha d'haver almenys k participants amb respostes
     * - Els usuaris han de tenir respostes vàlides per vectoritzar
     * 
     * @param idEnquesta   Identificador únic de l'enquesta a analitzar
     * @param k            Nombre de clusters a crear (ha de ser ≤ nombre de
     *                     participants)
     * @param usePlusPlus  true per usar KMeans++, false per KMeans estàndard
     *                     (ignorat si s'especifica algoritmeNom)
     * @param maxIters     Màxim nombre d'iteracions de l'algoritme
     * @param algoritmeNom Algoritme específic: "KMeans", "KMeans++" o "KMedoids"
     * @return ResultatClustering amb clusters generats, coeficients Silhouette,
     *         usuaris i vectors
     * @throws EnquestaNoExisteixException Si l'enquesta no existeix al sistema
     * @throws IllegalStateException       Si no hi ha preguntes o no hi ha prou
     *                                     participants
     * @see trobarMillorK(String, int, int, String, int)
     * @see ResultatClustering
     */
    public ResultatClustering analitzarEnquesta(String idEnquesta, int k, boolean usePlusPlus,
            int maxIters, String algoritmeNom)
            throws EnquestaNoExisteixException {

        // 0. Eliminar els perfils antics d'aquesta enquesta
        ctrlPersistencia.eliminarPerfilsEnquesta(idEnquesta);

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

        // 6.5. Calcular representants (punts reals més propers als centroides)
        DistanceCalculator dc = new DistanceCalculator();
        List<String[]> representants = new ArrayList<>();
        for (Kluster cluster : clusters) {
            String[] representant = cluster.getRepresentant(specs, dc);
            representants.add(representant);
        }

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
                    algoritmeNom);

            // Afegir el perfil al sistema de persistència (sense guardar encara per
            // eficiència)
            ctrlPersistencia.afegirPerfilSenseGuardar(perfilCluster);

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
                    }
                }
            }
        }

        // Guardar tots els canvis de perfils als usuaris
        ctrlPersistencia.flush();

        // 8. Retornar resultats
        return new ResultatClustering(clusters, silhouette, silhouettePerCluster,
                usernames, dataVectors, vectorToIndex, representants);
    }

    /**
     * Genera un nom descriptiu per a un cluster basat en les seves
     * característiques.
     * 
     * Aquest mètode analitza el centroide del cluster (valor representatiu de cada
     * pregunta)
     * i genera un nom intel·ligible que descriu el perfil del grup. La lògica
     * d'assignació
     * de noms depèn del tipus de pregunta i dels valors centrals del cluster:
     * 
     * - Per preguntes numèriques: classifica en rangs (Baix, Mitjà, Alt)
     * - Per preguntes qualitatives ordenades: utilitza el valor central directament
     * - Per preguntes de selecció múltiple: agafa l'opció més representativa
     * 
     * Si no es pot generar un nom descriptiu (per manca de dades significatives),
     * retorna
     * un nom genèric "Cluster N" on N és l'índex del cluster.
     * 
     * @param index     Índex del cluster (utilitzat per generar noms per defecte)
     * @param centroid  Vector amb els valors centrals de cada pregunta del cluster
     * @param preguntes Llista de preguntes de l'enquesta per interpretar els valors
     * @return Nom descriptiu del cluster basat en les seves característiques
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
                            if (num < 15)
                                return "Grup Baix";
                            else if (num < 30)
                                return "Grup Mitjà";
                            else
                                return "Grup Alt";
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
     * Crea una nova instància de Pregunta amb tipus i màxim de seleccions.
     * 
     * @param idPregunta    L'ID de la pregunta.
     * @param textPregunta  El text de la pregunta.
     * @param tp            El tipus de pregunta.
     * @param maxSeleccions El nombre màxim de seleccions.
     * @return Una nova instància de Pregunta.
     */
    public Pregunta crearPregunta(String idPregunta, String textPregunta, TipusPregunta tp, int maxSeleccions) {
        return new Pregunta(idPregunta, textPregunta, tp, maxSeleccions);
    }

    /**
     * Classe auxiliar que encapsula els resultats complets d'un procés de
     * clustering.
     * 
     * Aquesta classe actua com a contenidor immutable per a tota la informació
     * generada
     * durant l'anàlisi de clustering d'una enquesta. Facilita el retorn de
     * múltiples
     * dades relacionades en una única estructura organitzada.
     * 
     * Camps inclosos:
     * - clusters: Llista de Kluster amb les agrupacions identificades i els seus
     * centroides
     * - silhouetteGlobal: Coeficient de Silhouette global que mesura la qualitat
     * general del clustering
     * - silhouettePerCluster: Array amb el coeficient de Silhouette específic de
     * cada cluster
     * - usernames: Llista ordenada dels noms d'usuari dels participants analitzats
     * - dataVectors: Vectors de respostes corresponents a cada usuari (mateix ordre
     * que usernames)
     * - vectorToIndex: Mapa que permet localitzar l'índex d'un vector a partir del
     * seu contingut
     * 
     * Aquesta informació és útil per:
     * - Avaluar la qualitat del clustering realitzat
     * - Identificar quins usuaris pertanyen a cada cluster
     * - Analitzar les característiques de cada agrupació
     * - Depurar i validar els resultats de l'algoritme
     * 
     * Tots els camps són finals (immutables) per garantir la consistència de les
     * dades.
     */
    /**
     * Consulta els perfils de l'usuari actual.
     * 
     * @return Text amb la informació dels perfils de l'usuari.
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat.
     * @throws PerfilNoTrobatException     Si l'usuari no té cap perfil.
     */
    public String consultarPerfilsUsuari() throws UsuariNoAutenticatException, PerfilNoTrobatException {
        Usuari usuari = ctrlUsuari.getUsuariActual();
        if (usuari == null) {
            throw new UsuariNoAutenticatException("No hi ha cap usuari autenticat.");
        }
        String username = usuari.getUsername();

        // Obtenir tots els perfils de l'usuari
        HashMap<String, Perfil> perfils = usuari.getPerfils();

        if (perfils == null || perfils.isEmpty()) {
            throw new PerfilNoTrobatException(
                    "No tens cap perfil generat encara.\n\nPer generar un perfil:\n1. Respon una enquesta\n2. Espera que el creador de l'enquesta faci l'anàlisi de clustering\n3. Se t'assignarà automàticament un perfil basat en les teves respostes");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== ELS MEUS PERFILS ===\n\n");
        sb.append("Usuari: ").append(username).append("\n");
        sb.append("Total de perfils: ").append(perfils.size()).append("\n\n");

        int count = 1;
        for (java.util.Map.Entry<String, Perfil> entry : perfils.entrySet()) {
            Perfil perfil = entry.getValue();
            if (perfil == null)
                continue;

            sb.append("--- PERFIL ").append(count++).append(" ---\n");
            sb.append("Enquesta: ").append(perfil.getIdEnquesta()).append("\n");
            sb.append("Cluster: ").append(perfil.getClusterNom()).append("\n");
            sb.append("Descripció: ").append(perfil.getDescripcion()).append("\n");

            if (perfil.getClusterMida() != null) {
                sb.append("Membres del grup: ").append(perfil.getClusterMida()).append(" persones\n");
            }

            if (perfil.getAlgoritme() != null) {
                sb.append("Algoritme utilitzat: ").append(perfil.getAlgoritme()).append("\n");
            }

            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Consulta el perfil de l'usuari actual per a una enquesta específica.
     * 
     * @param idEnquesta L'ID de l'enquesta
     * @return Text amb la informació del perfil
     * @throws UsuariNoAutenticatException Si no hi ha cap usuari autenticat.
     * @throws PerfilNoTrobatException     Si no es troba el perfil.
     */
    public String consultarPerfilUsuariEnquesta(String idEnquesta)
            throws UsuariNoAutenticatException, PerfilNoTrobatException {
        Usuari usuari = ctrlUsuari.getUsuariActual();
        if (usuari == null) {
            throw new UsuariNoAutenticatException("No hi ha cap usuari autenticat.");
        }
        String username = usuari.getUsername();

        // Obtenir el perfil específic de l'enquesta
        HashMap<String, Perfil> perfils = usuari.getPerfils();

        // Comprovar si existeix algun perfil per a aquesta enquesta en el sistema
        boolean existeixAnalisi = false;
        HashMap<String, Perfil> totsPerfils = ctrlPersistencia.getAllPerfils();
        if (totsPerfils != null) {
            for (Perfil p : totsPerfils.values()) {
                if (p.teClustering() && idEnquesta.equals(p.getIdEnquesta())) {
                    existeixAnalisi = true;
                    break;
                }
            }
        }

        if (perfils == null || !perfils.containsKey(idEnquesta)) {
            if (existeixAnalisi) {
                // Hi ha anàlisi però l'usuari no té perfil = va contestar després
                throw new PerfilNoTrobatException("⚠️ NO TENS PERFIL ASSIGNAT ⚠️\n\n" +
                        "Has contestat aquesta enquesta DESPRÉS que el creador fes l'anàlisi de clustering.\n\n" +
                        "Per obtenir el teu perfil:\n" +
                        "• Espera que el creador torni a fer un nou anàlisi de clustering\n" +
                        "• El nou anàlisi inclourà les teves respostes\n" +
                        "• Aleshores se t'assignarà un perfil automàticament");
            } else {
                // No hi ha anàlisi encara
                throw new PerfilNoTrobatException("No tens cap perfil per a l'enquesta: " + idEnquesta + "\n\n" +
                        "Per generar un perfil:\n" +
                        "1. Respon l'enquesta si encara no ho has fet\n" +
                        "2. Espera que el creador de l'enquesta faci l'anàlisi de clustering\n" +
                        "3. Se t'assignarà automàticament un perfil basat en les teves respostes");
            }
        }

        Perfil perfil = perfils.get(idEnquesta);

        if (perfil == null) {
            if (existeixAnalisi) {
                // Hi ha anàlisi però el perfil és null
                throw new PerfilNoTrobatException("⚠️ NO TENS PERFIL ASSIGNAT ⚠️\n\n" +
                        "Has contestat aquesta enquesta DESPRÉS que el creador fes l'anàlisi de clustering.\n\n" +
                        "Per obtenir el teu perfil:\n" +
                        "• Espera que el creador torni a fer un nou anàlisi de clustering\n" +
                        "• El nou anàlisi inclourà les teves respostes\n" +
                        "• Aleshores se t'assignarà un perfil automàticament");
            } else {
                throw new PerfilNoTrobatException("El perfil per a aquesta enquesta encara no s'ha generat.");
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== EL MEU PERFIL ===\n\n");
        sb.append("Usuari: ").append(username).append("\n");
        sb.append("Enquesta: ").append(perfil.getIdEnquesta()).append("\n\n");

        sb.append("Cluster: ").append(perfil.getClusterNom()).append("\n");
        sb.append("Descripció: ").append(perfil.getDescripcion()).append("\n");

        if (perfil.getClusterMida() != null) {
            sb.append("Membres del grup: ").append(perfil.getClusterMida()).append(" persones\n");
        }

        if (perfil.getAlgoritme() != null) {
            sb.append("Algoritme utilitzat: ").append(perfil.getAlgoritme()).append("\n");
        }

        // Mostrar el vector característic si està disponible
        if (perfil.getVectorCaracteristic() != null && perfil.getNomsPreguntes() != null) {
            sb.append("\n--- Característiques del teu perfil ---\n");
            String[] vector = perfil.getVectorCaracteristic();
            java.util.List<String> preguntes = perfil.getNomsPreguntes();

            for (int i = 0; i < Math.min(vector.length, preguntes.size()); i++) {
                sb.append(preguntes.get(i)).append(": ").append(vector[i]).append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * Consulta l'anàlisi de clustering d'una enquesta.
     * Mostra tots els perfils/clusters generats per a aquesta enquesta.
     * 
     * @param idEnquesta L'ID de l'enquesta
     * @return Text amb la informació de tots els clusters
     * @throws AnalisiNoRealitzatException Si no s'ha realitzat l'anàlisi.
     */
    public String consultarAnalisiEnquesta(String idEnquesta) throws AnalisiNoRealitzatException {
        // Obtenir tots els perfils del sistema
        HashMap<String, Perfil> totsPerfils = ctrlPersistencia.getAllPerfils();

        if (totsPerfils == null || totsPerfils.isEmpty()) {
            throw new AnalisiNoRealitzatException(idEnquesta);
        }

        // Filtrar perfils per aquesta enquesta
        List<Perfil> perfilsEnquesta = new ArrayList<>();
        for (Perfil perfil : totsPerfils.values()) {
            if (perfil.teClustering() && idEnquesta.equals(perfil.getIdEnquesta())) {
                perfilsEnquesta.add(perfil);
            }
        }

        if (perfilsEnquesta.isEmpty()) {
            throw new AnalisiNoRealitzatException(idEnquesta);
        }

        // Ordenar per índex de cluster
        perfilsEnquesta.sort((p1, p2) -> Integer.compare(p1.getClusterIndex(), p2.getClusterIndex()));

        StringBuilder sb = new StringBuilder();
        sb.append("=== ANÀLISI DE CLUSTERING ===\n\n");
        sb.append("Enquesta: ").append(idEnquesta).append("\n");
        sb.append("Total de clusters: ").append(perfilsEnquesta.size()).append("\n");

        if (!perfilsEnquesta.isEmpty()) {
            Perfil primer = perfilsEnquesta.get(0);
            if (primer.getAlgoritme() != null) {
                sb.append("Algoritme utilitzat: ").append(primer.getAlgoritme()).append("\n");
            }
        }

        sb.append("\n");

        // Mostrar cada cluster
        for (Perfil perfil : perfilsEnquesta) {
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            sb.append("CLUSTER ").append(perfil.getClusterIndex() + 1)
                    .append(": ").append(perfil.getClusterNom()).append("\n");
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

            sb.append("Descripció: ").append(perfil.getDescripcion()).append("\n");

            if (perfil.getClusterMida() != null) {
                sb.append("Membres: ").append(perfil.getClusterMida()).append(" persones\n");
            }

            if (perfil.getClusterSilhouette() != null) {
                sb.append("Qualitat: ").append(perfil.getQualitatText()).append("\n");
                sb.append("Coeficient Silhouette: ")
                        .append(String.format("%.3f", perfil.getClusterSilhouette())).append("\n");
            }

            // Mostrar el vector característic
            if (perfil.getVectorCaracteristic() != null && perfil.getNomsPreguntes() != null) {
                sb.append("\nCaracterístiques representatives:\n");
                String[] vector = perfil.getVectorCaracteristic();
                List<String> preguntes = perfil.getNomsPreguntes();

                for (int i = 0; i < Math.min(vector.length, preguntes.size()); i++) {
                    sb.append("  • ").append(preguntes.get(i))
                            .append(": ").append(vector[i]).append("\n");
                }
            }

            sb.append("\n");
        }

        // Calcular i mostrar estadístiques generals
        double silhouetteMitja = perfilsEnquesta.stream()
                .filter(p -> p.getClusterSilhouette() != null)
                .mapToDouble(Perfil::getClusterSilhouette)
                .average()
                .orElse(0.0);

        int totalMembres = perfilsEnquesta.stream()
                .filter(p -> p.getClusterMida() != null)
                .mapToInt(Perfil::getClusterMida)
                .sum();

        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("ESTADÍSTIQUES GENERALS\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("Total de participants analitzats: ").append(totalMembres).append("\n");
        sb.append("Qualitat mitjana dels clusters: ")
                .append(String.format("%.3f", silhouetteMitja)).append("\n");

        return sb.toString();
    }

    public static class ResultatClustering {
        public final List<Kluster> clusters;
        public final double silhouetteGlobal;
        public final double[] silhouettePerCluster;
        public final List<String> usernames;
        public final List<String[]> dataVectors;
        public final HashMap<String, Integer> vectorToIndex;
        /** Representants (punts reals més propers al centroide) de cada cluster */
        public final List<String[]> representants;

        public ResultatClustering(List<Kluster> clusters, double silhouetteGlobal,
                double[] silhouettePerCluster, List<String> usernames,
                List<String[]> dataVectors, HashMap<String, Integer> vectorToIndex,
                List<String[]> representants) {
            this.clusters = clusters;
            this.silhouetteGlobal = silhouetteGlobal;
            this.silhouettePerCluster = silhouettePerCluster;
            this.usernames = usernames;
            this.dataVectors = dataVectors;
            this.vectorToIndex = vectorToIndex;
            this.representants = representants;
        }

        /**
         * Obté el nom d'usuari del representant d'un cluster.
         * 
         * @param clusterIndex Índex del cluster (0-based)
         * @return Nom d'usuari del representant, o null si no es troba
         */
        public String getUsernameRepresentant(int clusterIndex) {
            if (clusterIndex < 0 || clusterIndex >= representants.size())
                return null;

            String[] representant = representants.get(clusterIndex);
            String vectorKey = String.join("|", representant);
            Integer idx = vectorToIndex.get(vectorKey);

            if (idx != null && idx >= 0 && idx < usernames.size()) {
                return usernames.get(idx);
            }
            return null;
        }

        /**
         * Obté el vector representant d'un cluster.
         * 
         * @param clusterIndex Índex del cluster (0-based)
         * @return Vector representant, o null si índex invàlid
         */
        public String[] getRepresentant(int clusterIndex) {
            if (clusterIndex < 0 || clusterIndex >= representants.size())
                return null;
            return representants.get(clusterIndex);
        }
    }
}

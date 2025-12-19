package edu.upc.prop.clusterxx.persistencia;

import edu.upc.prop.clusterxx.domini.classes.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 * Gestor encarregat de la persistència de les enquestes en el sistema de fitxers.
 * <p>
 * Aquesta classe gestiona l'emmagatzematge i recuperació de les enquestes utilitzant una estructura
 * jeràrquica de directoris i fitxers JSON. Actua com a punt d'entrada principal per a la persistència
 * d'enquestes, delegant la gestió detallada de preguntes i respostes als seus respectius gestors
 * (GestorPreguntes i GestorRespostes) quan és necessari.
 * </p>
 * <p>
 * <strong>Estructura de fitxers:</strong>
 * <ul>
 *   <li><code>dades/enquestes/index.json</code>: Índex global amb metadades bàsiques de totes les enquestes.</li>
 *   <li><code>dades/enquestes/{id_enquesta}/</code>: Directori arrel per a una enquesta específica.</li>
 *   <li><code>dades/enquestes/{id_enquesta}/enquesta.json</code>: Fitxer amb la definició bàsica de l'enquesta.</li>
 *   <li><code>dades/enquestes/{id_enquesta}/preguntes/</code>: Subdirectori gestionat per GestorPreguntes.</li>
 *   <li><code>dades/enquestes/{id_enquesta}/respostes/</code>: Subdirectori gestionat per GestorRespostes.</li>
 * </ul>
 * </p>
 * <p>
 * <strong>Característiques tècniques:</strong>
 * <ul>
 *   <li>Utilitza <code>java.nio</code> per a operacions de fitxers eficients i modernes.</li>
 *   <li>Garanteix la codificació <strong>UTF-8</strong> en totes les operacions de lectura i escriptura.</li>
 *   <li>Implementa un algorisme de fusió (merge) optimitzat per a l'actualització de l'índex.</li>
 * </ul>
 * </p>
 * 
 * @author ClusterXX
 * @version 2.0
 */
public class GestorEnquestes {
    
    /** Ruta relativa al directori base on s'emmagatzemen les enquestes. */
    private static final String DIRECTORI_ENQUESTES = "dades/enquestes";
    
    /** Nom del fitxer d'índex global. */
    private static final String FITXER_INDEX = "index.json";
    
    /** Nom del fitxer que conté les dades bàsiques d'una enquesta. */
    private static final String FITXER_ENQUESTA = "enquesta.json";
    
    /** Nom del subdirectori per a les preguntes. */
    private static final String DIR_PREGUNTES = "preguntes";
    
    /** Nom del subdirectori per a les respostes. */
    private static final String DIR_RESPOSTES = "respostes";

    /**
     * Constructor per defecte.
     * Els directoris es crearan automàticament quan sigui necessari guardar dades.
     */
    public GestorEnquestes() {
        // No crear directoris fins que sigui necessari
    }

    /**
     * Guarda un conjunt d'enquestes al sistema de persistència.
     * <p>
     * Aquest mètode realitza dues operacions principals:
     * <ol>
     *   <li>Guarda individualment cada enquesta del mapa proporcionat, creant o actualitzant
     *       la seva estructura de directoris i fitxers.</li>
     *   <li>Actualitza l'índex global d'enquestes amb la informació de les enquestes proporcionades,
     *       mantenint les entrades existents que no estan en el mapa (merge).</li>
     * </ol>
     * </p>
     * 
     * @param enquestes Mapa que conté les enquestes a guardar, on la clau és l'ID de l'enquesta
     *                  i el valor és l'objecte Enquesta complet.
     * @throws IOException Si es produeix un error d'entrada/sortida durant l'escriptura dels fitxers.
     */
    public void guardarEnquestes(HashMap<String, Enquesta> enquestes) throws IOException {
        for (Enquesta enquesta : enquestes.values()) {
            guardarFitxerEnquesta(enquesta);
        }
        
        actualitzarIndex(enquestes);
    }

    /**
     * Guarda les dades bàsiques d'una enquesta en el seu fitxer corresponent.
     * <p>
     * Crea l'estructura de directoris necessària (incloent subdirectoris per preguntes i respostes)
     * i escriu el fitxer <code>enquesta.json</code> amb les metadades de l'enquesta (títol, descripció,
     * creador, participants).
     * També delega el guardat de preguntes i respostes als seus respectius gestors.
     * </p>
     * 
     * @param enquesta L'objecte Enquesta que es vol guardar.
     * @throws IOException Si no es poden crear els directoris o escriure el fitxer.
     */
    private void guardarFitxerEnquesta(Enquesta enquesta) throws IOException {
        Path dirEnquesta = Paths.get(DIRECTORI_ENQUESTES, enquesta.getId());
        if (!Files.exists(dirEnquesta)) Files.createDirectories(dirEnquesta);
        
        Path dirPreguntes = dirEnquesta.resolve(DIR_PREGUNTES);
        Path dirRespostes = dirEnquesta.resolve(DIR_RESPOSTES);
        if (!Files.exists(dirPreguntes)) Files.createDirectories(dirPreguntes);
        if (!Files.exists(dirRespostes)) Files.createDirectories(dirRespostes);
        
        JSONObject jsonEnquesta = new JSONObject();
        jsonEnquesta.put("id", enquesta.getId());
        jsonEnquesta.put("titol", enquesta.getTitol());
        jsonEnquesta.put("descripcio", enquesta.getDescripcio());
        jsonEnquesta.put("idCreador", enquesta.getIdCreador());
        
        JSONArray jsonParticipants = new JSONArray(enquesta.getParticipants());
        jsonEnquesta.put("participants", jsonParticipants);
        
        Path fitxerEnquesta = dirEnquesta.resolve(FITXER_ENQUESTA);
        Files.write(fitxerEnquesta, jsonEnquesta.toString(4).getBytes(StandardCharsets.UTF_8));

        // Delegar guardat de preguntes i respostes
        GestorPreguntes gestorPreguntes = new GestorPreguntes();
        gestorPreguntes.guardarPreguntes(enquesta.getId(), enquesta.getPreguntes());

        GestorRespostes gestorRespostes = new GestorRespostes();
        gestorRespostes.guardarRespostes(enquesta.getId(), enquesta.getPreguntes());
    }

    /**
     * Actualitza el fitxer d'índex global amb les noves dades de les enquestes.
     * <p>
     * Implementa una estratègia de "Smart Merge" amb complexitat O(N+M):
     * <ol>
     *   <li>Llegeix l'índex actual a memòria (si existeix).</li>
     *   <li>Actualitza o afegeix les entrades corresponents a les enquestes proporcionades.</li>
     *   <li>Reescriu l'índex complet al disc.</li>
     * </ol>
     * Això assegura que no es perdin les dades d'enquestes que no estan sent modificades en aquesta operació.
     * </p>
     * 
     * @param enquestesActualitzades Mapa amb les enquestes que s'han d'actualitzar o afegir a l'índex.
     * @throws IOException Si hi ha errors llegint o escrivint el fitxer d'índex.
     */
    private void actualitzarIndex(HashMap<String, Enquesta> enquestesActualitzades) throws IOException {
        Path indexPath = Paths.get(DIRECTORI_ENQUESTES, FITXER_INDEX);
        
        HashMap<String, JSONObject> indexMap = new HashMap<>();

        if (Files.exists(indexPath)) {
            String content = new String(Files.readAllBytes(indexPath), StandardCharsets.UTF_8);
            if (!content.isEmpty()) {
                JSONArray currentArray = new JSONArray(content);
                for (int i = 0; i < currentArray.length(); i++) {
                    JSONObject entry = currentArray.getJSONObject(i);
                    indexMap.put(entry.getString("id"), entry);
                }
            }
        }

        for (Enquesta enquesta : enquestesActualitzades.values()) {
            JSONObject nouEntry = new JSONObject();
            nouEntry.put("id", enquesta.getId());
            nouEntry.put("titol", enquesta.getTitol());
            nouEntry.put("descripcio", enquesta.getDescripcio());
            nouEntry.put("creador", enquesta.getIdCreador());
            nouEntry.put("numPreguntes", enquesta.getPreguntes().size());
            nouEntry.put("numParticipants", enquesta.getNumParticipants());

            indexMap.put(enquesta.getId(), nouEntry);
        }

        JSONArray finalArray = new JSONArray(indexMap.values());
        Files.write(indexPath, finalArray.toString(4).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Elimina completament una enquesta del sistema de persistència.
     * <p>
     * Aquesta operació és destructiva i irreversible. Realitza:
     * <ol>
     *   <li>L'eliminació recursiva del directori de l'enquesta i tot el seu contingut (preguntes, respostes, configuració).</li>
     *   <li>L'eliminació de l'entrada corresponent a l'índex global.</li>
     * </ol>
     * </p>
     * 
     * @param idEnquesta L'identificador únic de l'enquesta a eliminar.
     * @throws IOException Si hi ha errors durant l'esborrat de fitxers o l'actualització de l'índex.
     */
    public void eliminarEnquestaCompleta(String idEnquesta) throws IOException {
        Path dirEnquesta = Paths.get(DIRECTORI_ENQUESTES, idEnquesta);
        if (Files.exists(dirEnquesta)) {
            try (Stream<Path> walk = Files.walk(dirEnquesta)) {
                walk.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            }
        }

        eliminarEnquestaDeIndex(idEnquesta);
    }

    /**
     * Adaptador no-throw que elimina una enquesta completa. Alguns callers (p.ex. CtrlPersistencia)
     * esperen un mètode que no llenci excepcions; aquest envolta la versió que pot llençar IOException.
     * 
     * @param idEnquesta L'identificador únic de l'enquesta a eliminar
     */
    public void eliminarFitxerEnquesta(String idEnquesta) {
        try {
            eliminarEnquestaCompleta(idEnquesta);
        } catch (IOException e) {
            System.err.println("Error eliminant enquesta " + idEnquesta + ": " + e.getMessage());
        }
    }

    /**
     * Elimina una enquesta específica del fitxer d'índex global.
     * <p>
     * Llegeix l'índex, filtra l'entrada corresponent a l'ID proporcionat i reescriu el fitxer
     * només si s'ha trobat i eliminat l'entrada.
     * </p>
     * 
     * @param idEnquesta L'identificador de l'enquesta a eliminar de l'índex.
     * @throws IOException Si hi ha errors de lectura o escriptura.
     */
    private void eliminarEnquestaDeIndex(String idEnquesta) throws IOException {
        Path indexPath = Paths.get(DIRECTORI_ENQUESTES, FITXER_INDEX);
        if (!Files.exists(indexPath)) return;

        String content = new String(Files.readAllBytes(indexPath), StandardCharsets.UTF_8);
        if (content.isEmpty()) return;

        JSONArray jsonArray = new JSONArray(content);
        JSONArray newArray = new JSONArray();
        boolean found = false;
        
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonEntry = jsonArray.getJSONObject(i);
            if (!jsonEntry.getString("id").equals(idEnquesta)) {
                newArray.put(jsonEntry);
            } else {
                found = true;
            }
        }

        if (found) {
            Files.write(indexPath, newArray.toString(4).getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * Carrega totes les enquestes disponibles al sistema.
     * <p>
     * Explora el directori d'enquestes i carrega les dades bàsiques de cada enquesta trobada.
     * També carrega les preguntes associades a cada enquesta.
     * </p>
     * 
     * @param usuaris Mapa d'usuaris existents per vincular l'enquesta amb el seu creador.
     * @return Un HashMap amb totes les enquestes carregades, indexades per ID.
     * @throws IOException Si hi ha errors generals d'accés al sistema de fitxers.
     */
    public HashMap<String, Enquesta> carregarEnquestes(HashMap<String, Usuari> usuaris) throws IOException {
        HashMap<String, Enquesta> enquestes = new HashMap<>();
        File dir = new File(DIRECTORI_ENQUESTES);

        if (!dir.exists()) return enquestes;

        File[] subdirs = dir.listFiles(File::isDirectory);
        if (subdirs == null) return enquestes;

        GestorPreguntes gestorPreguntes = new GestorPreguntes();
        GestorRespostes gestorRespostes = new GestorRespostes();
        
        for (File subdir : subdirs) {
            try {
                String idEnquesta = subdir.getName();
                Enquesta enquesta = carregarDadesBasiquesEnquesta(idEnquesta, usuaris);
                if (enquesta != null) {
                    // Carregar preguntes associades
                    try {
                        ArrayList<Pregunta> preguntes = gestorPreguntes.carregarPreguntes(idEnquesta);
                        for (Pregunta p : preguntes) {
                            enquesta.afegirPregunta(p);
                        }
                    } catch (Exception e) {
                        System.err.println("Error carregant preguntes per enquesta " + idEnquesta + ": " + e.getMessage());
                    }

                    // Carregar respostes associades
                    try {
                        for (String participant : enquesta.getParticipants()) {
                            HashMap<String, Resposta> respostesUsuari = gestorRespostes.carregarRespostesUsuari(
                                    idEnquesta, participant, usuaris);
                            
                            for (Resposta r : respostesUsuari.values()) {
                                Pregunta p = enquesta.getPregunta(r.getIdPregunta());
                                if (p != null) {
                                    p.afegirResposta(participant, r);
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error carregant respostes per enquesta " + idEnquesta + ": " + e.getMessage());
                    }

                    enquestes.put(enquesta.getId(), enquesta);
                }
            } catch (Exception e) {
                System.err.println("Error carregant enquesta " + subdir.getName() + ": " + e.getMessage());
            }
        }
        return enquestes;
    }

    /**
     * Carrega les dades bàsiques d'una enquesta específica des del seu fitxer JSON.
     * <p>
     * Recupera informació com el títol, descripció, creador i llista de participants.
     * També estableix la relació bidireccional amb l'usuari creador si aquest existeix al mapa proporcionat.
     * </p>
     * 
     * @param idEnquesta L'identificador de l'enquesta a carregar.
     * @param usuaris Mapa d'usuaris per resoldre la referència al creador.
     * @return L'objecte Enquesta carregat, o <code>null</code> si el fitxer no existeix.
     * @throws IOException Si hi ha errors de lectura del fitxer.
     */
    public Enquesta carregarDadesBasiquesEnquesta(String idEnquesta, HashMap<String, Usuari> usuaris) throws IOException {
        Path path = Paths.get(DIRECTORI_ENQUESTES, idEnquesta, FITXER_ENQUESTA);
        if (!Files.exists(path)) return null;
        
        String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        JSONObject jsonEnquesta = new JSONObject(content);
        
        String id = jsonEnquesta.getString("id");
        String titol = jsonEnquesta.getString("titol");
        String descripcio = jsonEnquesta.getString("descripcio");
        String idCreador = jsonEnquesta.getString("idCreador");

        Usuari creador = null;
        if (usuaris != null) creador = usuaris.get(idCreador);
        if (creador == null) creador = new Usuari(idCreador, "unknown");

        Enquesta enquesta = new Enquesta(id, titol, descripcio, creador);

        if (jsonEnquesta.has("participants")) {
            JSONArray jsonParticipants = jsonEnquesta.getJSONArray("participants");
            for (int k = 0; k < jsonParticipants.length(); k++) {
                enquesta.registrarParticipacio(jsonParticipants.getString(k));
            }
        }

        if (creador != null) creador.addEnquestaCreada(enquesta);

        return enquesta;
    }
    
    /**
     * Obté el contingut de l'índex global d'enquestes.
     * <p>
     * Aquest mètode és útil per obtenir una llista ràpida de totes les enquestes disponibles
     * amb les seves metadades bàsiques sense necessitat de carregar i analitzar cada fitxer d'enquesta individualment.
     * </p>
     * 
     * @return Un JSONArray amb la llista d'enquestes i les seves dades resumides.
     * @throws IOException Si hi ha errors llegint el fitxer d'índex.
     */
    public JSONArray obtenirIndexEnquestes() throws IOException {
        Path indexPath = Paths.get(DIRECTORI_ENQUESTES, FITXER_INDEX);
        if (!Files.exists(indexPath)) return new JSONArray();
        
        String content = new String(Files.readAllBytes(indexPath), StandardCharsets.UTF_8);
        return content.isEmpty() ? new JSONArray() : new JSONArray(content);
    }

    /**
     * Guarda una única enquesta (adaptador). Els tests i altres callers poden cridar
     * aquest mètode senzill sense gestionar IOException.
     * També guarda les preguntes i respostes associades si existeixen.
     * 
     * @param enquesta L'enquesta a guardar
     */
    public void guardarEnquesta(Enquesta enquesta) {
        try {
            guardarFitxerEnquesta(enquesta);
            HashMap<String, Enquesta> map = new HashMap<>();
            map.put(enquesta.getId(), enquesta);
            actualitzarIndex(map);
            
            // Guardar preguntes si n'hi ha
            if (enquesta.getPreguntes() != null && !enquesta.getPreguntes().isEmpty()) {
                GestorPreguntes gestorPreguntes = new GestorPreguntes();
                gestorPreguntes.guardarPreguntes(enquesta.getId(), enquesta.getPreguntes());
            }
        } catch (IOException e) {
            System.err.println("Error guardant enquesta " + enquesta.getId() + ": " + e.getMessage());
        }
    }
}
package edu.upc.prop.clusterxx.presentacio;

import edu.upc.prop.clusterxx.domini.controladors.CtrlDomini;

import edu.upc.prop.clusterxx.domini.classes.Exceptions.PerfilNoTrobatException;
import edu.upc.prop.clusterxx.domini.classes.Exceptions.AnalisiNoRealitzatException;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

/**
 * Controlador principal de la capa de presentació.
 * Coordina les vistes i comunica amb el controlador de domini.
 */
public class CtrlPresentacio {
    private CtrlDomini ctrlDomini;
    private VistaPrincipal vistaPrincipal;

    private String currentUsername;

    /**
     * Constructor del controlador de presentació.
     * Inicialitza el controlador de domini i la vista principal.
     */
    public CtrlPresentacio() {
        ctrlDomini = new CtrlDomini();
        vistaPrincipal = new VistaPrincipal(this);
    }

    /**
     * Inicialitza la presentació fent visible la finestra principal.
     */
    public void inicializarPresentacio() {
        vistaPrincipal.hacerVisible();
    }

    /**
     * Intenta autenticar a un usuario en el sistema.
     * 
     * @param user Nombre de usuario
     * @param pass Contraseña
     * @return true si el login es correcto, false si falla (o lanza excepción)
     */
    public boolean login(String user, String pass) {
        try {
            ctrlDomini.login(user, pass);
            this.currentUsername = user; // Guardar usuario actual
            return true;
        } catch (Exception e) {
            System.out.println("Error en login: " + e.getMessage());
            return false;
        }
    }

    /**
     * Crea una nova enquesta al sistema.
     * 
     * @param id    Identificador únic de l'enquesta.
     * @param titol Títol de l'enquesta.
     * @param desc  Descripció de l'enquesta.
     * @return Un missatge indicant si l'operació ha estat exitosa o l'error
     *         produït.
     */
    public String crearEnquesta(String id, String titol, String desc) {
        try {
            ctrlDomini.crearEnquesta(id, titol, desc);
            return "Enquesta creada correctament!";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Registra un nou usuari al sistema.
     * 
     * @param user Nom d'usuari.
     * @param pass Contrasenya de l'usuari.
     * @return Un missatge indicant si el registre ha estat exitós o l'error
     *         produït.
     */
    public String registrarUsuari(String user, String pass) {
        try {
            ctrlDomini.registrarUsuari(user, pass);
            return "Usuari registrat correctament!";
        } catch (Exception e) {
            return "Error al registrar: " + e.getMessage();
        }
    }

    /**
     * Obté la llista d'enquestes creades per l'usuari actualment autenticat.
     * 
     * @return Una llista de llistes amb [id, titol, descripcio]. Si hi ha un error,
     *         retorna una llista buida.
     */
    public ArrayList<ArrayList<String>> getEnquestesUsuari() {
        try {
            return ctrlDomini.getEnquestesUsuariRaw();
        } catch (Exception e) {
            System.out.println("Error al consultar enquestes: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Obté els participants d'una enquesta.
     * 
     * @param idEnquesta ID de l'enquesta.
     * @return Llista de noms d'usuari.
     */
    public ArrayList<String> getParticipantsEnquesta(String idEnquesta) {
        try {
            return ctrlDomini.getParticipantsEnquestaRaw(idEnquesta);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Modifica les dades d'una enquesta existent.
     * Permet actualitzar el títol i/o la descripció.
     * 
     * @param id       Identificador de l'enquesta a modificar.
     * @param nouTitol Nou títol per l'enquesta (si no és null ni buit).
     * @param novaDesc Nova descripció per l'enquesta (si no és null).
     * @return Un missatge indicant l'èxit o el fracàs de l'operació.
     */
    public String modificarEnquesta(String id, String nouTitol, String novaDesc) {
        try {
            if (nouTitol != null && !nouTitol.isEmpty()) {
                ctrlDomini.modificarTitolEnquesta(id, nouTitol);
            }
            if (novaDesc != null) {
                ctrlDomini.modificarDescripcioEnquesta(id, novaDesc);
            }
            return "Enquesta modificada correctament!";
        } catch (Exception e) {
            return "Error al modificar: " + e.getMessage();
        }
    }

    /**
     * Elimina una enquesta del sistema.
     * 
     * @param id Identificador de l'enquesta a eliminar.
     * @return Un missatge confirmant l'eliminació o descrivint l'error.
     */
    public String esborrarEnquesta(String id) {
        try {
            ctrlDomini.esborrarEnquesta(id);
            return "Enquesta eliminada correctament!";
        } catch (Exception e) {
            return "Error al eliminar: " + e.getMessage();
        }
    }

    /**
     * Importa una enquesta des d'un fitxer extern (format JSON).
     * 
     * @param path Ruta absoluta del fitxer a importar.
     * @return Un missatge indicant si la importació ha estat correcta o l'error.
     */
    public String importarEnquesta(String path) {
        try {
            ctrlDomini.importarEnquesta(path);
            return "Enquesta importada correctament!";
        } catch (Exception e) {
            return "Error al importar: " + e.getMessage();
        }
    }

    /**
     * Importa una resposta des d'un fitxer JSON.
     * 
     * @param path Ruta del fitxer JSON a importar.
     * @return Missatge de resultat.
     */
    public String importarResposta(String path) {
        try {
            ctrlDomini.importarRespostes(path);
            return "Respostes importades correctament!";
        } catch (Exception e) {
            return "Error al importar: " + e.getMessage();
        }
    }

    /**
     * Consulta totes les respostes d'una enquesta.
     * Només el creador de l'enquesta pot consultar-les.
     * 
     * @param idEnquesta L'ID de l'enquesta
     * @return HashMap amb clau=idPregunta i valor=ArrayList de totes les respostes
     */
    /**
     * Consulta totes les respostes d'una enquesta.
     * Només el creador de l'enquesta pot consultar-les.
     * 
     * @param idEnquesta L'ID de l'enquesta
     * @return HashMap amb clau=idPregunta i valor=ArrayList de respostes [username,
     *         text]
     */
    public HashMap<String, ArrayList<ArrayList<String>>> consultarRespostesEnquesta(
            String idEnquesta) {
        try {
            return ctrlDomini.consultarRespostesEnquestaRaw(idEnquesta);
        } catch (Exception e) {
            System.err.println("Error consultant respostes: " + e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Consulta totes les respostes d'una pregunta específica.
     * 
     * @param idPregunta L'ID de la pregunta
     * @return ArrayList de totes les respostes de la pregunta
     */
    /**
     * Consulta totes les respostes d'una pregunta específica.
     * 
     * @param idPregunta L'ID de la pregunta
     * @return ArrayList de totes les respostes de la pregunta en format [username,
     *         text]
     */
    public ArrayList<ArrayList<String>> consultarRespostesPregunta(String idPregunta) {
        try {
            return ctrlDomini.consultarRespostesPreguntaRaw(idPregunta);
        } catch (Exception e) {
            System.err.println("Error consultant respostes de la pregunta: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Tanca la sessió de l'usuari actual.
     */
    public void logout() {
        this.currentUsername = null;
        System.out.println("Sessió tancada.");
    }

    /**
     * Elimina el compte de l'usuari actualment autenticat.
     * 
     * @return Un missatge indicant el resultat de l'operació.
     */
    public String esborrarUsuariActual() {
        if (currentUsername == null) {
            return "Error: No hi ha cap usuari autenticat.";
        }
        try {
            ctrlDomini.eliminarUsuari(currentUsername);
            String userDeleted = currentUsername;
            logout(); // Tancar sessió localment
            return "L'usuari " + userDeleted + " ha estat eliminat correctament.";
        } catch (Exception e) {
            return "Error al eliminar usuari: " + e.getMessage();
        }
    }

    /**
     * Afegeix una nova pregunta a una enquesta.
     *
     * @param idEnquesta    L'ID de l'enquesta.
     * @param idPregunta    L'ID de la nova pregunta.
     * @param textPregunta  El text de la pregunta.
     * @param tipus         El tipus de pregunta (NUMERICA, TEXT_LLIURE, etc.).
     * @param min           Valor mínim (només per a NUMERICA).
     * @param max           Valor màxim (només per a NUMERICA).
     * @param opcions       Llista d'opcions (només per a QUALITATIVA).
     * @param maxSeleccions Màxim de seleccions (només per a QUALITATIVA_MULTIPLE).
     * @return Un missatge indicant el resultat de l'operació.
     */
    public String afegirPregunta(String idEnquesta, String idPregunta, String textPregunta, String tipus,
            Double min, Double max, ArrayList<String> opcions, int maxSeleccions) {
        try {
            ctrlDomini.afegirPregunta(idEnquesta, idPregunta, textPregunta, tipus, min, max, opcions, maxSeleccions);
            return "Pregunta afegida correctament.";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Modifica una pregunta existent en una enquesta.
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
     * @return Un missatge indicant el resultat de l'operació.
     */
    public String modificarPregunta(String idEnquesta, String idPregunta, String textPregunta, String tipus,
            Double min, Double max, ArrayList<String> opcions, int maxSeleccions) {
        try {
            ctrlDomini.modificarPregunta(idEnquesta, idPregunta, textPregunta, tipus, min, max, opcions, maxSeleccions);
            return "Pregunta modificada correctament.";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Obté les dades d'una pregunta específica en format cru.
     * 
     * @param idEnquesta L'ID de l'enquesta.
     * @param idPregunta L'ID de la pregunta.
     * @return ArrayList amb les dades de la pregunta (o null si no es troba/error).
     *         [0] ID (String)
     *         [1] Text (String)
     *         [2] Tipus (String)
     *         [3] Min (Double)
     *         [4] Max (Double)
     *         [5] Opcions (ArrayList<String>)
     *         [6] MaxSeleccions (Integer)
     */
    public ArrayList<Object> getDadesPregunta(String idEnquesta, String idPregunta) {
        try {
            return ctrlDomini.getDadesPregunta(idEnquesta, idPregunta);
        } catch (Exception e) {
            System.out.println("Error al recuperar dades pregunta: " + e.getMessage());
        }
        return null;
    }

    /**
     * Elimina una pregunta d'una enquesta.
     *
     * @param idEnquesta L'ID de l'enquesta.
     * @param idPregunta L'ID de la pregunta a eliminar.
     * @return Un missatge indicant el resultat de l'operació.
     */
    public String eliminarPregunta(String idEnquesta, String idPregunta) {
        try {
            ctrlDomini.eliminarPregunta(idEnquesta, idPregunta);
            return "Pregunta eliminada correctament.";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Obté totes les enquestes del sistema en format cru.
     * 
     * @return Llista de llistes amb [id, titol].
     */
    public ArrayList<ArrayList<String>> getAllEnquestes() {
        try {
            return ctrlDomini.getAllEnquestesRaw();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Obté les preguntes d'una enquesta en format cru.
     * 
     * @param idEnquesta ID de l'enquesta.
     * @return Llista de preguntes en format raw (Llista de llistes amb ID, Text,
     *         Tipus, etc.).
     */
    public ArrayList<ArrayList<Object>> getPreguntesEnquestaRaw(String idEnquesta) {
        try {
            return ctrlDomini.getPreguntesEnquestaRaw(idEnquesta);
        } catch (Exception e) {
            System.out.println("Error al recuperar preguntes: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * Envia les respostes d'una enquesta.
     * 
     * @param idEnquesta ID de l'enquesta.
     * @param respostes  Map amb les respostes (idPregunta -> textResposta).
     * @return Missatge de resultat.
     */
    public String contestarEnquesta(String idEnquesta, HashMap<String, String> respostes) {
        try {
            HashMap<String, String> mapRespostes = new HashMap<>();
            HashMap<String, String> mapIds = new HashMap<>();

            String username = currentUsername;

            for (Map.Entry<String, String> entry : respostes.entrySet()) {
                String idPregunta = entry.getKey();
                String text = entry.getValue();

                String idResposta = idPregunta + "_" + username;

                mapRespostes.put(idResposta, text);
                mapIds.put(idResposta, idPregunta);
            }

            ctrlDomini.contestarEnquesta(idEnquesta, mapRespostes, mapIds);
            return "Enquesta contestada correctament!";

        } catch (Exception e) {
            return "Error al contestar: " + e.getMessage();
        }
    }

    /**
     * Obté les respostes de l'usuari actual a una enquesta.
     * 
     * @param idEnquesta ID de l'enquesta.
     * @return Map amb idPregunta -> textResposta.
     */
    public HashMap<String, String> getRespostesUsuariEnquesta(String idEnquesta) {
        try {
            return ctrlDomini.getRespostesUsuariEnquestaRaw(idEnquesta, currentUsername);
        } catch (Exception e) {
            System.out.println("Error recuperant respostes usuari: " + e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Modifica una resposta existent.
     * 
     * @param idEnquesta   ID de l'enquesta.
     * @param idPregunta   ID de la pregunta.
     * @param novaResposta Nou text de la resposta.
     * @return Missatge de resultat.
     */
    public String modificarResposta(String idEnquesta, String idPregunta, String novaResposta) {
        try {
            ctrlDomini.modificarResposta(idEnquesta, idPregunta, novaResposta);
            return "Resposta modificada correctament.";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Esborra totes les respostes de l'usuari a una enquesta.
     * 
     * @param idEnquesta ID de l'enquesta.
     * @return Missatge de resultat.
     */
    public String esborrarRespostesEnquesta(String idEnquesta) {
        try {
            ctrlDomini.esborrarResposta(idEnquesta);
            return "Respostes esborrades correctament.";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Obté la llista d'enquestes que l'usuari actual ha contestat.
     * 
     * @return Llista d'enquestes contestades (ID, Títol).
     */
    public ArrayList<ArrayList<String>> getEnquestesContestades() {
        try {
            return ctrlDomini.getEnquestesContestadesRaw(currentUsername);
        } catch (Exception e) {
            System.out.println("Error filtrant enquestes contestades: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Analitza una enquesta amb l'algoritme de clustering especificat.
     * 
     * @param idEnquesta ID de l'enquesta
     * @param modeK      Mode de selecció de K: "manual", "aleatori" o "automatic"
     * @param kManual    Valor de K (només si modeK = "manual")
     * @param algoritme  Algoritme: "kmeans", "kmeans++" o "kmedoids"
     * @return Text amb els resultats de l'anàlisi
     */
    public String analitzarEnquesta(String idEnquesta, String modeK, int kManual, String algoritme) {
        try {
            return ctrlDomini.generarInformeAnalisi(idEnquesta, modeK, kManual, algoritme);
        } catch (Exception e) {
            return "Error en l'anàlisi: " + e.getMessage();
        }
    }

    /**
     * Consulta el perfil de l'usuari actual.
     * 
     * @return Text amb la informació del perfil
     */
    public String consultarMeuPerfil() {
        try {
            return ctrlDomini.consultarPerfilsUsuari();
        } catch (PerfilNoTrobatException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Error consultant el perfil: " + e.getMessage();
        }
    }

    /**
     * Consulta el perfil de l'usuari actual per a una enquesta específica.
     * 
     * @param idEnquesta L'ID de l'enquesta
     * @return Text amb la informació del perfil
     */
    public String consultarPerfilEnquesta(String idEnquesta) {
        try {
            return ctrlDomini.consultarPerfilUsuariEnquesta(idEnquesta);
        } catch (PerfilNoTrobatException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Error consultant el perfil: " + e.getMessage();
        }
    }

    /**
     * Consulta l'anàlisi de clustering d'una enquesta.
     * Mostra tots els perfils/clusters generats per a aquesta enquesta.
     * 
     * @param idEnquesta L'ID de l'enquesta
     * @return Text amb la informació de tots els clusters
     */
    public String consultarAnalisiEnquesta(String idEnquesta) {
        try {
            return ctrlDomini.consultarAnalisiEnquesta(idEnquesta);
        } catch (AnalisiNoRealitzatException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Error consultant l'anàlisi: " + e.getMessage();
        }
    }
}

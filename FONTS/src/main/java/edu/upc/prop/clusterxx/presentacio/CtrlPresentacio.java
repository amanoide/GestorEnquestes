package edu.upc.prop.clusterxx.presentacio;

import edu.upc.prop.clusterxx.domini.controladors.CtrlDomini;
import edu.upc.prop.clusterxx.domini.classes.Enquesta;
import edu.upc.prop.clusterxx.domini.classes.Pregunta;
import edu.upc.prop.clusterxx.domini.classes.Opcio;
import edu.upc.prop.clusterxx.domini.classes.TipusPregunta;
import java.util.ArrayList;

/**
 * Controlador principal de la capa de presentació.
 * Coordina les vistes i comunica amb el controlador de domini.
 */
public class CtrlPresentacio {
    private CtrlDomini ctrlDomini;
    private VistaPrincipal vistaPrincipal;

    private String currentUsername;

    public CtrlPresentacio() {
        ctrlDomini = new CtrlDomini();
        vistaPrincipal = new VistaPrincipal(this);
    }

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
     * @return Una llista d'objectes Enquesta. Si hi ha un error, retorna una llista
     *         buida.
     */
    public ArrayList<edu.upc.prop.clusterxx.domini.classes.Enquesta> getEnquestesUsuari() {
        try {
            return ctrlDomini.consultarEnquestes();
        } catch (Exception e) {
            System.out.println("Error al consultar enquestes: " + e.getMessage());
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

            if (p != null) {
                ctrlDomini.afegirPregunta(idEnquesta, p);
                return "Pregunta afegida correctament.";
            } else {
                return "Error: No s'ha pogut crear la pregunta.";
            }

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

            if (p != null) {
                ctrlDomini.modificarPregunta(idEnquesta, idPregunta, p);
                return "Pregunta modificada correctament.";
            } else {
                return "Error: No s'ha pogut crear l'objecte pregunta modificat.";
            }

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Obté les dades d'una pregunta específica.
     * 
     * @param idEnquesta L'ID de l'enquesta.
     * @param idPregunta L'ID de la pregunta.
     * @return Un objecte Pregunta (o null si no es troba/error).
     */
    public Pregunta getDadesPregunta(String idEnquesta, String idPregunta) {
        try {
            Enquesta enquesta = ctrlDomini.getEnquesta(idEnquesta);
            if (enquesta != null) {
                return enquesta.getPregunta(idPregunta);
            }
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
     * Obté totes les enquestes del sistema.
     * 
     * @return Llista de totes les enquestes.
     */
    public ArrayList<Enquesta> getAllEnquestes() {
        try {
            return ctrlDomini.consultarEnquestes();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Obté els objectes Pregunta d'una enquesta.
     * 
     * @param idEnquesta ID de l'enquesta.
     * @return Llista d'objectes Pregunta.
     */
    public ArrayList<Pregunta> getPreguntesEnquestaObjects(String idEnquesta) {
        try {
            Enquesta enquesta = ctrlDomini.getEnquesta(idEnquesta);
            if (enquesta != null) {
                return enquesta.getPreguntes();
            }
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
    public String contestarEnquesta(String idEnquesta, java.util.HashMap<String, String> respostes) {
        try {
            java.util.HashMap<String, String> mapRespostes = new java.util.HashMap<>();
            java.util.HashMap<String, String> mapIds = new java.util.HashMap<>();

            String username = currentUsername;

            for (java.util.Map.Entry<String, String> entry : respostes.entrySet()) {
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
    public java.util.HashMap<String, String> getRespostesUsuariEnquesta(String idEnquesta) {
        java.util.HashMap<String, String> respostesUsuari = new java.util.HashMap<>();
        try {
            Enquesta enquesta = ctrlDomini.getEnquesta(idEnquesta);
            if (enquesta != null && currentUsername != null) {
                for (Pregunta p : enquesta.getPreguntes()) {
                    if (p.teResposta(currentUsername)) {
                        respostesUsuari.put(p.getId(), p.getResposta(currentUsername).getTextResposta());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error recuperant respostes usuari: " + e.getMessage());
        }
        return respostesUsuari;
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
     * @return Llista d'enquestes contestades.
     */
    public java.util.ArrayList<Enquesta> getEnquestesContestades() {
        java.util.ArrayList<Enquesta> contestades = new java.util.ArrayList<>();
        try {
            java.util.ArrayList<Enquesta> totes = ctrlDomini.consultarEnquestes();
            if (currentUsername != null) {
                for (Enquesta e : totes) {
                    if (e.haRespostUsuari(currentUsername)) {
                        contestades.add(e);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error filtrant enquestes contestades: " + e.getMessage());
        }
        return contestades;
    }
}

package edu.upc.prop.clusterxx.presentacio;

import edu.upc.prop.clusterxx.domini.controladors.CtrlDomini;
import edu.upc.prop.clusterxx.domini.classes.Enquesta;
import edu.upc.prop.clusterxx.domini.classes.Pregunta;
import edu.upc.prop.clusterxx.domini.classes.Opcio;
import edu.upc.prop.clusterxx.domini.classes.TipusPregunta;
import edu.upc.prop.clusterxx.domini.classes.Resposta;
import java.util.HashMap;
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
    public ArrayList<Enquesta> getEnquestesUsuari() {
        try {
            return new ArrayList<>(ctrlDomini.consultarEnquestesDelUsuari());
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
    public HashMap<String, ArrayList<edu.upc.prop.clusterxx.domini.classes.Resposta>> consultarRespostesEnquesta(String idEnquesta) {
        try {
            return ctrlDomini.consultarRespostesEnquesta(idEnquesta);
        } catch (Exception e) {
            System.err.println("Error consultant respostes: " + e.getMessage());
            return new java.util.HashMap<>();
        }
    }

    /**
     * Consulta totes les respostes d'una pregunta específica.
     * 
     * @param idPregunta L'ID de la pregunta
     * @return ArrayList de totes les respostes de la pregunta
     */
    public ArrayList<Resposta> consultarRespostesPregunta(String idPregunta) {
        try {
            return ctrlDomini.consultarRespostesPregunta(idPregunta);
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
                            // Per a preguntes ordenades, l'ordre és la posició a la llista
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
                    p = new Pregunta(idPregunta, textPregunta, tp, maxSeleccions);
                    if (opcions != null) {
                        int i = 1;
                        for (String opcioText : opcions) {
                            // Per a preguntes ordenades, l'ordre és la posició a la llista
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

    /**
     * Analitza una enquesta amb l'algoritme de clustering especificat.
     * 
     * @param idEnquesta ID de l'enquesta
     * @param modeK Mode de selecció de K: "manual", "aleatori" o "automatic"
     * @param kManual Valor de K (només si modeK = "manual")
     * @param algoritme Algoritme: "kmeans", "kmeans++" o "kmedoids"
     * @return Text amb els resultats de l'anàlisi
     */
    public String analitzarEnquesta(String idEnquesta, String modeK, int kManual, String algoritme) {
        try {
            int k;
            String detallsK = "";
            
            // Obtenir el número de participants per ajustar kMax
            Enquesta enquesta = ctrlDomini.getEnquesta(idEnquesta);
            int numParticipants = enquesta.getNumParticipants();
            
            // Determinar el valor de K segons el mode
            if (modeK.equals("manual")) {
                k = kManual;
                detallsK = "K seleccionat manualment: " + k;
            } else if (modeK.equals("aleatori")) {
                k = ctrlDomini.escollirKAleatori(idEnquesta);
                detallsK = "K escollit aleatòriament: " + k;
            } else { // automatic
                // Ajustar kMax segons el número de participants (mínim 2, màxim numParticipants)
                int kMax = Math.min(10, numParticipants);
                if (kMax < 2) {
                    return "Error: Es necessiten almenys 2 participants per fer l'anàlisi automàtic.";
                }
                
                edu.upc.prop.clusterxx.domini.controladors.CtrlAnalisi.OptimalKResult optResult = 
                    ctrlDomini.trobarMillorK(idEnquesta, 2, kMax, algoritme, 100);
                k = optResult.bestK;
                detallsK = "K òptim trobat (Silhouette): " + k + " (coeficient: " + 
                          String.format("%.4f", optResult.bestSilhouette) + ")";
            }

            // Executar l'anàlisi
            boolean usePlusPlus = algoritme.equals("kmeans++");
            edu.upc.prop.clusterxx.domini.controladors.CtrlDomini.ResultatClustering resultat = 
                ctrlDomini.analitzarEnquesta(idEnquesta, k, usePlusPlus, 100, algoritme);

            // Formatar resultats
            StringBuilder sb = new StringBuilder();
            sb.append("=== RESULTATS DE L'ANÀLISI ===\n\n");
            sb.append("Enquesta: ").append(idEnquesta).append("\n");
            sb.append("Algoritme: ").append(algoritme.toUpperCase()).append("\n");
            sb.append(detallsK).append("\n");
            sb.append("Iteracions màximes: 100\n\n");
            
            sb.append("--- MÈTRIQUES GLOBALS ---\n");
            sb.append("Coeficient de Silhouette global: ").append(String.format("%.4f", resultat.silhouetteGlobal)).append("\n\n");
            
            sb.append("--- CLUSTERS TROBATS ---\n");
            for (int i = 0; i < resultat.clusters.size(); i++) {
                edu.upc.prop.clusterxx.domini.classes.Kluster cluster = resultat.clusters.get(i);
                sb.append("Cluster ").append(i).append(":\n");
                sb.append("  Mida: ").append(cluster.size()).append(" usuaris\n");
                sb.append("  Silhouette: ").append(String.format("%.4f", resultat.silhouettePerCluster[i])).append("\n");
                
                // Obtenir representant
                String usernameRep = resultat.getUsernameRepresentant(i);
                if (usernameRep != null) {
                    sb.append("  Representant: ").append(usernameRep).append("\n");
                    
                    // Obtenir perfil del representant
                    try {
                        edu.upc.prop.clusterxx.domini.classes.Perfil perfil = ctrlDomini.getPerfil(usernameRep);
                        if (perfil != null) {
                            sb.append("  Descripció perfil: ").append(perfil.getDescripcion()).append("\n");
                        }
                    } catch (Exception ignored) {}
                }
                sb.append("\n");
            }
            
            return sb.toString();
            
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
            if (currentUsername == null) {
                return "No hi ha cap usuari autenticat.";
            }
            
            // Obtenir l'usuari actual
            edu.upc.prop.clusterxx.domini.classes.Usuari usuari = ctrlDomini.getUsuariActual();
            if (usuari == null) {
                return "No s'ha trobat l'usuari: " + currentUsername;
            }
            
            // Obtenir tots els perfils de l'usuari
            java.util.HashMap<String, edu.upc.prop.clusterxx.domini.classes.Perfil> perfils = usuari.getPerfils();
            
            if (perfils == null || perfils.isEmpty()) {
                return "No tens cap perfil generat encara.\n\nPer generar un perfil:\n1. Respon una enquesta\n2. Espera que el creador de l'enquesta faci l'anàlisi de clustering\n3. Se t'assignarà automàticament un perfil basat en les teves respostes";
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("=== ELS MEUS PERFILS ===\n\n");
            sb.append("Usuari: ").append(currentUsername).append("\n");
            sb.append("Total de perfils: ").append(perfils.size()).append("\n\n");
            
            int count = 1;
            for (java.util.Map.Entry<String, edu.upc.prop.clusterxx.domini.classes.Perfil> entry : perfils.entrySet()) {
                edu.upc.prop.clusterxx.domini.classes.Perfil perfil = entry.getValue();
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
            if (currentUsername == null) {
                return "No hi ha cap usuari autenticat.";
            }
            
            // Obtenir l'usuari actual
            edu.upc.prop.clusterxx.domini.classes.Usuari usuari = ctrlDomini.getUsuariActual();
            if (usuari == null) {
                return "No s'ha trobat l'usuari: " + currentUsername;
            }
            
            // Obtenir el perfil específic de l'enquesta
            java.util.HashMap<String, edu.upc.prop.clusterxx.domini.classes.Perfil> perfils = usuari.getPerfils();
            
            // Comprovar si existeix algun perfil per a aquesta enquesta en el sistema
            boolean existeixAnalisi = false;
            java.util.HashMap<String, edu.upc.prop.clusterxx.domini.classes.Perfil> totsPerfils = 
                    ctrlDomini.getAllPerfils();
            if (totsPerfils != null) {
                for (edu.upc.prop.clusterxx.domini.classes.Perfil p : totsPerfils.values()) {
                    if (p.teClustering() && idEnquesta.equals(p.getIdEnquesta())) {
                        existeixAnalisi = true;
                        break;
                    }
                }
            }
            
            if (perfils == null || !perfils.containsKey(idEnquesta)) {
                if (existeixAnalisi) {
                    // Hi ha anàlisi però l'usuari no té perfil = va contestar després
                    return "⚠️ NO TENS PERFIL ASSIGNAT ⚠️\n\n" +
                           "Has contestat aquesta enquesta DESPRÉS que el creador fes l'anàlisi de clustering.\n\n" +
                           "Per obtenir el teu perfil:\n" +
                           "• Espera que el creador torni a fer un nou anàlisi de clustering\n" +
                           "• El nou anàlisi inclourà les teves respostes\n" +
                           "• Aleshores se t'assignarà un perfil automàticament";
                } else {
                    // No hi ha anàlisi encara
                    return "No tens cap perfil per a l'enquesta: " + idEnquesta + "\n\n" +
                           "Per generar un perfil:\n" +
                           "1. Respon l'enquesta si encara no ho has fet\n" +
                           "2. Espera que el creador de l'enquesta faci l'anàlisi de clustering\n" +
                           "3. Se t'assignarà automàticament un perfil basat en les teves respostes";
                }
            }
            
            edu.upc.prop.clusterxx.domini.classes.Perfil perfil = perfils.get(idEnquesta);
            
            if (perfil == null) {
                if (existeixAnalisi) {
                    // Hi ha anàlisi però el perfil és null
                    return "⚠️ NO TENS PERFIL ASSIGNAT ⚠️\n\n" +
                           "Has contestat aquesta enquesta DESPRÉS que el creador fes l'anàlisi de clustering.\n\n" +
                           "Per obtenir el teu perfil:\n" +
                           "• Espera que el creador torni a fer un nou anàlisi de clustering\n" +
                           "• El nou anàlisi inclourà les teves respostes\n" +
                           "• Aleshores se t'assignarà un perfil automàticament";
                } else {
                    return "El perfil per a aquesta enquesta encara no s'ha generat.";
                }
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("=== EL MEU PERFIL ===\n\n");
            sb.append("Usuari: ").append(currentUsername).append("\n");
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
            // Obtenir tots els perfils del sistema
            java.util.HashMap<String, edu.upc.prop.clusterxx.domini.classes.Perfil> totsPerfils = 
                    ctrlDomini.getAllPerfils();
            
            if (totsPerfils == null || totsPerfils.isEmpty()) {
                return "No hi ha cap anàlisi de clustering al sistema.";
            }
            
            // Filtrar perfils per aquesta enquesta
            java.util.List<edu.upc.prop.clusterxx.domini.classes.Perfil> perfilsEnquesta = new java.util.ArrayList<>();
            for (edu.upc.prop.clusterxx.domini.classes.Perfil perfil : totsPerfils.values()) {
                if (perfil.teClustering() && idEnquesta.equals(perfil.getIdEnquesta())) {
                    perfilsEnquesta.add(perfil);
                }
            }
            
            if (perfilsEnquesta.isEmpty()) {
                return "No s'ha realitzat cap anàlisi de clustering per a l'enquesta: " + idEnquesta + "\n\n" +
                       "Per generar l'anàlisi:\n" +
                       "1. Assegura't que l'enquesta té respostes\n" +
                       "2. Fes clic a 'Analitzar Enquesta'\n" +
                       "3. Selecciona els paràmetres de clustering";
            }
            
            // Ordenar per índex de cluster
            perfilsEnquesta.sort((p1, p2) -> 
                Integer.compare(p1.getClusterIndex(), p2.getClusterIndex()));
            
            StringBuilder sb = new StringBuilder();
            sb.append("=== ANÀLISI DE CLUSTERING ===\n\n");
            sb.append("Enquesta: ").append(idEnquesta).append("\n");
            sb.append("Total de clusters: ").append(perfilsEnquesta.size()).append("\n");
            
            if (!perfilsEnquesta.isEmpty()) {
                edu.upc.prop.clusterxx.domini.classes.Perfil primer = perfilsEnquesta.get(0);
                if (primer.getAlgoritme() != null) {
                    sb.append("Algoritme utilitzat: ").append(primer.getAlgoritme()).append("\n");
                }
            }
            
            sb.append("\n");
            
            // Mostrar cada cluster
            for (edu.upc.prop.clusterxx.domini.classes.Perfil perfil : perfilsEnquesta) {
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
                    java.util.List<String> preguntes = perfil.getNomsPreguntes();
                    
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
                    .mapToDouble(edu.upc.prop.clusterxx.domini.classes.Perfil::getClusterSilhouette)
                    .average()
                    .orElse(0.0);
            
            int totalMembres = perfilsEnquesta.stream()
                    .filter(p -> p.getClusterMida() != null)
                    .mapToInt(edu.upc.prop.clusterxx.domini.classes.Perfil::getClusterMida)
                    .sum();
            
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            sb.append("ESTADÍSTIQUES GENERALS\n");
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            sb.append("Total de participants analitzats: ").append(totalMembres).append("\n");
            sb.append("Qualitat mitjana dels clusters: ")
              .append(String.format("%.3f", silhouetteMitja)).append("\n");
            
            return sb.toString();
            
        } catch (Exception e) {
            return "Error consultant l'anàlisi: " + e.getMessage();
        }
    }
}

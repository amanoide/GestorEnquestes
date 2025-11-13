/**
 * @file CtrlDominiDriver.java
 * @author Joan Garvin Cardona
 * Driver per provar la classe CtrlDomini.
 * Permet provar les funcionalitats principals de gestió d'usuaris,
 * enquestes, preguntes i respostes.
 * 
 * IMPORTANT:
 * ESTA A MITJA IMPLEMENTACIÓ NO ESTÀ COMPLETA.
 * 
 * HE AÑADIDO COMENTARIOS EN LOS TESTS DONDE HE VIST QUE FALTABA ALGUNA COMPROBACIÓN EN LOS CONTROLADORES O CLASES INTERNAS
 * PUEDE QUE FALTEN ALGUNOS TESTS MÁS, PERO HE INTENTADO CUBRIR LAS FUNCIONALIDADES PRINCIPALES.
 */

package edu.upc.prop.clusterxx.controladors;

// Importacions dels controladors
import edu.upc.prop.clusterxx.domini.controladors.CtrlDomini;

// Importacions del paquet de domini per poder crear objectes
import edu.upc.prop.clusterxx.domini.classes.*;
import static edu.upc.prop.clusterxx.domini.classes.Exceptions.*;

// Importacions de Java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Driver per provar la classe CtrlDomini.
 * Permet provar les funcionalitats principals de gestió d'usuaris,
 * enquestes, preguntes i respostes.
 */
public class CtrlDominiDriver {

    private static Scanner in;
    private static CtrlDomini cd;

    /**
     * Mètode principal que executa el driver.
     */
    public static void main(String[] args) {
        init();
        System.out.println("Driver de testeig de CtrlDomini");

        String input = "";
        while (!input.equals("0") && !input.equals("sortir")) {
            mostra_metodes();
            input = in.nextLine();
            
            // Gestionem l'entrada en un mètode separat
            // i capturem excepcions per no aturar el driver
            try {
                gestionarEntrada(input);
            } catch (Exception e) {
                // Imprimeix el missatge d'error de les excepcions personalitzades
                System.err.println("S'HA PRODUÏT UN ERROR: " + e.getMessage());
                // e.printStackTrace(); // Descomentar per a més detalls de debugging
            }
        }
        in.close();
        System.out.println("Tancant el driver. Adéu!");
    }

    /**
     * Inicialitza l'Scanner i el CtrlDomini.
     */
    private static void init() {
        in = new Scanner(System.in);
        cd = new CtrlDomini();
    }

    /**
     * Mostra el menú d'opcions al driver.
     */
    private static void mostra_metodes() {
        System.out.println("\n--- Menú Principal ---");
        System.out.println("--- Gestió d'Usuaris ---");
        System.out.println("(1) Registrar Usuari");
        System.out.println("(2) Login");
        System.out.println("(3) Mostrar Usuari Actual");
        System.out.println("(4) Comprovar Contrasenya Usuari Actual");
        System.out.println("(5) Crear Perfil");
        System.out.println("(6) Mostrar Perfil");
        System.out.println("--- Gestió d'Enquestes ---");
        System.out.println("(10) Crear Enquesta");
        System.out.println("(11) Consultar Totes les Enquestes");
        System.out.println("(12) Esborrar Enquesta");
        System.out.println("(13) Modificar Títol Enquesta");
        System.out.println("--- Gestió de Preguntes ---");
        System.out.println("(20) Afegir Pregunta a Enquesta");
        System.out.println("(21) Eliminar Pregunta d'Enquesta");
        System.out.println("(22) Afegir Opció a Pregunta");
        System.out.println("(23) Eliminar Opció de Pregunta");
        System.out.println("(24) Modificar Pregunta d'Enquesta");
        System.out.println("--- Gestió de Respostes ---");
        System.out.println("(30) Contestar Enquesta");
        System.out.println("(31) Consultar Respostes d'Enquesta");
        System.out.println("(32) Modificar Resposta d'Enquesta");
        System.out.println("(33) Eliminar Resposta d'Enquesta");
        System.out.println("------------------------");
        System.out.println("(0|sortir) - Tancar driver");
        System.out.println("Escull una opció: ");
    }

    /**
     * Gestiona l'entrada de l'usuari amb un switch.
     * Llança excepcions si ocorren, per ser capturades al main.
     */
    private static void gestionarEntrada(String input) throws Exception {
        switch (input) {
            // --- Usuaris ---
            case "1":
                testRegistrarUsuari();
                break;
            case "2":
                testLogin();
                break;
            case "3":
                testMostrarUsuariActual();
                break;
            case "4":
                testcheckPassword();
                break;
            case "5":
                testcrearPerfil();
                break;
            case "6":
                testMostraPerfil();
                break;
            
            // --- Enquestes ---
            case "10":
                testCrearEnquesta();
                break;
            case "11":
                testConsultarEnquestes();
                break;
            case "12":
                testEsborrarEnquesta();
                break;
            case "13":
                testModificarTitolEnquesta();
                break;

            // --- Preguntes ---
            case "20":
                testAfegirPregunta();
                break;
            case "21":
                testEliminarPregunta();
                break;
            case "22":
                testAfegirOpcioAPregunta();
                break;
            case "23":
                testEliminarOpcioDePregunta();
                break;
            case "24":
                testmodificarPregunta();
                break;
                
            // --- Respostes ---
            case "30":
                testContestarEnquesta();
                break;
            case "31":
                testConsultarRespostesEnquesta();
                break;
            case "32":
                testmodificarResposta();
                break;
            case "33":
                testeliminarResposta();;
                break;

            case "0":
            case "sortir":
                break;
            default:
                System.out.println("Valor invàlid");
                break;
        }
    }

    // --- Mètodes de Test per a Usuaris ---

    private static void testRegistrarUsuari() throws UsuariJaExisteixException , Exception {
        System.out.println("Introdueix nom d'usuari: ");
        String username = in.nextLine();
        System.out.println("Introdueix contrasenya: ");
        String password = in.nextLine();
        cd.registrarUsuari(username, password);
        System.out.println("Usuari '" + username + "' registrat correctament.");
    }

    private static void testLogin() throws CredencialsIncorrectesException, Exception {
        System.out.println("Introdueix nom d'usuari: ");
        String username = in.nextLine();
        System.out.println("Introdueix contrasenya: ");
        String password = in.nextLine();
        cd.login(username, password);
        System.out.println("Login correcte. Benvingut, " + username + ".");
    }

    private static void testMostrarUsuariActual() {
        Usuari usuari = cd.getUsuariActual();
        if (usuari == null) {
            System.out.println("No hi ha cap usuari autenticat.");
        } else {
            System.out.println("Usuari actual: " + usuari.getUsername());
        }
    }

    private static void testcheckPassword() {
        System.out.println("Introdueix contrasenya a verificar: ");
        String password = in.nextLine();
        Usuari usuari = cd.getUsuariActual();
        if (usuari == null) {
            System.out.println("No hi ha cap usuari autenticat.");
            return;
        }
        boolean correcte = usuari.checkPassword(password);
        if (correcte) {
            System.out.println("La contrasenya és correcta.");
        } else {
            System.out.println("La contrasenya és incorrecta.");
        }
    }

    private static void testcrearPerfil() {
        System.out.println("Introdueix id del perfil: ");
        String id = in.nextLine();
        String descripcio = in.nextLine();
        cd.crearPerfil(id, descripcio);
        System.out.println("Perfil '" + id + " amb descripció " + descripcio + " creat correctament.");
    }

    private static void testMostraPerfil(){
        System.out.println("Introdueix id del perfil a mostrar: ");
        String id = in.nextLine();
        Perfil perfil = cd.getPerfil(id);
        if (perfil == null) {
            System.out.println("No s'ha trobat cap perfil amb id: " + id);
        } else {
            System.out.println("Perfil trobat: ID = " + perfil.getId() + ", Descripció = " + perfil.getDescripcion());
        }
    }

    // --- Mètodes de Test per a Enquestes ---

    /**
     * SE DEBERIA PONER UNA EXCEPCION DE SI EL USUARIO NO ESTA AUTENTICADO EN EL CONTROLADOR
     * TAMBIEN QUE NO SE PUEDA CREAR UNA ENCUESTA CON UN ID QUE YA EXISTA NO?
     */
    private static void testCrearEnquesta() throws ParametreInvalidException, EnquestaJaExisteixException, UsuariNoAutenticatException {
        Usuari actual = cd.getUsuariActual();
        if (actual == null) {
            System.out.println("ERROR: Cal estar autenticat per crear una enquesta.");
            return;
        }
        System.out.println("Introdueix ID enquesta: ");
        String id = in.nextLine();
        System.out.println("Introdueix títol: ");
        String titol = in.nextLine();
        System.out.println("Introdueix descripció: ");
        String descripcio = in.nextLine();
        
        // El mètode crearEnquesta de CtrlDomini ja fa la comprovació de permisos
        cd.crearEnquesta(actual, id, titol, descripcio);
        System.out.println("Enquesta '" + id + "' creada.");
    }

    private static void testConsultarEnquestes() {
        ArrayList<Enquesta> enquestes = cd.consultarEnquestes();
        if (enquestes.isEmpty()) {
            System.out.println("No hi ha enquestes.");
            return;
        }
        System.out.println("Llista d'enquestes:");
        for (Enquesta e : enquestes) {
            System.out.println("- ID: " + e.getId() + ", Títol: " + e.getTitol() + " (Creador: " + e.getIdCreador() + ")");
        }
    }

    private static void testEsborrarEnquesta() throws EnquestaNoExisteixException, PermisDenegatException, UsuariNoAutenticatException {
        System.out.println("Introdueix ID enquesta a esborrar: ");
        String id = in.nextLine();
        cd.esborrarEnquesta(id);
        System.out.println("Enquesta '" + id + "' esborrada.");
    }

    private static void testModificarTitolEnquesta() throws EnquestaNoExisteixException, PermisDenegatException {
        System.out.println("Introdueix ID enquesta: ");
        String id = in.nextLine();
        System.out.println("Introdueix nou títol: ");
        String nouTitol = in.nextLine();
        cd.modificarTitolEnquesta(id, nouTitol);
        System.out.println("Títol modificat.");
    }

    // --- Mètodes de Test per a Preguntes ---
    /*
     * NO ES COMPROVA SI LA PREGUNTA JA EXISTEIX A L'ENQUESTA?
     */

    private static void testAfegirPregunta() throws EnquestaNoExisteixException, PermisDenegatException {
        System.out.println("Introdueix ID enquesta on afegir la pregunta: ");
        String idEnquesta = in.nextLine();
        
        // Fem servir un mètode auxiliar per crear la pregunta
        Pregunta p = crearPreguntaInteractivament();
        
        cd.afegirPregunta(idEnquesta, p);
        System.out.println("Pregunta '" + p.getId() + "' afegida a l'enquesta '" + idEnquesta + "'.");
    }
/*
 * NO ES COMPROVA SI LA PREGUNTA EXISTEIX A L'ENQUESTA?
 */
    private static void testEliminarPregunta() throws EnquestaNoExisteixException, PermisDenegatException {
        System.out.println("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();
        System.out.println("Introdueix ID pregunta a eliminar: ");
        String idPregunta = in.nextLine();
        
        cd.eliminarPregunta(idEnquesta, idPregunta);
        System.out.println("Pregunta '" + idPregunta + "' eliminada.");
    }
/*
 * NO ES COMPROVA SI LA PREGUNTA EXISTEIX A L'ENQUESTA?
 */
    private static void testmodificarPregunta() throws EnquestaNoExisteixException, PermisDenegatException {
        System.out.println("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();
        System.out.println("Introdueix ID pregunta a modificar: ");
        String idPregunta = in.nextLine();
        Pregunta p = crearPreguntaInteractivament();
        cd.modificarPregunta(idEnquesta, idPregunta, p);
        System.out.println("Pregunta '" + idPregunta + "' modificada.");
    }
/*
 * NO ES COMPROVA SI LA PREGUNTA EXISTEIX A L'ENQUESTA?
 */
    private static void testAfegirOpcioAPregunta() throws Exception {
        System.out.println("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();
        System.out.println("Introdueix ID pregunta: ");
        String idPregunta = in.nextLine();
        
        System.out.println("Introdueix ID opció (número): ");
        int idOpcio = Integer.parseInt(in.nextLine());
        System.out.println("Introdueix text opció: ");
        String textOpcio = in.nextLine();
        System.out.println("Introdueix ordre (o 0 si no aplica): ");
        int ordre = Integer.parseInt(in.nextLine());

        Opcio o;
        if (ordre > 0) {
            o = new Opcio(idOpcio, textOpcio, ordre);
        } else {
            o = new Opcio(idOpcio, textOpcio);
        }
        
        cd.afegirOpcioAPregunta(idEnquesta, idPregunta, o);
        System.out.println("Opció afegida.");
    }

    private static void testEliminarOpcioDePregunta() throws Exception {
        System.out.println("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();
        System.out.println("Introdueix ID pregunta: ");
        String idPregunta = in.nextLine();
        System.out.println("Introdueix ID opció (número) a eliminar: ");
        int idOpcio = Integer.parseInt(in.nextLine());

        cd.eliminarOpcioDePregunta(idEnquesta, idPregunta, idOpcio);
        System.out.println("Opció eliminada.");
    }

    // --- Mètodes de Test per a Respostes ---

    /*
     * POR MUCHO QUE NO SE VALIDE UNA RESPUESTA SE MUESTRA IGUALMENTE
     */
    private static void testContestarEnquesta() throws EnquestaNoExisteixException, PreguntaNoExisteixException {
        Usuari actual = cd.getUsuariActual();
        if (actual == null) {
            System.out.println("ERROR: Cal estar autenticat per contestar.");
            return;
        }
        
        System.out.println("Introdueix ID enquesta a contestar: ");
        String idEnquesta = in.nextLine();
        
        // Obtenim l'enquesta per poder mostrar les preguntes
        Enquesta enquestaAContestar = null;
        for (Enquesta e : cd.consultarEnquestes()) {
            if (e.getId().equals(idEnquesta)) {
                enquestaAContestar = e;
                break;
            }
        }
        
        if (enquestaAContestar == null) {
            throw new EnquestaNoExisteixException(idEnquesta);
        }

        System.out.println("Contestant a: " + enquestaAContestar.getTitol());
        HashMap<String, String> respostes = new HashMap<>();
        HashMap<String, String> idsPreguntaPerResposta = new HashMap<>();
        ArrayList<Pregunta> preguntes = enquestaAContestar.getPreguntes();

        if (preguntes.isEmpty()) {
            System.out.println("Aquesta enquesta no té preguntes.");
            return;
        }

        int contadorResposta = 0;
        for (Pregunta p : preguntes) {
            System.out.println("\nPregunta: " + p.getText() + ", Tipus: " + p.getTipus() + ")");
            // Mostrem opcions si n'hi ha
            if (p.tipusAdmetOpcions()) {
                for (Opcio o : p.getOpcions()) {
                    System.out.println("  (" + o.getId() + ") " + o.getText());
                }
            }
            System.out.println("La teva resposta: ");
            String resposta = in.nextLine();
            
            // Generar ID únic per aquesta resposta
            String idResposta = "r" + contadorResposta++;
            respostes.put(idResposta, resposta);
            idsPreguntaPerResposta.put(idResposta, p.getId());
        }
        
        try {
            cd.contestarEnquesta(idEnquesta, respostes, idsPreguntaPerResposta);
            System.out.println("Enquesta contestada. Gràcies!");
        } catch (UsuariNoAutenticatException | EnquestaNoExisteixException | EnquestaJaContestadaException | PreguntaNoExisteixException | RespostaInvalidaException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
/*
         * No s'hauria d'afegir una comprovació de si l'enquesta existeix al ctrlador?
         * SI ELIMINAMOS UNA PREGUNTA I DESPRÉS VOLEM MOSTRAR LES RESPOSTES LA RESPOSTA NO S'ELIMINA
         * SI ELIMINAMOS UNA RESPUESTA LA SIGUE MOSTRANT
         */
    private static void testConsultarRespostesEnquesta() {
        System.out.println("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();
        
        
        HashMap<String, ArrayList<Resposta>> respostesPerUsuari = cd.consultarRespostesEnquesta(idEnquesta);
        
        if (respostesPerUsuari == null || respostesPerUsuari.isEmpty()) {
            System.out.println("No hi ha respostes per aquesta enquesta.");
            return;
        }

        System.out.println("Respostes de l'enquesta '" + idEnquesta + "':");
        for (Map.Entry<String, ArrayList<Resposta>> entry : respostesPerUsuari.entrySet()) {
            System.out.println("  Usuari: " + entry.getKey());
            for (Resposta r : entry.getValue()) {
                System.out.println("    - Pregunta " + r.getIdPregunta() + ": " + r.getTextResposta());
            }
        }
    }

    private static void testmodificarResposta() throws Exception {
        System.out.println("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();
        System.out.println("Introdueix ID pregunta a modificar: ");
        String idPregunta = in.nextLine();
        System.out.println("Introdueix nova resposta: ");
        String novaResposta = in.nextLine();
        
        cd.modificarResposta(idEnquesta, idPregunta, novaResposta);
        System.out.println("Resposta modificada correctament.");
    }
    
    private static void testeliminarResposta() throws Exception {
        System.out.println("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();
        
        cd.esborrarResposta(idEnquesta);
        System.out.println("Totes les respostes de l'enquesta han estat eliminades correctament.");
    }


    // --- Mètode Auxiliar per crear Preguntes ---

    /**
     * Mètode auxiliar interactiu per crear un objecte Pregunta.
     * @return un objecte Pregunta llest per ser afegit a una enquesta.
     */
    private static Pregunta crearPreguntaInteractivament() {
        System.out.println("Introdueix ID pregunta: ");
        String idPregunta = in.nextLine();
        System.out.println("Introdueix text pregunta: ");
        String text = in.nextLine();
        System.out.println("Tipus de pregunta:");
        System.out.println(" (1) Text Lliure");
        System.out.println(" (2) Numèrica");
        System.out.println(" (3) Qualitativa Ordenada (selecció única)");
        System.out.println(" (4) Qualitativa Simple (selecció única, no ordenada)");
        System.out.println(" (5) Qualitativa Múltiple (selecció múltiple, no ordenada)");
        System.out.println("Escull tipus: ");
        String tipus = in.nextLine();

        switch (tipus) {
            case "2": // Numèrica
                System.out.println("Valor mínim: ");
                double min = Double.parseDouble(in.nextLine());
                System.out.println("Valor màxim: ");
                double max = Double.parseDouble(in.nextLine());
                return new Pregunta(idPregunta, text, min, max);
            
            case "3": // Qualitativa Ordenada
                // El '1' indica maxSeleccions = 1
                return new Pregunta(idPregunta, text, TipusPregunta.QUALITATIVA_ORDENADA, 1);
            
            case "4": // Qualitativa Simple
                // El '1' indica maxSeleccions = 1
                return new Pregunta(idPregunta, text, TipusPregunta.QUALITATIVA_NO_ORDENADA_SIMPLE, 1);
            
            case "5": // Qualitativa Múltiple
                System.out.println("Màxim seleccions: ");
                int maxSel = Integer.parseInt(in.nextLine());
                return new Pregunta(idPregunta, text, TipusPregunta.QUALITATIVA_NO_ORDENADA_MULTIPLE, maxSel);
            
            case "1": // Text
                return new Pregunta(idPregunta, text);
            default:
                // Constructor per a preguntes de text lliure
                return new Pregunta(idPregunta, text);
        }
    }


}
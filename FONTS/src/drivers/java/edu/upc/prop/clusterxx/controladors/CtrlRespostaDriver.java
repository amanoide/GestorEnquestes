/* 


/**
 * Driver per a la classe CtrlResposta.
 * Permet provar les funcionalitats de registre, modificació, esborrat i consulta de respostes.7
 * S'utilitza per a la validació i testeig de la classe CtrlResposta.
 * 
 * @autor Joan Garvin Cardona
 * 
 * IMPORTANT:
 * ESTA A MITJES DE DESENVOLUPAMENT
 * 
 */

package edu.upc.prop.clusterxx.controladors;

// Importacions dels controladors
import edu.upc.prop.clusterxx.domini.controladors.CtrlResposta;

// Importacions dels stubs de domini
import edu.upc.prop.clusterxx.domini.classes.Enquesta;
import edu.upc.prop.clusterxx.domini.classes.Pregunta;
import edu.upc.prop.clusterxx.domini.classes.Resposta;
import edu.upc.prop.clusterxx.domini.classes.Usuari;
import static edu.upc.prop.clusterxx.domini.classes.Exceptions.*;

// Importacions de Java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Driver per provar la classe CtrlResposta.
 * Permet provar el registre, modificació, esborrat i consulta de respostes.
 * 
 * HE AÑADIDO COMENTARIOS EN LOS TESTS DONDE HE VIST QUE FALTABA ALGUNA COMPROBACIÓN EN LOS CONTROLADORES O CLASES INTERNAS
 * PUEDE QUE FALTEN ALGUNOS TESTS MÁS, PERO HE INTENTADO CUBRIR LAS FUNCIONALIDADES PRINCIPALES.
 */
public class CtrlRespostaDriver {

    private static Scanner in;
    private static CtrlResposta cr;
    /**
     * Mètode principal que executa el driver.
     */
    public static void main(String[] args) {
        init();
        System.out.println("Driver de testeig de CtrlResposta");

        String input = "";
        while (!input.equals("0") && !input.equals("sortir")) {
            mostra_metodes();
            input = in.nextLine();

            // Gestionem l'entrada i capturem errors
            try {
                gestionarEntrada(input);
            } catch (Exception e) {
                System.err.println("S'HA PRODUÏT UN ERROR D'ENTRADA: " + e.getMessage());
            }
        }
        in.close();
        System.out.println("Tancant el driver. Adéu!");
    }

    /**
     * Inicialitza l'Scanner i el CtrlResposta.
     */
    private static void init() {
        in = new Scanner(System.in);
        cr = new CtrlResposta();
    }

    /**
     * Mostra el menú d'opcions al driver.
     */
    private static void mostra_metodes() {
        System.out.println("\n--- Menú CtrlResposta ---");
        System.out.println("(1) Registrar Respostes d'Usuari a Enquesta");
        System.out.println("(2) Modificar Resposta");
        System.out.println("(3) Esborrar Resposta");
        System.out.println("--- Consultes ---");
        System.out.println("(10) Consultar Respostes d'una Enquesta");
        System.out.println("(11) Consultar Respostes d'un Usuari");
        System.out.println("(12) Consultar Núm. Participants d'Enquesta");
        System.out.println("(13) Consultar Núm. Total d'Enquestes amb Respostes");
        System.out.println("(14) Netejar Totes les Respostes");
        System.out.println("------------------------");
        System.out.println("(0|sortir) - Tancar driver");
        System.out.println("Escull una opció: ");
    }

    /**
     * Gestiona l'entrada de l'usuari amb un switch.
     */
    private static void gestionarEntrada(String input) throws Exception {
        switch (input) {
            case "1":
            case "Registrar Respostes d'Usuari a Enquesta":
                testRegistrarRespostes();
                break;
            case "2":
            case "Modificar Resposta":
                testModificarResposta();
                break;
            case "3":
            case "Esborrar Resposta":
                testEsborrarResposta();
                break;
            case "10":
            case "Consultar Respostes d'una Enquesta":
                testGetRespostesEnquesta();
                break;
            case "11":
            case "Consultar Respostes d'un Usuari":
                testGetRespostesUsuari();
                break;
            
                case "12":
            case "Consultar Núm. Participants d'Enquesta":
                testGetNumParticipants();
                break;
            case "13":
            case "Consultar Núm. Total d'Enquestes amb Respostes":
                testGetNumEnquestesAmbRespostes();
                break;
            case "14":
            case "Netejar Totes les Respostes":
                cr.netejarRespostes();
                System.out.println("Totes les respostes han estat esborrades.");
                break;
            case "0":
            case "sortir":
                break;
            default:
                System.out.println("Valor invàlid");
                break;
        }
    }


    // --- Mètodes de Test ---

    /**
     * Prova registrar un conjunt de respostes.
     * Crea objectes 'mock' d'Enquesta i Usuari.
     * 
     * 
     * PARA ESTA FUNCIONALIDAD NO SE DEBERIA NECESITAR EL OBJETO ENCUESTA SI NO EL ID DE LA ENCUESTA
     * ASI QUE IGUAL HAY QUE MODIFICARLO EN EL CONTROLADOR
     * 
     * USA MOCKS PARA CREAR EL USUARIO Y LA ENCUESTA FICTÍCIOS
     * 
     * DEBERIA HABER UNA EXCEPCION SI LA ENCUESTA NO EXISTE
     */
    private static void testRegistrarRespostes() {
        System.out.print("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();
        System.out.print("Introdueix nom d'usuari (participant): ");
        String username = in.nextLine();
        System.out.print("Introdueix nom d'usuari (creador de l'enquesta 'mock'): ");
        String creadorName = in.nextLine();

        // 1. Crear Usuari mock (el que contesta)
        Usuari usuariMock = new Usuari(username, "pass_mock");
        
        // 2. Crear Enquesta mock (la que es contesta)
        Usuari creadorMock = new Usuari(creadorName, "pass_mock");
        Enquesta enquestaMock = new Enquesta(idEnquesta, "Enquesta Mock", "Desc Mock", creadorMock);

        // 3. Demanar les respostes
        HashMap<String, String> respostesUsuari = new HashMap<>();
        System.out.println("Introdueix les respostes (escriu 'fi' com a ID de pregunta per acabar):");
        while (true) {
            System.out.print("  ID Pregunta: ");
            String idPregunta = in.nextLine();
            if (idPregunta.equalsIgnoreCase("fi")) {
                break;
            }
            
            System.out.print("  Text Resposta: ");
            String textResposta = in.nextLine();
            
            respostesUsuari.put(idPregunta, textResposta);
            
            // Afegim una pregunta 'mock' a l'enquesta 'mock'
            // Això és necessari perquè cr.registrarRespostes crida a enquesta.getPregunta()
            enquestaMock.afegirPregunta(new Pregunta(idPregunta, "Pregunta Mock Text"));
        }

        // 4. Cridar al controlador
        // COMENTAT: Aquest mètode ja no existeix després del merge amb Marc
        // cr.registrarRespostes(enquestaMock, usuariMock, respostesUsuari);
        System.out.println("[FUNCIONALITAT DESACTIVADA] Respostes registrades per l'usuari '" + username + "' a l'enquesta '" + idEnquesta + "'.");
    }

    /**
     * Prova modificar una resposta i gestiona els codis de retorn.
     * 
     * DEBERIA HABER UNA EXCEPCION SI LA ENCUESTA NO EXISTE
     * DEBERIA HABER UNA EXCEPCION SI LA PREGUNTA NO EXISTE
     */
    private static void testModificarResposta() {
        System.out.print("Introdueix nom d'usuari (el propietari de la resposta): ");
        String username = in.nextLine();
        System.out.print("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();
        System.out.print("Introdueix ID pregunta: ");
        String idPregunta = in.nextLine();
        System.out.print("Introdueix el NOU text de la resposta: ");
        String novaResposta = in.nextLine();

        // Creem un usuari 'mock' per passar-lo al mètode
        Usuari usuariMock = new Usuari(username, "pass_mock");

        //AQUÍ SERIA EL USUARI ACTUAL SUPONGO EN VEZ DEL MOCK
        // COMENTAT: La signatura del mètode ha canviat després del merge
        // int resultat = cr.modificarResposta(usuariMock, idEnquesta, idPregunta, novaResposta);
        int resultat = -1; // Funcionalitat desactivada

        switch (resultat) {
            case 0:
                System.out.println("Resposta modificada correctament.");
                break;
            case 1:
                System.out.println("ERROR: No s'ha trobat la resposta (potser l'ID enquesta, usuari o pregunta són incorrectes).");
                break;
            case 2:
                System.out.println("ERROR: Permís denegat. L'usuari '" + username + "' no és el propietari original d'aquesta resposta.");
                break;
        }
    }

    /**
     * Prova esborrar una resposta i gestiona els codis de retorn.
     * 
     * DEBERIA HABER UNA EXCEPCION SI LA ENCUESTA NO EXISTE
     * DEBERIA HABER UNA EXCEPCION SI LA PREGUNTA NO EXISTE
     * 
     * ALGO PASA QUE SE SIGUEN MOSTRANDO RESPUESTAS QUE YA SE HAN BORRADO
     * CREO QUE TIENE QUE VER CON QUE LO GUARDAS EN PERSISTENCIA Y SOLO BORRAS EL HASHMAP LOCAL NO EL DE PERSISTENCIA
     */
    private static void testEsborrarResposta() {
        System.out.print("Introdueix nom d'usuari (el propietari de la resposta): ");
        String username = in.nextLine();
        System.out.print("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();
        System.out.print("Introdueix ID pregunta: ");
        String idPregunta = in.nextLine();

        // Creem un usuari 'mock' per passar-lo al mètode
        Usuari usuariMock = new Usuari(username, "pass_mock");

        // COMENTAT: La signatura del mètode ha canviat després del merge
        // int resultat = cr.esborrarResposta(usuariMock, idEnquesta, idPregunta);
        int resultat = -1; // Funcionalitat desactivada

        switch (resultat) {
            case 0:
                System.out.println("Resposta esborrada correctament.");
                break;
            case 1:
                System.out.println("ERROR: No s'ha trobat la resposta (potser l'ID enquesta, usuari o pregunta són incorrectes).");
                break;
            case 2:
                System.out.println("ERROR: Permís denegat. L'usuari '" + username + "' no és el propietari original d'aquesta resposta.");
                break;
        }
    }

    /**
     * Prova la consulta de totes les respostes d'una enquesta.
     * Esta funcion no deberia ser usada solo si el user es el creador de la enquesta??
     * DEBERIA HABER UNA EXCEPCION SI LA ENCUESTA NO EXISTE
     */
    private static void testGetRespostesEnquesta() {
        System.out.print("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();

        // COMENTAT: Aquest mètode ja no existeix després del merge amb Marc
        // HashMap<String, ArrayList<Resposta>> respostesPerUsuari = cr.getRespostesEnquesta(idEnquesta);
        HashMap<String, ArrayList<Resposta>> respostesPerUsuari = new HashMap<>(); // Funcionalitat desactivada

        if (respostesPerUsuari.isEmpty()) {
            System.out.println("No s'han trobat respostes per a l'enquesta '" + idEnquesta + "'.");
            return;
        }

        System.out.println("Respostes de l'enquesta '" + idEnquesta + "':");
        for (Map.Entry<String, ArrayList<Resposta>> entry : respostesPerUsuari.entrySet()) {
            System.out.println("  > Usuari: " + entry.getKey());
            for (Resposta r : entry.getValue()) {
                System.out.println("    - Pregunta: id: " + r.getIdPregunta() + ", Resposta: " + r.getTextResposta());
            }
        }
    }

    /**
     * Prova la consulta de les respostes d'un usuari específic.
     * DEBERIA HABER UNA EXCEPCION SI LA ENCUESTA NO EXISTE
     * DEBERIA HABER UNA EXCEPCION SI EL USUARIO NO EXISTE
     */
    private static void testGetRespostesUsuari() {
        System.out.print("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();
        System.out.print("Introdueix nom d'usuari: ");
        String username = in.nextLine();

        // COMENTAT: Aquest mètode ja no existeix després del merge amb Marc
        // ArrayList<Resposta> respostes = cr.getRespostesUsuari(idEnquesta, username);
        ArrayList<Resposta> respostes = new ArrayList<>(); // Funcionalitat desactivada

        if (respostes.isEmpty()) {
            System.out.println("No s'han trobat respostes per a l'usuari '" + username + "' en aquesta enquesta.");
            return;
        }

        System.out.println("Respostes de '" + username + "' a l'enquesta '" + idEnquesta + "':");
        for (Resposta r : respostes) {
            System.out.println("  - Pregunta: " + r.getIdPregunta() + ", Resposta: " + r.getTextResposta());
        }
    }
 
    /**
     * Prova la consulta del número de participants.
     * DEBERIA HABER UNA EXCEPCION SI LA ENCUESTA NO EXISTE
     */
    private static void testGetNumParticipants() {
        System.out.print("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();
        // COMENTAT: Aquest mètode ja no existeix després del merge amb Marc
        // int num = cr.getNumParticipants(idEnquesta);
        int num = 0; // Funcionalitat desactivada
        System.out.println("L'enquesta '" + idEnquesta + "' té " + num + " participants.");
    }

    /**
     * Prova la consulta del número total d'enquestes amb respostes.
     */
    private static void testGetNumEnquestesAmbRespostes() {
        // COMENTAT: Aquest mètode ja no existeix després del merge amb Marc
        // int num = cr.getNumEnquestesAmbRespostes();
        int num = 0; // Funcionalitat desactivada
        System.out.println("Hi ha un total de " + num + " enquestes amb respostes.");
    }
    


}
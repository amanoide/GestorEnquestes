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
import edu.upc.prop.clusterxx.domini.controladors.CtrlPersistencia;

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
    private static CtrlPersistencia persistencia;
    private static int contadorRespostes = 1; // Contador para generar IDs únicos
    
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
        persistencia = CtrlPersistencia.getInstance();
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
     * Prova registrar una resposta individual d'un usuari a una pregunta d'una enquesta.
     */
    private static void testRegistrarRespostes() {
        System.out.print("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();
        System.out.print("Introdueix ID pregunta: ");
        String idPregunta = in.nextLine();
        System.out.print("Introdueix nom d'usuari: ");
        String username = in.nextLine();
        System.out.print("Introdueix text resposta: ");
        String textResposta = in.nextLine();

        try {
            // Obtenir l'enquesta
            Enquesta enquesta = persistencia.getEnquesta(idEnquesta);
            if (enquesta == null) {
                System.out.println("ERROR: L'enquesta amb ID '" + idEnquesta + "' no existeix.");
                return;
            }

<<<<<<< HEAD:src/drivers/java/edu/upc/prop/clusterxx/controladors/CtrlRespostaDriver.java
            // Obtenir la pregunta
            Pregunta pregunta = enquesta.getPregunta(idPregunta);
            if (pregunta == null) {
                System.out.println("ERROR: La pregunta amb ID '" + idPregunta + "' no existeix a aquesta enquesta.");
                return;
            }

            // Obtenir o crear l'usuari
            Usuari usuari = persistencia.getUsuari(username);
            if (usuari == null) {
                System.out.println("L'usuari no existeix. Creant usuari nou...");
                usuari = new Usuari(username, "password123");
                persistencia.afegirUsuari(username, usuari);
            }

            // Validar la resposta
            if (!pregunta.validarResposta(textResposta)) {
                System.out.println("ERROR: La resposta no és vàlida per aquesta pregunta.");
                return;
            }

            // Generar ID únic per la resposta
            String idResposta = "r" + contadorRespostes++;

            // Registrar la resposta
            cr.registrarResposta(idResposta, idPregunta, textResposta, usuari, pregunta);
            System.out.println("Resposta registrada amb èxit! ID: " + idResposta);
        } catch (Exception e) {
            System.out.println("ERROR inesperat: " + e.getMessage());
            e.printStackTrace();
        }
=======
        // 4. Cridar al controlador
        // COMENTAT: Aquest mètode ja no existeix després del merge amb Marc
        // cr.registrarRespostes(enquestaMock, usuariMock, respostesUsuari);
        System.out.println("[FUNCIONALITAT DESACTIVADA] Respostes registrades per l'usuari '" + username + "' a l'enquesta '" + idEnquesta + "'.");
>>>>>>> 984b1583f67ffeaaf7fd39829524318335ad535e:ENTREGA/FONTS/src/drivers/java/edu/upc/prop/clusterxx/controladors/CtrlRespostaDriver.java
    }

    /**
     * Prova modificar una resposta existent d'un usuari a una pregunta.
     */
    private static void testModificarResposta() {
        System.out.print("Introdueix nom d'usuari: ");
        String username = in.nextLine();
        System.out.print("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();
        System.out.print("Introdueix ID pregunta: ");
        String idPregunta = in.nextLine();
        System.out.print("Introdueix el NOU text de la resposta: ");
        String novaResposta = in.nextLine();

        try {
            // Obtenir l'enquesta
            Enquesta enquesta = persistencia.getEnquesta(idEnquesta);
            if (enquesta == null) {
                System.out.println("ERROR: L'enquesta amb ID '" + idEnquesta + "' no existeix.");
                return;
            }

<<<<<<< HEAD:src/drivers/java/edu/upc/prop/clusterxx/controladors/CtrlRespostaDriver.java
            // Obtenir la pregunta
            Pregunta pregunta = enquesta.getPregunta(idPregunta);
            if (pregunta == null) {
                System.out.println("ERROR: La pregunta amb ID '" + idPregunta + "' no existeix a aquesta enquesta.");
                return;
            }
=======
        //AQUÍ SERIA EL USUARI ACTUAL SUPONGO EN VEZ DEL MOCK
        // COMENTAT: La signatura del mètode ha canviat després del merge
        // int resultat = cr.modificarResposta(usuariMock, idEnquesta, idPregunta, novaResposta);
        int resultat = -1; // Funcionalitat desactivada
>>>>>>> 984b1583f67ffeaaf7fd39829524318335ad535e:ENTREGA/FONTS/src/drivers/java/edu/upc/prop/clusterxx/controladors/CtrlRespostaDriver.java

            // Obtenir l'usuari
            Usuari usuari = persistencia.getUsuari(username);
            if (usuari == null) {
                System.out.println("ERROR: L'usuari '" + username + "' no existeix.");
                return;
            }

            // Validar la nova resposta
            if (!pregunta.validarResposta(novaResposta)) {
                System.out.println("ERROR: La nova resposta no és vàlida per aquesta pregunta.");
                return;
            }

            // Modificar la resposta
            cr.modificarResposta(usuari, pregunta, novaResposta);
            System.out.println("Resposta modificada amb èxit!");
        } catch (Exception e) {
            System.out.println("ERROR inesperat: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Prova esborrar una resposta d'un usuari a una pregunta.
     */
    private static void testEsborrarResposta() {
        System.out.print("Introdueix nom d'usuari: ");
        String username = in.nextLine();
        System.out.print("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();
        System.out.print("Introdueix ID pregunta: ");
        String idPregunta = in.nextLine();

        try {
            // Obtenir l'enquesta
            Enquesta enquesta = persistencia.getEnquesta(idEnquesta);
            if (enquesta == null) {
                System.out.println("ERROR: L'enquesta amb ID '" + idEnquesta + "' no existeix.");
                return;
            }

<<<<<<< HEAD:src/drivers/java/edu/upc/prop/clusterxx/controladors/CtrlRespostaDriver.java
            // Obtenir la pregunta
            Pregunta pregunta = enquesta.getPregunta(idPregunta);
            if (pregunta == null) {
                System.out.println("ERROR: La pregunta amb ID '" + idPregunta + "' no existeix a aquesta enquesta.");
                return;
            }
=======
        // COMENTAT: La signatura del mètode ha canviat després del merge
        // int resultat = cr.esborrarResposta(usuariMock, idEnquesta, idPregunta);
        int resultat = -1; // Funcionalitat desactivada
>>>>>>> 984b1583f67ffeaaf7fd39829524318335ad535e:ENTREGA/FONTS/src/drivers/java/edu/upc/prop/clusterxx/controladors/CtrlRespostaDriver.java

            // Obtenir l'usuari
            Usuari usuari = persistencia.getUsuari(username);
            if (usuari == null) {
                System.out.println("ERROR: L'usuari '" + username + "' no existeix.");
                return;
            }

            // Esborrar la resposta
            cr.esborrarResposta(usuari, pregunta);
            System.out.println("Resposta esborrada amb èxit!");
        } catch (Exception e) {
            System.out.println("ERROR inesperat: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Prova la consulta de totes les respostes d'una enquesta.
     */
    private static void testGetRespostesEnquesta() {
        System.out.print("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();

<<<<<<< HEAD:src/drivers/java/edu/upc/prop/clusterxx/controladors/CtrlRespostaDriver.java
        try {
            Enquesta enquesta = persistencia.getEnquesta(idEnquesta);
            if (enquesta == null) {
                System.out.println("ERROR: L'enquesta amb ID '" + idEnquesta + "' no existeix.");
                return;
=======
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
>>>>>>> 984b1583f67ffeaaf7fd39829524318335ad535e:ENTREGA/FONTS/src/drivers/java/edu/upc/prop/clusterxx/controladors/CtrlRespostaDriver.java
            }

            ArrayList<Pregunta> preguntes = enquesta.getPreguntes();
            if (preguntes.isEmpty()) {
                System.out.println("L'enquesta '" + idEnquesta + "' no té preguntes.");
                return;
            }

            boolean hiHaRespostes = false;
            System.out.println("\nRespostes de l'enquesta '" + idEnquesta + "':");
            
            for (Pregunta pregunta : preguntes) {
                Map<String, edu.upc.prop.clusterxx.domini.classes.Resposta> respostesPregunta = pregunta.getRespostes();
                
                if (!respostesPregunta.isEmpty()) {
                    hiHaRespostes = true;
                    System.out.println("\n  Pregunta: " + pregunta.getText());
                    for (Map.Entry<String, edu.upc.prop.clusterxx.domini.classes.Resposta> respostaEntry : respostesPregunta.entrySet()) {
                        edu.upc.prop.clusterxx.domini.classes.Resposta resposta = respostaEntry.getValue();
                        System.out.println("    - Usuari: " + resposta.getUsernameUsuari() + 
                                         " | Resposta: " + resposta.getTextResposta());
                    }
                }
            }
            
            if (!hiHaRespostes) {
                System.out.println("No s'han trobat respostes per aquesta enquesta.");
            }
        } catch (Exception e) {
            System.out.println("ERROR inesperat: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Prova la consulta de les respostes d'un usuari específic.
     */
    private static void testGetRespostesUsuari() {
        System.out.print("Introdueix nom d'usuari: ");
        String username = in.nextLine();

<<<<<<< HEAD:src/drivers/java/edu/upc/prop/clusterxx/controladors/CtrlRespostaDriver.java
        try {
            Usuari usuari = persistencia.getUsuari(username);
            if (usuari == null) {
                System.out.println("ERROR: L'usuari '" + username + "' no existeix.");
                return;
            }
=======
        // COMENTAT: Aquest mètode ja no existeix després del merge amb Marc
        // ArrayList<Resposta> respostes = cr.getRespostesUsuari(idEnquesta, username);
        ArrayList<Resposta> respostes = new ArrayList<>(); // Funcionalitat desactivada
>>>>>>> 984b1583f67ffeaaf7fd39829524318335ad535e:ENTREGA/FONTS/src/drivers/java/edu/upc/prop/clusterxx/controladors/CtrlRespostaDriver.java

            HashMap<String, edu.upc.prop.clusterxx.domini.classes.Resposta> respostesUsuari = usuari.getRespostesUsuari();
            if (respostesUsuari.isEmpty()) {
                System.out.println("L'usuari '" + username + "' no té respostes registrades.");
                return;
            }

            System.out.println("\nRespostes de l'usuari '" + username + "':");
            for (Map.Entry<String, edu.upc.prop.clusterxx.domini.classes.Resposta> entry : respostesUsuari.entrySet()) {
                edu.upc.prop.clusterxx.domini.classes.Resposta resposta = entry.getValue();
                System.out.println("  - Resposta ID: " + resposta.getId() + 
                                 " | Pregunta ID: " + resposta.getIdPregunta() + 
                                 " | Text: " + resposta.getTextResposta());
            }
        } catch (Exception e) {
            System.out.println("ERROR inesperat: " + e.getMessage());
            e.printStackTrace();
        }
    }
 
    /**
     * Prova la consulta del número de participants d'una enquesta.
     */
    private static void testGetNumParticipants() {
        System.out.print("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();
<<<<<<< HEAD:src/drivers/java/edu/upc/prop/clusterxx/controladors/CtrlRespostaDriver.java

        try {
            Enquesta enquesta = persistencia.getEnquesta(idEnquesta);
            if (enquesta == null) {
                System.out.println("ERROR: L'enquesta amb ID '" + idEnquesta + "' no existeix.");
                return;
            }

            int numParticipants = enquesta.getNumParticipants();
            System.out.println("L'enquesta '" + idEnquesta + "' té " + numParticipants + " participants.");
        } catch (Exception e) {
            System.out.println("ERROR inesperat: " + e.getMessage());
            e.printStackTrace();
        }
=======
        // COMENTAT: Aquest mètode ja no existeix després del merge amb Marc
        // int num = cr.getNumParticipants(idEnquesta);
        int num = 0; // Funcionalitat desactivada
        System.out.println("L'enquesta '" + idEnquesta + "' té " + num + " participants.");
>>>>>>> 984b1583f67ffeaaf7fd39829524318335ad535e:ENTREGA/FONTS/src/drivers/java/edu/upc/prop/clusterxx/controladors/CtrlRespostaDriver.java
    }

    /**
     * Prova la consulta del número total d'enquestes amb respostes.
     */
    private static void testGetNumEnquestesAmbRespostes() {
<<<<<<< HEAD:src/drivers/java/edu/upc/prop/clusterxx/controladors/CtrlRespostaDriver.java
        try {
            ArrayList<Enquesta> enquestes = persistencia.getAllEnquestes();
            int numEnquestesAmbRespostes = 0;

            for (Enquesta enquesta : enquestes) {
                if (enquesta.getNumParticipants() > 0) {
                    numEnquestesAmbRespostes++;
                }
            }

            System.out.println("Hi ha un total de " + numEnquestesAmbRespostes + " enquestes amb respostes.");
        } catch (Exception e) {
            System.out.println("ERROR inesperat: " + e.getMessage());
            e.printStackTrace();
        }
    }
=======
        // COMENTAT: Aquest mètode ja no existeix després del merge amb Marc
        // int num = cr.getNumEnquestesAmbRespostes();
        int num = 0; // Funcionalitat desactivada
        System.out.println("Hi ha un total de " + num + " enquestes amb respostes.");
    }
    


>>>>>>> 984b1583f67ffeaaf7fd39829524318335ad535e:ENTREGA/FONTS/src/drivers/java/edu/upc/prop/clusterxx/controladors/CtrlRespostaDriver.java
}
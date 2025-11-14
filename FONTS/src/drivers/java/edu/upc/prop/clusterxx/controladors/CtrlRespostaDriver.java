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
import edu.upc.prop.clusterxx.domini.controladors.CtrlDomini;

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
    private static CtrlDomini cd;
    private static Usuari usuariActual;
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
        cd = new CtrlDomini();
        usuariActual = cd.getUsuariActual();
    }

    /**
     * Mostra el menú d'opcions al driver.
     */
    private static void mostra_metodes() {
        System.out.println("\n--- Menú CtrlResposta ---");
        System.out.println("(1) Contestar enquesta");
        System.out.println("(2) Modificar Resposta");
        System.out.println("(3) Esborrar Resposta");
        System.out.println("(4) Importar Respostes des de Fitxer");
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
            case "Contestar enquesta":
                //testRegistrarRespostes();
                break;
            case "2":
            case "Modificar Resposta":
                testModificarResposta();
                break;
            case "3":
            case "Esborrar Resposta":
                testEsborrarResposta();
                break;
           /* case "4":
            case "Importar Respostes des de Fitxer":
                testGetRespostesEnquesta();
                break;*/
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
/*
    private static void respondreEnquesta() {
        System.out.println("\n═══ RESPONDRE ENQUESTA ═══");
        
        ArrayList<Enquesta> totes = cd.consultarEnquestes();
        if (totes.isEmpty()) {
            System.out.println("No hi ha enquestes al sistema.");
            return;
        }
        
        System.out.println("Enquestes disponibles:");
        for (int i = 0; i < totes.size(); i++) {
            Enquesta e = totes.get(i);
            System.out.println((i + 1) + ". " + e.getTitol() + " (ID: " + e.getId() + ")");
        }
        
        int num = -1;
        boolean numValid = false;
        
        while (!numValid) {
            try {
                System.out.print("\nEscull enquesta (número): ");
                num = Integer.parseInt(in.nextLine()) - 1;
                
                if (num < 0 || num >= totes.size()) {
                    System.out.println("❌ Número no vàlid. Tria un número entre 1 i " + totes.size());
                } else {
                    numValid = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Si us plau, introdueix un número vàlid");
            }
        }
        
        try {
            Enquesta enquesta = totes.get(num);
            List<Pregunta> preguntes = enquesta.getPreguntes();
            
            if (preguntes.isEmpty()) {
                throw new PreguntaNoExisteixException("L'enquesta no té preguntes");
            }
            
            System.out.println("\n─── " + enquesta.getTitol() + " ───");
            System.out.println(enquesta.getDescripcio() + "\n");
            
            for (Pregunta p : preguntes) {
                System.out.println("➤ " + p.getText());
                
                switch (p.getTipus()) {
                    case TEXT_LLIURE:
                        System.out.print("  Resposta: ");
                        String respText = in.nextLine();
                        cd.registrarResposta(enquesta.getId(), usuariActual, p.getId(), respText);
                        break;
                        
                    case NUMERICA:
                        System.out.print("  Valor (" + p.getValorMinim() + "-" + p.getValorMaxim() + "): ");
                        String respNum = in.nextLine();
                        cd.registrarResposta(enquesta.getId(), usuariActual, p.getId(), respNum);
                        break;
                        
                    case QUALITATIVA_ORDENADA:
                    case QUALITATIVA_NO_ORDENADA_SIMPLE:
                        List<Opcio> opcions = p.getOpcions();
                        for (int j = 0; j < opcions.size(); j++) {
                            System.out.println("  " + (j + 1) + ". " + opcions.get(j).getText());
                        }
                        
                        int opcioIdx = -1;
                        boolean opcioValida = false;
                        while (!opcioValida) {
                            try {
                                System.out.print("  Escull opció (1-" + opcions.size() + "): ");
                                opcioIdx = Integer.parseInt(in.nextLine()) - 1;
                                if (opcioIdx < 0 || opcioIdx >= opcions.size()) {
                                    System.out.println("  ❌ Opció no vàlida. Tria entre 1 i " + opcions.size());
                                } else {
                                    opcioValida = true;
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("  ❌ Si us plau, introdueix un número vàlid");
                            }
                        }
                        
                        cd.registrarResposta(enquesta.getId(), usuariActual, p.getId(), opcions.get(opcioIdx).getText());
                        break;
                        
                    case QUALITATIVA_NO_ORDENADA_MULTIPLE:
                        List<Opcio> opcionsM = p.getOpcions();
                        for (int j = 0; j < opcionsM.size(); j++) {
                            System.out.println("  " + (j + 1) + ". " + opcionsM.get(j).getText());
                        }
                        System.out.print("  Escull opcions separades per comes (ex: 1,3,4): ");
                        String opcionsEsc = in.nextLine();
                        cd.registrarResposta(enquesta.getId(), usuariActual, p.getId(), opcionsEsc);
                        break;
                }
            }
            
            cd.registrarParticipacio(enquesta.getId(), usuariActual.getUsername());//esto ya lo hace contestarEnquesta 
            System.out.println("\n✓ Enquesta completada! Gràcies per participar.");
            
        } catch (PreguntaNoExisteixException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
*/


    
    private static void testModificarResposta() {
        System.out.println("----MODIFICAR RESPOSTA----");
        System.out.print("Introdueix ID de l'enquesta: ");
        String idEnquesta = in.nextLine();
        System.out.print("Introdueix ID de la pregunta: ");
        String idPregunta = in.nextLine();
        System.out.println("Introdueix la nova resposta: ");
        String novaResposta = in.nextLine();

        try{
            cd.modificarResposta(usuariActual, idEnquesta, idPregunta, novaResposta);
            System.out.println("Resposta modificada correctament.");
        } catch (EnquestaNoExisteixException | PreguntaNoExisteixException | RespostaNoExisteixException | PermisDenegatException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

  
    private static void testEsborrarResposta() {
        System.out.println("----ESBORRAR RESPOSTA----");
        System.out.print("Introdueix ID de l'enquesta: ");
        String idEnquesta = in.nextLine();
        System.out.print("Introdueix ID de la pregunta: ");
        String idPregunta = in.nextLine();

        System.out.print("⚠️ Estàs segur que vols eliminar aquesta resposta? Aquesta acció no es pot desfer. (S/N): ");
        String confirmacio = in.nextLine();

        // Error: L'usuari cancel·la el procés
        if (!confirmacio.equalsIgnoreCase("S")) {
            System.out.println("ℹ️ Acció cancel·lada. La resposta no s'ha esborrat.");
            return; // Tornem al menú
        }

        try{
            cd.esborrarResposta(usuariActual, idEnquesta, idPregunta);
            System.out.println("Resposta esborrada correctament.");
        } catch (EnquestaNoExisteixException | PreguntaNoExisteixException | RespostaNoExisteixException | PermisDenegatException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
}

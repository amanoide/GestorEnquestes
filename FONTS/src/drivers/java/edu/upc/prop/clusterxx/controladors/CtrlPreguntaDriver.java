package edu.upc.prop.clusterxx.controladors;

// Importacions dels controladors
import edu.upc.prop.clusterxx.domini.controladors.CtrlPregunta;

// Importacions del paquet de domini
import edu.upc.prop.clusterxx.domini.classes.Opcio;
import edu.upc.prop.clusterxx.domini.classes.Pregunta;
import edu.upc.prop.clusterxx.domini.classes.TipusPregunta;
import static edu.upc.prop.clusterxx.domini.classes.Exceptions.*;

import java.util.Scanner;

/**
 * Driver per provar la classe CtrlPregunta.
 * Aquest driver depèn dels STUBS de CtrlPersistencia, Pregunta i Opcio.
 * 
 * HE AÑADIDO COMENTARIOS EN LOS TESTS DONDE HE VIST QUE FALTABA ALGUNA COMPROBACIÓN EN LOS CONTROLADORES O CLASES INTERNAS
 * PUEDE QUE FALTEN ALGUNOS TESTS MÁS, PERO HE INTENTADO CUBRIR LAS FUNCIONALIDADES PRINCIPALES.
 */
public class CtrlPreguntaDriver {

    private static Scanner in;
    private static CtrlPregunta cp;

    public static void main(String[] args) {
        init();
        System.out.println("Driver de testeig de CtrlPregunta");
        String input = "";
        while (!input.equals("0") && !input.equals("sortir")) {
            mostra_metodes();
            input = in.nextLine();

            try {
                gestionarEntrada(input);
            } catch (Exception e) {
                System.err.println("S'HA PRODUÏT UN ERROR: " + e.getMessage());
            }
        }
        in.close();
        System.out.println("Tancant el driver. Adéu!");
    }

    /**
     * Inicialitza el driver.
     */
    private static void init() {
        in = new Scanner(System.in);
        cp = new CtrlPregunta();
    }

    /**
     * Mostra el menú d'opcions.
     */
    private static void mostra_metodes() {
        System.out.println("\n--- Menú CtrlPregunta ---");
        System.out.println("(1) Crear/Afegir Pregunta (mock)");
        System.out.println("(2) Modificar Pregunta");
        System.out.println("(3) Afegir Opció a Pregunta");
        System.out.println("(4) Eliminar Opció de Pregunta");   
        System.out.println("------------------------");
        System.out.println("(0|sortir) - Tancar driver");
        System.out.println("Escull una opció: ");
    }

    /**
     * Gestiona l'entrada de l'usuari.
     */
    private static void gestionarEntrada(String input) throws Exception {
        switch (input) {
            case "1":
            case "Crear/Afegir Pregunta":
                testCrearPregunta();
                break;
            case "2":
            case "Modificar Pregunta":
                testModificarPregunta();
                break;
            case "3":
            case "Afegir Opció a Pregunta":
                testAfegirOpcio();
                break;
            case "4":
            case "Eliminar Opció de Pregunta":
                testEliminarOpcio();
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
     * Opció 1: Crea una pregunta directament al stub de persistència per
     * preparar l'entorn de proves.
     * 
     * FALTA UNA EXCEPCIÓ SI JA EXISTEIX LA PREGUNTA
     */
    private static void testCrearPregunta() {
        
        Pregunta p = crearPreguntaInteractivament();

        System.out.println("Pregunta creada correctament");
        
    }

    /**
     * Opció 2: Prova el mètode modificarPregunta de CtrlPregunta.
     * 
     * FALTA UNA EXCEPCIÓ SI NO EXISTEIX LA PREGUNTA A MODIFICAR
     */
    private static void testModificarPregunta() {
        System.out.println("Introdueix ID de la pregunta a modificar: ");
        String idExistent = in.nextLine();
        
        System.out.println("Introdueix les dades de la NOVA pregunta (amb la que se sobreescriurà):");
        System.out.println("  Nou ID (pot ser el mateix o diferent): ");
        String nouId = in.nextLine();
        System.out.println("  Nou text: ");
        String nouText = in.nextLine();
        
        Pregunta novaPregunta = new Pregunta(nouId, nouText);
        
        // Cridem al controlador
        cp.modificarPregunta(idExistent, novaPregunta);
        
        System.out.println("Pregunta modificada correctament.");
    }

    /**
     * Opció 3: Prova el mètode afegirOpcio de CtrlPregunta.
     * 
     * FALTA UNA EXCEPCIÓ SI NO EXISTEIX LA PREGUNTA A MODIFICAR
     * FALTA UNA EXCEPCIÓ SI JA EXISTEIX L'OPCIÓ A AFEGIR
     */
    private static void testAfegirOpcio() {
        System.out.println("Introdueix ID de la pregunta on afegir l'opció: ");
        String idPregunta = in.nextLine();
        
        System.out.println("  Introdueix ID de la nova opció (numèric): ");
        int idOpcio = Integer.parseInt(in.nextLine());
        System.out.println("  Introdueix text de l'opció: ");
        String textOpcio = in.nextLine();
        System.out.println("  Introdueix ordre (0 si no aplica): ");
        int ordre = Integer.parseInt(in.nextLine());

        Opcio o;
        if (ordre > 0) {
            o = new Opcio(idOpcio, textOpcio, ordre);
        } else {
            o = new Opcio(idOpcio, textOpcio);
        }

        // Cridem al controlador
        cp.afegirOpcio(idPregunta, o);
        System.out.println("Opció afegida correctament.");
    }

    /**
     * Opció 4: Prova el mètode eliminarOpcio de CtrlPregunta.
     * FALTA UNA EXCEPCIÓ SI NO EXISTEIX LA PREGUNTA O L'OPCIÓ
     */
    private static void testEliminarOpcio() {
        System.out.println("Introdueix ID de la pregunta d'on eliminar l'opció: ");
        String idPregunta = in.nextLine();
        System.out.println("  Introdueix ID de l'opció a eliminar (numèric): ");
        int idOpcio = Integer.parseInt(in.nextLine());
        
        // Cridem al controlador
        cp.eliminarOpcio(idPregunta, idOpcio);
        System.out.println("Opció eliminada correctament.");
    }

    /**
     * Mètode auxiliar interactiu per crear un objecte Pregunta. MOCK DE UN OBJECTE PREGUNTA PER PROBAR EL DRIVER
     */
    private static Pregunta crearPreguntaInteractivament() {
        System.out.println("  Introdueix ID pregunta: ");
        String idPregunta = in.nextLine();
        System.out.println("  Introdueix text pregunta: ");
        String text = in.nextLine();
        System.out.println("  Tipus de pregunta:");
        System.out.println("   (1) Text Lliure");
        System.out.println("   (2) Numèrica");
        System.out.println("   (3) Qualitativa Ordenada");
        System.out.println("   (4) Qualitativa Simple (No ordenada)");
        System.out.println("   (5) Qualitativa Múltiple (No ordenada)");
        System.out.println("  Escull tipus: ");
        String tipus = in.nextLine();

        switch (tipus) {
            case "2": // Numèrica
                System.out.println("  Valor mínim: ");
                double min = Double.parseDouble(in.nextLine());
                System.out.println("  Valor màxim: ");
                double max = Double.parseDouble(in.nextLine());
                return new Pregunta(idPregunta, text, min, max);
            case "3": // Qualitativa Ordenada
                return new Pregunta(idPregunta, text, TipusPregunta.QUALITATIVA_ORDENADA, 1);
            case "4": // Qualitativa Simple
                return new Pregunta(idPregunta, text, TipusPregunta.QUALITATIVA_NO_ORDENADA_SIMPLE, 1);
            case "5": // Qualitativa Múltiple
                System.out.println("  Màxim seleccions: ");
                int maxSel = Integer.parseInt(in.nextLine());
                return new Pregunta(idPregunta, text, TipusPregunta.QUALITATIVA_NO_ORDENADA_MULTIPLE, maxSel);
            case "1": // Text
                return new Pregunta(idPregunta, text);
            default:
                return new Pregunta(idPregunta, text);
        }
    }

}
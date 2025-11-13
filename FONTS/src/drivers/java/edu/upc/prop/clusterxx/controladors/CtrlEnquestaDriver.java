/**
 * Driver per a la classe CtrlEnquesta.
 * Permet provar les funcionalitats bàsiques de gestió d'enquestes,
 * preguntes i opcions.
 * 
 * @author Joan Garvin Cardona
 * 
 * IMPORTANT:
 * ESTA A MIG IMPLEMENTAR I PROVAR!
 * HE AÑADIDO COMENTARIOS EN LOS TESTS DONDE HE VIST QUE FALTABA ALGUNA COMPROBACIÓN EN LOS CONTROLADORES O CLASES INTERNAS
 * PUEDE QUE FALTEN ALGUNOS TESTS MÁS, PERO HE INTENTADO CUBRIR LAS FUNCIONALIDADES PRINCIPALES.
 */
package edu.upc.prop.clusterxx.controladors;

// Importacions dels controladors
import edu.upc.prop.clusterxx.domini.controladors.CtrlEnquesta;

// Importacions del paquet de domini
import edu.upc.prop.clusterxx.domini.classes.Enquesta;
import edu.upc.prop.clusterxx.domini.classes.Opcio;
import edu.upc.prop.clusterxx.domini.classes.Pregunta;
import edu.upc.prop.clusterxx.domini.classes.TipusPregunta;
import edu.upc.prop.clusterxx.domini.classes.Usuari;
import static edu.upc.prop.clusterxx.domini.classes.Exceptions.*;

// Importacions de Java
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Driver per provar la classe CtrlEnquesta.
 * Permet provar la creació, modificació i consulta d'enquestes,
 * preguntes i opcions.
 */
public class CtrlEnquestaDriver {

    private static Scanner in;
    private static CtrlEnquesta ce;

    /**
     * Mètode principal que executa el driver.
     */
    public static void main(String[] args) {
        init();
        System.out.println("Driver de testeig de CtrlEnquesta");

        String input = "";
        while (!input.equals("0") && !input.equals("sortir")) {
            mostra_metodes();
            input = in.nextLine();

            // Gestionem l'entrada i capturem errors d'usuari (p.ex. text per número)
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
     * Inicialitza l'Scanner i el CtrlEnquesta.
     */
    private static void init() {
        in = new Scanner(System.in);
        ce = new CtrlEnquesta();
    }

    /**
     * Mostra el menú d'opcions al driver.
     */
    private static void mostra_metodes() {
        System.out.println("\n--- Menú CtrlEnquesta ---");
        System.out.println("(1) Crear Enquesta");
        System.out.println("(2) Modificar Títol Enquesta");
        System.out.println("(3) Modificar Descripció Enquesta");
        System.out.println("(4) Eliminar Enquesta");
        System.out.println("(5) Consultar Pregunta d'Enquesta");
        System.out.println("(6) Consultar ID Creador d'Enquesta");
        System.out.println("--- Preguntes i Opcions ---");
        System.out.println("(10) Afegir Pregunta a Enquesta");
        System.out.println("(11) Eliminar Pregunta d'Enquesta");
        System.out.println("(12) Modificar Pregunta");
        System.out.println("(13) Afegir Opció a Pregunta");
        System.out.println("(14) Eliminar Opció de Pregunta");
        System.out.println("--- Consultes ---");
        System.out.println("(20) Consultar Enquesta (per ID)");
        System.out.println("(21) Llistar Totes les Enquestes");
        System.out.println("(22) Consultar Enquestes per Creador");
        System.out.println("(23) Registrar Participació");
        System.out.println("------------------------");
        System.out.println("(0|sortir) - Tancar driver");
        System.out.println("Escull una opció: ");
    }

    /**
     * Gestiona l'entrada de l'usuari amb un switch.
     * Llança excepcions si ocorren (p.ex. NumberFormatException).
     */
    private static void gestionarEntrada(String input) throws Exception {
        switch (input) {
            case "1":
                testCrearEnquesta();
                break;
            case "2":
                testModificarTitolEnquesta();
                break;
            case "3":
                testModificarDescripcioEnquesta();
                break;
            case "4":
                testEliminarEnquesta();
                break;
            case "5":
                testConsultaPregunta();
                break;
            case "6":
                testConsultaIdCreador();
                break;
            case "10":
                testAfegirPregunta();
                break;
            case "11":
                testEliminarPregunta();
                break;
            case "12":
                testModificarPregunta();
                break;
            case "13":
                testAfegirOpcioAPregunta();
                break;
            case "14":
                testEliminarOpcioDePregunta();
                break;
            case "20":
                testConsultarEnquesta();
                break;
            case "21":
                testLlistarEnquestes();
                break;
            case "22":
                testConsultarEnquestesPerCreador();
                break;
            case "23":
                testRegistrarParticipacio();
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
/*
 *   FALTA UNA EXCEPCIO DE SI L'ENQUESTA AMB ID JA EXISTEIX
 */
    private static void testCrearEnquesta() {
        System.out.println("Introdueix ID enquesta: ");
        String id = in.nextLine();
        System.out.println("Introdueix títol: ");
        String titol = in.nextLine();
        System.out.println("Introdueix descripció: ");
        String descripcio = in.nextLine();
        
        // Com que aquest driver no gestiona usuaris, en creem un de 'mock'
        System.out.println("Introdueix nom d'usuari creador: ");
        String username = in.nextLine();
        Usuari creadorMock = new Usuari(username, "pass_mock"); // Contrasenya irrellevant aquí

        ce.crearEnquesta(id, titol, descripcio, creadorMock);
        System.out.println("Enquesta '" + id + "' creada per " + username + ".");
    }
/*
 * FALTA UNA EXCEPCIO DE SI L'ENQUESTA AMB ID NO EXISTEIX
 */
    private static void testModificarTitolEnquesta() {
        System.out.println("Introdueix ID enquesta a modificar: ");
        String id = in.nextLine();
        System.out.println("Introdueix nou títol: ");
        String nouTitol = in.nextLine();
        ce.modificarTitolEnquesta(id, nouTitol);
        System.out.println("Títol modificat.");
    }
    /*
     * FALTA UNA EXCEPCIO DE SI L'ENQUESTA AMB ID NO EXISTEIX
     */
    private static void testModificarDescripcioEnquesta() {
        System.out.println("Introdueix ID enquesta a modificar: ");
        String id = in.nextLine();
        System.out.println("Introdueix nova descripció: ");
        String novaDesc = in.nextLine();
        ce.modificarDescripcioEnquesta(id, novaDesc);
        System.out.println("Descripció modificada");
    }
    /*
     * FALTA UNA EXCEPCIO DE SI L'ENQUESTA AMB ID NO EXISTEIX
     */
    private static void testEliminarEnquesta() {
        System.out.println("Introdueix ID enquesta a esborrar: ");
        String id = in.nextLine();
        ce.eliminarEnquesta(id);
        System.out.println("Enquesta '" + id + "' esborrada");
    }
/*
 * FALTA UNA EXCEPCIO DE SI L'ENQUESTA AMB ID NO EXISTEIX
 * FALTA UNA EXCEPCIO DE SI LA PREGUNTA AMB ID JA EXISTEIX
 */
    private static void testAfegirPregunta() {
        System.out.println("Introdueix ID enquesta on afegir la pregunta: ");
        String idEnquesta = in.nextLine();
        
        // Fem servir un mètode auxiliar per crear la pregunta
        Pregunta p = crearPreguntaInteractivament();
        
        ce.afegirPregunta(idEnquesta, p);
        System.out.println("Pregunta '" + p.getId() + "' afegida a l'enquesta '" + idEnquesta + "'.");
    }
/*
 * FALTA UNA EXCEPCIO DE SI L'ENQUESTA AMB ID NO EXISTEIX
 * FALTA UNA EXCEPCIO DE SI LA PREGUNTA AMB ID NO EXISTEIX
 */
    private static void testEliminarPregunta() {
        System.out.println("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();
        System.out.println("Introdueix ID pregunta a eliminar: ");
        String idPregunta = in.nextLine();
        
        ce.eliminarPregunta(idEnquesta, idPregunta);
        System.out.println("Pregunta '" + idPregunta + "' eliminada.");
    }
    /*
     * FALTA UNA EXCEPCIO DE SI L'ENQUESTA AMB ID NO EXISTEIX
     * FALTA UNA EXCEPCIO DE SI LA PREGUNTA AMB ID NO EXISTEIX
     */
    private static void testModificarPregunta() {
        System.out.println("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();
        System.out.println("Introdueix ID pregunta a modificar: ");
        String idPregunta = in.nextLine();

        System.out.println("Introdueix les dades de la NOVA pregunta:");
        Pregunta novaPregunta = crearPreguntaInteractivament();

        ce.modificarPregunta(idEnquesta, idPregunta, novaPregunta);
        System.out.println("Pregunta '" + idPregunta + "' modificada");
    }

    /*
     * FALTA UNA EXCEPCIO DE SI L'ENQUESTA AMB ID NO EXISTEIX
     * FALTA UNA EXCEPCIO DE SI LA PREGUNTA AMB ID NO EXISTEIX
     * FALTA UNA EXCEPCIO DE SI L'OPCIO AMB ID JA EXISTEIX
     */
    private static void testAfegirOpcioAPregunta() {
        System.out.println("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();
        System.out.println("Introdueix ID pregunta: ");
        String idPregunta = in.nextLine();
        
        Opcio o = crearOpcioInteractivament();
        
        ce.afegirOpcioAPregunta(idEnquesta, idPregunta, o);
        System.out.println("Opció afegida");
    }
    /*
     * FALTA UNA EXCEPCIO DE SI L'ENQUESTA AMB ID NO EXISTEIX
     * FALTA UNA EXCEPCIO DE SI LA PREGUNTA AMB ID NO EXISTEIX
     * FALTA UNA EXCEPCIO DE SI L'OPCIO AMB ID NO EXISTEIX
     */
    private static void testEliminarOpcioDePregunta() {
        System.out.println("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();
        System.out.println("Introdueix ID pregunta: ");
        String idPregunta = in.nextLine();
        System.out.println("Introdueix ID opció (número) a eliminar: ");
        int idOpcio = Integer.parseInt(in.nextLine());

        ce.eliminarOpcioDepregunta(idEnquesta, idPregunta, idOpcio);
        System.out.println("Opció eliminada (si existia).");
    }
/*
 *  FALTA UNA EXCEPCIO DE SI L'ENQUESTA AMB ID NO EXISTEIX
 */
    private static void testConsultaPregunta() {
        System.out.println("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();
        System.out.println("Introdueix ID pregunta: ");
        String idPregunta = in.nextLine();

        Pregunta p = ce.getPregunta(idEnquesta, idPregunta);
        if (p == null) {
            System.out.println("No s'ha trobat la pregunta amb ID: " + idPregunta);
        } else {
            System.out.println("Pregunta trobada: ID=" + p.getId() + ", Text='" + p.getText() + "', Tipus=" + p.getTipus());
        }
    }

    private static void testConsultaIdCreador(){
        System.out.println("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();

        String idCreador = ce.getIdCreador(idEnquesta);
        if (idCreador == null) {
            System.out.println("No s'ha trobat l'enquesta amb ID: " + idEnquesta);
        } else {
            System.out.println("L'ID del creador de l'enquesta '" + idEnquesta + "' és: " + idCreador);
        }
    }

    private static void testConsultarEnquesta() {
        System.out.println("Introdueix ID enquesta a consultar: ");
        String id = in.nextLine();
        Enquesta e = ce.getEnquesta(id);

        if (e == null) {
            System.out.println("No s'ha trobat cap enquesta amb l'ID: " + id);
        } else {
            imprimirEnquestaDetall(e);
        }
    }
    
    private static void testLlistarEnquestes() {
        ArrayList<Enquesta> enquestes = ce.llistarEnquestes();
        if (enquestes.isEmpty()) {
            System.out.println("No hi ha cap enquesta creada.");
            return;
        }
        System.out.println("Llistat d'enquestes (" + enquestes.size() + "):");
        for (Enquesta e : enquestes) {
            System.out.println("  - ID: " + e.getId() + ", Títol: " + e.getTitol() + " (Creador: " + e.getIdCreador() + ")");
        }
    }
    
    private static void testConsultarEnquestesPerCreador() {
        System.out.println("Introdueix nom d'usuari del creador: ");
        String username = in.nextLine();
        ArrayList<Enquesta> enquestes = ce.getEnquestesPerCreador(username);
        
        if (enquestes.isEmpty()) {
            System.out.println("L'usuari '" + username + "' no ha creat cap enquesta.");
            return;
        }
        System.out.println("Enquestes creades per '" + username + "':");
        for (Enquesta e : enquestes) {
            System.out.println("  - ID: " + e.getId() + ", Títol: " + e.getTitol());
        }
    }
    /*
     * FALTA UNA EXCEPCIO DE SI L'ENQUESTA AMB ID NO EXISTEIX
     */
    private static void testRegistrarParticipacio() {
        System.out.println("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();
        System.out.println("Introdueix nom d'usuari participant: ");
        String username = in.nextLine();
        ce.registrarParticipacio(idEnquesta, username);
        System.out.println("Participació registrada");
    }


    // --- Mètodes Auxiliars ---

    /**
     * Mètode auxiliar interactiu per crear un objecte Pregunta.
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
    
    /**
     * Mètode auxiliar interactiu per crear un objecte Opcio.
     */
    private static Opcio crearOpcioInteractivament() {
        System.out.println("  Introdueix ID opció (número): ");
        int idOpcio = Integer.parseInt(in.nextLine());
        System.out.println("  Introdueix text opció: ");
        String textOpcio = in.nextLine();
        System.out.println("  Introdueix ordre (0 si no aplica): ");
        int ordre = Integer.parseInt(in.nextLine());

        if (ordre > 0) {
            return new Opcio(idOpcio, textOpcio, ordre);
        } else {
            return new Opcio(idOpcio, textOpcio);
        }
    }
    
    /**
     * Mètode auxiliar per imprimir els detalls d'una enquesta.
     */
    private static void imprimirEnquestaDetall(Enquesta e) {
        System.out.println("--- Detalls de l'Enquesta ---");
        System.out.println("ID: " + e.getId());
        System.out.println("Títol: " + e.getTitol());
        System.out.println("Descripció: " + e.getDescripcio());
        System.out.println("Creador: " + e.getIdCreador());
        System.out.println("Preguntes:");
        
        ArrayList<Pregunta> preguntes = e.getPreguntes();
        if (preguntes.isEmpty()) {
            System.out.println("  (Aquesta enquesta no té preguntes)");
        } else {
            for (Pregunta p : preguntes) {
                System.out.println("  - ID: " + p.getId() + ", Text: " + p.getText() + " (Tipus: " + p.getTipus() + ")");
                
                ArrayList<Opcio> opcions = p.getOpcions();
                if (!opcions.isEmpty()) {
                    System.out.println("    Opcions:");
                    for (Opcio o : opcions) {
                        System.out.println("      (" + o.getId() + ") " + o.getText() + (o.getOrdre() > 0 ? " [Ordre: " + o.getOrdre() + "]" : ""));
                    }
                }
            }
        }
        System.out.println("-----------------------------");
    }
}
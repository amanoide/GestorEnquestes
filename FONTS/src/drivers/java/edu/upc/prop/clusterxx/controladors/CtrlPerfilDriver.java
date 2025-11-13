package edu.upc.prop.clusterxx.controladors;

// Importacions dels controladors
import edu.upc.prop.clusterxx.domini.controladors.CtrlPerfil;

// Importacions del paquet de domini
import edu.upc.prop.clusterxx.domini.classes.Perfil;
import static edu.upc.prop.clusterxx.domini.classes.Exceptions.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Driver per provar la classe CtrlPerfil.
 * Aquest driver depèn d'un STUB de CtrlPersistencia per funcionar,
 * ja que CtrlPerfil delega totes les operacions a la persistència.
 * 
 * HE AÑADIDO COMENTARIOS EN LOS TESTS DONDE HE VIST QUE FALTABA ALGUNA COMPROBACIÓN EN LOS CONTROLADORES O CLASES INTERNAS
 * PUEDE QUE FALTEN ALGUNOS TESTS MÁS, PERO HE INTENTADO CUBRIR LAS FUNCIONALIDADES PRINCIPALES.
 */
public class CtrlPerfilDriver {

    private static Scanner in;
    private static CtrlPerfil cp;

    /**
     * Mètode principal que executa el driver.
     */
    public static void main(String[] args) {
        init();
        System.out.println("Driver de testeig de CtrlPerfil");

        String input = "";
        while (!input.equals("0") && !input.equals("sortir")) {
            mostra_metodes();
            input = in.nextLine();

            // Gestionem l'entrada
            try {
                gestionarEntrada(input);
            } catch (Exception e) {
                // Captura errors com el NumberFormatException si l'ID no és numèric
                System.err.println("S'HA PRODUÏT UN ERROR: " + e.getMessage());
            }
        }
        in.close();
        System.out.println("Tancant el driver. Adéu!");
    }

    /**
     * Inicialitza l'Scanner i el CtrlPerfil.
     * En crear CtrlPerfil, automàticament s'obté la instància del (stub de) CtrlPersistencia.
     */
    private static void init() {
        in = new Scanner(System.in);
        cp = new CtrlPerfil();
    }

    /**
     * Mostra el menú d'opcions al driver.
     */
    private static void mostra_metodes() {
        System.out.println("\n--- Menú CtrlPerfil ---");
        System.out.println("(1) Crear Perfil");
        System.out.println("(2) Consultar Perfil");
        System.out.println("(3) Modificar Descripció Perfil");
        System.out.println("(4) Eliminar Perfil");
        System.out.println("(5) Llistar Tots els Perfils");
        System.out.println("------------------------");
        System.out.println("(0|sortir) - Tancar driver");
        System.out.println("Escull una opció: ");
    }

    /**
     * Gestiona l'entrada de l'usuari amb un switch.
     * Llança Excepció si hi ha errors d'entrada.
     */
    private static void gestionarEntrada(String input) throws Exception {
        switch (input) {
            case "1":
            case "Crear Perfil":
                testCrearPerfil();
                break;
            case "2":
            case "Consultar Perfil":
                testGetPerfil();
                break;
            case "3":
            case "Modificar Descripció Perfil":
                testModificarPerfil();
                break;
            case "4":
            case "Eliminar Perfil":
                testEliminarPerfil();
                break;
            case "5":
            case "Llistar Tots els Perfils":
                testLlistarPerfils();
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
     * FALTA EXCEPCIO SI ID JA EXISTEIX
     */
    private static void testCrearPerfil() {
        System.out.println("Introdueix ID (ha de ser numèric): ");
        String id = in.nextLine();
        System.out.println("Introdueix descripció: ");
        String descripcio = in.nextLine();
        
        cp.crearPerfil(id, descripcio);
        
        System.out.println("Perfil creat amb ID " + id + " i descripció '" + descripcio + "'.");
    }

    private static void testGetPerfil() {
        System.out.println("Introdueix ID del perfil a consultar: ");
        String id = in.nextLine();
        
        Perfil p = cp.getPerfil(id);
        
        if (p == null) {
            System.out.println("Resultat: No s'ha trobat cap perfil amb ID " + id);
        } else {
            System.out.println("Resultat: ID: " + p.getId() + " Descripció: " + p.getDescripcion());
        }
    }

    /*
     * FALTA EXCEPCIO SI ID NO EXISTEIX
     */
    private static void testModificarPerfil() {
        System.out.println("Introdueix ID del perfil a modificar: ");
        String id = in.nextLine();
        System.out.println("Introdueix la NOVA descripció: ");
        String novaDescripcio = in.nextLine();
        
        cp.modificarPerfil(id, novaDescripcio);
        
        System.out.println("Descripció del perfil amb ID " + id + " modificada.");
    }
    /*
     * FALTA EXCEPCIO SI ID NO EXISTEIX JA
     */
    private static void testEliminarPerfil() {
        System.out.println("Introdueix ID del perfil a eliminar: ");
        String id = in.nextLine();
        
        cp.eliminarPerfil(id);
        
        System.out.println("Perfil amb ID " + id + " eliminat.");
    }

    private static void testLlistarPerfils() {
        ArrayList<Perfil> perfils = cp.llistarPerfils();
        
        if (perfils.isEmpty()) {
            System.out.println("No hi ha cap perfil al sistema.");
            return;
        }
        
        System.out.println("Llistat de perfils (" + perfils.size() + "):");
        for (Perfil p : perfils) {
            System.out.println("  ID: " + p.getId() + " Descripció: " + p.getDescripcion());
        }
    }
}
package edu.upc.prop.clusterxx.controladors;

import edu.upc.prop.clusterxx.domini.controladors.CtrlDomini;
// Importacions dels controladors

// Importacions del paquet de domini
import edu.upc.prop.clusterxx.domini.classes.Enquesta;
import edu.upc.prop.clusterxx.domini.classes.Exceptions.*;

import edu.upc.prop.clusterxx.domini.classes.Opcio;
import edu.upc.prop.clusterxx.domini.classes.Pregunta;
import edu.upc.prop.clusterxx.domini.classes.TipusPregunta;
import edu.upc.prop.clusterxx.domini.classes.Usuari;

// Importacions de Java
import java.util.Scanner;
import java.util.List;


/**
 * Driver per provar la classe CtrlEnquesta.
 * Permet provar la creació, modificació i consulta d'enquestes,
 * preguntes i opcions.
 */
public class CtrlEnquestaDriver {

    private static Scanner in;
    private static CtrlDomini cd;
    private static Usuari admin;

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

    
    private static void init() {
        in = new Scanner(System.in);
        cd = new CtrlDomini();
        admin = new Usuari("USER_MOCK", "1234");
        Usuari.login(admin);
        
    }

    /**
     * Mostra el menú d'opcions al driver.
     */
    private static void mostra_metodes() {
        System.out.println("\n--- Menú CtrlEnquesta ---");
        System.out.println("--- Enquestes ---");
        System.out.println("(1) Crear Enquesta");
        System.out.println("(2) Modificar Títol Enquesta");
        System.out.println("(3) Modificar Descripció Enquesta");
        System.out.println("(4) Esborrar Enquesta");
        System.out.println( "(5) Importar enquesta");

        System.out.println("--- Preguntes i Opcions ---");
        System.out.println("(10) Afegir Pregunta");
        System.out.println("(11) Eliminar Pregunta");
        System.out.println("(12) Modificar Pregunta");
        System.out.println("(13) Afegir Opció a Pregunta");
        System.out.println("(14) Eliminar Opció de Pregunta");

        System.out.println("--- Consultores ---");
        System.out.println("(20) Consultar Enquestes");
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
                testEsborrarEnquesta();
                break;
            case "5":
                testImportarEnquesta();
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
                consultarEnquestes();
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

    // --- Enquestes ---

    private static void testCrearEnquesta(){
        System.out.println("\n═══ CREAR NOVA ENQUESTA ═══");
        System.out.println("Introdueix ID enquesta: ");
        String id = in.nextLine();
        System.out.println("Introdueix títol: ");
        String titol = in.nextLine();
        System.out.println("Introdueix descripció: ");
        String descripcio = in.nextLine();
        
        try {
            cd.crearEnquesta(id, titol, descripcio);
            System.out.println("✓ Enquesta creada correctament!");
            System.out.println("  ID: " + id);
            System.out.println("  Títol: " + titol);
        } catch (EnquestaJaExisteixException | ParametreInvalidException | UsuariNoAutenticatException e) {
            System.out.println("❌ Error creant l'enquesta: " + e.getMessage());
        } 
    }

    private static void testModificarTitolEnquesta() {
        System.out.println("\n─── MODIFICAR TÍTOL ───");
        System.out.println("Introdueix ID enquesta a modificar: ");
        String id = in.nextLine();
        
        Enquesta enquesta = null;
        try{
        enquesta = cd.getEnquesta(id);
        } catch (UsuariNoAutenticatException | ParametreInvalidException | EnquestaNoExisteixException e){
            System.out.println("❌ Error: " + e.getMessage());
        }
    System.out.println("Títol actual: " + enquesta.getTitol());
    System.out.println("Nou títol: ");
        String nouTitol = in.nextLine();
        try {
            cd.modificarTitolEnquesta(id, nouTitol);
            System.out.println("✓ Títol modificat!");
        } catch (UsuariNoAutenticatException | ParametreInvalidException | EnquestaNoExisteixException | PermisDenegatException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    
    private static void testModificarDescripcioEnquesta() {
        System.out.println("\n─── MODIFICAR DESCRIPCIÓ ───");
        System.out.println("Introdueix ID enquesta a modificar: ");
        String id = in.nextLine();
        Enquesta enquesta = null;
        try{
        enquesta = cd.getEnquesta(id);
        } catch (UsuariNoAutenticatException | ParametreInvalidException | EnquestaNoExisteixException e){
            System.out.println("❌ Error: " + e.getMessage());
        }
    System.out.println("Descripció actual: " + enquesta.getDescripcio());
    System.out.println("Nova descripció: ");
        String novaDesc = in.nextLine();
        try {
            cd.modificarDescripcioEnquesta(id, novaDesc);
            System.out.println("✓ Descripció modificada!");
        } catch (ParametreInvalidException | UsuariNoAutenticatException | EnquestaNoExisteixException | PermisDenegatException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    
    private static void testEsborrarEnquesta(){
        System.out.println("\n─── ESBORRAR ENQUESTA ───");
        System.out.println("Introdueix ID enquesta a esborrar: ");
        String id = in.nextLine();
        try{
            cd.esborrarEnquesta(id);
            System.out.println("✓ Enquesta esborrada correctament!");
        } catch (EnquestaNoExisteixException | PermisDenegatException | UsuariNoAutenticatException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        
    }

    private static void testAfegirPregunta() {
        System.out.println("\n─── AFEGIR PREGUNTA ───");
        System.out.println("Introdueix ID enquesta a la qual afegir la pregunta: ");
        String idEnquesta = in.nextLine();
        Enquesta enquesta = null;
        try{
        enquesta = cd.getEnquesta(idEnquesta);
        } catch (UsuariNoAutenticatException | ParametreInvalidException | EnquestaNoExisteixException e){
            System.out.println("❌ Error: " + e.getMessage());
        }        
        
        System.out.println("ID de la pregunta: ");
        String id = in.nextLine();
        
        // Verificar si ja existeix aquesta pregunta a l'enquesta
        for (Pregunta p : enquesta.getPreguntes()) {
            if (p.getId().equals(id)) {
                System.out.println("Ja existeix una pregunta amb aquest ID a l'enquesta");
                return;
            }
        }
        
        System.out.println("Text de la pregunta: ");
        String text = in.nextLine();
        
        System.out.println("\nTipus de pregunta:");
        System.out.println("  1. Text lliure");
        System.out.println("  2. Numèrica");
        System.out.println("  3. Qualitativa ordenada");
        System.out.println("  4. Qualitativa no ordenada simple");
        System.out.println("  5. Qualitativa no ordenada múltiple");
        System.out.println("Escull (1-5): ");
        
        String tipusOpcio = in.nextLine();
        Pregunta pregunta = null;
        
        switch (tipusOpcio) {
            case "1":
                pregunta = new Pregunta(id, text, "text");
                break;
            case "2":
                double min = 0, max = 0;
                boolean valorsValids = false;
                
                while (!valorsValids) {
                    try {
                        System.out.println("Valor mínim: ");
                        min = Double.parseDouble(in.nextLine());
                        System.out.println("Valor màxim: ");
                        max = Double.parseDouble(in.nextLine());
                        
                        if (min >= max) {
                            System.out.println("El valor mínim ha de ser menor que el màxim");
                        } else {
                            valorsValids = true;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Si us plau, introdueix números vàlids");
                    }
                }
                
                pregunta = new Pregunta(id, text, min, max);
                break;
            case "3":
                pregunta = new Pregunta(id, text, "qualitativa_ordenada");
                afegirOpcions(pregunta, true);
                break;
            case "4":
                pregunta = new Pregunta(id, text, "qualitativa_simple");
                afegirOpcions(pregunta, false);
                break;
            case "5":
                int maxSel = -1;
                while (maxSel < 1) {
                    try {
                        System.out.println("Màxim de seleccions: ");
                        maxSel = Integer.parseInt(in.nextLine());
                        if (maxSel < 1) {
                            System.out.println("Ha de ser un número positiu");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Si us plau, introdueix un número vàlid");
                    }
                }
                pregunta = new Pregunta(id, text, TipusPregunta.QUALITATIVA_NO_ORDENADA_MULTIPLE, maxSel);
                afegirOpcions(pregunta, false);
                break;
            default:
                System.out.println("Tipus no vàlid");
                return;
        }
        
        try {
            cd.afegirPregunta(idEnquesta, pregunta);
            System.out.println("Pregunta afegida correctament!");
        } catch (ParametreInvalidException | UsuariNoAutenticatException | PreguntaJaExisteixException | RespostaInvalidaException | EnquestaNoExisteixException | PermisDenegatException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void testEliminarPregunta() {
        System.out.println("\n─── ELIMINAR PREGUNTA ───");
        System.out.println("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();
        Enquesta enquesta = null;
        try{
        enquesta = cd.getEnquesta(idEnquesta);
        } catch (UsuariNoAutenticatException | ParametreInvalidException | EnquestaNoExisteixException e){
            System.out.println("❌ Error: " + e.getMessage());
        }   

        Pregunta p = escollirPregunta(enquesta);
        
        try {
            cd.eliminarPregunta(idEnquesta, p.getId());
            System.out.println("✓ Pregunta eliminada!");
        } catch (ParametreInvalidException | PreguntaNoExisteixException | UsuariNoAutenticatException | EnquestaNoExisteixException | PermisDenegatException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

   
    private static void testModificarPregunta() {
        System.out.println("\n─── MODIFICAR PREGUNTA ───");
        System.out.println("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();
        Enquesta enquesta = null;
        
        try{
        enquesta = cd.getEnquesta(idEnquesta);
        } catch (UsuariNoAutenticatException | ParametreInvalidException | EnquestaNoExisteixException e){
            System.out.println("❌ Error: " + e.getMessage());
        }        
        
        Pregunta preguntaActual = escollirPregunta(enquesta);

        System.out.println("\n─── Pregunta actual ───");
        System.out.println("Text: " + preguntaActual.getText());
        System.out.println("Tipus: " + preguntaActual.getTipus());
        
        System.out.println("\n¿Què vols modificar?");
        System.out.println("  1. Només el text");
        System.out.println("  2. Text i tipus (es crearà una nova pregunta)");
        System.out.println("Escull (1-2): ");
        
        String opcio = in.nextLine();
        
        //CASE 1: MODIFICAR NOMÉS EL TEXT
        if (opcio.equals("1")) {
            // Modificar només el text
            System.out.println("Nou text de la pregunta: ");
            String nouText = in.nextLine();
            
            try {
                // Crear una nova pregunta amb el mateix tipus però diferent text
                Pregunta novaPregunta = null;
                
                switch (preguntaActual.getTipus()) {
                    case TEXT_LLIURE:
                        novaPregunta = new Pregunta(preguntaActual.getId(), nouText, "text");
                        break;
                    case NUMERICA:
                        novaPregunta = new Pregunta(preguntaActual.getId(), nouText, 
                            preguntaActual.getValorMinim(), preguntaActual.getValorMaxim());
                        break;
                    case QUALITATIVA_ORDENADA:
                        novaPregunta = new Pregunta(preguntaActual.getId(), nouText, "qualitativa_ordenada");
                        // Copiar les opcions
                        for (Opcio o : preguntaActual.getOpcions()) {
                            novaPregunta.afegirOpcio(o);
                        }
                        break;
                    case QUALITATIVA_NO_ORDENADA_SIMPLE:
                        novaPregunta = new Pregunta(preguntaActual.getId(), nouText, "qualitativa_simple");
                        // Copiar les opcions
                        for (Opcio o : preguntaActual.getOpcions()) {
                            novaPregunta.afegirOpcio(o);
                        }
                        break;
                    case QUALITATIVA_NO_ORDENADA_MULTIPLE:
                        novaPregunta = new Pregunta(preguntaActual.getId(), nouText, 
                            TipusPregunta.QUALITATIVA_NO_ORDENADA_MULTIPLE, preguntaActual.getMaxSeleccions());
                        // Copiar les opcions
                        for (Opcio o : preguntaActual.getOpcions()) {
                            novaPregunta.afegirOpcio(o);
                        }
                        break;
                }
                
                cd.modificarPregunta(enquesta.getId(), preguntaActual.getId(), novaPregunta);
                System.out.println("✓ Pregunta modificada correctament!");
                
            } catch (ParametreInvalidException | UsuariNoAutenticatException | EnquestaNoExisteixException | PreguntaNoExisteixException | PermisDenegatException | RespostaInvalidaException e) {
                System.out.println("❌ Error: " + e.getMessage());
            }
            
        } 
        
        //CASE 2: MODIFICAR TEXT I TIPUS
        else if (opcio.equals("2")) {
            // Crear una pregunta completament nova
            System.out.println("Nou text de la pregunta: ");
            String nouText = in.nextLine();
            
            System.out.println("\nNou tipus de pregunta:");
            System.out.println("  1. Text lliure");
            System.out.println("  2. Numèrica");
            System.out.println("  3. Qualitativa ordenada");
            System.out.println("  4. Qualitativa no ordenada simple");
            System.out.println("  5. Qualitativa no ordenada múltiple");
            System.out.println("Escull (1-5): ");
            
            String tipusOpcio = in.nextLine();
            Pregunta novaPregunta = null;
            
            try {
                switch (tipusOpcio) {
                    case "1":
                        novaPregunta = new Pregunta(preguntaActual.getId(), nouText, "text");
                        break;
                    case "2":
                        double min = 0, max = 0;
                        boolean valorsValids = false;
                        
                        while (!valorsValids) {
                            try {
                                System.out.println("Valor mínim: ");
                                min = Double.parseDouble(in.nextLine());
                                System.out.println("Valor màxim: ");
                                max = Double.parseDouble(in.nextLine());
                                
                                if (min >= max) {
                                    System.out.println("❌ El valor mínim ha de ser menor que el màxim");
                                } else {
                                    valorsValids = true;
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("❌ Si us plau, introdueix números vàlids");
                            }
                        }
                        
                        novaPregunta = new Pregunta(preguntaActual.getId(), nouText, min, max);
                        break;
                    case "3":
                        novaPregunta = new Pregunta(preguntaActual.getId(), nouText, "qualitativa_ordenada");
                        afegirOpcions(novaPregunta, true);
                        break;
                    case "4":
                        novaPregunta = new Pregunta(preguntaActual.getId(), nouText, "qualitativa_simple");
                        afegirOpcions(novaPregunta, false);
                        break;
                    case "5":
                        int maxSel = -1;
                        while (maxSel < 1) {
                            try {
                                System.out.println("Màxim de seleccions: ");
                                maxSel = Integer.parseInt(in.nextLine());
                                if (maxSel < 1) {
                                    System.out.println("❌ Ha de ser un número positiu");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("❌ Si us plau, introdueix un número vàlid");
                            }
                        }
                        novaPregunta = new Pregunta(preguntaActual.getId(), nouText, 
                            TipusPregunta.QUALITATIVA_NO_ORDENADA_MULTIPLE, maxSel);
                        
                        afegirOpcions(novaPregunta, false);
                        break;
                    default:
                        System.out.println("❌ Tipus no vàlid");
                        return;
                }
                
                cd.modificarPregunta(idEnquesta, preguntaActual.getId(), novaPregunta);
                System.out.println("✓ Pregunta modificada correctament!");
                
            } catch (ParametreInvalidException | UsuariNoAutenticatException | EnquestaNoExisteixException | PreguntaNoExisteixException | PermisDenegatException | RespostaInvalidaException e) {
                System.out.println("❌ Error: " + e.getMessage());
            }
            
        } 
        
        else {
            System.out.println("❌ Opció no vàlida");
        }
    }

    private static void testAfegirOpcioAPregunta() {
        System.out.println("\n─── AFEGIR OPCIÓ A PREGUNTA ───");
        System.out.println("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();
        Enquesta enquesta = null;
        
        try{
        enquesta = cd.getEnquesta(idEnquesta);
        } catch (UsuariNoAutenticatException | ParametreInvalidException | EnquestaNoExisteixException e){
            System.out.println("❌ Error: " + e.getMessage());
        }                
        
        Pregunta preguntaActual = escollirPregunta(enquesta);
        
        // Calcular el següent ID disponible
        int nextId = 0;
        for (Opcio opcio : preguntaActual.getOpcions()) {
            if (opcio.getId() >= nextId) {
                nextId = opcio.getId() + 1;
            }
        }
        
        Opcio o = null;
        //Fer una opció segons tipus de pregunta
        switch (preguntaActual.getTipus()) {
            case QUALITATIVA_ORDENADA:
                System.out.println("Text de l'opció: ");
                String textOrd = in.nextLine();
                System.out.println("Ordre de l'opció (número enter): ");
                int ordre = Integer.parseInt(in.nextLine());
                
                o = new Opcio(nextId, textOrd, ordre);
                break;
            
            case QUALITATIVA_NO_ORDENADA_SIMPLE:
            case QUALITATIVA_NO_ORDENADA_MULTIPLE:
                
                System.out.println("Text de l'opció: ");
                String textNoOrd = in.nextLine();
                o = new Opcio(nextId, textNoOrd);
                break;
            case TEXT_LLIURE:
            case NUMERICA:
                System.out.println("❌ No es poden afegir opcions a preguntes de tipus TEXT_LLIURE o NUMERICA.");
                break;
            default:
                break;
        }
        

        try{
        cd.afegirOpcioAPregunta(idEnquesta, preguntaActual.getId(), o);
        System.out.println("Opció afegida");
        } catch (ParametreInvalidException | UsuariNoAutenticatException | EnquestaNoExisteixException | PreguntaNoExisteixException | PermisDenegatException | RespostaInvalidaException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    
    private static void testEliminarOpcioDePregunta() {
        System.out.println("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();
        Enquesta enquesta = null;
        
        try{
        enquesta = cd.getEnquesta(idEnquesta);
        } catch (UsuariNoAutenticatException | ParametreInvalidException | EnquestaNoExisteixException e){
            System.out.println("❌ Error: " + e.getMessage());
        }                
        
        Pregunta preguntaActual = escollirPregunta(enquesta);

        //mostrar opcions de la pregunta actual
        System.out.println("\n─── OPCIONS DE LA PREGUNTA ───");
        for (Opcio o : preguntaActual.getOpcions()) {
            System.out.println("ID: " + o.getId() + " - Text: " + o.getText());
        }
        
        System.out.println("Introdueix ID de l'opció a eliminar: ");
        int idOpcio = Integer.parseInt(in.nextLine());
        
        try{
        cd.eliminarOpcioDePregunta(idEnquesta, preguntaActual.getId(), idOpcio);
        System.out.println("Opció eliminada");
        } catch (ParametreInvalidException | UsuariNoAutenticatException | EnquestaNoExisteixException | PreguntaNoExisteixException | PermisDenegatException | RespostaInvalidaException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // --- Consultores ---  

    private static void consultarEnquestes() {
        System.out.println("\n─── LLISTAT D'ENQUESTES ───");
        List<Enquesta> enquestes = null;
        try{
        enquestes = cd.consultarEnquestes();
        } catch (UsuariNoAutenticatException e){
            System.out.println("❌ Error: " + e.getMessage());

        }
        
        if (enquestes.isEmpty()) {
            System.out.println("No hi ha enquestes disponibles.");
        } else {
            for (Enquesta e : enquestes) {
                System.out.println("ID: " + e.getId() + " | Títol: " + e.getTitol() + " | Descripció: " + e.getDescripcio() + " | Creat per: " + e.getIdCreador());
                //llistar preguntes
                List<Pregunta> preguntes = e.getPreguntes();
                if (preguntes.isEmpty()) {
                    System.out.println("  No hi ha preguntes a aquesta enquesta.");
                } else {
                    for (Pregunta p : preguntes) {
                        System.out.println("  Pregunta ID: " + p.getId() + " | Text: " + p.getText() + " | Tipus: " + p.getTipus());
                        //llistar opcions si n'hi ha
                        List<Opcio> opcions = p.getOpcions();
                        if (!opcions.isEmpty()) {
                            System.out.println("    Opcions:");
                            for (Opcio o : opcions) {
                                System.out.println("      ID: " + o.getId() + " | Text: " + o.getText() + " | Ordre: " + o.getOrdre());
                            }
                        }
                    }
            }
            
        }
        }
    }

    private static void testImportarEnquesta() {
        
        System.out.println("\n═══ IMPORTAR ENQUESTA DES DE JSON ═══");
        System.out.println("Fitxer d'exemple: exemple_enquesta.json");
    System.out.println("Ruta del fitxer JSON (o només el nom si està en el directori actual): ");
        String path = in.nextLine().trim();
        
        // Si solo es un nombre de archivo, añadir la ruta completa
        if (!path.contains("\\") && !path.contains("/")) {
            path = System.getProperty("user.dir") + "\\" + path;
        }
        
        try {
            cd.importarEnquesta(path);
            System.out.println("✓ Enquesta importada correctament!");
        } catch (ErrorImportacioException e) {
            System.out.println("❌ Error important l'enquesta: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error inesperat: " + e.getMessage());
        }
    

    }




    // --- Mètodes Auxiliars ---
    

    private static void afegirOpcions(Pregunta pregunta, boolean ordenada) {
        System.out.println("\n─── AFEGIR OPCIONS ───");
        
        int n = -1;
        while (n < 1) {
            try {
                System.out.println("Quantes opcions vols afegir? ");
                n = Integer.parseInt(in.nextLine());
                if (n < 1) {
                    System.out.println("❌ Ha de ser almenys 1 opció");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Si us plau, introdueix un número vàlid");
            }
        }
        
        for (int i = 0; i < n; i++) {
            System.out.println("Text opció " + (i + 1) + ": ");
            String text = in.nextLine();
            
            if (ordenada) {
                int ordre = -1;
                boolean ordreValid = false;
                while (!ordreValid) {
                    try {
                        System.out.println("Ordre (número): ");
                        ordre = Integer.parseInt(in.nextLine());
                        ordreValid = true;
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Si us plau, introdueix un número vàlid");
                    }
                }
                pregunta.afegirOpcio(new Opcio(i, text, ordre));
            } else {
                pregunta.afegirOpcio(new Opcio(i, text));
            }
        }
        
        System.out.println("✓ " + n + " opcions afegides!");
    }

    private static Pregunta escollirPregunta(Enquesta enquesta) {
        List<Pregunta> preguntes = enquesta.getPreguntes();
        if (preguntes.isEmpty()) {
            System.out.println("⚠ L'enquesta no té preguntes");
            return null;
        }
        
        System.out.println("\n─── PREGUNTES ───");
        for (int i = 0; i < preguntes.size(); i++) {
            System.out.println((i + 1) + ". " + preguntes.get(i).getText());
        }
        
        int num = -1;
        boolean numValid = false;
        
        while (!numValid) {
            try {
                System.out.println("Escull la pregunta: ");
                num = Integer.parseInt(in.nextLine()) - 1;
                
                if (num < 0 || num >= preguntes.size()) {
                    System.out.println("❌ Número no vàlid. Tria un número entre 1 i " + preguntes.size());
                } else {
                    numValid = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Si us plau, introdueix un número vàlid");
            }
        }
        
        return preguntes.get(num);
    }
    
}
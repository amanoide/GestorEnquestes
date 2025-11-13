package edu.upc.prop.clusterxx;

import edu.upc.prop.clusterxx.domini.classes.*;
import edu.upc.prop.clusterxx.domini.controladors.CtrlDomini;
import static edu.upc.prop.clusterxx.domini.classes.Exceptions.*;
import java.util.*;

/**
 * Driver principal integrado del sistema d'enquestes
 * Permet provar totes les funcionalitats de forma coordinada
 * Utilitza CtrlDomini com a punt d'entrada únic per totes les operacions
 */
public class MainDriver {
    private static Scanner in = new Scanner(System.in);
    private static CtrlDomini ctrlDomini = new CtrlDomini();
    private static Usuari usuariActual = null;

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║   SISTEMA DE GESTIÓ D'ENQUESTES - DEMO     ║");
        System.out.println("╚════════════════════════════════════════════╝\n");
        
        boolean continuar = true;
        while (continuar) {
            if (usuariActual == null) {
                continuar = menuNoAutenticat();
            } else {
                continuar = menuAutenticat();
            }
        }
        
        System.out.println("\n👋 Gràcies per usar el sistema!");
        in.close();
    }

    private static boolean menuNoAutenticat() {
        System.out.println("\n┌─────────────────────────────────┐");
        System.out.println("│  MENÚ PRINCIPAL (No autenticat) │");
        System.out.println("├─────────────────────────────────┤");
        System.out.println("│ 1. Registrar nou usuari         │");
        System.out.println("│ 2. Iniciar sessió (login)       │");
        System.out.println("│ 0. Sortir                       │");
        System.out.println("└─────────────────────────────────┘");
        System.out.print("→ ");
        
        String opcio = in.nextLine().trim();
        
        switch (opcio) {
            case "1":
                registrarUsuari();
                break;
            case "2":
                login();
                break;
            case "0":
                return false;
            default:
                System.out.println("❌ Opció no vàlida");
        }
        
        return true;
    }

    private static boolean menuAutenticat() {
        System.out.println("\n┌────────────────────────────────────────────┐");
        System.out.println("│  MENÚ PRINCIPAL - Usuari: " + usuariActual.getUsername() );
        System.out.println("├────────────────────────────────────────────┤");
        System.out.println("│ 1. Crear enquesta                          │");
        System.out.println("│ 2. Llistar les meves enquestes             │");
        System.out.println("│ 3. Gestionar enquesta (afegir preguntes)   │");
        System.out.println("│ 4. Respondre enquesta                      │");
        System.out.println("│ 5. Veure totes les enquestes del sistema   │");
        System.out.println("│ 6. Estadístiques del sistema               │");
        System.out.println("│ 7. Consultar respostes d'una enquesta      │");
        System.out.println("│ 8. Veure les meves respostes               │");
        System.out.println("│ 9. Provar drivers individuals              │");
        System.out.println("│ 10. Tancar sessió (logout)                 │");
        System.out.println("│ 0. Sortir                                  │");
        System.out.println("└────────────────────────────────────────────┘");
        System.out.print("→ ");
        
        String opcio = in.nextLine().trim();
        
        switch (opcio) {
            case "1":
                crearEnquesta();
                break;
            case "2":
                llistarMevesEnquestes();
                break;
            case "3":
                gestionarEnquesta();
                break;
            case "4":
                respondreEnquesta();
                break;
            case "5":
                veureTotesEnquestes();
                break;
            case "6":
                mostrarEstadistiques();
                break;
            case "7":
                consultarRespostes();
                break;
            case "8":
                consultarMevesRespostes();
                break;
            case "9":
                menuDrivers();
                break;
            case "10":
                logout();
                break;
            case "0":
                return false;
            default:
                System.out.println("❌ Opció no vàlida");
        }
        
        return true;
    }

    private static void registrarUsuari() {
        System.out.println("\n═══ REGISTRAR NOU USUARI ═══");
        System.out.print("Username: ");
        String username = in.nextLine();
        System.out.print("Password: ");
        String password = in.nextLine();
        
        try {
            ctrlDomini.registrarUsuari(username, password);
            System.out.println("✓ Usuari registrat correctament!");
            System.out.println("  Ara pots fer login amb aquest usuari.");
        } catch (UsuariJaExisteixException e) {
            System.out.println("❌ Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private static void login() {
        System.out.println("\n═══ INICIAR SESSIÓ ═══");
        System.out.print("Username: ");
        String username = in.nextLine();
        System.out.print("Password: ");
        String password = in.nextLine();
        
        try {
            ctrlDomini.login(username, password);
            usuariActual = ctrlDomini.getUsuariActual();
            
            System.out.println("✓ Login correcte!");
            System.out.println("  Benvingut/da, " + username + "!");
        } catch (CredencialsIncorrectesException e) {
            System.out.println("❌ Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private static void logout() {
        System.out.println("\n✓ Sessió tancada. Fins aviat, " + usuariActual.getUsername() + "!");
        usuariActual = null;
    }

    private static void crearEnquesta() {
        System.out.println("\n═══ CREAR NOVA ENQUESTA ═══");
        System.out.print("ID de l'enquesta: ");
        String id = in.nextLine();
        
        if (ctrlDomini.existeixEnquesta(id)) {
            System.out.println("❌ Ja existeix una enquesta amb aquest ID");
            return;
        }
        
        System.out.print("Títol: ");
        String titol = in.nextLine();
        System.out.print("Descripció: ");
        String descripcio = in.nextLine();
        
        try {
            ctrlDomini.crearEnquesta(usuariActual, id, titol, descripcio);
            System.out.println("✓ Enquesta creada correctament!");
            System.out.println("  ID: " + id);
            System.out.println("  Títol: " + titol);
        } catch (Exception e) {
            System.out.println("❌ Error creant l'enquesta: " + e.getMessage());
        }
    }

    private static void llistarMevesEnquestes() {
        System.out.println("\n═══ LES MEVES ENQUESTES ═══");
        List<Enquesta> mevesEnquestes = usuariActual.getEnquestesCreades();
        
        if (mevesEnquestes.isEmpty()) {
            System.out.println("No has creat cap enquesta encara.");
        } else {
            for (int i = 0; i < mevesEnquestes.size(); i++) {
                Enquesta e = mevesEnquestes.get(i);
                System.out.println((i + 1) + ". ID: " + e.getId());
                System.out.println("   Títol: " + e.getTitol());
                System.out.println("   Descripció: " + e.getDescripcio());
                System.out.println("   Preguntes: " + e.getPreguntes().size());
                System.out.println("   Participants: " + e.getParticipants().size());
                System.out.println();
            }
        }
    }

    private static void gestionarEnquesta() {
        System.out.println("\n═══ GESTIONAR ENQUESTA ═══");
        System.out.print("ID de l'enquesta: ");
        String id = in.nextLine();
        
        try {
            Enquesta enquesta = ctrlDomini.getEnquesta(id);
            
            if (!enquesta.getIdCreador().equals(usuariActual.getUsername())) {
                throw new PermisDenegatException("No tens permís per modificar aquesta enquesta");
            }
            
            boolean continuar = true;
            while (continuar) {
                System.out.println("\n┌─ ENQUESTA: " + enquesta.getTitol() + " ─┐");
                System.out.println("│ 1. Afegir pregunta              │");
                System.out.println("│ 2. Eliminar pregunta            │");
                System.out.println("│ 3. Llistar preguntes            │");
                System.out.println("│ 4. Modificar pregunta           │");
                System.out.println("│ 5. Modificar títol              │");
                System.out.println("│ 6. Modificar descripció         │");
                System.out.println("│ 0. Tornar al menú principal     │");
                System.out.println("└─────────────────────────────────┘");
                System.out.print("→ ");
                
                String opcio = in.nextLine().trim();
                
                switch (opcio) {
                    case "1":
                        afegirPregunta(enquesta);
                        break;
                    case "2":
                        eliminarPregunta(enquesta);
                        break;
                    case "3":
                        llistarPreguntes(enquesta);
                        break;
                    case "4":
                        modificarPregunta(enquesta);
                        break;
                    case "5":
                        modificarTitolEnquesta(enquesta);
                        break;
                    case "6":
                        modificarDescripcioEnquesta(enquesta);
                        break;
                    case "0":
                        continuar = false;
                        break;
                    default:
                        System.out.println("❌ Opció no vàlida");
                }
            }
        } catch (ParametreInvalidException | UsuariNoAutenticatException | EnquestaNoExisteixException | PermisDenegatException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private static void afegirPregunta(Enquesta enquesta) {
        System.out.println("\n─── AFEGIR PREGUNTA ───");
        System.out.print("ID de la pregunta: ");
        String id = in.nextLine();
        
        // Verificar si ja existeix aquesta pregunta a l'enquesta
        for (Pregunta p : enquesta.getPreguntes()) {
            if (p.getId().equals(id)) {
                System.out.println("❌ Ja existeix una pregunta amb aquest ID a l'enquesta");
                return;
            }
        }
        
        System.out.print("Text de la pregunta: ");
        String text = in.nextLine();
        
        System.out.println("\nTipus de pregunta:");
        System.out.println("  1. Text lliure");
        System.out.println("  2. Numèrica");
        System.out.println("  3. Qualitativa ordenada");
        System.out.println("  4. Qualitativa no ordenada simple");
        System.out.println("  5. Qualitativa no ordenada múltiple");
        System.out.print("Escull (1-5): ");
        
        String tipusOpcio = in.nextLine();
        Pregunta pregunta = null;
        
        switch (tipusOpcio) {
            case "1":
                pregunta = new Pregunta(id, text, "TEXT_LLIURE");
                break;
            case "2":
                double min = 0, max = 0;
                boolean valorsValids = false;
                
                while (!valorsValids) {
                    try {
                        System.out.print("Valor mínim: ");
                        min = Double.parseDouble(in.nextLine());
                        System.out.print("Valor màxim: ");
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
                
                pregunta = new Pregunta(id, text, min, max);
                break;
            case "3":
                pregunta = new Pregunta(id, text, "QUALITATIVA_ORDENADA");
                afegirOpcions(pregunta, true);
                break;
            case "4":
                pregunta = new Pregunta(id, text, "QUALITATIVA_NO_ORDENADA_SIMPLE");
                afegirOpcions(pregunta, false);
                break;
            case "5":
                int maxSel = -1;
                while (maxSel < 1) {
                    try {
                        System.out.print("Màxim de seleccions: ");
                        maxSel = Integer.parseInt(in.nextLine());
                        if (maxSel < 1) {
                            System.out.println("❌ Ha de ser un número positiu");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Si us plau, introdueix un número vàlid");
                    }
                }
                pregunta = new Pregunta(id, text, TipusPregunta.QUALITATIVA_NO_ORDENADA_MULTIPLE, maxSel);
                afegirOpcions(pregunta, false);
                break;
            default:
                System.out.println("❌ Tipus no vàlid");
                return;
        }
        
        try {
            ctrlDomini.afegirPregunta(enquesta.getId(), pregunta);
            System.out.println("✓ Pregunta afegida correctament!");
        } catch (ParametreInvalidException | UsuariNoAutenticatException | EnquestaNoExisteixException | PermisDenegatException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private static void afegirOpcions(Pregunta pregunta, boolean ordenada) {
        System.out.println("\n─── AFEGIR OPCIONS ───");
        
        int n = -1;
        while (n < 1) {
            try {
                System.out.print("Quantes opcions vols afegir? ");
                n = Integer.parseInt(in.nextLine());
                if (n < 1) {
                    System.out.println("❌ Ha de ser almenys 1 opció");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Si us plau, introdueix un número vàlid");
            }
        }
        
        for (int i = 0; i < n; i++) {
            System.out.print("Text opció " + (i + 1) + ": ");
            String text = in.nextLine();
            
            if (ordenada) {
                int ordre = -1;
                boolean ordreValid = false;
                while (!ordreValid) {
                    try {
                        System.out.print("Ordre (número): ");
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

    private static void eliminarPregunta(Enquesta enquesta) {
        List<Pregunta> preguntes = enquesta.getPreguntes();
        if (preguntes.isEmpty()) {
            System.out.println("⚠ L'enquesta no té preguntes");
            return;
        }
        
        System.out.println("\n─── ELIMINAR PREGUNTA ───");
        for (int i = 0; i < preguntes.size(); i++) {
            System.out.println((i + 1) + ". " + preguntes.get(i).getText());
        }
        
        int num = -1;
        boolean numValid = false;
        
        while (!numValid) {
            try {
                System.out.print("Número de pregunta a eliminar: ");
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
        
        Pregunta p = preguntes.get(num);
        try {
            ctrlDomini.eliminarPregunta(enquesta.getId(), p.getId());
            System.out.println("✓ Pregunta eliminada!");
        } catch (EnquestaNoExisteixException | PermisDenegatException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private static void modificarPregunta(Enquesta enquesta) {
        List<Pregunta> preguntes = enquesta.getPreguntes();
        if (preguntes.isEmpty()) {
            System.out.println("⚠ L'enquesta no té preguntes");
            return;
        }
        
        System.out.println("\n─── MODIFICAR PREGUNTA ───");
        for (int i = 0; i < preguntes.size(); i++) {
            System.out.println((i + 1) + ". " + preguntes.get(i).getText());
        }
        
        int num = -1;
        boolean numValid = false;
        
        while (!numValid) {
            try {
                System.out.print("Número de pregunta a modificar: ");
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
        
        Pregunta preguntaActual = preguntes.get(num);
        System.out.println("\n─── Pregunta actual ───");
        System.out.println("Text: " + preguntaActual.getText());
        System.out.println("Tipus: " + preguntaActual.getTipus());
        
        System.out.println("\n¿Què vols modificar?");
        System.out.println("  1. Només el text");
        System.out.println("  2. Text i tipus (es crearà una nova pregunta)");
        System.out.print("Escull (1-2): ");
        
        String opcio = in.nextLine();
        
        if (opcio.equals("1")) {
            // Modificar només el text
            System.out.print("Nou text de la pregunta: ");
            String nouText = in.nextLine();
            
            try {
                // Crear una nova pregunta amb el mateix tipus però diferent text
                Pregunta novaPregunta = null;
                
                switch (preguntaActual.getTipus()) {
                    case TEXT_LLIURE:
                        novaPregunta = new Pregunta(preguntaActual.getId(), nouText, "TEXT_LLIURE");
                        break;
                    case NUMERICA:
                        novaPregunta = new Pregunta(preguntaActual.getId(), nouText, 
                            preguntaActual.getValorMinim(), preguntaActual.getValorMaxim());
                        break;
                    case QUALITATIVA_ORDENADA:
                        novaPregunta = new Pregunta(preguntaActual.getId(), nouText, "QUALITATIVA_ORDENADA");
                        // Copiar les opcions
                        for (Opcio o : preguntaActual.getOpcions()) {
                            novaPregunta.afegirOpcio(o);
                        }
                        break;
                    case QUALITATIVA_NO_ORDENADA_SIMPLE:
                        novaPregunta = new Pregunta(preguntaActual.getId(), nouText, "QUALITATIVA_NO_ORDENADA_SIMPLE");
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
                
                ctrlDomini.modificarPregunta(enquesta.getId(), preguntaActual.getId(), novaPregunta);
                System.out.println("✓ Pregunta modificada correctament!");
                
            } catch (EnquestaNoExisteixException | PermisDenegatException e) {
                System.out.println("❌ Error: " + e.getMessage());
            }
            
        } else if (opcio.equals("2")) {
            // Crear una pregunta completament nova
            System.out.print("Nou text de la pregunta: ");
            String nouText = in.nextLine();
            
            System.out.println("\nNou tipus de pregunta:");
            System.out.println("  1. Text lliure");
            System.out.println("  2. Numèrica");
            System.out.println("  3. Qualitativa ordenada");
            System.out.println("  4. Qualitativa no ordenada simple");
            System.out.println("  5. Qualitativa no ordenada múltiple");
            System.out.print("Escull (1-5): ");
            
            String tipusOpcio = in.nextLine();
            Pregunta novaPregunta = null;
            
            try {
                switch (tipusOpcio) {
                    case "1":
                        novaPregunta = new Pregunta(preguntaActual.getId(), nouText, "TEXT_LLIURE");
                        break;
                    case "2":
                        double min = 0, max = 0;
                        boolean valorsValids = false;
                        
                        while (!valorsValids) {
                            try {
                                System.out.print("Valor mínim: ");
                                min = Double.parseDouble(in.nextLine());
                                System.out.print("Valor màxim: ");
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
                        novaPregunta = new Pregunta(preguntaActual.getId(), nouText, "QUALITATIVA_ORDENADA");
                        afegirOpcions(novaPregunta, true);
                        break;
                    case "4":
                        novaPregunta = new Pregunta(preguntaActual.getId(), nouText, "QUALITATIVA_NO_ORDENADA_SIMPLE");
                        afegirOpcions(novaPregunta, false);
                        break;
                    case "5":
                        int maxSel = -1;
                        while (maxSel < 1) {
                            try {
                                System.out.print("Màxim de seleccions: ");
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
                
                ctrlDomini.modificarPregunta(enquesta.getId(), preguntaActual.getId(), novaPregunta);
                System.out.println("✓ Pregunta modificada correctament!");
                
            } catch (EnquestaNoExisteixException | PermisDenegatException e) {
                System.out.println("❌ Error: " + e.getMessage());
            }
            
        } else {
            System.out.println("❌ Opció no vàlida");
        }
    }

    private static void llistarPreguntes(Enquesta enquesta) {
        System.out.println("\n═══ PREGUNTES DE L'ENQUESTA ═══");
        List<Pregunta> preguntes = enquesta.getPreguntes();
        
        if (preguntes.isEmpty()) {
            System.out.println("L'enquesta no té preguntes encara.");
        } else {
            for (int i = 0; i < preguntes.size(); i++) {
                Pregunta p = preguntes.get(i);
                System.out.println("\n" + (i + 1) + ". " + p.getText());
                System.out.println("   ID: " + p.getId());
                System.out.println("   Tipus: " + p.getTipus());
                
                if (p.getTipus() == TipusPregunta.NUMERICA) {
                    System.out.println("   Rang: [" + p.getValorMinim() + ", " + p.getValorMaxim() + "]");
                }
                
                if (p.getOpcions().size() > 0) {
                    System.out.println("   Opcions:");
                    for (Opcio o : p.getOpcions()) {
                        System.out.println("     - " + o.getText() + 
                            (o.esOrdenada() ? " (ordre: " + o.getOrdre() + ")" : ""));
                    }
                }
            }
        }
    }

    private static void modificarTitolEnquesta(Enquesta enquesta) {
        System.out.println("\n─── MODIFICAR TÍTOL ───");
        System.out.println("Títol actual: " + enquesta.getTitol());
        System.out.print("Nou títol: ");
        String nouTitol = in.nextLine();
        try {
            ctrlDomini.modificarTitolEnquesta(enquesta.getId(), nouTitol);
            System.out.println("✓ Títol modificat!");
        } catch (ParametreInvalidException | UsuariNoAutenticatException | EnquestaNoExisteixException | PermisDenegatException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private static void modificarDescripcioEnquesta(Enquesta enquesta) {
        System.out.println("\n─── MODIFICAR DESCRIPCIÓ ───");
        System.out.println("Descripció actual: " + enquesta.getDescripcio());
        System.out.print("Nova descripció: ");
        String novaDesc = in.nextLine();
        try {
            ctrlDomini.modificarDescripcioEnquesta(enquesta.getId(), novaDesc);
            System.out.println("✓ Descripció modificada!");
        } catch (ParametreInvalidException | UsuariNoAutenticatException | EnquestaNoExisteixException | PermisDenegatException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private static void respondreEnquesta() {
        System.out.println("\n═══ RESPONDRE ENQUESTA ═══");
        
        try {
            ArrayList<Enquesta> totes = ctrlDomini.consultarEnquestes();
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
                            ctrlDomini.registrarResposta(enquesta.getId(), usuariActual, p.getId(), respText);
                            break;
                        
                        case NUMERICA:
                            System.out.print("  Valor (" + p.getValorMinim() + "-" + p.getValorMaxim() + "): ");
                            String respNum = in.nextLine();
                            ctrlDomini.registrarResposta(enquesta.getId(), usuariActual, p.getId(), respNum);
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
                        
                            ctrlDomini.registrarResposta(enquesta.getId(), usuariActual, p.getId(), opcions.get(opcioIdx).getText());
                            break;
                        
                        case QUALITATIVA_NO_ORDENADA_MULTIPLE:
                            List<Opcio> opcionsM = p.getOpcions();
                            for (int j = 0; j < opcionsM.size(); j++) {
                                System.out.println("  " + (j + 1) + ". " + opcionsM.get(j).getText());
                            }
                            System.out.print("  Escull opcions separades per comes (ex: 1,3,4): ");
                            String opcionsEsc = in.nextLine();
                            ctrlDomini.registrarResposta(enquesta.getId(), usuariActual, p.getId(), opcionsEsc);
                            break;
                    }
                }
            
                ctrlDomini.registrarParticipacio(enquesta.getId(), usuariActual.getUsername());
                System.out.println("\n✓ Enquesta completada! Gràcies per participar.");
            
            } catch (PreguntaNoExisteixException e) {
                System.out.println("❌ Error: " + e.getMessage());
            }
        } catch (UsuariNoAutenticatException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private static void veureTotesEnquestes() {
        System.out.println("\n═══ TOTES LES ENQUESTES DEL SISTEMA ═══");
        
        try {
            ArrayList<Enquesta> totes = ctrlDomini.consultarEnquestes();
            if (totes.isEmpty()) {
                System.out.println("No hi ha enquestes al sistema.");
            } else {
                for (Enquesta e : totes) {
                    System.out.println("\n┌─ " + e.getTitol() + " ─┐");
                    System.out.println("│ ID: " + e.getId());
                    System.out.println("│ Descripció: " + e.getDescripcio());
                    System.out.println("│ Creador: " + e.getIdCreador());
                    System.out.println("│ Preguntes: " + e.getPreguntes().size());
                    System.out.println("│ Participants: " + e.getParticipants().size());
                    System.out.println("└─────────────────────────┘");
                }
            }
        } catch (UsuariNoAutenticatException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    
    private static void mostrarEstadistiques() {
        System.out.println("\n═══ ESTADÍSTIQUES DEL SISTEMA ═══");
        
        HashMap<String, Integer> stats = ctrlDomini.getEstadistiques();
        
        System.out.println("Total enquestes: " + stats.get("totalEnquestes"));
        System.out.println("Total usuaris: " + stats.get("totalUsuaris"));
        System.out.println("Total preguntes: " + stats.get("totalPreguntes"));
        System.out.println("Total participacions: " + stats.get("totalParticipants"));
    }
    
    private static void consultarRespostes() {
        System.out.println("\n═══ CONSULTAR RESPOSTES D'UNA ENQUESTA ═══");
        
        try {
            ArrayList<Enquesta> totes = ctrlDomini.consultarEnquestes();
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
        
        Enquesta enquesta = totes.get(num);
        
        // Verificar si l'usuari és el creador de l'enquesta
        if (!enquesta.getIdCreador().equals(usuariActual.getUsername())) {
            System.out.println("⚠ Atenció: Només pots veure les respostes de les enquestes que has creat.");
            System.out.println("  Aquesta enquesta ha estat creada per: " + enquesta.getIdCreador());
            return;
        }
        
        // Utilitzar el mètode de CtrlDomini
        // Nota: consultarRespostesEnquesta retorna HashMap<username, ArrayList<Resposta>>
        HashMap<String, ArrayList<Resposta>> respostesPerUsuariOriginal = ctrlDomini.consultarRespostesEnquesta(enquesta.getId());
        
        if (respostesPerUsuariOriginal.isEmpty()) {
            System.out.println("\n⚠ Aquesta enquesta encara no té respostes.");
            return;
        }
        
        System.out.println("\n═══ RESPOSTES DE: " + enquesta.getTitol() + " ═══");
        
        // Reorganitzar les respostes en un format més còmode: usuari -> (idPregunta -> Resposta)
        HashMap<String, HashMap<String, Resposta>> respostesPerUsuari = new HashMap<>();
        
        for (Map.Entry<String, ArrayList<Resposta>> entry : respostesPerUsuariOriginal.entrySet()) {
            String username = entry.getKey();
            ArrayList<Resposta> respostes = entry.getValue();
            
            HashMap<String, Resposta> respostesUsuari = new HashMap<>();
            for (Resposta r : respostes) {
                respostesUsuari.put(r.getIdPregunta(), r);
            }
            
            respostesPerUsuari.put(username, respostesUsuari);
        }
        
        System.out.println("Total de participants: " + respostesPerUsuari.size());
        
        // Mostrar les respostes agrupades per usuari
        for (Map.Entry<String, HashMap<String, Resposta>> entryUsuari : respostesPerUsuari.entrySet()) {
            String username = entryUsuari.getKey();
            HashMap<String, Resposta> respostesUsuari = entryUsuari.getValue();
            
            System.out.println("\n┌─ Usuari: " + username + " ─┐");
            
            for (Pregunta p : enquesta.getPreguntes()) {
                Resposta resposta = respostesUsuari.get(p.getId());
                
                if (resposta != null) {
                    System.out.println("│ ➤ " + p.getText());
                    System.out.println("│   Resposta: " + resposta.getTextResposta());
                } else {
                    System.out.println("│ ➤ " + p.getText());
                    System.out.println("│   Resposta: [Sense respondre]");
                }
            }
            
            System.out.println("└" + "─".repeat(50) + "┘");
        }
        
        // Estadístiques bàsiques per pregunta
        System.out.println("\n═══ RESUM PER PREGUNTA ═══");
        
        // Reorganitzar respostes per pregunta per a les estadístiques
        HashMap<String, ArrayList<Resposta>> respostesPerPregunta = new HashMap<>();
        for (Map.Entry<String, HashMap<String, Resposta>> entryUsuari : respostesPerUsuari.entrySet()) {
            HashMap<String, Resposta> respostesUsuari = entryUsuari.getValue();
            
            for (Map.Entry<String, Resposta> entryResp : respostesUsuari.entrySet()) {
                String idPregunta = entryResp.getKey();
                Resposta resposta = entryResp.getValue();
                
                if (!respostesPerPregunta.containsKey(idPregunta)) {
                    respostesPerPregunta.put(idPregunta, new ArrayList<>());
                }
                respostesPerPregunta.get(idPregunta).add(resposta);
            }
        }
        
        for (Pregunta p : enquesta.getPreguntes()) {
            System.out.println("\n➤ " + p.getText());
            
            ArrayList<Resposta> respostesPregunta = respostesPerPregunta.get(p.getId());
            int totalRespostes = respostesPregunta != null ? respostesPregunta.size() : 0;
            
            System.out.println("  Total respostes: " + totalRespostes);
            
            if (totalRespostes > 0 && p.getTipus() != TipusPregunta.TEXT_LLIURE) {
                HashMap<String, Integer> comptadorRespostes = new HashMap<>();
                
                for (Resposta r : respostesPregunta) {
                    String text = r.getTextResposta();
                    comptadorRespostes.put(text, comptadorRespostes.getOrDefault(text, 0) + 1);
                }
                
                System.out.println("  Distribució:");
                for (Map.Entry<String, Integer> entry : comptadorRespostes.entrySet()) {
                    double percentatge = (entry.getValue() * 100.0) / totalRespostes;
                    System.out.printf("    - %s: %d (%.1f%%)%n", 
                        entry.getKey(), entry.getValue(), percentatge);
                }
            } else if (totalRespostes > 0) {
                System.out.println("  (Respostes de text lliure - veure detall per usuari)");
            }
        }
        
        } catch (UsuariNoAutenticatException e) {
            System.out.println("❌ Error: " + e.getMessage());
        } catch (ParametreInvalidException | EnquestaNoExisteixException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    
    private static void consultarMevesRespostes() {
        System.out.println("\n═══ LES MEVES RESPOSTES ═══");
        
        try {
            ArrayList<Enquesta> totes = ctrlDomini.consultarEnquestes();
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
        
            Enquesta enquesta = totes.get(num);
            
            // Obtenir les meves respostes a aquesta enquesta
            HashMap<String, Resposta> mevesRespostes = ctrlDomini.consultarMevesRespostesEnquesta(enquesta.getId());
            
            if (mevesRespostes.isEmpty()) {
                System.out.println("\n⚠ No has respost aquesta enquesta encara.");
                return;
            }
            
            System.out.println("\n═══ LES TEVES RESPOSTES A: " + enquesta.getTitol() + " ═══");
            System.out.println("Total de preguntes respostes: " + mevesRespostes.size() + " de " + enquesta.getPreguntes().size());
            
            // Mostrar cada pregunta amb la meva resposta
            for (Pregunta p : enquesta.getPreguntes()) {
                Resposta mevaResposta = mevesRespostes.get(p.getId());
                
                System.out.println("\n┌─ " + p.getText() + " ─┐");
                System.out.println("│ Tipus: " + p.getTipus());
                
                if (mevaResposta != null) {
                    System.out.println("│ ✓ La teva resposta: " + mevaResposta.getTextResposta());
                } else {
                    System.out.println("│ ✗ Sense respondre");
                }
                
                System.out.println("└" + "─".repeat(50) + "┘");
            }
            
        } catch (UsuariNoAutenticatException e) {
            System.out.println("❌ Error: " + e.getMessage());
        } catch (ParametreInvalidException | EnquestaNoExisteixException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    
    private static void menuDrivers() {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n╔═══════════════════════════════════════════╗");
            System.out.println("║    MENÚ DE DRIVERS DE CONTROLADORS        ║");
            System.out.println("╠═══════════════════════════════════════════╣");
            // System.out.println("║ 1 │ Driver Usuari                         ║");
            // System.out.println("║ 2 │ Driver Enquesta                       ║");
            // System.out.println("║ 3 │ Driver Pregunta                       ║");
            // System.out.println("║ 4 │ Driver Resposta                       ║");
            // System.out.println("║ 5 │ Driver Opcio                          ║");
            System.out.println("║ 1 │ Driver CtrlDomini                     ║");
            System.out.println("║ 2 │ Driver CtrlEnquesta                   ║");
            System.out.println("║ 3 │ Driver CtrlPregunta                   ║");
            System.out.println("║ 4 │ Driver CtrlResposta                   ║");
            System.out.println("║ 5 │ Driver CtrlUsuari                     ║");
            System.out.println("║ 6 │ Driver CtrlPersistencia               ║");
            System.out.println("║ 7 │ Driver CtrlPerfil                     ║");
            System.out.println("║ 8 │ Driver CtrlAnalisi                    ║");
            System.out.println("║ 0 │ Tornar al menú principal              ║");
            System.out.println("╚═══════════════════════════════════════════╝");
            System.out.print("→ ");
            
            String opcio = "";
            try {
                opcio = in.nextLine().trim();
            } catch (Exception e) {
                // Si hay cualquier problema con el Scanner, salir del menú
                System.out.println("\n⚠ Error llegint l'entrada. Tornant al menú principal...");
                break;
            }
            
            switch (opcio) {
                // case "1":
                //     executarDriver("domini.UsuariDriver");
                //     break;
                // case "2":
                //     executarDriver("domini.EnquestaDriver");
                //     break;
                // case "3":
                //     executarDriver("domini.PreguntaDriver");
                //     break;
                // case "4":
                //     executarDriver("domini.RespostaDriver");
                //     break;
                // case "5":
                //     executarDriver("domini.OpcioDriver");
                //     break;
                case "1":
                    executarDriver("controladors.CtrlDominiDriver");
                    break;
                case "2":
                    executarDriver("controladors.CtrlEnquestaDriver");
                    break;
                case "3":
                    executarDriver("controladors.CtrlPreguntaDriver");
                    break;
                case "4":
                    executarDriver("controladors.CtrlRespostaDriver");
                    break;
                case "5":
                    executarDriver("controladors.CtrlUsuariDriver");
                    break;
                case "6":
                    executarDriver("controladors.CtrlPersistenciaDriver");
                    break;
                case "7":
                    executarDriver("controladors.CtrlPerfilDriver");
                    break;
                case "8":
                    executarDriver("controladors.CtrlAnalisiDriver");
                    break;
                case "0":
                    continuar = false;
                    break;
                default:
                    System.out.println("❌ Opció no vàlida");
            }
        }
    }
    
    private static void executarDriver(String driverClass) {
        try {
            String fullClassName = "edu.upc.prop.clusterxx." + driverClass;
            Class<?> clazz = Class.forName(fullClassName);
            java.lang.reflect.Method mainMethod = clazz.getMethod("main", String[].class);
            
            System.out.println("\n═══════════════════════════════════════════");
            System.out.println("   Executant " + driverClass);
            System.out.println("═══════════════════════════════════════════\n");
            
            String[] args = new String[0];
            mainMethod.invoke(null, (Object) args);
            
            System.out.println("\n═══════════════════════════════════════════");
            System.out.println("   Fi de " + driverClass);
            System.out.println("═══════════════════════════════════════════");
            
            // Recrear el Scanner después de ejecutar el driver
            // porque muchos drivers hacen in.close() al terminar
            in = new Scanner(System.in);
            
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Error: No s'ha trobat la classe del driver: " + driverClass);
        } catch (NoSuchMethodException e) {
            System.out.println("❌ Error: El driver no té un mètode main()");
        } catch (Exception e) {
            System.out.println("❌ Error en executar el driver: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

package edu.upc.prop.clusterxx.controladors;

// Importacions dels controladors
import edu.upc.prop.clusterxx.domini.controladors.CtrlAnalisi;
import edu.upc.prop.clusterxx.domini.controladors.CtrlDomini;
// Importacions dels stubs de domini
import edu.upc.prop.clusterxx.domini.classes.ClusteringAlgorithm;
import edu.upc.prop.clusterxx.domini.classes.KMeans;
import static edu.upc.prop.clusterxx.domini.classes.Exceptions.*;

// Importacions de Java
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Driver per provar la classe CtrlAnalisi.
 * Aquest driver depèn dels STUBS de KMeans i ClusteringAlgorithm.
 */
public class CtrlAnalisiDriver {

    private static Scanner in;
    private static CtrlAnalisi ca;
    private static CtrlDomini cd;
    
    /**
     * Emmagatzemem les últimes dades creades per poder-les
     * passar al mètode silhouette().
     */
    private static List<double[]> dadesActuals;

    public static void main(String[] args) {
        init();
        System.out.println("Driver de testeig de CtrlAnalisi");

        String input = "";
        while (!input.equals("0") && !input.equals("sortir")) {
            mostra_metodes();
            input = in.nextLine();

            try {
                gestionarEntrada(input);
            } catch (Exception e) {
                System.err.println("S'HA PRODUÏT UN ERROR: " + e.getMessage());
                // e.printStackTrace(); // Descomentar per a més detalls
            }
        }
        in.close();
        System.out.println("Tancant el driver. Adéu!");
    }

    private static void init() {
        in = new Scanner(System.in);
        ca = new CtrlAnalisi();
        dadesActuals = new ArrayList<>(); // Inicialitzem la llista
    }

    private static void mostra_metodes() {
        System.out.println("\n--- Menú CtrlAnalisi ---");
        System.out.println("(1) Crear Dades de Mostra");
        System.out.println("--- Proves ---");
        System.out.println("(10) Executar KMeans");
        System.out.println("(11) Executar KMeans avançat)");
        System.out.println("(12) Executar generic");
        System.out.println("(13) Calcular Silhouette");
        System.out.println("------------------------");
        System.out.println("(0|sortir) - Tancar driver");
        System.out.print("Escull una opció: ");
    }

    private static void gestionarEntrada(String input) throws Exception {
        switch (input) {
            case "1":
            case "Crear Dades de Mostra":
                crearDadesMostra();
                break;
            case "10":
            case "Executar KMeans":
                testExecutarKMeansSimple();
                break;
            case "11":
            case "Executar KMeans avançat":
                testExecutarKMeansAvançat();
                break;
            case "12":
            case "Executar generic":
                testExecutarGeneric();
                break;
            case "13":
            case "Calcular Silhouette":
                testSilhouette();
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

    private static void testExecutarKMeansSimple() {
        if (dadesActuals.isEmpty()) {
            System.out.println("Primer has de crear dades (opció 1).");
            return;
        }
        System.out.println("Introdueix el número de clusters (k): ");
        int k = Integer.parseInt(in.nextLine());

        int[] labels = ca.executarKMeans(k, dadesActuals);
        
        System.out.println("Execució simple completada. Etiquetes (clusters) assignades:");
        imprimirLabels(labels);
    }

    private static void testExecutarKMeansAvançat() {
        if (dadesActuals.isEmpty()) {
            System.out.println("Primer has de crear dades (opció 1).");
            return;
        }
        System.out.println("Introdueix el número de clusters (k): ");
        int k = Integer.parseInt(in.nextLine());
        System.out.println("Vols fer servir K-means++ (true/false): ");
        boolean useKpp = Boolean.parseBoolean(in.nextLine());
        System.out.println("Distància (EUCLIDEAN o MANHATTAN): ");
        KMeans.Distance dist = KMeans.Distance.valueOf(in.nextLine().toUpperCase());
        System.out.println("Introdueix el seed (llavor, un número llarg): ");
        long seed = Long.parseLong(in.nextLine());

        int[] labels = ca.executarKMeans(k, dadesActuals, useKpp, dist, seed);

        System.out.println("Execució avançada completada. Etiquetes (clusters) assignades:");
        imprimirLabels(labels);
    }

    private static void testExecutarGeneric() {
        if (dadesActuals.isEmpty()) {
            System.out.println("Primer has de crear dades (opció 1).");
            return;
        }
        System.out.print("Introdueix el número de clusters (k) per l'algorisme stub: ");
        int k = Integer.parseInt(in.nextLine());
        int dim = dadesActuals.get(0).length;
        
        // Creem un stub de KMeans (que és un ClusteringAlgorithm) per passar-lo
        ClusteringAlgorithm algorismeStub = new KMeans(k, dim);
        
        System.out.print("Vols fer servir K-means++ (true/false): ");
        boolean useKpp = Boolean.parseBoolean(in.nextLine());

        int[] labels = ca.executar(algorismeStub, dadesActuals, useKpp);
        
        System.out.println("Execució genèrica completada. Etiquetes (clusters) assignades:");
        imprimirLabels(labels);
    }

    private static void testSilhouette() {
        if (dadesActuals.isEmpty()) {
            System.out.println("Primer has de crear dades (opció 1) i executar un KMeans (opció 10 o 11).");
            return;
        }
        
        // El mètode silhouette depèn de les dades i de l'últim 'kmeans' guardat a CtrlAnalisi
        double score = ca.silhouette(dadesActuals);
        
        if (Double.isNaN(score)) {
            System.out.println("ERROR: No s'ha pogut calcular. Has executat un KMeans abans (opció 10, 11 o 12)?");
        } else {
            System.out.println("Puntuació Silhouette (simulada) de l'última execució: " + score);
        }
    }

    // --- Mètodes Auxiliars ---

    /**
     * Mètode auxiliar per crear una llista de dades aleatòries.
     * Guarda el resultat a la variable estàtica 'dadesActuals'.
     */
    private static void crearDadesMostra() {
        dadesActuals.clear(); // Neteja dades anteriors
        System.out.println("Quants punts de dades vols crear? ");
        int n = Integer.parseInt(in.nextLine());
        System.out.println("Quina dimensió (quants valors per punt)? ");
        int dim = Integer.parseInt(in.nextLine());
        
        Random r = new Random();
        for (int i = 0; i < n; i++) {
            double[] punt = new double[dim];
            for (int j = 0; j < dim; j++) {
                punt[j] = r.nextDouble() * 100; // Valors aleatoris 0-100
            }
            dadesActuals.add(punt);
        }
        System.out.println("S'han creat " + n + " dades de dimensió " + dim + ".");
    }

    /**
     * Mètode auxiliar per imprimir l'array d'etiquetes.
     */
    private static void imprimirLabels(int[] labels) {
        if (labels == null || labels.length == 0) {
            System.out.println("[]");
        } else {
            System.out.println(Arrays.toString(labels));
        }
    }
}
package edu.upc.prop.clusterxx.persistencia;

import edu.upc.prop.clusterxx.domini.classes.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import static org.junit.Assert.*;

import java.io.File;
import java.util.HashMap;

/**
 * Tests per verificar el correcte funcionament de la capa de persistència.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPersistencia {

    private static final String DATA_DIR = "dades";
    private static final String ENQUESTES_DIR = DATA_DIR + "/enquestes";
    private static final String USUARIS_DIR = DATA_DIR + "/usuaris";
    private static final String PERFILS_DIR = DATA_DIR + "/perfils";

    private static void netejarDirectori(File dir) {
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        netejarDirectori(f);
                    }
                    f.delete();
                }
            }
        }
    }

    // ===========================================
    //              TESTS USUARIS
    // ===========================================

    @Test
    public void testGuardarICarregarUsuari() throws Exception {
        GestorUsuaris gestor = new GestorUsuaris();
        
        // Crear usuari
        Usuari usuari = new Usuari("testuser", "password123");
        HashMap<String, Usuari> usuaris = new HashMap<>();
        usuaris.put(usuari.getUsername(), usuari);
        
        // Guardar
        gestor.guardarUsuaris(usuaris);
        
        // Verificar que el fitxer d'índex existeix
        File fitxerIndex = new File(USUARIS_DIR + "/index.json");
        assertTrue("El fitxer index.json d'usuaris hauria d'existir", fitxerIndex.exists());
        
        // Verificar que el fitxer individual existeix
        File fitxerUsuari = new File(USUARIS_DIR + "/testuser.json");
        assertTrue("El fitxer testuser.json hauria d'existir", fitxerUsuari.exists());
        
        // Carregar
        HashMap<String, Usuari> usuarisCarregats = gestor.carregarUsuaris();
        
        // Verificar
        assertEquals("Hauria d'haver 1 usuari", 1, usuarisCarregats.size());
        assertTrue("Hauria de contenir 'testuser'", usuarisCarregats.containsKey("testuser"));
    }

    @Test
    public void testGuardarMultiplesUsuaris() throws Exception {
        GestorUsuaris gestor = new GestorUsuaris();
        
        HashMap<String, Usuari> usuaris = new HashMap<>();
        usuaris.put("user1", new Usuari("user1", "pass1"));
        usuaris.put("user2", new Usuari("user2", "pass2"));
        usuaris.put("user3", new Usuari("user3", "pass3"));
        
        gestor.guardarUsuaris(usuaris);
        HashMap<String, Usuari> carregats = gestor.carregarUsuaris();
        
        assertEquals("Haurien d'haver 3 usuaris", 3, carregats.size());
        
        // Verificar fitxers individuals
        assertTrue("Hauria d'existir user1.json", new File(USUARIS_DIR + "/user1.json").exists());
        assertTrue("Hauria d'existir user2.json", new File(USUARIS_DIR + "/user2.json").exists());
        assertTrue("Hauria d'existir user3.json", new File(USUARIS_DIR + "/user3.json").exists());
    }

    // ===========================================
    //              TESTS ENQUESTES
    // ===========================================

    @Test
    public void testGuardarEnquestaBuida() throws Exception {
        GestorEnquestes gestor = new GestorEnquestes();
        
        // Crear usuari creador
        Usuari creador = new Usuari("creador", "pass");
        
        // Crear enquesta sense preguntes
        Enquesta enquesta = new Enquesta("enq1", "Enquesta Test", "Descripció de prova", creador);
        
        // Guardar
        gestor.guardarEnquesta(enquesta);
        
        // Verificar fitxer individual
        File fitxer = new File(ENQUESTES_DIR + "/enq1/enquesta.json");
        assertTrue("El fitxer enq1/enquesta.json hauria d'existir", fitxer.exists());
    }

    @Test
    public void testGuardarEnquestaAmbPreguntes() throws Exception {
        GestorEnquestes gestor = new GestorEnquestes();
        GestorUsuaris gestorUsuaris = new GestorUsuaris();
        
        // Crear usuari creador i guardar-lo
        Usuari creador = new Usuari("creador2", "pass");
        HashMap<String, Usuari> usuaris = new HashMap<>();
        usuaris.put(creador.getUsername(), creador);
        gestorUsuaris.guardarUsuaris(usuaris);
        
        // Crear enquesta amb pregunta
        Enquesta enquesta = new Enquesta("enq2", "Enquesta Completa", "Amb preguntes", creador);
        Pregunta pregunta = new Pregunta("preg1", "Com valores el servei?");
        enquesta.afegirPregunta(pregunta);
        
        // Guardar
        HashMap<String, Enquesta> enquestes = new HashMap<>();
        enquestes.put(enquesta.getId(), enquesta);
        gestor.guardarEnquestes(enquestes);
        gestor.guardarEnquesta(enquesta);
        
        // Carregar
        HashMap<String, Usuari> usuarisCarregats = gestorUsuaris.carregarUsuaris();
        HashMap<String, Enquesta> carregades = gestor.carregarEnquestes(usuarisCarregats);
        
        // Verificar
        assertTrue("Hauria de contenir 'enq2'", carregades.containsKey("enq2"));
        Enquesta carregada = carregades.get("enq2");
        assertEquals("Enquesta Completa", carregada.getTitol());
        assertEquals("Hauria de tenir 1 pregunta", 1, carregada.getPreguntes().size());
    }

    @Test
    public void testEliminarEnquesta() throws Exception {
        GestorEnquestes gestor = new GestorEnquestes();
        Usuari creador = new Usuari("creador3", "pass");
        
        // Crear i guardar
        Enquesta enquesta = new Enquesta("enq_eliminar", "Per eliminar", "Test", creador);
        gestor.guardarEnquesta(enquesta);
        
        File dirEnquesta = new File(ENQUESTES_DIR + "/enq_eliminar");
        assertTrue("El directori hauria d'existir abans d'eliminar", dirEnquesta.exists());
        
        // Eliminar
        gestor.eliminarFitxerEnquesta("enq_eliminar");
        
        assertFalse("El directori NO hauria d'existir després d'eliminar", dirEnquesta.exists());
    }

    // ===========================================
    //              TESTS PERFILS
    // ===========================================

    @Test
    public void testGuardarICarregarPerfils() throws Exception {
        GestorPerfils gestor = new GestorPerfils();
        
        HashMap<String, Perfil> perfils = new HashMap<>();
        perfils.put("1", new Perfil(1, "Perfil A"));
        perfils.put("2", new Perfil(2, "Perfil B"));
        
        gestor.guardarPerfils(perfils);
        
        // Verificar fitxer índex
        File fitxerIndex = new File(PERFILS_DIR + "/index.json");
        assertTrue("El fitxer index.json de perfils hauria d'existir", fitxerIndex.exists());
        
        // Verificar fitxers individuals
        assertTrue("Hauria d'existir 1.json", new File(PERFILS_DIR + "/1.json").exists());
        assertTrue("Hauria d'existir 2.json", new File(PERFILS_DIR + "/2.json").exists());
        
        HashMap<String, Perfil> carregats = gestor.carregarPerfils();
        assertEquals("Haurien d'haver 2 perfils", 2, carregats.size());
    }

    // ===========================================
    //         TESTS CTRLPERSISTENCIA
    // ===========================================

    @Test
    public void testCtrlPersistenciaSingleton() {
        CtrlPersistencia ctrl1 = CtrlPersistencia.getInstance();
        CtrlPersistencia ctrl2 = CtrlPersistencia.getInstance();
        
        assertSame("Hauria de ser la mateixa instància (Singleton)", ctrl1, ctrl2);
    }

    @Test
    public void testCtrlPersistenciaAfegirUsuari() {
        CtrlPersistencia ctrl = CtrlPersistencia.getInstance();
        
        Usuari usuari = new Usuari("ctrl_test_user", "mypass");
        ctrl.afegirUsuari(usuari);
        
        Usuari recuperat = ctrl.getUsuari("ctrl_test_user");
        assertNotNull("L'usuari hauria d'existir", recuperat);
    }

    @Test
    public void testCtrlPersistenciaAfegirEnquesta() {
        CtrlPersistencia ctrl = CtrlPersistencia.getInstance();
        
        Usuari creador = new Usuari("ctrl_creador", "pass");
        ctrl.afegirUsuari(creador);
        
        Enquesta enquesta = new Enquesta("ctrl_enq_test", "Test Ctrl", "Prova", creador);
        ctrl.afegirEnquesta(enquesta);
        
        Enquesta recuperada = ctrl.getEnquesta("ctrl_enq_test");
        assertNotNull("L'enquesta hauria d'existir", recuperada);
        assertEquals("Test Ctrl", recuperada.getTitol());
    }

    @Test
    public void testPersistenciaSobreviuReinicio() throws Exception {
        // Primer: guardar dades
        GestorUsuaris gestor = new GestorUsuaris();
        HashMap<String, Usuari> usuaris = new HashMap<>();
        usuaris.put("persistent_user", new Usuari("persistent_user", "secret"));
        gestor.guardarUsuaris(usuaris);
        
        // Simular "reinici": crear nou gestor i carregar
        GestorUsuaris gestorNou = new GestorUsuaris();
        HashMap<String, Usuari> carregats = gestorNou.carregarUsuaris();
        
        assertTrue("L'usuari hauria de persistir després de 'reiniciar'", carregats.containsKey("persistent_user"));
    }
}

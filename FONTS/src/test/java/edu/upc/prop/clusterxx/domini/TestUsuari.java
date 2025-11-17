package edu.upc.prop.clusterxx.domini;

import edu.upc.prop.clusterxx.domini.classes.Usuari;
import edu.upc.prop.clusterxx.domini.classes.Enquesta;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

/**
 * Tests de la classe Usuari.
 */
public class TestUsuari {
    /**
     * Reseteja l'estat dels usuaris abans de cada test.
     */
    @Before
    public void resetUsuari() {
        Usuari.logout();
    }
    
    /**
     * Tests de la constructora de la classe Usuari.
     */
    @Test 
    public void testConstructoraUsuari() {
        Usuari u = new Usuari("usuari", "contrasenya");
        assertEquals("usuari", u.getUsername());
        assertTrue(u.checkPassword("contrasenya"));
        assertFalse(u.checkPassword(null));
        assertFalse(u.checkPassword("incorrecta"));
    }

    /**
     * Tests dels mètodes d'afegir i eliminar enquestes d'un usuari.
     */
    public void testAfegirEliminarEnquestaUsuari() {
        Usuari u = new Usuari("usuari", "contrasenya");
        assertEquals(0, u.getEnquestesCreades().size());

        Enquesta e = new Enquesta("E1", "Títol E1","Descripció E1",u);
        u.addEnquestaCreada(e);
        assertEquals(1, u.getEnquestesCreades().size());
        assertTrue(u.getEnquestesCreades().contains(e));

        u.removeEnquestaCreada(e);
        assertEquals(0, u.getEnquestesCreades().size());
    }

    /**
     * Tests d'obtenció i modificar de la llista enquestes d'un usuari no modifica les enquesta pròpies de l'usuari.
     */
    @Test
    public void testModificarEnquestesUsuari() {
        Usuari u = new Usuari("usuari", "contrasenya");
        Enquesta e1 = new Enquesta("E1", "Títol E1","Descripció E1",u);
        Enquesta e2 = new Enquesta("E2", "Títol E2","Descripció E2",u);
        u.addEnquestaCreada(e1);

        // Obtenir la llista d'enquestes creades
        List<Enquesta> enquestes = u.getEnquestesCreades();
        assertEquals(1, enquestes.size());
        assertTrue(enquestes.contains(e1));

        // Modificar la llista obtinguda
        enquestes.add(e2);

        // Comprovar que la llista interna de l'usuari no ha canviat
        List<Enquesta> enquestesDespres = u.getEnquestesCreades();
        assertEquals(1, enquestesDespres.size());
        assertFalse(enquestesDespres.contains(e2));
    }   

    /**
     * Tests del login d'un usuari.
     */
    @Test
    public void testLoginUsuari() {
        Usuari nouUsuari = new Usuari("nouUsuari", "novaContrasenya");
        assertNull(Usuari.getUsuariActual());

        Usuari.login(nouUsuari);
        Usuari actual = Usuari.getUsuariActual();
        assertNotNull(actual);
        assertEquals("nouUsuari", actual.getUsername());
    }

    /**
     * Tests de checkPassword.
     */
    @Test
    public void testCheckPassword() {
        Usuari usuari = new Usuari("usuariValid", "contrasenyaCorrecta");
        
        assertTrue(usuari.checkPassword("contrasenyaCorrecta"));
        assertFalse(usuari.checkPassword("contrasenyaIncorrecta"));
    }

    /**
     * Tests del login amb diversos usuaris.
     */
    @Test
    public void testLoginDiversosUsuaris() {
        Usuari usuari1 = new Usuari("usuari1", "contrasenya1");
        Usuari usuari2 = new Usuari("usuari2", "contrasenya2");

        Usuari.login(usuari1);
        assertNotNull(Usuari.getUsuariActual());
        assertEquals("usuari1", Usuari.getUsuariActual().getUsername());

        Usuari.login(usuari2);
        assertNotNull(Usuari.getUsuariActual());
        assertEquals("usuari2", Usuari.getUsuariActual().getUsername());
    }
    
}

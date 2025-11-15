package edu.upc.prop.clusterxx.domini;

import edu.upc.prop.clusterxx.domini.classes.Pregunta;
import edu.upc.prop.clusterxx.domini.classes.Opcio;
import edu.upc.prop.clusterxx.domini.classes.TipusPregunta;
import static org.junit.Assert.*;
import org.junit.Test;
import java.util.List;

/**
 * Tests de la classe Pregunta.
 */
public class TestPregunta {   
    /*  ===========================================
            TESTS CONSTRUCTORES CLASSE PREGUNTA
        =========================================== */

    /**
     * Tests de la constructora de Pregunta de tipus text lliure.
     */
    @Test
    public void testConstructoraTextLliure() {
        Pregunta p = new Pregunta("P1", "Com et dius?");
        assertEquals("P1", p.getId());
        assertEquals("Com et dius?", p.getText());
        assertEquals(TipusPregunta.TEXT_LLIURE, p.getTipus());
        assertTrue(p.getOpcions().isEmpty());
    }

    /**
     * Tests de la constructora de Pregunta de tipus numèrica amb màxim.
     */
    @Test
    public void testConstructoraNumerica() {
        Pregunta p = new Pregunta("P2", "Quants dies treballes per setmana?", 0.0, 7.0);
        assertEquals("P2", p.getId());
        assertEquals("Quants dies treballes per setmana?", p.getText());
        assertEquals(TipusPregunta.NUMERICA, p.getTipus());
        assertEquals(Double.valueOf(0.0), p.getValorMinim());
        assertEquals(Double.valueOf(7.0), p.getValorMaxim());
        assertTrue(p.getOpcions().isEmpty());
    }
    
    /**
     * Tests de la constructora de Pregunta de tipus qualitativa ordenada.
     */
    @Test
    public void testConstructoraQualitativaOrdenada() {
        Pregunta p = new Pregunta("P3", "Quant de temps al dia creus que dediques al mòbil?", TipusPregunta.QUALITATIVA_ORDENADA, 1);
        assertEquals("P3", p.getId());
        assertEquals("Quant de temps al dia creus que dediques al mòbil?", p.getText());
        assertEquals(TipusPregunta.QUALITATIVA_ORDENADA, p.getTipus());
        assertEquals(1, p.getMaxSeleccions());
        assertTrue(p.getOpcions().isEmpty());
    }

    /**
     * Tests de la constructora de Pregunta de tipus qualitativa no ordenada simple.
     */
    @Test
    public void testConstructoraQualitativaNoOrdenadaSimple() {
        Pregunta p = new Pregunta("P4", "Quin és el teu color preferit?", TipusPregunta.QUALITATIVA_NO_ORDENADA_SIMPLE, 1);
        assertEquals("P4", p.getId());
        assertEquals("Quin és el teu color preferit?", p.getText());
        assertEquals(TipusPregunta.QUALITATIVA_NO_ORDENADA_SIMPLE, p.getTipus());
        assertEquals(1, p.getMaxSeleccions());
        assertTrue(p.getOpcions().isEmpty());
    }

    /** Tests de la constructora de Pregunta de tipus qualitativa ordenada múltiple */
    @Test
    public void testConstructoraQualitativaNoOrdenadaMultiple() {
        Pregunta p = new Pregunta("P5", "Quins idiomes parles?", TipusPregunta.QUALITATIVA_NO_ORDENADA_MULTIPLE, 3);
        assertEquals("P5", p.getId());
        assertEquals("Quins idiomes parles?", p.getText());
        assertEquals(TipusPregunta.QUALITATIVA_NO_ORDENADA_MULTIPLE, p.getTipus());
        assertEquals(3, p.getMaxSeleccions());
        assertTrue(p.getOpcions().isEmpty());
    }
    
    /** 
     * Comprova que la constructora amb String tradueix tots els àlies al TipusPregunta correcte.
     */
    @Test
    public void constructorAmbString_mappingTipus() {
        assertEquals(TipusPregunta.NUMERICA, new Pregunta("id","t","numerica").getTipus());
        assertEquals(TipusPregunta.QUALITATIVA_ORDENADA, new Pregunta("id","t","ordenada").getTipus());
        assertEquals(TipusPregunta.QUALITATIVA_ORDENADA, new Pregunta("id","t","qualitativa_ordenada").getTipus());
        assertEquals(TipusPregunta.QUALITATIVA_NO_ORDENADA_SIMPLE, new Pregunta("id","t","qualitativa_simple").getTipus());
        assertEquals(TipusPregunta.QUALITATIVA_NO_ORDENADA_MULTIPLE, new Pregunta("id","t","qualitativa_multiple").getTipus());
        assertEquals(TipusPregunta.TEXT_LLIURE, new Pregunta("id","t","text").getTipus());
        assertEquals(TipusPregunta.TEXT_LLIURE, new Pregunta("id","t","desconegut").getTipus());
    }

    /*  ===========================================
          TESTS SETTERS i GETTERS CLASSE PREGUNTA
        =========================================== */
    /** 
     * Tests del setter i getter del text de la Pregunta.
     */
    @Test
    public void testSetGetTextPregunta() {
        Pregunta p = new Pregunta("P6", "Quin era el text d'aquesta pregunta?");
        p.setText("Quin és el nou text d'aquesta pregunta?");
        assertEquals("Quin és el nou text d'aquesta pregunta?", p.getText());
    }

    /**
     * Test del setter i getter del màxim de seleccions per a preguntes qualitatives.
     */
    @Test
    public void testSetGetMaxSeleccions() {
        Pregunta p = new Pregunta("P7", "Quants dies fas esport per setmana?", TipusPregunta.QUALITATIVA_NO_ORDENADA_MULTIPLE, 2);
        assertEquals(2, p.getMaxSeleccions());
        p.setMaxSeleccions(4);
        assertEquals(4, p.getMaxSeleccions());
    }

    /**
     * Tests del setter i getter del rang numèric d'una pregunta numèrica.
     */
    @Test
    public void testSetGetRangNum() {
        Pregunta p = new Pregunta("P8", "Quina és la teva alçada en cm?", 150.0, 220.0);
        assertEquals(Double.valueOf(150.0), p.getValorMinim());
        assertEquals(Double.valueOf(220.0), p.getValorMaxim());
        // Modificar els valors mínim i màxim
        p.setRangNumeric(160.0,210.0);
        assertEquals(Double.valueOf(160.0), p.getValorMinim());
        assertEquals(Double.valueOf(210.0), p.getValorMaxim());
    }

    /**
     * Tests del setter del rang numèric per comprovar que no afecta a preguntes no numèriques.
     */
    @Test
    public void testSetRangNumNoNumerica() {
        Pregunta p = new Pregunta("P9", "Quin és el teu menjar preferit?", TipusPregunta.QUALITATIVA_NO_ORDENADA_SIMPLE, 1);
        p.setRangNumeric(10.0, 20.0); // No hauria de tenir efecte
        assertNull(p.getValorMinim());
        assertNull(p.getValorMaxim());
    }

    /*  ===========================================
               TESTS OPCIONS CLASSE PREGUNTA
        =========================================== */
    
    /**
     * Tests del mètode d'afegir opcions a una pregunta qualitativa no ordenada simple.
     */
    @Test
    public void testAfegirOpcionsPreguntaQualitativa() {
        Pregunta p = new Pregunta("P10", "Quin és el teu esport preferit?", TipusPregunta.QUALITATIVA_NO_ORDENADA_SIMPLE, 1);
        Opcio o1 = new Opcio(1, "Futbol");
        Opcio o2 = new Opcio(2, "Bàsquet");
        p.afegirOpcio(o1);
        p.afegirOpcio(o2);
        List<Opcio> opcions = p.getOpcions();
        assertEquals(2, opcions.size());
        assertTrue(opcions.contains(o1));
        assertTrue(opcions.contains(o2));
    }

    /**
     * Tests del mètode d'afegir una opció en una pregunta no qualitativa.
     */
    @Test 
    public void testAfegirOpcioPreguntaNoQualitativa() {
        Pregunta p = new Pregunta("P11", "Quants dies a la setmana mires la televisió?", 0.0, 7.0); // Pregunta numèrica
        Opcio o1 = new Opcio(1, "Opció invàlida");
        p.afegirOpcio(o1); // No hauria de tenir efecte
        List<Opcio> opcions = p.getOpcions();
        assertTrue(opcions.isEmpty());
    }

    /**
     * Tests del mètode d'eliminar opcions d'una pregunta qualitativa.
     */
    @Test
    public void testEliminarOpcionsPreguntaQualitativa() {
        Pregunta p = new Pregunta("P12", "Quin tipus de truita de patates prefereixes?", TipusPregunta.QUALITATIVA_NO_ORDENADA_SIMPLE, 1);
        Opcio o1 = new Opcio(1, "Amb ceba");
        Opcio o2 = new Opcio(2, "Sense Ceba");
        p.afegirOpcio(o1);
        p.afegirOpcio(o2);
        p.eliminarOpcio(1); // Eliminar l'opció "Amb ceba"
        List<Opcio> opcions = p.getOpcions();
        assertEquals(1, opcions.size());
        assertFalse(opcions.contains(o1));
        assertTrue(opcions.contains(o2));
    }

    /**
     * Eliminar una opció que no existeix no ha d'afectar la llista actual.
     */
    @Test
    public void testEliminarOpcioInexistentNoCanvia() {
        Pregunta p = new Pregunta("P12b", "Preferències", TipusPregunta.QUALITATIVA_NO_ORDENADA_SIMPLE, 1);
        Opcio o1 = new Opcio(1, "Opció real");
        p.afegirOpcio(o1);

        p.eliminarOpcio(999);

        assertEquals(1, p.getOpcions().size());
        assertTrue(p.getOpcions().contains(o1));
    }

    /**
     * Tests de retornar null si opció no existeix.
     */
    @Test
    public void testGetOpcioInexistent() {
        Pregunta p = new Pregunta("P13", "Quin és el teu animal preferit?", TipusPregunta.QUALITATIVA_NO_ORDENADA_SIMPLE, 1);
        Opcio o = p.getOpcio(87 ); // ID inexistent
        assertNull(o);
    }

    /**
     * Tests de modificar llista opcions no afecta a les opcions de la pregunta en si.
     */
    @Test
    public void testGetOpcionsNoModifica() {
        Pregunta p = new Pregunta("P14", "Quin és el teu plat preferit?", TipusPregunta.QUALITATIVA_NO_ORDENADA_SIMPLE, 1);
        Opcio o1 = new Opcio(1, "Pasta a la carbionara.");
        p.afegirOpcio(o1);
        List<Opcio> opcions = p.getOpcions();
        opcions.clear(); // Modificar la llista retornada
        List<Opcio> opcionsDespres = p.getOpcions();
        assertEquals(1, opcionsDespres.size()); // Hauria de seguir tenint l'opció original
        assertTrue(opcionsDespres.contains(o1));
    }

    /**
     * Tests del tipus d'opcions que admet cada pregunta.
     */
    @Test
    public void testTipusOpcions() {
        Pregunta p1 = new Pregunta("P15", "Pregunta numèrica?", 0.0, 10.0);
        assertFalse(p1.tipusAdmetOpcions());

        Pregunta p2 = new Pregunta("P16", "Pregunta qualitativa ordenada?", TipusPregunta.QUALITATIVA_ORDENADA, 1);
        assertTrue(p2.tipusAdmetOpcions());

        Pregunta p3 = new Pregunta("P17", "Pregunta qualitativa no ordenada simple?", TipusPregunta.QUALITATIVA_NO_ORDENADA_SIMPLE, 1);
        assertTrue(p3.tipusAdmetOpcions());

        Pregunta p4 = new Pregunta("P18", "Pregunta qualitativa no ordenada múltiple?", TipusPregunta.QUALITATIVA_NO_ORDENADA_MULTIPLE, 2);
        assertTrue(p4.tipusAdmetOpcions());

        Pregunta p5 = new Pregunta("P19", "Pregunta text lliure?");
        assertFalse(p5.tipusAdmetOpcions());
    }

       /*  ===========================================
               TESTS RESPOSTES CLASSE PREGUNTA
        =========================================== */

    /**
     * Tests de validació de respostes per a preguntes numèriques.
     */
    @Test
    public void testValidarRespostesNumerica() {
        Pregunta p = new Pregunta("P20", "Respon amb un número entre el 0 i el 10", 0.0, 10.0);
        assertTrue(p.validarResposta("5"));
        assertTrue(p.validarResposta("0"));
        assertTrue(p.validarResposta("10"));
        assertFalse(p.validarResposta("-5")); // Fora de rang
        assertFalse(p.validarResposta("130")); // Fora de rang
        assertFalse(p.validarResposta("No és un número")); // No numèric
    }

    /**
     * Sense rang numèric definit qualsevol valor parsejable ha de ser vàlid.
     */
    @Test
    public void testValidarRespostaNumericaSenseRang() {
        Pregunta p = new Pregunta("P20b", "Sense límits", null, null);
        assertTrue(p.validarResposta("-999"));
        assertTrue(p.validarResposta("999"));
    }

    /**
     * Tests de validació de respostes per a preguntes qualitatives simples.
     */
    @Test
    public void testValidarRespostesQualitativa() {
        Pregunta p = new Pregunta("P21", "Quin és el teu mes de vacances preferit?", TipusPregunta.QUALITATIVA_NO_ORDENADA_SIMPLE, 1);
        Opcio o1 = new Opcio(1, "Agost");
        Opcio o2 = new Opcio(2, "Desembre");
        p.afegirOpcio(o1);
        p.afegirOpcio(o2);
        assertTrue(p.validarResposta("Agost"));     // Opció vàlida
        assertTrue(p.validarResposta("Desembre"));  // Opció vàlida
        assertFalse(p.validarResposta("Dijous"));   // Opció invàlida
        assertFalse(p.validarResposta("Dimarts"));  // Opció invàlida
    }

    /**
     * Tests de validació de respostes per a preguntes qualitatives múltiples.
     */
    @Test
    public void testValidarRespostesQualitativaMultiple() {
        Pregunta p = new Pregunta("P5", "Quins idiomes parles?", TipusPregunta.QUALITATIVA_NO_ORDENADA_MULTIPLE, 3);
        Opcio o1 = new Opcio(1, "Anglès");
        Opcio o2 = new Opcio(2, "Espanyol");
        Opcio o3 = new Opcio(3, "Francès");
        p.afegirOpcio(o1);
        p.afegirOpcio(o2);
        p.afegirOpcio(o3);
        assertTrue(p.validarResposta("Anglès,Espanyol"));           // Dues opcions vàlides
        assertTrue(p.validarResposta("Francès"));                  // Una opció vàlida
        assertFalse(p.validarResposta("Anglès,Alemany"));          // Una opció invàlida
        assertFalse(p.validarResposta("Anglès,Espanyol,Francès,Alemany")); // Més opcions de les permeses
    }

    /**
     * Les respostes múltiples han de ser tolerants amb espais extra i majúscules/minúscules.
     */
    @Test
    public void testValidarRespostaQualitativaMultipleAmbEspais() {
        Pregunta p = new Pregunta("P5b", "Quins idiomes parles?", TipusPregunta.QUALITATIVA_NO_ORDENADA_MULTIPLE, 3);
        p.afegirOpcio(new Opcio(1, "Anglès"));
        p.afegirOpcio(new Opcio(2, "Francès"));

        assertTrue(p.validarResposta(" Anglès , Francès "));
    }

    /**
     * Tests de validació de respostes per a preguntes de text lliure.
     */
    @Test
    public void testValidarRespostaTextLliure() {
        Pregunta p = new Pregunta("P22", "Explica'ns la teva opinió sobre el medi ambient.");
        assertTrue(p.validarResposta("Crec que és molt important cuidar el nostre planeta."));
        assertTrue(p.validarResposta("El medi ambient és una responsabilitat de tots."));
        assertFalse(p.validarResposta("")); 
        assertFalse(p.validarResposta(null));
    }

          /*  ===========================================
               TESTS ToString CLASSE PREGUNTA
        =========================================== */

    /**
     * Test del mètode toString pels diferents tipus de preguntes.
     */
    @Test
    public void testToStringTextLliure() {
        Pregunta p = new Pregunta("P23", "Què penses sobre la intel·ligència artificial?");
        String esperat = "Pregunta{id='P23', text='Què penses sobre la intel·ligència artificial?', tipus=TEXT_LLIURE}";
        assertEquals(esperat, p.toString());
    }

    /**
     * Les preguntes numèriques han d'incloure el rang dins del toString.
     */
    @Test
    public void testToStringNumerica() {
        Pregunta p = new Pregunta("P24", "Quantes mascotes has tingut al llarg de la teva vida?", 0.0, 50.0);
        String esperat = "Pregunta{id='P24', text='Quantes mascotes has tingut al llarg de la teva vida?', tipus=NUMERICA, rang=[0.0-50.0]}";
        assertEquals(esperat, p.toString());
    }

    /**
     * Les preguntes qualitatives simples han de mostrar el recompte d'opcions.
     */
    @Test 
    public void testToStringQualitativaSimple() {
        Pregunta p = new Pregunta("P25", "Quina modalitat de treball prefereixes?", TipusPregunta.QUALITATIVA_NO_ORDENADA_SIMPLE, 1);
        Opcio o1 = new Opcio(1, "Presencial");
        Opcio o2 = new Opcio(2, "Remot");
        p.afegirOpcio(o1);
        p.afegirOpcio(o2);
        String expected = "Pregunta{id='P25', text='Quina modalitat de treball prefereixes?', tipus=QUALITATIVA_NO_ORDENADA_SIMPLE, opcions=2}";
        assertEquals(expected, p.toString());
    }

    /**
     * En les preguntes múltiples també s'ha d'imprimir el màxim de seleccions permès.
     */
    @Test 
    public void testToStringQualitativaMúltiple() {
        Pregunta p = new Pregunta("P26", "Tria més d'una opció.", TipusPregunta.QUALITATIVA_NO_ORDENADA_MULTIPLE, 3);
        Opcio o1 = new Opcio(1, "Opció 1");
        Opcio o2 = new Opcio(2, "Opció 2");
        Opcio o3 = new Opcio(3, "Opció 3");
        p.afegirOpcio(o1);
        p.afegirOpcio(o2);
        p.afegirOpcio(o3);
        String expected = "Pregunta{id='P26', text='Tria més d'una opció.', tipus=QUALITATIVA_NO_ORDENADA_MULTIPLE, opcions=3, max_seleccions=3}";
        assertEquals(expected, p.toString());
    }

}


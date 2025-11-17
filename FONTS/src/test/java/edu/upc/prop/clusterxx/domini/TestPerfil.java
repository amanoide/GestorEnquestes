package edu.upc.prop.clusterxx.domini;

import edu.upc.prop.clusterxx.domini.classes.Perfil;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Arrays;

/**
 * Tests de la classe Perfil.
 */
public class TestPerfil {
	/**
	 * Crea un perfil complet amb els paràmetres més utilitzats durant els tests.
	 * @param silhouette valor de la silhouette a utilitzar.
	 * @return Perfil inicialitzat amb dades de clustering.
	 */
	private Perfil crearPerfilComplet(Double silhouette) {
		return new Perfil(10,"Perfil complet","E1",1,"Cluster A",25,silhouette,new String[]{"Resposta 1", "Resposta 2"},Arrays.asList("Pregunta 1", "Pregunta 2"),"KMeans++"
		);
	}

	/**
	 * Verifica que la constructora bàsica assigna l'identificador i la descripció
	 * i no marca el perfil com a clustering.
	 */
	@Test
	public void testConstructoraBasica() {
		Perfil p = new Perfil(1, "Perfil senzill");
		assertEquals("Perfil{id=1, descripcion='Perfil senzill'}", p.toString());
		assertFalse(p.teClustering());
		assertEquals("Perfil sense dades de clustering", p.getPerfilLlegible());
	}

	/**
	 * Verifica que la constructora completa estableix totes les dades i teClustering és cert.
	 */
	@Test
	public void testConstructoraCompleta() {
		Perfil p = crearPerfilComplet(0.63);
		assertTrue(p.teClustering());
		String llegible = p.getPerfilLlegible();
		assertTrue(llegible.contains("Cluster 2"));
		assertTrue(llegible.contains("Enquesta: E1"));
		assertTrue(llegible.contains("Algoritme: KMeans++"));
		assertTrue(llegible.contains("Mida del cluster: 25 membres"));
		assertTrue(llegible.contains("Pregunta 1: Resposta 1"));
	}

	/**
	 * Cas límit: vector característic nul implica que no hi ha dades de clustering.
	 */
	@Test
	public void testTeClusteringSenseVector() {
		Perfil p = new Perfil(2, "Desc", "E2", 0, "Cluster B", 15,0.5, null, Arrays.asList("Pregunta"), "KMeans");
		assertFalse(p.teClustering());
		assertEquals("Perfil sense dades de clustering", p.getPerfilLlegible());
	}

	/**
	 * Cas límit: clusterIndex nul implica que tampoc hi ha dades de clustering.
	 */
	@Test
	public void testTeClusteringSenseIndex() {
		Perfil p = new Perfil(3, "Desc", "E3", null, "Cluster C", 8,0.45, new String[]{"A"}, Arrays.asList("Pregunta"), "KMeans");
		assertFalse(p.teClustering());
	}

	/**
	 * El mètode toString ha d'incloure dades del cluster quan existeixen.
	 */
	@Test
	public void testToStringAmbClustering() {
		Perfil p = new Perfil(99, "Desc", "E9", 2, "Exploradors", 5,0.75, new String[]{"Alta"}, Arrays.asList("Participació"), "KMeans");
		String expected = "Perfil{id=99, cluster=Exploradors, mida=5, silhouette=" + String.format("%.3f", 0.75) + "}";
		assertEquals(expected, p.toString());
	}

	/**
	 * Quan només hi ha una resposta, getPerfilLlegible no ha d'intentar llegir més preguntes de les disponibles.
	 */
	@Test
	public void testPerfilLlegibleTruncat() {
		Perfil p = new Perfil(5, "Desc", "E5", 0, "Cluster D", 3,0.3, new String[]{"Resposta única"}, Arrays.asList("Pregunta 1", "Pregunta 2"), "KMedoids");
		String llegible = p.getPerfilLlegible();
		assertTrue(llegible.contains("Pregunta 1: Resposta única"));
		assertFalse(llegible.contains("Pregunta 2"));
	}

	/**
	 * Silhouette nul s'ha de descriure com a "Desconeguda".
	 */
	@Test
	public void testQualitatSilhouetteDesconeguda() {
		Perfil p = new Perfil(6, "Sense dades");
		assertEquals("Desconeguda", p.getQualitatText());
	}

	/**
	 * Qualitats per sobre de 0.7 s'han d'etiquetar com a excel·lents.
	 */
	@Test
	public void testQualitatSilhouetteExcelent() {
		assertEquals("Excel·lent", crearPerfilComplet(0.75).getQualitatText());
	}

	/**
	 * Valors intermedis alts (0.5) s'han de catalogar com a bons.
	 */
	@Test
	public void testQualitatSilhouetteBona() {
		assertEquals("Bona", crearPerfilComplet(0.55).getQualitatText());
	}

	/**
	 * Rangos propers a 0.3 han de retornar "Acceptable".
	 */
	@Test
	public void testQualitatSilhouetteAcceptable() {
		assertEquals("Acceptable", crearPerfilComplet(0.3).getQualitatText());
	}

	/**
	 * Valors lleugerament positius però baixos són "Pobra".
	 */
	@Test
	public void testQualitatSilhouettePobra() {
		assertEquals("Pobra", crearPerfilComplet(0.1).getQualitatText());
	}

	/**
	 * Resultats negatius s'han de marcar com a "Molt pobra".
	 */
	@Test
	public void testQualitatSilhouetteMoltPobra() {
		assertEquals("Molt pobra", crearPerfilComplet(-0.2).getQualitatText());
	}

}

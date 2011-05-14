/**
 * Mit dieser Klasse kann ein Objekt Level erstellt werden,
 * das alle Informationen eines Levels fuer das Spiel "Kistenschieber" speichert.
 * In einem Objekt wird gespeichert:
 * 		Welche Abmessungen hat das Level
 * 		Welche Felder sind immer an der selben Stelle
 * 		Auf welchen Feldern stehen anfangs die Kisten
 * 		Auf welchem Feld steht anfangs die Figur
 * 
 * In dem Datenfeld valid wird gespeichert, ob alle Angaben gueltig waren
 * und bei Fehlern wird eine Beschreibung gespeichert.
 * 
 * @author Nicolai Ommer
 * @version 21.10.08
 */
public class Level
{	
	//Level-spezifische Daten speichern
	private int levelbreite,levelhoehe;
	private char[][] festesFeld, kistenPos, punktPos, figPos;
	private Icons icons;
	private LevelInfos infos;
	private int anzKisten;
	
	//Datenfeld, das angibt, ob alles korrekt ist
	private boolean valid = false;
	
	/**
	 * Konstruktor, der ein neues Level definiert und folgende Werte verlangt:
	 * 
	 * @param levelbreite	Die Anzahl der Felder in der Waagerechten
	 * @param levelhoehe	Die Anzahl der Felder in der Senkrechten
	 * @param felder		2D-Array aller Felder des Spielfeldes
	 */
	public Level(	int levelbreite,
					int levelhoehe,
					char[][] figPos,
					char[][] festesFeld,
					char[][] kistenPos,
					char[][] punktPos,
					Icons icons,
					LevelInfos infos)
	{
		this.levelbreite = levelbreite;
		this.levelhoehe = levelhoehe;
		this.figPos = figPos;
		this.festesFeld = festesFeld;
		this.kistenPos = kistenPos;
		this.punktPos = punktPos;
		this.icons = icons;
		this.infos = infos;
		
		if(pruefePunkteKisten()) {
			valid = true;			
		}
	}
	
	/**
	 * Ueberpruefe, ob im angegebenen 2D-Array die Anzahl der Kisten mit der Anzahl der Punkte uebereinstimmt.
	 * @return		Anzahl Kisten = Anzahl Punkte?
	 */
	private boolean pruefePunkteKisten()
	{
		int kisten = 0;
		int punkte = 0;
		
		for(int y = 0 ; y < levelhoehe ; y = y + 1) {
		    for(int x = 0 ; x < levelbreite ; x = x + 1) {
		    	if(kistenPos[x][y] == '$'
		    	|| kistenPos[x][y] == '*') {
		    		kisten = kisten + 1;
		    	}
		    	if(punktPos[x][y] == '.'
		    	|| punktPos[x][y] == '+'
		    	|| punktPos[x][y] == '*') {
		    		punkte = punkte + 1;
		    	}
		    	
		    }
		}
		fehlerAusgeben("Es gibt " + kisten + " Kisten.");
		fehlerAusgeben("Es gibt " + punkte + " Punkte.");
		anzKisten=kisten;
		if(kisten == punkte) {
			fehlerAusgeben("Anzahl Kisten und Punkte stimmen ueberein.");
			return true;
		}
		else {
	       	fehlerAusgeben("Die Anzahl der Kisten und der Punkte stimmen nicht ueberein.");
			return false;
		}
	}
	
	/**
	 * Erstelle den gueltigen Validatecode aus den Daten des Levels.
	 * Der Code ist nicht zurueckverfolgbar!
	 * 
	 * @return erzeugter Validatecode
	 */
	public String getValidateCode()
	{
		String code = new String("");
		code = 	Spiel.getNewCountSystem(levelbreite,5) +
				Spiel.getNewCountSystem(levelhoehe,11) +
				Spiel.getNewCountSystem(Integer.valueOf( infos.getInfo("anzahlWaende") ),20) +
				Spiel.getNewCountSystem(Integer.valueOf( infos.getInfo("anzahlKisten") ),4) +
				Spiel.getNewCountSystem(Integer.valueOf( infos.getInfo("anzahlBoeden") ),12) +
				Spiel.getNewCountSystem(Integer.valueOf( infos.getInfo("anzahlPunkte") ),8);
		return code;
	}
	
	/**
	 * Liefert true zurueck, wenn das Level gueltig ist, also alle Angaben zur Erstellung
	 * des Levels korrekt waren.
	 * 
	 * @return	gueltig oder nicht als boolean
	 */
	public boolean isvalid()
	{
		return valid;
	}
	
	/**
	 * Gib die Startposition der Figur zurueck.
	 * @return Figur-Startposition
	 */
	public char[][] getFigPos()
	{
		return figPos;
	}
	
	/**
	 * Gib dir Startpoistionen der Kisten zurueck.
	 * @return Kisten-Startpositionen
	 */
	public char[][] getKistenPos()
	{
		return kistenPos;
	}
	
	/**
	 * Gib die festen Felder (Wand,Boden,Punkt) zurueck.
	 * @return die festen Felder des Levels
	 */
	public char[][] getFestesFeld()
	{
		return festesFeld;
	}
	
	/**
	 * Gib die Positionen der Punkte zurueck.
	 * @return Poistionen der Punkte
	 */
	public char[][] getPunktPos()
	{
		return punktPos;
	}
	
	/**
	 * Gib die maximale Anzahl der nebeneinander liegenden Felder zurueck.
	 * @return  maximale Anzahl der nebeneinander liegenden Felder
	 */
	public int getlevelbreite()
	{
		return levelbreite;
	}
	
	/**
	 * Gib die maximale Anzahl der uebereinander liegenden Felder zurueck.
	 * @return  maximale Anzahl der uebereinander liegenden Felder
	 */
	public int getlevelhoehe()
	{
		return levelhoehe;
	}
	
	/**
	 * In icons sind die zu benutzenden Bilder fuer das Spielfeld enthalten.
	 * 
	 * @return zu benutzende Icons
	 */
	public Icons getIcons()
	{
		return icons;
	}
	
	/**
	 * Anzahl der vorhanden Kisten im Level
	 * 
	 * @return Anzahl Kisten
	 */
    public int getAnzKisten() {
		return anzKisten;
	}

	/**
     * Gib den Wert des Info-Schluessels zurueck.
     * 	Beispiel: infoname=version -> "1.0"
     * 
     * @param infoname Schluesselname der gewuenschten Info
     * @return Wert der Information
     */
    public String getInfos(String infoname)
    {
    	return infos.getInfo(infoname);
    }
	
	/**
	 * Wenn DEBUGMODUS=true, dann werden Nachrichten zur Kontrolle des Quellcodes ausgegeben.
	 * 
	 * @param text	Der Text, der ausgegeben werden soll.
	 */
	private void fehlerAusgeben(String text)
	{
		if(Spiel.DEBUGMODUS) {
			System.out.println("Level: " + text);
		}
	}
	
}

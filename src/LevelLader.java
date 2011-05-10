import java.io.*;
import java.util.ArrayList;

/**
 * Oeffnet eine geforderte Datei und liesst die Informationen daraus aus.
 * Wenn alle Angaben richtig ausgewertet werden konnten, wird ein neues Level-Objekt erzeugt und zurueckgeliefert.
 * 
 * @author Nicolai Ommer
 * @version	20.10.08
 */
public class LevelLader
{
	private int 		levelbreite,
						levelhoehe;
	private char[][] 	festeFelder,
						kistenPos,
						punktPos,
						figPos;
	private Icons 		icons;
	private LevelInfos 	infos; // Lege Standards fuer Infos fest und lade Infos aus Leveldatei
	private boolean 	valid = true; // pruefe, ob alles in Ordnung ist

	/**
	 * Lege Standards fuer die Levelinfos fest, falls keine Infos im Level verfuegbar sind.
	 * Rufe alle Leveldaten ab und erstelle, wenn valid, das neue Level und liefere es zurueck.
	 * 
	 * @param level	Dateiname des Levels, das geladen werden muss (inkl. ".lev" Endung)
	 * @return vollstaendiges Level
	 */
	public Level getLevel(String level)
	{
		this.icons = new Icons("31");
		this.infos = new LevelInfos();
		this.infos.setInfo("autor", "unknown");
		this.infos.setInfo("titel", "unknown");
		this.infos.setInfo("version", "unknown");
		this.infos.setInfo("discription", "no discription available");
		
		getleveldaten(new File("level/" + level));
		
		if(valid) {
			fehlerAusgeben("Level wird jetzt uebergeben.");
			return new Level(levelbreite,levelhoehe,figPos,festeFelder,kistenPos,punktPos,icons,infos);
		}
		else {
			fehlerAusgeben("Level war nicht gueltig, wird also auch nicht uebergeben.");
			return null;
		}	
	}
	
	/**
	 * Lese alle Informationen aus angegebener Datei heraus.
	 * Filtere dabei alle Zeilen aus, die mit "//" beginnen.
	 * Zeilen, die mit "#" beginnen, stellen das Spielfeld da.
	 * 	'#'=Wand
	 * 	' '=Boden
	 * 	'.'=Punkt
	 * 	'$'=Kiste
	 * 	'@'=Figur
	 * 	'*'=Kiste auf Punkt
	 * 	'+'=Figur auf Punkt
	 * 
	 * Zeilen, die ein "=" enthalten werden folgendermassen ausgewertet:
	 * 	vor "=": Schluessel
	 * 	nach "=": Wert
	 * Diese Infos werden alle gespeichert, auch wenn nicht auf alle zugegriffen wird. 
	 * 
	 * @param file		Level-Datei, in der die Informationen ueber das Level gespeichert sind.
	 */
	private void getleveldaten(File file)
	{
		String zeile,infoname,inhalt;
		ArrayList<String> leveldaten = new ArrayList<String>();
		try {			
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			zeile = reader.readLine();
			while(zeile != null) {
				if(!zeile.startsWith("//") && !zeile.equals("")) {
					if(zeile.contains("=")) {
						infoname = zeile.split("=")[0];
						if(zeile.split("=").length > 1) {
							inhalt = zeile.split("=")[1];
						}
						else {
							inhalt = "";
						}
						infos.setInfo(infoname, inhalt);
						fehlerAusgeben(infoname + " gefunden: " + inhalt);
						
						if(infoname.equals("dir")) {
							if(inhalt.length() > 0) {
								fehlerAusgeben("Dir wird geladen.");
								icons.load(inhalt);
							}
						}
					}
					else if(zeile.startsWith("#")) {
						leveldaten.add(zeile);
						fehlerAusgeben(zeile);
					}
					else {
						fehlerAusgeben("Kann Zeile nicht zuordnen.");
					}
				}
				zeile = reader.readLine();
			}
			this.ermittleLevelAbmasse(leveldaten);
			this.ermittleFelder(leveldaten);
			infos.setInfo("dateiname", file.getName());
		}
		catch(FileNotFoundException e) {
			fehlerAusgeben("Das Level " + file.getName() + " konnte nicht gefunden werden.");
			valid = false;
		}
		catch(IOException e) {
			fehlerAusgeben("Das Einlesen oder Schliessen funktionierte nicht.");
			valid = false;
		}
	}
	
	/**
	 * Ermittle die Hoehe und Breite des Spielfeldes. Gehe von der Zeile aus, die am laengsten ist,
	 * um spaeter die leeren Felder mit Boden zu fuellen.
	 * 
	 * @param zeilen		Die Zeilen aus der Level-Datei
	 */
	private void ermittleLevelAbmasse(ArrayList<String> zeilen)
	{
		levelbreite = 0;
		for(int i = 0;i < zeilen.size();i = i + 1) {
			if(zeilen.get(i).length() > levelbreite) {
				levelbreite = zeilen.get(i).length(); 
			}
		}
		levelhoehe = zeilen.size();
		fehlerAusgeben("levelbreite " + levelbreite + " und levelhoehe " + levelhoehe + " ermittelt.");
	}
	
	/**
	 * Suche aus der Level-Datei alle Wand und Punkt Felder heraus und trage sie ein.
	 * Fuelle den Rest mit Boden Feldern
	 * 
	 * @param zeilen		Die Zeilen aus der Level-Datei
	 */
	private void ermittleFelder(ArrayList<String> zeilen)
	{
		String punktmeldung = new String("");
		String kistenmeldung = new String("");
		int anzahlWaende = 0;
		int anzahlBoeden = 0;
		int anzahlKisten = 0;
		int anzahlPunkte = 0;
		festeFelder = new char[levelbreite][levelhoehe];
		kistenPos = new char[levelbreite][levelhoehe];
		punktPos = new char[levelbreite][levelhoehe];
		figPos = new char[levelbreite][levelhoehe];
		boolean figPosExistiert = false;
		
		//Alle Felder mit Boden ausfuellen
		for(int y = 0; y < levelhoehe ; y = y + 1) {
			for(int x = 0; x < levelbreite ; x = x + 1) {
				festeFelder[x][y] = ' ';
				anzahlBoeden += 1;
			}
		}
		
		for(int y = 0 ; y < levelhoehe ; y = y + 1) {
			for(int x = 0 ; x < zeilen.get(y).length() ; x = x + 1) {
				//Wand
				if(zeilen.get(y).substring(x, x + 1).equals("#")) {
					festeFelder[x][y] = '#';
					anzahlWaende += 1;
				}
				//Punkt
				else if(zeilen.get(y).substring(x, x + 1).equals(".")) {
					festeFelder[x][y] = '.';
					punktPos[x][y] = '.';
					punktmeldung = punktmeldung + " (" + x + "|" + y + ")";
					anzahlPunkte += 1;
				}
				//Kiste
				else if(zeilen.get(y).substring(x, x + 1).equals("$")) {
					kistenPos[x][y] = '$';
					kistenmeldung = kistenmeldung + " (" + x + "|" + y + ")";
					anzahlKisten += 1;
				}
				//Kiste-Punkt
				else if(zeilen.get(y).substring(x, x + 1).equals("*")) {
					kistenPos[x][y] = '*';
					punktPos[x][y] = '*';
					festeFelder[x][y] = '.';
					punktmeldung = punktmeldung + " (" + x + "|" + y + ")";
					kistenmeldung = kistenmeldung + " (" + x + "|" + y + ")";
					anzahlKisten += 1;
					anzahlPunkte += 1;
				}
				//Spielfigur
				else if(zeilen.get(y).substring(x, x + 1).equals("@")) {
					figPos[x][y] = '@';
					figPosExistiert = true;
				}
				//Spielfigur-Punkt
				else if(zeilen.get(y).substring(x, x + 1).equals("+")) {
					figPos[x][y] = '+';
					punktPos[x][y] = '+';
					festeFelder[x][y] = '.';
					punktmeldung = punktmeldung + " (" + x + "|" + y + ")";
					anzahlPunkte += 1;
					figPosExistiert = true;
				}
			}
		}
		
		fehlerAusgeben("Es wurden " + anzahlWaende + " Waende geladen.");
		fehlerAusgeben("Folgende Punkte geladen:" + punktmeldung);
		fehlerAusgeben("Folgende Kisten geladen:" + kistenmeldung);
		
		infos.setInfo("anzahlWaende", "" + anzahlWaende);
		infos.setInfo("anzahlPunkte", "" + anzahlPunkte);
		infos.setInfo("anzahlKisten", "" + anzahlKisten);
		
		if(!figPosExistiert) {
			fehlerAusgeben("FigPos konnte nicht geladen werden.");
			valid = false;
		}
		
		infos.setInfo("anzahlBoeden", "" + anzahlBoeden);
		fehlerAusgeben("Es wurden " + anzahlBoeden + " Boeden geladen.");
	}
	
	/**
	 * Wenn DEBUGMODUS=true, dann werden Nachrichten zur Kontrolle des Quellcodes ausgegeben.
	 * 
	 * @param text	Der Text, der ausgegeben werden soll.
	 */
	private void fehlerAusgeben(String text)
	{
		if(Spiel.DEBUGMODUS) {
			System.out.println("LevelLader: " + text);
		}
	}
}


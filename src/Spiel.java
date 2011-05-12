import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

import javax.swing.*;

/**
 * Dies ist die Hauptklasse des Spiels "Kistenschieber".
 * Die main-Methode erstellt ein Objekt von dieser Klasse.
 * In ihr werden alle Ereignisse ueberwacht: Menueeintraege und Tastatur-Tasten.
 * 
 * Bei der Erstellung wird eine neue Oberflaeche, ein neues Spielfeld und ein neuer Levelchooser erzeugt.
 * Diese Klasse implementiert einen KeyListener, der der Oberflaeche hinzugefuegt wird,
 * um Tastatur-Ereignisse abzuhoeren.
 * Die Menueeintraege werden beim erstellen dieser Klasse erstellt und der Oberflaeche hinzugefuegt.
 * Die ActionListener sind als interne Klassen definiert.
 * 
 * Die Klasse regelt das Bewegen der Figur und verwaltet auch alle Informationen ueber Level und Spiel.
 * Sie ruft die Informationen ueber ein Level aus der Level-Klasse ab und erstellt den vollstaendigen
 * Validatecode, der sich aus dem Code aus der Level-Klasse und einem neuen Code, der sich von den Schritten ableitet,
 * zusammensetzt.
 * Ausserdem werden hier die Schritte gezaehlt.
 * 
 * @author Nicolai Ommer
 * @version 24.10.08
 */
public class Spiel implements KeyListener
{
	// DEBUGMODUS: wenn true, dann werden Ausgaben zur leichteren Fehlerbehandlung ausgegeben
	public static final boolean DEBUGMODUS=true;
	// VERSION: die aktuelle Version, die in der Spiel-Info angezeigt wird.
	public static final String VERSION = new String("0.9");
	public static final String TITEL = new String("Kistenschieber");
	private Oberflaeche 		oberf; // Fenster, auf dem die Spielflaeche angezeigt wird
	private Spielfeld 			spielfeld; // aktuelle Sammlung aller Felder
	private LevelChooser 		levelchooser; // lade ein Level aus einer Datei
	private LoesungZeigen		loesungZeigen; // Dialog zur Steuerung der Loesungsvorschau
	private Bot 				bot; // KI zum automatischen loesen
	private int 				schritte; // zaehle Schritte zur aktuellen Anzeige und zum speichern bei Gewinn
	private boolean				tastenSperren; // legt fest, ob man die Figur bewegen darf oder nicht
	/**
	 * Speichere vor jedem Schritt die Position der Figur
	 * und merke, von wo nach wo eine Kiste verschoben wurde 
	 */
	private ArrayList<Point> 	schrittSpeicherAlteKiste;
	private ArrayList<Point> 	schrittSpeicherNeueKiste;
	private ArrayList<Point> 	schrittSpeicherFigur;
	
	/**
	 * Initialisiere Das Spiel.
	 * Erstelle eine neue Oberflaeche, ein neues Spielfeld und einen neuen Levelchooser.
	 * Initialisiere die restlichen Datenfelder.
	 * Fuege der Oberflaeche diese Klasse als KeyListener hinzu.
	 * Fuege ihr alle Menueeintraege ein.
	 * Hole vom Levelchooser das naechste nicht geloeste Level.
	 * Mache die Oberflaeche sichtbar.
	 */
	public Spiel()
	{
		loesungZeigen = new LoesungZeigen(this);
		oberf = new Oberflaeche(loesungZeigen);
		spielfeld = new Spielfeld();
		levelchooser = new LevelChooser(oberf);
		schrittSpeicherAlteKiste = new ArrayList<Point>();
		schrittSpeicherNeueKiste = new ArrayList<Point>();
		schrittSpeicherFigur = new ArrayList<Point>();
		bot = new Bot(this,spielfeld);
		tastenSperren = false;
		
		oberf.addKeyListener(this);
		this.menueEintraege();
		
		ladeLevel(levelchooser.getNextLevel());
		
		oberf.setVisible(true);
	}
	
	/**
	 * Lade das aktuell geladene Level erneut.
	 */
	public void reloadLevel()
	{
		ladeLevel(levelchooser.getAktuellesLevel());
		tastenSperren = false;
	}
		
	/**
	 * Pruefe, ob die Figur sich in die angegebene Richtung bewegen darf.
	 * Wenn auf dem neuen Feld eine Kiste steht, pruefe, ob die Kiste ein Feld weiter verschoben werden kann.
	 * Speichere vor einer Veraenderung die Position der Figur
	 * und speichere bei einer Verschiebung einer Kiste die neue und alte Position.
	 * Gespeichert wird in eine ArrayList, in der fuer jeden Schritt die Positionen gespeichert sind.
	 * Veraendere das Spielfeld entsprechend und aktualisiere es.
	 * Pruefe zum Schluss, ob alle Kisten einen Punkt bedecken.
	 * 
	 * Die Parameter geben an, in welche Richtung sich die Figur bewegen soll.
	 * Dabei wird eine positive oder negative Zahl(in der Regel 1 fuer ein Feld) angegeben.
	 * 
	 * @param waag	-1 fuer links, 1 fuer rechts
	 * @param senk	-1 fuer hoch, 1 fuer runter
	 */
	public boolean bewegen(int waag, int senk)
	{
		boolean moved=false;
		if(spielfeld.valid()) {
			Point figPos = spielfeld.getFigPos();
			Point neuePos = new Point(figPos.x + waag,figPos.y + senk);
			
			fehlerAusgeben("figPos("+figPos.x+"|"+figPos.y+"),"+
					   	"neuePos("+neuePos.x+"|"+neuePos.y+")");
			
			if(freiesFeld(neuePos,false)) {
				fehlerAusgeben("neuePos ist frei");
				char neuesFeld = spielfeld.getFeld(neuePos);
				
				if(neuesFeld == '*'
				|| neuesFeld == '$') {
					fehlerAusgeben("Neues Feld enthaelt Kiste.");
					Point neuePos2 = new Point(neuePos.x + waag,neuePos.y + senk);
					if(freiesFeld(neuePos2,true)) {
						fehlerAusgeben("Kiste kann zum naechsten Feld verschoben werden.");
						
						schrittSpeicherAlteKiste.add(neuePos);
						schrittSpeicherNeueKiste.add(neuePos2);
						schrittSpeicherFigur.add(neuePos);
						
						spielfeld.verschiebeKiste(neuePos,neuePos2);
						spielfeld.verschiebeFigPos(figPos,neuePos);
						schritte = schritte + 1;
						moved=true;
					}
				}
				else if(neuesFeld == ' '
					|| 	neuesFeld == '.') {
					fehlerAusgeben("Neues Feld ist leer, also Figur verschieben.");
					
					schrittSpeicherAlteKiste.add(null);
					schrittSpeicherNeueKiste.add(null);
					schrittSpeicherFigur.add(neuePos);
					
					spielfeld.verschiebeFigPos(figPos,neuePos);
					schritte = schritte + 1;
					moved=true;
					
				}
				else {
					fehlerAusgeben("Irgendwas stimmt nicht in bewegen()");
				}
			}
			oberf.reloadSpielfeld(spielfeld.build(),levelchooser.getAktuellesLevel(),schritte);
			if(spielfeld.pruefeObGewonnen()) {
				this.gewonnen();
			}
		}
		return moved;
	}

	/**
	 * Pruefe, ob das angegebene Feld frei ist oder nicht.
	 * 
	 * 
	 * @param neuePos		das Feld, das geprueft werden soll als Point
	 * @param auchKisten	pruefe auch, ob das Feld keine Kiste enthaelt
	 * @return
	 */
	private boolean freiesFeld(Point neuePos, boolean auchKisten)
	{
		if(spielfeld.getLevel().getlevelbreite() > neuePos.x
		&& spielfeld.getLevel().getlevelhoehe() > neuePos.y
		&& neuePos.x >= 0
		&& neuePos.y >= 0) {
			char neuesFeld = spielfeld.getFeld(neuePos);
			
			if(neuesFeld == ' '
			|| neuesFeld == '.') {
				fehlerAusgeben("Feld ("+neuePos.x+"|"+neuePos.y+") ist frei.");
				return true;
			}
			else if(neuesFeld == '$' || neuesFeld == '*') {
				fehlerAusgeben("Feld ("+neuePos.x+"|"+neuePos.y+") ist Kiste.");
				if(!auchKisten) {
					return true;
				}
			}
			else {
				fehlerAusgeben("Bewegen nicht moeglich. Kein begehbares Feld.");
			}
		}
		else {
			fehlerAusgeben("Spielfeldrand erreicht. Bewegen nicht mehr moeglich.");
		}
		return false;
	}
	
	/**
	 * Erstelle alle Menueeintraege fuer die Oberflaeche und weise sie einem Listener zu.
	 * Die Listener sind als Sub-Klasse in dieser Klasse definiert.
	 */
	private void menueEintraege()
	{
		JMenu mnSpiel = new JMenu("Spiel");
		JMenu mnOptionen = new JMenu("Optionen");
		JMenu mnHilfe = new JMenu("Info");
		
		oberf.menueEintrag("Neu (N)",mnSpiel,new SpielNeuListener());
		oberf.menueEintrag("Oeffnen (O)",mnSpiel,new SpielOeffnenListener());
		oberf.menueEintrag("Rueckgaengig (R)",mnSpiel,new SpielRueckgaengigListener());
		oberf.menueEintrag("Beenden (Q)",mnSpiel,new SpielBeendenListener());
		oberf.menueEintrag("Alle Erfolge zuruecksetzen",mnOptionen,new OptionenErfZurueckListener());
		// Aendern von "Zeige Loesung (L)" muss auch unter ladeLevel geaendert werden!
		oberf.menueEintrag("Zeige Loesung (L)",mnOptionen,new OptionenLoesungzeigen());
		oberf.menueEintrag("Bot / KI (B)",mnOptionen,new OptionenBot());
		oberf.menueEintrag("Spiel",mnHilfe,new InfoSpielListener());
		oberf.menueEintrag("Level",mnHilfe,new InfoLevelListener());
	}
	
	/**
	 * Hier stehen alle Ereignisse, die ausgefuehrt werden sollen, wenn das Level gewonnen wurde.
	 * Es wird ein gueltiger Validatecode in die Level-Datei geschrieben.
	 * Eine Info-Nachricht wird ausgegeben, die dem Spieler gratuliert.
	 * Danach wird das naechste ungeloeste Level geladen und schritte auf 0 zurueckgesetzt.
	 */
	private void gewonnen()
	{
		if(!tastenSperren) {
			fehlerAusgeben("Gewonnen!!");
			
			refreshFile(false,spielfeld.getLevel().getInfos("dateiname"));
			
			JOptionPane.showMessageDialog(
	    			null,
	    			"Sie haben gewonnen! Herzlichen Glueckwunsch",
	    			"Gewonnen",
	    			JOptionPane.OK_OPTION);
			ladeLevel(levelchooser.getNextLevel());
		}
    }
	
	/**
	 * Speichere einen neuen Validatecode in der Leveldatei.
	 * Oeffne zuerst die Datei und lese alles ein.
	 * Ueberpruefe dann, ob bereits ein Eintrag fuer validatecode vorhanden ist.
	 * Erneuere den Code oder fuege ihn neu hinzu.
	 * Schreibe die Aenderungen in die Datei.
	 * 
	 * @param validateCode	der neue ValidateCode, der in die Level-Datei geschrieben werden soll.
	 * @param levelname		der Name des Levels, in dem der Code veraendert werden soll.
	 */
	//private void refreshFile(String validateCode, String levelname)
	private void refreshFile(boolean reset, String levelname)
	{
		ArrayList<String> zeilen = new ArrayList<String>();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader("level/" + levelname));
			
			int x = 0;
			zeilen.add(reader.readLine());
			while(zeilen.get(x) != null) {
				x = x + 1;
				zeilen.add(reader.readLine());
			}
			reader.close();
		}
		catch (FileNotFoundException e) {
			fehlerAusgeben("Datei nicht gefunden.");
		}
		catch(IOException e) {
			fehlerAusgeben("Ein Fehler beim Lesen der Datei trat auf.");
		}
		
		int i = 0;
		boolean codefound = false;
		boolean loesungfound = false;
		int grenze = zeilen.size() - 1; //die letzte Zeile ist immer null, kann also nicht behandelt werden
		
		String validateCode = new String("");
		String alterLoesungsweg = spielfeld.getLevel().getInfos("loesungsweg");
		String loesungsweg = alterLoesungsweg;
		
		if(!reset) {
			validateCode = getFullValidateCode();
			
			loesungsweg = "";
			for(Point figPos : schrittSpeicherFigur) {
				loesungsweg = loesungsweg + figPos.x + "," + figPos.y + ";";
			}
			int alteLaenge = alterLoesungsweg.split(";").length;
			int neueLaenge = loesungsweg.split(";").length;
			if(alteLaenge > 1 && alteLaenge < neueLaenge) {
				loesungsweg = alterLoesungsweg;
			}
		}
		
		while((!codefound || !loesungfound) && i < grenze) {
			if(zeilen.get(i).contains("validatecode=")) {
				zeilen.remove(i);
				zeilen.add(i, "validatecode=" + validateCode);
				codefound = true;
				fehlerAusgeben("Eintrag fuer validatecode gefunden.");
			}
			else if(zeilen.get(i).contains("loesungsweg=")) {
				zeilen.remove(i);
				zeilen.add(i, "loesungsweg=" + loesungsweg);
				loesungfound = true;
				fehlerAusgeben("Eintrag fuer loesungsweg gefunden.");
			}
			i = i + 1;
		}
		if(!codefound) {
			zeilen.add("validatecode=" + validateCode);
			fehlerAusgeben("Eintrag fuer validatecode nicht gefunden. Fuege neuen Eintrag hinzu.");
		}
		if(!loesungfound) {
			zeilen.add("loesungsweg=" + loesungsweg);
			fehlerAusgeben("Eintrag fuer loesungsweg nicht gefunden. Fuege neuen Eintrag hinzu.");
		}
		
		try {
			FileWriter writer = new FileWriter("level/" + levelname);
			
			for(String zeile : zeilen) {
				if(zeile != null) {
					writer.write(zeile + System.getProperty("line.separator"));
					fehlerAusgeben("Schreibe Zeile: " + zeile);
				}
			}
			writer.close();
		}
		catch (IOException e) {
			fehlerAusgeben("Fehler beim schreiben der Datei.");
		}
	}
	
	/**
	 * Setze den validateCode vom Level mit den Schritten zum kompletten Code zusammen.
	 * 
	 * @return vollstaendigen ValidateCode inkl. Schritten
	 */
	private String getFullValidateCode()
	{
		String fullValCode = spielfeld.getLevel().getValidateCode() +
								"A" +
								getNewCountSystem(schritte,4) ;
		return fullValCode;
	}
	
	/**
	 * Ermittle die Anzahl der Schritte, die gebraucht wurden, um das Level zu gewinnen.
	 * Diese stehen verschluesselt in der Level-Datei.
	 * Der Code wird aus den Level-Infos geholt und umgewandelt und anschliessend zurueckgeliefert.
	 * 
	 * @return	Anzahl Schritte, die gebraucht wurden, um das Level zu gewinnen.
	 */
	private int getBesteSchritte()
	{
		String[] code = spielfeld.getLevel().getInfos("validatecode").split("A");
		int ergebnis = 0;
		int ziffer;
		double exp;
		
		if(code.length > 1) {
			for(int i = 0 ; i < code[1].length() ; i = i + 1) {
				ziffer = Integer.valueOf(code[1].substring(i, i + 1));
				exp = (double) code[1].length() - i - 1;
				ergebnis = ergebnis + (int)(Math.pow(4.0, exp)) * ziffer;
			}
		}
		return ergebnis;
	}

	/**
	 * Pruefe, ob aktuelles Level gueltig, sprich loesbar ist.
	 * Level ist gueltig, wenn jemand das Level bereits erfolgreich gespielt hat.
	 * Es wird der gespeicherte Code mit einem neu generierten Code verglichen.
	 *  
	 * @return	True, wenn der gespeicherte Code gueltig ist.
	 */
	private boolean isLevelGueltig()
	{
		String[] neuerCode = spielfeld.getLevel().getInfos("validatecode").split("A");
		String[] gueltigerCode = getFullValidateCode().split("A");
		
		if(neuerCode[0].equals(gueltigerCode[0])) {
			return true;
		}
		return false;
	}
	
	/**
	 * Angegebenes Level in die Spielflaeche laden und die Oberflaeche aktualisieren.
	 */
	public void ladeLevel(String levelname)
	{
		spielfeld.load(levelname);
		schritte = 0;
		schrittSpeicherAlteKiste.removeAll(schrittSpeicherAlteKiste);
		schrittSpeicherNeueKiste.removeAll(schrittSpeicherNeueKiste);
		schrittSpeicherFigur.removeAll(schrittSpeicherFigur);
		oberf.reloadSpielfeld(spielfeld.build(),levelchooser.getAktuellesLevel(),schritte);
		schrittSpeicherAlteKiste.add(null);
		schrittSpeicherNeueKiste.add(null);
		schrittSpeicherFigur.add(spielfeld.getFigPos());
		if(spielfeld.getLevel().getInfos("loesungsweg").equals("")) {
			oberf.setMenuItemEnabled("Zeige Loesung (L)", false);
		}
		else {
			oberf.setMenuItemEnabled("Zeige Loesung (L)", true);
		}		
	}
	
	/**
	 * Mache den zuletzt gemachten Schritt rueckgaengig.
	 * Die Methode kann so oft ausgefuehrt werden, bis Schritte == 0 ist.
	 * Die Anzahl der Schritte wird wieder um 1 verringert.
	 * Falls eine Kiste verschoben wurde, wird diese wieder zurueckverschoben.
	 * Die Figur wird auf die vorherige Position zurueckverschoben.
	 * Alle Angaben zu dem Schritt, der rueckgaengig gemacht wurde, werden geloescht.
	 * Das Spielfeld wird aktualisiert.
	 */
	public void schrittRueckgaengig()
	{
		if(schritte > 0) {
			schritte = schritte - 1;
			
			if(schrittSpeicherAlteKiste.get(schrittSpeicherAlteKiste.size() - 1) != null) {
				spielfeld.verschiebeKiste(schrittSpeicherNeueKiste.get(schrittSpeicherNeueKiste.size() - 1), schrittSpeicherAlteKiste.get(schrittSpeicherAlteKiste.size() - 1));
			}
			
			spielfeld.verschiebeFigPos(spielfeld.getFigPos(),schrittSpeicherFigur.get(schritte));
			
			schrittSpeicherAlteKiste.remove(schrittSpeicherAlteKiste.size() - 1);
			schrittSpeicherNeueKiste.remove(schrittSpeicherNeueKiste.size() - 1);
			schrittSpeicherFigur.remove(schrittSpeicherFigur.size() - 1);
			
			oberf.reloadSpielfeld(spielfeld.build(),levelchooser.getAktuellesLevel(),schritte);
			fehlerAusgeben("Schritt zurück");
		}
	}
	
	/**
	 * Wenn ein Loesungsweg existiert, werden alle Eingaben gesperrt und ein neuer Dialog geoeffnet,
	 * in dem man die Figur mit vor und zurueck steuern kann. Die Figur laeuft dann den gespeicherten Weg ab.
	 */
	private void loesungZeigen()
	{
		if(!spielfeld.getLevel().getInfos("loesungsweg").equals("")) {
			Point location;
			tastenSperren = true;
	    	String[] loesungsweg = spielfeld.getLevel().getInfos("loesungsweg").split(";");
	    	Point[] wegPunkte = new Point[loesungsweg.length];
	    	        	
	    	for(int i = 0 ; i < loesungsweg.length ; i = i + 1) {
	    		wegPunkte[i] = new Point(Integer.valueOf(loesungsweg[i].split(",")[0]),Integer.valueOf(loesungsweg[i].split(",")[1]));
	    	}
	    	
	    	location = new Point(oberf.getLocation().x, oberf.getLocation().y + oberf.getHeight());
	    	
	    	loesungZeigen.run(wegPunkte,location);
		}
	}
	
	/**
	 * Setze das Datenfeld, das bestimmt, ob Tastatureingaben erlaubt sind.
	 * @param bool ob Tasten gesperrt sind oder nicht
	 */
	public void setTastenSperren(boolean bool)
	{
		tastenSperren = bool;
	}
	
	/**
     * Beobachte den Menue-Eintrag Datei->Neu und fuere entsprechende Actionen aus.
     */
    class SpielNeuListener implements ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
            fehlerAusgeben("Menue: Neu");
            reloadLevel();
        }
    }
    
    /**
     * Beobachte den Menue-Eintrag Datei->Oeffnen und fuere entsprechende Actionen aus.
     */
    class SpielOeffnenListener implements ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
        	fehlerAusgeben("Menue: Oeffnen");
        	
        	ladeLevel(levelchooser.getNewLevel());
        }
    }
    
    /**
     * Beobachte den Menue-Eintrag Datei->Rueckgaengig und fuere entsprechende Actionen aus.
     */
    class SpielRueckgaengigListener implements ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
        	fehlerAusgeben("Menue: Rueckgaengig");
        	schrittRueckgaengig();
        }
    }
    
    /**
     * Beobachte den Menue-Eintrag Datei->Exit und fuere entsprechende Actionen aus.
     */
    class SpielBeendenListener implements ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
        	fehlerAusgeben("Exit");
            System.exit(0);
        }
    }
    
    /**
     * Beobachte den Menue-Eintrag Optionen->Erfolge zuruecksetzen und fuere entsprechende Actionen aus.
     * Wenn der Dialog mit Abbrechen beantwortet wird, wird nichts getan, sonst loescht er in jeder Level-Datei
     * im Ordner "level/" den Validatecode.
     */
    class OptionenErfZurueckListener implements ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
        	fehlerAusgeben("Menue: Optionen->Erfolge zuruecksetzen");
        	int value = JOptionPane.showOptionDialog(
        			null,
        			"Dies setzt den Erfolg jedes Levels im level-Ordner zurueck.\n" +
        			"Danach wird automatisch wieder beim ersten Level angefangen.\n" +
        			"Sind sie sicher?",
        			"Erfolge zuruecksetzen",
        			JOptionPane.OK_CANCEL_OPTION,
        			JOptionPane.WARNING_MESSAGE,
        			null,
        			null,
        			null);
        	fehlerAusgeben("Value: " + value);
        	if(value == 0) {
        		File dir = new File("level/");
        		File[] files = dir.listFiles();
        		for(File file : files) {
        			if(file.getName().endsWith(".lev")) {
        				fehlerAusgeben("Level " + file.getName() + " gefunden.");
        				refreshFile(true,file.getName());
        			}
        		}        		
        	}
        }
    }
    
    /**
     * Beobachte den Menue-Eintrag Optionen->Zeige Loesung und fuere entsprechende Actionen aus.
     * Sollte das Level schon einmal geloest worden sein, so kann der genutzte Loesungsweg gezeigt werden.
     */
    class OptionenLoesungzeigen implements ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
        	fehlerAusgeben("Menue: Optionen->Zeige Loesung");
        	loesungZeigen();
        }
    }    
    
    /**
     * Beobachte den Menue-Eintrag Optionen->Bot / KI und fuere entsprechende Aktionen aus.
     * Das Level wird mit hilfe einer KI automatisch geloest.
     */
    class OptionenBot implements ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
        	fehlerAusgeben("Menue: Optionen->Bot");
        	activateBot();
        }
    }  
    
    /**
     * Beobachte den Menue-Eintrag Info->Spiel und fuere entsprechende Actionen aus.
     */
    class InfoSpielListener implements ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
        	fehlerAusgeben("Menue: Info->Spiel");
        	JOptionPane.showMessageDialog(
        			null,
        			TITEL + " " + VERSION + "\n" +
        			"Autor: Nicolai Ommer\n\n" +
        			"Ziel des Spieles ist es, alle Kisten auf einen Punkt zu schieben.\n" +
        			"Es ist egal, welche Kiste auf welchem Punkt steht.\n" +
        			"Mit Hilfe des Menues oder den im Menue in Klammern angegebenen Buchstaben " +
        			"koennen entsprechende Ereignisse wie neues Spiel oder Schritt Rueckgaengig ausgefuehrt werden.\n" +
        			"Dies ist ein uraltes Spiel, und dessen urspruenglicher Name ist Sokoban.\n\n" +
        			"Dieses Spiel ist in Form eines Projektes bei einem Praktikum bei der " +
        			"Deutschen Telekom entstanden.",
        			"Spiel-Info",
        			JOptionPane.OK_OPTION);
        }
    }
    
    /**
     * Beobachte den Menue-Eintrag Info->Level und fuere entsprechende Actionen aus.
     */
    class InfoLevelListener implements ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
        	fehlerAusgeben("Menue: Info->Level");
        	JOptionPane.showMessageDialog(
        			null,
        			"Levelname: " + spielfeld.getLevel().getInfos("titel") + "\n"+
        			"Version: " + spielfeld.getLevel().getInfos("version") + "\n" +
        			"Autor: " + spielfeld.getLevel().getInfos("autor") + "\n" +
        			"Schritte: " + getBesteSchritte() + "\n" +
        			"Gueltig: " + isLevelGueltig() + "\n\n" +
        			"Beschreibung: " + spielfeld.getLevel().getInfos("discription") + "\n",
        			"Level-Info",
        			JOptionPane.OK_OPTION);
        }
    }

    /**
	 * Reagiere auf verschiedene Tastatureingaben.
	 * Wenn die Pfeiltasten gedrueckt wurden, wird bewegung ausgefuehrt.
	 * Wenn ein definierter Buchstabe gedrueckt wurde, fuehre entsprechendes Ereignis aus.
	 * 
	 * @param arg0	KeyEvent, das ausgeoest wurde
	 */
	public void keyReleased(KeyEvent arg0)
	{
		if(!tastenSperren) {
			if(arg0.getKeyCode() == KeyEvent.VK_LEFT) {
				System.out.println("left gedrueckt.");
				bewegen(-1,0);
			}
			else if(arg0.getKeyCode() == KeyEvent.VK_RIGHT) {
				System.out.println("right gedrueckt.");
				bewegen(1,0);
			}
			else if(arg0.getKeyCode() == KeyEvent.VK_DOWN) {
				System.out.println("down gedrueckt.");
				bewegen(0,1);
			}
			else if(arg0.getKeyCode() == KeyEvent.VK_UP) {
				System.out.println("up gedrueckt.");
				bewegen(0,-1);
			}
			else if(arg0.getKeyCode() == KeyEvent.VK_N) {
				System.out.println("N gedrueckt.");
				ladeLevel(levelchooser.getAktuellesLevel());
			}
			else if(arg0.getKeyCode() == KeyEvent.VK_O) {
				System.out.println("O gedrueckt.");
				ladeLevel(levelchooser.getNewLevel());
			}
			else if(arg0.getKeyCode() == KeyEvent.VK_Q) {
				System.out.println("Q gedrueckt.");
				System.exit(0);
			}
			else if(arg0.getKeyCode() == KeyEvent.VK_R) {
				System.out.println("R gedrueckt.");
				schrittRueckgaengig();
			}
			else if(arg0.getKeyCode() == KeyEvent.VK_L) {
				System.out.println("L gedrueckt.");
				loesungZeigen();
			}
			else if(arg0.getKeyCode() == KeyEvent.VK_B) {
				System.out.println("B gedrueckt.");
				activateBot();
			}
			else if(arg0.getKeyCode() == KeyEvent.VK_S) {
				System.out.println("S gedrueckt.");
				if(bot.isRunning()) bot.step();
			}
		}
	}
    
    public void activateBot() {
		// TODO
    	if(bot.isRunning()) {
    		bot.stop();
    	}
    	else {
    		tastenSperren = true;
    		bot.start();
    		tastenSperren = false;
    	}
		
	}

	/**
	 * Wandel angegebene Zahl in ein neues Zahlensystem um.
	 * Wenn fuer div eine groessere Zahl als 10 eingegeben wird,
	 * erhaelt man keine gueltigen Zahlen mehr.
	 * Statt mit Buchstaben weiter zu machen, wird hier mit 2-stelligen Zahlen weiter gemacht.
	 * Dies bedeutet, dass die entstehende Zahl nicht wieder umgewandelt werden kann.
	 * 
	 * Beispiel:
	 * 	4(10er)->100(2er)
	 * 
	 * @param zahl	Die Zahl, die umgewandelt werden soll
	 * @param div	Zahlensystem (z.B. 2 fuer Duales Zahlensystem)
	 * @return		Das Ergebnis als String
	 */
	public static String getNewCountSystem(int zahl, int div)
	{
		String ergebnis = new String("");
		int zwischenergebnis = 0;
		
		while(zahl > 0) {
			zwischenergebnis = zahl % div;
			zahl = zahl / div;
			ergebnis = Integer.valueOf(zwischenergebnis) + ergebnis;
		}
		return ergebnis;
	}
    
	@Override
	public void keyPressed(KeyEvent arg0)
	{
	}
	
	@Override
	public void keyTyped(KeyEvent arg0)
	{
	}
	
	/**
	 * Die main-Methode wird zum Programmstart aufgerufen.
	 * Sie erstellt ein neues Objekt vom Typ Spiel.
	 * 
	 * @param args	wird hier nicht benoetigt, da keine parameter uebergeben werden muessen.
	 */
	public static void main(String[] args)
	{
		new Spiel();
	}
	
	/**
	 * Wenn DEBUGMODUS=true, dann werden Nachrichten zur Kontrolle des Quellcodes ausgegeben.
	 * 
	 * @param text	Der Text, der ausgegeben werden soll.
	 */
	private void fehlerAusgeben(String text)
	{
		if(Spiel.DEBUGMODUS) {
			System.out.println("Spiel: " + text);
		}
	}
}

import java.awt.*;
import javax.swing.*;
/**
 * Diese Klasse definiert ein Spielfeld fuer das Spiel "Kistenschieber".
 * Die Groesse des Spielfeldes wird durch die Spielfeldstuecke definiert.
 * Ein Spielfeldstueck ist ein Feld, das die folgenden Zustaende annehmen kann:
 *      Boden,Wand,Kiste,Punkt,Figur
 * 
 * Es wird zuerst ein Level ueber den LevelLader geladen und dessen Daten gespeichert.
 * Ausserdem wird die aktuelle Position der Spielfigur und aller Kisten gespeichert.
 * Bei einer Veraenderung werden diese Elemente auf die neue Position gesetzt und der Rest
 * mit den festen Feldern gefuellt.
 * 
 * blubb
 *  
 * @author Nicolai Ommer 
 * @version 20.10.08
 */
public class Spielfeld
{
    private char[][]	figPos; //Koordinaten der Figur
    private char[][] 	kistenPos; // Positionen der Kisten, null wenn keine Kiste
    private LevelLader 	levelLader; // Objekt, um ein Level einzulesen
    private Level 		level; // Aktuelles Level, hier werden keine Aenderungen gemacht
    private boolean 	valid; // Sagt aus, ob alles Angaben im Level korrekt sind 

    /**
     * Erstelle neuen LevelLader und setze das Level erst einmal auf valid = false.
     */
    public Spielfeld()
    {
    	levelLader = new LevelLader();
    	valid = false;
    }
    
	/**
	 * Lade anhand des Levelnamens(Name der Datei) ein Level aus dem LevelLader
	 * und uebernehme dessen Daten.
	 * 
	 * @param levelname	Dateiname inkl. ".lev" Endung des gewuenschten Levels
	 */
    public boolean load(String levelname)
    {
    	level = levelLader.getLevel(levelname);
        if(level != null && level.isvalid()) {
        	figPos = level.getFigPos();
        	kistenPos = level.getKistenPos();
        	valid = true;
        	fehlerAusgeben("Leveldaten von " + levelname + " wurden geladen.");
        	return true;
        }
        else {
        	fehlerAusgeben(levelname + " ist ungueltig. Nichts zum laden.");
        	valid = false;
        	return false;
        }
    }
    
    /**
     * Erstelle ein neues Spielfeld und fuege ihm zuerst die beweglichen Elemente
     * (Kisten und Figur) hinzu und fuelle die restlichen Felder mit den festen Feldern.
     * 
     * @return		fertiges Spielfeld
     */
    public JPanel build()
    {
    	JPanel spielfeld = new JPanel();
    	if(valid) {
    		spielfeld.setLayout(new GridLayout(level.getlevelhoehe(),level.getlevelbreite()));
    		char[][] alleFelder = getAlleFelder();
    		
    		for(int y = 0 ; y < level.getlevelhoehe() ; y = y + 1) {
    			for(int x = 0 ; x < level.getlevelbreite() ; x = x + 1) {
    				if(alleFelder[x][y] == ' ') {
    					spielfeld.add(new JLabel(level.getIcons().getBoden()));
    				}
    				else if(alleFelder[x][y] == '@') {
    					spielfeld.add(new JLabel(level.getIcons().getFigur()));
    				}
    				else if(alleFelder[x][y] == '+') {
    					spielfeld.add(new JLabel(level.getIcons().getFigurPunkt()));
    				}
    				else if(alleFelder[x][y] == '$') {
    					spielfeld.add(new JLabel(level.getIcons().getKiste()));
    				}
    				else if(alleFelder[x][y] == '*') {
    					spielfeld.add(new JLabel(level.getIcons().getKistePunkt()));
    				}
    				else if(alleFelder[x][y] == '.') {
    					spielfeld.add(new JLabel(level.getIcons().getPunkt()));
    				}
    				else if(alleFelder[x][y] == '#') {
    					spielfeld.add(new JLabel(level.getIcons().getWand()));
    				}
    				else {
    					fehlerAusgeben("Zeichen " + alleFelder[x][y] + " in alleFelder[][] nicht bekannt.");
    				}
    			}
    		}
    	}
    	else {
    		spielfeld.add(new JLabel("Level ist ungueltig!"));
    	}
    	return spielfeld;
    }
    
    /**
     * Verschiebe eine Kiste in dem 2D-Array kistenPos.
     * Loesche zuerst die alte Position und fuege anschliessend die neue Position hinzu.
     * 
     * @param altePos	Die zu loeschende Kiste
     * @param neuePos	Die neue Position der Kiste
     */
    public void verschiebeKiste(Point altePos, Point neuePos)
    {
    	kistenPos[altePos.x][altePos.y] = (char) Character.DIRECTIONALITY_UNDEFINED;
    	if(level.getPunktPos()[neuePos.x][neuePos.y] == '.'
        	|| level.getPunktPos()[neuePos.x][neuePos.y] == '*') {
    		kistenPos[neuePos.x][neuePos.y] = '*';
    	}
    	else {
    		kistenPos[neuePos.x][neuePos.y] = '$';
    	}
    }
    
    /**
     * Verschiebe die Figur in dem 2D-Array figPos.
     * Loesche zuerst die alte Position und fuege anschliessend die neue Position hinzu.
     * 
     * @param altePos	Die zu loeschende Figur
     * @param neuePos	Die neue Position der Figur
     */
    public void verschiebeFigPos(Point altePos, Point neuePos)
    {
    	figPos[altePos.x][altePos.y] = (char) Character.DIRECTIONALITY_UNDEFINED;
    	if(level.getPunktPos()[neuePos.x][neuePos.y] == '.'
    	|| level.getPunktPos()[neuePos.x][neuePos.y] == '*') {
    		figPos[neuePos.x][neuePos.y] = '+';
    	}
    	else {
    		figPos[neuePos.x][neuePos.y] = '@';
    	}
    }
    
    /**
     * Pruefe, ob alle Kisten auf einem Punkt stehen.
     * Wenn ja, wurde das Level gewonnen.
     */
    public boolean pruefeObGewonnen()
    {
    	if(level.getPunktPos().length == kistenPos.length) {
    		if(level.getPunktPos()[0].length == kistenPos[0].length) {
    			for(int x = 0 ; x < kistenPos.length ; x = x + 1) {
    	    		for(int y = 0 ; y < kistenPos[x].length ; y = y + 1) {
    	    			if(getFeld(new Point(x,y)) == '.'
    	    			|| getFeld(new Point(x,y)) == '$') {
    	    				return false;
    	    			}
    	    		}
    	    	}
    		}
    	}
    	return true;
    }
    
    /**
     * Suche das Feld mit den angegebenen Koordinaten und gib es zurueck.
     * 
     * @param p		Koordinate des Feldes, das gesucht wird
     * @return		gefundenes Feld(JLabel)
     */
    public char getFeld(Point p)
    {
    	if(kistenPos[p.x][p.y] == '$' || kistenPos[p.x][p.y] == '*') {
    		return kistenPos[p.x][p.y];
    	}
    	else if(figPos[p.x][p.y] == '@' || figPos[p.x][p.y] == '+') {
    		return figPos[p.x][p.y];
    	}
    	else {
    		return level.getFestesFeld()[p.x][p.y];
    	}
    }
    
    /**
     * Liefere die aktuell dargestellten Felder alle zusammen in einem 2D-Array zurueck.
     * Hier sind auch die Kisten und die FigPos enthalten.
     * 
     * @return 2D-Array mit allen aktuellen Feldern
     */
    public char[][] getAlleFelder()
    {
    	char[][] alleFelder = new char[level.getlevelbreite()][level.getlevelhoehe()];
    	
    	for(int y = 0 ; y < level.getlevelhoehe() ; y = y + 1) {
			for(int x = 0 ; x < level.getlevelbreite() ; x = x + 1) {
				alleFelder[x][y] = getFeld(new Point(x,y));
			}
		}
    	
    	return alleFelder;
    }

    /**
     * Liefere aktuelles Level zurueck.
     * Hierrueber koennen auch alle wichtigen Informationen von dem Level abgefragt werden.
     * @return level
     */
    public Level getLevel()
    {
    	return level;
    }
    
    /**
     * Aktuelle Position der Figur
     * @return figPos
     */
    public Point getFigPos()
    {
    	for(int y = 0 ; y < level.getlevelhoehe() ; y = y + 1) {
			for(int x = 0 ; x < level.getlevelbreite() ; x = x + 1) {
				if(figPos[x][y] == '@' || figPos[x][y] == '+') {
					return new Point(x,y);
				}
			}
    	}
    	return null;
    }
    
    /**
     * Aktuelle Positionen der Kisten
     * @return kistenPos
     */
    public char[][] getKistenPos()
    {
    	return kistenPos;
    }
    
    /**
     * Ist Level gueltig oder nicht?
     * @return valid
     */
    public boolean valid()
    {
    	return valid;
    }
    
    /**
     * Veraendere das 2D-Array der Kisten-Positionen
     * @param kistenPos neues 2D-Array der KistenPos
     */
    public void setKistenPos(char[][] kistenPos)
    {
    	this.kistenPos = kistenPos;
    }
    
    /**
     * Veraendere die Position der Figur
     * @param figPos neue Position der Figur
     */
    public void setFigPos(char[][] figPos)
    {
       	this.figPos = figPos;
    }
    
    /**
	 * Wenn DEBUGMODUS=true, dann werden Nachrichten zur Kontrolle des Quellcodes ausgegeben.
	 * 
	 * @param text	Der Text, der ausgegeben werden soll.
	 */
	private void fehlerAusgeben(String text)
	{
		if(Spiel.DEBUGMODUS) {
			System.out.println("Spielfeld: " + text);
		}
	}
}

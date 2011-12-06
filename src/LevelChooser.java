import java.awt.GridLayout;
import java.io.File;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;

/**
 * Die Klasse LevelChooser ist dafuer zustaendig, das naechste Level zu bestimmen, das geladen werden soll.
 * Dazu gibt es zwei Moeglichkeiten:
 * 		-hole das naechste ungeloeste Level
 * 			-es werden der Reihe nach solange Level geoeffnet, bis ein ungeloestes entdeckt wurde.
 * 			-sollte auch das letzte bereits geloest sein, so wird dieses erneut geoeffnet
 * 		-frage in einem Dialog nach dem gewuenschten Level
 * 			-ein Dialog-Fenster wird erzeugt, das fuer jedes Level im Ordner level/ einen Button erzeugt.
 * 			-die Spielfeld-Oberflaeche wird gesperrt, bis ein Button gedrueckt wurde.
 * 
 * Es wird jeweils nur der Dateiname des gewuenschten Levels zurueckgegeben.
 * 
 * @author Nicolai Ommer
 * @version 21.10.08
 */
public class LevelChooser implements ActionListener
{
	private LevelLader 	levelLader; // zum pruefen der gueltigkeit eines Levels
	// speichert, welches Level als letztes geladen wurde und somit auch auf der Oberflaeche aktiv ist
	private String 		aktuellesLevel;
	private JDialog 	dialog; // das Fenster, das zur Auswahl der Level angezeigt wird
	
	/**
	 * Den LevelChooser initialisieren:
	 * aktuelles Level wird als leerer String definiert
	 * Ein neuer LevelLader wird erstellt.
	 * Das Dialog-Fenster wird definiert:
	 * 	Bei aktivierung wird die Oberflaeche deaktiviert.
	 * 	Die Position soll vom BS bestimmt werden.
	 * 
	 * @param oberf Die Oberflaeche, die angehalten werden soll, wenn der Dialog angezeigt wird.
	 */
	public LevelChooser(JFrame oberf)
	{
		this.aktuellesLevel = new String("");
		this.levelLader = new LevelLader();
		this.dialog = new JDialog(oberf, "Level waehlen", true);
		//this.dialog.setLocationByPlatform(true); // Lasse die Position des Fensters durch das Betriebssystem bestimmen
	}
	
	/**
	 * Gehe alle Level im Ordner level/ durch, bis ein Level gefunden wurde,
	 * das keinen gueltigen Validatecode enthaelt und somit noch nicht bestanden wurde.
	 * Sollten alle Level einen gueltigen Code haben, so wird das letzte Level geladen.
	 * 
	 * @return Dateiname des Levels
	 */
	public String getNextLevel()
	{
		int x = 0;
		Level level;
		boolean codeGueltig;
		String[] levelnamen = getLevelnamenListe();
		
		if(levelnamen != null) {
			do {
				level = levelLader.getLevel(levelnamen[x]);
				x = x + 1;
				if(level != null) {
					codeGueltig = level.getInfos("validatecode").split("A")[0].equals(level.getValidateCode());
					aktuellesLevel = levelnamen[x-1];
				}
				else {
					codeGueltig = true;
					fehlerAusgeben("Level ist = null");
					aktuellesLevel = null;
				}
			}
			while(codeGueltig && x < levelnamen.length);
			
			return aktuellesLevel;
		}
		return null;
	}
	
	/**
     * Suche alle Level zusammen und erstelle einen Panel mit allen Buttons im GridLayout.
     * @return fertiges Panel, das alle Buttons enthaelt
     */
    public JPanel getNewLevelButtons()
    {
    	JPanel buttonpanel = new JPanel();
    	JButton button;
    	buttonpanel.setLayout(new GridLayout(0,8));
    	
    	String[] levelnamen = getLevelnamenListe();
    	for(int i = 0 ; i < levelnamen.length ; i = i + 1) {
    		button = new JButton(levelnamen[i]);
    		button.addActionListener(this);
    		buttonpanel.add(button);
    	}
    	
    	return buttonpanel;
    }
    
    /**
     * Durchsuche den Level-Ordner nach .lev Dateien und gebe diese in einer
     * ArrayList zurueck.
     * 
     * @return	ArrayList mit allen .lev Dateien im Level-Ordner
     */
	private String[] getLevelnamenListe()
	{
		File levelOrdner = new File("level/");
		if(levelOrdner.exists()) {
			File[] files = levelOrdner.listFiles();
			if(files.length > 0) {
				ArrayList<String> levelnamen = new ArrayList<String>();
				
				for(int i = 0 ; i < files.length ; i = i + 1) {
					if(files[i].getName().endsWith(".lev")) {
						levelnamen.add(files[i].getName());
					}
				}
				
				if(levelnamen.size() > 0) {
					String[] array = new String[levelnamen.size()];
					int x = 0;
					for(String string : levelnamen) {
						array[x] = string;
						x = x + 1;
					}
					Arrays.sort(array);
					return array;
				}
				else {
					fehlerAusgeben("Es sind keine .lev Datein im Ordner level/ enthalten!");
				}
			}
			else {
				fehlerAusgeben("Keine Dateien im Ordner level/ verfuegbar!");
			}
			
		}
		else {
			fehlerAusgeben("Ordner level/ existiert nicht!");
		}
		return null;
	}
	
	/**
     * Bringe ein neues Fenster zum vorschein, auf dem alle verfuegbaren Level aufgelistet sind
     * und geladen werden koennen.
     * Sobald ein Level ausgewaehlt wurde, soll dieses zurueckgegeben werden.
     * 
     * @return Dateinamen des ausgewaehlten Levels
     */
    public String getNewLevel()
    {
    	dialog.add(getNewLevelButtons());
    	dialog.pack();
    	dialog.setVisible(true);
    	return aktuellesLevel;
    }
	
    /**
     * Liefere den Namen des aktuellen Levels zurueck.
     * @return Dateinamen des aktuellen Levels
     */
	public String getAktuellesLevel()
	{
		return aktuellesLevel;
	}

	/**
	 * Wenn einer der Button des Dialogs gedrueckt wurde, wird dessen Name (der Name des Levels) gespeichert
	 * und der Dialog geschlossen.
	 * 
	 * @param event Das event, das sich durch das Druecken eines Buttons ereignet hat
	 */
	public void actionPerformed(ActionEvent event) {
		fehlerAusgeben(event.getActionCommand() + " wurde gedrueckt.");
		aktuellesLevel = event.getActionCommand();
		dialog.setVisible(false);
	}
	
	/**
	 * Wenn DEBUGMODUS=true, dann werden Nachrichten zur Kontrolle des Quellcodes ausgegeben.
	 * 
	 * @param text	Der Text, der ausgegeben werden soll.
	 */
	private void fehlerAusgeben(String text)
	{
		if(Spiel.DEBUGMODUS) {
			System.out.println("LevelChooser: " + text);
		}
	}
}

import java.awt.Point;
import java.awt.event.*;

import javax.swing.*;

/**
 * Diese Klasse enthaelt ein JDialog, der angezeigt wird, sobald der Loesungsweg gezeigt werden soll.
 * Er bietet einen Button fuer Zurueck, Vor und Beenden.
 * Wenn die Methode run(wegPunkte,location) ausgeführt wird,
 * wird der JDiaglog an die angegeben Position des Bildschirms gesetzt.
 * Mit dem Vor-Button kann man durch die Wegpunkte gehen.
 * Der Zurueck-Button macht einen Schritt rueckgaengig.
 * Wenn das Fenster geschlossen wird, oder der Beenden-Button gedrueckt wird, wird eine Methode aufgerufen,
 * die das Level neu laed, die Tasten wieder freigibt, und zum schluss sich selbst schliesst. 
 * 
 * @author Nicolai Ommer
 * @version 24.10.08
 */
public class LoesungZeigen extends JDialog implements WindowListener
{
	private static final long serialVersionUID = 4600685984113351922L;
	Spiel spiel;
	JButton zurueck = new JButton("Zurueck");
	JButton vor = new JButton("Vor");
	JButton abbrechen = new JButton("Beenden");
	JPanel fensterflaeche = new JPanel();
	Point[] wegPunkte; // ein Array, das alle Punkte der Figur enthaelt, von 0-Start bis n-Ziel
	int schritt = 0;
	
	/**
	 * Baue den Dialog auf.
	 * Die drei Buttons werden hinzugefuegt.
	 * 
	 * @param spiel Referenz zum Spiel-Objekt
	 */
	public LoesungZeigen(Spiel spiel)
	{
		this.spiel = spiel;
		zurueck.addActionListener(new ButtonZurueck());
		vor.addActionListener(new ButtonVor());
		abbrechen.addActionListener(new ButtonAbbrechen());
		fensterflaeche.add(zurueck);
		fensterflaeche.add(vor);
		fensterflaeche.add(abbrechen);
		
		this.addWindowListener(this);
		this.add(fensterflaeche);
		this.pack();
	}

	/**
	 * Lade das aktuelle Level neu, damit die Figur im Ausgangszustand ist.
	 * Mache den Dialog sichtbar und richte alle ein.
	 * 
	 * @param wegPunkte Ein Array von Punkten, die die Figur der Reihe nach ablaufen soll.
	 * @param location Position des Dialogs, sollte nach moeglichkeit an die Oberflaeche angepasst werden.
	 */
	public void run(Point[] wegPunkte, Point location)
	{
		spiel.reloadLevel();
		zurueck.setEnabled(false);
		schritt = 0;
		this.setLocation(location);
		this.setVisible(true);
		this.wegPunkte = wegPunkte;
	}
	
	/**
	 * Benutze die rueckgaengig-Funktion der Klasse Spiel, um die Figur wieder einen Schritt zurueck zu setzen.
	 * Setze "Schritt" einen zurueck und pruefe, ob noch schritte vorhanden sind, sonst button deaktivieren.
	 */
	private void zurueck()
	{
		spiel.schrittRueckgaengig();
		schritt = schritt - 1;
		if(schritt < 1) {
			zurueck.setEnabled(false);
		}
		vor.setEnabled(true);
	}
	
	/**
	 * Bewege die Figur auf das naechste Feld. Wenn das letzte Feld erreich wurde,
	 * deaktiviere den Button.
	 */
	private void vor()
	{
		spiel.bewegen(wegPunkte[schritt+1].x - wegPunkte[schritt].x,wegPunkte[schritt+1].y - wegPunkte[schritt].y);
		fehlerAusgeben(wegPunkte[schritt+1].x + "," + wegPunkte[schritt+1].y);
		schritt = schritt + 1;
		zurueck.setEnabled(true);
		if(schritt >= wegPunkte.length - 1) {
			vor.setEnabled(false);
		}
	}
	
	/**
	 * Lade aktuelles Level neu, entsperre die Tasten und schliesse den Dialog.
	 */
	private void abbrechen()
	{
		spiel.reloadLevel();
		spiel.setTastenSperren(false);
		schritt = 0;
		setVisible(false);
		dispose();
	}

	class ButtonZurueck implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			zurueck();
		}
	}
	
	class ButtonVor implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			vor();			
		}
	}
	
	class ButtonAbbrechen implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			abbrechen();
		}
	}
	
	/**
	 * Wenn DEBUGMODUS=true, dann werden Nachrichten zur Kontrolle des Quellcodes ausgegeben.
	 * 
	 * @param text	Der Text, der ausgegeben werden soll.
	 */
	private void fehlerAusgeben(String text)
	{
		if(Spiel.DEBUGMODUS) {
			System.out.println("LoesungZeigen: " + text);
		}
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		abbrechen();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}

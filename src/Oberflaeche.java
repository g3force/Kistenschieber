import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * Dies ist die Oberflaeche fuer das Spiel "Kistenschieber".
 * Sie enthaelt die Spielflaeche mit folgenden Objekten:
 *      Boden,Wand,Kiste,Punkt,Figur
 * Ausserdem: Ein Menue, das in der Klasse Spiel definiert wird.
 * 
 * Und eine Statusleiste im unteren Bereich des Fensters.
 * Dort wird der Name des aktuellen Levels und die Anzahl der bisherigen Schritte angezeigt.
 * 
 * @author Nicolai Ommer
 * @version 24.10.08
 */
public class Oberflaeche extends JFrame implements WindowFocusListener, WindowListener
{
	private static final long serialVersionUID = 5932033652233743276L;
	LoesungZeigen loesungZeigen;
	private JPanel 		fensterflaeche; // enthaelt aktSpielfeld und statusleiste
	private JPanel 		aktSpielfeld; // enthaelt das Spielfeld
	private JMenuBar 	menuBar; // die Menueleiste, deren Eintraege aus der Klasse Spiel hinzugefuegt werden
	private JLabel 		statusleiste; // Label am unteren Rand des Fensters

    /**
     * Der Konstruktor erstellt eine neue MenuBar und ruft die Methode zum Fenster aufbauen auf.
     */
    public Oberflaeche(LoesungZeigen loesungZeigen)
    {
    	this.loesungZeigen = loesungZeigen;
    	this.addWindowFocusListener(this);
    	this.addWindowListener(this);
    	menuBar = new JMenuBar();
        fensterAufbauen();
    }

    /**
     * Das Fenster wird aufgebaut.
     * Es wird der Titel des Fensters definiert.
     * Die Fensterflaeche wird formatiert, und eine Statusleiste erstellt.
     * Ausserdem wird ein leeres Spielfeld eingefuegt, damit spaeter beim loeschen
     * keine Fehlermeldung auftritt, und das Spielfeld erfolgreich erneuert werden kann.
     */
    private void fensterAufbauen()
    {
        this.setTitle(Spiel.TITEL);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Wenn Fenster geschlossen wird, wird das Programm beendet
        //this.setLocationByPlatform(true); // Lasse die Position des Fensters durch das Betriebssystem bestimmen
        
        fensterflaeche = (JPanel)getContentPane();
        fensterflaeche.setLayout(new BorderLayout());
        
        aktSpielfeld = new JPanel();
        fensterflaeche.add(aktSpielfeld);
        
        statusleiste = new JLabel("Fenster initialisiert.");
        fensterflaeche.add(statusleiste, BorderLayout.SOUTH);
        pack();
    }
    
    /**
     * Menue-Eintraege erstellen.
     * Diese Methode wird von der Klasse "Spiel" aus ausgefuehrt.
     * 	Sie traegt jeden Menue-Eintrag einzeln ins Menue ein und
     * 	aktuallisiert es danach sofort.
     * 
     * @param text		Name des Menueeintrages
     * @param menu		Das Menue, in welches der Menue-Eintrag eingetragen wird
     * @param listener	ActionListener fuer den Menue-Eintrag (in der Regel in "Spiel" definiert)
     */
    public void menueEintrag(String text, JMenu menu, ActionListener listener)
    {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.addActionListener(listener);
        menu.add(menuItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);
        pack();
    }
    
    /**
     * Binde das uebergebene Spielfeld in die Fensterflaeche ein.
     * Loesche dazu vorher das alte Spielfeld.
     * 
     * @param spielfeldPanel	Spielfeld(JPanel) mit definierten Feldern(JLabels)
     */
    public void reloadSpielfeld(JPanel spielfeldPanel,String aktuellesLevel,int schritte)
    {
    	fensterflaeche.remove(aktSpielfeld);
    	fensterflaeche.remove(statusleiste);
    	
    	aktSpielfeld = spielfeldPanel;
    	statusleiste = new JLabel(aktuellesLevel + " - " + schritte + " Schritte");
    	
        fensterflaeche.add(aktSpielfeld,BorderLayout.WEST);
        fensterflaeche.add(statusleiste,BorderLayout.SOUTH);
        pack();
    }
    
    /**
     * Aktiviere oder Deaktiviere ein Menue in der Menueleiste.
     * Es wird nach einem Menueitem mit dem angegebenen Text gesucht und auf den gewuenschten Zustand gesetzt.
     * Existiert kein Menueitem mit angegebenem Namen, so wird false zurueckgegeben.
     * 
     * @param menuItem Name des zu aendernden Menueitems
     * @param bool aktivieren/deaktivieren
     * @return ob das Menueitem gefunden wurde oder nicht
     */
    public boolean setMenuItemEnabled(String menuItem, boolean bool)
    {
    	for(int i = 0 ; i < menuBar.getMenuCount() ; i = i + 1) {
    		for(int j = 0 ; j < menuBar.getMenu(i).getMenuComponentCount() ; j = j + 1) {
    			if(menuBar.getMenu(i).getItem(j).getText().equals(menuItem)) {
    				menuBar.getMenu(i).getItem(j).setEnabled(bool);
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
    /**
	 * Wenn DEBUGMODUS=true, dann werden Nachrichten zur Kontrolle des Quellcodes ausgegeben.
	 * 
	 * @param text	Der Text, der ausgegeben werden soll.
	 */
	@SuppressWarnings("unused")
	private void fehlerAusgeben(String text)
	{
		if(Spiel.DEBUGMODUS) {
			System.out.println("Oberflaeche: " + text);
		}
	}

	@Override
	public void windowGainedFocus(WindowEvent arg0)
	{
		if(loesungZeigen.isVisible()) {
			loesungZeigen.setVisible(true);
		}
	}

	@Override
	public void windowLostFocus(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
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
		loesungZeigen.dispose(); //Schliesse das Dialogfenster, damit das Programm beendet werden kann
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

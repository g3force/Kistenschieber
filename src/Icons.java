import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Hier werden die Icons, die die einzelnen Felder darstellen, gespeichert.
 * Es sind fuenf verschiedene Icons noetig. Diese liegen in einem beliebigen
 * Verzeichnis im Ordner "Bilder/".
 * Durch aufruf der Methode load(String dir) kann man eine neue Icon-Sammlung laden.
 * 	dir steht hier fuer den Namen des Ordners in "Bilder/", indem die fuenf gueltigen Icons liegen.
 * Diese muessen folgendermassen benannt werden:
 * 		Boden.jpg,Figur.jpg,Kiste.jpg,Punkt.jpg und Wand.jpg
 * Sollte eines dieser Icons nicht existierten, so wird keines geladen und die alten Icons
 * bleiben bestehen.
 * 
 * @author Nicolai Ommer
 * @version 15.10.08
 *
 */
public class Icons
{
	/**
	 * In den Datenfeldern stehen die einzelnen Icons.
	 */
	//private ImageIcon Boden,Figur,FigurPunkt,Kiste,KistePunkt,Punkt,Wand;
	private String[] fields = {"boden","figur","figurpunkt","kiste","kistepunkt","punkt","wand"};
	private ImageIcon[] icons = new ImageIcon[7];
	/**
	 * Initialiesere alle Icons, falls spaeter ein Fehler auftritt.
	 * Rufe die Methode zum Laden einer Icon-Sammlung auf.
	 * 
	 * @param dir Name des Ordners, in dem sich die gewuenschten Icons befinden. 
	 */
	public Icons(String dir)
	{
		/*
		Boden = new ImageIcon();
		Figur = new ImageIcon();
		FigurPunkt = new ImageIcon();
		Kiste = new ImageIcon();
		KistePunkt = new ImageIcon();
		Punkt = new ImageIcon();
		Wand  = new ImageIcon();
		*/
		load(dir);
	}
	
	/**
	 * Lade alle Icons mit uebergebenem Pfad und pruefe ob diese alle existieren.
	 * 
	 * @param dir Name des Ordners, in dem sich die Icons befinden
	 */
	public void load(String dir)
	{
		Integer I = 0;
		//fehlerAusgeben(Icons.class.getResource("bilder/boden.gif").toString());
		fehlerAusgeben(System.getProperty("java.class.path"));
		String fulldir = new String("bilder/" + dir + "/");
		String ending = new String(".gif");
		
		// Erstes Icons wird leer initialisiert, falls es fehlt; andere fehlende Icons werden durch dieses ersetzt.
		icons[0]=new ImageIcon();
		
		for(I=0;I<7;I++) {
			icons[I] = new ImageIcon(fulldir + fields[I] + ending);
			
			if(icons[I].getIconHeight() <= 0) {
				icons[I]=icons[0];
				if(I==0) {
					fehlerAusgeben("Erstes Icon nicht gefunden. Bleibt leer, ebenso alle anderen Fehlenden!");
				}
				else {
					fehlerAusgeben("Konnte Icon \"" + fields[I] + "\" nicht finden.");
				}
			}
		}
		
		/* Alter Code:
		ImageIcon newBoden = new ImageIcon("bilder/" + dir + "/boden.gif");
		ImageIcon newFigur = new ImageIcon("bilder/" + dir + "/figur.gif");
		ImageIcon newFigurPunkt = new ImageIcon("bilder/" + dir + "/figurpunkt.gif");
		ImageIcon newKiste = new ImageIcon("bilder/" + dir + "/kiste.gif");
		ImageIcon newKistePunkt = new ImageIcon("bilder/" + dir + "/kistePunkt.gif");
		ImageIcon newPunkt = new ImageIcon("bilder/" + dir + "/punkt.gif");
		ImageIcon newWand  = new ImageIcon("bilder/" + dir + "/wand.gif");
		
		if(		   newBoden.getIconHeight() > 0
				&& newFigur.getIconHeight() > 0
				&& newFigurPunkt.getIconHeight() > 0
				&& newKiste.getIconHeight() > 0
				&& newKistePunkt.getIconHeight() > 0
				&& newPunkt.getIconHeight() > 0
				&&  newWand.getIconHeight() > 0) {
					Boden 		= newBoden;
					Figur 		= newFigur;
					FigurPunkt 	= newFigurPunkt;
					Kiste 		= newKiste;
					KistePunkt 	= newKistePunkt;
					Punkt 		= newPunkt;
					Wand  		= newWand;
					fehlerAusgeben("Neue Icons uebernommen.");
		}
		else {
			fehlerAusgeben("Mindestens ein Icon ist ungueltig. Icons werden nicht geladen!");
		}
		*/
	}
	
	public Icon getBoden()
	{
		return icons[0];
	}

	public Icon getFigur()
	{
		return icons[1];
	}
	
	public Icon getFigurPunkt()
	{
		return icons[2];
	}

	public Icon getKiste()
	{
		return icons[3];
	}
	
	public Icon getKistePunkt()
	{
		return icons[4];
	}

	public Icon getPunkt()
	{
		return icons[5];
	}

	public Icon getWand()
	{
		return icons[6];
	}
	
	/**
	 * Wenn DEBUGMODUS=true, dann werden Nachrichten zur Kontrolle des Quellcodes ausgegeben.
	 * 
	 * @param text	Der Text, der ausgegeben werden soll.
	 */
	private void fehlerAusgeben(String text)
	{
		if(Spiel.DEBUGMODUS) {
			System.out.println("Icons: " + text);
		}
	}
}

import java.util.HashMap;

/**
 * Hier werden alle Informationen zu einem Level gespeichert, die in der Leveldatei gefunden wurden.
 * Eine Info setzt sich zusammen aus einem Schluessel und einem Wert.
 * Ueber den Schluessel kann man auf den zugehoerigen Wert zugreifen.
 * 
 * @author Nicolai Ommer
 * @version 21.10.08
 */
public class LevelInfos
{
	/**
	 * Hier werden die Informationen gespeichert.
	 * Ein Schluessel wird einem Wert zugeordnet.
	 */
	HashMap<String,String> infos = new HashMap<String,String>();
	
	/**
	 * Fuege eine neue Info ein.
	 * 
	 * @param schluessel Identifizierer der Info
	 * @param wert Inhalt der Info
	 */
	public void setInfo(String schluessel, String wert)
	{
		infos.put(schluessel, wert);
	}
	
	/**
	 * Liefere den Wert des angegebenen Schluessels zurueck.
	 * 
	 * @param schluessel Identifizierer der Info
	 * @return Inhalt der Info
	 */
	public String getInfo(String schluessel)
	{
		if(infos.containsKey(schluessel)) {
			return infos.get(schluessel);
		}
		return "";
	}
}

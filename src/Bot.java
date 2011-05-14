import java.util.ArrayList;


public class Bot {
	Spiel spiel;
	Spielfeld spielfeld;
	ArrayList<Direction> path=new ArrayList<Direction>();
	boolean running = false;
	int dir;
	
	public Bot(Spiel s,Spielfeld sp) {
		spiel=s;
		spielfeld=sp;
		init();
	}
	
	private Direction getLastPath() {
		return path.get(path.size()-1);
	}
	
	public void step() {
		getLastPath().incDir();
		
		if(deadEnd()) {
			debug("Dead End.");
			if(path.size()>0) {
				path.remove(path.size()-1);
			}
			else {
				debug("ACHTUNG! Das Level scheint unloesbar zu sein...");
			}
			spiel.schrittRueckgaengig();
		}
		else {
			
			if(getLastPath().getRotation()<=4) {
				// If direction is where figure came from (1<->3 bzw. 2<->4)
				if(path.size()>1 && Math.abs(path.get(path.size()-2).getDir()-getLastPath().getDir())==2) {
					debug("Back Direction. dir="+getLastPath().getDir());
					step();
				}
				else {
					debug("Try moving in dir="+getLastPath().getDir());
					
					if(spiel.bewegen(getLastPath().getX(), getLastPath().getY())) {
						path.add(new Direction(getLastPath().getDir()));
						debug("Path added. dir="+getLastPath().getDir());
					}
					else {
						debug("Could not move. Performed rotations="+getLastPath().getRotation());
						step();
					}
				}
			}
			else {
				path.remove(path.size()-1);
				spiel.schrittRueckgaengig();				
				debug("Backtracked. Current dir="+getLastPath().getDir());
			}
		}
	}
	
	public boolean deadEnd() {
		char[][] felder = spielfeld.getAlleFelder();
		int kistenFound=0;
		
		for(int i=0; i<spielfeld.getLevel().getlevelbreite() && 
		kistenFound!=spielfeld.getLevel().getAnzKisten();i++) {
			for(int j=0; j<spielfeld.getLevel().getlevelhoehe() && 
			kistenFound!=spielfeld.getLevel().getAnzKisten();j++) {
				if(felder[i][j]=='$' || felder[i][j]=='*') {
					kistenFound++;
					boolean free=true;
					for(int a=1;a<5;a++) {
						Direction kDir=new Direction(a);
						if(i+kDir.getY()>=0 && i+kDir.getY()<spielfeld.getLevel().getlevelbreite() &&
								j+kDir.getX()>=0 && j+kDir.getX()<spielfeld.getLevel().getlevelhoehe() &&
								(felder[i+kDir.getX()][j+kDir.getY()]=='.' || felder[i+kDir.getX()][j+kDir.getY()]==' ' ||
								felder[i+kDir.getX()][j+kDir.getY()]=='@' || felder[i+kDir.getX()][j+kDir.getY()]=='+')) {
							// Field is free
							free=true;
						}
						else {
							// Field is not free
							if(!free) {
								debug("Dead End: kDir="+kDir.getDir()+" kiste(i,j)=("+i+","+j+")");
								return true;
							}
							free=false;
						}
					}
				}
			}
		}
		return false;
	}
	
	public void stop() {
		running=false;
	}
	
	public void start() {
		init();
		running=true;
	}
	
	public void init() {
		running = false;
		dir=1;
		path=new ArrayList<Direction>();
		path.add(new Direction(1));
	}

	public boolean isRunning() {
		return running;
	}
	
	/**
	 * Wenn DEBUGMODUS=true, dann werden Nachrichten zur Kontrolle des Quellcodes ausgegeben.
	 * 
	 * @param text	Der Text, der ausgegeben werden soll.
	 */
	private void debug(String text)
	{
		if(Spiel.DEBUGMODUS) {
			System.out.println("Bot: " + text);
		}
	}
}

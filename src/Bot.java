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
		dir=(dir)%4+1;
		getLastPath().setI(dir);
		fehlerAusgeben("Try moving in dir="+dir);
		
		if(!spiel.bewegen(getLastPath().getX(), getLastPath().getY())) {
			fehlerAusgeben("Could not move. Performed rotations="+getLastPath().getRotation());

			getLastPath().incRotation();
			
			if(getLastPath().getRotation()<4) {
				// If direction is where figure came from (1<->3 bzw. 2<->4)
				if(Math.abs(path.get(path.size()-2).getI()-dir)==2) {
					fehlerAusgeben("Back Direction. dir="+dir);
				}
				else {
					//step();	
				}
			}
			else {
				path.remove(path.size()-1);
				spiel.schrittRueckgaengig();				
				fehlerAusgeben("Backtracked. Next dir="+dir);
			}
			
		}
		else {
			path.add(new Direction(dir-1));
			dir--;
			fehlerAusgeben("path added. dir="+dir);
		}
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
	private void fehlerAusgeben(String text)
	{
		if(Spiel.DEBUGMODUS) {
			System.out.println("Bot: " + text);
		}
	}
}

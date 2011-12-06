import java.util.ArrayList;
import java.util.LinkedList;


public class Bot {
	Spiel spiel;
	Spielfeld spielfeld;
	ArrayList<Direction> path=new ArrayList<Direction>();
	boolean running = false;
	int dir;
//	int newSteps=0;
	LinkedList<char[][]> history = new LinkedList<char[][]>(); 
	
	public Bot(Spiel s,Spielfeld sp) {
		spiel=s;
		spielfeld=sp;
		init();
	}
	
	private Direction getLastPath() {
		if(path.size()<=0) return new Direction(0);
		return path.get(path.size()-1);
	}
	
	private boolean back() {
		if(path.size()>0) {
			path.remove(path.size()-1);
			spiel.schrittRueckgaengig();
			debug("Backtracked. Current dir="+getLastPath().getDir());
			if(history.size()>0) history.remove();
			return true;
		}
		else {
			debug("ACHTUNG! Das Level scheint unloesbar zu sein...");
		}
		return false;
	}
	
	public void step() {
		getLastPath().incDir();
		
		if(path.size()==0) {
			debug("ACHTUNG! Das Level scheint unloesbar zu sein...");
		}
		else if(deadEnd()) {
			back();
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
					int moved = spiel.bewegen(getLastPath().getX(), getLastPath().getY()); 
					if(moved!=0) {
						path.add(new Direction(getLastPath().getDir()));
						debug("Path added. dir="+getLastPath().getDir());
						history.add(spiel.getSpielfeld().getAlleFelder());
						//if(moved==2) history.clear(); //newSteps=0;
						debug(history.size()+" steps ahead");
						for(int i=0;i<history.size()-1;i++) {
							debug(spiel.getLastFigPos(i+1)+" "+spiel.getLastFigPos(0));
							//if(spiel.getLastFigPos(i+1).equals(spiel.getLastFigPos(0))) {
							if(samefields(history.get(i),history.getLast())) {
//								debug("Circle! Go back "+(i+1)+" steps");
//								for(int j=0;j<i+1;j++) {
									back();
//								}
								break;
							}							
						}
					}
					else {
						debug("Could not move. Performed rotations="+getLastPath().getRotation());
						step();
					}
				}
			}
			else {
				back();
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
				if(felder[i][j]=='$') {
					kistenFound++;
					boolean free=true;
					Direction kDir = new Direction(0);
					for(int a=1;a<6;a++) {
						kDir=new Direction(a);
						if(i+kDir.getY()>=0 && i+kDir.getY()<spielfeld.getLevel().getlevelbreite() &&
								j+kDir.getX()>=0 && j+kDir.getX()<spielfeld.getLevel().getlevelhoehe()) {
							if(felder[i+kDir.getX()][j+kDir.getY()]!='#') {
								free=true;
							}
							else {
								if(!free) return true;
								free=false;
							}
							if(felder[i+kDir.getX()][j+kDir.getY()]=='$') {
								if(kDir.getDir()%2==0) {
									if(	(j<spielfeld.getLevel().getlevelhoehe() && 
										felder[i][j+1]=='#' && felder[i+kDir.getX()][j+kDir.getY()+1]=='#') ||
										(j>0 && felder[i][j-1]=='#' && felder[i+kDir.getX()][j+kDir.getY()-1]=='#')) {
										return true;
									}
								}
								else {
									if(	(i<spielfeld.getLevel().getlevelbreite() && 
										felder[i+1][j]=='#' && felder[i+kDir.getX()+1][j+kDir.getY()]=='#') ||
										(i>0 && felder[i-1][j]=='#' && felder[i+kDir.getX()-1][j+kDir.getY()]=='#')) {
										return true;
									}
								}
							}
						}
						else {
							if(felder[i+kDir.getX()][j+kDir.getY()]=='#') {
								if(!free) return true;
								free=false;
							}
						}
					}
				}
				else if(felder[i][j]=='*') {
					kistenFound++;
				}
			}
		}
		return false;
	}
	
	public boolean samefields(char[][] a, char[][] b) {
		for(int y = 0 ; y < a.length ; y++) {
			for(int x = 0 ; x < a[0].length ; x++) {
				if(a[y][x]!=b[y][x]) return false;
			}
		}		
		return true;
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

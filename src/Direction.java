/*
 * i  x  y
 * 0  0  0
 * 1  0 -1
 * 2  1  0
 * 3  0  1
 * 4 -1  0
 */
public class Direction {
	private int x=0;
	private int y=0;
	private int dir=0;
	private int rotation=0;
	
	public Direction(int dir) {
		setDir(dir);		
	}
	
	public void incDir() {
		setDir(dir%4+1);
		rotation++;
	}
	
	public void decDir() {
		dir--;
		if(dir<=0) dir=4;
		setDir(dir);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getDir() {
		return dir;
	}

	public int getRotation() {
		return rotation;
	}

	public void setRotation(int rotation) {
		this.rotation = rotation;
	}

	public void setDir(int dir) {
		if(dir>=0 && dir<6) {
			this.dir=dir;
			x=0;
			y=0;
			switch(dir) {
			case 1: y=-1; break;
			case 2: x=1; break;
			case 3: y=1; break;
			case 4: x=-1; break;
			case 5: y=-1; break;
			}
		}
		else {
			fehlerAusgeben("passed dir is too high/low");
		}
	}
	
	public boolean samePos(Direction direction) {
		if(direction.getX()==getX() && direction.getY()==getY()) return true;
		return false;
	}
	
	public String toString() {
		return "X="+getX()+" Y="+getY()+" dir="+getDir();		
	}
	
	/**
	 * Wenn DEBUGMODUS=true, dann werden Nachrichten zur Kontrolle des Quellcodes ausgegeben.
	 * 
	 * @param text	Der Text, der ausgegeben werden soll.
	 */
	private void fehlerAusgeben(String text)
	{
		if(Spiel.DEBUGMODUS) {
			System.out.println("Direction: " + text);
		}
	}
}
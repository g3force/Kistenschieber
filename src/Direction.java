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
	private int i=0;
	private int rotation=0;
	
	public Direction(int new_i) {
		setI(new_i);		
	}
	
	public void incRotation() {
		rotation++;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getI() {
		return i;
	}

	public int getRotation() {
		return rotation;
	}

	public void setRotation(int rotation) {
		this.rotation = rotation;
	}

	public void setI(int new_i) {
		if(new_i>=0 && new_i<5) {
			i=new_i;
			x=0;
			y=0;
			switch(i) {
			case 1: y=-1; break;
			case 2: x=1; break;
			case 3: y=1; break;
			case 4: x=-1; break;
			}
		}
	}
}
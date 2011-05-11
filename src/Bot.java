
public class Bot {
	Spiel spiel;
	Spielfeld spielfeld;

	public Bot(Spiel s,Spielfeld sp) {
		spiel=s;
		spielfeld=sp;
		
	}
	
	public void run() {
		while(!spielfeld.pruefeObGewonnen()) {
			spiel.bewegen(0, 1);
			
			break;
		}
	}

	
	
}

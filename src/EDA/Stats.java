package EDA;

public class Stats {
	private int match;
	private int miss;
	private int GU;
	private int GC;
	private int AU;
	
	@Override
	public String toString(){
		String cadena=String.valueOf(match)+","+String.valueOf(miss)+","+String.valueOf(AU)+","+String.valueOf(GC)+","+String.valueOf(GU)+",";
		return cadena;
	}
	
	public Stats(){
		this.setMatch(0);
		this.setMiss(0);
		this.setAU(0);
		this.setGC(0);
		this.setGU(0);
	}
	
	public Stats(int match, int miss, int GU, int GC, int AU){
		this.setMatch(match);
		this.setMiss(miss);
		this.setAU(AU);
		this.setGC(GC);
		this.setGU(GU);
	}
	
	public int getMatch() {
		return match;
	}
	public void setMatch(int match) {
		this.match = match;
	}
	public int getMiss() {
		return miss;
	}
	public void setMiss(int miss) {
		this.miss = miss;
	}
	public int getGU() {
		return GU;
	}
	public void setGU(int gU) {
		GU = gU;
	}
	public int getGC() {
		return GC;
	}
	public void setGC(int gC) {
		GC = gC;
	}
	public int getAU() {
		return AU;
	}
	public void setAU(int aU) {
		AU = aU;
	}
}

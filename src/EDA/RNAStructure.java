package EDA;

public class RNAStructure {
	private String miRNA_id;
	private String miRNA;
	private String lncRNA_id;
	private String lncRNA;
	private String mre;
	private int position;
	
	@Override
	public String toString(){
		String cadena = miRNA_id+","+lncRNA_id+","+String.valueOf(position)+",";
		return cadena;
	}
	
	public RNAStructure(){
		this.setmiRNA_id(null);
		this.setMiRNA(null);
		this.setLncRNA_id(null);
		this.setLncRNA(null);
		this.setPosition(0);
	}
	
	public RNAStructure(String miRNA_id,String miRNA,String lncRNA_id,String lncRNA,String mre,int position){
		this.setmiRNA_id(miRNA_id);
		this.setMiRNA(miRNA);
		this.setLncRNA_id(lncRNA_id);
		this.setLncRNA(lncRNA);
		this.setMre(mre);
		this.setPosition(position);
	}
	
	public String getmiRNA_id(){
		return miRNA_id;
	}
	
	public void setmiRNA_id(String miRNA_id){
		this.miRNA_id=miRNA_id;
	}

	public String getMiRNA() {
		return miRNA;
	}

	public void setMiRNA(String miRNA) {
		this.miRNA = miRNA;
	}

	public String getLncRNA_id() {
		return lncRNA_id;
	}

	public void setLncRNA_id(String lncRNA_id) {
		this.lncRNA_id = lncRNA_id;
	}

	public String getLncRNA() {
		return lncRNA;
	}

	public void setLncRNA(String lncRNA) {
		this.lncRNA = lncRNA;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getMre() {
		return mre;
	}

	public void setMre(String mre) {
		this.mre = mre;
	}
	
}

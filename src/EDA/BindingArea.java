package EDA;

public class BindingArea {
	private String miRNA_miss;
	private String miRNA;
	private String link;
	private String lncRNA;
	private String lncRNA_miss;
	
	public BindingArea(){
		this.setMiRNA_miss(null);
		this.setMiRNA(null);
		this.setLink(null);
		this.setLncRNA(null);
		this.setLncRNA_miss(null);
	}
	
	public BindingArea(String miRNA_miss, String miRNA, String link, String lncRNA, String lncRNA_miss){
		this.setMiRNA_miss(miRNA_miss);
		this.setMiRNA(miRNA);
		this.setLink(link);
		this.setLncRNA(lncRNA);
		this.setLncRNA_miss(lncRNA_miss);
	}
	
	public String getMiRNA_miss() {
		return miRNA_miss;
	}
	public void setMiRNA_miss(String miRNA_miss) {
		this.miRNA_miss = miRNA_miss;
	}
	public String getMiRNA() {
		return miRNA;
	}
	public void setMiRNA(String miRNA) {
		this.miRNA = miRNA;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getLncRNA_miss() {
		return lncRNA_miss;
	}
	public void setLncRNA_miss(String lncRNA_miss) {
		this.lncRNA_miss = lncRNA_miss;
	}
	public String getLncRNA() {
		return lncRNA;
	}
	public void setLncRNA(String lncRNA) {
		this.lncRNA = lncRNA;
	}
}

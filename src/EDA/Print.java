package EDA;

public class Print {

	private String print;
	private int order;
	
	public Print(){
		this.setPrint(null);
		this.setOrder(0);
	}
	
	public Print(String print,int order){
		this.setPrint(print);
		this.setOrder(order);
	}
	
	public void setPrint(String print){this.print=print;}
	public String getPrint(){return print;}
	public void setOrder(int order){this.order=order;}
	public int getOrder(){return order;}
}

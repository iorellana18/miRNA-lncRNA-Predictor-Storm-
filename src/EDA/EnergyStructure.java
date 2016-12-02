package EDA;

public class EnergyStructure {
	private float mfe;
	private float binding;
	private float DGOpen;
	private float DGAccessibility;
	private float Region3;
	private float Region5;
	
	@Override
	public String toString(){
		String cadena = "";
		cadena=cadena+String.valueOf(mfe)+",";
		if(DGAccessibility!=0){
			cadena=cadena+String.valueOf(DGAccessibility)+",";
		}
		if(Region3!=0){
			cadena=cadena+String.valueOf(Region3)+",";
		}
		if(Region5!=0){
			cadena=cadena+String.valueOf(Region5)+",";
		}
		return cadena;
	}
	
	public EnergyStructure(){
		this.setMfe(0);
		this.setBinding(0);
		this.setDGOpen(0);
		this.setDGAccessibility(0);
		this.setRegion3(0);
		this.setRegion5(0);
	}
	
	public EnergyStructure(float mfe, float binding){
		this.setMfe(mfe);
		this.setBinding(binding);
		this.setDGOpen(0);
		this.setDGAccessibility(0);
		this.setRegion3(0);
		this.setRegion5(0);
	}
	
	public EnergyStructure(float mfe, float binding, float DGOpen, float DGAccessibility){
		this.setMfe(mfe);
		this.setBinding(binding);
		this.setDGOpen(DGOpen);
		this.setDGAccessibility(DGAccessibility);
		this.setRegion3(0);
		this.setRegion5(0);
	}
	
	public EnergyStructure(float mfe, float binding, float DGOpen, float DGAccessibility, float Region3, float Region5 ){
		this.setMfe(mfe);
		this.setBinding(binding);
		this.setDGOpen(DGOpen);
		this.setDGAccessibility(DGAccessibility);
		this.setRegion3(Region3);
		this.setRegion5(Region5);
	}

	public float getMfe() {
		return mfe;
	}

	public void setMfe(float mfe) {
		this.mfe = mfe;
	}

	public float getBinding() {
		return binding;
	}

	public void setBinding(float binding) {
		this.binding = binding;
	}

	public float getDGOpen() {
		return DGOpen;
	}

	public void setDGOpen(float dGOpen) {
		DGOpen = dGOpen;
	}

	public float getDGAccessibility() {
		return DGAccessibility;
	}

	public void setDGAccessibility(float dGAccessibility) {
		DGAccessibility = dGAccessibility;
	}

	public float getRegion3() {
		return Region3;
	}

	public void setRegion3(float region3) {
		Region3 = region3;
	}

	public float getRegion5() {
		return Region5;
	}

	public void setRegion5(float region5) {
		Region5 = region5;
	}

}

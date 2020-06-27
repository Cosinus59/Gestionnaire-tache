package application;

public class Projet {

	Tache projet;
	String FileName;
	
	
	public Projet() {
		
	}
	public Projet(Tache item) {
		this.projet = item;
	}
	
	public Tache getProjet() {
		return projet;
	}
	public void setProjet(Tache projet) {
		this.projet = projet;
	}
	public String getFileName() {
		return FileName;
	}
	public void setFileName(String filename) {
		FileName = filename;
	}
	
	@Override
	public String toString() {
		return this.projet != null ? this.projet.getName() : "Sans Nom";
	}
	
}

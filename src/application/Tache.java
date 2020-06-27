package application;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Tache implements Serializable {
	
	private int level;
	private String name;
	private String startDate;
	private long time;
	private String text;
	String FileName;
	
	public Tache parent;
	public List<Tache> children;
	
	public Tache() {
		
	}
	
	public Tache(String name, String text, int level) {
		this.level = level;
		this.name = name;
		this.startDate = getStartDate(LocalDate.now());
		this.time = 0;
		this.text = text;
		
		this.children = new ArrayList<Tache>();
	}
	
	public Tache(String name) {
		this(name,"",0);
	}

	public Tache getRoot() {
		if(this.isRoot())return this;
		else return parent.getRoot();
	}
	
	public boolean isRoot() {
		return parent == null;
	}

	public boolean isLeaf() {
		return children.size() == 0;
	}
	
	public boolean addChild(Tache child) {
		Tache children = new Tache("Nouvelle Tache");
		children.parent = this;
		this.children.add(child);
		return true;
	}
	
	public Tache getParent() {
		return parent;
	}

	public void setParent(Tache parent) {
		this.parent = parent;
	}

	public List<Tache> getChildren() {
		return children;
	}

	public void setChildren(List<Tache> children) {
		this.children = children;
	}

	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getStartDate() {
		return this.startDate;
	}
	public void setStartDate(LocalDate startDate) {
		this.startDate = getStartDate(startDate);
	}
	
	public long getTime() {
		return this.time;
	}
	
	public long getTotalTime() {
		if(this.isLeaf()) return this.time;
		long total = this.time;
		for (Tache tache : children) {
			total+=tache.getTotalTime();
		}
		return total;
	}
	public void setTime(long time) {
		this.time = time;
	}

	public String getText() {
		return this.text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public int getLevel() {
		return this.level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}

	public void setStartDate(String date) {
		this.startDate = date;
	}
	
	public String getStartDate(LocalDate startDate) {
		String mois = "";
		if(startDate.getMonth()==Month.JANUARY)mois = "Janvier";
		if(startDate.getMonth()==Month.FEBRUARY)mois = "Février";
		if(startDate.getMonth()==Month.MARCH)mois = "Mars";
		if(startDate.getMonth()==Month.APRIL)mois = "Avril";
		if(startDate.getMonth()==Month.MAY)mois = "May";
		if(startDate.getMonth()==Month.JUNE)mois = "Juin";
		if(startDate.getMonth()==Month.JULY)mois = "Juillet";
		if(startDate.getMonth()==Month.AUGUST)mois = "Août";
		if(startDate.getMonth()==Month.SEPTEMBER)mois = "Septembre";
		if(startDate.getMonth()==Month.OCTOBER)mois = "Octobre";
		if(startDate.getMonth()==Month.NOVEMBER)mois = "Novembre";
		if(startDate.getMonth()==Month.DECEMBER)mois = "Décembre";
		return ""+startDate.getDayOfMonth()+" "+ mois + " " + startDate.getYear();
	}
	
	public String getFileName() {
		return FileName;
	}

	public void setFileName(String fileName) {
		FileName = fileName;
	}

	public String toString() {
		return this.name;
	}
	
}

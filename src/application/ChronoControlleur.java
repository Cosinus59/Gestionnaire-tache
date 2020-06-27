package application;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;

import javax.swing.filechooser.FileSystemView;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

@SuppressWarnings("unused")
public class ChronoControlleur {
	
	
	@FXML
	SplitPane main;
	@FXML
	SplitMenuButton startMenu;
	@FXML
	MenuButton rightMenu;
	@FXML
	MenuItem playBtn,renameBtn,subBtn,newProjectBtn,deleteBtn;
	@FXML
	MenuItem selectedLoadBtn,selectedRenameBtn;
	@FXML
	ContextMenu nameMenu, textMenu;
	@FXML
	Label nameLbl,projectNameLbl,chronoLbl,startDateLbl,textLbl,projectTimeLbl,totalLbl;
	@FXML
	Label selectedNameLbl,selectedProjectNameLbl,selectedTextLbl,selectedTimeLlb;
	@FXML
	Label WarningLbl;
	@FXML
	BorderPane WarningMsg;
	@FXML
	TextField renameFld,selectedRenameFld;
	@FXML
	TextArea reTextFld,selectedReTextFld;
	@FXML
	TreeView<Tache> arborescence;
	@FXML
	VBox VBoxRight;
	@FXML
	Button loadBtn;
	
	static String toDay;

	public void initialize() {
		
		
		projectTimeLbl.setText(displayTime(Main.time).substring(0, 5));
		
		MenuItem openItem = new MenuItem("Ouvrir");
		openItem.setOnAction(e-> {
				Main.loadSelected();
	    	});
		MenuItem renameItem = new MenuItem("Renommer");
		renameItem.setOnAction(e-> {
				pressedSelectedRename(e);
	    	});
		MenuItem deleteItem = new MenuItem("Supprimer");
		deleteItem.setOnAction(e-> {
				pressedSelectedDelete(e);
	    	});
		ContextMenu treeMenu = new ContextMenu(openItem,renameItem,deleteItem);
		
		arborescence.setCellFactory(tree -> {
            TreeCell<Tache> cell = new TreeCell<Tache>() {
                @Override
                public void updateItem(Tache item, boolean empty) {
                    super.updateItem(item, empty) ;
                    if (empty) {
                        setText(null);
                        setContextMenu(null);
                    } else {
                        setText(item.getName());
                        setContextMenu(treeMenu);
                    }
                }
            };
            cell.setOnMouseClicked(event -> {
                if (! cell.isEmpty()) {
                	Main.selectedTreeItem = arborescence.getSelectionModel().getSelectedItem();
                	Main.selected = Main.selectedTreeItem.getValue();
                	Main.selectedProject = Main.getRoot(Main.selectedTreeItem).getValue();
        			arborescence.getSelectionModel().getSelectedItem().setExpanded(true);
        			updateSelection();
        			if (event.getClickCount() == 2) {
        				try {
        					for (TreeItem<Tache> item : Main.selectedTreeItem.getParent().getChildren()) {
        						if(item!=Main.selectedTreeItem)item.setExpanded(false);
        					}
        					Main.loadSelected();
        					displayRefresh();
        				} catch (NullPointerException e) {
        					// C'est normal si on clique a coté
        				}
        				
        			}
                }
            });
            return cell;
		});
	}
	
	
	
	public void deleteWarning(boolean isRoot) {
		
		//if(isRoot)Main.msgLbl.setText("Voulez-vous supprimer ce Tache ?");
		//else Main.msgLbl.setText("Voulez-vous supprimer cette tâche ?");
		Main.deleteWarning.showAndWait();
	}
	
	//  Le MenuSplit de gauche et la gauche tout court
	//
	public void pressedStart(ActionEvent event) {   //appui sur le gros bouton
		Main.start();
	}
	
	public void pressedSub(ActionEvent event) {
		Main.initNewSub();
	}
	
	public void pressedRename(ActionEvent event) {
		Main.rename(false);
	}
	
	public void pressedDelete(ActionEvent event) {
		
		if(Main.nouveau) {
			Main.cancelNewProject();
		} else if(Main.current!=null) {
			Main.currentDelete();
		}
	}
	
	public void pressedNewProject(ActionEvent event) {
		Main.initNewProject();
	}
	
	public void requestReText(ActionEvent event) {
		Main.reText();
	}
	
	// le code
	public void leftEscape(KeyEvent event) {
		//System.out.println("Maintenant");
		if(event.getCode().equals(KeyCode.ESCAPE)) {
			Main.cancelNewProject();
		}
	}
	
	
	
	public void demarrer() {
		startMenu.setText("Démarrer");
		playBtn.setText("Démarrer");
	}
	
	public void reprendre() {
		startMenu.setText("Reprendre");
		playBtn.setText("Reprendre");
	}
	
	public void suspendre() {
		startMenu.setText("Suspendre");
		playBtn.setText("Suspendre");
	}
	
	
	
	public String getReTextFld() {
		return reTextFld.getText();
	}
	
	public String getRenameFld() {
		return renameFld.getText();
	}
	
	public String getNameLbl() {
		return nameLbl.getText();
	}
	
	public String getTextLbl() {
		return textLbl.getText();
	}
	
	public String getSelectedReTextFld() {
		return selectedReTextFld.getText();
	}
	
	public String getSelectedRenameFld() {
		return selectedRenameFld.getText();
	}
	
	public String getTempReText() {
		return reTextFld.getText();
	}
	
	public String getTempRename() {
		return renameFld.getText();
	}
	
	
	
	public void renameDisplay() {
		renameBtn.setDisable(true);
		if(!Main.firstName) {
			renameFld.setText(nameLbl.getText());
			renameFld.setPromptText("Nouveau nom");
			if(!Main.nouveau&&!Main.sub) {
				startMenu.setText("Renommer");
				playBtn.setText("Renommer");
			} else {
				startMenu.setText("Annuler");
				playBtn.setText("Annuler");
			}
		}
		else if(!Main.sub) renameFld.setPromptText("Futur nom du Projet");
		else renameFld.setPromptText("Futur nom de la tâche");
		renameFld.setVisible(true);
		renameFld.setDisable(false);
		renameFld.requestFocus();
	}
	
	public void endRename(ActionEvent event) {
		if(renameFld.getText()!=null&&!renameFld.getText().equals("")) {
			renameBtn.setDisable(false);
			renameFld.setDisable(true);
			renameFld.setVisible(false);
			if(Main.nouveau||Main.sub) {
				startMenu.setDisable(false);
				newProjectBtn.setDisable(false);
				subBtn.setDisable(false);
				nameLbl.setText(renameFld.getText());
				startMenu.setText("Enregistrer");
			}
			Main.endRename();
			renameFld.setText("");
			//arborescence.refresh()
		}
	}
	
	public void cancelRenameRequested(KeyEvent event) {
		if(event.getCode().equals(KeyCode.ESCAPE)) {
			if(Main.nouveau||Main.sub) {
				if(!Main.renaming)Main.cancelNewProject();
				else Main.cancelRename();
			} else if(Main.renaming)Main.cancelRename();
		}
	}
	
	
	
	public void reTextDisplay() {
		if(!Main.nouveau&&!Main.sub) {
			reTextFld.setText(Main.current.getText());
			reTextFld.setPromptText("Notes, commentaires...");
		}
		else {
			reTextFld.setText(textLbl.getText());
			reTextFld.setPromptText("Texte de présentation, optionel");
		}
		reTextFld.setDisable(false);
		reTextFld.setVisible(true);
		reTextFld.requestFocus();
	}
	
	public void endReTextRequest(KeyEvent event) {
		if(event.getCode().equals(KeyCode.ENTER)) {
			if(!Main.nouveau&&!Main.sub) {
				Main.endReText();
				reTextFld.setDisable(true);
				reTextFld.setVisible(false);
				reTextFld.setText("");
			} else {
				textLbl.setText(reTextFld.getText());
				reTextFld.setDisable(true);
				reTextFld.setVisible(false);
				reTextFld.setText("");
				//System.out.println(reTextFld.getText());
			}
		} else if(event.getCode().equals(KeyCode.ESCAPE)) {
			reTextFld.setDisable(true);
			reTextFld.setVisible(false);
			reTextFld.setText("");
		}
	}
	
	
	public void initNewProjectDisplay() {
		
		nameLbl.setDisable(false);
		nameLbl.setText("");
		textLbl.setDisable(false);
		textLbl.setText("");
		reTextDisplay();
		renameFld.requestFocus();
		projectNameLbl.setText("");
		chronoLbl.setText("00:00:00");
		chronoLbl.setDisable(true);
	
		startMenu.setText("Annuler");
		startMenu.setDisable(false);
		playBtn.setText("Démarrer");
		playBtn.setDisable(true);
	
		subBtn.setDisable(true);
		newProjectBtn.setDisable(true);
		subBtn.setDisable(true);
		deleteBtn.setText("Annuler");
		deleteBtn.setDisable(false);
		VBoxRight.setDisable(true);
		
		startDateLbl.setText(toDay);
	}
	
	public void endCreateDisplay() {
		closeLeft();
		VBoxRight.setDisable(false);
	    updateSelection();
		displayRefresh();
		
	}
	
	public void cancelNewProjectDisplay(){
		closeLeft();
		VBoxRight.setDisable(false);
		displayRefresh();
	}
	
	
	
	// Les boutons (et actions) de droite
	//
	
	public void pressedSave(ActionEvent event) {
		Main.pause();
		Main.save();
	}
	
	public void pressedSelectedDelete(ActionEvent event) {
		Main.selectedDelete();
	}
	
	public void pressedSelectedRename(ActionEvent event) {
		selectedRename();
	}
	
	public void requestSelectedReText(ActionEvent event) {
		selectedReText();
	}
	
	// le code derrière
	public void updateSelection() {  //pour mettre a jour l'item selectionné
		closeRight();
		if(Main.selected==null) {
			//System.out.println("current selected == null");
			Main.unSelect();
		} else {
			
			selectedTimeLlb.setDisable(false);
			selectedTimeLlb.setText(displayTime(Main.selectedProject.getTotalTime()).substring(0, 5));
			loadBtn.setDisable(false);
			rightMenu.setDisable(false);
			selectedNameLbl.setText(Main.selected.getName());
			if(Main.selected==Main.selectedProject) {
				selectedProjectNameLbl.setDisable(true);
			} else {
				selectedProjectNameLbl.setDisable(false);
			}
			selectedProjectNameLbl.setText(Main.selectedProject.getName());
			selectedTextLbl.setText(Main.selected.getText());
			selectedTextLbl.setDisable(false);
		}
	}
	
	public void unSelectDisplay() { // pour déselectionner l'item
		Main.selected = null;
		Main.selectedTreeItem = null;
		Main.selectedProject = null;
		loadBtn.setDisable(true);
		rightMenu.setDisable(true);
		selectedTimeLlb.setDisable(true);
		selectedTimeLlb.setText("Aucun Projet sélectioné");
		selectedNameLbl.setText("");
		selectedProjectNameLbl.setDisable(false);
		selectedProjectNameLbl.setDisable(false);
		selectedProjectNameLbl.setText("");
		selectedTextLbl.setText("");
		selectedTextLbl.setDisable(true);
		arborescence.getSelectionModel().clearSelection();
	}
	
	public void selectedReText() {
		selectedReTextFld.setText(selectedTextLbl.getText());
		selectedReTextFld.setDisable(false);
		selectedReTextFld.setVisible(true);
		selectedReTextFld.requestFocus();
	}

	public void endSelectedReText(KeyEvent event) {
		if(event.getCode().equals(KeyCode.ENTER)) {
			selectedReTextFld.setDisable(true);
			selectedReTextFld.setVisible(false);
			Main.endSelectedReText();
			selectedReTextFld.setText("");
			updateSelection();
		} else if(event.getCode().equals(KeyCode.ESCAPE)) {
			selectedReTextFld.setDisable(true);
			selectedReTextFld.setVisible(false);
			selectedReTextFld.setText("");
		}
	}
	
	public void selectedRename() {
		selectedRenameBtn.setDisable(true);
		selectedRenameFld.setText(selectedNameLbl.getText());
		selectedRenameFld.setVisible(true);
		selectedRenameFld.setDisable(false);
		selectedRenameFld.requestFocus();
	}
	
	public void endRenameSelected(ActionEvent event){
		selectedRenameBtn.setDisable(false);
		selectedRenameFld.setDisable(true);
		selectedRenameFld.setVisible(false);
		Main.endSelectedRename();
		updateSelection();
		renameFld.setText("");
	}
	
	public void loadSelectedRequest(ActionEvent event) {
		Main.loadSelected();
	}
	
	
	// Affichage, Rafraichissement etc
	//
	public void chronoRefresh() {
		chronoLbl.setText(displayTime(Main.time+Main.chrono));
	}
	
	public void displayDisabled() {  //quand rien n'est chargé
		nameLbl.setText("Aucun Projet sélectionné");
		nameLbl.setDisable(true);
		projectNameLbl.setText("Vous pouvez en choisir un dans la liste à droite,\nou en créer un nouveau");
		projectNameLbl.setDisable(false);
		chronoLbl.setText("00:00:00");
		textLbl.setText("");
		textLbl.setDisable(true);
		startDateLbl.setText("Aucun Projet sélectionné");
		projectTimeLbl.setText("00:00");
		
		chronoLbl.setDisable(true);
		projectTimeLbl.setDisable(true);
		totalLbl.setDisable(true);
		startMenu.setDisable(false);
		startMenu.setText("Nouveau Projet");
		renameBtn.setDisable(true);
		playBtn.setDisable(true);
		subBtn.setDisable(true);
		newProjectBtn.setDisable(false);
		subBtn.setDisable(true);
		deleteBtn.setText("supprimer");
		deleteBtn.setDisable(true);
	}
	
	public void displayRefresh() {
		
		if(Main.current==null)displayDisabled();
		else {
			Main.refreshCurrent();
			toDay = "Nous somme le "+getStartDate(LocalDate.now());
			nameLbl.setText(Main.current.getName());
			nameLbl.setDisable(false);
			projectNameLbl.setText(Main.currentProject.getName());
			projectTimeLbl.setDisable(false);
			totalLbl.setDisable(false);
			if(Main.current==Main.currentProject) {
				projectNameLbl.setDisable(true);
			}
			else {
				projectNameLbl.setDisable(false);
			}
			
			projectTimeLbl.setText(displayTime(Main.currentProject.getTotalTime()).substring(0, 5));
			chronoLbl.setDisable(false);
			startMenu.setDisable(false);
			textLbl.setDisable(false);
			
			
			chronoLbl.setText(displayTime(Main.time+Main.chrono));
			textLbl.setText(Main.current.getText());
			startDateLbl.setText(startedAt(Main.startDate));
			newProjectBtn.setDisable(false);
			subBtn.setDisable(false);
			deleteBtn.setText("supprimer");
			deleteBtn.setDisable(false);
		}
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
	
	public void refreshTimeLbl() {
		projectTimeLbl.setText(displayTime(Main.currentProject.getTotalTime()).substring(0, 5));
	}
	
	// Pour fermer les champs de saisies
	public void closeLeft() {
		renameBtn.setDisable(false);
		renameFld.setDisable(true);
		renameFld.setVisible(false);
		renameFld.setText("");
		
		reTextFld.setDisable(true);
		reTextFld.setVisible(false);
		reTextFld.setText("");
	}
	
	public void closeRight() {
		selectedRenameBtn.setDisable(false);
		selectedRenameFld.setDisable(true);
		selectedRenameFld.setVisible(false);
		selectedRenameFld.setText("");
		
		selectedReTextFld.setDisable(true);
		selectedReTextFld.setVisible(false);
		selectedReTextFld.setText("");
	}

	public String startedAt(String startDate) {
		return "Commencé le "+startDate;
	}
	
	public String displayTime(long time) {
		int heure = (int)(((time%86400)-(time%3600))/3600);
		int minute = (int)(((time%3600)-(time%60))/60);
		int sec = (int)(time%60);
		StringBuilder sb = new StringBuilder();
		if(heure<10)sb.append(""+0);
		sb.append(""+heure+":");
		if(minute<10)sb.append(""+0);
		sb.append(""+minute+":");
		if(sec<10)sb.append(""+0);
		sb.append(""+sec);
		return sb.toString();
	}
	
	public long getTotalTime(Tache t) {  //ne sert qu'avec les sous tache  TODO
		long idx = 0;
		idx+=t.getTime();
		for (Tache child : t.getChildren()) {
			idx+=getTotalTime(child);
		}
		//System.out.println(idx);
		return idx;
	}
	
	
	
	
	// Tout ce qui est save etc.
	//
	/*
	public void updateFile(){
		
		File path = new File(Main.res);
		String[] filelist = path.list();
		filteredFileList = new ArrayList<String>();
		if (filelist != null) {
			for (String fichier:filelist) {
				File f = new File(Main.res+fichier);
				if (fichier.charAt(0) != '.'&&f.isFile()&&!fichier.equals("icone.png")) {
					//System.out.println(fichier);
					filteredFileList.add(fichier);
				}
			}
		}
		
		rootItem = new TreeItem<Tache>(null);
	    rootItem.setExpanded(true);
	    
	    for (String fichier:filteredFileList) {
	    	//System.out.println(loadBranche(fichier).getName());
	    	Tache toTest;
			try {
				toTest = loadObject(fichier);
				if(toTest!=null) {
					TreeItem<Tache> item = new TreeItem<Tache>(toTest);
					rootItem.getChildren().add(item);
					addChild(item);
			    }
			} catch (ClassNotFoundException | IOException e) {
				System.out.println("Pb lors de l'ouverture du fichier");
				e.printStackTrace();
			}
		}
	    arborescence.setRoot(rootItem);
	    arborescence.refresh();
	    unSelect();
	}*/
	
	
	
	/*
	public void loadSelected() { //permet de charger à gauche l'objet sélctionner a droite
		Main.pause();
		closeLeft();
		save();
		Main.currentTreeItem = Main.selectedTreeItem;
		Main.current = Main.currentTreeItem.getValue();
		Main.currentProject = getRoot(Main.currentTreeItem).getValue();
		
		//System.out.println(Main.currentProject.getName());
		Main.time = Main.current.getTime();
		Main.startDate = Main.current.getStartDate();
		displayRefresh();
		Main.pause();
	}
	*/
	
	/*
	public void load(TreeItem<Tache> item) { //permet de charger n'importe lequel
		Main.current = item.getValue();
		arborescence.getSelectionModel().select(item);
		Main.currentTreeItem = arborescence.getSelectionModel().getSelectedItem();
		Main.currentProject = getRoot(Main.currentTreeItem).getValue();
	}
	*/
	/*
	public void save() {  //permet de sauvegarder la tache racine (currentProject)
		
		if(Main.current!=null) {
			Main.chronoStack();
			Main.current.setTime(Main.time);
			Main.currentTreeItem.setValue(Main.current);
			
			projectTimeLbl.setText(displayTime(Main.currentProject.getTotalTime()).substring(0, 5));
			if(Main.currentProject.getFileName()==null) {
				Main.currentProject.setFileName(Main.makeFileName(Main.currentProject));
			}
			
			try {
				Main.saveObject(Main.currentProject);
			} catch (IOException e) {
				System.out.println("sa ne s'enregistre pas");
				e.printStackTrace();
			}
			//updateFile(); update file reset l'arbre donc la selection aussi
			arborescence.refresh();
			updateSelection();
		}
	}*/
	
	
	
	
}

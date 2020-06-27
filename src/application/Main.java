package application;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {
	
	static String res = "data"+File.separator;
	
	static ChronoControlleur display;
	static DeleteControlleur delete;
	
    static long time = 0;
	static long chrono;
	static boolean play;
	static Timeline timeline;
	static String startDate;
	static boolean nouveau;
	static boolean renaming;
	static boolean sub;
	static boolean deletecurrent;
	static boolean firstName;
	static String tempReText;
	static String tempRename;
	static ArrayList<String> filteredFileList;
	
	
	
	static TreeItem<Tache> rootItem;
	static TreeItem<Tache> currentTreeItem;
	static TreeItem<Tache> selectedTreeItem;
	
	static Tache currentProject;   //Les Projets
	static Tache selectedProject;
	
	static Tache current;    //Les Taches
	static Tache selected;
    
	static int stateWindow = 1;
	static SystemTray tray;
	static TrayIcon trayIcon;
	static java.awt.MenuItem aboutItem;
	
	static Stage noName = new Stage();
	static Stage deleteWarning = new Stage();
	
	
	
	@Override
	public void start(final Stage stage) throws Exception {
		
		createAndShowGUI(stage);
		javafx.scene.image.Image ico = new javafx.scene.image.Image("/icone.png");
		stage.getIcons().add(ico);
		// A la base il fallait double cliquésur l'icone pour ouvrir l'interface utilisateur
        // avec ça il suffit d'un simple clic
		
		trayIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if (event.getButton() == MouseEvent.BUTTON1) {
					afficherGestionnaire(stage);
        		}
    		}
		});
		aboutItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
            	afficherGestionnaire(stage);
            }
        });

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/Interface.fxml"));
		Parent root = loader.load();
		display = (ChronoControlleur) loader.getController();
		
		FXMLLoader loaderNoName = new FXMLLoader();
		loaderNoName.setLocation(getClass().getResource("/NoName.fxml"));
		noName.setScene(new Scene(loaderNoName.load()));
		noName.initOwner(stage);
		noName.initModality(Modality.WINDOW_MODAL);
		
		FXMLLoader loaderDeleteWarning = new FXMLLoader();
		loaderDeleteWarning.setLocation(getClass().getResource("/DeleteWarning.fxml"));
		delete = (DeleteControlleur) loaderDeleteWarning.getController();
		deleteWarning.setScene(new Scene(loaderDeleteWarning.load()));
		
		deleteWarning.getIcons().add(ico);
		deleteWarning.setTitle("Suppression de la tâche");
		deleteWarning.setResizable(false);
		deleteWarning.initOwner(stage);
		deleteWarning.initModality(Modality.WINDOW_MODAL);
		
		
		
		

    	File f;
		
		/*     //Peut être utile si l'on veut utilisé le dossier mes documents comme base
		FileSystemView fsv = FileSystemView.getFileSystemView();
		f = fsv.getDefaultDirectory();
		//System.out.println(f.toString()+File.separator+"Gestionnaire de temps citronné"+File.separator+"res");
		res = f.toString()+File.separator+"Gestionnaire de temps citronné"+File.separator+"data"+File.separator;
		*/
		
		f = new File(Main.res);
		f.mkdirs();
		
		//testFile();
		
		Main.nouveau = false;
		Main.play = false;
		Main.renaming = false;
		Main.sub = false;
		
		updateFile();
		display.displayDisabled();
		
		Main.timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
			Main.chrono++;
		    display.chronoRefresh();
		   	if((Main.chrono+Main.time)%60==0) {
		   		save();
		   	}
		}));
		Main.timeline.setCycleCount(Animation.INDEFINITE);
    	
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Gestionnaire Taché");
		Platform.setImplicitExit(false);  //
		stage.setResizable(false);
    	stage.show();
	}
	
	public void testFile() {
		Tache heho = new Tache("Bouh", "Wabba Labba Dub Dub",0);
		//System.out.println(heho.getFileName());
		try {
			saveObject(heho);
		} catch (IOException e) {
			System.out.println("Pb avec la tache de test");
			e.printStackTrace();
		}
	}

	public static void pause() {
		timeline.pause();
		play = false;
		chronoStack();
		if(time==0) {
			display.demarrer();
		} else {
			display.reprendre();
		}
	}
	
	public static void play() {
		timeline.play();
		play = true;
		display.suspendre();	
	}
	
	public static void start() {
		if(Main.nouveau||Main.sub) {
			if(firstName)cancelNewProject();
			else if(Main.renaming)cancelNewProject();
			else {
				//System.out.println(textLbl.getText());
				endCreate();
			}
		} else if (Main.renaming) {
			endRename();
		} else if(Main.play) {
			Main.pause();
			save();
		} else if(Main.current==null) {
			nouveau=true;
			initNewProject();
		} else {
			Main.play();
		}
	}
	
	
	public static void rename(boolean initNew) {
		Main.pause();
		Main.renaming = true;
		if(initNew) {
			firstName = true;
		} else {
			save();
		}
		display.renameDisplay();
	}
	
	public static void endRename() {
		if(!firstName) {
			if(!nouveau&&!sub) {
				Main.current.setName(display.getRenameFld());
				save();
				display.displayRefresh();
				//pause();
				if(Main.play)Main.play();
				else Main.pause();
			} else {
				tempRename = display.getTempRename();
			}
		} else {
			tempRename = display.getTempRename();
		}
		firstName = false;
		Main.renaming = false;
		
	}
	
	public static void cancelRename() {
		Main.renaming = false;
		display.closeLeft();
		Main.pause();
	}
	
	
	public static void reText() {
		display.reTextDisplay();
	}
	
	public static void endReText() {
		if(!nouveau&&!sub) {
			Main.current.setText(display.getReTextFld());
			if(Main.play)Main.play();
			else Main.pause();
			save();
			display.displayRefresh();
		} else tempReText = display.getTempReText();
	}
	
	
	public static void endSelectedReText() {
		if(Main.currentProject.getFileName()==Main.selectedProject.getFileName()) {
			Main.current.setText(display.getSelectedReTextFld());
			save();
			display.displayRefresh();
			
		} else {
			
			Main.selected.setText(display.getSelectedReTextFld());
			try {
				Main.saveObject(Main.selectedProject);
			} catch (IOException e) {
				System.out.println("Un pb lors de l'enregistrement !!");
				e.printStackTrace();
			}
			display.arborescence.refresh();
			
		}
	}
	
	public static void endSelectedRename() {
		
		if(Main.current.getFileName()==Main.selected.getFileName()) {
			Main.current.setName(display.getSelectedRenameFld());
			save();
			display.displayRefresh();
		} else {
			Main.selected.setName(display.getSelectedRenameFld());
			try {
				Main.saveObject(Main.selectedProject);
			} catch (IOException e) {
				System.out.println("Un pb lors de l'enregistrement !!");
				e.printStackTrace();
			}
			display.arborescence.refresh();
		}
	}

	
	public static void chronoStack() { //permet d'additionner les variable chrono et time (time étant la variable directement chargé depuis le projet)
		time+=chrono;
		chrono = 0;
	}
	
	
	public static void initNewProject() {
		Main.pause();
		cancelRename();
		save();
		Main.nouveau = true;
		rename(true);
		
		display.initNewProjectDisplay();
	}
	
	public static void initNewSub() {
		Main.pause();
		cancelRename();
		save();
		Main.sub = true;
		rename(true);
		
		display.initNewProjectDisplay();
	}
	
	public static void endCreate() {
		if(display.getNameLbl().equals("")) {
			Main.noName.showAndWait();
		} else {
			Tache nouvelle;
			time = 0;
			if(Main.nouveau) {
				if(display.getTextLbl().equals("")) {
					nouvelle = new Tache(display.getNameLbl());
				} else {
					nouvelle = new Tache(display.getNameLbl(),display.getTextLbl(),0);
				}
				Tache nouveauProjet = nouvelle;
				Main.currentProject = nouveauProjet;
				
				TreeItem<Tache> item = new TreeItem<Tache>(nouvelle);
				Main.rootItem.getChildren().add(item);
		    	load(item);
		    	
			} else {
				nouvelle = new Tache(display.getNameLbl(),display.getTextLbl(),Main.current.getLevel()+1);
				Main.current.addChild(nouvelle);
				TreeItem<Tache> item = new TreeItem<Tache>(nouvelle);
				Main.currentTreeItem.getChildren().add(item);
				load(item);
			}
			//System.out.println(currentProject.getName());
			save();
			
			Main.pause();
			display.endCreateDisplay();
			Main.sub = false;
			Main.nouveau = false;
		}
	}
	
	public static void cancelNewProject() {
		Main.nouveau = false;
		Main.sub = false;
		Main.pause();
		display.cancelNewProjectDisplay();
	}
	
	
	public static void selectedDelete() {
		Main.deletecurrent = false;
		confirmDelete();
	}
		
	public static void currentDelete() {
		Main.deletecurrent = true;
		confirmDelete();
	}
	
	public static void confirmDelete() {
		deleteWarning.show();
	}
	
	public static void closeWarning() {
		Main.deleteWarning.close();
	}
	
	public static void deleteProject() {
		// oui le bouton n'est pas beau
		closeWarning();
		if(Main.deletecurrent) {
			pause();
			if(Main.current==Main.currentProject) {
				String fileName = Main.currentProject.getFileName();
				Main.current=null;
				File toDeleteFile = new File(Main.res+fileName);
				toDeleteFile.delete();
				Main.updateFile();
			} else {
				save();
				TreeItem<Tache> newOne = currentTreeItem.getParent();
				
				newOne.getValue().children.remove(Main.current);
				newOne.getChildren().remove(currentTreeItem);
				
				Main.currentTreeItem = newOne;
				Main.current = currentTreeItem.getValue();
				Main.time=Main.current.getTime();
				unSelect();
				display.updateSelection();
				save();
			}
		} else {
			if(Main.selected==Main.selectedProject) {
				String fileName = Main.selectedProject.getFileName();
				if(isLeafOf(Main.currentTreeItem,Main.selectedTreeItem)) {
					Main.current=null;
				}
				Main.selected=null;
				File toDeleteFile = new File(Main.res+fileName);
				toDeleteFile.delete();
			} else {
				save();
				if(isLeafOf(Main.currentTreeItem,Main.selectedTreeItem)) {
					Main.current=null;
				}
				Main.selectedTreeItem.getParent().getValue().children.remove(Main.selected);
				Main.selected = Main.selectedTreeItem.getParent().getValue();
				Main.selectedTreeItem = Main.selectedTreeItem.getParent();
				try {
					Main.saveObject(Main.selectedProject);
				} catch (IOException e) {
					System.out.println("Un pb lors de l'enregistrement !!");
					e.printStackTrace();
				}
				load(Main.selectedTreeItem);
				display.arborescence.refresh();
				display.updateSelection();
			}
		}
		//Main.updateFile();
		display.arborescence.refresh();
		display.updateSelection();
		display.displayRefresh();
		Main.deletecurrent = false;
		
	}
	
	
	
	public static void updateFile() {
		
		File path = new File(res);
		String[] filelist = path.list();
		filteredFileList = new ArrayList<String>();
		if (filelist != null) {
			for (String fichier:filelist) {
				File f = new File(res+fichier);
				if (fichier.charAt(0) != '.'&&f.isFile()&&!fichier.equals("icone.png")) {
					//System.out.println(fichier);
					filteredFileList.add(fichier);
				}
			}
		}
		rootItem = new TreeItem<Tache>(new Tache());
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
					//System.out.println(rootItem);
			    }
			} catch (ClassNotFoundException | IOException e) {
				System.out.println("Pb lors de l'ouverture du fichier");
				e.printStackTrace();
			}
		}
	    //System.out.println(rootItem);
	    display.arborescence.setRoot(rootItem);
	    display.arborescence.refresh();
	    unSelect();
	}
	
	public static void unSelect() {
		Main.selected = null;
		Main.selectedTreeItem = null;
		Main.selectedProject = null;
		display.unSelectDisplay();
	}
	
	public static void refreshCurrent() {
		Main.chronoStack();
		Main.time=Main.current.getTime();
		Main.startDate=Main.current.getStartDate();
	}
	
	
	public static void addChild(TreeItem<Tache> item) {
		for (Tache children : item.getValue().getChildren()) {
			TreeItem<Tache> child = new TreeItem<Tache>(children);
			item.getChildren().add(child);
			addChild(child);
			//System.out.println(item);
		}
	}

	public static boolean isLeafOf(TreeItem<Tache> test, TreeItem<Tache> base) {
		if(test==null)return false;
		if(test.getValue()==base.getValue())return true;
		else if(test.getValue().getLevel()==0)return false;
		else return isLeafOf(test.getParent(), base);
	}
	
	public static TreeItem<Tache> getRoot(TreeItem<Tache> item) {
		//System.out.println(item.getParent());
		if(item.getParent()==Main.rootItem)return item;
		else return getRoot(item.getParent());
	}
	
	
	public static void loadSelected() { //permet de charger à gauche l'objet sélctionner a droite
		Main.pause();
		display.closeLeft();
		save();
		Main.currentTreeItem = Main.selectedTreeItem;
		Main.current = Main.currentTreeItem.getValue();
		Main.currentProject = getRoot(Main.currentTreeItem).getValue();
		
		//System.out.println(Main.currentProject.getName());
		Main.time = Main.current.getTime();
		Main.startDate = Main.current.getStartDate();
		display.displayRefresh();
		Main.pause();
	}
	
	public static void load(TreeItem<Tache> item) { //permet de charger n'importe lequel
		Main.current = item.getValue();
		display.arborescence.getSelectionModel().select(item);
		Main.currentTreeItem = display.arborescence.getSelectionModel().getSelectedItem();
		Main.currentProject = getRoot(Main.currentTreeItem).getValue();
	}
	
	public static void save() {  //permet de sauvegarder la tache racine (currentProject)
		
		if(Main.current!=null) {
			Main.chronoStack();
			Main.current.setTime(Main.time);
			Main.currentTreeItem.setValue(Main.current);
			
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
			display.refreshTimeLbl();
			display.arborescence.refresh();
			display.updateSelection();
		}
	}
	
	private static void saveObject(Tache proj,String fileName) throws IOException  {
		if(proj.getFileName()==null)proj.setFileName(fileName);
		
		FileOutputStream fos = new FileOutputStream(res+fileName);
	    XMLEncoder encoder = new XMLEncoder(fos);
	    encoder.setExceptionListener(new ExceptionListener() {
	            public void exceptionThrown(Exception e) {
	                System.out.println("Exception! :"+e.toString());
	            }
	    });
	    File truc = new File(res+fileName);
	    if(truc.exists()&&truc.isFile()) {
			//System.out.println(t.getFiletName());
	    	truc.delete();
		}
	    encoder.writeObject(proj);
	    encoder.close();
	    fos.close();
		
	}
	
	static void saveObject(Tache proj) throws IOException  {
		//System.out.println("étape 2");
		if(proj.getFileName()==null)saveObject(proj, makeFileName(proj));
		else saveObject(proj, proj.getFileName());
	}
	
	public static String makeFileName(Tache proj) {
		String res;
		int idx = 0;
		
		File f;
		do {
			if(proj.getName().length()<6)res = proj.getName()+idx+".proj";
			else res = proj.getName().substring(0, 6)+idx+".proj";
			f = new File(Main.res+res);
			idx++;
			//System.out.println("test");
		} while(f.exists());
		
		return res;
	}
	
	private static Tache loadObject(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
		
		FileInputStream fis = new FileInputStream(res+fileName);
	    XMLDecoder decoder = new XMLDecoder(fis);
	    Object tmp;
		Tache tache = new Tache();
		tmp = decoder.readObject();
		decoder.close();
		if(tmp instanceof Tache) {
			tache = (Tache) tmp;
			fis.close();
			tache.setFileName(fileName);
			return tache;
		} else return null;
		
	}
	
	
	
	public void closeNoName() {
		noName.close();
	}
	
	public void afficherGestionnaire(final Stage stage) {
		Platform.runLater(new Runnable() {
				@Override
				public void run() {
					stage.show();
				}
			});
	}
	
	// Il y a bien un tuto app in tray sur moodle, mais lorsque j'ai essayer de comprendre ce qu'il en était, j'ai trouver un squelette à peu près identique � cette adresse
	// https://stackoverflow.com/questions/40571199/creating-tray-icon-using-javafx
	// squelette que j'ai donc utilisé car je l'ai trouvé plus simple d'utilisation et de compréhension,il faut donc considérer que je l'ai utilisée la place de celui dont le lien est sur moodle
	// je l'ai bien sur largement modifi�.
	private static void createAndShowGUI(final Stage stage) {
		
		//Check the SystemTray is supported      oui je ne traduis pas
		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
		    return;
		}
		final PopupMenu popup = new PopupMenu();
		trayIcon = new TrayIcon(createImage("/icone.png", "tray icon"));
		tray = SystemTray.getSystemTray();
		
		trayIcon.setImageAutoSize(true);
		trayIcon.setToolTip("Gestionnaire Camusien");
		
		// les menu item
		aboutItem = new java.awt.MenuItem("Gestionnaire de projets");
		/*java.awt.Menu displayMenu = new java.awt.Menu("Afficher un message test");
		java.awt.MenuItem errorItem = new java.awt.MenuItem("Erreur");
		java.awt.MenuItem warningItem = new java.awt.MenuItem("Warning");
		java.awt.MenuItem infoItem = new java.awt.MenuItem("Info");
		java.awt.MenuItem noneItem = new java.awt.MenuItem("Lambda");*/
		java.awt.MenuItem exitItem = new java.awt.MenuItem("Quitter");
		
	
		popup.add(aboutItem);
	    popup.addSeparator();
	    /*popup.add(displayMenu);
	    displayMenu.add(errorItem);
	    displayMenu.add(warningItem);
	    displayMenu.add(infoItem);
	    displayMenu.add(noneItem);*/
	    popup.add(exitItem);
	    
	    trayIcon.setPopupMenu(popup);
	    
	    try {
	    	tray.add(trayIcon);
	    } catch (AWTException e) {
	    	System.out.println("L'îcone de la barre des taches n'a pas pu être ajoutée"); 
	        return;
	    }
	    
	    // pour les tests uniquements donc je n'y ai pas trop touché
	    /*ActionListener listener = new ActionListener() {
	    	public void actionPerformed(java.awt.event.ActionEvent e) {
	    		java.awt.MenuItem item = (java.awt.MenuItem)e.getSource();
		         	System.out.println(item.getLabel());
		                
		           if ("Erreur".equals(item.getLabel())) {
		        	   trayIcon.displayMessage("Sun TrayIcon Demo",
		        			   "C'est pour une future implémentation", TrayIcon.MessageType.ERROR);
		        	   
		           } else if ("Warning".equals(item.getLabel())) {
		        	   trayIcon.displayMessage("Sun TrayIcon Demo",
		        			   "C'est pour une future implémentation", TrayIcon.MessageType.WARNING);
		        	   
		           } else if ("Info".equals(item.getLabel())) {
		        	   trayIcon.displayMessage("Sun TrayIcon Demo",
		        			   "C'est pour une future implémentation", TrayIcon.MessageType.INFO);
		        	   
		           } else if ("Lambda".equals(item.getLabel())) {
		        	   trayIcon.displayMessage("Sun TrayIcon Demo",
		        			   "C'est pour une future implémentation", TrayIcon.MessageType.NONE);
		           }
	    	}
	    };
	    errorItem.addActionListener(listener);
	    warningItem.addActionListener(listener);	
	    infoItem.addActionListener(listener);
	    noneItem.addActionListener(listener);
		*/   
	    exitItem.addActionListener(new ActionListener() {
	    	public void actionPerformed(java.awt.event.ActionEvent e) {
	    		tray.remove(trayIcon);
	    		System.exit(0);
	    	}
	    });    
	
	}

	public static void main(String[] args) {
		launch(args);
	}

	//Cette partie du code vient aussi du tuto pour l'AppToTray,
	// il ne s'agit donc pas de mon travail
	protected static Image createImage(String path, String description) {
    	URL imageURL = Main.class.getResource(path);
    
    	if (imageURL == null) {
    		System.err.println("Pas trouvé " + path);
    		return null;
    	} else {
    	return (new ImageIcon(imageURL, description)).getImage();
    	}
	}

}
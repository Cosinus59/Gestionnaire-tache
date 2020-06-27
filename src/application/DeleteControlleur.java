package application;

import javafx.event.ActionEvent;

public class DeleteControlleur {
	
	public void initialize() {
	}

	public void deleteConfirmed(ActionEvent event) {
		Main.deleteProject();
	}
	
	public void cancelConfirmed(ActionEvent event) {
		Main.closeWarning();
	}
}

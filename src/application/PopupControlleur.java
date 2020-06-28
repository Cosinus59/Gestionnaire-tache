package application;

import javafx.event.ActionEvent;

public class PopupControlleur {
	
	public void initialize() {
	}

	public void answerYes(ActionEvent event) {
		Main.answerYes();
	}
	
	public void answerNo(ActionEvent event) {
		Main.closeWarning();
	}
}

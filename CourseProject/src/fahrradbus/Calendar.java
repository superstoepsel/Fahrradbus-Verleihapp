package fahrradbus;

import java.util.List;
import java.time.LocalDate;
import java.util.Locale;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
  
public class Calendar {
  
	private List<Bus> busse;
 
    private DatePicker entleihenDatePicker;
    private DatePicker rückgabeDatePicker;
    private ComboBox<String> anzahlPlätze;
    private Button buchen = new Button("Verbindlich buchen");
    
    public Calendar(List<Bus> busse) {
    	this.busse = busse;
    }
  
    public Scene getScene() {
	    Locale.setDefault(Locale.US); 
    
        VBox vbox = new VBox(20);
        vbox.setStyle("-fx-padding: 10;");
        Scene scene = new Scene(vbox, 400, 400);
        entleihenDatePicker = new DatePicker();
        rückgabeDatePicker = new DatePicker();
        entleihenDatePicker.setValue(LocalDate.now());
        rückgabeDatePicker.setValue(entleihenDatePicker.getValue().plusDays(1));
        rückgabeDatePicker.hide();
        
        buchen.setStyle("-fx-background-color: #194ea0; -fx-text-fill: #7892ba");
		buchen.setPrefSize(150, 40);
        
        entleihenDatePicker.valueProperty().addListener((ov, oldValue, newValue) -> {
        rückgabeDatePicker.setValue(null);
        });
        
        entleihenDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()) || isDateBlocked(date));
            }
        });
        
        rückgabeDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(entleihenDatePicker.getValue()) || 
                isDateBlocked(date) || date.isAfter(entleihenDatePicker.getValue().plusDays(2)));
            }
        });
        
        ObservableList<String> anzahlPlätzeList = 
        	    FXCollections.observableArrayList(
        	        "2",
        	        "4",
        	        "6"
        	    );
        anzahlPlätze = new ComboBox<String>(anzahlPlätzeList);
        anzahlPlätze.getSelectionModel().selectFirst();
        
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        Label plätzeLabel = new Label("Gewünschte Anzahl an Plätzen:");
        gridPane.add(plätzeLabel, 0, 0);
        gridPane.add(anzahlPlätze, 0, 1);
        Label entleihlabel = new Label("Entleihdatum:");
        gridPane.add(entleihlabel, 0, 2);
        GridPane.setHalignment(entleihlabel, HPos.LEFT);
        gridPane.add(entleihenDatePicker, 0, 3);
        Label rückgabelabel = new Label("Rückgabedatum:");
        gridPane.add(rückgabelabel, 0, 4);
        GridPane.setHalignment(rückgabelabel, HPos.LEFT);
        gridPane.add(rückgabeDatePicker, 0, 5);
        gridPane.add(buchen, 0, 6);
        vbox.getChildren().add(gridPane);
        
        return scene;
    }
    
    public boolean isDateBlocked(LocalDate date) {
    	String[] dates;
    	int freiePlätze = 6;
    	for (Bus bus : busse) {
    		dates = bus.zeitGeblockt.split(":");
    		if (!(dates.length == 1 && dates[0].equals(""))) {
    			for (int i = 0; i < dates.length; i++) {
    				if (LocalDate.parse(dates[i]).equals(date)) {
    					freiePlätze -= 2;
    				}
    			}
    		}
    	}
    	if (freiePlätze >= Integer.parseInt(anzahlPlätze.getValue())) {
    		return false;
    	}
    	return true;
    }
    
    public void setBuchenEventHandler(EventHandler<ActionEvent> buchenHandler) {
		buchen.setOnAction(buchenHandler);
	}

	public int getAnzahlPlätze() {
		return Integer.parseInt(anzahlPlätze.getValue());
	}

	public String getDates() {
		String dates = entleihenDatePicker.getValue().toString();
		LocalDate datum = entleihenDatePicker.getValue();
		while (!datum.equals(rückgabeDatePicker.getValue())) {
			datum = datum.plusDays(1);
			dates = dates + ":" + datum.toString();
		}
		return dates;
	}
    
    
    
}
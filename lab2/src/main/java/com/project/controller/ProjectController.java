package com.project.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.project.model.Projekt;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.application.Platform;
import com.project.dao.ProjektDAO;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Optional;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import java.util.List;
import java.util.ArrayList;
import javafx.scene.control.TableCell;

public class ProjectController {
	 private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);
	 
	 private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	 private static final DateTimeFormatter dateTimeFormater = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	 //Zmienne do obsługi stronicowania i wyszukiwania
	 private String search4;
	 private Integer pageNo;
	 private Integer pageSize;
	 private ExecutorService wykonawca;
	 private ProjektDAO projektDAO;
	
	 //Automatycznie wstrzykiwane komponenty GUI
	 @FXML
	 private ChoiceBox<Integer> cbPageSizes;
	 @FXML
	 private TableView<Projekt> tblProjekt;
	 @FXML
	 private TableColumn<Projekt, Integer> colId;
	 @FXML
	 private TableColumn<Projekt, String> colNazwa;
	 @FXML
	 private TableColumn<Projekt, String> colOpis;
	 @FXML
	 private TableColumn<Projekt, LocalDateTime> colDataCzasUtworzenia;
	 @FXML
	 private TableColumn<Projekt, LocalDate> colDataOddania;
	 @FXML
	 private TextField txtSzukaj;
	 @FXML
	 private Button btnDalej;
	 @FXML
	 private Button btnWstecz;
	 @FXML
	 private Button btnPierwsza;
	 @FXML
	 private Button btnOstatnia;
	 
	 private ObservableList<Projekt> projekty;
	 
	 public ProjectController(ProjektDAO projektDAO) {
		 this.projektDAO = projektDAO;
		 wykonawca = Executors.newFixedThreadPool(1);// W naszej aplikacji wystarczy jeden wątek do pobierania
		// danych. Przekazanie większej ilości takich zadań do puli
	 } // jednowątkowej powoduje ich kolejkowanie i sukcesywne
		// wykonywanie.

	 @FXML
	 public void initialize() { //Metoda automatycznie wywoływana przez JavaFX zaraz po wstrzyknięciu
		// Inicjalizacja wartości
		    search4 = ""; 
		    pageNo = 0;
		    pageSize = 5;

		    // Ustawienie dostępnych rozmiarów strony
		    cbPageSizes.getItems().addAll(5, 10, 20, 50, 100);
		    cbPageSizes.setValue(pageSize);
		 colId.setCellValueFactory(new PropertyValueFactory<Projekt, Integer>("projektId"));
		 colNazwa.setCellValueFactory(new PropertyValueFactory<Projekt, String>("nazwa"));
		 colOpis.setCellValueFactory(new PropertyValueFactory<Projekt, String>("opis"));
		 colDataCzasUtworzenia.setCellValueFactory(new PropertyValueFactory<Projekt, LocalDateTime>("dataCzasUtworzenia"));
		 colDataOddania.setCellValueFactory(new PropertyValueFactory<Projekt, LocalDate>("dataOddania"));
		 
		//Utworzenie nowej kolumny
		 TableColumn<Projekt, Void> colEdit = new TableColumn<>("Edycja");
			 colEdit.setCellFactory(column -> new TableCell<Projekt, Void>() {
			 private final GridPane pane;
			 { // Blok inicjalizujący w anonimowej klasie wewnętrznej
				 Button btnEdit = new Button("Edycja");
				 Button btnRemove = new Button("Usuń");
				 btnEdit.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
				 btnRemove.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
				 btnEdit.setOnAction(event -> {
					 edytujProjekt(getCurrentProjekt());
				 });
				 btnRemove.setOnAction(event -> {
					 usunProjekt(getCurrentProjekt());
				 });

				 pane = new GridPane();
				 pane.setAlignment(Pos.CENTER);
				 pane.setHgap(10);
				 pane.setVgap(10);
				 pane.setPadding(new Insets(5, 5, 5, 5));
				 pane.add(btnEdit, 0, 0);
				 pane.add(btnRemove, 0, 1);
			 }
			 private Projekt getCurrentProjekt() {
				 int index = this.getTableRow().getIndex();
				 return this.getTableView().getItems().get(index);
			 }
			 @Override
			 protected void updateItem(Void item, boolean empty) {
				 super.updateItem(item, empty);
				 setGraphic(empty ? null : pane);
			 }
		 });
			 
		
			//Dodanie kolumny do tabeli
			 tblProjekt.getColumns().add(colEdit);
			 tblProjekt.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			 colId.setMaxWidth(5000);
			 colNazwa.setMaxWidth(10000);
			 colOpis.setMaxWidth(10000);
			 colDataCzasUtworzenia.setMaxWidth(9000);
			 colDataOddania.setMaxWidth(7000);
			 colEdit.setMaxWidth(15000);

		 
		 colDataCzasUtworzenia.setCellFactory(column -> new TableCell<Projekt, LocalDateTime>() {
			 @Override
			 protected void updateItem(LocalDateTime item, boolean empty) {
				 super.updateItem(item, empty);
				 if (item == null || empty) {
					 setText(null);
				 } else {
					 setText(dateTimeFormater.format(item));
				 }
			 }
		 });
	
		 projekty = FXCollections.observableArrayList();
	
		 //Powiązanie tabeli z listą typu ObservableList przechowującą projekty
		 tblProjekt.setItems(projekty);
		 
		 wykonawca.execute(() -> loadPage(search4, pageNo, pageSize));
	 }
	 
	 private void loadPage(String search4, Integer pageNo, Integer pageSize) {
		    try {
		        final List<Projekt> projektList = new ArrayList<>();
		        int totalRows = projektDAO.getRowsNumber();  // Całkowita liczba rekordów
		        int totalPages = (totalRows - 1) / pageSize;     // Obliczenie liczby stron
		        
		        // Ustal finalną, efektywnie finalną zmienną currentPageNo
		        final int currentPageNo = (pageNo > totalPages) ? totalPages : pageNo;

		        // Ładowanie danych według kryteriów wyszukiwania
		        if (search4 != null && !search4.isEmpty()) {
		            if (search4.matches("\\d+")) {
		                // Wyszukiwanie po identyfikatorze
		                Projekt projekt = projektDAO.getProjekt(Integer.parseInt(search4));
		                if (projekt != null) {
		                    projektList.add(projekt);
		                }
		            } else if (search4.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
		                // Wyszukiwanie po dacie oddania
		                LocalDate dataOddania = LocalDate.parse(search4);
		                projektList.addAll(projektDAO.getProjektyWhereDataOddaniaIs(dataOddania, currentPageNo * pageSize, pageSize));
		            } else {
		                // Wyszukiwanie po nazwie
		                projektList.addAll(projektDAO.getProjektyWhereNazwaLike(search4, currentPageNo * pageSize, pageSize));
		            }
		        } else {
		            projektList.addAll(projektDAO.getProjekty(currentPageNo * pageSize, pageSize));
		        }

		        Platform.runLater(() -> {
		            projekty.clear();
		            projekty.addAll(projektList);
		            // Używamy currentPageNo w lambdach – jest on finalny
		            btnWstecz.setDisable(currentPageNo <= 0);
		            btnDalej.setDisable(currentPageNo >= totalPages);
		            btnPierwsza.setDisable(currentPageNo == 0);
		            btnOstatnia.setDisable(currentPageNo == totalPages);
		        });

		    } catch (RuntimeException e) {
		        String errMsg = "Błąd podczas pobierania listy projektów.";
		        logger.error(errMsg, e);
		        String errDetails = (e.getCause() != null)
		                ? e.getMessage() + "\n" + e.getCause().getMessage()
		                : e.getMessage();
		        Platform.runLater(() -> showError(errMsg, errDetails));
		    }
		}
	 
	   /** Metoda pomocnicza do prezentowania użytkownikowi informacji o błędach */
	   private void showError(String header, String content) {
	       Alert alert = new Alert(AlertType.ERROR);
	       alert.setTitle("Błąd");
	       alert.setHeaderText(header);
	       alert.setContentText(content);
	       alert.showAndWait();
	   }

	   public void shutdown() {
	       if (wykonawca != null) {
	           wykonawca.shutdown();
	           try {
	               if (!wykonawca.awaitTermination(5, TimeUnit.SECONDS)) {
	                   wykonawca.shutdownNow();
	               }
	           } catch (InterruptedException e) {
	               wykonawca.shutdownNow();
	           }
	       }
	   }


	   @FXML
	   private void onActionBtnSzukaj(ActionEvent event) {
	       search4 = txtSzukaj.getText().trim();
	       pageNo = 0;
	       wykonawca.execute(() -> loadPage(search4, pageNo, pageSize));
	   }

	   @FXML
	   private void onActionBtnDalej(ActionEvent event) {
	       pageNo++;  // Increment page number
	       updatePage(pageNo);  // Pass updated pageNo
	   }

	   @FXML
	   private void onActionBtnWstecz(ActionEvent event) {
	       if (pageNo > 0) {
	           pageNo--;  // Decrement page number
	           updatePage(pageNo);  // Pass updated pageNo
	       }
	   }

	   private void updatePage(int pageNo) {
	       int currentPageNo = pageNo; // Capture the value before using in lambda
	       wykonawca.execute(() -> loadPage(search4, currentPageNo, pageSize));  // Use captured value
	   }

	   @FXML
	   private void onActionBtnPierwsza(ActionEvent event) {
	       pageNo = 0;
	       wykonawca.execute(() -> loadPage(search4, pageNo, pageSize));
	   }

	   @FXML
	   private void onActionBtnOstatnia(ActionEvent event) {
	       int totalRows = projektDAO.getRowsNumber();
	       pageNo = (totalRows - 1) / pageSize;
	       wykonawca.execute(() -> loadPage(search4, pageNo, pageSize));
	   }

	   @FXML
	   public void onActionBtnDodaj(ActionEvent event) {
	       edytujProjekt(new Projekt());
	   }
	   
	   @FXML
	   private void onChangePageSize(ActionEvent event) {
	       pageSize = cbPageSizes.getValue();
	       pageNo = 0;
	       wykonawca.execute(() -> loadPage(search4, pageNo, pageSize));
	   }


	 
	   private void edytujProjekt(Projekt projekt) {
		    Dialog<Projekt> dialog = new Dialog<>();
		    dialog.setTitle("Edycja");
		    if (projekt.getProjektId() != null) {
		        dialog.setHeaderText("Edycja danych projektu");
		    } else {
		        dialog.setHeaderText("Dodawanie projektu");
		    }
		    dialog.setResizable(true);
		    Label lblNazwa = getRightLabel("Nazwa: ");
		    Label lblOpis = getRightLabel("Opis: ");
		    Label lblDataOddania = getRightLabel("Data oddania: ");
		    TextField txtNazwa = new TextField();
		    if (projekt.getNazwa() != null)
		        txtNazwa.setText(projekt.getNazwa());
		    TextArea txtOpis = new TextArea();
		    txtOpis.setPrefRowCount(6);
		    txtOpis.setPrefColumnCount(40);
		    txtOpis.setWrapText(true);
		    if (projekt.getOpis() != null)
		        txtOpis.setText(projekt.getOpis());
		    DatePicker dtDataOddania = new DatePicker();
		    dtDataOddania.setPromptText("RRRR-MM-DD");
		    dtDataOddania.setConverter(new StringConverter<LocalDate>() {
		        @Override
		        public String toString(LocalDate date) {
		            return date != null ? dateFormatter.format(date) : null;
		        }

		        @Override
		        public LocalDate fromString(String text) {
		            return text == null || text.trim().isEmpty() ? null : LocalDate.parse(text, dateFormatter);
		        }
		    });
		    dtDataOddania.getEditor().focusedProperty().addListener((obsValue, oldFocus, newFocus) -> {
		        if (!newFocus) {
		            try {
		                dtDataOddania.setValue(dtDataOddania.getConverter().fromString(
		                        dtDataOddania.getEditor().getText()));
		            } catch (DateTimeParseException e) {
		                dtDataOddania.getEditor().setText(dtDataOddania.getConverter()
		                        .toString(dtDataOddania.getValue()));
		            }
		        }
		    });

		    if (projekt.getDataOddania() != null) {
		        dtDataOddania.setValue(projekt.getDataOddania());
		    }
		    GridPane grid = new GridPane();
		    grid.setHgap(10);
		    grid.setVgap(10);
		    grid.setPadding(new Insets(5, 5, 5, 5));
		    grid.add(lblNazwa, 0, 2);
		    grid.add(txtNazwa, 1, 2);
		    grid.add(lblOpis, 0, 3);
		    grid.add(txtOpis, 1, 3);
		    grid.add(lblDataOddania, 0, 4);
		    grid.add(dtDataOddania, 1, 4);
		    dialog.getDialogPane().setContent(grid);
		    ButtonType buttonTypeOk = new ButtonType("Zapisz", ButtonData.OK_DONE);
		    ButtonType buttonTypeCancel = new ButtonType("Anuluj", ButtonData.CANCEL_CLOSE);
		    dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
		    dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
		    dialog.setResultConverter(new Callback<ButtonType, Projekt>() {
		        @Override
		        public Projekt call(ButtonType butonType) {
		            if (butonType == buttonTypeOk) {
		                projekt.setNazwa(txtNazwa.getText().trim());
		                projekt.setOpis(txtOpis.getText().trim());
		                projekt.setDataOddania(dtDataOddania.getValue());
		                return projekt;
		            }
		            return null;
		        }
		    });
		    Optional<Projekt> result = dialog.showAndWait();
		    if (result.isPresent()) {
		        wykonawca.execute(() -> {
		            try {
		                projektDAO.setProjekt(projekt);
		                Platform.runLater(() -> {
		                    if (tblProjekt.getItems().contains(projekt)) {
		                        tblProjekt.refresh();
		                    } else {
		                        tblProjekt.getItems().add(0, projekt);
		                    }
		                });
		            } catch (RuntimeException e) {
		                String errMsg = "Błąd podczas zapisywania danych projektu!";
		                logger.error(errMsg, e);
		                String errDetails = e.getCause() != null ?
		                        e.getMessage() + "\n" + e.getCause().getMessage()
		                        : e.getMessage();
		                Platform.runLater(() -> showError(errMsg, errDetails));
		            }
		        });
		    }
		}

	 
	 private Label getRightLabel(String text) {
		 Label lbl = new Label(text);
		 lbl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		 lbl.setAlignment(Pos.CENTER_RIGHT);
		 return lbl;
	 }
	 
	 private void usunProjekt(Projekt projekt) {
		    Alert alert = new Alert(AlertType.CONFIRMATION);
		    alert.setTitle("Potwierdzenie usunięcia");
		    alert.setHeaderText("Czy na pewno chcesz usunąć projekt?");
		    alert.setContentText("Projekt: " + projekt.getNazwa());

		    Optional<ButtonType> result = alert.showAndWait();
		    if (result.isPresent() && result.get() == ButtonType.OK) {
		        wykonawca.execute(() -> {
		            try {
		                projektDAO.deleteProjekt(projekt.getProjektId());
		                Platform.runLater(() -> tblProjekt.getItems().remove(projekt));
		            } catch (RuntimeException e) {
		                String errMsg = "Błąd podczas usuwania projektu!";
		                logger.error(errMsg, e);
		                String errDetails = e.getCause() != null ? e.getMessage() + "\n" + e.getCause().getMessage() : e.getMessage();
		                Platform.runLater(() -> showError(errMsg, errDetails));
		            }
		        });
		    }
		}

		private void openZadanieFrame(Projekt projekt) {
		    // TODO
		    logger.info("Opening task frame for project: " + projekt.getNazwa());
		}

}

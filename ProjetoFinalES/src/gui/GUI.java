package gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import classes_auxiliares.Metodo;
import classes_auxiliares.Resultado;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GUI {

	@FXML
	MenuItem importButton;

	@FXML
	TextField locTextField;
	@FXML
	TextField cycloTextField;
	@FXML
	TextField atfdTextField;
	@FXML
	TextField laaTextField;

	Parent root;
	Stage stage;
	List<Metodo> metodos = new ArrayList<Metodo>();
	DataFormatter df = new DataFormatter();
	Resultado resultado;
	GUI controller;

	@FXML
	TableView<Metodo> table;

	@FXML
	TableColumn<Metodo, Integer> method_id;

	@FXML
	TableColumn<Metodo, String> package_name;
	@FXML
	TableColumn<Metodo, String> class_name;
	@FXML
	TableColumn<Metodo, String> method_name;
	@FXML
	TableColumn<Metodo, Integer> loc;
	@FXML
	TableColumn<Metodo, Integer> cyclo;
	@FXML
	TableColumn<Metodo, Integer> atfd;
	@FXML
	TableColumn<Metodo, Double> laa;
	@FXML
	TableColumn<Metodo, Boolean> is_long_method;
	@FXML
	TableColumn<Metodo, Boolean> iplasma;
	@FXML
	TableColumn<Metodo, Boolean> pmd;
	@FXML
	TableColumn<Metodo, Boolean> is_feature_envy;

	@FXML
	MenuItem compararButton;
	@FXML
	Button detetarErrosButton;

	private static int LOC_REGRA_DEFAULT = 80;
	private static int CYCLO_REGRA_DEFAULT = 10;
	private static int ATFD_REGRA_DEFAULT = 4;
	private static double LAA_REGRA_DEFAULT = 0.42;
	private int loc_regra_atual;
	private int cyclo_regra_atual;
	private int atfd_regra_atual;
	private double laa_regra_atual;



	public void initialize() {
		carregarRegrasDefault();
		compararButton.setDisable(true);

	}

	public void ativarTextFields() {
		locTextField.setDisable(false);
		cycloTextField.setDisable(false);
		atfdTextField.setDisable(false);
		laaTextField.setDisable(false);
	}

	public void desativarTextFields() {
		locTextField.setDisable(true);
		cycloTextField.setDisable(true);
		atfdTextField.setDisable(true);
		laaTextField.setDisable(true);
	}

	public void buttonImport() throws IOException {
		FileChooser fc = new FileChooser();
		fc.setInitialDirectory(new File(System.getProperty("user.dir")));
		FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("exceldoc", "*.xlsx");
		fc.getExtensionFilters().add(extensionFilter);
		File selectedFile = fc.showOpenDialog(stage);
		if (selectedFile != null) {
			lerFicheiroExcel(selectedFile);
			mostrarFicheiroImportado();
		}
	}

	private void mostrarFicheiroImportado() {
		ObservableList<Metodo> list = FXCollections.observableArrayList(metodos);
		method_id.setCellValueFactory(new PropertyValueFactory<>("method_id"));
		package_name.setCellValueFactory(new PropertyValueFactory<>("package_name"));
		class_name.setCellValueFactory(new PropertyValueFactory<>("class_name"));
		method_name.setCellValueFactory(new PropertyValueFactory<>("name"));
		loc.setCellValueFactory(new PropertyValueFactory<>("loc"));
		cyclo.setCellValueFactory(new PropertyValueFactory<>("cyclo"));
		atfd.setCellValueFactory(new PropertyValueFactory<>("atfd"));
		laa.setCellValueFactory(new PropertyValueFactory<>("laa"));
		is_long_method.setCellValueFactory(new PropertyValueFactory<>("is_long_method"));
		iplasma.setCellValueFactory(new PropertyValueFactory<>("plasma"));
		pmd.setCellValueFactory(new PropertyValueFactory<>("pmd"));
		is_feature_envy.setCellValueFactory(new PropertyValueFactory<>("is_feature_envy"));
		table.setItems(list);
		compararButton.setDisable(false);
	}

	private void lerFicheiroExcel(File f) throws EncryptedDocumentException, IOException {
		Workbook workbook = WorkbookFactory.create(f);
		Sheet sheet = workbook.getSheetAt(0);
		for (Row row : sheet) {
			Metodo metodo = new Metodo();
			List<Object> atributos = new ArrayList<>();
			if (row.equals(sheet.getRow(0)))
				continue;
			for (Cell cell : row) {
				String cellValue = df.formatCellValue(cell);
				atributos.add(cellValue);
			}
			metodo.setAtributos(atributos);
			metodos.add(metodo);
		}
	}

	private void carregarRegrasDefault() {
		setLoc_regra_atual(LOC_REGRA_DEFAULT);
		setCyclo_regra_atual(CYCLO_REGRA_DEFAULT);
		setAtfd_regra_atual(ATFD_REGRA_DEFAULT);
		setLaa_regra_atual(LAA_REGRA_DEFAULT);
	}

	public void abrirJanelaComparacao() throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ResultadosGUI.fxml"));
		root = fxmlLoader.load();
		GUIControllerResultados controller = (GUIControllerResultados) fxmlLoader.getController();
		List<Metodo> metodos_a_enviar = getMetodos();
		controller.initialize(metodos_a_enviar);
		stage = new Stage();
		stage.setScene(new Scene(root, 800, 400));
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.show();
	}

	public int getLoc_regra_atual() {
		return loc_regra_atual;
	}

	public void setLoc_regra_atual(int loc_regra_atual) {
		this.loc_regra_atual = loc_regra_atual;
	}

	public int getCyclo_regra_atual() {
		return cyclo_regra_atual;
	}

	public void setCyclo_regra_atual(int cyclo_regra_atual) {
		this.cyclo_regra_atual = cyclo_regra_atual;
	}

	public int getAtfd_regra_atual() {
		return atfd_regra_atual;
	}

	public void setAtfd_regra_atual(int atfd_regra_atual) {
		this.atfd_regra_atual = atfd_regra_atual;
	}

	public double getLaa_regra_atual() {
		return laa_regra_atual;
	}

	public void setLaa_regra_atual(double laa_regra_atual) {
		this.laa_regra_atual = laa_regra_atual;
	}

	public List<Metodo> getMetodos() {
		return metodos;
	}

	public void setController(GUI controller) {
		this.controller = controller;
	}

	public void detetarErros() throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("detecaoErrosGUI.fxml"));
		root = fxmlLoader.load();
		DetecaoErroController controller = (DetecaoErroController) fxmlLoader.getController();
		List<Metodo> metodos_a_enviar = getMetodos();
		controller.initialize(metodos_a_enviar, loc_regra_atual, cyclo_regra_atual, atfd_regra_atual, laa_regra_atual);
		stage = new Stage();
		stage.setScene(new Scene(root, 600, 400));
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.show();
	}

	public void abrirRegras() throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("regrasGUI.fxml"));
		root = fxmlLoader.load();
		RegrasGUIController controller = (RegrasGUIController) fxmlLoader.getController();
		controller.initialize(loc_regra_atual, cyclo_regra_atual, atfd_regra_atual, laa_regra_atual, this);
		stage = new Stage();
		stage.setScene(new Scene(root, 700, 400));
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.show();
	}



}

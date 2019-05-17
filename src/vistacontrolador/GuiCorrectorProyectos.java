package vistacontrolador;

/**
 * @author - Alba Orbegozo Martínez
 */

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelo.AlumnoNoExistenteExcepcion;
import modelo.CorrectorProyectos;
import modelo.Proyecto;

public class GuiCorrectorProyectos extends Application
{

	private MenuItem itemLeer;
	private MenuItem itemGuardar;
	private MenuItem itemSalir;

	private TextField txtAlumno;
	private Button btnVerProyecto;

	private RadioButton rbtAprobados;
	private RadioButton rbtOrdenados;
	private Button btnMostrar;

	private TextArea areaTexto;

	private Button btnClear;
	private Button btnSalir;

	private CorrectorProyectos corrector; // el modelo

	@Override
	public void start(Stage stage) {

		corrector = new CorrectorProyectos();

		BorderPane root = crearGui();

		Scene scene = new Scene(root, 800, 600);
		stage.setScene(scene);
		stage.setTitle("- Corrector de proyectos -");
		scene.getStylesheets().add(getClass()
		                .getResource("/css/application.css").toExternalForm());
		stage.show();
	}

	private BorderPane crearGui() {

		BorderPane panel = new BorderPane();
		MenuBar barraMenu = crearBarraMenu();
		panel.setTop(barraMenu);

		VBox panelPrincipal = crearPanelPrincipal();
		panel.setCenter(panelPrincipal);

		HBox panelBotones = crearPanelBotones();
		panel.setBottom(panelBotones);

		return panel;
	}

	private MenuBar crearBarraMenu() {

		MenuBar barraMenu = new MenuBar();

		Menu menu = new Menu("Archivo");

		itemLeer = new MenuItem("Leer de fichero");
		itemLeer.setAccelerator(KeyCombination.keyCombination("CTRL+L"));
		itemLeer.setOnAction(e -> leerDeFichero());

		itemGuardar = new MenuItem("Guardar en fichero");
		itemGuardar.setDisable(true);
		itemGuardar.setAccelerator(KeyCombination.keyCombination("Ctrl+G"));
		itemGuardar.setOnAction(e -> salvarEnFichero());

		itemSalir = new MenuItem("Salir");
		itemSalir.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
		itemSalir.setOnAction(e -> salir());
		
		menu.getItems().addAll(itemLeer, itemGuardar, new SeparatorMenuItem(), itemSalir);
		
		barraMenu.getMenus().add(menu);
		
		return barraMenu;
	}

	private VBox crearPanelPrincipal() {

		VBox panel = new VBox();
		panel.setPadding(new Insets(5));
		panel.setSpacing(10);

		Label lblEntrada = new Label("Panel de entrada");
		lblEntrada.setMaxWidth(Integer.MAX_VALUE);
		lblEntrada.getStyleClass().add("titulo-panel");

		Label lblOpciones = new Label("Panel opciones");
		lblOpciones.setMaxWidth(Integer.MAX_VALUE);
		lblOpciones.getStyleClass().add("titulo-panel");
		
		areaTexto = new TextArea();
		areaTexto.setMaxHeight(Integer.MAX_VALUE);
		VBox.setVgrow(areaTexto, Priority.ALWAYS);
		
		HBox pnlEntrada = crearPanelEntrada();
		HBox pnlOpciones = crearPanelOpciones();
		
		panel.getChildren().addAll(lblEntrada, pnlEntrada, lblOpciones, pnlOpciones, areaTexto);
		
		return panel;
	}

	private HBox crearPanelEntrada() {

		HBox panel = new HBox();
		panel.setPadding(new Insets(5));
		panel.setSpacing(10);

		Label lblAlumno = new Label("Alumno");
		
		txtAlumno = new TextField();
		txtAlumno.setPrefColumnCount(30);
		txtAlumno.setOnAction(e -> verProyecto());

		btnVerProyecto = new Button("Ver proyecto");
		btnVerProyecto.setPrefWidth(120);
		btnVerProyecto.setOnAction(e -> verProyecto());

		panel.getChildren().addAll(lblAlumno, txtAlumno, btnVerProyecto);

		return panel;
	}

	private HBox crearPanelOpciones() {

		HBox panel = new HBox();
		panel.setPadding(new Insets(5));
		panel.setSpacing(50);
		panel.setAlignment(Pos.CENTER);
						
		rbtAprobados = new RadioButton("Mostrar aprobados");
		rbtAprobados.setSelected(true);
		
		rbtOrdenados = new RadioButton("Mostrar ordenados");

		ToggleGroup botones = new ToggleGroup();
		rbtAprobados.setToggleGroup(botones);
		rbtOrdenados.setToggleGroup(botones);
		
		btnMostrar = new Button("Mostrar");
		btnMostrar.setOnAction(e -> mostrar());

		panel.getChildren().addAll(rbtAprobados, rbtOrdenados, btnMostrar);
		return panel;
	}

	private HBox crearPanelBotones() {

		HBox panel = new HBox();
		panel.setPadding(new Insets(5));
		panel.setSpacing(10);
		panel.setAlignment(Pos.BOTTOM_RIGHT);

		btnClear = new Button("Clear");
		btnClear.setPrefWidth(90);
		btnClear.setOnAction(e -> clear());
		
		btnSalir = new Button("Salir");
		btnSalir.setPrefWidth(90);
		btnSalir.setOnAction(e -> salir());
		
		panel.getChildren().addAll(btnClear, btnSalir);
		return panel;
	}

	private void salvarEnFichero() {
		try {
			corrector.guardarOrdenadosPorNota();
			areaTexto.setText("Guardados en el fichero los resultados del examen");
		}
		catch (IOException ex) {
			areaTexto.setText("\n" + "Error al guardar en el fichero los resultados");
		}
	}

	private void leerDeFichero() {
		itemLeer.setDisable(true);
		itemGuardar.setDisable(false);
		corrector.leerDatosProyectos();
	}

	private void verProyecto() {
		if (!itemLeer.isDisable())
		{
			areaTexto.setText("No se han leido todavia los datos del fichero" + "\n"
			        + "Vaya a la opcion leer del menu");
		}
		else
		{
			String alumno = txtAlumno.getText();
			if (alumno.isEmpty())
			{
				areaTexto.setText("Teclee nombre de alumno");
			}
			else
			{
				try
				{
					Proyecto proyecto = corrector.proyectoDe(alumno);
					areaTexto.setText(proyecto.toString());
				}
				catch (AlumnoNoExistenteExcepcion ex)
				{
					areaTexto.setText("Alumno/a no existente");
				}
			}
		}
			cogerFoco();
	}

	private void mostrar() {
		clear();

		if (!itemLeer.isDisable()) {
			areaTexto.setText("No se han leido todavia los datos del fichero" + "\n"
			        + "Vaya a la opcion leer del menu");
		}
		else if (rbtAprobados.isSelected()) {
			areaTexto.setText("Han aprobado el proyecto " + corrector.aprobados());
		}
		else if (rbtOrdenados.isSelected()) {
			List<Map.Entry<String, Proyecto>> ordenados = corrector.ordenadosPorNota();
			for (Entry<String, Proyecto> entrada : ordenados) {
				String alumno = entrada.getKey();
				Proyecto proyecto = entrada.getValue();
				areaTexto.appendText(alumno.toUpperCase() + "\n" + proyecto.toString());
			}
		}
			cogerFoco();
	}

	private void cogerFoco() {

		txtAlumno.requestFocus();
		txtAlumno.selectAll();

	}

	private void salir() {
		System.exit(0);
	}

	private void clear() {
		areaTexto.clear();
		cogerFoco();
	}

	public static void main(String[] args) {
		launch(args);
	}
}

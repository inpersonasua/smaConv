package smaConv;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import smaConv.ui.Messages;
import smaConv.ui.RootLayoutController;
import smaConv.util.AnkiApkg;
import smaConv.util.SmpakParserWithUpdates;

public class MainApp extends Application {

  private Stage stage;
  private AnchorPane rootLayout;
  private RootLayoutController controller;
  private final BooleanProperty isConverting = new SimpleBooleanProperty(false);
  private Task<Void> task;

  @Override
  public final void start(Stage primaryStage) {
    this.stage = primaryStage;
    this.stage.setTitle("smaConv"); //$NON-NLS-1$
    initRootLayout();

  }

  private void initRootLayout() {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(MainApp.class.getResource("ui/RootLayout.fxml")); //$NON-NLS-1$
      loader.setResources(ResourceBundle.getBundle("smaConv.ui.messages", Locale.getDefault()));
      rootLayout = (AnchorPane) loader.load();
      Scene scene = new Scene(rootLayout);

      stage.setScene(scene);
      stage.setResizable(false);

      controller = loader.getController();
      controller.setMainApp(this);

      controller.getGridPane().disableProperty().bind(isConverting);
      controller.getStartStopButton().disableProperty().bind(isConverting);

      scene.getWindow().setOnCloseRequest(new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent ev) {
          if (!controller.shutdown()) {
            ev.consume();
          }
        }
      });

      stage.show();

    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

  public final Stage getPrimaryStage() {
    return stage;
  }

  public final void convert() throws Exception {
    if (controller.getInputFile() == null) {
      Platform.runLater(() -> showMessage(Messages.getString("MainApp.chooseFile"))); //$NON-NLS-1$
      return;
    } else if (Files.exists(controller.getOutputFile())) {
      Alert alert = new Alert(AlertType.CONFIRMATION);
      alert.setTitle(Messages.getString("MainApp.warning")); //$NON-NLS-1$
      alert.setHeaderText(Messages.getString("MainApp.file") //$NON-NLS-1$
          + controller.getOutputFile().getFileName() + Messages.getString("MainApp.exist")); //$NON-NLS-1$
      Optional<ButtonType> result = alert.showAndWait();
      if (result.get() == ButtonType.CANCEL) {
        return;
      }
    }

    task = new Task<Void>() {
      String message;

      @Override
      protected Void call() throws Exception {
        isConverting.set(true);
        SuperMemoToAnkiConverter sm2ankiConverter = new SuperMemoToAnkiConverter(
            new SmpakParserWithUpdates(controller.getInputFile()), controller.getConverter(),
            new AnkiApkg(controller.getOutputFile()));
        message = sm2ankiConverter.convert();
        return null;
      }

      @Override
      protected void succeeded() {
        Platform.runLater(() -> showMessage(message));
        isConverting.set(false);
        super.succeeded();
      }

      @Override
      protected void failed() {
        Platform.runLater(() -> showMessage(Messages.getString("MainApp.convertionFinished"))); //$NON-NLS-1$
        isConverting.set(false);
        super.failed();
      }
    };

    Thread conversionThread = new Thread(task);
    conversionThread.setDaemon(true);
    conversionThread.start();

  }

  private void showMessage(String message) {
    Alert alert = new Alert(AlertType.INFORMATION, message);
    alert.setTitle(Messages.getString("MainApp.information")); //$NON-NLS-1$
    alert.setHeaderText(""); //$NON-NLS-1$
    alert.showAndWait().filter(response -> response == ButtonType.OK);
  }

  public BooleanProperty isConvertingProperty() {
    return isConverting;
  }

  public boolean isConverting() {
    return isConverting.get();
  }

  public static void main(String[] args) {
    launch(args);
  }
}

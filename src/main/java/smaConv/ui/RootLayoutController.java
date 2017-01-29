package smaConv.ui;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import smaConv.MainApp;
import smaConv.converters.ClozeConverter;
import smaConv.converters.ClozeWithSoundConverter;
import smaConv.converters.Converter;
import smaConv.converters.WordsConverter;
import smaConv.converters.WordsWithSoundsConverter;

public class RootLayoutController {

  private MainApp mainApp;

  public void setMainApp(MainApp mainApp) {
    this.mainApp = mainApp;
  }

  @FXML
  private AnchorPane rootLayout;
  @FXML
  private TextField inputFile;
  @FXML
  private TextField outputFile;
  @FXML
  private Button startStopButton;
  @FXML
  private Button openButton;
  @FXML
  private Button saveButton;

  @FXML
  private RadioButton words;
  @FXML
  private RadioButton wordsWithSound;
  @FXML
  private RadioButton clozeDeletion;
  @FXML
  private RadioButton clozeDeletionWithSound;

  @FXML
  private GridPane gridPane;

  public Path getInputFile() {
    if (inputFile.getText().isEmpty()) {
      return null;
    }
    return Paths.get(inputFile.getText());
  }

  public Path getOutputFile() {
    if (outputFile.getText().isEmpty()) {
      return null;
    }
    return Paths.get(outputFile.getText());
  }

  public Converter getConverter() {
    if (words.isSelected()) {
      return new WordsConverter();
    } else if (clozeDeletion.isSelected()) {
      return new ClozeConverter();
    } else if (wordsWithSound.isSelected()) {
      return new WordsWithSoundsConverter();
    } else if (clozeDeletionWithSound.isSelected()) {
      return new ClozeWithSoundConverter();
    } else {
      throw new RuntimeException("Nothing selected.");
    }

  }

  @FXML
  private void handleOpen() {
    FileChooser fileChooser = new FileChooser();
    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("plik *.smpak",
        "*.smpak");
    fileChooser.getExtensionFilters().add(extFilter);
    File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

    if (file != null) {
      String filePath = file.getAbsolutePath();
      inputFile.setText(filePath);
      setTargetFile(new File(filePath.substring(0, filePath.lastIndexOf("smpak")) + "apkg"));
    }
  }

  @FXML
  private void handleSave() {
    FileChooser fileChooser = new FileChooser();
    FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
        "spakowana talia Anki", "*.apkg");
    fileChooser.getExtensionFilters().add(extensionFilter);
    fileChooser.setInitialFileName("sm.apkg");
    File targetFile = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
    if (targetFile != null) {
      setTargetFile(targetFile);
    }
  }

  private void setTargetFile(File targetFile) {
    outputFile.setText(targetFile.getAbsolutePath());
  }

  @FXML
  private void handleConvert() throws Exception {
    mainApp.convert();
  }

  public GridPane getGridPane() {
    return gridPane;
  }

  public Button getStartStopButton() {
    return startStopButton;
  }

  public boolean shutdown() {
    if (mainApp.isConverting()) {
      Alert alert = new Alert(AlertType.CONFIRMATION);
      alert.setTitle("Potwierdzenie");
      alert.setHeaderText("Przerwać konwertowanie?");
      Optional<ButtonType> result = alert.showAndWait();
      return result.get() == ButtonType.OK;
    }
    return true;
  }
}

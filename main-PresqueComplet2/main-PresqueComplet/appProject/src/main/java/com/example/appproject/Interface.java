package com.example.appproject;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;


/**
 * The Interface class is the main class of the program, it manages the canvas (drawing area), visible by the user.
 */
public class Interface extends Application {

    //TODO: version ligne de commande

    //TODO : ameliorer le GUI

    //TODO : Classe com.example.appproject.Variable

    //TODO : gerer les exceptions

    //TODO : documentation


    //TODO : Importer un fichier texte

    Interpreter interpreter;

    @FXML protected Pane drawingPane;
    @FXML protected Pane cursorPane;
    @FXML protected ListView<Cursor> cursorListView;
    @FXML protected TextField posXField;
    @FXML protected TextField posYField;
    @FXML protected TextField angleField;
    @FXML protected TextField thicknessField;
    @FXML protected TextField redField;
    @FXML protected TextField greenField;
    @FXML protected TextField blueField;
    @FXML protected TextField distanceField;
    @FXML protected TextArea Console;
    @FXML protected ColorPicker colorPicker ;
    @FXML protected TextFlow history;
    @FXML protected Slider sliderTime;
    @FXML protected ScrollPane historyScrollPane;
    @FXML protected CheckBox errorsOrNot;
    @FXML protected MenuItem loadFileButton ;


    protected int executeTime = 0;
    protected MapCursor mapCursor = new MapCursor();
    protected int cursorIdCounter = 1;
    protected int selectedCursorId = -1;
    Screen screen = Screen.getPrimary();
    protected MapVariable mapVariable = new MapVariable();

    /**
     * The width of the canvas.
     */
    double screenWidth = screen.getBounds().getWidth();
    /**
     * The height of the canvas.
     */
    double screenHeight = screen.getBounds().getHeight();
    protected boolean isChecked;



    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Interface.class.getResource("App.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), screenWidth, screenHeight);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Dessin de Lignes avec Rectangle Dynamique");
        primaryStage.show();
    }

    public int getSelectedCursorId() {
        return selectedCursorId;
    }

    public Cursor getselectedCursor(){
        return mapCursor.getCursorById(getSelectedCursorId());
    }

    /**
     * It manages the selection of active cursor.
     */
    @FXML
    protected void initialize() {
        cursorListView.setOnMouseClicked(event -> {
            Cursor selectedCursor = cursorListView.getSelectionModel().getSelectedItem();
            if (selectedCursor != null) {
                selectedCursorId = selectedCursor.getId();
            }
            historyScrollPane.heightProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    // Faites défiler le ScrollPane vers le bas lorsque la taille change
                    historyScrollPane.setVvalue(1.0);
                }
            });

        });
    }

    /**
     * The method manages a Button for the user to add cursor.
     */
    @FXML
    protected void createCursorButtonClicked() {
        try {
            int posX = Integer.parseInt(posXField.getText());
            int posY = Integer.parseInt(posYField.getText());
            float angle = Float.parseFloat(angleField.getText());
            double thickness = Double.parseDouble(thicknessField.getText());
            int r = Integer.parseInt(redField.getText());
            int g = Integer.parseInt(greenField.getText());
            int b = Integer.parseInt(blueField.getText());

            checkPosition(posX, posY);

            Colorj customColor = new Colorj(r, g, b); // Créer une nouvelle instance de Colorj avec les valeurs RGB
            Cursor newCursor = new Cursor(posX, posY, angle, cursorIdCounter, thickness, true, customColor, 0);

            mapCursor.addCursor(newCursor);
            drawCursor(newCursor);

            cursorIdCounter++;
        } catch (NumberFormatException e) {
            Console.appendText("Error: Invalid input\n");
        } catch (OutOfPositionException e) {
            Console.appendText(e.getMessage() + "\n");
        }
    }

    /**
     * The method manages a Button for the user to delete the selected cursor.
     */
    @FXML
    protected void deleteCursorsButtonClicked() {
        cursorPane.getChildren().clear();
        cursorListView.getItems().clear();
        drawingPane.getChildren().clear();
        cursorIdCounter = 1;
        mapCursor.clearCursors();
    }
    @FXML
    protected void forwardButtonClicked() {
        try {
            int distance = Integer.parseInt(distanceField.getText());
            Cursor cursor = mapCursor.getCursorById(selectedCursorId);
            if (cursor != null) {
                int tempX = cursor.getPositionX();
                int tempY = cursor.getPositionY();
                cursor.forward(distance);

                checkPosition(cursor.getPositionX(), cursor.getPositionY());

                moveCursor(cursor);
                drawLine(tempX, tempY, cursor.getPositionX(), cursor.getPositionY(), cursor.getThickness(), cursor.getColorj().getRgb()[0], cursor.getColorj().getRgb()[1], cursor.getColorj().getRgb()[2],cursor.getOpacity());
            }
        } catch (NumberFormatException e) {
            Console.appendText("Error: Invalid input\n");
        } catch (OutOfPositionException e) {
            Console.appendText(e.getMessage() + "\n");
        }
    }

    /**
     * The method manages a Button for the user to save the drawing contained in the canvas.
     * The cursors are not visible in the picture.
     * The picture's format is PNG.
     */
    @FXML
    protected void saveAsPngButtonClicked() {
        // Vérifiez si le Pane est vide
        if (isPaneEmpty(drawingPane)) {
            addHistory("Error: The drawing pane is empty. No image to save.\n", Color.RED);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png"));
        File file = fileChooser.showSaveDialog(drawingPane.getScene().getWindow());
        if (file != null) {
            try {
                WritableImage writableImage = new WritableImage((int) drawingPane.getWidth(), (int) drawingPane.getHeight());
                drawingPane.snapshot(new SnapshotParameters(), writableImage);
                ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
                addHistory("Image saved successfully to: " + file.getAbsolutePath(), Color.BLUE);
            } catch (IOException ex) {
                addHistory("Error: Could not save image\n", Color.RED);
            }
        }
    }

    private boolean isPaneEmpty(Pane pane) {
        return pane.getChildren().isEmpty();
    }

    /**
     * The method is used to represent the cursor on the drawing area interface.
     * @param cursor
     */
    protected void drawCursor(Cursor cursor) {
        if (cursor.isVisible()) {
            double cursorSize = 10;
            double angle = Math.toRadians(cursor.getDirection());

            // Calculate the coordinates of the triangle's vertices
            double x1 = cursor.getPositionX() + cursorSize * Math.cos(angle);
            double y1 = cursor.getPositionY() + cursorSize * Math.sin(angle);
            double x2 = cursor.getPositionX() + cursorSize * Math.cos(angle + 2 * Math.PI / 3);
            double y2 = cursor.getPositionY() + cursorSize * Math.sin(angle + 2 * Math.PI / 3);
            double x3 = cursor.getPositionX() + cursorSize * Math.cos(angle + 4 * Math.PI / 3);
            double y3 = cursor.getPositionY() + cursorSize * Math.sin(angle + 4 * Math.PI / 3);

            // Create the triangle shape
            Polygon triangle = new Polygon();
            triangle.getPoints().addAll(x1, y1, x2, y2, x3, y3);

            // Set the stroke width and fill color based on the cursor's properties
            triangle.setStrokeWidth(cursor.getThickness());
            triangle.setFill(Color.rgb(cursor.getColorj().getRgb()[0], cursor.getColorj().getRgb()[1], cursor.getColorj().getRgb()[2]));
            triangle.setOpacity(cursor.getOpacity());

            // Set user data for identification
            triangle.setUserData(cursor.getId());

            // Add the triangle to the pane
            cursorPane.getChildren().add(triangle);
        }
        if (!cursorListView.getItems().contains(cursor)) {
            cursorListView.getItems().add(cursor);
        }
    }

    /**
     * Displays the movement of the selected cursor on the drawing area.
     * It erases the old cursor representation and redraws it at the new position.
     * @param cursor
     */
    protected void moveCursor(Cursor cursor) {
        removeCursorVisual(cursor);

        // Update the cursor position in the ListView
        cursorListView.getItems().remove(cursor);
        cursorListView.getItems().add(cursor);

        // Redraw the cursor at its new position
        drawCursor(cursor);
    }

    /**
     * Remove the visual representation of a cursor from the drawing area Pane.
     * @param cursorToRemove
     */
    protected void removeCursorVisual(Cursor cursorToRemove) {
        cursorPane.getChildren().removeIf(node -> {
            if (node instanceof Polygon) {
                Polygon triangle = (Polygon) node;
                return triangle.getUserData() != null && triangle.getUserData().equals(cursorToRemove.getId());
            }
            return false;
        });
    }

    /**
     * Remove the selected cursor from the drawing area Pane and the map.
     * @param cursorToRemove
     */
    protected void removeCursor(Cursor cursorToRemove) {


            mapCursor.removeCursor(cursorToRemove.getId());
            cursorListView.getItems().remove(cursorToRemove);
            addHistory("cursor Delet",Color.ROSYBROWN);
            removeCursorVisual(cursorToRemove);

    }


    /**
     * Draws a line from a position to another, with the specified color and width.
     */
    protected void drawLine(double startX, double startY, double endX, double endY, double stroke, int r, int g, int b,double opacity) {
        try {
            checkLinePosition(startX, startY, endX, endY);

            Line line = new Line(startX, startY, endX, endY);
            line.setStrokeWidth(stroke);
            Color customColor = Color.rgb(r, g, b);
            line.setStroke(customColor);
            line.setOpacity(opacity);
            drawingPane.getChildren().add(line);
        } catch (OutOfPositionException e) {
            Console.appendText(e.getMessage() + "\n");
        }
    }

    @FXML
    protected void setExecuteTime(){
        executeTime = (int) Math.round(sliderTime.getValue());
    }
    @FXML
    protected void setBackground(){
        Color selectedColor = colorPicker.getValue();
        drawingPane.setStyle("-fx-background-color: #" + colorToHex(selectedColor) + ";");
    }

    protected String colorToHex(Color color) {
        return String.format("%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
    //TODO : empecher la poursuite de l'execution
    /**
     * Checks if the position is in or out of the canvas' dimensions.
     * @param x
     * @param y
     * @throws OutOfPositionException
     */
    protected void checkPosition(double x, double y) throws OutOfPositionException {
        if (x < 0 || x > drawingPane.getWidth() || y < 0 || y > drawingPane.getHeight()) {
            this.addHistory("Error: Position out of Pane",Color.RED);
            throw new OutOfPositionException("Error: Position out of Pane");
        }
    }
    protected void checkPositionByCursor(Cursor cursor) throws OutOfPositionException {
        if (cursor.getPositionX() < 0 || cursor.getPositionX() > drawingPane.getWidth() || cursor.getPositionY() < 0 || cursor.getPositionY() > drawingPane.getHeight()) {
            this.addHistory("Error: Position out of Pane for cursor "+cursor.getId(),Color.RED);
            throw new OutOfPositionException("Error: Position out of Pane");
        }
    }
    @FXML
    public void scan(){
        String instruction = Console.getText();
        try {
            Interpreter.interpret(instruction,this,mapCursor,mapCursor.getCursorById(selectedCursorId),mapVariable);
        } catch (Exception e) {

        }
    }
    @FXML
    public void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a file");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        Stage stage = (Stage) drawingPane.getScene().getWindow();

        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            addHistory("File selected: " + selectedFile.getAbsolutePath(), Color.BLUE);
            try {
                scanFile(selectedFile);
            } catch (IOException e) {
                System.err.println("Error while reading the file: " + e.getMessage());
                addHistory("Error reading the file", Color.RED);
            }
        } else {
            addHistory("Nothing file selected", Color.RED);
        }
    }



    private void scanFile(File file) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            //TODO : gérer STOP
            boolean stop =false;
            String line;
            Timeline timeline = new Timeline();
            int index = 0;
            int numLine=0;
            while ((line = br.readLine()) != null) {
                if(stop && !this.isChecked){
                    break;
                }
                try {


                    numLine++;
                    PauseTransition pause = new PauseTransition(Duration.millis(executeTime * index));
                    String finalLine = line;
                    pause.setOnFinished(event -> {


                            Interpreter.interpret(finalLine, this, mapCursor, mapCursor.getCursorById(selectedCursorId), mapVariable);


                    });
                    pause.play();
                    index++;
                }
                catch (RuntimeException e){
                    addHistory(e.getMessage() + "Line : "+numLine,Color.RED);
                    stop=true;
                }
            }
        }
    }



    public void addHistory(String message,Color color){
        Text text = new Text(message+"\n");
        text.setFill(color);
        history.getChildren().add(text);

    }

    protected boolean ignoreErrors() {
        boolean ignoreErrors = errorsOrNot.isSelected();
        return ignoreErrors;
    }


    protected void checkLinePosition(double startX, double startY, double endX, double endY) throws OutOfPositionException {
        checkPosition(startX, startY);
        checkPosition(endX, endY);
    }

    /* protected void breakDrawing() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(10), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                addHistory("pause de " + executeTime, Color.BLACK);
                System.out.println("test");
            }
        }));
        timeline.play();
    }
    protected void startBreak(){

    }
    */

    public double getDrawingPaneWidth(){
        return drawingPane.getWidth();
    }
    public double getDrawingPaneHeight(){
        return drawingPane.getHeight();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

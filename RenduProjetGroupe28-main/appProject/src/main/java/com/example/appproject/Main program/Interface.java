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

import javafx.animation.Timeline;
import javafx.util.Duration;



/**
 * The <b><i>Interface</i></b> Class is the main class of the program, it manages the canvas (drawing area), the
 * cursors, the writing area for the user to type instructions, the history and the buttons.
 *
 * Using FXML
 */
public class Interface extends Application {
    Interpreter interpreter;

    /**
     * The drawing area where all traces are drawn.
     */
    @FXML protected Pane drawingPane;

    /**
     * The pane where the cursors are drawn.
     * The Pane is superposed with <i>drawingPane</i> so the user can see the cursors but the cursors are not part of
     * the final drawing.
     */
    @FXML protected Pane cursorPane;

    /**
     * The list containing the existing cursors.
     */
    @FXML protected ListView<Cursor> cursorListView;

    /**
     * The area where the user can type the instructions.
     */
    @FXML protected TextArea Console;

    /**
     * The color of the background.
     */
    @FXML protected ColorPicker colorPicker ;

    /**
     * The history of executed instructions and errors, visible by the user on the interface.
     */
    @FXML protected TextFlow history;

    /**
     * The slider use to choose the time between each instruction.
     */
    @FXML protected Slider sliderTime;

    /**
     * The pane containing history of all instructions that were written plus the encountered errors.
     */
    @FXML protected ScrollPane historyScrollPane;

    /**
     * The checkbox use to indicate whether the errors interrupt the execution of a script or not.
     */
    @FXML protected CheckBox errorsOrNot;


    /**
     * The attribute determines the time between two instructions to execute.
     * If <i>executeTime</i> == 0, the <i>Interpreter.interpretWithoutDelay</i> method will be called.
     * If <i>executeTime</i> > 0n the <i>Interpreter.interpret</i> method will be called.
     */
    protected int executeTime = 1;

    /**
     * The Map of existing cursors.
     */
    protected MapCursor mapCursor = new MapCursor();

    /**
     * The number of existing cursors.
     */
    protected int cursorIdCounter = 1;

    /**
     * The id of the cursor currently selected.
     * Default value as <i>-1</i>.
     */
    protected int selectedCursorId = -1;

    /**
     * The portion of the app stage that appears on screen.
     */
    Screen screen = Screen.getPrimary();

    /**
     * The Map containing all defined variables.
     */
    protected MapVariable mapVariable = new MapVariable();

    /**
     * The width of the canvas.
     */
    double screenWidth = screen.getBounds().getWidth();

    /**
     * The height of the canvas.
     */
    double screenHeight = screen.getBounds().getHeight();

    /**
     *The value of the chexkbox
     */
    protected boolean isChecked;

    /**
     *To stop the execution of the interpreter
     */
    protected boolean stop = false ;

    /**
     Counter of the number of instruction
     */
    protected int itCounter;

    /**
     * The attribute is used to stop the execution of instructions passed a number of them.
     * Currently set at 10_000.
     */
    protected int maxInstructions = 100 ;

    /**
     *a security to stop the programme
     */
    protected boolean kill = false;


    //TODO : doc
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Interface.class.getResource("App.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), screenWidth, screenHeight);
        primaryStage.setScene(scene);
        primaryStage.setTitle("ING 1 GI GROUP 28 2023/2024 ChromatYnk");
        primaryStage.show();
    }
    /**
    *getter for the selected cursor
     */
    public int getSelectedCursorId() {
        return selectedCursorId;
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

    /**
     * The method to update the value of stop when you press the stop button .
     */
    @FXML protected void stopButton(){

        this.stop=true;

    }
    public boolean getStop(){

        return this.stop;
    }

    /**
     * The method to set the value of stop
     * @param state : state
     */
    public void setStop(boolean state){
        this.stop =state;
    }
    public int getItCounter(){
        return this.itCounter;
    }
    /**
     * The method to reset the iteration counter
     */
    public void resetItCounter(){
        this.itCounter =1;
    }
    public int getMaxInstructions(){
        return this.maxInstructions;
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

    /**
     * The method is used to check if a Pane is empty
     *
     * @param pane
     */
    private boolean isPaneEmpty(Pane pane) {
        return pane.getChildren().isEmpty();
    }

    /**
     * The method is used to represent the cursor on the drawing area interface.
     *
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
     *
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
            removeCursorVisual(cursorToRemove);

    }


    /**
     * Draws a line from a position to another, with the specified color and width.
     *
     * @param startX,startY,endX,endY
     * @param stroke
     * @param r,g,b
     * @param opacity
     */
    protected void drawLine(double startX, double startY, double endX, double endY, double stroke, int r, int g, int b,double opacity)throws OutOfPositionException {
        try {

            checkLinePosition(startX, startY, endX, endY,stroke);

            Line line = new Line(startX, startY, endX, endY);
            line.setStrokeWidth(stroke);
            Color customColor = Color.rgb(r, g, b);
            line.setStroke(customColor);
            line.setOpacity(opacity);
            drawingPane.getChildren().add(line);
        } catch (OutOfPositionException e) {
            throw new OutOfPositionException("Line out of Pane");
        }
    }

    /**
     * The user sets the <i>executeTime</i> attribute with a slider on the interface.
     */
    @FXML
    protected void setExecuteTime(){
        executeTime = (int) Math.round(sliderTime.getValue());
    }

    /**
     * The user can choose the background color with a Button on the interface.
     */
    @FXML
    protected void setBackground(){
        Color selectedColor = colorPicker.getValue();
        drawingPane.setStyle("-fx-background-color: #" + colorToHex(selectedColor) + ";");
    }

    /**
     *
     * @param color
     * @return the web format ("RRGGBB") of a <i>Color</i> Object.
     */
    protected String colorToHex(Color color) {
        return String.format("%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    /**
     * Checks if the position is in or out of the canvas' dimensions.
     *
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
    /**
     * The method scans the text entered by the user and execute the instructions.
     * The method <i>Interpret.interpret</i> is called.
     */
    @FXML
    public void scan(){
        setStop(false);
        kill = false;
        resetItCounter();
        String instruction = Console.getText();
        try {
            Interpreter.interpret(instruction,this,mapCursor,mapCursor.getCursorById(selectedCursorId),mapVariable,1);
        } catch (Exception e) {

        }
    }

    /**
     * The method is used to allow the user to select a file to import and execute.
     */
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
            addHistory("No file selected", Color.RED);
        }
    }

    /**
     * Scan the file entered as argument and execute its content using <i>Interpret.interpret</i> method.
     * The execution can eventually be interrupted if the conditions are met.
     *
     * @param file
     * @throws IOException
     */
    private void scanFile(File file) throws IOException {
        setStop(false);
        kill = false;
        resetItCounter();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            boolean stop =false;
            String line;
            Timeline timeline = new Timeline();
            int index = 0;
            int numLine=0;
            while ((line = br.readLine()) != null) {
                if (getItCounter()>getMaxInstructions()){
                    addHistory("test",Color.RED);
                    break;
                }
                if(stop && !this.isChecked){
                    break;
                }
                if (getStop()){
                    break;
                }
                if(kill){
                    break;
                }


                numLine++;
                PauseTransition pause = new PauseTransition(Duration.millis(executeTime * index));
                String finalLine = line;
                int finalNumLine = numLine;
                pause.setOnFinished(event -> {


                    Interpreter.interpret(finalLine, this, mapCursor, mapCursor.getCursorById(selectedCursorId), mapVariable,finalNumLine);



                });
                pause.play();
                index++;
            }
        }
    }


    /**
     * The method adds to the <i>history</i> the message enterd as argument with the designated <i>Color</i>.
     * @param message Can be an instruction which has been executed or an Exception/Error.
     *
     * @param color The colors associated to the instructions are BLACK and RED for the exceptions.
     */
    public void addHistory(String message,Color color){
        Text text = new Text(message+"\n");
        text.setFill(color);
        history.getChildren().add(text);
    }

    /**
     * The method returns the state of the option, for the user, to ignore incorrect instructions while performing
     * a script.
     *
     * @return
     */
    protected boolean ignoreErrors() {
        boolean ignoreErrors = errorsOrNot.isSelected();
        return ignoreErrors;
    }

    /**
     * The method assures that a segment defined by two point (x,y) is in the drawing area's limits.
     *
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @throws OutOfPositionException
     */
    protected void checkLinePosition(double startX, double startY, double endX, double endY,double stroke) throws OutOfPositionException {
        checkPosition(startX, startY);
        checkPosition(startX+stroke/2, startY+stroke/2);
        checkPosition(startX-stroke/2, startY-stroke/2);
        checkPosition(startX+stroke/2, startY-stroke/2);
        checkPosition(startX-stroke/2, startY+stroke/2);
        checkPosition(endX, endY);

    }


    public double getDrawingPaneWidth(){
        return drawingPane.getWidth();
    }
    public double getDrawingPaneHeight(){
        return drawingPane.getHeight();
    }

    /**
     * <b><i>main</i></b> function that launches the application.
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}

package com.example.appproject;

import java.util.regex.*;


import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * The <b><i>Interpreter</i></b> Class manages the instructions entered by the user.
 * It checks if the instruction the syntax is fitting the language and executes the corresponding behavior.
 */

public class Interpreter {

    /**
     * This attribute is used to determine if the execution has to be interrupted or not.
     * It is used in the <i>interpret</i> and <i>interpretWithoutDelay</i> methods.
     */
    static boolean stop = false;

    public boolean getStop(){
        return stop;
    }

    /**
     * The method launches the execution of the instructions entered by the user, if a delay is requested between each
     * execution.
     * It pauses the running of the programme between each instruction, for as long as <i>Interface.executeTime</i>.
     *
     * @param input
     * @param interfaceInstance
     * @param cursors
     * @param cursor
     * @param variables
     * @param numLine the line number of the current executed instruction
     */
    public static void interpret(String input, Interface interfaceInstance, MapCursor cursors, Cursor cursor, MapVariable variables, int numLine) {
        List<String> instructions = splitCommand(input);

        // The method is interrupted either if the instructions list is empty or emergency stop is called by clicking the STOP button.
        if (instructions.isEmpty() || interfaceInstance.kill) {
            return;
        }

        stop = false;
        Timeline timeline = new Timeline();
        int index = 1;

        for (String instruction : instructions) {
            interfaceInstance.itCounter++;



            final String currentInstruction = instruction;

            KeyFrame keyFrame = new KeyFrame(Duration.millis(interfaceInstance.executeTime * index), event -> {
                try {
                    // The execution is interrupted as soon as the maximum number of instructions is reached.
                    if (interfaceInstance.getItCounter() > interfaceInstance.getMaxInstructions()) {
                    interfaceInstance.kill = true;
                    interfaceInstance.addHistory("Number of instructions exceeded : " + interfaceInstance.getMaxInstructions(), Color.DARKRED);
                    timeline.stop();
                    return;
                }
                    //STOP button was clicked and execution is stopped as soon as an error is found
                    if (stop && !interfaceInstance.ignoreErrors()) {
                        interfaceInstance.kill = true;
                        timeline.stop();
                        return;
                    }
                    //STOP button was clicked
                    if (interfaceInstance.stop) {
                        interfaceInstance.kill = true;
                        interfaceInstance.addHistory("Emergency stop", Color.DARKRED);
                        timeline.stop();
                        return;
                    }

                    executeInstruction(currentInstruction, interfaceInstance, cursors, cursor, variables, numLine);
                } catch (Exception e) {
                    interfaceInstance.addHistory("Line " + numLine + " Error executing command: " + currentInstruction + " " + e.getMessage(), Color.RED);
                    e.printStackTrace();
                    stop = true; // Arrête le traitement des instructions suivantes
                }
            });

            timeline.getKeyFrames().add(keyFrame);
            index++;
        }

        timeline.play();
    }


    /**
     * The method launches the execution of the instructions entered by the user if no delay is set.
     *
     * @param input
     * @param interfaceInstance
     * @param cursors
     * @param cursor
     * @param variables
     * @param numLine
     */
    public static void interpretWithoutDELAY(String input, Interface interfaceInstance, MapCursor cursors, Cursor cursor, MapVariable variables, int numLine) {
        List<String> instructions = splitCommand(input);

        if (instructions.isEmpty()) {
            return;
        }
        stop = false;
        if (interfaceInstance.executeTime!=0) {
            interfaceInstance.addHistory("The delay is not available for MIRROR, MIMIC commands",Color.DARKGREY);
        }
        for (String instruction : instructions) {
            if ((stop) && (!interfaceInstance.ignoreErrors())) {
                break;
            }
            try {
                executeInstruction(instruction, interfaceInstance, cursors, cursor, variables, numLine);
            } catch (Exception e) {
                interfaceInstance.addHistory("Line " + numLine + " Error executing command: " + instruction + " " + e.getMessage(), Color.RED);
                e.printStackTrace();
                stop = true; // Stop processing further instructions
            }
        }
    }


    /**
     * The <i>executeInstruction</i> method is used to execute the instructions asked by the user.
     *
     * The instruction takes the form of a String composed of tokens (Strings separated by " ").
     * The first token is the actual instruction (FWD, BWD, HIDE, ...), it is caught with a switch to execute the right
     * behavior for each possible instruction, or throw an exception if no known instruction is detected.
     *
     * For each instruction, the selected Cursor object has its attributes updated and the Interface object, which is
     * the drawing area seen by the user, is updated.
     * Every executed instruction or Errors/Exceptions are stored in the History and printed in the history console on
     * the interface.
     *
     * @param instruction
     * @param interfaceInstance
     * @param cursors
     * @param cursor
     * @param variables
     * @param numLine
     * @throws Exception
     */
    private static void executeInstruction(String instruction, Interface interfaceInstance, MapCursor cursors, Cursor cursor, MapVariable variables,int numLine) throws Exception {
        try {

                String[] tokens = instruction.split(" ");
                switch (tokens[0]) {
                    case "FWD":
                        executeFwd(tokens, interfaceInstance, cursor, variables);
                        interfaceInstance.addHistory("Cursor " + cursor.getId() + " : " + tokens[0] + " " + tokens[1], Color.BLACK);
                        break;
                    case "BWD":
                        executeBwd(tokens, interfaceInstance, cursor);
                        interfaceInstance.addHistory("Cursor " + cursor.getId() + " : " + tokens[0] + " " + tokens[1], Color.BLACK);
                        break;
                    case "TURN":
                        cursor.turn(Double.parseDouble(tokens[1]));
                        interfaceInstance.moveCursor(cursor);
                        interfaceInstance.addHistory("Cursor " + cursor.getId() + " : " + tokens[0] + " " + tokens[1], Color.BLACK);
                        break;
                    case "COLOR":
                        applyColor(tokens, interfaceInstance, cursor);
                        if (tokens.length == 2) {
                            interfaceInstance.addHistory("Cursor " + cursor.getId() + " : " + tokens[0] + " " +
                                    tokens[1], Color.BLACK);
                        }
                        else if (tokens.length == 4){
                            interfaceInstance.addHistory("Cursor " + cursor.getId() + " : " + tokens[0] + " " +
                                    tokens[1] + " " + tokens[2] + " " + tokens[3], Color.BLACK);
                        }
                        break;
                    case "THICK":
                        if (Num.isName(tokens[1])) {
                            Num var = (Num) variables.getVariableById(tokens[1]);
                            cursor.setThickness(var.getNumValue());
                        } else {
                            cursor.setThickness(Double.parseDouble(tokens[1]));
                        }
                        interfaceInstance.addHistory("Cursor " + cursor.getId() + " : " + tokens[0] + " " + tokens[1], Color.BLACK);
                        break;
                    case "PRESS":
                        cursor.setOpacity(Double.parseDouble(tokens[1]));
                        interfaceInstance.addHistory("Cursor " + cursor.getId() + " : " + tokens[0] + " " + tokens[1], Color.BLACK);
                        break;
                    case "MOV":
                        executeMove(tokens, interfaceInstance, cursor);
                        interfaceInstance.addHistory("Cursor " + cursor.getId() + " : " + tokens[0] + " " + tokens[1] + " " + tokens[2], Color.BLACK);
                        break;
                    case "POS":
                        executePos(tokens, interfaceInstance, cursor);
                        interfaceInstance.addHistory("Cursor " + cursor.getId() + " : " + tokens[0] + " " + tokens[1] + " " + tokens[2], Color.BLACK);
                        break;
                    case "LOOKAT":
                        System.out.println("lookat command" + tokens[0] + tokens[1] + tokens[2]);
                        executeLookAt(tokens, interfaceInstance, cursors, cursor, variables);
                        interfaceInstance.addHistory("Cursor " + cursor.getId() + " : " + tokens[0] + " " + tokens[1], Color.BLACK);
                        break;
                    case "HIDE":
                        cursor.setVisible(false);
                        interfaceInstance.moveCursor(cursor);
                        interfaceInstance.addHistory("Cursor " + cursor.getId() + " : " + tokens[0], Color.BLACK);
                        break;
                    case "SHOW":
                        cursor.setVisible(true);
                        interfaceInstance.drawCursor(cursor);
                        interfaceInstance.addHistory("Cursor " + cursor.getId() + " : " + tokens[0], Color.BLACK);
                        break;
                    case "CURSOR":
                        executeCursor(tokens, interfaceInstance, cursors);
                        interfaceInstance.addHistory(" new Cursor " + tokens[1], Color.BLACK);
                        break;
                    case "SELECT":
                        cursor = cursors.getCursorById(Integer.parseInt(tokens[1]));
                        interfaceInstance.selectedCursorId = Integer.parseInt((tokens[1]));
                        interfaceInstance.addHistory("Cursor " + cursor.getId() + " : " + tokens[0], Color.BLACK);
                        break;
                    case "REMOVE":
                        interfaceInstance.removeCursor(cursor);
                        interfaceInstance.addHistory("Cursor " + cursor.getId() + " : " + tokens[0], Color.BLACK);
                        break;
                    case "FOR":
                        handleForLoop(tokens, instruction, interfaceInstance, cursors, cursor, variables,numLine);
                        break;
                    case "IF":
                        executeIf(tokens, instruction, interfaceInstance, cursors, cursor, variables,numLine);
                        break;
                    case "WHILE":
                        executeWhile(tokens, instruction, interfaceInstance, cursors, cursor, variables,numLine);
                        break;
                    case "MIMIC":
                        executeMimic(tokens, instruction, interfaceInstance, cursors, cursor, variables,numLine);
                        break;
                    case "MIRROR":
                        executeMirror(tokens, instruction, interfaceInstance, cursors, cursor, variables,numLine);
                        break;
                    case "NUM":
                        executeNum(tokens, interfaceInstance, variables);
                        interfaceInstance.addHistory(" new Variable " + tokens[1] + " : " + tokens[0], Color.BLACK);
                        break;

                    case "STR":
                        executeStr(tokens, interfaceInstance, variables);
                        interfaceInstance.addHistory(" new Variable " + tokens[1] + " : " + tokens[0], Color.BLACK);
                        break;
                    case "BOOL":
                        executeBool(tokens, interfaceInstance, variables);
                        interfaceInstance.addHistory(" new Variable " + tokens[1] + " : " + tokens[0], Color.BLACK);
                        break;
                    case "DEL":
                        executeDel(tokens, interfaceInstance, variables);
                        interfaceInstance.addHistory("Variable " + tokens[1] + " : " + tokens[0], Color.BLACK);
                        break;
                    default:
                        throw new UnknownCommand("Unknown command");
                }

            }
         catch (UnknownCommand e) {
            stop=true;
            throw new Exception(e.getMessage());
        }
    }


    /**
     * Executes the FWD (forward) instruction.
     * It moves the cursor following its direction by the "distance" entered by the user.
     * It takes into account if the user entered a percentage, symbolized with a '%' or not.
     *
     * @param tokens
     * @param interfaceInstance
     * @param cursor
     * @throws Exception
     * @throws UndefinedVariableException
     */
    private static void executeFwd(String[] tokens, Interface interfaceInstance, Cursor cursor, MapVariable variables) throws Exception, UndefinedVariableException {
        try {
            int distance;
            System.out.println("thickness :"+cursor.getThickness()+"\n");
            /*
             * If the user enters a percentage, it adapts the forward method so the distance is the percentage of
             * the largest dimension of the canvas between width and height.
             */
            if (Num.isName(tokens[1])) {
                if (!(variables.getVariableById(tokens[1]) instanceof Num)) {
                    throw new UndefinedVariableException("The name is does not refer to a Num variable");
                }
                Num var = (Num) variables.getVariableById(tokens[1]);
                distance = (int) (var.getNumValue());
                System.out.println("Value of "+tokens[1]+" var is "+var.getNumValue()+"\n");
            } else {
                if (tokens[1].endsWith("%")) {
                    Percentage distance_per = new Percentage(tokens[1]);
                    double dimension = Math.max(interfaceInstance.getDrawingPaneWidth(), interfaceInstance.getDrawingPaneHeight());
                    distance = (int) Math.round(dimension * distance_per.getValue());
                } else {
                    distance = Integer.parseInt(tokens[1]);
                }
            }
                if (cursor != null) {
                    System.out.println("thickness :"+cursor.getThickness()+"\n");
                    int tempX = cursor.getPositionX();
                    int tempY = cursor.getPositionY();

                    interfaceInstance.checkPosition(tempX + distance * Math.cos(Math.toRadians(cursor.getDirection())), tempY + distance * Math.sin(Math.toRadians(cursor.getDirection())));
                    cursor.forward(distance);
                    interfaceInstance.checkPosition(cursor.getPositionX(), cursor.getPositionY());
                    interfaceInstance.moveCursor(cursor);
                    interfaceInstance.drawLine(tempX, tempY, cursor.getPositionX(), cursor.getPositionY(), cursor.getThickness(),
                            cursor.getColorj().getRgb()[0], cursor.getColorj().getRgb()[1], cursor.getColorj().getRgb()[2], cursor.getOpacity());

            }
        } catch (NumberFormatException e) {
            throw new Exception(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new Exception(e.getMessage());
        } catch (OutOfPositionException e) {
            throw new Exception(e.getMessage());
        }


    }

    /**
     * Executes the BWD (backward) instruction.
     * It moves the cursor following its direction by the "distance" entered by the user.
     * It takes into account if the user entered a percentage, symbolized with a '%' or not.
     *
     * @param tokens
     * @param interfaceInstance
     * @param cursor
     * @throws Exception
     */
    private static void executeBwd(String[] tokens, Interface interfaceInstance, Cursor cursor) throws Exception {
        try {
            int distance;
            if (tokens[1].endsWith("%")) {
                Percentage distance_per = new Percentage(tokens[1]);
                double dimension = Math.max(interfaceInstance.getDrawingPaneWidth(), interfaceInstance.getDrawingPaneHeight());
                distance = (int) Math.round(dimension * distance_per.getValue());
            } else {
                distance = Integer.parseInt(tokens[1]);
            }
            if (cursor != null) {
                int tempX = cursor.getPositionX();
                int tempY = cursor.getPositionY();
                interfaceInstance.checkPosition(tempX-distance * Math.cos(Math.toRadians(cursor.getDirection())),tempY - distance * Math.sin(Math.toRadians(cursor.getDirection())));
                cursor.forward(-distance);
                interfaceInstance.checkPosition(cursor.getPositionX(), cursor.getPositionY());
                interfaceInstance.moveCursor(cursor);
                interfaceInstance.drawLine(tempX, tempY, cursor.getPositionX(), cursor.getPositionY(), cursor.getThickness(),
                        cursor.getColorj().getRgb()[0], cursor.getColorj().getRgb()[1], cursor.getColorj().getRgb()[2], cursor.getOpacity());
            }
        } catch (NumberFormatException e) {
            throw new Exception(e.getMessage());
        } catch (IllegalArgumentException e){
            throw new Exception(e.getMessage());
        } catch (OutOfPositionException e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Executes the POS (position) instruction.
     * It changes the coordinates of the selected cursor to values entered as arguments.
     *
     * @param tokens
     * @param interfaceInstance
     * @param cursor
     * @throws Exception
     */
    private static void executePos(String[] tokens, Interface interfaceInstance, Cursor cursor) throws Exception {
        if (cursor != null) {
            try {

                int tempX = cursor.getPositionX();
                int tempY = cursor.getPositionY();
                interfaceInstance.checkPosition(tempX,tempY);

                if (tokens[1].endsWith("%") && tokens[2].endsWith("%")) {
                    double canvasHeight = interfaceInstance.getDrawingPaneHeight();
                    double canvasWidth = interfaceInstance.getDrawingPaneWidth();
                    Percentage abscissa_per = new Percentage(tokens[1]);
                    Percentage ordinate_per = new Percentage(tokens[2]);

                    System.out.println(canvasWidth);
                    System.out.println(abscissa_per.getValue());

                    int newPosX = (int) Math.round(canvasWidth * abscissa_per.getValue());
                    int newPosY = (int) Math.round(canvasHeight * ordinate_per.getValue());

                    interfaceInstance.checkPosition(newPosX,newPosY);
                    cursor.position(newPosX,newPosY);
                } else {
                    cursor.position(Integer.parseInt(tokens[1]),Integer.parseInt(tokens[2]));
                }

                interfaceInstance.moveCursor(cursor);

            } catch (NumberFormatException e) {
                throw new Exception(e.getMessage());
            } catch (IllegalArgumentException e){
                throw new Exception(e.getMessage());
            } catch (OutOfPositionException e) {
                throw new Exception(e.getMessage());
            }

        }

    }

    /**
     * Executes the MOV (move) instruction.
     * As executePos() but it draws the line between the last position of the cursor and the new one.
     *
     * @param tokens
     * @param interfaceInstance
     * @param cursor
     * @throws Exception
     */
    private static void executeMove(String[] tokens, Interface interfaceInstance, Cursor cursor) throws Exception {
        if (cursor != null) {
            int tempX = cursor.getPositionX();
            int tempY = cursor.getPositionY();
            try {
                interfaceInstance.checkPosition(tempX,tempY);
            } catch (OutOfPositionException e) {
                throw new Exception(e);
            }

            executePos(tokens, interfaceInstance, cursor);

            interfaceInstance.drawLine(tempX, tempY, cursor.getPositionX(), cursor.getPositionY(), cursor.getThickness(), cursor.getColorj().getRgb()[0],
                    cursor.getColorj().getRgb()[1], cursor.getColorj().getRgb()[2], cursor.getOpacity());
        }
    }

    /**
     * Executes the LOOKAT instructions which can be called with different parameters.
     * The coordinates as integers, a cursor ID as an integer or the abscissa and ordinate in percentages of the canvas.
     *
     * @param tokens
     * @param interfaceInstance
     * @param mapCursor The Map of cursors is needed when the selected cursor is asked to look at another cursor.
     * @param cursor
     * @throws Exception
     */
    private static void executeLookAt(String[] tokens, Interface interfaceInstance, MapCursor mapCursor, Cursor cursor, MapVariable variables) throws Exception {
        try {
            if (tokens.length == 2) {
                Cursor cursorToLookAt = mapCursor.getCursorById(Integer.parseInt(tokens[1]));

                cursor.lookAt(cursorToLookAt);
            } else if (tokens.length == 3) {
                if (tokens[1].endsWith("%") && tokens[2].endsWith("%")) {
                    double canvasHeight = interfaceInstance.getDrawingPaneHeight();
                    double canvasWidth = interfaceInstance.getDrawingPaneWidth();
                    Percentage abscissa_per = new Percentage(tokens[1]);
                    Percentage ordinate_per = new Percentage(tokens[2]);

                    cursor.lookAt(abscissa_per, ordinate_per, canvasWidth, canvasHeight);
                } else {
                    int posX, posY;
                    //Using only variables
                    if ( (variables.containsKey(tokens[1])) && (variables.containsKey(tokens[2]))){
                        Num var1 = (Num) variables.getVariableById(tokens[1]);
                        Num var2 = (Num) variables.getVariableById(tokens[2]);
                        System.out.println("Using variables"+var1+" and "+var2+"\n");
                        posX = (int) var1.getNumValue();
                        posY = (int) var2.getNumValue();
                    } else if (Num.isName(tokens[1])){
                        //First argument is the name of a Num variable
                        posY = Integer.parseInt(tokens[2]);
                        Num var = (Num) variables.getVariableById(tokens[1]);
                        System.out.println("Using variables"+var+" as 1st arg\n");
                        posX = (int) var.getNumValue();
                    } else if (Num.isName(tokens[2])){
                        posX = Integer.parseInt(tokens[1]);
                        Num var = (Num) variables.getVariableById(tokens[2]);
                        System.out.println("Using variables"+var+" as 2nd arg\n");

                        posY = (int) var.getNumValue();
                    } else {
                        posX = Integer.parseInt(tokens[1]);
                        posY = Integer.parseInt(tokens[2]);
                    }

                    cursor.lookAt(posX, posY);
                }
            }
            interfaceInstance.moveCursor(cursor);
        }
        catch (NumberFormatException e) {
            throw new Exception(e.getMessage());
        } catch (IllegalArgumentException e){
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Executes the instruction COLOR by applying the wanted color to the selected cursor.
     * <p>
     * The color can be specified through web format (#RRGGBB) in hexa, in rgb with integers from to 0 to 255 or float
     * numbers from 0 to 1.
     * The user does not have to specify the format when it calls the COLOR instruction.
     * If the user types "COLOR 1 1 1", the program considers it to be integers. So it will apply the color (1,1,1) in
     * RGB format and not (255,255,255). (255,255,255) can be applied with (1.0,1.0,1.0) for example, or as long as
     * at least a double number is called (example (1.0,1,1) also works).
     *
     * @param tokens
     * @param interfaceInstance
     * @param cursor
     * @throws Exception
     */
    private static void applyColor(String tokens[], Interface interfaceInstance, Cursor cursor) throws Exception {
        try {
            if (tokens.length == 2) {
                //Web format : #RRGGBB
                cursor.setColor(tokens[1]);
            } else {
                if (tokens[1].contains(".") || tokens[2].contains(".") || tokens[3].contains(".")) {
                    //double value from 0 to 1
                    double red = Double.parseDouble(tokens[1]);
                    double green = Double.parseDouble(tokens[2]);
                    double blue = Double.parseDouble(tokens[3]);
                    cursor.setColor(red, green, blue);
                } else {
                    //int values from 0 to 255
                    int red = Integer.parseInt(tokens[1]);
                    int green = Integer.parseInt(tokens[2]);
                    int blue = Integer.parseInt(tokens[3]);
                    cursor.setColor(red, green, blue);
                }
            }
            interfaceInstance.moveCursor(cursor);
        } catch (NumberFormatException e){

            throw new Exception(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Executes the CURSOR instruction by creating a Cursor Object and displaying it on the drawing area.
     * The new Cursor is placed at the center of the drawing area.
     * The syntax is
     * CURSOR <i>id_value</i>.
     *
     * @param tokens
     * @param interfaceInstance
     * @param cursors
     * @throws Exception
     */
    private static void executeCursor(String[] tokens, Interface interfaceInstance, MapCursor cursors) throws NewCursorException {
        try {
            int cursorId = Integer.parseInt(tokens[1]);
            Cursor existingCursor = cursors.getCursorById(cursorId);
            if (existingCursor != null) {
                throw new NewCursorException("Error: Cursor with ID " + cursorId + " already exists.");
            }
            Cursor newCursor = new Cursor(cursorId);
            //A new cursor is by default in X=0, Y = 0. We position it at the center of the drawing Pane for convenience.
            newCursor.position(new Percentage(0.5),new Percentage(0.5), (int) interfaceInstance.getDrawingPaneWidth(), (int) interfaceInstance.getDrawingPaneHeight());
            cursors.addCursor(newCursor);
            interfaceInstance.drawCursor(newCursor);
        } catch (NumberFormatException e) {
            throw new NewCursorException("Error: Invalid input to create a Cursor.");
        }

    }

    /**
     * Executes the FOR instruction by executing the instructions contained in the instruction block with a loop
     * defined by the parameters.
     * It needs a variable, a starting and finishing value.
     * The syntax is
     * FOR <i>var_name </i> FROM <i>start</i> TO <i>end</i> STEP <i>stepValue</i>.
     * FROM, STEP and <i>stepValue</i> are optional.
     * The default value of <i>start</i> is 0, and the default  of <i>stepValue</i> is 1.
     *
     * @param tokens
     * @param instruction
     * @param interfaceInstance
     * @param cursors
     * @param cursor
     * @param variables
     * @param numLine
     * @throws Exception
     */
    private static void handleForLoop(String[] tokens, String instruction, Interface interfaceInstance, MapCursor cursors, Cursor cursor, MapVariable variables,int numLine) throws Exception {



        if (tokens.length < 3) {
            throw new LoopSyntaxException("Error: Invalid FOR loop syntax");
        }

        String variableName = tokens[1];
        if(variables.containsKey(variableName)){
            throw new LoopSyntaxException("Error : variable "+tokens[1]+" already exist");
        }

        int from = 0;
        int to;
        int step = 1;

        int currentIndex = 2;
        if (tokens[currentIndex].equals("FROM")) {
            from = Integer.parseInt(tokens[++currentIndex]);
            currentIndex++;


            if (!tokens[currentIndex].equals("TO") || !tokens[currentIndex + 2].equals("STEP")) {
                throw new LoopSyntaxException("Error: Invalid FOR loop syntax");
            }
            to = Integer.parseInt(tokens[++currentIndex]);
            currentIndex++;
            step = Integer.parseInt(tokens[++currentIndex]);
            currentIndex++;
        } else {
            if (!tokens[currentIndex].equals("TO")) {
                throw new LoopSyntaxException("Error: Invalid FOR loop syntax "+ tokens[currentIndex]);
            }
            to = Integer.parseInt(tokens[++currentIndex]);
            currentIndex++;
        }


        System.out.println(tokens[currentIndex] + "//" + currentIndex);
        if (tokens[currentIndex].startsWith("{")) {

            String stepBlock = instruction.substring(instruction.indexOf("{") + 1, instruction.lastIndexOf("}"));
            Str var = new Str(variableName,variableName);
            variables.addVariable(var);
            String finalCommand ="";
            for (int i = from; i <= to; i += step) {
                        String modifiedCommand = stepBlock.trim().replace(variableName, String.valueOf(i));
                        finalCommand+= modifiedCommand+";";

            }
            interpret(finalCommand, interfaceInstance, cursors, cursor, variables,numLine);
            variables.removeVariable(var.getVarId());
        } else {
            throw new LoopSyntaxException("Error: Invalid FOR loop syntax");
        }


    }

    /**
     * This method evaluates the boolean expression entered as a String parameter and returns its boolean value.
     * It first parses the condition to obtain its corresponding boolean value.
     * The method does not implement the parenthesis for a boolean expression.
     *
     * @param condition
     * @return the value of the condition
     * @throws IllegalArgumentException handles inputs which values are not boolean
     */
    private static boolean evaluateBooleanExpression(String condition) throws IllegalArgumentException{
        System.out.println("Evaluating "+condition+"\n");
        // Deleting the " " characters.
        condition = condition.replaceAll("\\s+", "");

        // Dividing the boolean expression by the logic operators
        String[] logicalParts = condition.split("(OR|AND)");

        System.out.println("boolean parts : "+logicalParts+"\n");
        // Initializing the final result as true.
        boolean globalResult = true;

        // Visiting each part of the expression
        for (String logicalPart : logicalParts) {

            /* Checking if the part starts with a NOT.
                If so storing the information in a variable "negated" to invert the final boolean result of the part.
            */
            System.out.println("current bool expression is "+logicalPart+"\n");
            boolean result = false;

            boolean negated = false;

            if (logicalPart.startsWith("NOT")) {
                negated = true;
                logicalPart = logicalPart.substring(1); // Removing the NOT
            }

            // Divifing the expression using the comparison expressions.
            String[] comparisonParts = logicalPart.split("(==|<=|>=|<|>)");
            System.out.println("comparison parts of "+logicalPart+": "+comparisonParts+"\n");
            // Checking the validity of comparison expressions.
            if (comparisonParts.length > 2) {
                // If a comparison does not have two values to compare an exception is thrown.
                throw new IllegalArgumentException("Illegal syntax : expression of comparison");
            }

            //If it is a boolean statement
            else if (comparisonParts.length == 1){

                String statement = comparisonParts[0].replaceAll("\\s+", "");
                if (statement.equals("true") || statement.equals("false")){
                    result = Boolean.parseBoolean(statement);
                }
            }

            //If it is a comparison
            else if (comparisonParts.length == 2){
                // Extracting the operator
                String operator = logicalPart.replaceAll("[a-zA-Z0-9]", "");

                //If the operands are true or false.
                if (comparisonParts[0].equals("true") || comparisonParts[0].equals("false") &&
                        comparisonParts[1].equals("true") || comparisonParts[1].equals("false")){
                    switch (operator) {
                        case "==" :
                            result = (comparisonParts[0] == comparisonParts[1]);
                            break;
                        case "!=":
                            result = (comparisonParts[0] != comparisonParts[1]);
                            break;
                    }
                }
                //If the operands are numerical values.
                else{
                    //Extracting the operands
                    int operand1 = Integer.parseInt(comparisonParts[0]);
                    int operand2 = Integer.parseInt(comparisonParts[1]);

                    // Verifying the operators and executing the comparison.
                    switch (operator) {
                        case "==":
                            result = (operand1 == operand2);
                            break;
                        case "<=":
                            result = (operand1 <= operand2);
                            break;
                        case ">=":
                            result = (operand1 >= operand2);
                            break;
                        case "<":
                            result = (operand1 < operand2);
                            break;
                        case ">":
                            result = (operand1 > operand2);
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown comparison operator expression.");
                    }
                }
            }

            // If the part was negated, the result is inverted.
            if (negated) {
                result = !result;
            }

            // Depending on if the logic operator is OR or AND, the global result is adapted.
            if (condition.contains("OR")) {
                globalResult = globalResult || result;
            } else if (condition.contains("AND")) {
                if (!result) {
                    //If the operator is AND, if at least a part is false, the final result is false.
                    return false;
                }
            } else {
                // If no operator is used, the final result is simply the result of the only part existing.
                globalResult = result;
            }
        }

        // Returning the global result at last.
        return globalResult;
    }

    /**
     * Executes the IF instructions by executing the block of instructions if the boolean expression's value is true.
     * The syntax is IF <i>var_name</i>.
     *
     * @param tokens Contains de block of instructions to execute and the boolean expression as tokens[1].
     * @param instruction
     * @param interfaceInstance
     * @param cursors
     * @param cursor
     * @param variables
     * @param numLine
     * @throws Exception
     */
    private static void executeIf(String[] tokens, String instruction, Interface interfaceInstance, MapCursor cursors, Cursor cursor,MapVariable variables,int numLine) throws Exception {
        try {

            if (tokens.length < 2) {
                throw new IllegalAccessException("Error : Invalid IF syntax");
            }

            String condition = instruction.substring(instruction.indexOf("IF") + 3, instruction.indexOf('{')).trim();

            if (variables.containsKey(condition)) {
                System.out.println(condition +" is defined");
                Bool boolVariable = (Bool) variables.getVariableById(condition);
                condition = String.valueOf(boolVariable.getBoolValue());
            }

            String block = instruction.substring(instruction.indexOf("{") + 1, instruction.lastIndexOf("}")).trim();

            if (evaluateBooleanExpression(condition)) {
                List<String> commands = splitCommand(block);
                for (String command : commands) {
                    interpret(command, interfaceInstance, cursors, cursor, variables,numLine);
                }
            }
        } catch (IllegalArgumentException e) {
            throw new Exception(e);
        }
    }

    /**
     * Executes the WHILE instruction by first assessing the condition following the WHILE key-word, and then by
     * executing the block of instructions.
     * It needs an existing boolean variable. The syntax is
     * WHILE <i>var_name </i>.
     *
     * @param tokens
     * @param instruction
     * @param interfaceInstance
     * @param cursors
     * @param cursor
     * @param variables
     * @param numLine
     * @throws Exception
     */
    private static void executeWhile(String[] tokens, String instruction, Interface interfaceInstance, MapCursor cursors, Cursor cursor,MapVariable variables,int numLine) throws Exception {
        if (tokens.length < 2) {
            interfaceInstance.addHistory("Error : Invalid While syntax",Color.RED);
            return;
        }

        String condition = instruction.substring(instruction.indexOf("WHILE") + 3, instruction.indexOf('{')).trim();

        //If the condition is the Bool Variable
        if (variables.containsKey(condition)) {
            Bool boolVariable = (Bool) variables.getVariableById(condition);
            condition = String.valueOf(boolVariable.getBoolValue());
        }

        String block = instruction.substring(instruction.indexOf("{") + 1, instruction.lastIndexOf("}")).trim();
        while (evaluateBooleanExpression(condition)) {
            List<String> commands = splitCommand(block);
            for (String command : commands) {
                interpret(command, interfaceInstance, cursors, cursor, variables,numLine);
            }
        }
    }

    /**
     * Parses the instructions entered as inputs.
     * This method is the first step to executing any instruction. It is called when the RUN button is clicked.
     *
     * @param instruction
     * @return a list containing each item appearing in the instruction except spaces.
     */
    private static List<String> splitCommand(String instruction) {
        List<String> intsructionSplit = new ArrayList<>();
        StringBuilder tokens = new StringBuilder();
        int count = 0;
        for (int i = 0; i < instruction.length(); i++) {
            char character = instruction.charAt(i);
            if (character == '{') {
                count++;
            } else if (character == '}') {
                count--;
            }
            //End of current simple instruction or block
            if ((character == ';') && (count == 0)) {
                //Add the content without the spaces
                intsructionSplit.add(tokens.toString().trim());
                //Resetting tokens to add next instruction / block
                tokens.setLength(0);
            } else {
                //Adding a character that appears inside an instruction or a block
                tokens.append(character);
            }
        }
        //The instruction was a simple command without finishing by ";"
        if (tokens.length() > 0) {
            intsructionSplit.add(tokens.toString().trim());
        }
        //Content of each appearing command in the instruction inside a List
        return intsructionSplit;
    }

    /**
     * Executes the MIMIC command by executing the block of instructions on the selected cursor and the temporary cursor
     * created at the position of the cursor entered as parameter.
     *
     * @param tokens Contains the id of the cursor that will execute the block of instructions, the temporary cursor
     *               will mimic its behavior.
     * @param instruction
     * @param interfaceInstance
     * @param cursors
     * @param cursor The selected cursor, the temporary cursor will be created as a model of this cursor.
     * @param variables
     * @param numLine
     * @throws MimicException
     */
    private static void executeMimic(String[] tokens, String instruction, Interface interfaceInstance, MapCursor cursors, Cursor cursor, MapVariable variables,int numLine) throws MimicException {
        if (tokens.length < 2) {
            interfaceInstance.addHistory("Error : Invalid Mimic syntax",Color.RED);
            return;
        }
        /*
         * Creating a temporary cursor to mimic the one identified in the instruction, the new cursor is a copy of the
         * selected one.
         */
        int tmpCursorId = cursors.smallestAvailableId();
        Cursor tmpCursor = new Cursor(tmpCursorId);
        tmpCursor.duplicate(cursor);

        //Display new temporary cursor
        interfaceInstance.drawCursor(tmpCursor);

        int modelCursorId = Integer.parseInt(tokens[1]);
        //Block delimiters are missing
        if (!instruction.contains("{") && /*|| better*/ !instruction.contains("}")){
            interfaceInstance.removeCursor(tmpCursor);
            throw new MimicException("There is no { and/or } in block");
        }
        String Block = instruction.substring(instruction.indexOf("{") + 1, instruction.lastIndexOf("}"));
        List<String> commands = splitCommand(Block);
        for (String command : commands) {
            try {
                /*
                 * First executes the command for the targeted cursor and then the temporary one. Commands by commands
                 * in the block
                 */
                interpretWithoutDELAY(command, interfaceInstance, cursors, cursors.getCursorById(modelCursorId),variables,numLine);
                interpretWithoutDELAY(command, interfaceInstance, cursors, tmpCursor,variables,numLine);
            } catch (Exception e) {
                interfaceInstance.removeCursor(tmpCursor);
                throw new MimicException(e.getMessage());

            }
        }
        //At the end, the temporary cursor is removed.
        interfaceInstance.removeCursor(tmpCursor);
    }


    //TODO : ajuster doc
    /**
     * Executes the MIRROR command.
     * After a cursor is selected, another one is temporarly used to execute a block of instructions.
     * The temporary cursor is created by two ways.
     *
     * Version  1 : the temporary cursor is symmetrical to the choosen cursor by using a central symmetry
     * with the coordinates of a choosen point.
     *
     * Version 2 the temporary cursor is symmetrical to the choosen cursor by using a symmetrical axis
     * with the coordinates of two choosen points.
     *
     * @param tokens
     * @param instruction
     * @param interfaceInstance
     * @param cursors
     * @param cursor
     * @param variables
     * @param numLine
     * @throws Exception
     */

    //TODO : MIRROR axial

    private static void executeMirror(String[] tokens, String instruction, Interface interfaceInstance, MapCursor cursors, Cursor cursor,MapVariable variables,int numLine) throws Exception {

            //central symmetry
        if (tokens[3].contains("{")) {
            int symetryPointX;
            int symetryPointY;
            if (tokens[1].endsWith("%") && tokens[2].endsWith("%")) {
                double canvasHeight = interfaceInstance.getDrawingPaneHeight();
                double canvasWidth = interfaceInstance.getDrawingPaneWidth();
                Percentage abscissa_per = new Percentage(tokens[1]);
                Percentage ordinate_per = new Percentage(tokens[2]);

                System.out.println(canvasWidth);
                System.out.println(abscissa_per.getValue());

                symetryPointX = (int) Math.round(canvasWidth * abscissa_per.getValue());
                symetryPointY = (int) Math.round(canvasHeight * ordinate_per.getValue());

            } else {
                symetryPointX = Integer.parseInt(tokens[1]);
                symetryPointY = Integer.parseInt(tokens[2]);
            }

            //Temporary Cursor
            int tmpCursorId = cursors.smallestAvailableId();
            Cursor tmpCursor = new Cursor(tmpCursorId);
            tmpCursor.duplicate(cursor);

            //Applying symmetry

            tmpCursor.setPositionX((int) Math.round(2 * symetryPointX - cursor.getPositionX()));
            tmpCursor.setPositionY((int) Math.round(2 * symetryPointY - cursor.getPositionY()));
            tmpCursor.turn(180);
            interfaceInstance.drawCursor(tmpCursor);
            if (!instruction.contains("{") && !instruction.contains("}")){
                interfaceInstance.removeCursor(tmpCursor);
                throw new MirrorException("There is no { and/or } in block");
            }
            String Block = instruction.substring(instruction.indexOf("{") + 1, instruction.lastIndexOf("}"));

            try {
                /*
                * The block is executed for the selected cursor and then for temporary one.
                 */
                interpretWithoutDELAY(Block, interfaceInstance, cursors, cursor, variables,numLine);
                interpretWithoutDELAY(Block, interfaceInstance, cursors, tmpCursor, variables,numLine);
                interfaceInstance.removeCursor(tmpCursor);


                } catch (IllegalArgumentException e) {
                    interfaceInstance.removeCursor(tmpCursor);
                    throw new MirrorException(e.getMessage());

                } catch (Exception e) {
                    interfaceInstance.removeCursor(tmpCursor);
                    throw new MirrorException(e.getMessage());
                }

        }

        // symmetrical axis
        else if (tokens[5].contains("{")) {

            //Coordinates of the symmetry axis
            int x1, y1, x2, y2;

            //Managing the parameters in percentages
            if (tokens[1].endsWith("%") && tokens[2].endsWith("%") && tokens[3].endsWith("%") && tokens[4].endsWith("%")) {
                double canvasHeight = interfaceInstance.getDrawingPaneHeight();
                double canvasWidth = interfaceInstance.getDrawingPaneWidth();

                Percentage x1_per = new Percentage(tokens[1]);
                Percentage y1_per = new Percentage(tokens[2]);
                Percentage x2_per = new Percentage(tokens[3]);
                Percentage y2_per = new Percentage(tokens[4]);

                x1 = (int) Math.round(canvasWidth * x1_per.getValue());
                y1 = (int) Math.round(canvasHeight * y1_per.getValue());
                x2 = (int) Math.round(canvasWidth * x2_per.getValue());
                y2 = (int) Math.round(canvasHeight * y2_per.getValue());

            } else {
                x1 = Integer.parseInt(tokens[1]);
                y1 = Integer.parseInt(tokens[2]);
                x2 = Integer.parseInt(tokens[3]);
                y2 = Integer.parseInt(tokens[4]);
            }

            interfaceInstance.drawLine(x1, y1, x2, y2, 2, 0, 0, 0, 1);

            Cursor tmpCursor;
            tmpCursor = cursor.createMirrorAxis(x1, y1, x2, y2, cursors);
            int tmpCursorId = tmpCursor.getId();

            cursors.removeCursor(tmpCursorId);
        }
    }


    /**
     * Executes the NUM command and creates a new Num (extension of Variable) Object.
     * @param tokens Contains the name of the new variable and eventually its value (a Double).
     * @param interfaceInstance
     * @param vars
     */
    private static void executeNum(String[] tokens, Interface interfaceInstance, MapVariable vars) {
        Variable.isValidName(tokens[1]);
        try {
            String newId = tokens[1];
            if(vars.containsKey(newId)){
                throw new IllegalArgumentException();
            }
            Num newVar = null;
            if(tokens.length == 2) {
                newVar = new Num(newId, 0.0);

            } else {
                double val = Double.parseDouble(tokens[3]);
                newVar = new Num(newId,val);
            }
            vars.addVariable(newVar);

        } catch (NumberFormatException e) {
            interfaceInstance.addHistory("Error: Invalid input in NUM",Color.RED);
        }catch (IllegalArgumentException e){
            interfaceInstance.addHistory("The variable "+tokens[1]+" already exists",Color.RED);
        }
    }

    /**
     * Executes the STR command and creates a new Str (extension of Variable) Object.
     * @param tokens Contains the name of the new variable and eventually its value (a String).
     * @param interfaceInstance
     * @param vars
     * @throws IllegalArgumentException
     */

    private static void executeStr(String[] tokens, Interface interfaceInstance, MapVariable vars) throws IllegalArgumentException {
        Variable.isValidName(tokens[1]);
        try {
            String newId = tokens[1];
            if(vars.containsKey(newId)){
                throw new IllegalArgumentException();
            }
            Str newVar = null;
            if(tokens.length == 2) {
                newVar = new Str(newId,"");
            } else {
                newVar = new Str(newId,tokens[3]);
            }
            vars.addVariable(newVar);
        } catch (NumberFormatException e) {
            interfaceInstance.addHistory("Error: Invalid input in STR",Color.RED);
        }catch (IllegalArgumentException e){
            interfaceInstance.addHistory("The variable "+tokens[1]+" already exists",Color.RED);
        }
    }

    /**
     * Executes the BOOL command and creates a new Bool (extension of Variable) Object.
     * @param tokens Contains the name of the new variable and eventually its value : "true" or "false".
     * @param interfaceInstance
     * @param vars
     */
    private static void executeBool(String[] tokens, Interface interfaceInstance, MapVariable vars) {
        Variable.isValidName(tokens[1]);
        try {
            String newId = tokens[1];
            if(vars.containsKey(newId)){
                throw new IllegalArgumentException();
            }
            Bool newVar = null;
            if(tokens.length == 2) {
                newVar = new Bool(newId,false);
            } else {
                boolean val = evaluateBooleanExpression(tokens[3]);
                newVar = new Bool(newId,val);
            }
            vars.addVariable(newVar);

        } catch (NumberFormatException e) {
            interfaceInstance.addHistory("Error: Invalid input in BOOL",Color.RED);
        } catch (IllegalArgumentException e){
            interfaceInstance.addHistory("The variable "+tokens[1]+" already exists",Color.RED);
        }
    }

    /**
     * Executes the DEL instruction and remove from vars (Map of the existing variables) the designated variable.
     * @param tokens Contains the name (Key in the MapVariable) of the variable to remove as tokens[1].
     * @param interfaceInstance
     * @param vars
     */
    private static void executeDel(String[] tokens, Interface interfaceInstance, MapVariable vars) {
        try{
            String varName = tokens[1];
            vars.removeVariable(varName);
        }
        catch (IllegalArgumentException e){
            interfaceInstance.addHistory(e.getMessage(), Color.RED);
        }
    }
}

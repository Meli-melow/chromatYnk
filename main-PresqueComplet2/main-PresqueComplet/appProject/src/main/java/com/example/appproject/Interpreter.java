package com.example.appproject;

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
 * The interpret method is used to execute the instructions asked by the user.
 *
 * The instruction takes the form of a String composed of tokens (Strings separated by " ").
 * The first token is the actual instruction (FWD, BWD, HIDE, ...), it is caught with a switch to execute the right
 * behavior for each possible instruction, or throw an exception if no known instruction is detected.
 *
 * For each instruction, the selected Cursor object has its attributes updated and the Interface object, which is
 * the drawing area seen by the user, is updated.
 *
 */

public class Interpreter {

    static boolean stop = false;
    //TODO : documentation


    public boolean getStop(){
        return stop;
    }
    /**
     * The main method of Interpreter, it identifies with a switch what instruction has been called and executes it, or
     * throws an exception.
     * Every executed instruction or Errors/Exceptions are stored in the History and printed in the history console on
     * the interface.
     *
     * @param input The command entered by the user. It is divided in a List of tokens, the first token is the instruction
     *              to execute, the rest are the values/parameters.
     * @param interfaceInstance The interface used to draw.
     * @param cursors Map of existing cursors.
     * @param cursor The selected cursor. The instruction will be applied on it.
     * @param variables Map of existing variables.
     */

    public static void interpret(String input, Interface interfaceInstance, MapCursor cursors, Cursor cursor, MapVariable variables) throws RuntimeException {

        List<String> instructions = splitCommand(input);
        interfaceInstance.addHistory(String.valueOf(interfaceInstance.isChecked),Color.RED);


        if (instructions.isEmpty()) {
            return;
        }
        stop = false;
        Timeline timeline = new Timeline();
        int index = 1;
        for (String instruction : instructions) {
            if((stop) && (!interfaceInstance.ignoreErrors())){
                break;
            }
            final String currentInstruction = instruction;

            PauseTransition pause = new PauseTransition(Duration.millis(interfaceInstance.executeTime * index));
            pause.setOnFinished(event -> {
                try {
                    executeInstruction(currentInstruction, interfaceInstance, cursors, cursor, variables);
                } catch (Exception e) {


                    String error = "Error executing command: " + currentInstruction +" "+ e;
                    interfaceInstance.addHistory("Error executing command: " + currentInstruction +" "+ e.getMessage(), Color.RED);
                    e.printStackTrace();
                    stop = true; // Stop processing further instructions
                    throw new RuntimeException(error);

                }
            });
            pause.play();

            index++;
        }
    }




    private static void executeInstruction(String instruction, Interface interfaceInstance, MapCursor cursors, Cursor cursor, MapVariable variables) throws Exception {
        try {

                String[] tokens = instruction.split(" ");
                switch (tokens[0]) {
                    case "FWD":
                        executeFwd(tokens, interfaceInstance, cursor);
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
                        cursor.setThickness(Double.parseDouble(tokens[1]));
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
                        System.out.println("lookat command" + tokens[0] + tokens[1]);
                        executeLookAt(tokens, interfaceInstance, cursors, cursor);
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
                        handleForLoop(tokens, instruction, interfaceInstance, cursors, cursor, variables);
                        break;
                    case "IF":
                        executeIf(tokens, instruction, interfaceInstance, cursors, cursor, variables);
                        break;
                    case "WHILE":
                        executeWhile(tokens, instruction, interfaceInstance, cursors, cursor, variables);
                        break;
                    case "MIMIC":
                        executeMimic(tokens, instruction, interfaceInstance, cursors, cursor, variables);
                        break;
                    case "MIRROR":
                        executeMirror(tokens, instruction, interfaceInstance, cursors, cursor, variables);
                        break;
                    case "NUM":
                        //TODO : get value of created variable
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
                        interfaceInstance.addHistory("Unknown command: " + tokens[0] , Color.RED);
                        throw new IllegalArgumentException();
                }

            }
         catch (IllegalArgumentException e) {
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
     */
    private static void executeFwd(String[] tokens, Interface interfaceInstance, Cursor cursor) throws Exception {
        try {
            int distance;

            /*
             * If the user enters a percentage, it adapts the forward method so the distance is the percentage of
             * the largest dimension of the canvas between width and height.
             */
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

                interfaceInstance.checkPosition(tempX + distance * Math.cos(Math.toRadians(cursor.getDirection())), tempY + distance * Math.sin(Math.toRadians(cursor.getDirection())));
                cursor.forward(distance);
                interfaceInstance.checkPosition(cursor.getPositionX(), cursor.getPositionY());
                interfaceInstance.moveCursor(cursor);
                interfaceInstance.drawLine(tempX, tempY, cursor.getPositionX(), cursor.getPositionY(), cursor.getThickness(),
                        cursor.getColorj().getRgb()[0], cursor.getColorj().getRgb()[1], cursor.getColorj().getRgb()[2], cursor.getOpacity());

            }
        } catch (NumberFormatException e) {
            interfaceInstance.addHistory(e.getMessage(), Color.RED);
            throw new Exception(e.getMessage());
        } catch (IllegalArgumentException e){
            interfaceInstance.addHistory(e.getMessage(), Color.RED);
            throw new Exception(e.getMessage());
        } catch (OutOfPositionException e) {
            interfaceInstance.addHistory(e.getMessage(), Color.RED);
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Executes the BWD (backward) instruction.
     * It moves the cursor following its direction by the "distance" entered by the user.
     * It takes into account if the user entered a percentage, symbolized with a '%' or not.
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
            interfaceInstance.addHistory(e.getMessage(), Color.RED);
            throw new Exception(e.getMessage());
        } catch (IllegalArgumentException e){
            interfaceInstance.addHistory(e.getMessage(), Color.RED);
            throw new Exception(e.getMessage());
        } catch (OutOfPositionException e) {
            interfaceInstance.addHistory(e.getMessage(), Color.RED);
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Executes the POS (position) instruction.
     * It changes the coordinates of the selected cursor to values entered as arguments.
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

                    cursor.setPositionX(newPosX);
                    cursor.setPositionY(newPosY);
                } else {
                    cursor.setPositionX(Integer.parseInt(tokens[1]));
                    cursor.setPositionY(Integer.parseInt(tokens[2]));
                }
                interfaceInstance.moveCursor(cursor);
            } catch (NumberFormatException e) {
                interfaceInstance.addHistory(e.getMessage(), Color.RED);
                throw new Exception(e.getMessage());
            } catch (IllegalArgumentException e){
                interfaceInstance.addHistory(e.getMessage(), Color.RED);
                throw new Exception(e.getMessage());
            } catch (OutOfPositionException e) {
                interfaceInstance.addHistory(e.getMessage(), Color.RED);
                throw new Exception(e.getMessage());
            }

        }
    }

    /**
     * Executes the MOV (move) instruction.
     * As executePos() but it draws the line between the last position of the cursor and the new one.
     */
    private static void executeMove(String[] tokens, Interface interfaceInstance, Cursor cursor) throws Exception {
        if (cursor != null) {
            int tempX = cursor.getPositionX();
            int tempY = cursor.getPositionY();
            try {
                interfaceInstance.checkPosition(tempX,tempY);
            } catch (OutOfPositionException e) {
                interfaceInstance.addHistory(e.getMessage(),Color.RED);
            }

            executePos(tokens, interfaceInstance, cursor);

            interfaceInstance.drawLine(tempX, tempY, cursor.getPositionX(), cursor.getPositionY(), cursor.getThickness(), cursor.getColorj().getRgb()[0],
                    cursor.getColorj().getRgb()[1], cursor.getColorj().getRgb()[2], cursor.getOpacity());
        }
    }

    /**
     * Executes the LOOKAT instructions which can be called with different parameters.
     * The coordinates as integers, a cursor ID as an integer or the abscissa and ordinate in percentages of the canvas.
     * @param mapCursor The Map of cursors is needed when the selected cursor is asked to look at another cursor.
     */
    private static void executeLookAt(String[] tokens, Interface interfaceInstance, MapCursor mapCursor, Cursor cursor) throws Exception {
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
                    int posX = Integer.parseInt(tokens[1]);
                    int posY = Integer.parseInt(tokens[2]);

                    cursor.lookAt(posX, posY);
                }
            }
            interfaceInstance.moveCursor(cursor);
        }
        catch (NumberFormatException e) {
            interfaceInstance.addHistory(e.getMessage(), Color.RED);
            throw new Exception(e.getMessage());
        } catch (IllegalArgumentException e){
            interfaceInstance.addHistory(e.getMessage(), Color.RED);
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
            interfaceInstance.addHistory(e.getMessage(),Color.RED);
            throw new Exception(e.getMessage());
        } catch (IllegalArgumentException e) {
            interfaceInstance.addHistory(e.getMessage(), Color.RED);
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Executes the CURSOR instruction by creating a Cursor Object and displaying it on the drawing area.
     * The new Cursor is placed at the center of the drawing area.
     * @param tokens
     * @param interfaceInstance
     * @param cursors
     * @throws Exception
     */
    private static void executeCursor(String[] tokens, Interface interfaceInstance, MapCursor cursors) throws Exception {
        try {
            int cursorId = Integer.parseInt(tokens[1]);
            Cursor existingCursor = cursors.getCursorById(cursorId);
            if (existingCursor != null) {
                throw new Exception("Error: Cursor with ID " + cursorId + " already exists.");
            }
            Cursor newCursor = new Cursor(cursorId);
            //A new cursor is by default in X=0, Y = 0. We position it at the center of the drawing Pane for convenience.
            newCursor.position(new Percentage(0.5),new Percentage(0.5), (int) interfaceInstance.getDrawingPaneWidth(), (int) interfaceInstance.getDrawingPaneHeight());
            cursors.addCursor(newCursor);
            interfaceInstance.drawCursor(newCursor);
        } catch (NumberFormatException e) {
            throw new Exception("Error: Invalid input to create a Cursor.");
        }
        catch (Exception e){
            throw new Exception(e);
        }
    }

    private static void handleForLoop(String[] tokens, String instruction, Interface interfaceInstance, MapCursor cursors, Cursor cursor, MapVariable variables) throws Exception {
        try {


        if (tokens.length < 3) {
            interfaceInstance.addHistory("Error: Invalid FOR loop syntax",Color.RED);
            return;
        }

        String variableName = tokens[1];
        if(variables.containsKey(variableName)){
            throw new IllegalArgumentException();
        }

        int from = 0;
        int to;
        int step = 1;

        int currentIndex = 2;
        if (tokens[currentIndex].equals("FROM")) {
            from = Integer.parseInt(tokens[++currentIndex]);
            currentIndex++;


            if (!tokens[currentIndex].equals("TO") || !tokens[currentIndex + 2].equals("STEP")) {
                interfaceInstance.addHistory("Error: Invalid FOR loop syntax",Color.RED);
                return;
            }
            to = Integer.parseInt(tokens[++currentIndex]);
            currentIndex++;
            step = Integer.parseInt(tokens[++currentIndex]);
            currentIndex++;
        } else {
            if (!tokens[currentIndex].equals("TO")) {
                interfaceInstance.addHistory("Error: Invalid FOR loop syntax "+ tokens[currentIndex],Color.RED);
                return;
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
            interpret(finalCommand, interfaceInstance, cursors, cursor, variables);
            variables.removeVariable(var.getVarId());
        } else {
            interfaceInstance.addHistory("Error: Invalid FOR loop syntax",Color.RED);
        }
        }catch (IllegalArgumentException e){
            throw new Exception("Error : variable "+tokens[1]+" already exist");
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    private static void executeIf(String[] tokens, String instruction, Interface interfaceInstance, MapCursor cursors, Cursor cursor,MapVariable variables) throws Exception {
        try {

        if (tokens.length < 2) {
            throw new IllegalAccessException("Error : Invalid IF syntax");
        }
        String condition = instruction.substring(instruction.indexOf("IF") + 3, instruction.indexOf('{')).trim();
        String block = instruction.substring(instruction.indexOf("{") + 1, instruction.lastIndexOf("}")).trim();
        if (evaluateBooleanExpression(condition)) {
            List<String> commands = splitCommand(block);
            for (String command : commands) {
                interpret(command, interfaceInstance, cursors, cursor, variables);
            }
        }
        } catch (IllegalArgumentException e) {
            throw new Exception(e);
        }
    }

    private static boolean evaluateBooleanExpression(String condition) {
        // Supprimer les espaces inutiles
        condition = condition.replaceAll("\\s+", "");

        // Diviser la condition en parties, en utilisant les opérateurs logiques comme délimiteurs
        String[] logicalParts = condition.split("(\\|\\||&&)");

        // Initialiser le résultat global à true
        boolean globalResult = true;

        // Parcourir chaque partie de la condition logique
        for (String logicalPart : logicalParts) {
            // Vérifier si la partie commence par le non-logique ("!")
            boolean negated = false;
            if (logicalPart.startsWith("!")) {
                negated = true;
                logicalPart = logicalPart.substring(1); // Supprimer le "!"
            }

            // Diviser la partie en utilisant les opérateurs de comparaison comme délimiteurs
            String[] comparisonParts = logicalPart.split("(==|!=|<=|>=|<|>)");

            // Vérifier les opérateurs de comparaison
            if (comparisonParts.length != 2) {
                // Si la partie ne contient pas exactement deux éléments après la division, la condition est invalide
                return false;
            }

            // Extraire les opérateurs et les opérandes
            String operator = logicalPart.replaceAll("[a-zA-Z0-9]", "");
            int operand1 = Integer.parseInt(comparisonParts[0]);
            int operand2 = Integer.parseInt(comparisonParts[1]);

            // Vérifier l'opérateur et évaluer la condition de comparaison
            boolean result = false;
            switch (operator) {
                case "==":
                    result = (operand1 == operand2);
                    break;
                case "!=":
                    result = (operand1 != operand2);
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
                    // Opérateur de comparaison invalide
                    return false;
            }

            // Si la partie était négative, inverser le résultat
            if (negated) {
                result = !result;
            }

            // Si l'opérateur logique est "||", mettre à jour le résultat global
            if (condition.contains("||")) {
                globalResult = globalResult || result;
            } else if (condition.contains("&&")) {
                // Si l'opérateur logique est "&&", arrêter l'évaluation dès qu'une partie est fausse
                if (!result) {
                    return false;
                }
            } else {
                // Si aucun opérateur logique n'est spécifié, le résultat global est égal au résultat de la partie actuelle
                globalResult = result;
            }
        }

        // Renvoyer le résultat global
        return globalResult;
    }


    private static void executeWhile(String[] tokens, String instruction, Interface interfaceInstance, MapCursor cursors, Cursor cursor,MapVariable variables) throws Exception {
        if (tokens.length < 2) {
            interfaceInstance.addHistory("Error : Invalid While syntax",Color.RED);
            return;
        }
        String condition = instruction.substring(instruction.indexOf("WHILE") + 3, instruction.indexOf('{')).trim();
        String block = instruction.substring(instruction.indexOf("{") + 1, instruction.lastIndexOf("}")).trim();
        while (evaluateBooleanExpression(condition)) {
            List<String> commands = splitCommand(block);
            for (String command : commands) {
                interpret(command, interfaceInstance, cursors, cursor, variables);
            }
        }
    }

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

            if ((character == ';') && (count == 0)) {
                intsructionSplit.add(tokens.toString().trim());
                tokens.setLength(0);
            } else {
                tokens.append(character);
            }
        }
        if (tokens.length() > 0) {
            intsructionSplit.add(tokens.toString().trim());
        }
        return intsructionSplit;
    }

    private static void executeMimic(String[] tokens, String instruction, Interface interfaceInstance, MapCursor cursors, Cursor cursor, MapVariable variables) throws Exception {
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

        String Block = instruction.substring(instruction.indexOf("{") + 1, instruction.lastIndexOf("}"));
        List<String> commands = splitCommand(Block);
        for (String command : commands) {
            try {
                /*
                 * First executes the command for the targeted cursor and then the temporary one. Commands by commands
                 * in the block
                 */
                interpret(command, interfaceInstance, cursors, cursors.getCursorById(modelCursorId),variables);
                interpret(command, interfaceInstance, cursors, tmpCursor,variables);
            } catch (Exception e) {
                interfaceInstance.addHistory("Error : one or more instructions invalid in MIMIC block",Color.RED);
                cursors.removeCursor(tmpCursorId);
            }
        }
        //At the end, the temporary cursor is removed.
        cursors.removeCursor(tmpCursorId);
    }

    //TODO : MIRROR axial

    private static void executeMirror(String[] tokens, String instruction, Interface interfaceInstance, MapCursor cursors, Cursor cursor,MapVariable variables) throws Exception {
        //central symmetry
        if (tokens[3].contains("{")) {
            double symetryPointX;
            double symetryPointY;
            if (tokens[1].endsWith("%") && tokens[2].endsWith("%")) {
                double canvasHeight = interfaceInstance.getDrawingPaneHeight();
                double canvasWidth = interfaceInstance.getDrawingPaneWidth();
                Percentage abscissa_per = new Percentage(tokens[1]);
                Percentage ordinate_per = new Percentage(tokens[2]);

                System.out.println(canvasWidth);
                System.out.println(abscissa_per.getValue());

                symetryPointX = Math.round(canvasWidth * abscissa_per.getValue());
                symetryPointY = Math.round(canvasHeight * ordinate_per.getValue());

            } else {
                symetryPointX = Double.parseDouble(tokens[1]);
                symetryPointY = Double.parseDouble(tokens[2]);
            }

            //Temporary Cursor
            int tmpCursorId = cursors.smallestAvailableId();
            Cursor tmpCursor = new Cursor(tmpCursorId);
            tmpCursor.duplicate(cursor);

            //Applying symmetry
            try {
                tmpCursor.setPositionX((int) Math.round(2 * symetryPointX - cursor.getPositionX()));
                tmpCursor.setPositionY((int) Math.round(2 * symetryPointY - cursor.getPositionY()));
                tmpCursor.turn(180);
                interfaceInstance.drawCursor(tmpCursor);
                String Block = instruction.substring(instruction.indexOf("{") + 1, instruction.lastIndexOf("}"));

                try {
                    /*
                     * Exécute d'abord la commande pour le curseur ciblé, puis pour celui temporaire. Commande par commande
                     * dans le bloc
                     */
                    interpret(Block, interfaceInstance, cursors, cursor, variables);
                    interpret(Block, interfaceInstance, cursors, tmpCursor, variables);


                } catch (IllegalArgumentException e) {
                    System.out.println("Erreur : une ou plusieurs instructions invalides dans le bloc MIRROR");
                    cursors.removeCursor(tmpCursorId);
                }

                interfaceInstance.addHistory(String.valueOf(tmpCursorId),Color.BURLYWOOD);
                cursors.removeCursor(tmpCursorId);
                interfaceInstance.addHistory("LCKCLFGRLFOGK",Color.BURLYWOOD);

            } catch (OutOfPositionException e) {
                interfaceInstance.addHistory("Error Pos in mirror",Color.RED);
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



    private static void executeNum(String[] tokens, Interface interfaceInstance, MapVariable vars) {
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

    private static void executeStr(String[] tokens, Interface interfaceInstance, MapVariable vars) {
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

    private static void executeBool(String[] tokens, Interface interfaceInstance, MapVariable vars) {
        try {
            String newId = tokens[1];
            if(vars.containsKey(newId)){
                throw new IllegalArgumentException();
            }
            Bool newVar = null;
            if(tokens.length == 2) {
                newVar = new Bool(newId,false);
            } else {
                boolean val = Boolean.parseBoolean(tokens[3]);
                newVar = new Bool(newId,val);
            }
            vars.addVariable(newVar);

        } catch (NumberFormatException e) {
            interfaceInstance.addHistory("Error: Invalid input in BOOL",Color.RED);
        } catch (IllegalArgumentException e){
            interfaceInstance.addHistory("The variable "+tokens[1]+" already exists",Color.RED);
        }
    }

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

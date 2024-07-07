package com.example.appproject;

import javafx.animation.Timeline;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * The interpret method is used to execute the instructions asked by the user.
 *
 * The instruction takes the form of a String composed of tokens.
 * The first token is the actual instruction (FWD, BWD, HIDE, ...), it is caught with a switch to execute the right
 * behavior for each possible instruction, or throw an exception if no known instructions is detected.
 *
 * For each instruction, the selected Cursor object has its attributes updated and the Interface object, which is
 * the drawing area seen by the user, is updated.
 *
 */

public class Interpreter {
    private static Set<String> existingVariables = new HashSet<>();

    //TODO : documentation

    //TODO: Vitesse d'exécution

    //TODO: Out of bounds c'est bon normalement !:!!! (;

    //TODO: Classe Variable

    public static void interpret(String input, Interface interfaceInstance, MapCursor cursors, Cursor cursor /*, MapVariables variables, Variable variable*/) {
        try {


            List<String> instructions = splitCommand(input);
            for (String instruction : instructions) {

                String[] tokens = instruction.split(" ");
                switch (tokens[0]) {
                    case "FWD":
                        executeFwd(tokens, interfaceInstance, cursor);
                        interfaceInstance.addHistory("Cursor " + cursor.getId() + " : " + tokens[0] + tokens[1], Color.BLACK);
                        break;
                    case "BWD":
                        executeBwd(tokens, interfaceInstance, cursor);
                        interfaceInstance.addHistory("Cursor " + cursor.getId() + " : " + tokens[0] + tokens[1], Color.BLACK);
                        break;
                    case "TURN":
                        cursor.turn(Double.parseDouble(tokens[1]));
                        interfaceInstance.moveCursor(cursor);
                        interfaceInstance.addHistory("Cursor " + cursor.getId() + " : " + tokens[0] + tokens[1], Color.BLACK);
                        break;
                    case "COLOR":
                        applyColor(tokens, interfaceInstance, cursor);
                        interfaceInstance.addHistory("Cursor " + cursor.getId() + " : " + tokens[0] + tokens[1], Color.BLACK);
                        break;
                    case "THICK":
                        cursor.setThickness(Double.parseDouble(tokens[1]));
                        interfaceInstance.addHistory("Cursor " + cursor.getId() + " : " + tokens[0] + tokens[1], Color.BLACK);
                        break;
                    case "PRESS":
                        cursor.setOpacity(Double.parseDouble(tokens[1]));
                        interfaceInstance.addHistory("Cursor " + cursor.getId() + " : " + tokens[0] + tokens[1], Color.BLACK);
                        break;
                    case "MOV":
                        executeMove(tokens, interfaceInstance, cursor);
                        interfaceInstance.addHistory("Cursor " + cursor.getId() + " : " + tokens[0] + tokens[1] + " " + tokens[2], Color.BLACK);
                        break;
                    case "POS":
                        executePos(tokens, interfaceInstance, cursor);
                        interfaceInstance.addHistory("Cursor " + cursor.getId() + " : " + tokens[0] + tokens[1] + " " + tokens[2], Color.BLACK);
                        break;
                    case "LOOKAT":
                        System.out.println("lookat command" + tokens[0] + tokens[1]);
                        executeLookAt(tokens, interfaceInstance, cursors, cursor);
                        interfaceInstance.addHistory("Cursor " + cursor.getId() + " : " + tokens[0] + tokens[1], Color.BLACK);
                        break;
                    case "HIDE":
                        cursor.setHidden(false);
                        interfaceInstance.moveCursor(cursor);
                        interfaceInstance.addHistory("Cursor " + cursor.getId() + " : " + tokens[0], Color.BLACK);
                        break;
                    case "SHOW":
                        cursor.setHidden(true);
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
                        handleForLoop(tokens, instruction, interfaceInstance, cursors, cursor);
                        break;
                    case "IF":
                        executeIf(tokens, instruction, interfaceInstance, cursors, cursor);
                    case "WHILE":
                        executeWhile(tokens, instruction, interfaceInstance, cursors, cursor);
                    case "MIMIC":
                        executeMimic(tokens, instruction, interfaceInstance, cursors, cursor);
                    case "MIRROR":
                        executeMirror(tokens, instruction, interfaceInstance, cursors, cursor);
                    /*
                    case "NUM":
                        exceuteNum(tokens, instruction, interfaceInstance, variables, variable);
                        interfaceInstance.addHistory(" new Variable " + tokens[1]);
                        break;*/
                    default:
                        interfaceInstance.addHistory("Unknown command: " + tokens[0], Color.RED);
                        throw new IllegalArgumentException("Unknown command: " + tokens[0]);
                }

            }
        } catch (IllegalArgumentException e) {

            throw e;
        }
    }

    /**
     * Executes the FORWARD instruction.
     * It moves the cursor following its direction by the "distance" entered by the user.
     * It takes into account if the user entered a percentage, symbolized with a '%' or not.
     *
     * @param tokens
     * @param interfaceInstance
     * @param cursor
     */
    private static void executeFwd(String[] tokens, Interface interfaceInstance, Cursor cursor) {
        try {
            int distance;

            //If the user enters a percentage, it adapts the forward method so the distance is the percentage of
            //the largest dimension of the canvas between width and height.
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
                interfaceInstance.checkPosition(tempX+distance * Math.cos(Math.toRadians(cursor.getDirection())),tempY+ distance * Math.sin(Math.toRadians(cursor.getDirection())));
                cursor.forward(distance);
                interfaceInstance.checkPosition(cursor.getPositionX(), cursor.getPositionY());
                interfaceInstance.moveCursor(cursor);
                interfaceInstance.drawLine(tempX, tempY, cursor.getPositionX(), cursor.getPositionY(), cursor.getThickness(),
                        cursor.getColorj().getRgb()[0], cursor.getColorj().getRgb()[1], cursor.getColorj().getRgb()[2],cursor.getOpacity());

            }
        } catch (NumberFormatException e) {
            // Handle invalid number format
            e.getMessage();
        } catch (OutOfPositionException e) {
            // Handle out of position exception
            e.getMessage();
        }
    }

    private static void executeBwd(String[] tokens, Interface interfaceInstance, Cursor cursor) {
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
            // Handle invalid number format
            e.getMessage();
        } catch (OutOfPositionException e) {
            // Handle out of position exception
        }
    }

    /**
     * Execute the POS instruction.
     */
    private static void executePos(String[] tokens, Interface interfaceInstance, Cursor cursor) {
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
            } catch (OutOfPositionException e) {
            }

        }
    }

    /**
     * Execute the MOV instruction.
     * As executePos() but it draws the line between the last position of the cursor and the new one.
     */
    private static void executeMove(String[] tokens, Interface interfaceInstance, Cursor cursor) {
        if (cursor != null) {
            int tempX = cursor.getPositionX();
            int tempY = cursor.getPositionY();
            try {
                interfaceInstance.checkPosition(tempX,tempY);
            } catch (OutOfPositionException e) {

            }

            executePos(tokens, interfaceInstance, cursor);

            interfaceInstance.drawLine(tempX, tempY, cursor.getPositionX(), cursor.getPositionY(), cursor.getThickness(), cursor.getColorj().getRgb()[0],
                    cursor.getColorj().getRgb()[1], cursor.getColorj().getRgb()[2], cursor.getOpacity());
        }
    }

    /**
     * Manages the LOOKAT instructions which can be called with different parameters.
     * The coordinates as integers, a cursor ID as an integer or percentages in abscissa and ordinate of the canvas.
     */
    private static void executeLookAt(String[] tokens, Interface interfaceInstance, MapCursor mapCursor, Cursor cursor) {
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
    ///Gerer les exceptions, notamment double >1, rgb <0 et >255 ...
    private static void applyColor(String tokens[], Interface interfaceInstance, Cursor cursor) {
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
    }

    private static void executeCursor(String[] tokens, Interface interfaceInstance, MapCursor cursors) {
        try {
            int cursorId = Integer.parseInt(tokens[1]);
            Cursor existingCursor = cursors.getCursorById(cursorId);
            if (existingCursor != null) {
                interfaceInstance.Console.appendText("Error: Cursor with ID " + cursorId + " already exists\n");
                return;
            }
            Cursor newCursor = new Cursor(cursorId);
            cursors.addCursor(newCursor);
            interfaceInstance.drawCursor(newCursor);
        } catch (NumberFormatException e) {
            interfaceInstance.Console.appendText("Error: Invalid input\n");
        }
    }

    /*
    private static void executeNum(String[] tokens, Interface interfaceInstance, MapVariable vars) {
        try {
            //TODO : convert arg to Variable
            int varId = Integer.parseInt(tokens[1].getVarId());
            Variable existingVar = vars.getVariableById(varId);
            if (existingVar != null) {
                interfaceInstance.Console.appendText("Error: Variable with ID " + varId + " already exists\n");
                return;
            }
            if(token[2].equalsTo("=") {

                Variable newVar = new Num(varId);
                vars.addVar(newVar);
            }
        } catch (NumberFormatException e) {
            interfaceInstance.Console.appendText("Error: Invalid input\n");
        }
    }

     */

    private static void handleForLoop(String[] tokens, String instruction, Interface interfaceInstance, MapCursor cursors, Cursor cursor) {
        if (tokens.length < 3) {
            interfaceInstance.Console.appendText("Error: Invalid FOR loop syntax\n");
            return;
        }

        String variableName = tokens[1];
        if (existingVariables.contains(variableName)) {
            interfaceInstance.Console.appendText("Error: Variable " + variableName + " already exists\n");
            return;
        }

        int from = 0;
        int to;
        int step = 1;

        int currentIndex = 2;
        if (tokens[currentIndex].equals("FROM")) {
            from = Integer.parseInt(tokens[++currentIndex]);
            currentIndex++;


            if (!tokens[currentIndex].equals("TO") || !tokens[currentIndex + 2].equals("STEP")) {
                interfaceInstance.Console.appendText("Error: Invalid FOR loop syntax\n");
                return;
            }
            to = Integer.parseInt(tokens[++currentIndex]);
            currentIndex++;
            step = Integer.parseInt(tokens[++currentIndex]);
            currentIndex++;
        } else {
            if (!tokens[currentIndex].equals("TO")) {
                interfaceInstance.Console.appendText("Error: Invalid FOR loop syntax\n" + tokens[currentIndex]);
                return;
            }
            to = Integer.parseInt(tokens[++currentIndex]);
            currentIndex++;
        }


        System.out.println(tokens[currentIndex] + "//" + currentIndex);
        if (tokens[currentIndex].startsWith("{")) {

            String stepBlock = instruction.substring(instruction.indexOf("{") + 1, instruction.lastIndexOf("}"));
            List<String> commands = splitCommand(stepBlock);
            existingVariables.add(variableName);

            for (int i = from; i <= to; i += step) {
                for (String command : commands) {
                    try {
                        String modifiedCommand = command.trim().replace(variableName, String.valueOf(i));
                        interpret(modifiedCommand, interfaceInstance, cursors, cursor);
                    } catch (Exception e) {
                        System.out.println("Error : one or more instructions invalid in FOR loop");
                        existingVariables.remove(variableName);
                    }
                }
            }

            existingVariables.remove(variableName);
        } else {
            interfaceInstance.Console.appendText("Error: Invalid FOR loop syntax c la d\n");
        }
    }

    private static void executeIf(String[] tokens, String instruction, Interface interfaceInstance, MapCursor cursors, Cursor cursor) {
        if (tokens.length < 2) {
            interfaceInstance.Console.appendText("Error : Invalid IF syntax\n");
            return;
        }
        String condition = instruction.substring(instruction.indexOf("IF") + 3, instruction.indexOf('{')).trim();
        String block = instruction.substring(instruction.indexOf("{") + 1, instruction.lastIndexOf("}")).trim();
        if (evaluateBooleanExpression(condition)) {
            List<String> commands = splitCommand(block);
            for (String command : commands) {
                interpret(command, interfaceInstance, cursors, cursor);
            }
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


    private static void executeWhile(String[] tokens, String instruction, Interface interfaceInstance, MapCursor cursors, Cursor cursor) {
        if (tokens.length < 2) {
            interfaceInstance.Console.appendText("Error : Invalid IF syntax\n");
            return;
        }
        String condition = instruction.substring(instruction.indexOf("WHILE") + 3, instruction.indexOf('{')).trim();
        String block = instruction.substring(instruction.indexOf("{") + 1, instruction.lastIndexOf("}")).trim();
        while (evaluateBooleanExpression(condition)) {
            List<String> commands = splitCommand(block);
            for (String command : commands) {
                interpret(command, interfaceInstance, cursors, cursor);
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

    private static void executeMimic(String[] tokens, String instruction, Interface interfaceInstance, MapCursor cursors, Cursor cursor) {
        if (tokens.length < 2) {
            interfaceInstance.Console.appendText("Error : Invalid IF syntax\n");
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
                interpret(command, interfaceInstance, cursors, cursors.getCursorById(modelCursorId));
                interpret(command, interfaceInstance, cursors, tmpCursor);
            } catch (Exception e) {
                System.out.println("Error : one or more instructions invalid in MIMIC block");
                cursors.removeCursor(tmpCursorId);
            }
        }
        //At the end, the temporary cursor is removed.
        cursors.removeCursor(tmpCursorId);
    }

    //TODO : MIRROR axial

    private static void executeMirror(String[] tokens, String instruction, Interface interfaceInstance, MapCursor cursors, Cursor cursor) {
        //central symetry
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
                tmpCursor.setPositionX((int) Math.round(Math.abs(symetryPointX - cursor.getPositionX()) + symetryPointX));
                tmpCursor.setPositionY((int) Math.round(Math.abs(symetryPointY - cursor.getPositionY()) + symetryPointY));
                tmpCursor.turn(180);
                interfaceInstance.drawCursor(tmpCursor);
                String block = instruction.substring(instruction.indexOf("{") + 1, instruction.lastIndexOf("}")).trim();
                List<String> commands = splitCommand(block);
                for (String command : commands) {
                    try {
                        /*
                         * First executes the command for the targeted cursor and then the temporary one. Commands by commands
                         * in the block
                         */
                        interpret(command, interfaceInstance, cursors, cursors.getCursorById(tmpCursorId));
                        interpret(command, interfaceInstance, cursors, tmpCursor);
                    } catch (Exception e) {
                        System.out.println("Error : one or more instructions invalid in MIRROR block");
                        cursors.removeCursor(tmpCursorId);
                    }
                }
                cursors.removeCursor(tmpCursorId);
            } catch (OutOfPositionException e) {
            }
        }

        // symétrie axiale
        else if (tokens[5].contains("{")) {
            int x1 = Integer.parseInt(tokens[1]);
            int y1 = Integer.parseInt(tokens[2]);
            int x2 = Integer.parseInt(tokens[3]);
            int y2 = Integer.parseInt(tokens[4]);
            Cursor tmpCursor = cursor.createMirrorAxis(x1, y1, x2, y2, cursors);

            int tmpCursorId = cursors.smallestAvailableId();

            tmpCursor.duplicate(cursor);

            interfaceInstance.drawCursor(tmpCursor);
            String Block = instruction.substring(instruction.indexOf("{") + 1, instruction.lastIndexOf("}"));
            List<String> commands = splitCommand(Block);
            for (String command : commands) {
                try {
                    /*
                     * First executes the command for the targeted cursor and then the temporary one. Commands by commands
                     * in the block
                     */
                    interpret(command, interfaceInstance, cursors, cursors.getCursorById(tmpCursorId));
                    interpret(command, interfaceInstance, cursors, tmpCursor);
                } catch (Exception e) {
                    System.out.println("Error : one or more instructions invalid in MIRROR block");
                    cursors.removeCursor(tmpCursorId);
                }
            }
            //At the end, the temporary cursor is removed.
            cursors.removeCursor(tmpCursorId);
        }
    }
}



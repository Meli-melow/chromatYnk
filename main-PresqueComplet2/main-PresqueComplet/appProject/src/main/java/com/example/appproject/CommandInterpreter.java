package com.example.appproject;
/*
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.*;

public class CommandInterpreter {
    private Map<String, Cursor> cursors;
    private Cursor currentCursor;
    private GraphicsContext gc;
    private Map<String, Double> numericVariables;
    private Map<String, String> stringVariables;
    private Map<String, Boolean> booleanVariables;
    private StringBuilder commandHistory;
    private List<LineSegment> lines;
    private int speed;
    private List<String> mimicCursors;
    private List<String> mirrorCursors;
    private boolean stopOnError;
    private boolean ignoreErrors;

    public CommandInterpreter(GraphicsContext gc) {
        this.gc = gc;
        this.cursors = new HashMap<>();
        this.currentCursor = new Cursor();
        this.cursors.put("default", currentCursor);
        this.numericVariables = new HashMap<>();
        this.stringVariables = new HashMap<>();
        this.booleanVariables = new HashMap<>();
        this.commandHistory = new StringBuilder();
        this.lines = new ArrayList<>();
        this.speed = 10;
        this.mimicCursors = new ArrayList<>();
        this.mirrorCursors = new ArrayList<>();
        this.stopOnError = false;
        this.ignoreErrors = false;
        updateDrawing();
    }

    public void setSpeed(int speed) {
        if (speed < 1 || speed > 10) {
            throw new IllegalArgumentException("Speed must be between 1 and 10");
        }
        this.speed = speed;
    }

    public void setStopOnError(boolean stopOnError) {
        this.stopOnError = stopOnError;
    }

    public void setIgnoreErrors(boolean ignoreErrors) {
        this.ignoreErrors = ignoreErrors;
    }

    public void executeCommandsWithDelay(List<String> commands) {
        Timeline timeline = new Timeline();
        int delayMillis = 1000 / speed;

        for (int i = 0; i < commands.size(); i++) {
            final int index = i;
            KeyFrame keyFrame = new KeyFrame(Duration.millis(delayMillis * (i + 1)), event -> {
                try {
                    interpret(commands.get(index));
                } catch (IllegalArgumentException e) {
                    System.err.println("Error: " + e.getMessage());
                    if (stopOnError) {
                        timeline.stop();
                    }
                }
            });
            timeline.getKeyFrames().add(keyFrame);
        }

        timeline.play();
    }

    public void interpret(String command) {
        commandHistory.append(command).append("\n");
        String[] parts = command.split(" ");
        try {
            switch (parts[0].toLowerCase()) {
                case "fwd":
                    validateSingleParam(parts, "fwd");
                    double fwdDistance = parseDistance(parts[1]);
                    moveCursors("fwd", fwdDistance);
                    break;
                case "bwd":
                    validateSingleParam(parts, "bwd");
                    double bwdDistance = parseDistance(parts[1]);
                    moveCursors("bwd", bwdDistance);
                    break;
                case "turn":
                    validateSingleParam(parts, "turn");
                    double angle = parseValue(parts[1]);
                    moveCursors("turn", angle);
                    break;
                case "color":
                    if (parts.length == 2) {
                        validateColor(parts[1]);
                        currentCursor.setColor(parts[1]);
                    } else if (parts.length == 4) {
                        validateRGBColor(parts);
                        currentCursor.setColor(Color.rgb(
                                Integer.parseInt(parts[1]),
                                Integer.parseInt(parts[2]),
                                Integer.parseInt(parts[3])
                        ).toString());
                    } else {
                        throw new IllegalArgumentException("Invalid parameters for color command");
                    }
                    gc.setStroke(Color.web(currentCursor.getColor(), currentCursor.getOpacity()));
                    break;
                case "thick":
                    validateSingleParam(parts, "thick");
                    currentCursor.setThickness(parseValue(parts[1]));
                    gc.setLineWidth(currentCursor.getThickness());
                    break;
                case "press":
                    validateSingleParam(parts, "press");
                    currentCursor.setOpacity(parseOpacity(parts[1]));
                    gc.setGlobalAlpha(currentCursor.getOpacity());
                    break;
                case "move":
                    validateDoubleParam(parts, "move");
                    currentCursor.moveTo(parseCoordinate(parts[1], true), parseCoordinate(parts[2], false));
                    checkBounds(currentCursor);
                    updateDrawing();
                    break;
                case "pos":
                    validateDoubleParam(parts, "pos");
                    currentCursor.moveTo(parseCoordinate(parts[1], true), parseCoordinate(parts[2], false));
                    checkBounds(currentCursor);
                    updateDrawing();
                    break;
                case "hide":
                    currentCursor.hide();
                    updateDrawing();
                    break;
                case "show":
                    currentCursor.show();
                    updateDrawing();
                    break;
                case "cursor":
                    validateSingleParam(parts, "cursor");
                    createCursor(parts[1]);
                    break;
                case "select":
                    validateSingleParam(parts, "select");
                    selectCursor(parts[1]);
                    break;
                case "remove":
                    validateSingleParam(parts, "remove");
                    removeCursor(parts[1]);
                    break;
                case "num":
                    validateVariableParam(parts, "num");
                    numericVariables.put(parts[1], parts.length == 3 ? Double.parseDouble(parts[2]) : 0.0);
                    break;
                case "str":
                    validateVariableParam(parts, "str");
                    stringVariables.put(parts[1], parts.length == 3 ? parts[2] : "");
                    break;
                case "bool":
                    validateVariableParam(parts, "bool");
                    booleanVariables.put(parts[1], parts.length == 3 ? Boolean.parseBoolean(parts[2]) : false);
                    break;
                case "del":
                    validateSingleParam(parts, "del");
                    numericVariables.remove(parts[1]);
                    stringVariables.remove(parts[1]);
                    booleanVariables.remove(parts[1]);
                    break;
                case "if":
                    interpretIfStatement(command);
                    break;
                case "while":
                    interpretWhileLoop(command);
                    break;
                case "for":
                    interpretForLoop(command);
                    break;
                case "lookat":
                    interpretLookAt(parts);
                    break;
                case "mimic":
                    validateSingleParam(parts, "mimic");
                    mimicCursor(parts[1]);
                    break;
                case "mirror":
                    validateMirrorParam(parts);
                    interpretMirror(parts);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown command: " + parts[0]);
            }
        } catch (IllegalArgumentException e) {
            if (ignoreErrors) {
                System.err.println("Error: " + e.getMessage());
            } else {
                throw e;
            }
        }
    }

    private void moveCursors(String command, double value) {
        try {
            executeMovement(currentCursor, command, value);
        } catch (IllegalArgumentException e) {
            if (ignoreErrors) {
                System.err.println("Error: " + e.getMessage());
                return;
            } else {
                throw e;
            }
        }
        for (String id : mimicCursors) {
            Cursor cursor = cursors.get(id);
            try {
                executeMovement(cursor, command, value);
            } catch (IllegalArgumentException e) {
                if (ignoreErrors) {
                    System.err.println("Error: " + e.getMessage());
                    return;
                } else {
                    throw e;
                }
            }
        }
        for (String id : mirrorCursors) {
            Cursor cursor = cursors.get(id);
            try {
                executeMovement(cursor, command, value);
            } catch (IllegalArgumentException e) {
                if (ignoreErrors) {
                    System.err.println("Error: " + e.getMessage());
                    return;
                } else {
                    throw e;
                }
            }
        }
        updateDrawing();
    }

    private void executeMovement(Cursor cursor, String command, double value) {
        switch (command) {
            case "fwd":
                drawLine(cursor.getX(), cursor.getY(), value);
                cursor.forward((int) value);
                checkBounds(cursor);
                break;
            case "bwd":
                drawLine(cursor.getX(), cursor.getY(), -value);
                cursor.backward((int) value);
                checkBounds(cursor);
                break;
            case "turn":
                cursor.turn(value);
                break;
        }
    }

    private void validateSingleParam(String[] parts, String command) {
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid parameters for " + command + " command");
        }
    }

    private void validateDoubleParam(String[] parts, String command) {
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid parameters for " + command + " command");
        }
    }

    private void validateVariableParam(String[] parts, String command) {
        if (parts.length < 2 || parts.length > 3) {
            throw new IllegalArgumentException("Invalid parameters for " + command + " command");
        }
    }

    private void validateColor(String color) {
        if (!color.matches("^#([A-Fa-f0-9]{6})$")) {
            throw new IllegalArgumentException("Invalid color format. Must be in the form #RRGGBB");
        }
    }

    private void validateRGBColor(String[] parts) {
        for (int i = 1; i <= 3; i++) {
            int value = Integer.parseInt(parts[i]);
            if (value < 0 || value > 255) {
                throw new IllegalArgumentException("RGB values must be between 0 and 255");
            }
        }
    }

    private void validateMirrorParam(String[] parts) {
        if (parts.length != 3 && parts.length != 5) {
            throw new IllegalArgumentException("Invalid parameters for mirror command");
        }
    }

    private double parseDistance(String value) {
        if (value.endsWith("%")) {
            double percentage = Double.parseDouble(value.replace("%", ""));
            double maxDimension = Math.max(gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
            return (percentage / 100) * maxDimension;
        }
        return Double.parseDouble(value);
    }

    private int parseCoordinate(String value, boolean isWidth) {
        if (value.endsWith("%")) {
            double percentage = Double.parseDouble(value.replace("%", ""));
            double dimension = isWidth ? gc.getCanvas().getWidth() : gc.getCanvas().getHeight();
            return (int) ((percentage / 100) * dimension);
        }
        return Integer.parseInt(value);
    }

    private double parseOpacity(String value) {
        if (value.endsWith("%")) {
            double percentage = Double.parseDouble(value.replace("%", ""));
            return percentage / 100;
        }
        return Double.parseDouble(value);
    }

    private int parseValue(String value) {
        if (numericVariables.containsKey(value)) {
            return numericVariables.get(value).intValue();
        }
        return Integer.parseInt(value);
    }

    private void createCursor(String id) {
        if (cursors.containsKey(id)) {
            throw new IllegalArgumentException("Cursor ID already exists: " + id);
        }
        cursors.put(id, new Cursor());
    }

    private void selectCursor(String id) {
        if (!cursors.containsKey(id)) {
            throw new IllegalArgumentException("Cursor ID does not exist: " + id);
        }
        currentCursor = cursors.get(id);
    }

    private void removeCursor(String id) {
        if (!cursors.containsKey(id)) {
            throw new IllegalArgumentException("Cursor ID does not exist: " + id);
        }
        cursors.remove(id);
    }

    private void interpretIfStatement(String command) {
        String condition = command.substring(command.indexOf("(") + 1, command.indexOf(")")).trim();
        String body = command.substring(command.indexOf("{") + 1, command.lastIndexOf("}")).trim();

        if (evaluateCondition(condition)) {
            String[] commands = body.split(";");
            executeCommandsWithDelay(Arrays.asList(commands));
        }
    }

    private boolean evaluateCondition(String condition) {
        String[] parts = condition.split(" ");
        double left = parseValue(parts[0]);
        String operator = parts[1];
        double right = parseValue(parts[2]);

        switch (operator) {
            case ">":
                return left > right;
            case "<":
                return left < right;
            case "==":
                return left == right;
            case "!=":
                return left != right;
            case ">=":
                return left >= right;
            case "<=":
                return left <= right;
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }

    private void interpretWhileLoop(String command) {
        String condition = command.substring(command.indexOf("(") + 1, command.indexOf(")")).trim();
        String body = command.substring(command.indexOf("{") + 1, command.lastIndexOf("}")).trim();

        Timeline timeline = new Timeline();
        int delayMillis = 1000 / speed;
        final int[] iteration = {0};

        KeyFrame keyFrame = new KeyFrame(Duration.millis(delayMillis), event -> {
            if (evaluateCondition(condition)) {
                String[] commands = body.split(";");
                for (String cmd : commands) {
                    try {
                        interpret(cmd.trim());
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error: " + e.getMessage());
                        if (stopOnError) {
                            timeline.stop();
                        }
                    }
                }
            } else {
                timeline.stop();
            }
            iteration[0]++;
        });

        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void interpretForLoop(String command) {
        String header = command.substring(command.indexOf("for") + 3, command.indexOf("{")).trim();
        String[] headerParts = header.split(" ");
        String varName = headerParts[1];
        int start = Integer.parseInt(headerParts[3]);
        int end = Integer.parseInt(headerParts[5]);
        int step = headerParts.length > 6 ? Integer.parseInt(headerParts[7]) : 1;
        String body = command.substring(command.indexOf("{") + 1, command.lastIndexOf("}")).trim();

        Timeline timeline = new Timeline();
        int delayMillis = 1000 / speed;

        for (int i = start; i <= end; i += step) {
            final int iteration = i;
            KeyFrame keyFrame = new KeyFrame(Duration.millis(delayMillis * (iteration - start + 1)), event -> {
                numericVariables.put(varName, (double) iteration);
                String[] commands = body.split(";");
                for (String cmd : commands) {
                    try {
                        interpret(cmd.trim());
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error: " + e.getMessage());
                        if (stopOnError) {
                            timeline.stop();
                        }
                    }
                }
            });
            timeline.getKeyFrames().add(keyFrame);
        }

        timeline.play();
    }

    private void interpretLookAt(String[] parts) {
        if (parts.length == 2) {
            // Look at another cursor
            String cursorID = parts[1];
            if (cursors.containsKey(cursorID)) {
                Cursor targetCursor = cursors.get(cursorID);
                double angle = Math.atan2(targetCursor.getY() - currentCursor.getY(), targetCursor.getX() - currentCursor.getX());
                currentCursor.turn(Math.toDegrees(angle) - currentCursor.getDirection());
            } else {
                throw new IllegalArgumentException("Cursor ID does not exist: " + cursorID);
            }
        } else if (parts.length == 3) {
            // Look at a specific point
            int targetX = parseCoordinate(parts[1], true);
            int targetY = parseCoordinate(parts[2], false);
            double angle = Math.atan2(targetY - currentCursor.getY(), targetX - currentCursor.getX());
            currentCursor.turn(Math.toDegrees(angle) - currentCursor.getDirection());
        } else {
            throw new IllegalArgumentException("Invalid lookat command");
        }
    }

    private void mimicCursor(String cursorID) {
        if (!cursors.containsKey(cursorID)) {
            throw new IllegalArgumentException("Cursor ID does not exist: " + cursorID);
        }
        Cursor targetCursor = cursors.get(cursorID);
        Cursor mimicCursor = new Cursor();
        mimicCursor.moveTo(targetCursor.getX(), targetCursor.getY());
        mimicCursor.turn(targetCursor.getDirection());
        mimicCursor.setColor(targetCursor.getColor());
        mimicCursor.setThickness(targetCursor.getThickness());
        mimicCursor.setOpacity(targetCursor.getOpacity());
        String mimicCursorID = "mimic" + (mimicCursors.size() + 1);
        cursors.put(mimicCursorID, mimicCursor);
        mimicCursors.add(mimicCursorID);
    }

    private void interpretMirror(String[] parts) {
        if (parts.length == 5) {
            // Axial symmetry
            int x1 = parseCoordinate(parts[1], true);
            int y1 = parseCoordinate(parts[2], false);
            int x2 = parseCoordinate(parts[3], true);
            int y2 = parseCoordinate(parts[4], false);

            Cursor mirrorCursor = new Cursor();
            mirrorCursor.moveTo(currentCursor.getX(), currentCursor.getY());
            mirrorCursor.turn(currentCursor.getDirection());
            mirrorCursor.setColor(currentCursor.getColor());
            mirrorCursor.setThickness(currentCursor.getThickness());
            mirrorCursor.setOpacity(currentCursor.getOpacity());

            String mirrorCursorID = "mirror" + (mirrorCursors.size() + 1);
            cursors.put(mirrorCursorID, mirrorCursor);
            mirrorCursors.add(mirrorCursorID);

            // Update the position of the mirrored cursor
            int deltaX = x2 - x1;
            int deltaY = y2 - y1;
            mirrorCursor.moveTo(currentCursor.getX() + deltaX, currentCursor.getY() + deltaY);
        } else if (parts.length == 3) {
            // Central symmetry
            int x = parseCoordinate(parts[1], true);
            int y = parseCoordinate(parts[2], false);

            Cursor mirrorCursor = new Cursor();
            mirrorCursor.moveTo(currentCursor.getX(), currentCursor.getY());
            mirrorCursor.turn(currentCursor.getDirection());
            mirrorCursor.setColor(currentCursor.getColor());
            mirrorCursor.setThickness(currentCursor.getThickness());
            mirrorCursor.setOpacity(currentCursor.getOpacity());

            String mirrorCursorID = "mirror" + (mirrorCursors.size() + 1);
            cursors.put(mirrorCursorID, mirrorCursor);
            mirrorCursors.add(mirrorCursorID);

            // Update the position of the mirrored cursor
            mirrorCursor.moveTo(2 * x - currentCursor.getX(), 2 * y - currentCursor.getY());
        } else {
            throw new IllegalArgumentException("Invalid mirror command");
        }
    }

    private void drawLine(int startX, int startY, double distance) {
        int endX = startX + (int) (distance * Math.cos(Math.toRadians(currentCursor.getDirection())));
        int endY = startY + (int) (distance * Math.sin(Math.toRadians(currentCursor.getDirection())));
        lines.add(new LineSegment(startX, startY, endX, endY, currentCursor.getColor(), currentCursor.getThickness(), currentCursor.getOpacity()));
    }

    private void checkBounds(Cursor cursor) {
        if (cursor.getX() < 0 || cursor.getX() > gc.getCanvas().getWidth() || cursor.getY() < 0 || cursor.getY() > gc.getCanvas().getHeight()) {
            throw new IllegalArgumentException("Cursor out of bounds");
        }
    }

    private void updateDrawing() {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        for (LineSegment line : lines) {
            gc.setStroke(Color.web(line.getColor(), line.getOpacity()));
            gc.setLineWidth(line.getThickness());
            gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
        }
        for (Cursor cursor : cursors.values()) {
            if (cursor.isVisible()) {
                drawCursor(cursor);
                gc.beginPath();
                gc.moveTo(cursor.getX(), cursor.getY());
            }
        }
    }

    private void drawCursor(Cursor cursor) {
        if (cursor.isVisible()) {
            double cursorSize = 10;
            double angle = Math.toRadians(cursor.getDirection());
            double x1 = cursor.getX() + cursorSize * Math.cos(angle);
            double y1 = cursor.getY() + cursorSize * Math.sin(angle);
            double x2 = cursor.getX() + cursorSize * Math.cos(angle + 2 * Math.PI / 3);
            double y2 = cursor.getY() + cursorSize * Math.sin(angle + 2 * Math.PI / 3);
            double x3 = cursor.getX() + cursorSize * Math.cos(angle + 4 * Math.PI / 3);
            double y3 = cursor.getY() + cursorSize * Math.sin(angle + 4 * Math.PI / 3);

            gc.setFill(Color.RED);
            gc.fillPolygon(new double[]{x1, x2, x3}, new double[]{y1, y2, y3}, 3);
        }
    }

    public String getCommandHistory() {
        return commandHistory.toString();
    }

    private static class LineSegment {
        private final int startX;
        private final int startY;
        private final int endX;
        private final int endY;
        private final String color;
        private final int thickness;
        private final double opacity;

        public LineSegment(int startX, int startY, int endX, int endY, String color, int thickness, double opacity) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.color = color;
            this.thickness = thickness;
            this.opacity = opacity;
        }

        public int getStartX() {
            return startX;
        }

        public int getStartY() {
            return startY;
        }

        public int getEndX() {
            return endX;
        }

        public int getEndY() {
            return endY;
        }

        public String getColor() {
            return color;
        }

        public int getThickness() {
            return thickness;
        }

        public double getOpacity() {
            return opacity;
        }
    }
}
*/
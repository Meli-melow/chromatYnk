package com.example.appproject;

import javafx.fxml.FXML;

import java.util.Objects;

/**
 * The Cursor Class contains the attributes used to describe a Cursor (drawing pen) and the methods used in the
 * Interpreter Class to execute the user's instructions.
 */

public class Cursor {

    //TODO : documentation
    /**
     * The coordinate on the abscissa axis.
     */
    int positionX;
    /**
     * The coordinate on the ordinate axis.
     */
    int positionY;

    /**
     * The attribute representing the angle, in degrees from the abscissa, pointed by the cursor
     */
    float direction;
    
    /**
     * The id used in the Class MapCursor as a Key.
     */
    int id;

    /**
     * The attribute representing the length of the trail traced by the cursor.
     */
    double thickness;

    /**
     * The intensity of the marking leaved by the cursor.
     */
    double opacity;

    /**
     * The attribute representing the visibility by the user of the cursor. If true, the cursor is appearing in the drawing
     * area interface.
     */
    boolean visible;

    /**
     * The Colorj object representing the color of the cursor and the color of the trail.
     * The color can be determined with multiple formats, web or rgb.
     */
    Colorj color;


    /**
     * This constructor method creates a cursor with preset attributes
     * @param id The id of the cursor used as a Key in MapCursor Objects.
     */

    public Cursor(int id) {
        this.id = id;

        this.positionX = 0;
        this.positionY = 0;
        this.direction = 0;
        this.thickness = 3;
        this.opacity = 1;
        this.visible = true;
        this.color = new Colorj(0,0,0);
    }

    public Cursor(int positionX, int positionY, float direction, int id, double thickness, boolean visible, Colorj color, double opacity) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.direction = direction;
        this.id = id;
        this.thickness = thickness;
        this.opacity = opacity;
        this.visible = visible;
        this.color = color;
    }

    /**
     * Change the attributes of this cursor to copy those of modelCursor.
     * @param modelCursor
     */
    public void duplicate(Cursor modelCursor) throws OutOfPositionException{
        setPositionX(modelCursor.getPositionX());
        setPositionY(modelCursor.getPositionY());
        setDirection(modelCursor.getDirection());
        setThickness(modelCursor.getThickness());
        setOpacity(modelCursor.getOpacity());
        setVisible(modelCursor.isVisible());
        setColor(modelCursor.getColorj().getRgb()[0],modelCursor.getColorj().getRgb()[1],modelCursor.getColorj().getRgb()[2]);
    }

    /**
     * The position method is used to implement the POS instruction and MOV instruction.
     * When POS is called the position method is used, but when MOV is called a lign is
     * drawn between the last position and the new one.
     */
    public void position(int positionX,int positionY) throws OutOfPositionException {
        setPositionX(positionX);
        setPositionY(positionY);
    }

    /**
     * The POS and MOV methods can be used with percentages of the dimensions of the canvas.
     * per_x and per_y are Percentage objects which ensure that their value is between 0 and 1.
     * @param per_x Position X on the abscissa in percentage of the width of the canvas.
     * @param per_y Position Y on the ordinate in percentage of the height of the canvas.
     * @param dimensionX Width of the canvas.
     * @param dimensionY Height of the canvas.
     */
    public void position(Percentage per_x, Percentage per_y, int dimensionX, int dimensionY) throws OutOfPositionException {
        setPositionX((int) Math.floor(per_x.getValue()*dimensionX));
        setPositionY((int) Math.floor(per_y.getValue()*dimensionY));
    }

    /**
     * The forward method move the cursor by "distance" pixels, following the direction the cursor is headed on, which is
     * the "direction" angle from the abscissa.
     * @param distance
     */
    public void forward(int distance) throws OutOfPositionException {
        this.positionX += distance*Math.cos(Math.toRadians(direction));
        this.positionY += distance*Math.sin(Math.toRadians(direction));
    }

    /**
     *
     * @param value As the "distance" attribute but based of a percentage of the dimension of the canvas.
     * @param dimension The dimension linked to the canvas from which we want the percentage value to be
     *                  related to.
     */
    public void forward(Percentage value, int dimension) throws OutOfPositionException {
        int distance = (int) Math.floor(value.getValue() * dimension);

        this.positionX += distance * Math.cos(Math.toRadians(direction));
        this.positionY += distance * Math.sin(Math.toRadians(direction));
        throw new OutOfPositionException("Warning : cursor is out of bounds");

    }

    public void backward(int value) throws OutOfPositionException {
        forward(-value);
    }

    public void backward(Percentage value, int dimension) throws OutOfPositionException {
        int distance = (int) Math.floor(value.getValue() * dimension);
        positionX -= distance * Math.cos(Math.toRadians(direction));
        positionY -= distance * Math.sin(Math.toRadians(direction));
    }

    /**
     * Turn the cursor by "angle" degrees, rotating clock-wise.
     * The direction is supposed to be in degrees, between 0 and 359, the setDirection method assures that it is the case.
     * The angle is converted to a float value so the remainder operator % can handle it in setDirection method.
     */
    public void turn(double angle){
        float f_angle = (float) angle;
        setDirection(getDirection() + f_angle);
    }

    /**
     *  The lookat method turns the cursor so it points to the wanted position.
     * @param lookAt_x
     * @param lookAt_y
     */
    public void lookAt(int lookAt_x, int lookAt_y){
        double u = Math.abs(lookAt_x - getPositionX());
        double v = Math.abs(lookAt_y - getPositionY());

        if (lookAt_y==getPositionY()) {
            if (lookAt_x > getPositionX()){setDirection(0);}
            else if (lookAt_x < getPositionX()){setDirection(180);}
        }
        else {
            /* Angle on selected cursor's side on the right-angle triangle composed of the selected cursor and
               the targeted cursor.
            */
            float angle = (float) Math.toDegrees(Math.atan(u / v));
            //System.out.println("angle rotation en radian : "+Math.atan(v/u)+"\nAngle en degrees : "+Math.toDegrees(Math.atan(v / u)));

            //depending on where the cursor to look at is, we have to adapt the angle

            //the half-plan on the right of the selected cursor
            if (getPositionX()<=lookAt_x){
                //Top right corner
                if (getPositionY()>lookAt_y){
                    setDirection(angle + 270);
                }
                //Bottom Right corner
                else if (getPositionY()<lookAt_y) {
                    setDirection(90-angle);
                }
            }
            //the half plan on the left of the selected cursor
            else if (getPositionX()>lookAt_x) {
                //Top left corner
                if (getPositionY()>lookAt_y){
                    setDirection((90-angle) + 180);
                }
                //Bottom left corner
                else if (getPositionY()<lookAt_y) {
                    setDirection(angle+90);
                }
            }

        }
    }

    /**
     * Turns the cursor to point at the "modelCursor".
     * @param modelCursor The cursor to point at.
     */
    public void lookAt(Cursor modelCursor) throws IllegalArgumentException{
        if (!modelCursor.equals(this)) {
            lookAt(modelCursor.getPositionX(), modelCursor.getPositionY());
        }
        else {
            throw new IllegalArgumentException("Cursor can not look at itself");
        }
    }

    public void lookAt(Percentage per_x, Percentage per_y, double width, double height){
        int lookAt_x = (int) Math.floor(per_x.getValue()*width);
        int lookAt_y = (int) Math.floor(per_y.getValue()*height);
        lookAt(lookAt_x,lookAt_y);
    }

    /**
     * Creates a new cursor symmetrical to the selected cursor by the symmetrical axis contaning the points (x1,y1) and
     * (x2,y2).
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param cursors
     * @return A new Cursor symmetrical to the selected cursor (this)
     */
    public Cursor createMirrorAxis(int x1, int y1, int x2, int y2, MapCursor cursors) throws OutOfPositionException,NumberFormatException{

        int cursorSymId = cursors.smallestAvailableId();
        Cursor cursorSym = new Cursor (cursorSymId);
        cursorSym.duplicate(this);
        cursorSym.setDirection(180 + this.getDirection());
        int uX = x2 - x1;
        int uY = y2 - y1;
        //double theta = Math.atan2(uY, uX);  // Calculate angle of the line with the x-axis

        int x = this.getPositionX();
        int y = this.getPositionY();
        //Matrice paramétrée
        int [][] matPar = {{uX, uY}, {-uY, uX}};

        //Vecteur paramétré
        int[] par = {-x*uX -y*uY, -y1*uX + x1*uY};

        //Déterminants pour la formule de Cramer
        int detMat = (int) (Math.pow(uX,2) + Math.pow(uY,2));
        System.out.println("\n Déterminant matrice :"+detMat+"\n");

        int det1 = par[0]*matPar[1][1] - par[1]*matPar[0][1];
        int det2 = matPar[0][0]*par[1] - par[0]*matPar[1][0];

        System.out.println("\n Cramer déterminant 1 :"+det1+"\n");
        System.out.println("\n Cramer déterminant 2 :"+det2+"\n");

        //Coordonées du projeté
        int xP = (int) (det1/detMat);
        int yP = (int) (det2/detMat);

        int[] projete = {xP, yP};

        int newX = 2 * projete[0] - x;
        int newY = 2 * projete[1] - y;

        this.positionX = newX;
        this.positionY = newY;

        return cursorSym;
    }

    public double getOpacity() {
        return opacity;
    }

    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) throws OutOfPositionException {
        this.positionX = positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) throws OutOfPositionException {
        this.positionY = positionY;
    }

    public float getDirection() {
        return this.direction;
    }

    /**
     * The method checks if the angle from the abscissa is in degrees from 0 to 359, and sets the new value.
     */
    public void setDirection(float angle) throws IllegalArgumentException{
        if (angle >= 0) {
            this.direction = (angle % 360);
            System.out.println("Angle : " + direction);
        }
        else {throw new IllegalArgumentException("Negative angle.");}
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getThickness() {
        return thickness;
    }

    public void setThickness(double thickness) {
        this.thickness = thickness;
    }

    /**
     * isVisible() is the getter for the attribute Cursor.visible.
     * */
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Colorj getColorj() {
        return this.color;
    }

    public void setColor(String webColor) throws NumberFormatException{
        this.color.setRgbFromWeb(webColor);
    }

    public void setColor(int red, int green, int blue) throws IllegalArgumentException{
        this.color.setRgb(red,green,blue);
    }

    public void setColor(double red, double green, double blue) throws IllegalArgumentException{
        this.color.setRgbFromRgbDouble(red,green,blue);
    }

    @Override
    public String toString() {
        return String.format("Cursor %d  X:%d Y:%d hidden=%b dir:%.2f thick:%.2f Press:%.2f,"+this.color.toString(),
                id,positionX, positionY,visible, direction,  thickness, opacity);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cursor cursor = (Cursor) obj;
        return id == cursor.id; // It is supposed that the id is unique for each cursor.
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

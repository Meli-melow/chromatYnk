package com.example.appproject;

import java.util.Objects;

/**
 * The Cursor Class contains the attributes used to describe a Cursor (drawing pen) and the methods used in the
 * Interpreter Class to execute the user's instructions.
 */

public class Cursor {

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
     *
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

    /**
     * This constructor method creates a cursor with the attributes entered as parameters.
     *
     * @param positionX
     * @param positionY
     * @param direction
     * @param id
     * @param thickness
     * @param visible
     * @param color
     * @param opacity
     */

    public Cursor(int positionX, int positionY, float direction, int id, double thickness, boolean visible, Colorj color, double opacity) {
        position(positionX,positionY);
        setDirection(direction);
        this.id = id;
        setThickness(thickness);
        setOpacity(opacity);
        setVisible(visible);
        this.color = color;
    }

    /**
     * Changes the attributes of this cursor to copy those of modelCursor.
     *
     * @param modelCursor
     */
    public void duplicate(Cursor modelCursor){
        position(modelCursor.getPositionX(),modelCursor.getPositionY());
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
     *
     * @param positionX
     * @param positionY
     */
    public void position(int positionX,int positionY){
        setPositionX(positionX);
        setPositionY(positionY);
    }

    /**
     * The POS and MOV methods can be used with percentages of the dimensions of the canvas.
     * per_x and per_y are Percentage objects which ensure that their value is between 0 and 1.
     *
     * @param per_x Position X on the abscissa in percentage of the width of the canvas.
     * @param per_y Position Y on the ordinate in percentage of the height of the canvas.
     * @param dimensionX Width of the canvas.
     * @param dimensionY Height of the canvas.
     */
    public void position(Percentage per_x, Percentage per_y, int dimensionX, int dimensionY) {
        setPositionX((int) Math.floor(per_x.getValue()*dimensionX));
        setPositionY((int) Math.floor(per_y.getValue()*dimensionY));
    }

    /**
     * The forward method move the cursor by "distance" pixels, following the direction the cursor is headed on, which is
     * the "direction" angle from the abscissa.
     *
     * @param distance
     */
    public void forward(int distance){
        this.positionX += distance*Math.cos(Math.toRadians(direction));
        this.positionY += distance*Math.sin(Math.toRadians(direction));
    }

    /**
     * Unused : A method to move the cursor forward, the distance travelled is determined by the percentage of the dimension.
     *
     * @param value As the "distance" attribute but based of a percentage of the dimension of the canvas.
     * @param dimension The dimension linked to the canvas from which we want the percentage value to be
     * related to.
     */
    public void forward(Percentage value, int dimension){
        int distance = (int) Math.floor(value.getValue() * dimension);

        this.positionX += distance * Math.cos(Math.toRadians(direction));
        this.positionY += distance * Math.sin(Math.toRadians(direction));

    }

    /**
     * As Forward but the distance entered is the opposite.
     *
     * @param value
     */
    public void backward(int value){
        forward(-value);
    }

    public void backward(Percentage value, int dimension){
        int distance = (int) Math.floor(value.getValue() * dimension);
        positionX -= distance * Math.cos(Math.toRadians(direction));
        positionY -= distance * Math.sin(Math.toRadians(direction));
    }

    /**
     * Turn the cursor by "angle" degrees, rotating clock-wise.
     * The direction is supposed to be in degrees, between 0 and 359, the setDirection method assures that it is the case.
     * The angle is converted to a float value so the remainder operator % can handle it in setDirection method.
     *
     * @param angle
     */
    public void turn(double angle){
        float f_angle = (float) angle;
        setDirection(getDirection() + f_angle);
    }

    /**
     *  The lookat method turns the cursor so it points to the wanted position.
     *
     * @param lookAt_x
     * @param lookAt_y
     */
    public void lookAt(int lookAt_x, int lookAt_y){
        double u = Math.abs(lookAt_x - getPositionX());
        double v = Math.abs(lookAt_y - getPositionY());

        //We assure that v is not null so we can divide u/v.
        if (lookAt_y==getPositionY()) {
            if (lookAt_x > getPositionX()){setDirection(0);}
            else if (lookAt_x < getPositionX()){setDirection(180);}
        }
        else {
            /*
                Angle on selected cursor's side on the right-angle triangle composed of the selected cursor and
                the targeted cursor.
            */
            float angle = (float) Math.toDegrees(Math.atan(u / v));

            /*
                Depending on where the cursor to look at is, we have to adapt the new angle.
            */

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
     *
     * @param modelCursor The cursor to point at.
     * @throws IllegalArgumentException
     */
    public void lookAt(Cursor modelCursor) throws IllegalArgumentException{
        if (!modelCursor.equals(this)) {
            lookAt(modelCursor.getPositionX(), modelCursor.getPositionY());
        }
        else {
            throw new IllegalArgumentException("Cursor can not look at itself");
        }
    }

    /**
     * Look at the coordinates designated by the percentages of the drawing area's dimensions.
     *
     * @param per_x
     * @param per_y
     * @param width
     * @param height
     */
    public void lookAt(Percentage per_x, Percentage per_y, double width, double height){
        int lookAt_x = (int) Math.floor(per_x.getValue()*width);
        int lookAt_y = (int) Math.floor(per_y.getValue()*height);
        lookAt(lookAt_x,lookAt_y);
    }

    /**
     * Creates a new cursor symmetrical to the selected cursor by the symmetrical axis containing the points (x1,y1) and
     * (x2,y2).
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param cursors
     * @return A new Cursor symmetrical to the selected cursor (this)
     * @throws OutOfPositionException
     * @throws NumberFormatException
     */
    public Cursor createMirrorAxis(int x1, int y1, int x2, int y2, MapCursor cursors) throws OutOfPositionException,NumberFormatException {

        int cursorSymId = cursors.smallestAvailableId();
        Cursor cursorSym = new Cursor (cursorSymId);
        cursorSym.duplicate(this);

        cursorSym.setDirection(180 + this.getDirection());

        double deltaX = x2 - x1;
        double deltaY = y2 - y1;
        double theta = Math.atan2(deltaY, deltaX);  // Calculate angle of the line with the x-axis

        double cos2Theta = Math.cos(2 * theta);
        double sin2Theta = Math.sin(2 * theta);

        int newX = (int) Math.round(this.getPositionX() * cos2Theta + this.getPositionY() * sin2Theta);
        int newY = (int) Math.round(this.getPositionX() * sin2Theta - this.getPositionY() * cos2Theta);

        this.positionX = newX;
        this.positionY = newY;

        return cursorSym;
    }

    /**
     * The getter of the opacity attribute.
     *
     * @return opacity of the cursor
     * */
    public double getOpacity() {
        return opacity;
    }

    /**
     * The setter of the opacity attribute
     *
     * @param opacity
     */
    public void setOpacity(double opacity) throws IllegalArgumentException{
        if (opacity>=0 && opacity<=1.0){
            this.opacity = opacity;
        }
        else {throw new IllegalArgumentException("Opacity is defined between 0 and 1.");}
    }

    /**
     * The getter of the abscissa attribute.
     *
     * @return abcissa of the cursor
     * */
    public int getPositionX() {
        return positionX;
    }

    /**
     * The setter of the abscissa attribute
     *
     * @param positionX
     */
    public void setPositionX(int positionX){
        this.positionX = positionX;
    }

    /**
     * The getter of the ordinate attribute.
     *
     * @return ordinate of the cursor
     */
    public int getPositionY() {
        return positionY;
    }

    /**
     * The setter of the ordinate attribute
     *
     * @param positionY
     */
    public void setPositionY(int positionY){
        this.positionY = positionY;
    }


    /**
     * The getter of the direction attribute.
     *
     * @return the current direction of the cursor.
     */
    public float getDirection() {
        return this.direction;
    }

    /**
     * The method checks if the angle from the abscissa is in degrees from 0 to 359, and sets the new value.
     *
     * @param angle
     */
    public void setDirection(float angle) throws IllegalArgumentException{
        if (angle >= 0) {
            this.direction = (angle % 360);
        }
        else {throw new IllegalArgumentException("Negative angle.");}
    }

    /**
     * The getter of the id attribute.
     *
     * @return id of the cursor
     */
    public int getId() {
        return id;
    }

    /**
     * The setter of the id attribute
     *
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * The getter of the thickness attribute.
     *
     * @return thickness of the cursor
     * */
    public double getThickness() {
        return thickness;
    }

    /**
     * The setter of the thickness attribute.
     *
     * @param thickness
     */
    public void setThickness(double thickness) throws IllegalArgumentException{
        if (thickness > 0){
            this.thickness = thickness;
        }
        else {throw new IllegalArgumentException("Thickness can nor be defined negatively.");}

    }

    /**
     * The getter of the visible attribute.
     *
     * @return indicates if the choosen cursor is visible.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * The setter for the attribute visible.
     *
     * @param visible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Colorj getColorj() {
        return this.color;
    }

    /**
     * Set the color attribute using a web format (#RRGGBB).
     *
     * @param webColor
     * @throws NumberFormatException Comes from Colorj.setRgbFromWeb().
     */
    public void setColor(String webColor) throws NumberFormatException{
        this.color.setRgbFromWeb(webColor);
    }

    /**
     * Set the color attribute using the RGB format with integers between 0 and 255.
     *
     * @param red
     * @param green
     * @param blue
     * @throws IllegalArgumentException Comes from Colorj.setRgb().
     */
    public void setColor(int red, int green, int blue) throws IllegalArgumentException{
        this.color.setRgb(red,green,blue);
    }

    /**
     * Set the color attribute using the RGB format with double values between 0 and 1.
     *
     * @param red
     * @param green
     * @param blue
     * @throws IllegalArgumentException Comes from Colorj.setRgbFromRgbDouble().
     */
    public void setColor(double red, double green, double blue) throws IllegalArgumentException{
        this.color.setRgbFromRgbDouble(red,green,blue);
    }

    /**
     * @return the state of the cursor inside a String
     */
    @Override
    public String toString() {
        return String.format("Cursor %d  X:%d Y:%d hidden=%b dir:%.2f thick:%.2f Press:%.2f,"+this.color.toString(),
                id,positionX, positionY,visible, direction,  thickness, opacity);
    }

    /**
     * @return indicates if the object entered as input is equal to the selected cursor
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        Cursor cursor = (Cursor) obj;
        return id == cursor.id; // It is supposed that the id is unique for each cursor.
    }

    /**
     * @return the hashCode of the entered id*/
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

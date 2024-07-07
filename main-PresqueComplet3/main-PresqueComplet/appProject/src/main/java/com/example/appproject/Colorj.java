package com.example.appproject;
/*import Exception.OutOfRangeRGBException;*/

/**
 * The <b><i>Colorj</i></b> is used to manage the color of cursors.
 * It can be associated with three different values, RGB as integers between 0 and 255, RGB as doubles between 0 and 1 or
 * a hexadecimal web format.
 */
public class Colorj {

    /**
     * Main attribute of <i>Colorj</i>, RGB description of the color in integers from 0 to 255.
     */
    int rgb[] = new int[3];

    /**
     * Hexadecimal Web format to describe the color : "#RRGGBB".
     */
    String web;

    /**
     * RGB description of the color in doubles from 0 to 1.
     */
    double rgb_double[] = new double[3];

    /**
     * This constructor method set the <i>web</i> attribute and the <i>rgb</i> attribute using <i>setRgbFromWeb()</i>
     * method.
     *
     * @param web Hexadecimal Web format : "#RRGGBB".
     */
    public Colorj(String web) {
        this.web = web;
        setRgbFromWeb(web);
    }

    /**
     * This constructor method sets the <i>rgb_double</i> attribute and the <i>rgb</i> attribute using
     * <i>setRgbFromRgbDouble()</i> method.
     *
     * @param red,green,blue RGB description of the color in doubles from 0 to 1.
     */
    public Colorj(double red, double green, double blue){
        this.rgb_double[0] = red;
        this.rgb_double[1] = green;
        this.rgb_double[2] = blue;
        setRgbFromRgbDouble(red,green,blue);
    }

    /**
     * This constructor method set the <i>rgb</i> attribute.
     *
     * @param red,green,blue RGB description of the color in integers from 0 to 255.
     * @throws IllegalAccessException
     */
    public Colorj(int red, int green, int blue) throws IllegalArgumentException {
        if (red >= 0 && red <= 255 && green >= 0 && green <= 255 && blue >= 0 && blue <= 255) {
            this.rgb[0] = red;
            this.rgb[1] = green;
            this.rgb[2] = blue;
        }
        else throw new IllegalArgumentException("RGB color value has to be between 0 and 255.");
    }


    /**
     * Set the rgb attribute based on the web description of the color in hexa.
     *
     * @param webColor Hexadecimal Web format : "#RRGGBB".
     * @throws  NumberFormatException
     */
    public void setRgbFromWeb(String webColor) throws NumberFormatException{
        //Upgrade : first char is "#"
        if (webColor.length()==7) {
            int red = Integer.parseInt(webColor.substring(1, 3), 16);
            int green = Integer.parseInt(webColor.substring(3, 5), 16);
            int blue = Integer.parseInt(webColor.substring(5, 7), 16);

            setRgb(red, green, blue);
        }
        else {throw new IllegalArgumentException("Illegal syntax for web format of color");}
    }

    /**
     * Sets the rgb value in integers from 0 to 255 of the color, from its rgb description in double value from 0 to 1.
     *
     * @thrwos IllegalArgumentException
     */
    public void setRgbFromRgbDouble(double red_d, double green_d, double blue_d) throws IllegalArgumentException {
        //Comparing 2 by 2 cause anything containing a comparison operator turns boolean -> 0.0 <= red <= 1.0 impossible
        //Checking color parameters are within range
        if (red_d <= 1.0 && red_d >= 0 && green_d <= 1.0 && green_d >= 0 && blue_d <= 1.0 && blue_d >= 0) {
            int red = (int) Math.round(red_d * 255);
            int green = (int) Math.round(green_d * 255);
            int blue = (int) Math.round(blue_d * 255);
            setRgb(red, green, blue);
        }
        else {throw new IllegalArgumentException("RGB color value has to between 0 and 1.");}
    }

    /**
     * Modifies the color in the RGB int format.
     *
     * @param red,green,blue the new RGB values
     * @throws IllegalArgumentException
     */
    public void setRgb(int red, int green, int blue) throws IllegalArgumentException {
        if (red >= 0 && red <= 255 && green >= 0 && green <= 255 && blue >= 0 && blue <= 255) {
            this.rgb[0] = red;
            this.rgb[1] = green;
            this.rgb[2] = blue;
        }
        else throw new IllegalArgumentException("RGB color value has to be between 0 and 255.");
    }

    public int[] getRgb() {
        return this.rgb;
    }

    /**
     * @return web the web format of the color in hexa as a String, converted from the rgb value int[].
     */
    public String rgbToWeb()/* throws OutOfRangeRGBException*/ {
        int red = this.rgb[0];
        int green = this.rgb[1];
        int blue = this.rgb[2];
        //Convert rgb format to web in hexadecimal : String containing hexa format
        if (red >= 0 && red <= 255 && green >= 0 && green <= 255 && blue >= 0 && blue <= 255) {
            String redHex = Integer.toHexString(red);
            String blueHex = Integer.toHexString(green);
            String greenHex = Integer.toHexString(blue);

            this.web = "#" + redHex + blueHex + greenHex;

            //Method can be used as a getter
            return this.web;
        }
        /*else throw new OutOfRangeRGBException("RGB format : color not included between 0 and 255");*/
        return this.web;
    }

    /**
     * @return the RGB description inside a String
     */
    @Override
    public String toString() {
        return ("R:"+this.getRgb()[0]+", G:"+this.getRgb()[1]+",B:"+this.getRgb()[2]);
    }
}

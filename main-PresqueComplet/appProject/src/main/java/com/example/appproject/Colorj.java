package com.example.appproject;
/*import Exception.OutOfRangeRGBException;*/
public class Colorj {

    //TODO : documentation

    int rgb[] = new int[3];
    String web;
    double rgb_double[] = new double[3];

    public Colorj(String web) {
        this.web = web;
        setRgbFromWeb(web);
    }

    public Colorj(double red, double green, double blue) {
        this.rgb_double[0] = red;
        this.rgb_double[1] = green;
        this.rgb_double[2] = blue;
        setRgbFromRgbDouble(red,green,blue);
    }

    public Colorj(int red, int green, int blue)/* throws OutOfRangeRGBException*/ {
        if (red >= 0 && red <= 255 && green >= 0 && green <= 255 && blue >= 0 && blue <= 255) {
            this.rgb[0] = red;
            this.rgb[1] = green;
            this.rgb[2] = blue;
        }
        /*else throw new OutOfRangeRGBException("Couleurs non compris entre 0 et 255");*/
    }


    /**
     * Set the rgb attribute based on the web description of the color in hexa.
     * */
    public void setRgbFromWeb(String webColor){
        System.out.println("Color hexa :"+webColor);
        int red = Integer.parseInt(webColor.substring(1,3),16);
        int green = Integer.parseInt(webColor.substring(3,5),16);
        int blue = Integer.parseInt(webColor.substring(5,7),16);
        System.out.println("RGB : "+red +" "+ green +" "+ blue);
        setRgb(red,green,blue);
    }

    /**
     *Set the rgb value in integers from 0 to 255 of the color, from its rgb description in double value from 0 to 1.
     */
    public void setRgbFromRgbDouble(double red_d, double green_d, double blue_d){
        int red = (int) Math.round(red_d*255);
        int green = (int) Math.round(green_d*255);
        int blue = (int) Math.round(blue_d*255);
        setRgb(red,green,blue);
    }

    public void setRgb(int red, int green, int blue) {
        this.rgb[0] = red;
        this.rgb[1] = green;
        this.rgb[2] = blue;
    }

    public int[] getRgb() {
        return this.rgb;
    }

    /**
     * Returns the web format of the color in hexa as a String, converted from the rgb value int[].
     */
    public String rgbToWeb()/* throws OutOfRangeRGBException*/ {
        int red = this.rgb[0];
        int green = this.rgb[1];
        int blue = this.rgb[2];
        //Convert rgb format to web in hexadecimal
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

    @Override
    public String toString() {
        return ("R:"+this.getRgb()[0]+", G:"+this.getRgb()[1]+",B:"+this.getRgb()[2]);
    }
}

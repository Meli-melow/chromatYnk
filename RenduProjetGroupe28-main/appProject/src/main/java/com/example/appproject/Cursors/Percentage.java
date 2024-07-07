package com.example.appproject;
/*import Exception.IncorrectPercentageException;*/

/**
 * The <b><i>Percentage</i></b> Class manages the percentages.
 * It allows the creation of <i>Percentage</i> objects, associated with a <i>value</i>, a double between 0 and 1.
 */

public class Percentage
{
    double value;

    /**
     * This constructor method creates a new <i>Percentage</i> object with a value between 0 and 1 or throws an exception
     * if <i>value</i> is not in that interval.
     *
     * @param value
     * @throws IllegalArgumentException
     */
    public Percentage(double value) throws IllegalArgumentException{
        if(value<=1.0&&value>=0.0)
        {
            this.value=value;
        }
        else {throw new IllegalArgumentException("Percentage value has to be between 0 and 100");}
    }

    /**
     * This constructor method creates a Percentage object from a String value, example : 50% = 0.5.
     *
     * @param percentageToken String parameter, format : "50%".
     */
    public Percentage(String percentageToken){
        if (percentageToken.endsWith("%")){
            int n = percentageToken.length();

            //Extract the percentage value from the String, the "%" character is abandonned.
            double tokenValue = Double.parseDouble(String.copyValueOf(percentageToken.toCharArray(),0,n-1));

            double doubleTokenValue = tokenValue/100;
            setValue(doubleTokenValue);

        }
    }
    /**
     * The setter.
     *
     * @param value of the <i>Percentage</i>.
     */
    public void setValue(double value) throws IllegalArgumentException{
        if(value<=1.0&&value>=0.0)//percentages are real numbers between 0 and 1
        {
            this.value=value;
        }
        else {throw new IllegalArgumentException("Percentage value has to be between 0 and 100");}
    }

    /**
     * The getter.
     *
     * @return value of the <i>Percentage</i>.
     */
    public double getValue() {
        return value;
    }
}

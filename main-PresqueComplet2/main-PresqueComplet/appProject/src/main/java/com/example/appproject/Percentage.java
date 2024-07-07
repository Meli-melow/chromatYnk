package com.example.appproject;
/*import Exception.IncorrectPercentageException;*/
public class Percentage
{
    //TODO : documentation

    double value;

    public Percentage(double value) throws IllegalArgumentException{
        if(value<=1.0&&value>=0.0)//les pourcentages sont des rééls entre 0 et 1
        {
            this.value=value;
        }
        else {throw new IllegalArgumentException("Percentage value has to be between 0 and 100");}
    }

    /**
     * This constructor method creates a Percentage object from a String value, example : 50% = 0.5.
     * @param percentageToken String parameter, format : "50%".
     */
    public Percentage(String percentageToken){
        if (percentageToken.endsWith("%")){
            int n = percentageToken.length();


            double tokenValue = Double.parseDouble(String.copyValueOf(percentageToken.toCharArray(),0,n-1));

            //crée l'objet Percentage correspondant
            double doubleTokenValue = tokenValue/100;
            System.out.println(doubleTokenValue+"double");
            setValue(doubleTokenValue);

        }
    }

    public void setValue(double value) throws IllegalArgumentException{
        if(value<=1.0&&value>=0.0)//percentages are real numbers between 0 and 1
        {
            this.value=value;
            //System.out.println("value dans percentage :"+value);
        }
        else {throw new IllegalArgumentException("Percentage value has to be between 0 and 100");}
    }

    public double getValue() {
        return value;
    }
}

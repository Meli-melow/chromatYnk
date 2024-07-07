package com.example.appproject;
/*import Exception.IncorrectPercentageException;*/
public class Percentage
{
    //TODO : documentation

    double value;

    public Percentage(double value) /*throws IncorrectPercentageException */{
        if(value<=1.0&&value>=0.0)//les pourcentages sont des rééls entre 0 et 1
        {
            this.value=value;
        }
        /*else throw new IncorrectPercentageException("Pourcentage non compris entre 0 et 1");*/
    }

    /**
     * This constructor method creates a Percentage object from a String value, example : 50% = 0.5.
     * @param percentageToken String parameter, format : "50%".
     */
    public Percentage(String percentageToken){
        if (percentageToken.endsWith("%")){
            int n = percentageToken.length();
            System.out.println("percentagetokenValue : "+percentageToken);
            //enleve le symnbole '%'
            double tokenValue = Double.parseDouble(String.copyValueOf(percentageToken.toCharArray(),0,n-1));
            System.out.println("tokenValue : "+tokenValue);

            //crée l'objet Percentage correspondant
            double doubleTokenValue = tokenValue/100;
            System.out.println(doubleTokenValue+"double");
            setValue(doubleTokenValue);

        }
    }

    public void setValue(double value) /*throws IncorrectPercentageException*/{
        if(value<=1.0&&value>=0.0)//les pourcentages sont des rééls entre 0 et 1
        {
            this.value=value;
            System.out.println("value dans percentage :"+value);
        }
        /*else throw new IncorrectPercentageException("Pourcentage non compris entre 0 et 1");*/
    }

    public double getValue() {
        return value;
    }
}

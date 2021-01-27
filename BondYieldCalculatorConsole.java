import java.io.*;

public class BondYieldCalculatorConsole {
  public static void main(String[] args) {
    calculatorFace();
  }

  private static void calculatorFace() {
    try {
      BondYieldCalculator calc = new BondYieldCalculator();
      InputStreamReader isr = new InputStreamReader(System.in);
      BufferedReader reader = new BufferedReader(isr);

      System.out.println("Welcome to the bond yield calculator developed by Kah Soon Yap for Chimera.\n");

      boolean done = false;
      /* Loop that deals with user choosing yield or price */
      while (!done) {
        System.out.println("Please select which you would like to calculate:");
        System.out.println("p to calculate bond price");
        System.out.println("y to calculate bond yield");
        System.out.println("q to quit the calculator");

        /* Read in user input from stdin */
        String choice;
        try {
          choice = reader.readLine().toLowerCase();
          System.out.println("");
        } catch(IOException e) {
          System.out.println(e);
          System.out.println("Error reading in choice");
          continue;
        }

        /* React to input */
        if (choice.equals("q")) {
          done = true;
          continue;
        } else if (choice.equals("p")){
          System.out.println("Please enter each parameter separated by a single space to calculate the bond price:");
          System.out.println("Coupon: coupon rate in decimal form");
          System.out.println("Years:  years to maturity (must be a whole number)");
          System.out.println("Face:   face value of the bond");
          System.out.println("Rate:   discount rate of the bond in decimal form");
          System.out.println("e.g.    0.10  5  1000.0  0.15");
        } else if (choice.equals("y")){
          System.out.println("Please enter each parameter separated by a single space to calculate the yield:");
          System.out.println("Coupon: coupon rate in decimal form");
          System.out.println("Years:  years to maturity (must be a whole number)");
          System.out.println("Face:   face value of the bond");
          System.out.println("Price:  price of the bond");
          System.out.println("e.g.    0.10  5  1000.0  832.3922451");
        } else {
          continue;
        }
        System.out.println("Enter 'b' to go back to selection or 'q' to quit\n");

        /* Inner loop that deals with user params */
        String line;
        String[] params;
        boolean paramDone = false;

        while (!paramDone) {
          try {
            line = reader.readLine();
            System.out.println("");
          } catch(IOException e) {
            System.out.println("Error reading in parameters");
            continue;
          }

          /* Go back or exit */
          if (line.toLowerCase().equals("q")) {
            done = true;
            break;
          } else if (line.toLowerCase().equals("b")) {
            break;
          }

          /* Check if formatted correctly */
          params = line.split(" ");
          if (params.length != 4) {
            System.out.println("Mistmatched number of parameters");
            System.out.println("Please reenter parameters");
          } else {
            boolean doubleSpace = false;
            for(String param : params) {
              if (param == "") {
                System.out.println("Please separate each parameter with a single space only");
                doubleSpace = true;
                continue;
              }
            }

            /* Parse for params then calculate */
            if (!doubleSpace) {
              try {
                double coupon = Double.parseDouble(params[0]);
                int years = Integer.parseInt(params[1]);
                double face = Double.parseDouble(params[2]);
                if (choice.equals("p")) {
                  double rate = Double.parseDouble(params[3]);
                  System.out.println(calc.prettyCalcPrice(coupon, years, face, rate));
                } else {
                  double price = Double.parseDouble(params[3]);
                  System.out.println(calc.prettyCalcYield(coupon, years, face, price));
                }
                paramDone = true;
              } catch (NumberFormatException e) {
                System.out.println("Non-number parameter given.");
                System.out.println("Please reenter parameters");
              }
            }
          }
          System.out.println("");
        }
      }

      /* Close reader and stdin */
      try {
        isr.close();
      } catch (Exception e) {
        System.out.println(e);
        System.out.println("Failed to input stream reader");
      }
      try {
        reader.close();
      } catch (Exception e) {
        System.out.println(e);
        System.out.println("Failed to close buffer reader");
      }

    } catch (Exception e) {
      System.out.println(e);
      System.out.println("Error opening input stream reader or buffer reader");
    }
  }
}

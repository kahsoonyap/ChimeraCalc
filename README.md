# Chimera Bond Yield Calculator
## Goal
Create a calculator capable of calculating of calculating the price of bonds or yield of bonds.
The two methods are:
`double CalcPrice(double coupon, int years, double face, double rate)`
`double CalcYield(double coupon, int years, double face, double yield)`

## How to run
1. In the console navigate to folder holding all `.java` files
2. Run `javac *.java` to compile all java
3. Upon successfully compiling, run `java BondYieldCalculatorConsole` to run the program
4. Instructions on how to use the calculator will be printed to the console

## Environment
### Java
* java version "15.0.1" 2020-10-20
* Java(TM) SE Runtime Environment (build 15.0.1+9-18)
* Java HotSpot(TM) 64-Bit Server VM (build 15.0.1+9-18, mixed mode, sharing)
### Windows
* developed on Windows 10 Home version 2004

## Design Choices
### Yield Calculator
Rearranging the bond price equation to try to solve for *r* (the yield) does not give an answer. The equation, however, is a polynomial equation. If the yield is taken as the domain and the price as the range, it is possible to solve for roots of the equation regardless of the degree.
Solving for the root is difficult, but there are methods to progressively narrow down the value. One popular method for finding roots is the secant method, however, it does not always converge. Another common method is the bisection method, which I implemented to calculate bond yield.
### Bisection Method
The bisection method takes advantage of the intermediate value theorem. It states that if a continuous function within the closed interval between *a* and *b*, then there exists some *c* between *a* and *b* that will produce a value between *f(a)* and *f(b)*. The bisection method continuously improves the guesses for what *c* could be by decreasing the interval by half with each iteration.
Using the bond price equation as the function, and applying this idea, it is possible to find the yield of a bond given its coupon rate, years to maturity, face value, and price.
The implementation uses a lower guess of *-1.0* and upper guess of *5.0*. The bond price equation has several asymptotes. As *r* gets larger, the price converges to 0. As *r* gets closer to *-1.0* from the right side, the price grows to infinity. The *5.0* represents an upper bound of a 500% discount rate, which seems to my financially untrained eye as reasonable limit.
The bond price equation on the right side of *1.0* flips based on the years given. When the years are even, the price approaches positive infinity as it approaches *-1.0* from the left. When the years are odd, then the price approaches negative infinity as it approaches *-1.0* from the left. This made calculating the bond yield difficult with the bisection method in many cases because it resulted in absurdly large numbers.
### Memoization
Many of the calculations would be repeated. A simple way to get around it is with dynamic programming and memoization. There are two memos; one that records coupon, years, face, price and their corresponding yield and one that records coupon and face to their respective year - total coupon value combo. Having a memo for yield was one of the fastest ways to increase speed. The other memo records not the price because of the price equation. Bond price has two components, the value of the face as well as the value of all the coupon payments. The face value can be calculated relatively quickly and that result only needs to be added to the total coupon value. The total coupon value is described in a series, which makes it a better candidate for the memo.

## Future Work / Ideas
Currently the calculations made do not persist after closing the program. It would be nice to have some sort of non-volatile memory. Some ideas include: having a DB server that can record the calculations and can build up over time. Similarly, if the data can be shared across multiple machines, users would not need to constantly build up their own repository of calculations. Another possible idea is to have a cache. This would be a good compromise between storage and speed. If a calculation has not been accessed in a very long time, there's a good chance that it can be expunged.
It might be nice for users to be able to give the calculator a csv or some other large data dump and have it do a large amount of calculations. However, there may be similar functionalities on spreadsheet programs so this may be unnecessary.

## Other Files
`Driver.java` was used to help with testing during the development process. It should not be used as a part of the calculator.
`BondYieldCalculator.csv` has limited data on the performance of the calculator functions.

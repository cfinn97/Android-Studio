package com.example.camerafinn.calculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button[] buttons_ = new Button[22];
    private TextView text_;
    private double ans_ = 0.0;
    private boolean negPosClicked_ = false;
    private int parenthesesCount_ = 0;
    private boolean invalidEquation_ = false;
    private boolean newEquation_ = true;
    private boolean addToEquation_ = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] buttonNames = new String[]{"AC", "perentheses", "delete", "power", "negPos", "percent", "divide", "7", "8", "9", "multiply", "4", "5", "6", "minus", "1", "2", "3", "add", "0", ".", "equal"};
        text_ = (TextView) findViewById(R.id.text_view);
        text_.setMovementMethod(new ScrollingMovementMethod());
        for (int i = 0; i < buttonNames.length; i++) {
            String id = "button_" + buttonNames[i];
            int resID = getResources().getIdentifier(id, "id", getPackageName());
            buttons_[i] = findViewById(resID);
            buttons_[i].setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (((Button) v).getText().toString().equals("=")) { // equals button
            newEquation_ = true;
            parenthesesCount_ = 0;

            calculate(text_.getText().toString());

            negPosClicked_ = false;
            invalidEquation_ = false;
        } else if (((Button) v) == buttons_[0]) { // clear button
            newEquation_ = true;
            parenthesesCount_ = 0;
            negPosClicked_ = false;
            if (!text_.getText().toString().equals(""))
                text_.setText(null);
        } else if (((Button) v) == buttons_[2]) { // delete button
            if (text_.getText().toString().endsWith(")")) parenthesesCount_ += 1;
            else if (text_.getText().toString().endsWith("(")) parenthesesCount_ -= 1;
            if (text_.getText().toString().length() > 0)
                text_.setText(text_.getText().subSequence(0, text_.getText().toString().length() - 1));
            negPosClicked_ = false;
        } else if (((Button) v).getText().toString().equals("+/-")) { // +/- button
            if (!negPosClicked_) {
                text_.setText(text_.getText().toString() + buttons_[14].getText().toString());
                negPosClicked_ = true;
            } else {
                text_.setText(text_.getText().subSequence(0, text_.getText().toString().length() - 1));
                negPosClicked_ = false;
            }
        } else if (((Button) v) == buttons_[1]) { // ( button
            text_.setText(text_.getText().toString() + ((Button) v).getText().toString());
            parenthesesCount_ += 1;
            negPosClicked_ = false;
            newEquation_ = false;
        } else if (((Button) v).getText().toString().equals(")")) { // ) button
            text_.setText(text_.getText().toString() + ((Button) v).getText().toString());
            parenthesesCount_ -= 1;
            negPosClicked_ = false;
        }
        // for number, ., and operator buttons
        else {
            if (text_.getText().toString().equals("-") && negPosClicked_ && numClicked(v))
                text_.setText(text_.getText().toString() + ((Button) v).getText().toString());
            else if (text_.getText().toString().equals("ERROR") || text_.getText().toString().equals("Not Divisible by 0") ||
                    (newEquation_ && numClicked(v))) {
                text_.setText(((Button) v).getText().toString());
                addToEquation_ = false;
            } else {
                text_.setText(text_.getText().toString() + ((Button) v).getText().toString());
                addToEquation_ = true;
            }
            negPosClicked_ = false;
            newEquation_ = false;
        }
        if (parenthesesCount_ > 0)
            buttons_[21].setText(")");
        else buttons_[21].setText("=");
    }

    /**
     * Checks if the button clicked is a number or not
     *
     * @param v the button
     * @return
     */
    public boolean numClicked(View v) {
        for (int i = 0; i < 10; i++) {
            if (((Button) v).getText().toString().equals(String.valueOf(i))) return true;
        }
        return false;
    }

    /**
     * Calculates the given String
     *
     * @param s a String of numbers and operation signs
     */
    public void calculate(String s) {
        List<String> parts = new ArrayList<String>(Arrays.asList(s.split("(?<=[-+*/^%()])|(?=[-+*/^%()])")));
        List<String> partEquation;
        if (!numberFoundAt(parts, 0) && !parts.get(0).toString().equals("(")) parts.remove(0);
        if (addToEquation_ && s.startsWith("-")) {
            parts.set(0, String.valueOf(ans_));
            parts.remove(1);
        }
        if (parts.size() == 1) {
            ans_ = Double.parseDouble(parts.get(0).toString());
        } else if (parts.size() == 2 && parts.get(0).toString().equals("-")) {
            ans_ = Double.parseDouble(parts.get(1)) * -1;
            parts.remove(0);
            parts.set(0, String.valueOf(ans_));
        } else if (parts.get(1).toString().equals("%")) {
            double num = Double.parseDouble(parts.get(0).toString());
            num = num / 100.0;
            ans_ = ans_ * num;
            parts.set(0, Double.toString(ans_));
            parts.remove(1);
        } else if (parts.get(0).toString().equals("%")) {
            text_.setText("ERROR");
            return;
        }

        int pStart = parts.lastIndexOf("(");
        int pEnd = pStart;
        int i;

        while (pStart != -1) { // calculate parentheses portions first
            for (i = pStart + 1; i < parts.size(); i++) {
                if (parts.get(i).toString().equals(")")) {
                    pEnd = i;
                    break;
                }
            }
            partEquation = new ArrayList<>(parts.subList(pStart + 1, pEnd)); // only the equation in parentheses, not the parentheses themselves

            for (i = 1; i < partEquation.size(); i++)
                parts.remove(pStart + 1);

            solveEquation(partEquation);

            parts.set(pStart + 1, partEquation.get(0));

            if (parts.size() > pStart + 3 && !operationFoundAt(parts, pStart + 3))
                parts.set(pStart + 2, "*");
            else parts.remove(pStart + 2);

            if (pStart > 0 && !operationFoundAt(parts, pStart - 1))
                parts.set(pStart, "*");
            else parts.remove(pStart);

            pStart = parts.lastIndexOf("(");
            pEnd = pStart;
        }

        solveEquation(parts);

        text_.setText(parts.get(0).toString());
    }

    /**
     * Solves the given equation
     *
     * @param equation a list of String
     */
    public void solveEquation(List<String> equation) {
        int start;
        start = findOperation(equation);
        while (start != -1) {
            if (equation.size() > (start + 1) && equation.get(start + 1).toString().equals("-")) {
                equation.set(start + 1, String.valueOf(Double.parseDouble(equation.get(start + 2).toString()) * -1));
                equation.remove(start + 2);
            }
            if (equation.size() > start + 2 && equation.get(start + 2).toString().equals("%"))
                percent(equation, start + 2);
            compute(equation, start);
            if (invalidEquation_) return;
            start = findOperation(equation);
        }
        if (equation.size() > 1)
            text_.setText("Error");
    }

    /**
     * Copmutes the operation at the given sign index in the given equation
     *
     * @param equation a List of String: the equation being computed
     * @param sign     the index of the operation sign in the equation
     */
    public void compute(List<String> equation, int sign) {
        double num1 = Double.parseDouble(equation.get(sign - 1)), num2 = Double.parseDouble(equation.get(sign + 1));
        String operator = equation.get(sign).toString();
        if (operator.equals("*")) ans_ = num1 * num2;
        else if (operator.equals("/")) {
            if (num2 == 0.0) {
                text_.setText("Not Divisible by 0");
                invalidEquation_ = true;
                return;
            }
            ans_ = num1 / num2; // works
        } else if (operator.equals("+")) ans_ = num1 + num2;
        else if (operator.equals(buttons_[14].getText().toString())) {
            ans_ = num1 - num2;
        } else if (operator.equals("^")) ans_ = Math.pow(num1, num2);
        equation.set(sign - 1, String.valueOf(ans_));
        equation.remove(sign);
        equation.remove(sign);
    }

    /**
     * Calculates the percentage where the given sign index is in the given equation.
     *
     * @param equation a list of String: the equation being calculates
     * @param sign     the index of the % sign in the given equation
     */
    public void percent(List<String> equation, int sign) {
        double num1 = Double.parseDouble(equation.get(sign - 3)), num2 = Double.parseDouble(equation.get(sign - 1));
        num2 = num2 / 100.0;
        ans_ = num1 * num2;
        equation.set(sign - 1, String.valueOf(ans_));
        equation.remove(sign);
    }

    /**
     * Finds the location of the first mathematical operation to do in the given equation
     *
     * @param equation List of String, the mathematical equation being computed
     * @return the index in the given equation where the first operation to compute is; -1 if there is no mathematical sign
     */
    public int findOperation(List<String> equation) {
        int temp;
        int sign = equation.size();
        temp = equation.indexOf("^");
        if (temp != -1 && sign > temp) {
            sign = temp;
            return sign;
        }
        if (equation.indexOf("*") != -1 || equation.indexOf("/") != -1) {
            temp = equation.indexOf("*");
            if (temp != -1 && sign > temp)
                sign = temp;
            temp = equation.indexOf("/");
            if (temp != -1 && sign > temp) sign = temp;
            return sign;
        } else if (equation.indexOf("+") != -1 || equation.indexOf("-") != -1) {
            temp = equation.indexOf("+");
            if (temp != -1 && sign > temp)
                sign = temp;
            temp = equation.indexOf("-");
            if (temp != -1 && sign > temp) sign = temp;
            return sign;
        } else
            return -1;
    }

    /**
     * Chackes if there is a number at in the given equation at the given index
     *
     * @param equation a list of String
     * @param index    an integer in the given equation in question
     * @return true if there is a number at the given index in the given equation; false otherwise
     */
    public boolean numberFoundAt(List<String> equation, int index) {
        for (int i = 0; i < 10; i++) {
            if (equation.get(index).toString().endsWith(String.valueOf(i))) return true;
            if (equation.get(index).toString().endsWith(".") && equation.get(index).toString().startsWith(String.valueOf(i)))
                return true;
            if (equation.get(index).toString().startsWith(".") && equation.get(index).toString().endsWith(String.valueOf(i)))
                return true;
        }
        return false;
    }

    /**
     * Checks if there is a "/", "*", "+", or "-" sign in the given equation at the given index
     *
     * @param equation a list of String
     * @param index    an integer in the given equation in question
     * @return true if there is a "/", "*", "+", or "-" at the given place in the given equation; false otherwise
     */
    public boolean operationFoundAt(List<String> equation, int index) {
        if (equation.get(index).toString().equals("/") || equation.get(index).toString().equals("+") || equation.get(index).toString().equals("*") || equation.get(index).toString().equals("-") || equation.get(index).toString().equals("^"))
            return true;
        return false;
    }
}

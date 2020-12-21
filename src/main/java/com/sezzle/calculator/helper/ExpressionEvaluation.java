package com.sezzle.calculator.helper;

import java.util.Stack;
import java.util.regex.Pattern;

public class ExpressionEvaluation {

    static Pattern simpleLang = Pattern.compile("\\s*-?\\d+(\\s*[-+*%/]\\s*-?\\d+)*\\s*");

    public static double evaluate(String expression) {
        char[] tokens = expression.toCharArray();

        // Stack for numbers: 'values'
        Stack<Double> values = new Stack<>();

        // to check for double negative
        char prevChar = 0;
        boolean nextNumberNegative = false;

        // Stack for Operators: 'ops'
        Stack<Character> ops = new Stack<>();

        for (int i = 0; i < tokens.length; i++) {
            // Current token is a whitespace, skip it
            if (tokens[i] == ' ')
                continue;

            // Current token is a number, push it to stack for numbers
            if (tokens[i] >= '0' && tokens[i] <= '9') {
                StringBuilder stringBuilder = new StringBuilder();
                // There may be more than one digits in number
                while (i < tokens.length && tokens[i] >= '0' && tokens[i] <= '9') {
                    if ((values.empty() && prevChar == '-') || nextNumberNegative) {
                        stringBuilder.append('-');
                    }
                    prevChar = tokens[i];
                    stringBuilder.append(tokens[i++]);
                }
                values.push(Double.parseDouble(stringBuilder.toString()));
                i--;
            }

            // Current token is an opening brace, push it to 'ops'
            else if (tokens[i] == '(') {
                prevChar = tokens[i];
                ops.push(tokens[i]);
            }

                // Closing brace encountered, solve entire brace
            else if (tokens[i] == ')') {
                while (ops.peek() != '(')
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                ops.pop();
            }

            // Current token is an operator.
            else if (isCharOperator(tokens[i]) && !isCharOperator(prevChar) && !values.empty()) {
                // While top of 'ops' has same or greater precedence to current
                // token, which is an operator. Apply operator on top of 'ops'
                // to top two elements in values stack
                while (!ops.empty() && !values.empty() &&  hasPrecedence(tokens[i], ops.peek()))
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));

                // Push current token to 'ops'.
                ops.push(tokens[i]);
            }

            else if ((isCharOperator(tokens[i]) && isCharOperator(prevChar) && tokens[i] == '-')) {
                // next number is negative
                nextNumberNegative = true;
            }
            prevChar = tokens[i];
        }

        // Entire expression has been parsed at this point, apply remaining
        // ops to remaining values
        while (!ops.empty())
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));

        // Top of 'values' contains result, return it
        return values.pop();
    }

    private static boolean isCharOperator(char prevChar) {
        return prevChar == '+' || prevChar == '-' ||
                prevChar == '*' || prevChar == '/';
    }

    // Returns true if 'op2' has higher or same precedence as 'op1',
    // otherwise returns false.
    public static boolean hasPrecedence(char op1, char op2)
    {
        if (op2 == '(' || op2 == ')') return false;
        return (op1 != '*' && op1 != '/') || (op2 != '+' && op2 != '-');
    }

    // A utility method to apply an operator 'op' on operands 'a'
    // and 'b'. Return the result.
    public static double applyOp(char op, double b, double a)
    {
        switch (op)
        {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0)
                    throw new
                            UnsupportedOperationException("Cannot divide by zero");
                return a / b;
        }
        return 0;
    }

    public static boolean isValidArithmeticExpression(String expr) {
        return simpleLang.matcher(expr).matches();
    }
}

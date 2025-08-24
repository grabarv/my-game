package utils;

import java.util.*;

public class MathEvaluator {

    public static double eval(String expression, Map<String, Float> constants) {
        // 1. Replace constants in the expression
        for (Map.Entry<String, Float> entry : constants.entrySet()) {
            // Replace variable name with value (be careful with regex special chars)
            expression = expression.replaceAll("\\b" + entry.getKey() + "\\b", entry.getValue().toString());
        }

        // 2. Convert to postfix using Shunting Yard
        List<String> postfix = toPostfix(expression);

        // 3. Evaluate postfix
        return evalPostfix(postfix);
    }

    private static List<String> toPostfix(String expression) {
        List<String> output = new ArrayList<>();
        Deque<Character> ops = new ArrayDeque<>();
        StringBuilder number = new StringBuilder();

        Map<Character, Integer> precedence = Map.of(
                '+', 1,
                '-', 1,
                '*', 2,
                '/', 2
        );

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isWhitespace(c)) continue;

            if (Character.isDigit(c) || c == '.') {
                number.append(c);
            } else {
                if (number.length() > 0) {
                    output.add(number.toString());
                    number.setLength(0);
                }

                if (c == '(') {
                    ops.push(c);
                } else if (c == ')') {
                    while (!ops.isEmpty() && ops.peek() != '(') {
                        output.add(String.valueOf(ops.pop()));
                    }
                    ops.pop(); // remove '('
                } else if (precedence.containsKey(c)) {
                    while (!ops.isEmpty() && ops.peek() != '(' &&
                            precedence.get(ops.peek()) >= precedence.get(c)) {
                        output.add(String.valueOf(ops.pop()));
                    }
                    ops.push(c);
                } else {
                    throw new IllegalArgumentException("Unexpected char: " + c);
                }
            }
        }

        if (number.length() > 0) {
            output.add(number.toString());
        }

        while (!ops.isEmpty()) {
            output.add(String.valueOf(ops.pop()));
        }

        return output;
    }

    private static double evalPostfix(List<String> postfix) {
        Deque<Double> stack = new ArrayDeque<>();

        for (String token : postfix) {
            switch (token) {
                case "+" -> stack.push(stack.pop() + stack.pop());
                case "-" -> {
                    double b = stack.pop();
                    double a = stack.pop();
                    stack.push(a - b);
                }
                case "*" -> stack.push(stack.pop() * stack.pop());
                case "/" -> {
                    double b = stack.pop();
                    double a = stack.pop();
                    stack.push(a / b);
                }
                default -> stack.push(Double.parseDouble(token));
            }
        }

        return stack.pop();
    }
}


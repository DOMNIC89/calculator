package com.sezzle.calculator.helper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExpressionEvaluationTest {

    ExpressionEvaluation expressionEvaluation;

    @BeforeEach
    public void setup() {
        expressionEvaluation = new ExpressionEvaluation();
    }

    @Test
    public void testEvaluateExpression() {
        String question = "2+10/-3";
        double expectedAnswer = -1.3333333333333335;
        double answer = ExpressionEvaluation.evaluate(question);
        Assertions.assertEquals(expectedAnswer, answer);
    }

    @Test
    public void testEvaluateExpressionWithNegativeFirst() {
        String question = "-2+2";
        double expectedAnswer = 0;
        double answer = ExpressionEvaluation.evaluate(question);
        Assertions.assertEquals(expectedAnswer, answer);
    }

    @Test
    public void testEvaluateExpressionWithLongerQuestion() {
        String question = "10+2/2";
        double expectedAnswer = 11;
        double answer = ExpressionEvaluation.evaluate(question);
        Assertions.assertEquals(expectedAnswer, answer);
    }

    @Test
    public void testEvaluateExpressionWithAnotherLongerQuestion() {
        String question = "10+2/2*6-5";
        double expectedAnswer = 11;
        double answer = ExpressionEvaluation.evaluate(question);
        Assertions.assertEquals(expectedAnswer, answer);
    }

    @Test
    public void testIsValidArithmeticExpressionForValid() {
        String question = "2--23";
        Assertions.assertTrue(ExpressionEvaluation.isValidArithmeticExpression(question));
    }

    @Test
    public void testIsValidArithmeticExpressionForInvalid() {
        String question = "2**2";
        Assertions.assertFalse(ExpressionEvaluation.isValidArithmeticExpression(question));
    }

    @Test
    public void testIsValidArithmeticExpressionForInvalidWithPlusSign() {
        String question = "2++2";
        Assertions.assertFalse(ExpressionEvaluation.isValidArithmeticExpression(question));
    }
}
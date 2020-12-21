package com.sezzle.calculator.common;

import com.sezzle.calculator.command.CalculatorActivityCO;
import com.sezzle.calculator.model.CalculatorActivity;

public class CalculatorActivityNapper {

    public static CalculatorActivityCO convertToCalculatorActivityCO(CalculatorActivity activity) {
        return new CalculatorActivityCO.CalculatorActivityCOBuilder()
                .user(activity.getUser())
                .question(activity.getQuestion())
                .answer(activity.getAnswer())
                .timestamp(activity.getTimestamp())
                .build();
    }

}

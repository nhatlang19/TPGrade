package com.tpgrade.tpgrade.Processors.RectangleTemplate;

import com.tpgrade.tpgrade.Processors.Interfaces.RectanglePointInterface;

public class TemplateFactory {
    public static RectanglePointInterface factory(int number) {
        switch (number) {
            case 50:
            default:
                return new Template50();
        }
    }
}

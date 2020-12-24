package com.wp;

public class BinaryHypothesis {

    private final static int CODE_SIZE = 4;

    private final static int DIVIDE = 600;

    private int codeCount = 0;

    private double code[] = new double[CODE_SIZE];

    public BinaryHypothesis() {
        // Do something
    }

    public void addValue(final long time, final double value) {
        if (codeCount >= CODE_SIZE) {
            codeCount = 0;
            printCode();
        }
        if (value < DIVIDE) {
            code[codeCount++] = 0;
        } else {
            code[codeCount++] = 1;
        }
    }

    private void printCode() {
        if (code[0] == 0 && code[1] == 0 && code[2] == 0 && code[3] == 0) {
            System.out.print("0");
            return;
        }
        if (code[0] == 0 && code[1] == 0 && code[2] == 0 && code[3] == 1) {
            System.out.print("1");
            return;
        }
        if (code[0] == 0 && code[1] == 0 && code[2] == 1 && code[3] == 0) {
            System.out.print("2");
            return;
        }
        if (code[0] == 0 && code[1] == 0 && code[2] == 1 && code[3] == 1) {
            System.out.print("3");
            return;
        }
        if (code[0] == 0 && code[1] == 1 && code[2] == 0 && code[3] == 0) {
            System.out.print("4");
            return;
        }
        if (code[0] == 0 && code[1] == 1 && code[2] == 0 && code[3] == 1) {
            System.out.print("5");
            return;
        }
        if (code[0] == 0 && code[1] == 1 && code[2] == 1 && code[3] == 0) {
            System.out.print("6");
            return;
        }
        if (code[0] == 0 && code[1] == 1 && code[2] == 1 && code[3] == 1) {
            System.out.print("7");
            return;
        }
        if (code[0] == 1 && code[1] == 0 && code[2] == 0 && code[3] == 0) {
            System.out.print("8");
            return;
        }
        if (code[0] == 1 && code[1] == 0 && code[2] == 0 && code[3] == 1) {
            System.out.print("9");
            return;
        }
        //System.out.println("?");
    }
}

package sample;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Роман on 07.11.2014.
 */
public class Jilba extends Metrics {
    private Comments comments;
    private int numberOfOperators = 0;
    private int CL = 0;
    private int arithmeticOperatorCount = 0;
    private int compoundAssignmentOperatorCount = 0;
    private int comparisonOperatorCount = 0;
    private int logicalOperatorCount = 0;
    private int cyclicOperatorCount = 0;
    private int otherOperatorCount = 0;
    private int maxDepth = 0;
    public String allCodeInline = "";
    private Pattern patternCL = Pattern.compile("([ \t]*if([ \t]|[(])|[?].+?[:].+)");
    private Pattern ifCLMatchPattern = Pattern.compile("[ \t]*if[ \t]*[(](.){1,}[)](.)*");
    private Pattern braceMatchPattern = Pattern.compile("[ \t]*[{](.)*");

    private Pattern arithmeticOperatorPattern = Pattern.compile("[a-zA-Z \t0-9)](=|[+]|-|[*]|[/]|%|[+][+]|[-][-])[a-zA-Z \t0-9(;]");
    private Pattern compoundAssignmentOperatorPattern = Pattern.compile("(([-+*/%&|^])|<{2}|>{2})=(?!=)");
    private Pattern comparisonOperatorPattern = Pattern.compile("[a-zA-Z \t0-9)](==|!=|>|<|>=|<=)[a-zA-Z \t0-9(;]");
    private Pattern logicalOperatorPattern = Pattern.compile("(![a-zA-Z \t(]|[a-zA-Z \t0-9)](&&|[|][|])[a-zA-Z \t0-9(;])");
    private Pattern cyclicOperatorPattern = Pattern.compile("((for|while)[ \t]*[(]|do[ \t]*[{])");
    private Pattern otherOperatorsPattern = Pattern.compile("(![a-zA-Z \t(]|[a-zA-Z \t)](~|&|\\||\\^|<<|>>|\\.|\\.\\*|\\[|,|instanceof)[a-zA-Z \t0-9(;])");

    private ArrayList < String > stringsWithoutComments = new ArrayList < String > ();
    protected ArrayList < String > sourceCodeStringsOnly = new ArrayList < String > ();

    public Jilba() {
        comments = super.comments;
        stringsWithoutComments = super.stringsWithoutComments;
        sourceCodeStringsOnly = super.sourceCodeStringsOnly;
        for (String currentString: stringsWithoutComments) {
            currentString = currentString.replaceAll("\n", "");
            allCodeInline += currentString;
        }
        countOperators();
    }

    public int getCL() {
        return CL;
    }

    public float getRelativeCL() {
        return (float) CL / (float) numberOfOperators;
    }

    public int getNumberOfOperators() {
        return numberOfOperators;
    }

    public int getMaxDepth() {
        countMaxCLDepth(-1, allCodeInline, false);
        return maxDepth;
    }

    public void countOperators() {
        CL = 0;
        arithmeticOperatorCount = 0;
        compoundAssignmentOperatorCount = 0;
        comparisonOperatorCount = 0;
        cyclicOperatorCount = 0;
        otherOperatorCount = 0;
        for (String currentString: stringsWithoutComments) {
            Matcher matchCL = patternCL.matcher(currentString);
            Matcher matchArithmeticOperators = arithmeticOperatorPattern.matcher(currentString);
            Matcher matchCompoundOperators = compoundAssignmentOperatorPattern.matcher(currentString);
            Matcher matchBitwiseOperators = otherOperatorsPattern.matcher(currentString);
            Matcher matchComparisonOperators = comparisonOperatorPattern.matcher(currentString);
            Matcher matchCyclicOperators = cyclicOperatorPattern.matcher(currentString);
            Matcher matchLogicalOperators = logicalOperatorPattern.matcher(currentString);

            while (matchCL.find()) {
                CL++;
            }
            while (matchArithmeticOperators.find()) {
                arithmeticOperatorCount++;
            }
            while (matchCompoundOperators.find()) {
                compoundAssignmentOperatorCount++;
            }
            while (matchComparisonOperators.find()) {
                comparisonOperatorCount++;
            }
            while (matchLogicalOperators.find()) {
                logicalOperatorCount++;
            }
            while (matchCyclicOperators.find()) {
                cyclicOperatorCount++;
            }
            while (matchBitwiseOperators.find()) {
                otherOperatorCount++;
            }
        }

        numberOfOperators = CL + arithmeticOperatorCount + compoundAssignmentOperatorCount
                + cyclicOperatorCount + comparisonOperatorCount + logicalOperatorCount + otherOperatorCount;

    }

    public int getLastOvalBracePos(String substring) {
        Matcher findIfCL = ifCLMatchPattern.matcher(substring);
        findIfCL.find();
        int countOvalBrace = 0;
        boolean ovalBraceTookPlace = false;
        int i = 0;
        for (i = findIfCL.start(); i < substring.length(); i++) {
            if (substring.charAt(i) == '(') {
                countOvalBrace++;
                ovalBraceTookPlace = true;
            } else if (substring.charAt(i) == ')') {
                countOvalBrace--;
            }
            if (ovalBraceTookPlace && countOvalBrace == 0) {
                break;
            }
        }
        return i;
    }

    public int getFigureBracePos(String substring) {
        Matcher matchBrace = braceMatchPattern.matcher(substring);
        matchBrace.find();
        int i = 0;
        for (i = matchBrace.start(); i < substring.length(); i++) {
            if (substring.charAt(i) == '{') {
                break;
            }
        }
        return i;
    }

    public int getLastFigureBracePos(String substring) {
        Matcher matchBrace = braceMatchPattern.matcher(substring);
        matchBrace.find();
        int countFigureBrace = 0;
        boolean figureBraceTookPlace = false;
        int i = 0;
        for (i = matchBrace.start(); i < substring.length(); i++) {
            if (substring.charAt(i) == '{') {
                countFigureBrace++;
                figureBraceTookPlace = true;
            } else if (substring.charAt(i) == '}') {
                countFigureBrace--;
            }
            if (figureBraceTookPlace && countFigureBrace == 0) {
                break;
            }
        }
        return i;
    }

    public void countMaxCLDepth(int depth, String substring, boolean insideCL) {
        Matcher matchIfCL = ifCLMatchPattern.matcher(substring);
        Matcher matchBrace = braceMatchPattern.matcher(substring);

        if (depth > maxDepth) {
            maxDepth = depth;
        }

        if (insideCL) {
            if (matchIfCL.matches()) {
                int i = getLastOvalBracePos(substring);
                countMaxCLDepth(depth + 1, substring.substring(i + 1), true);
            } else if (matchBrace.matches()) {
                int figureBracePos = getFigureBracePos(substring);
                int lastFigureBracePos = getLastFigureBracePos(substring);
                countMaxCLDepth(depth + 1, substring.substring(figureBracePos + 1, lastFigureBracePos), false);
                countMaxCLDepth(depth, substring.substring(lastFigureBracePos + 1), false);
            } else {
                if (matchIfCL.find()) {
                    int ifCLStart = matchIfCL.start();
                    countMaxCLDepth(depth, substring.substring(ifCLStart), false);
                } else {
                    depth++;
                    if (depth > maxDepth) {
                        maxDepth = depth;
                    }
                    return;
                }
            }
        } else {
            if (matchIfCL.find()) {
                int lastOvalBracePos = getLastOvalBracePos(substring);
                countMaxCLDepth(depth, substring.substring(lastOvalBracePos + 1), true);
            }
        }
    }
}
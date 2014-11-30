package sample;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Роман on 05.11.2014.
 */
public class Sloc extends Metrics {
    private Comments comments;
    private int stringsCount = 0;
    private int emptyStringsCount = 0;
    private int commentsCount = 0;
    private float commentsPercentage = 0;

    private ArrayList<String> allStrings = new ArrayList<String>();
    private ArrayList<String> sourceCodeStringsOnly = new ArrayList<String>();
    private ArrayList<Integer> functionsStringsCount = new ArrayList<Integer>();
    private ArrayList<Integer> functionsSourceCodeStringsCount = new ArrayList<Integer>();
    private ArrayList<Integer> classesStringsCount = new ArrayList<Integer>();
    private ArrayList<Integer> classesSourceCodeStringsCount = new ArrayList<Integer>();

    private Pattern functionDeclarationPattern = Pattern.compile("((public|private)[ \t]{1,}){0,1}" +
            "((void|int|[A-Za-z]{1,})[ \t]{1,})[::_A-Za-z]{1,}[ \t]*\\((((void|int|[A-Za-z]{1,})" +
            "[ \t]{1,})[:&*_A-Za-z]{1,}[ \t]*[,]*)*\\)");
    private Pattern classDeclarationPattern = Pattern.compile("class[ \t]{1,}[A-Za-z_]{1,}");

    public Sloc(){
        this.allStrings = super.allStrings;
        stringsCount = this.allStrings.size();
        comments = super.comments;
        sourceCodeStringsOnly = super.sourceCodeStringsOnly;
    }

    public ArrayList<Integer> getFunctionsStringsCount(){
        return functionsStringsCount;
    }

    public ArrayList<Integer> getFunctionsSourceCodeStringsCount(){
        return functionsSourceCodeStringsCount;
    }

    public ArrayList<Integer> getClassesStringsCount(){
        return classesStringsCount;
    }

    public ArrayList<Integer> getClassesSourceCodeStringsCount(){
        return classesSourceCodeStringsCount;
    }

    public float getAverage(ArrayList<Integer> myIntegers){
        int sum = 0;
        int numberOfIntegers = myIntegers.size();
        for(int number:myIntegers){
            sum+=number;
        }

        return (float)sum/(float)numberOfIntegers;
    }

    public int getStringsCount(){
        return stringsCount;
    }

    public int getEmptyStringsCount(){
        countEmptyStrings();
        return emptyStringsCount;
    }

    public int getCommentsCount(){
        countComments();
        return commentsCount;
    }

    public float getCommentsPercentage(){
        countCommentsPercentage();
        return (commentsPercentage*100);
    }

    public float getAverageFunctionsStringsCount(){
        countFunctionsStrings();
        return getAverage(functionsStringsCount);
    }

    public float getAverageFunctionsSourceCodeStringsCount(){
        countFunctionsSourceCodeStrings();
        return getAverage(functionsSourceCodeStringsCount);
    }

    public float getAverageClassesStringsCount(){
        countClassesStrings();
        return getAverage(classesStringsCount);
    }

    public float getAverageClassesSourceCodeStringsCount(){
        countClassesSourceCodeStrings();
        return getAverage(classesSourceCodeStringsCount);
    }

    private void countEmptyStrings(){
        emptyStringsCount = 0;
        for(String currentString:allStrings){
            if(currentString.isEmpty()){
                emptyStringsCount++;
            }
        }
    }

    private void countComments(){
        commentsCount = comments.commentsList.size();
    }

    private void countCommentsPercentage(){
        commentsPercentage =((float) commentsCount)/((float) stringsCount);
    }

    private void countFunctionsStrings(){
        functionsStringsCount = getStringsOfBlock(functionDeclarationPattern, allStrings);
    }

    private void countFunctionsSourceCodeStrings(){
        functionsSourceCodeStringsCount = getStringsOfBlock(functionDeclarationPattern,sourceCodeStringsOnly);
    }

    private void countClassesStrings(){
        classesStringsCount = getStringsOfBlock(classDeclarationPattern, allStrings);

    }

    private void countClassesSourceCodeStrings(){
        classesSourceCodeStringsCount = getStringsOfBlock(classDeclarationPattern,sourceCodeStringsOnly);
    }

    private ArrayList<Integer> getStringsOfBlock(Pattern blockDeclarationPattern, ArrayList<String> sourceToAnalyze){
        ArrayList<Integer> stringsOfBlockCount = new ArrayList<Integer>();
        boolean insideFunction = false;
        boolean openBraceTookPlace = false;
        int braceCount = 0;
        int stringsCount = 0;
        int braceInspectionStartPos = 0;
        int startIndex = 0;
        int endIndex = 0;
        int i = 0;
        while(i<sourceToAnalyze.size()){
            String currentString = sourceToAnalyze.get(i);
            if(insideFunction){
                if(typeOfString(currentString)==USUAL_STRING){
                    for(int j = braceInspectionStartPos; j<currentString.length();j++){
                        if (currentString.charAt(j)=='{'){
                            braceCount++;
                            openBraceTookPlace = true;
                        }else if(currentString.charAt(j)=='}'){
                            braceCount--;
                        }
                    }
                    if((braceCount==0)&&openBraceTookPlace){
                        endIndex = i;
                        stringsCount = endIndex-startIndex+1;
                        stringsOfBlockCount.add(stringsCount);
                        insideFunction = false;
                    }
                    braceInspectionStartPos = 0;
                }
                i++;
            }else{
                Matcher matchFunctionDeclaration = blockDeclarationPattern.matcher(currentString);
                if(matchFunctionDeclaration.find()&&(typeOfString(currentString)==USUAL_STRING)){
                    insideFunction = true;
                    braceInspectionStartPos = matchFunctionDeclaration.end();
                    braceCount = 0;
                    stringsCount = 0;
                    startIndex = i;
                    openBraceTookPlace = false;
                }else{
                    i++;
                }
            }
        }
        return stringsOfBlockCount;
    }
}

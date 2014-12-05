package sample;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Роман on 08.11.2014.
 */
public class Metrics {
    class Comments{
        public ArrayList<String> commentsList = new ArrayList<String>();
        public ArrayList<Boolean> isComment = new ArrayList<Boolean>();

        public Comments(){
            commentsList.clear();
            isComment.clear();
        }

        public void addComment(String currentString){
            commentsList.add(currentString);
            isComment.add(true);
        }

        public void markNonComment(){
            isComment.add(false);
        }

    }
    public final int USUAL_STRING = 0;
    public final int INLINE_COMMENT = 1;
    public final int BEGINNING_OF_MULTILINE_COMMENT = 2;
    public final int INFINITY = 999;

    private Pattern doubleSlashPattern = Pattern.compile("//");
    private Pattern openingSlashPattern = Pattern.compile("/[*]");
    private Pattern closingSlashPattern = Pattern.compile("[*]/");

    protected Comments comments;

    protected ArrayList<String> allStrings = new ArrayList<String>();
    protected ArrayList<String> stringsWithoutComments = new ArrayList<String>();
    protected ArrayList<String> sourceCodeStringsOnly = new ArrayList<String>();

    public Metrics(){
        allStrings = Controller.getStringsFromTextArea();
        comments = getComments();
        stringsWithoutComments = getStringsWithoutComments();
        sourceCodeStringsOnly = getSourceCodeStringsOnly();
    }

    public ArrayList<String> getStringsWithoutComments(){
        ArrayList<String> stringsNonComments = new ArrayList<String>();
        stringsNonComments.clear();
        String allCodeInline = Controller.textToAnalyse.getText();
        String codeWithoutCommentsInline = removeComments(allCodeInline);
        int startPos = 0;
        int i = 0;
        while(i<codeWithoutCommentsInline.length()){
            if((codeWithoutCommentsInline.charAt(i)=='\n')||(i==codeWithoutCommentsInline.length()-1)){
                if(i!=0){
                    String stringToAdd = codeWithoutCommentsInline.substring(startPos,i+1);
                    stringToAdd = stringToAdd.replaceAll("\n","");
                    stringsNonComments.add(stringToAdd);
                    startPos = i+1;
                }
            }
            i++;
        }
        return stringsNonComments;
    }

    public static String removeCharAt(String s, int pos) {

        return s.substring(0,pos)+" "+s.substring(pos+1);

    }

    private String removeComments(String sourceCode){
        int typeOfComment = 0;
        int i = 0;
        while(i<sourceCode.length()){
            if(typeOfComment==0){
                if((sourceCode.charAt(i)=='/')&&(sourceCode.charAt(i+1)=='/')){
                    typeOfComment=INLINE_COMMENT;
                    sourceCode = removeCharAt(sourceCode,i++);
                    sourceCode = removeCharAt(sourceCode,i++);
                }else if((sourceCode.charAt(i)=='/')&&(sourceCode.charAt(i+1)=='*')){
                    typeOfComment=BEGINNING_OF_MULTILINE_COMMENT;
                    sourceCode = removeCharAt(sourceCode,i++);
                    sourceCode = removeCharAt(sourceCode,i++);
                }else{
                    i++;
                }
            }else{
                if((sourceCode.charAt(i)=='\n')){
                    if(typeOfComment==INLINE_COMMENT) {
                        typeOfComment = 0;
                    }
                    i++;
                }else if((sourceCode.charAt(i)=='*')&&(sourceCode.charAt(i+1)=='/')){
                    if(typeOfComment==BEGINNING_OF_MULTILINE_COMMENT){
                        typeOfComment=0;
                        sourceCode = removeCharAt(sourceCode,i++);
                        sourceCode = removeCharAt(sourceCode,i++);
                    }else{
                        sourceCode = removeCharAt(sourceCode,i++);
                    }
                }else{
                    sourceCode = removeCharAt(sourceCode,i++);
                }

            }
        }
        return sourceCode;
    }

    public ArrayList<String> getSourceCodeStringsOnly(){
        ArrayList<String> sourceCodeStrings = new ArrayList<String>();
        for(String currentString:stringsWithoutComments){
            if(!((currentString.isEmpty()==true)||(currentString.matches("[ \t\n]{1,}")))){
                sourceCodeStrings.add(currentString);
            }
        }
        return sourceCodeStrings;
    }

    private boolean matchString(String stringToTest, String regEx){
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(stringToTest);
        return matcher.matches();
    }

    public int typeOfString(String testString) {
        int doubleSlashFirstPos = INFINITY;
        int openingSlashPos = INFINITY;
        int closingSlashPos = INFINITY;

        Matcher matchDoubleSlash = doubleSlashPattern.matcher(testString);
        if (matchDoubleSlash.find()) {
            doubleSlashFirstPos = matchDoubleSlash.start();
        }
        Matcher matchOpeningSlash = openingSlashPattern.matcher(testString);
        while (matchOpeningSlash.find()) {
            openingSlashPos = matchOpeningSlash.start();
        }
        Matcher matchClosingSlash = closingSlashPattern.matcher(testString);
        while (matchClosingSlash.find()) {
            closingSlashPos = matchClosingSlash.start();
        }
        if ((doubleSlashFirstPos < openingSlashPos) ||
                ((openingSlashPos < closingSlashPos) && closingSlashPos != INFINITY)) {
            return INLINE_COMMENT;
        }else if (((openingSlashPos > closingSlashPos) && openingSlashPos != INFINITY) ||
                (openingSlashPos < closingSlashPos) && closingSlashPos == INFINITY) {
            return BEGINNING_OF_MULTILINE_COMMENT;
        }else{
            return 0;
        }
    }

    public boolean hasCommentClosingSlash(String stringToMatch){
        String commentClosingSlash = "(.)*[*]/(.)*";
        if(matchString(stringToMatch,commentClosingSlash)){
            return true;
        }else{
            return false;
        }
    }

    public Comments getComments(){
        Comments comments = new Comments();
        boolean multilineComment = false;
        for(String currentString:allStrings){
            if(multilineComment){
                comments.addComment(currentString);
                if(hasCommentClosingSlash(currentString)){
                    if(typeOfString(currentString)==BEGINNING_OF_MULTILINE_COMMENT){
                        multilineComment = true;
                    }else{
                        multilineComment = false;
                    }
                }
            }else{
                switch(typeOfString(currentString)){
                    case INLINE_COMMENT:{
                        comments.addComment(currentString);
                        break;
                    }
                    case BEGINNING_OF_MULTILINE_COMMENT: {
                        comments.addComment(currentString);
                        multilineComment = true;
                        break;
                    }
                    default:{
                        comments.markNonComment();
                    }
                }
            }
        }
        return comments;
    }
}

package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.ListView;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.ArrayList;


public class Controller {

    public static ArrayList<String> getStringsFromTextArea(){
        ArrayList<String> allStringsList = new ArrayList<String>();
        for(String currentString:textToAnalyse.getText().split("\\n")){
            allStringsList.add(currentString);
        }
        return allStringsList;
    }

    @FXML static TextArea textToAnalyse;
    @FXML ListView slocListView;
    @FXML ListView jilbaListView;

    @FXML public void loadButtonClicked(){
        File sourceFile;
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "TXT", "txt");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(chooser);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            sourceFile = chooser.getSelectedFile();
            try {
                BufferedReader file = new BufferedReader(new FileReader(sourceFile.getAbsolutePath()));
                String currentString;
                while((currentString=file.readLine())!=null){
                    textToAnalyse.appendText(currentString+"\n");
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    @FXML public void startSlocButtonClicked(){
        Sloc mySlocMetric = new Sloc();
        String stringsCount = "Number of strings: "+String.valueOf(mySlocMetric.getStringsCount());
        String emptyStringsCount = "Number of empty strings: "+String.valueOf(mySlocMetric.getEmptyStringsCount());
        String commentsCount = "Number of strings with comments: "+String.valueOf(mySlocMetric.getCommentsCount());
        String commentsPercentage = "Comments percentage: "+String.valueOf(mySlocMetric.getCommentsPercentage());
        String averageFunctionsStringsCount = "Average number of strings for functions: "+
                String.valueOf(mySlocMetric.getAverageFunctionsStringsCount());

        String functionsStringsCount = mySlocMetric.getFunctionsStringsCount().toString();
        String averageFunctionsSourceCodeStringsCount = "Average number of source code strings for functions "+
                String.valueOf(mySlocMetric.getAverageFunctionsSourceCodeStringsCount());

        String functionsSourceCodeStringsCount = mySlocMetric.getFunctionsSourceCodeStringsCount().toString();


        String averageClassesStringsCount = "Average number of strings for classes: "+
                String.valueOf(mySlocMetric.getAverageClassesStringsCount());

        String classesStringsCount = mySlocMetric.getClassesStringsCount().toString();
        String averageClassesSourceCodeStringsCount = "Average number of source code strings for classes "+
                String.valueOf(mySlocMetric.getAverageClassesSourceCodeStringsCount());

        String classesSourceCodeStringsCount = mySlocMetric.getClassesSourceCodeStringsCount().toString();


        ObservableList<String> items = FXCollections.observableArrayList(stringsCount,emptyStringsCount,commentsCount,
                commentsPercentage,averageFunctionsStringsCount,functionsStringsCount,
                averageFunctionsSourceCodeStringsCount,functionsSourceCodeStringsCount,averageClassesStringsCount,
                classesStringsCount,averageClassesSourceCodeStringsCount,classesSourceCodeStringsCount);

        slocListView.setItems(items);
    }

    @FXML public void jilbaButtonClicked(){
        Jilba myJilba = new Jilba();
        myJilba.countMaxCLDepth(-1, myJilba.allCodeInline, false);
        String CL ="CL = " + String.valueOf(myJilba.getCL());
        String relativeCL ="cl = CL/Number of operators = " + String.valueOf(myJilba.getRelativeCL());
        String numberOfOperators = "Number of operators = " + String.valueOf(myJilba.getNumberOfOperators());
        String maxDepth = "CL maximal depth =" + String.valueOf(myJilba.getMaxDepth());
        ObservableList<String> items = FXCollections.observableArrayList(CL,relativeCL,numberOfOperators,maxDepth);
        jilbaListView.setItems(items);
   }
}

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.*;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    FileChooser fileChooser;
    String xml, xmlOut;
    File input, output;

    @FXML
    TextArea originalTA = new TextArea();
    @FXML
    TextArea resultTA = new TextArea();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileChooser = new FileChooser();
        //fileChooser.setInitialDirectory(new File("src\\sample"));

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        FileChooser.ExtensionFilter extFilter2 = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.getExtensionFilters().add(extFilter2);
    }

    public void onLoadXMLFile(ActionEvent e) {

        input = fileChooser.showOpenDialog(new Stage());

        if (input != null) {
            InputStream orgInStream = System.in;
            try {
                System.setIn(new FileInputStream(input));
            } catch (FileNotFoundException ee) {
                ee.printStackTrace();
            }
            xml = CustomStdIn.readString().replaceAll("\r", "");
            originalTA.setText(xml);
            CustomStdIn.close();
            System.setIn(orgInStream);
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("You must provide a file");
            alert.setContentText("You didn't provide a file");
            alert.showAndWait();
        }
    }

    public void onSaveXMLFile(ActionEvent e) {
        xml = originalTA.getText();
        output = fileChooser.showSaveDialog(new Stage());

        if (output != null) {
            PrintStream orgOutStream = System.out;
            try {
                System.setOut(new PrintStream(output));
            } catch (FileNotFoundException ee) {
                ee.printStackTrace();
            }
            CustomStdOut.write(xmlOut);

            CustomStdOut.close();
            System.setOut(orgOutStream);
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("You must provide a path");
            alert.setContentText("You didn't provide a path to save the file");
            alert.showAndWait();
        }
    }

    public void onJSON(ActionEvent e) {
        xml = originalTA.getText();
        if (checkIfEmpty(xml))
            return;

        ConsistencyCheck checker = new ConsistencyCheck(xml);
        if (checker.checkBalancedTags() && !xml.isEmpty()) {
            xmlOut = JSON.XMLToJSON(xml);
            resultTA.setText(xmlOut);
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Consistency error");
            alert.setHeaderText("Not consistent");
            alert.setContentText("The provided XML has to be consistent to be converted to JSON");
            alert.showAndWait();
        }

    }

    public void OnConsistency(ActionEvent e) {
        xml = originalTA.getText();
        if (checkIfEmpty(xml))
            return;

        ConsistencyCheck checker = new ConsistencyCheck(xml);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Consistency check");
        alert.setHeaderText("Consistency check");

        if (xml.isEmpty()) {
            alert.setContentText("No xml provided");
            alert.showAndWait();
            return;
        }

        if (checker.checkBalancedTags()) {
            alert.setContentText("XML file is consistent");
        } else {
            StringBuilder msg = new StringBuilder();
            msg.append("Errors count = ").append(checker.errorsCounter)
                    .append("\n").append("Error/s in the following tag/s:\n");

            for (String s : checker.leftTags) {
                msg.append(s).append("\n");
            }

            alert.setContentText("XML file is NOT consistent");
            resultTA.setText(msg.toString());
        }
        alert.showAndWait();
    }

    public void onFormatting(ActionEvent e) {
        xml = originalTA.getText();
        if (checkIfEmpty(xml))
            return;

        xmlOut = Formatting.format(xml);
        resultTA.setText(xmlOut);
    }

    public void onMinifying(ActionEvent e) {
        xml = originalTA.getText();
        if (checkIfEmpty(xml))
            return;

        xmlOut = Minifying.minify(xml);
        resultTA.setText(xmlOut);
    }

    public void onCompress(ActionEvent e) {
        xml = originalTA.getText();
        if (checkIfEmpty(xml))
            return;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("compression status");
        alert.setHeaderText("compression status");

        output = fileChooser.showSaveDialog(new Stage());

        if (output != null) {
            HuffmanCompression.compress(xml, output);
            alert.setContentText("compression completed");

            InputStream orgInStream = System.in;

            try {
                System.setIn(new FileInputStream(output));
            } catch (FileNotFoundException ee) {
                ee.printStackTrace();
            }
            xmlOut = CustomStdIn.readString();

            CustomStdIn.close();
            System.setIn(orgInStream);

            resultTA.setText(xmlOut);
        } else {
            alert.setContentText("You didn't provide a path to save the file");
            alert.showAndWait();
        }
    }

    public void onDecompress(ActionEvent e) {

        File originalFile, newFile;

        originalFile = fileChooser.showOpenDialog(new Stage());
        newFile = fileChooser.showSaveDialog(new Stage());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("decompression status");
        alert.setHeaderText("decompression status");
        if (originalFile != null && newFile != null) {
            HuffmanCompression.expand(originalFile, newFile);
            alert.setContentText("decompression completed");

            InputStream orgInStream = System.in;

            try {
                System.setIn(new FileInputStream(originalFile));
            } catch (FileNotFoundException ee) {
                ee.printStackTrace();
            }

            originalTA.setText(CustomStdIn.readString());
            CustomStdIn.close();
            try {
                System.setIn(new FileInputStream(newFile));
            } catch (FileNotFoundException ee) {
                ee.printStackTrace();
            }

            xml = CustomStdIn.readString().replaceAll("\r", "");
            CustomStdIn.close();
            System.setIn(orgInStream);
            resultTA.setText(xml);
        } else {
            alert.setContentText("You didn't provide a path/s to save/load the files");
            alert.showAndWait();
        }
    }

    private static boolean checkIfEmpty(String xml) {
        if (xml.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No XML");
            alert.setHeaderText("You must private an XML text or file");
            alert.setContentText("Provide an XML");
            alert.showAndWait();
            return true;
        }
        return false;
    }
}

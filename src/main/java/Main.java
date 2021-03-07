import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        MenuBar menuBar = new MenuBar();

        Menu dateiMenu = new Menu("Datei");
        Menu bearbeitenMenu = new Menu("Bearbeiten");

        MenuItem schliessenItem = new MenuItem("Schließen");
        MenuItem ausschneidenItem = new MenuItem("Ausschneiden");
        MenuItem kopierenItem = new MenuItem("Kopieren");
        MenuItem einfuegenItem = new MenuItem("Einfügen");

        ToolBar toolBar = new ToolBar();
        Button schliessenButton = new Button("Schließen");
        Button ausschneidenButton = new Button("Ausschneiden");
        Button kopierenButton = new Button("Kopieren");
        Button einfuegenButton = new Button("Einfügen");

        TabPane sourcetargetTabPane = new TabPane();
        TextArea loggingTextArea = new TextArea();
        TextArea sourceArea = new TextArea();
        TextArea targetArea = new TextArea();

        // 2c
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();

        KeyCombination closeApplication = new KeyCodeCombination(KeyCode.F4, KeyCombination.CONTROL_DOWN);
        schliessenItem.setAccelerator(closeApplication);
        schliessenItem.setOnAction(e -> {
            Platform.exit();
        });

        KeyCombination copyText = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
        kopierenItem.setAccelerator(copyText);
        kopierenItem.setOnAction(e -> {
            content.putString(sourceArea.getSelectedText().isBlank() ? "" : sourceArea.getSelectedText());
            clipboard.setContent(content);
            loggingTextArea.setText(loggingTextArea.getText() + "* Ausgewählter Text wurde kopiert *\n");
        });

        KeyCombination cutText = new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN);
        ausschneidenItem.setAccelerator(cutText);
        ausschneidenItem.setOnAction(e -> {
            content.putString(sourceArea.getSelectedText().isBlank() ? "" : sourceArea.getSelectedText());
            clipboard.setContent(content);
            String text = sourceArea.getText().toString().replace(sourceArea.getSelectedText(), "");
            sourceArea.setText(text);
            loggingTextArea.setText(loggingTextArea.getText().toString() + "* Ausgewählter Text wurde ausgeschnitten. *\n");
        });

        KeyCombination insertText = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN);
        einfuegenItem.setAccelerator(insertText);
        einfuegenItem.setOnAction(e -> {
            targetArea.setText(targetArea.getText() + clipboard.getString());
            loggingTextArea.setText(loggingTextArea.getText().toString() + "* Ausgewählter Text wurde eingefügt. *\n");
        });

        dateiMenu.getItems().addAll(schliessenItem);
        bearbeitenMenu.getItems().addAll(ausschneidenItem, kopierenItem, einfuegenItem);

        menuBar.getMenus().addAll(dateiMenu, bearbeitenMenu);

        schliessenButton.setOnAction(e ->{
            Platform.exit();
        });

        toolBar.getItems().addAll(schliessenButton,ausschneidenButton, kopierenButton, einfuegenButton);

        ausschneidenButton.setOnAction(e->{
            content.putString(sourceArea.getSelectedText().isBlank() ? "" : sourceArea.getSelectedText());
            clipboard.setContent(content);
            String text = sourceArea.getText().toString().replace(sourceArea.getSelectedText(), "");
            sourceArea.setText(text);
            loggingTextArea.setText(loggingTextArea.getText().toString() + "* Ausgewählter Text wurde ausgeschnitten via Toolbar. *\n");

        });

        kopierenButton.setOnAction(e ->{
            content.putString(sourceArea.getSelectedText().isBlank() ? "" : sourceArea.getSelectedText());
            clipboard.setContent(content);
            loggingTextArea.setText(loggingTextArea.getText().toString() + "* Ausgewählter Text wurde kopiert via Toolbar. *\n");
        });

        einfuegenButton.setOnAction(e -> {
            targetArea.setText(targetArea.getText() + clipboard.getString());
            loggingTextArea.setText(loggingTextArea.getText().toString() + "* Ausgewählter Text wurde eingefügt via Toolbar. *\n");
        });

        Tab sourcetargetTab = new Tab("Source/Target");
        Tab loggingTab = new Tab("Logging", loggingTextArea);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(sourceArea, targetArea);

        Label label = new Label("Drag a file to me.");
        Label dropped = new Label("");

        VBox dragTarget = new VBox();
        dragTarget.getChildren().addAll(label,dropped);

        sourceArea.setOnDragDetected(mouseEvent -> {
            Dragboard db = sourceArea.startDragAndDrop(TransferMode.ANY);
            content.putString(sourceArea.getSelectedText());
            db.setContent(content);
            mouseEvent.consume();
        });

        targetArea.setOnDragOver(dragEvent -> {
            if (dragEvent.getGestureSource() != targetArea && dragEvent.getDragboard().hasString()) {
                dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            dragEvent.consume();
        });

        targetArea.setOnDragDropped(dragEvent -> {
            Dragboard db = dragEvent.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                targetArea.setText(targetArea.getText() + db.getString());
                success = true;
            }
            dragEvent.setDropCompleted(success);
            dragEvent.consume();
            String text = sourceArea.getText().toString().replace(sourceArea.getSelectedText(), "");
            sourceArea.setText(text);
            loggingTextArea.setText(loggingTextArea.getText().toString() + "* Drag-and-Drop war erfolgreich. *\n");
        });

        sourcetargetTab.setContent(vBox);
        sourcetargetTabPane.getTabs().addAll(sourcetargetTab, loggingTab);

        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(toolBar);
        root.setBottom(sourcetargetTabPane);


        primaryStage.setTitle("Menüs, Tastatur, Drag-and-Drop, Zwischenablage");
        primaryStage.setScene(new Scene(root, 400, 500));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

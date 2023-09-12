import java.util.ArrayList;

import echobot.command.AddCommand;
import echobot.command.AddNoteCommand;
import echobot.command.Command;
import echobot.command.DeleteCommand;
import echobot.command.FindCommand;
import echobot.command.ListCommand;
import echobot.command.ListNoteCommand;
import echobot.command.MarkCommand;
import echobot.command.UnmarkCommand;
import echobot.note.Note;
import echobot.storage.Storage;
import echobot.task.Task;
import echobot.ui.Ui;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;


public class EchoBot extends Application {

    private ScrollPane scrollPane;
    private VBox dialogContainer;
    private TextField userInput;
    private Button sendButton;
    private Scene scene;
    private Image user = new Image(this.getClass().getResourceAsStream("/alles.jpg"));
    private Image duke = new Image(this.getClass().getResourceAsStream("/google.jpg"));

    private Ui ui;
    private Storage storage;
    private ArrayList<Task> tasks = new ArrayList<>();
    private ArrayList<Note> notes = new ArrayList<>();


    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage stage) {
        ui = new Ui();
        storage = new Storage("./data/duke.txt", "./data/notes.txt");
        tasks = storage.loadTasks();
        notes = storage.loadNotes();

        // Display the welcome message with Duke image using Platform.runLater()
        Platform.runLater(() -> {
            String welcomeMessage = ui.showWelcomeMessage();
            displayDukeMessage(welcomeMessage);
        });


        //Step 1. Setting up required components
        //The container for the content of the chat to scroll.
        scrollPane = new ScrollPane();
        dialogContainer = new VBox();
        scrollPane.setContent(dialogContainer);

        userInput = new TextField();
        sendButton = new Button("Send");

        AnchorPane mainLayout = new AnchorPane();
        mainLayout.getChildren().addAll(scrollPane, userInput, sendButton);

        scene = new Scene(mainLayout);

        stage.setScene(scene);
        stage.show();

        //Step 2. Formatting the window to look as expected
        stage.setTitle("Duke");
        stage.setResizable(false);
        stage.setMinHeight(600.0);
        stage.setMinWidth(400.0);

        mainLayout.setPrefSize(400.0, 600.0);

        scrollPane.setPrefSize(385, 535);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        scrollPane.setVvalue(1.0);
        scrollPane.setFitToWidth(true);

        dialogContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);

        userInput.setPrefWidth(325.0);

        sendButton.setPrefWidth(55.0);

        AnchorPane.setTopAnchor(scrollPane, 1.0);

        AnchorPane.setBottomAnchor(sendButton, 1.0);
        AnchorPane.setRightAnchor(sendButton, 1.0);

        AnchorPane.setLeftAnchor(userInput , 1.0);
        AnchorPane.setBottomAnchor(userInput, 1.0);

        //Step 3. Add functionality to handle user input.
        sendButton.setOnMouseClicked((event) -> {
            handleUserInput();
        });

        userInput.setOnAction((event) -> {
            handleUserInput();
        });

        //Scroll down to the end every time dialogContainer's height changes.
        dialogContainer.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));

    }


    private void handleUserInput() {
        String input = userInput.getText();
        assert input != null : "User input should not be null";
        String responseText = "";

        if (input.equalsIgnoreCase("bye")) {
            responseText = ui.showByeMessage();

            Duration delay = Duration.seconds(1);
            KeyFrame keyFrame = new KeyFrame(delay, event -> Platform.exit());
            Timeline timeline = new Timeline(keyFrame);
            timeline.play();
        } else if (input.equalsIgnoreCase("list tasks")) {
            ListCommand listCommand = new ListCommand();
            responseText = listCommand.doCommand(tasks, ui, storage, scene, dialogContainer);
        } else if (input.toLowerCase().startsWith("todo")) {
            String taskDescription = Command.extractDesc(input, "todo");
            AddCommand addCommand = new AddCommand(Command.TaskType.TODO, taskDescription, null, null);
            addCommand.doCommand(tasks, ui, storage, scene, dialogContainer);
            responseText = addCommand.getResponse();
        } else if (input.toLowerCase().startsWith("deadline")) {
            String taskDescription = Command.extractDesc(input, "deadline");
            int indexOfBy = taskDescription.indexOf("/by");
            String deadlineDescription = taskDescription.substring(0, indexOfBy).trim();
            String by = taskDescription.substring(indexOfBy + 3).trim();
            AddCommand addCommand = new AddCommand(Command.TaskType.DEADLINE, deadlineDescription, by, null);
            addCommand.doCommand(tasks, ui, storage, scene, dialogContainer);
            responseText = addCommand.getResponse();
        } else if (input.toLowerCase().startsWith("event")) {
            String taskDescription = Command.extractDesc(input, "event");
            int indexOfFrom = taskDescription.indexOf("/from");
            int indexOfTo = taskDescription.indexOf("/to");
            String eventDescription = taskDescription.substring(0, indexOfFrom).trim();
            String from = taskDescription.substring(indexOfFrom + 5, indexOfTo).trim();
            String to = taskDescription.substring(indexOfTo + 3).trim();
            AddCommand addCommand = new AddCommand(Command.TaskType.EVENT, eventDescription, from, to);
            addCommand.doCommand(tasks, ui, storage, scene, dialogContainer);
            responseText = addCommand.getResponse();
        } else if (input.toLowerCase().startsWith("mark")) {
            int taskNum = Command.extractTaskNum(input, "mark");
            MarkCommand markCommand = new MarkCommand(taskNum);
            markCommand.doCommand(tasks, ui, storage, scene, dialogContainer);
            responseText = markCommand.getResponse();
        } else if (input.toLowerCase().startsWith("unmark")) {
            int taskNum = Command.extractTaskNum(input, "unmark");
            UnmarkCommand unmarkCommand = new UnmarkCommand(taskNum);
            unmarkCommand.doCommand(tasks, ui, storage, scene, dialogContainer);
            responseText = unmarkCommand.getResponse();
        } else if (input.toLowerCase().startsWith("delete")) {
            int taskNum = Command.extractTaskNum(input, "delete");
            DeleteCommand deleteCommand = new DeleteCommand(taskNum);
            deleteCommand.doCommand(tasks, ui, storage, scene, dialogContainer);
            responseText = deleteCommand.getResponse();
        } else if (input.toLowerCase().startsWith("find")) {
            String keyword = Command.extractDesc(input, "find");
            FindCommand findCommand = new FindCommand(keyword);
            findCommand.doCommand(tasks, ui, storage, scene, dialogContainer);
            responseText = findCommand.getResponse();
        } else if (input.toLowerCase().startsWith("note")) {
            String description = Command.extractDesc(input, "note");
            String[] noteParts = description.split("::", 2);
            if (noteParts.length < 2) {
                responseText = "Please provide both a title and content for your note.";
            } else {
                String title = noteParts[0].trim();
                String content = noteParts[1].trim();

                AddNoteCommand addNoteCommand = new AddNoteCommand(title, content);
                responseText = addNoteCommand.doCommand(notes, ui, storage, scene, dialogContainer);
            }
        } else if (input.toLowerCase().startsWith("list notes")) {
            ListNoteCommand listNoteCommand = new ListNoteCommand();
            responseText = listNoteCommand.doCommand(notes, ui, storage, scene, dialogContainer);
        } else {
            responseText = "Sorry, I don't understand that command.";
        }

        // Display user input and Duke's response in the default format
        displayUserAndDukeMessages(input, responseText);

        userInput.clear();
    }


    private void displayUserAndDukeMessages(String userMessage, String dukeResponse) {
        Label userTextLabel = new Label(userMessage);
        Label dukeTextLabel = new Label(dukeResponse);

        ImageView userImageView = new ImageView(user);
        ImageView dukeImageView = new ImageView(duke);

        DialogBox userDialogBox = DialogBox.getUserDialog(userTextLabel, userImageView);
        DialogBox dukeDialogBox = DialogBox.getDukeDialog(dukeTextLabel, dukeImageView);

        dialogContainer.getChildren().addAll(userDialogBox, dukeDialogBox);
    }

    private void displayDukeMessage(String dukeResponse) {
        Label dukeTextLabel = new Label(dukeResponse);

        ImageView dukeImageView = new ImageView(duke);

        DialogBox dukeDialogBox = DialogBox.getDukeDialog(dukeTextLabel, dukeImageView);

        dialogContainer.getChildren().addAll(dukeDialogBox);
    }


}


package duke.command;

import duke.storage.Storage;
import duke.ui.Ui;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.Task;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.ArrayList;
public class UnmarkCommand extends Command {
    private int taskNum;

    public UnmarkCommand(int taskNum) {
        this.taskNum = taskNum;
    }

    public void doCommand(ArrayList<Task> tasks, Ui ui, Storage storage) {
        if (taskNum >= 1 && taskNum <= tasks.size()) {
            Task task = tasks.get(taskNum - 1);
            task.unmark();

            Ui.showHorizontalLine();
            System.out.println("    OK, I've marked this task as not done yet:");
            System.out.print("      [" + task.getStatusIcon() + "] " + task.getDescription());

            // Additional information
            if (task instanceof Event) {
                LocalDateTime from = ((Event) task).getFrom();
                LocalDateTime to = ((Event) task).getTo();
                System.out.print(" (from: " + from + " to: " + to + ")");
            } else if (task instanceof Deadline) {
                LocalDate by = ((Deadline) task).getBy();
                System.out.print(" (by: " + by + ")");
            }

            System.out.print("\n");
            Ui.showHorizontalLine();
            storage.saveTasks(tasks); // Save after unmarking
        }
    }
}

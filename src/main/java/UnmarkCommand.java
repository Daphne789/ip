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
                System.out.print(" (from: " + ((Event) task).from + " to: " + ((Event) task).to + ")");
            } else if (task instanceof Deadline) {
                System.out.print(" (by: " + ((Deadline) task).by + ")");
            }

            System.out.print("\n");
            Ui.showHorizontalLine();
            storage.saveTasks(tasks); // Save after unmarking
        }
    }
}

public class Task  {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public String toFileString() {
        String type = "";
        String dateTime = "";

        if (this instanceof Todo) {
            type = "T";
        } else if (this instanceof Deadline) {
            type = "D";
            dateTime = " | " + ((Deadline) this).getBy();
        } else if (this instanceof Event) {
            type = "E";
            dateTime = " | " + ((Event) this).getFrom() + " | " + ((Event) this).getTo();
        }

        return type + " | " + (isDone ? "1" : "0") + " | " + description + dateTime;
    }


    public static Task fromFileString(String fileString) {
        String[] parts = fileString.split(" \\| ");
        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String description = parts[2];

        Task task;

        if (type.equals("T")) {
            task = new Todo(description);
        } else if (type.equals("D")) {
            String by = parts[3];
            task = new Deadline(description, by);
        } else if (type.equals("E")) {
            String from = parts[3];
            String to = parts[4];
            task = new Event(description, from, to);
        } else {
            throw new IllegalArgumentException("Invalid task type: " + type);
        }

        if (isDone) {
            task.mark();
        }

        return task;
    }


    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    public void mark() {
        isDone = true;
    }

    public void unmark() {
        isDone = false;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "]" + " " + getDescription();
    }
}

package echobot.ui;

/**
 * Provides methods related to user interface.
 */
public class Ui {

    /**
     * Displays the echobot.main.EchoBot logo.
     */
    public String showLogo() {
        return " ,d88b.d88b,\n"
                + "88888888888\n"
                + " `Y8888888Y'\n"
                + "    `Y888Y'  \n"
                + "        `Y'    \n";
    }

    /**
     * Generates and returns a welcome message for the user.
     *
     * @return A welcome message string.
     */
    public String showWelcomeMessage() {
        String responseText = "Hello! I'm echobot.main.EchoBot\n";
        responseText += "What can I do for you?\n";
        responseText += showLogo();

        return responseText;
    }

    /**
     * Generates and returns a goodbye message for the user.
     *
     * @return A goodbye message string.
     */
    public String showByeMessage() {
        return "Bye. Hope to see you again soon!";
    }
}

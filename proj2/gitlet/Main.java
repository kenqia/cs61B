package gitlet;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @kenqia
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        if(args.length == 0) System.out.println("Please enter a command.");
        System.exit(0);
        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                // TODO: handle the `init` command
                Commit init = new Commit(new Metadata("1970-01-01 00：00：00" , args[1]) , null , null , null);
                Branch now = Repository.init();
                now.commit(init);
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                break;
            default:
                System.out.println("No command with that name exists.");
                break;

        }
    }
}

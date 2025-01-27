package gitlet;

import javax.imageio.IIOException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static gitlet.Utils.*;

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
        if(args.length == 0){
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                Repository.init();
                try {
                    join(Repository.GITLET_DIR, "HeadBranch").createNewFile();
                    join(Repository.GITLET_DIR, "StageFile").createNewFile();
                }catch (IOException e) {
                    throw new RuntimeException(e);
                }

                writeObject(join(Repository.GITLET_DIR, "HeadBranch") , new Branch("master"));
                writeObject(join(Repository.GITLET_DIR, "StageFile") , new Stage(0));

                if(!(args.length == 1)) {
                    Commit init = new Commit(new Metadata("1970-01-01 00：00：00", args[1]), null, null, null);
                    init.loadingCommit();
                }else{
                    Commit init = new Commit(new Metadata("1970-01-01 00：00：00", null), null, null, null);
                    init.loadingCommit();
                }
                break;
            case "add":
                if(!Repository.GITLET_DIR.exists()) System.exit(0);
                Repository.add(args[1]);
                break;
            case "commit":
                if(!Repository.GITLET_DIR.exists()) System.exit(0);
                Date date = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                Branch nowBranch = readObject(join(Repository.GITLET_DIR , "HeadBranch") , Branch.class );

                Commit wantToCommit = new Commit(new Metadata(formatter.format(date), args[1]), nowBranch.HEAD, null, nowBranch.HEAD.getBlob());

                wantToCommit.checkStage();
                wantToCommit.loadingCommit();
                break;
            default:
                System.out.println("No command with that name exists.");
                break;

        }
    }
}

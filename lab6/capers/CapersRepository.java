package capers;

import java.io.File;
import java.security.cert.CertPath;

import static capers.Utils.*;

/** A repository for Capers 
 * @author TODO
 * The structure of a Capers Repository is as follows:
 *
 * .capers/ -- top level folder for all persistent data in your lab12 folder
 *    - dogs/ -- folder containing all of the persistent data for dogs
 *    - story -- file containing the current story
 *
 * TODO: change the above structure if you do something different.
 */
public class CapersRepository {
    /** Current Working Directory. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /** Main metadata folder. */
    static final File CAPERS_FOLDER = join(CWD , ".capers"); // TODO Hint: look at the `join`

    //      function in Utils
    /**
     * Does required filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     * Remember: recommended structure (you do not have to follow):
     *
     * .capers/ -- top level folder for all persistent data in your lab12 folder
     *    - dogs/ -- folder containing all of the persistent data for dogs
     *    - story -- file containing the current story
     */
    public static void setupPersistence() {
        if (!CAPERS_FOLDER.exists()) {
            CAPERS_FOLDER.mkdir();
            try {
                join(CAPERS_FOLDER, "story").createNewFile();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            join(CAPERS_FOLDER , "dogs").mkdir();
        }
    }

    /**
     * Appends the first non-command argument in args
     * to a file called `story` in the .capers directory.
     * @param text String of the text to be appended to the story
     */
    public static void writeStory(String text) {
        writeContents(join(CAPERS_FOLDER , "story") , readContentsAsString(join(CAPERS_FOLDER , "story")) + text + "\n");
        System.out.print(readContentsAsString(join(CAPERS_FOLDER , "story")));
    }

    /**
     * Creates and persistently saves a dog using the first
     * three non-command arguments of args (name, breed, age).
     * Also prints out the dog's information using toString().
     */
    public static void makeDog(String name, String breed, int age) {
        Dog hobby = new Dog(name , breed , age);
        hobby.saveDog();
        System.out.println(hobby.toString());
    }

    /**
     * Advances a dog's age persistently and prints out a celebratory message.
     * Also prints out the dog's information using toString().
     * Chooses dog to advance based on the first non-command argument of args.
     * @param name String name of the Dog whose birthday we're celebrating.
     */
    public static void celebrateBirthday(String name) {
        if(join(join(CAPERS_FOLDER, "dogs"), name).exists()){
            Dog myFriend = Dog.fromFile(name);
            myFriend.haveBirthday();
            myFriend.saveDog();
        }
        else{
            System.out.println("没这个狗");
        }
    }
}

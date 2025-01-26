package gitlet;

// TODO: any imports you need here

import java.util.Date; // TODO: You'll likely use this in this class

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  kenqia
 */
public class Commit {
    /**
     * TODO: add instance variables here.
     *
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    private Metadata metadata;
    private Commit parent;
    private Commit parentMerge;
    private Blob[] file;
    public Commit(Metadata data , Commit first , Commit second , Blob[] filehere){
     this.metadata = data;
     this.parent = first;
     this.parentMerge = second;
     this.file = filehere;
    }

    /** The message of this Commit. */
    private String message;


    public void loadingCommit(){

    }
}

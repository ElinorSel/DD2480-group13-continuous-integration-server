package lab2;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;

public final class ProjectBuilder {

    public final Repository repo;
    public final File localDir;

    /**
     * repoURL = url of repository we wish to clone
     * branch = branch where change has been made
     */


    public ProjectBuilder(String repoUrl, String branch, String ID) {
        File cloneDirectoryPath = new File("./" + ID);
        if(cloneDirectoryPath.exists()){
            deleteClone(cloneDirectoryPath);
        }
        this.repo = cloneRepo(repoUrl, branch, cloneDirectoryPath);
        if(this.repo == null) {
            throw new RuntimeException("Failed to clone repository from " + repoUrl);
        }
        this.localDir = cloneDirectoryPath;   
    }

    private Repository cloneRepo(String repoUrl, String branch, File cloneDirectoryPath){
        Git git = null;
        try {
            System.out.println("Cloning repository from " + repoUrl + " to " + cloneDirectoryPath);
            git = Git.cloneRepository()
                          .setURI(repoUrl)
                          .setDirectory(cloneDirectoryPath)
                          .setBranch(branch)
                          .call();
            System.out.println("Repository cloned successfully.");
            return git.getRepository();
        } catch(GitAPIException e) {
            e.printStackTrace();
            return null;
        } finally {
            if(git != null) {
                git.close();
            }
        }
        
    }

    /**
     * Method to delete the clone of the repository
     */
    public void deleteClone(File directory) {
        if(directory != null && directory.exists()){
            try{
                FileUtils.deleteDirectory(directory);
                System.out.println("Deleted directory: " + directory);
            } catch(IOException e){
                System.err.println("Failed to delete directory: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

}

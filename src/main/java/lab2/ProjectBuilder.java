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

    /**
     * Method that creates a path and firstly checks if there already exists the same path, which is in that case deleted. 
     * Then it calls on the repo cloner method and also sets the attribute for the class to the cloned repository and its directory path. 
     * Parameters: link to the repository, branch and ID for the path
     */
    public ProjectBuilder(String repoUrl, String branch, String ID) {
        File cloneDirectoryPath = new File("./temp-builds/" + ID);
        if(cloneDirectoryPath.exists()){
            deleteClone(cloneDirectoryPath);
        }
        this.repo = cloneRepo(repoUrl, branch, cloneDirectoryPath);
        if(this.repo == null) {
            throw new RuntimeException("Failed to clone repository from " + repoUrl);
        }
        this.localDir = cloneDirectoryPath;   
    }

    /**
     * Method that clones a repository to the path that was specified in the previous method.
     * If cloning is successful we then try to compile it, and finally return the repo if compiling also was successful. 
     * Parameters: link to the repository, branch and the directory path that was created
     */
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
            if(compileMaven())
                return git.getRepository();
            else
                return null;
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
     * Parameters: The directory path of the repository
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

    /**
     * Method that runs a maven compile command on the cloned repository.
     * Returns true if exit code is 0 (success), false otherwise
     */
    public boolean compileMaven() {
        String[] command;
        String os = System.getProperty("os.name").toLowerCase();
        
        if (os.contains("win")) {
        command = new String[]{"cmd.exe", "/c", "mvn", "compile"};
        } else {
        command = new String[]{"mvn", "compile"};
        }
            try {
                System.out.println("Creating Process...");
                ProcessBuilder pb = new ProcessBuilder(command);
                pb.directory(this.localDir);
                pb.inheritIO();
                Process process = pb.start();
                int exitVal = process.waitFor();
                System.out.println("Maven compile finished with exit code: " + exitVal);
                return exitVal == 0;
            }
            catch (IOException | InterruptedException e) {
                System.err.println("Compilation failed due to internal error: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
    }
}

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
     * Creates a path and firstly checks if there already exists a clone on the path, which is in that case deleted. 
     * Then it calls on the repo cloner method and also sets the attribute for the class to the cloned repository and its directory path. 
     * @param repoUrl link to the repository
     * @param branch branch that we want to clone
     * @param ID ID for path and history
     * @throws RuntimeException if clone operation fails
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
     * Deletes the clone of the repository.
     * @param directory The directory path of the repository
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
     * Runs a maven compile command on the cloned repository.
     * @return true if exit code is 0 (success), false otherwise
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

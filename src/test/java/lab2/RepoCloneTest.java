package lab2;

import java.io.File;

import org.eclipse.jgit.lib.Repository;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;


public class RepoCloneTest {
    /**
     * Contract: The function must return true if the cloned branch is the same as the input branch.
     * Input: Repostory url, branch, and ID
     * Output: True
     */
    @Test void cloneRepoTest(){
        ProjectBuilder repoBuilder = new ProjectBuilder("https://github.com/helinsaeid/TestRepo", "refs/heads/testBranch", "cloneTest");
        Repository newRepo = repoBuilder.repo;
        try{
            assertThat(newRepo.getBranch(), equalTo("testBranch"));
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            repoBuilder.deleteClone(newRepo.getDirectory());
        }
    }

    /**
     * Contract: The function must return true if the cloned branch has an invalid URL.
     * Input: Repostory url, branch, and ID
     * Output: True
     */
    @Test void cloneInvalidUrlTest(){
        try{
            new ProjectBuilder("https://github.com/helinsaeid/TestRepoINVALID", "refs/heads/testBranch", "cloneURLTest");
            fail("Should have thrown RuntimeException");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("https://github.com/helinsaeid/TestRepoINVALID"), "Exception message should mention the URL");
        }
    }

    /**
     * Contract: The directory for a deleted clone should not exist
     * Input: Repostory url, branch, and ID
     * Output: True and false
     */
    @Test void deleteCloneTest(){
        ProjectBuilder testRepo =  new ProjectBuilder("https://github.com/helinsaeid/TestRepo", "refs/heads/testBranch", "cloneDeleteTest");
        File testDir = new File("./temp-builds/" + "cloneDeleteTest");
        assertTrue(testDir.exists(), "Directory should exists");
        testRepo.deleteClone(testDir);
        assertFalse(testDir.exists(), "Directory should not exists after deletion");
    }

    @Test void compileMavenTest(){
        ProjectBuilder testRepo =  new ProjectBuilder("https://github.com/helinsaeid/TestRepo", "refs/heads/testBranch", "cloneCompilationTest");
        assertNotNull(testRepo.repo, "Repository should not be null if compilation succeeded");
        testRepo.deleteClone(testRepo.localDir);
    }

}

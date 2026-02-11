package lab2;

import org.eclipse.jgit.lib.Repository;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
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
}

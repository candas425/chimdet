package ro.utcn.backend.backendservices;

import org.junit.Before;
import org.junit.Test;
import ro.utcn.BaseTest;
import ro.utcn.backend.repositories.exceptions.PersistanceException;
import ro.utcn.backend.model.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * UserService testing
 * <p>
 * Created by Lucas on 7/5/2017.
 */
public class UserServiceTest extends BaseTest {

    @Before
    public void setUp() throws PersistanceException {
        User user = createUser();
        userService.saveUser(user);
    }


    @Test
    public void saveUser() throws Exception {
        assertEquals(1, userService.getAll().size());

        User user = createUser("DAA", "DAA");
        userService.saveUser(user);

        assertEquals(2, userService.getAll().size());
    }


    @Test
    public void getAll() throws Exception {
        assertEquals(1, userService.getAll().size());
    }

    @Test
    public void getUserWithUsernameAndPassword() throws Exception {
        assertNotNull(userService.getUserWithUsernameAndPassword(USERNAME, PASSWORD));
    }

    @Test
    public void update() throws Exception {
        assertEquals(1, userService.getAll().size());
        User user = userService.getAll().get(0);

        user.setUsername("aaaa");
        userService.updateUser(user);

        assertEquals("aaaa", userService.getAll().get(0).getUsername());
    }
}

package ro.utcn.backend.backendservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.utcn.backend.repositories.UserRepository;
import ro.utcn.backend.repositories.exceptions.PersistanceException;
import ro.utcn.backend.model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * User service
 *
 * Created by Lucian on 5/11/2017.
 */

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void saveUser(User user) throws PersistanceException {
        user.setParola(encryptedPassword(user.getParola()));
        userRepository.save(user);
    }

    public void updateUser(User user) throws PersistanceException {
        user.setParola(encryptedPassword(user.getParola()));
        userRepository.save(user);
    }

    public User getUserWithUsernameAndPassword(String username, String password) throws PersistanceException {
        return userRepository.findByUsernameAndParola(username, encryptedPassword(password));
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    /**
     * Thid method is used to encrypt a password, it uses md5
     *
     * @param password the password which will be encrypted
     * @return an encrypted password
     */
    private static String encryptedPassword(String password) throws PersistanceException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] passBytes = password.getBytes();
            byte[] digested = md.digest(passBytes);
            StringBuilder sb = new StringBuilder();
            for (byte aDigested : digested) {
                //0xff represents 255 in decimal or 1111 1111 in hexa, a byte represent 8bits, so we make the AND operation between this two values
                sb.append(Integer.toHexString(0xff & aDigested));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new PersistanceException(PersistanceException.ENCRYPTED_EXCEPTION);
        }
    }
}

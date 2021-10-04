package ro.utcn.backend.repositories.exceptions;

/**
 * This class is used to throw exceptions for DAO package
 *
 * @author lucian.davidescu
 */
public class PersistanceException extends Exception {

    public static final String OBJECT_EXIST = "Obiectul exista";
    public static final String CODE_EXIST = "Codul identificator exista";
    public static final String OBJECT_NULL = "Object is null";
    public static final String ENCRYPTED_EXCEPTION = "NoSuchAlgorithmException";

    public PersistanceException(String message) {
        super(message);
    }
}
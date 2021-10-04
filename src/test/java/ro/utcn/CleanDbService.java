/*
 * Copyright 2016 Codemart, Junidas, Codebox
 * 
 * All Rights Reserved.
 */
package ro.utcn;

import javax.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * Db Service
 */

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
@Service
public class CleanDbService {

    @PersistenceContext
    private EntityManager em;

    public void cleanDb() {
        Query query4 = em.createNativeQuery("DELETE FROM lista_produse_comanda");
        query4.executeUpdate();
        Query query8 = em.createNativeQuery("DELETE FROM pret");
        query8.executeUpdate();
        Query query7 = em.createNativeQuery("DELETE FROM holiday");
        query7.executeUpdate();
        Query query1 = em.createNativeQuery("DELETE FROM angajat");
        query1.executeUpdate();
        Query query3 = em.createNativeQuery("DELETE FROM comanda");
        query3.executeUpdate();
        Query query6 = em.createNativeQuery("DELETE FROM firma");
        query6.executeUpdate();
        Query query9 = em.createNativeQuery("DELETE FROM produs");
        query9.executeUpdate();
        Query query10 = em.createNativeQuery("DELETE FROM setaregenerala");
        query10.executeUpdate();
        Query query11 = em.createNativeQuery("DELETE FROM user");
        query11.executeUpdate();
    }
}

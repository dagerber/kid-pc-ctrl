/**
 * Filename: Repository.java
 * Copyright(c) ISC-EJPD - Alle Rechte vorbehalten
 *
 * Letzter Commit
 *   Datum   : $LastChangedDate$
 *   Benutzer: $Author$
 *   Version : $Revision$ 
 */
package org.xcare.kidpcctrl;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains the Business code and accesses the database.
 */
public class Repository {

    private static final Logger logger = LoggerFactory.getLogger(Repository.class);

    public static final int DEFAULT_DAILY_CREDIT_MINUTES = 20;

    private EntityManager em() {
        return EntityManagerFactoryHelper.getFactory().createEntityManager();
    }

    public KompiAccess getInfo(String username) {
        EntityManager em = em();

        try {

            em.getTransaction().begin();

            TypedQuery<KompiAccess> query = em.createNamedQuery(KompiAccess.FIND_BY_USER, KompiAccess.class);
            query.setParameter("username", username);
            KompiAccess access = null;

            try {
                access = query.getSingleResult();

            } catch (NoResultException e) {
                // nothing todo
            }
            em.getTransaction().commit();
            return access;

        } catch (RuntimeException ex) {
            logger.error("An unexpected Runtime Exception occured, Transaction will be rolled back: ", ex);
            em.getTransaction().rollback();
            throw ex;
        }
    }

    public List<KompiAccess> getInfos() {
        EntityManager em = em();

        try {

            em.getTransaction().begin();
            TypedQuery<KompiAccess> query = em.createNamedQuery(KompiAccess.FIND_ALL, KompiAccess.class);
            List<KompiAccess> accessList = null;
            accessList = query.getResultList();
            em.getTransaction().commit();
            return accessList;

        } catch (RuntimeException ex) {
            logger.error("An unexpected Runtime Exception occured, Transaction will be rolled back: ", ex);
            em.getTransaction().rollback();
            throw ex;
        }
    }

    /**
     * For special Tasks (School, ...) we do not track the time
     *
     * @param minutes The minutes from now on.
     */
    public void doNotTrackForNextMinutes(String username, int minutes) {

        DateTime doNotTrackUntil = new DateTime().plusMinutes(minutes);

        EntityManager em = em();
        try {

            em.getTransaction().begin();

            TypedQuery<KompiAccess> query = em.createNamedQuery(KompiAccess.FIND_BY_USER, KompiAccess.class);
            query.setParameter("username", username);
            KompiAccess access = null;

            try {
                access = query.getSingleResult();
                access.setDoNotTrackUntil(doNotTrackUntil.toDate());
                em.merge(access);

            } catch (NoResultException e) {
                // nothing todo
            }
            em.getTransaction().commit();

        } catch (RuntimeException ex) {
            logger.error("An unexpected Runtime Exception occured, Transaction will be rolled back: ", ex);
            em.getTransaction().rollback();
            throw ex;
        }
    }

    /**
     * Add some extra credit, can have a negative value if you want to subract...
     *
     * @param username The user.
     * @param minutes The minutes extra credit (can be a negative number).
     */
    public void extraCredit(String username, int minutes) {
        logger.info("User {}: Extra credit: {} minutes", username, minutes);
        EntityManager em = em();

        try {

            em.getTransaction().begin();

            TypedQuery<KompiAccess> query = em.createNamedQuery(KompiAccess.FIND_BY_USER, KompiAccess.class);
            query.setParameter("username", username);
            KompiAccess access = null;

            try {
                access = query.getSingleResult();
                int newValue = access.getCreditBalanceInSeconds() + minutes * 60;
                logger.info("New value to set: {}", newValue);
                access.setCreditBalanceInSeconds(newValue);
                em.merge(access);

            } catch (NoResultException e) {
                // nothing todo
            }
            em.getTransaction().commit();

        } catch (RuntimeException ex) {
            logger.error("An unexpected Runtime Exception occured, Transaction will be rolled back: ", ex);
            em.getTransaction().rollback();
            throw ex;
        }
    }

    public void reset(String username) {
        EntityManager em = em();

        try {

            em.getTransaction().begin();

            TypedQuery<KompiAccess> query = em.createNamedQuery(KompiAccess.FIND_BY_USER, KompiAccess.class);
            query.setParameter("username", username);
            KompiAccess access = null;

            try {
                access = query.getSingleResult();
                access.setCreditBalanceInSeconds(0);
                access.setDoNotTrackUntil(null);
                em.merge(access);

            } catch (NoResultException e) {
                // nothing todo
            }
            em.getTransaction().commit();

        } catch (RuntimeException ex) {
            logger.error("An unexpected Runtime Exception occured, Transaction will be rolled back: ", ex);
            em.getTransaction().rollback();
            throw ex;
        }
    }

    /**
     * This methode will be triggered every 1 minutes and update the user online time (plus gives a new credit if appropriate).
     *
     * @param username The user to update.
     */
    public void update(String username) {

        DateTime now = new DateTime();

        EntityManager em = em();

        try {

            em.getTransaction().begin();

            TypedQuery<KompiAccess> query = em.createNamedQuery(KompiAccess.FIND_BY_USER, KompiAccess.class);
            query.setParameter("username", username);
            KompiAccess access = null;

            try {
                access = query.getSingleResult();

                // new Credit if last credit more than a day behind
                Date dailyCreditGrantedTimestamp = access.getDailyCreditGrantedTimestamp();
                Duration duration = new Duration(new DateTime(dailyCreditGrantedTimestamp), now);
                int durationDays = (int) duration.getStandardSeconds() / 3600 / 24;
                if (durationDays > 0) {
                    access.setCreditBalanceInSeconds(access.getCreditBalanceInSeconds() + access.getDailyCredit() * 60 * durationDays);
                    access.setDailyCreditGrantedTimestamp(now.toDate());
                }

                // For special Tasks (School, ...) we do not track the time
                DateTime doNotTrackUntil = null;
                if (access.getDoNotTrackUntil() != null) {
                    doNotTrackUntil = new DateTime(access.getDoNotTrackUntil());
                }
                if (doNotTrackUntil == null || doNotTrackUntil.isBefore(now)) {
                    // A gap a 5 minutes is regarded as a break (the cron should trigger every 1 minutes)
                    DateTime lastAccess = new DateTime(access.getLastAccess());
                    if (lastAccess.plusMinutes(5).isAfter(now)) {
                        duration = new Duration(lastAccess, now);
                        int seconds = (int) duration.getStandardSeconds();
                        logger.debug("Duration: {}s", seconds);
                        int oldValue = access.getCreditBalanceInSeconds();
                        int newValue = oldValue - seconds;
                        logger.debug("Updating from old {} to new {}", oldValue, newValue);
                        access.setCreditBalanceInSeconds(newValue);
                    }
                }

                access.setLastAccess(now.toDate());

                em.merge(access);

            } catch (NoResultException e) {

                logger.warn("No entry for {}, creating default entry", username);

                access = new KompiAccess();
                access.setUsername(username);
                access.setDailyCredit(DEFAULT_DAILY_CREDIT_MINUTES);

                access.setCreditBalanceInSeconds(DEFAULT_DAILY_CREDIT_MINUTES * 60);
                // Credit: 0:00:00h
                access.setDailyCreditGrantedTimestamp(new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 0, 0, 0, 0).toDate());

                access.setLastAccess(now.toDate());
                em.persist(access);
            }

            em.getTransaction().commit();

        } catch (RuntimeException ex) {
            logger.error("An unexpected Runtime Exception occured, Transaction will be rolled back: ", ex);
            em.getTransaction().rollback();
            throw ex;
        }

    }
}

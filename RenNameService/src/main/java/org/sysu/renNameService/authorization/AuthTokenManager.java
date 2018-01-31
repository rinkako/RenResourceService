/*
 * Project Ren @ 2018
 * Rinkako, Ariana, Gordan. SYSU SDCS.
 */
package org.sysu.renNameService.authorization;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.sysu.renNameService.GlobalContext;
import org.sysu.renNameService.entity.RenAuthEntity;
import org.sysu.renNameService.entity.RenSessionEntity;
import org.sysu.renNameService.utility.EncryptUtil;
import org.sysu.renNameService.utility.HibernateUtil;
import org.sysu.renNameService.utility.LogUtil;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

/**
 * Author: Rinkako
 * Date  : 2018/1/28
 * Usage : This class maintaining authorization of service request token.
 */
public class AuthTokenManager {
    /**
     * Request for a auth token by authorization user info.
     * @param username user unique id
     * @param password password
     * @return a token if authorization success, otherwise a string start with `#` for failure reason
     */
    @SuppressWarnings("unchecked")
    public static String Auth(String username, String password) {
        Session session = HibernateUtil.GetLocalThreadSession();
        Transaction transaction = session.beginTransaction();
        try {
            // verify username and password
            String encryptedPassword = EncryptUtil.EncryptSHA256(password);
            RenAuthEntity rae = session.get(RenAuthEntity.class, username);
            if (rae == null || rae.getState() != 0) {
                transaction.commit();
                return "#user_not_valid";
            }
            else if (!rae.getPassword().equals(encryptedPassword)) {
                transaction.commit();
                return "#password_invalid";
            }
            // check if active session exist, ban it
            List<RenSessionEntity> oldRseList = session.createQuery(String.format("FROM RenSessionEntity WHERE username = '%s' AND destroy_timestamp = NULL", username)).list();
            Timestamp currentTS = new Timestamp(System.currentTimeMillis());
            for (RenSessionEntity rse : oldRseList) {
                if (rse.getUntilTimestamp().after(currentTS)) {
                    rse.setDestroyTimestamp(currentTS);
                }
            }
            // create new session
            String tokenId = String.format("AUTH_%s_%s", username, UUID.randomUUID());
            RenSessionEntity rse = new RenSessionEntity();
            long createTs = System.currentTimeMillis();
            rse.setLevel(rae.getLevel());
            rse.setToken(tokenId);
            rse.setUsername(username);
            rse.setCreateTimestamp(new Timestamp(createTs));
            if (GlobalContext.AUTHORITY_TOKEN_VALID_SECOND != 0) {
                rse.setUntilTimestamp(new Timestamp(createTs + 1000 * GlobalContext.AUTHORITY_TOKEN_VALID_SECOND));
            }
            session.save(rse);
            transaction.commit();
            return tokenId;
        }
        catch (Exception ex) {
            LogUtil.Log(String.format("Request for auth but exception occurred (%s), service rollback, %s", username, ex),
                    AuthTokenManager.class.getName(), LogUtil.LogLevelType.ERROR, "");
            transaction.rollback();
            return "#exception_occurred";
        }
    }

    /**
     * Destroy a token.
     * @param token auth token
     */
    public static void Destroy(String token) {
        Session session = HibernateUtil.GetLocalThreadSession();
        Transaction transaction = session.beginTransaction();
        try {
            RenSessionEntity rse = session.get(RenSessionEntity.class, token);
            if (rse == null) {
                return;
            }
            rse.setDestroyTimestamp(new Timestamp(System.currentTimeMillis()));
            transaction.commit();
        }
        catch (Exception ex) {
            LogUtil.Log(String.format("Destroy auth but exception occurred (%s), service rollback, %s", token, ex),
                    AuthTokenManager.class.getName(), LogUtil.LogLevelType.ERROR, "");
            transaction.rollback();
        }
    }

    /**
     * Check if a token is valid.
     * @param token auth token to be checked
     * @return whether token is valid
     */
    public static boolean CheckValid(String token) {
        Session session = HibernateUtil.GetLocalThreadSession();
        Transaction transaction = session.beginTransaction();
        boolean retFlag = true;
        try {
            RenSessionEntity rse = session.get(RenSessionEntity.class, token);
            if (rse == null || rse.getDestroyTimestamp() != null ||
                rse.getUntilTimestamp().before(new Timestamp(System.currentTimeMillis()))) {
                retFlag = false;
            }
            transaction.commit();
        }
        catch (Exception ex) {
            LogUtil.Log(String.format("Check auth validation but exception occurred (%s), service rollback, %s", token, ex),
                    AuthTokenManager.class.getName(), LogUtil.LogLevelType.ERROR, "");
            transaction.rollback();
            retFlag = false;
        }
        return retFlag;
    }

    /**
     * Check if a token is valid and get level.
     * @param token auth token to be checked
     * @return token level, -1 if token is invalid
     */
    public static int CheckValidLevel(String token) {
        Session session = HibernateUtil.GetLocalThreadSession();
        Transaction transaction = session.beginTransaction();
        int retVal;
        try {
            RenSessionEntity rse = session.get(RenSessionEntity.class, token);
            if (rse == null || rse.getDestroyTimestamp() != null ||
                    rse.getUntilTimestamp().before(new Timestamp(System.currentTimeMillis()))) {
                retVal = -1;
            }
            else {
                retVal = rse.getLevel();
            }
            transaction.commit();
        }
        catch (Exception ex) {
            LogUtil.Log(String.format("Check auth validation but exception occurred (%s), service rollback, %s", token, ex),
                    AuthTokenManager.class.getName(), LogUtil.LogLevelType.ERROR, "");
            transaction.rollback();
            retVal = -1;
        }
        return retVal;
    }
}
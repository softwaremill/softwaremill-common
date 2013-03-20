package com.softwaremill.common.faces.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

/**
 * 1 transaction for GET: (before RESTORE_VIEW, after RENDER_RESPONSE)
 * 2 transactions for POST: (before RESTORE_VIEW, after INVOKE_APPLICATION), (before RENDER_RESPONSE, after RENDER_RESPONSE)
 * @author Adam Warski (adam at warski dot org)
 */
public class TransactionPhaseListener implements PhaseListener {
    private static final Logger log = LoggerFactory.getLogger(TransactionPhaseListener.class);

    public void beforePhase(PhaseEvent event) {
        // Always starting before RESTORE_VIEW
        if (PhaseId.RESTORE_VIEW.equals(event.getPhaseId())) {
            log.debug("Start transaction before RESTORE_VIEW");
            startTransaction(event.getFacesContext());
        }

        // Starting before RENDER_RESPONSE in case this is a postback
        if (PhaseId.RENDER_RESPONSE.equals(event.getPhaseId()) && event.getFacesContext().isPostback()) {
            log.debug("Start transaction before RENDER_RESPONSE, during a postback");
            startTransaction(event.getFacesContext());
        }
    }

    public void afterPhase(PhaseEvent event) {
        // Commiting if the response is complete
        if (event.getFacesContext().getResponseComplete()) {
            log.debug("Commit transaction as response is complete");
            commitTransaction(event.getFacesContext());
        }

        // Always commiting after RENDER_RESPONSE
        if (PhaseId.RENDER_RESPONSE.equals(event.getPhaseId())) {
            log.debug("Commit transaction after RENDER_RESPONSE");
            commitTransaction(event.getFacesContext());
        }

        // Commiting after INVOKE_APPLICATION in case of a postback
        if (PhaseId.INVOKE_APPLICATION.equals(event.getPhaseId()) && event.getFacesContext().isPostback()) {
            log.debug("Commit transaction after INVOKE_APPLICATION, during a postback");
            commitTransaction(event.getFacesContext());
        }
    }

    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

    private UserTransaction getUserTransaction() throws NamingException {
        return (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
    }

    private static final String STARTED_TX_KEY = "_started_tx_";

    private void startTransaction(FacesContext facesContext) {
        try {
            UserTransaction utx = getUserTransaction();

            if (utx.getStatus() != Status.STATUS_ACTIVE) {
                utx.begin();
                facesContext.getAttributes().put(STARTED_TX_KEY, new Object());
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new FacesException(e);
        }
    }

    private void commitTransaction(FacesContext facesContext) {
        try {
            UserTransaction utx = getUserTransaction();

            if (facesContext.getAttributes().containsKey(STARTED_TX_KEY)) {
                if (utx.getStatus() == Status.STATUS_ACTIVE) {
                    utx.commit();
                } else {
                    if (utx.getStatus() != Status.STATUS_ROLLEDBACK) {
                        utx.rollback();
                    }
                }
                
                facesContext.getAttributes().remove(STARTED_TX_KEY);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new FacesException(e);
        }
    }
}

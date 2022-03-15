package com.webank.wedatasphere.dss.standard.app.development.ref;

import java.util.Map;

/**
 * This class is used to represent a one-to-one relationship about DSS job with third appConn refJob.
 * When some kinds of DevelopmentOperation are called, they usually need these refJobContent which can
 * help to find the only refJob in third-appConn to operate the third-appConn.
 * The refJobContent is returned from the DevelopmentOperation of RefCreationOperation, RefImportOperation,
 * DSS will persistence these returned refJobContents, and set in when call some other DevelopmentOperations,
 * such as RefDeletionOperation, RefUpdateOperation.
 *
 * @author enjoyyin
 * @date 2022-03-09
 * @since 0.5.0
 */
public interface RefJobContentRequestRef<R extends RefJobContentRequestRef<R>>
        extends DevelopmentRequestRef<R> {

    /**
     * When some DevelopmentOperation is called, the refJobContent will be set in, so we can use it to
     * operate the relative third appConn refJob.
     * This method is usually called by DSS framework, the third-part AppConn just need to use getRefJobContent to
     * get the refJobContent.
     * @param refJobContent a refJobContent related a only third appConn refJob
     * @return this class
     */
    default R setRefJobContent(Map<String, Object> refJobContent) {
        setParameter("refJobContent", refJobContent);
        return (R) this;
    }

    /**
     * This is the content of the third appConn returned, when the RefCreationOperation is called.
     * the third-part AppConn usually will call this method to get refJobContent to do some operation.
     * @return a refJobContent related a only third appConn refJob
     */
    default Map<String, Object> getRefJobContent() {
        return (Map<String, Object>) getParameter("refJobContent");
    }

}

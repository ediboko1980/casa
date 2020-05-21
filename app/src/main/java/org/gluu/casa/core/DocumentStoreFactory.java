package org.gluu.casa.core;

import org.gluu.service.document.store.conf.*;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * @author Yuriy Movchan
 */
@ApplicationScoped
public class DocumentStoreFactory {

    @Inject
    private Logger logger;

    @Inject
    private PersistenceService persistenceService;

    @Produces @ApplicationScoped
   	public DocumentStoreConfiguration getDocumentStoreConfiguration() {
   		//There is no actual use of Document Store in Casa, return a simple configuration to satisfy Weld requirement
   		/*
    	DocumentStoreConfiguration documentStoreConfiguration = persistenceService.getDocumentStoreConfiguration();
   		if ((documentStoreConfiguration == null) || (documentStoreConfiguration.getDocumentStoreType() == null)) {
   			logger.error("Failed to read document store configuration from DB. Please check configuration oxDocumentStoreConfiguration attribute " +
   					"that must contain document store configuration JSON represented by DocumentStoreConfiguration.class");
   			logger.info("Creating fallback LOCAL document store configuration ... ");

   			documentStoreConfiguration = new DocumentStoreConfiguration();
   			documentStoreConfiguration.setLocalConfiguration(new LocalDocumentStoreConfiguration());

   			logger.info("LOCAL document store configuration is created.");
		}

   		logger.info("Document store configuration: " + documentStoreConfiguration);
   		*/
   		documentStoreConfiguration = new DocumentStoreConfiguration();
   		documentStoreConfiguration.setLocalConfiguration(new LocalDocumentStoreConfiguration());
   		return documentStoreConfiguration;
   	}

}

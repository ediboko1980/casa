package org.gluu.casa.core;

import org.gluu.casa.conf.MainSettings;
import org.gluu.casa.misc.Utils;
import org.gluu.casa.service.IBrandingManager;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Created by jgomer on 2018-09-24.
 */
@Named
@ApplicationScoped
public class AssetsService implements IBrandingManager {

    public static final String EMPTY_SNIPPET = "/**/";

    private static final String DEFAULT_LOGO_URL = "/images/logo.png";
    private static final String DEFAULT_FAVICON_URL = "/images/favicon.ico";
    private static final String DEFAULT_CUSTOM_PATH = "/custom";

    @Inject
    private Logger logger;

    @Inject
    private ConfigurationHandler confHandler;

    @Inject
    private MainSettings mainSettings;

    private String logoUrl;

    private String faviconUrl;

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getFaviconUrl() {
        return faviconUrl;
    }

    public String getPrefix() {
        return mainSettings.isUseExternalBranding() ? DEFAULT_CUSTOM_PATH : "";
    }

    public String getExtraCss() {
        return mainSettings.getExtraCssSnippet();
    }

    public void useExternalAssets() throws Exception {

        if (!mainSettings.isUseExternalBranding()) {
            try {
                logger.info("Changing to use external assets directory");
                mainSettings.setUseExternalBranding(true);
                mainSettings.setExtraCssSnippet(null);
                confHandler.saveSettings();
                reloadUrls();
            } catch (Exception e) {
                mainSettings.setUseExternalBranding(false);
                throw e;
            }
        }

    }

    public void useExtraCss(String css) throws Exception {

        String snip = mainSettings.getExtraCssSnippet();
        try {
            logger.info("Changing extra CSS code snippet");
            mainSettings.setUseExternalBranding(false);
            mainSettings.setExtraCssSnippet(css);
            confHandler.saveSettings();
            reloadUrls();
        } catch (Exception e) {
            mainSettings.setExtraCssSnippet(snip);
            throw e;
        }

    }

    public void setLogoContent(byte[] blob) throws Exception {
        logger.info("Setting newer logo content");
        storeAsset(getCustomPathForLogo(), blob);
    }

    public void setFaviconContent(byte[] blob) throws Exception {
        logger.info("Setting newer favicon content");
        storeAsset(getCustomPathForFavicon(), blob);
    }

    public void factoryReset() throws Exception {

        boolean external = mainSettings.isUseExternalBranding();
        String snip = mainSettings.getExtraCssSnippet();
        logger.info("Resetting to default Gluu theme");
        try {
            mainSettings.setUseExternalBranding(false);
            mainSettings.setExtraCssSnippet(null);
            confHandler.saveSettings();
            reloadUrls();

            if (Utils.isNotEmpty(snip)) {
                //Attempt to delete files
                Stream.of(getCustomPathForLogo(), getCustomPathForFavicon()).forEach(p -> {
                    try {
                        Files.deleteIfExists(p);
                    } catch (Exception e){
                        //No need to log here
                    }
                });
            }
        } catch (Exception e) {
            mainSettings.setUseExternalBranding(external);
            mainSettings.setExtraCssSnippet(snip);
            throw e;
        }

    }

    @PostConstruct
    private void init() {
        reloadUrls();
    }

    private void reloadUrls() {

        logoUrl = DEFAULT_LOGO_URL;
        faviconUrl = DEFAULT_FAVICON_URL;
        String customLogoUrl = DEFAULT_CUSTOM_PATH + DEFAULT_LOGO_URL;
        String customFaviconUrl = DEFAULT_CUSTOM_PATH + DEFAULT_FAVICON_URL;

        if (Utils.isNotEmpty(mainSettings.getExtraCssSnippet())) {
            if (Files.isRegularFile(getCustomPathForLogo())) {
                logoUrl = customLogoUrl;
            }
            if (Files.isRegularFile(getCustomPathForFavicon())) {
                faviconUrl = customFaviconUrl;
            }
        } else if (mainSettings.isUseExternalBranding()) {
            logoUrl = customLogoUrl;
            faviconUrl = customFaviconUrl;
        }

    }

    private Path getCustomPathForLogo() {
        return Paths.get(CUSTOM_FILEPATH, DEFAULT_LOGO_URL.split("/"));
    }

    private Path getCustomPathForFavicon() {
        return Paths.get(CUSTOM_FILEPATH, DEFAULT_FAVICON_URL.split("/"));
    }

    private void updateEmptyCssSnippet() throws Exception {

        if (Utils.isEmpty(mainSettings.getExtraCssSnippet())) {
            useExtraCss(EMPTY_SNIPPET);
        }

    }

    private void storeAsset(Path destination, byte[] data) throws Exception {
        logger.info("Saving file {}", destination.toString());
        Files.createDirectories(destination.getParent());
        Files.write(destination, data);
    }

}

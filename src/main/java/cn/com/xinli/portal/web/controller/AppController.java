package cn.com.xinli.portal.web.controller;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.RemoteException;
import cn.com.xinli.portal.core.ServerException;
import cn.com.xinli.portal.core.configuration.AppConfiguration;
import cn.com.xinli.portal.core.configuration.ServerConfigurationService;
import cn.com.xinli.portal.web.rest.AdminResponseBuilders;
import cn.com.xinli.portal.web.rest.RestResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.stream.Stream;

/**
 * Download controller.
 *
 * @author zhoupeng, created on 2016/4/6.
 */
@Controller
public class AppController {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(AppController.class);

    @Autowired
    private ResourceLoader resourceLoader;

    private final String[] supportedOs = {
            "ios", "android", "mac", "linux", "windows"
    };

    @Autowired
    private ServerConfigurationService serverConfigurationService;

    /**
     * Perform download.
     * @param path file path.
     * @param response response.
     * @return resource.
     */
    private Resource performDownload(String path, HttpServletResponse response) throws UnsupportedEncodingException {
        final String filename;

        int index = path.lastIndexOf('/');
        if (index != -1) {
            filename = path.substring(index + 1);
        } else {
            filename = path;
        }

        response.setHeader("Content-Disposition", "attachment; filename=\"" + UriUtils.encode(filename, "UTF-8") + "\"");

        return resourceLoader.getResource("file:./" + path);
    }

    /**
     * Get app path for os.
     * @param os operating system name.
     * @return path.
     */
    private String getAppPath(String os) {
        AppConfiguration appConfiguration =
                serverConfigurationService.getServerConfiguration().getAppConfiguration();
        String path = null;

        switch (os.toLowerCase()) {
            case "android":
                path = appConfiguration.getAndroidAppFileName();
                break;

            case "ios":
                path = appConfiguration.getiOSAppFileName();
                break;

            default:
                break;
        }

        return path;
    }

    /**
     * Download app file.
     *
     * <p>Execute this function will result in two results.
     * <ul>
     *     <li>Start downloading</li>
     *     Server will produces {@link MediaType#APPLICATION_OCTET_STREAM_VALUE}, and client
     *     (browsers or other agents) will start downloading file.
     *
     *     <li>Error result JSON</li>
     *     If app is not available, may not be uploaded yet.
     *     Server will produces a JSON error response by controller advisor.
     * </ul>
     * @param os operation system name.
     * @param response response.
     * @return resource.
     * @throws IOException
     * @throws ServerException
     */
    @RequestMapping(value = "/apps/{os}", method = RequestMethod.GET)
    @ResponseBody
    public Object download(@PathVariable("os") String os,
                             HttpServletResponse response)
            throws IOException, ServerException, RemoteException {

        final String path = getAppPath(os);
        if (!StringUtils.isEmpty(path)) {
            return performDownload(path, response);
        }

        throw new ServerException(PortalError.APP_NOT_AVAILABLE);
    }

    /**
     * Upload app file.
     * @param os target operation system name.
     * @param filename upload file name.
     * @param file upload file.
     * @return rest response.
     * @throws RemoteException
     * @throws ServerException
     */
    @RequestMapping(value = "/portal/admin/v1.0/apps/{os}", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public RestResponse upload(@PathVariable("os") String os,
                               @RequestParam("filename") String filename,
                               @RequestParam("file") MultipartFile file)
            throws RemoteException, ServerException {
        Stream.of(supportedOs).filter(os::equals).findAny().orElseThrow(
                () -> new RemoteException(PortalError.INVALID_OS)
        );

        if (StringUtils.isEmpty(filename) || filename.contains("/")) {
            throw new RemoteException(PortalError.INVALID_APP_FILENAME);
        }

        if (file.isEmpty()) {
            throw new RemoteException(PortalError.EMPTY_APP_FILE);
        }

        final String filepath = "apps/" + filename;

        try {
            BufferedOutputStream stream =
                    new BufferedOutputStream(new FileOutputStream(new File(filepath)));
            FileCopyUtils.copy(file.getInputStream(), stream);

            final String key = "app.download." + os,
                    value = "apps/" + filename;

            serverConfigurationService.updateConfigurationEntry(key, value);
        } catch (IOException e) {
            logger.error("Upload app exception: {}", e.getMessage());
            throw new ServerException(PortalError.SERVER_INTERNAL_ERROR, "upload error", e);
        }

        return AdminResponseBuilders.appResponseBuilder()
                .setOs(os)
                .setFilepath(filepath)
                .build();
    }

    /**
     * Delete existed app for os.
     * @param os operating system name.
     * @return rest response.
     * @throws RemoteException
     * @throws ServerException
     */
    @RequestMapping(value = "/portal/admin/v1.0/apps/{os}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public RestResponse delete(@PathVariable("os") String os) throws RemoteException, ServerException {
        Stream.of(supportedOs).filter(os::equals).findAny().orElseThrow(
                () -> new RemoteException(PortalError.INVALID_OS)
        );

        final String path = getAppPath(os);
        final String key = "app.download." + os, value = "";

        serverConfigurationService.updateConfigurationEntry(key, value);

        if (!StringUtils.isEmpty(path)) {
            File file = new File(path);
            if (file.exists() && !file.isDirectory()) {
                if (!file.delete()) {
                    logger.error("failed to delete file: {}", path);
                }
            }
        }

        return AdminResponseBuilders.successResponseBuilder().build();
    }
}

package cn.com.xinli.portal.web.controller;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.ServerException;
import cn.com.xinli.portal.core.configuration.AppConfiguration;
import cn.com.xinli.portal.core.configuration.ServerConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Download controller.
 *
 * @author zhoupeng, created on 2016/4/6.
 */
@Controller
public class DownloadController {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ServerConfiguration serverConfiguration;

    private Resource performDownload(String path, HttpServletResponse response) {
        final String filename;

        int index = path.lastIndexOf('/');
        if (index != -1) {
            filename = path.substring(index + 1);
        } else {
            filename = path;
        }

        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        //return resourceLoader.getResource("classpath:/WEB-INF/static/download/a.apk");
        if (path.startsWith("/") && path.length() > 1)
            path = path.substring(1);

        return resourceLoader.getResource("classpath:/WEB-INF/" + path);
    }

    @RequestMapping(value = "/download/{os}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public Resource download(@PathVariable("os") String os,
                             HttpServletResponse response)
            throws IOException, ServerException {
        AppConfiguration appConfiguration = serverConfiguration.getAppConfiguration();

        String path = null;

        switch (os.toLowerCase()) {
            case "android":
                path = appConfiguration.getAndroidAppFileName();
                break;

            case "ios":
                throw new IllegalArgumentException("fdaf");
//                path = appConfiguration.getiOSAppFileName();
//                break;

            default:
                break;
        }

        if (!StringUtils.isEmpty(path)) {
            return performDownload(path, response);
        }

        throw new ServerException(PortalError.APP_NOT_AVAILABLE);
    }
}

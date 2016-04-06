package cn.com.xinli.portal.web.controller;

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
import java.rmi.RemoteException;

/**
 * Download controller.
 *
 * @author zhoupeng, created on 2016/4/6.
 */
@Controller
public class DownloadController {

    @Autowired
    private ResourceLoader resourceLoader;

    @RequestMapping(value = "/download/{os}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public Resource download(@PathVariable("os") String os,
                             HttpServletResponse response) throws IOException {
        switch (os.toLowerCase()) {
            case "android":
                response.setHeader("Content-Disposition", "attachment; filename=\"a.apk\"");
                return resourceLoader.getResource("classpath:/WEB-INF/static/download/a.apk");

            case "ios":
            default:
                throw new RemoteException("not supported.");
        }
    }
}

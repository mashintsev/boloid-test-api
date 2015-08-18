package ru.mashintsev.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.mashintsev.Config;
import ru.mashintsev.api.ExternalApiService;
import ru.mashintsev.dao.UserDAO;
import ru.mashintsev.domain.*;
import ru.mashintsev.exceptions.NotFoundUserException;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadFactory;

@RestController
public class UserController {

    private static final Logger log = LogManager.getLogger(UserController.class);

    @Autowired
    private Config config;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private ExternalApiService externalApiService;
    private File rootImageFolder;

    @PostConstruct
    public void init() {
        this.rootImageFolder = new File(config.getImagesRootFolder());
        if (!rootImageFolder.exists() || !rootImageFolder.isDirectory())
            rootImageFolder.mkdirs();
    }

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    @ResponseBody
    public String provideUploadInfo() {
        return "You can upload a file by posting to this same URL.";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ApiResponse handleFileUpload(@RequestBody MultipartFile file) {
        ApiResponse resp = new ApiResponse();

        if (!file.isEmpty()) {
            //Можно дополнительно проверять файл на соответсвие jpeg-файлу
            try {
                String fileUri = UUID.randomUUID().toString();
                String dynFolder = DateTimeFormatter.ofPattern("yyyyMM").format(LocalDate.now());
                String uri = dynFolder + File.separator + fileUri;

                File copy = new File(rootImageFolder, uri + ".jpeg");
                if (!copy.getParentFile().isDirectory() || !copy.getParentFile().exists())
                    copy.getParentFile().mkdirs();
                OutputStream stream = new FileOutputStream(copy);

                IOUtils.copyLarge(file.getInputStream(), stream);
                IOUtils.closeQuietly(stream);

                resp.setSuccess(true);
                resp.setResult(uri);
            } catch (Throwable t) {
                resp.setSuccess(false);
                resp.setError("Can't upload file on server");

                log.error("Upload image", t);
            }
        } else {
            resp.setSuccess(false);
            resp.setError("Bad file to upload. Size=" + file.getSize() + "; Content-Type=" + file.getContentType());
        }

        return resp;
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ApiResponse createUser(@RequestBody User user) {
        ApiResponse response = new ApiResponse();
        try {
            userDAO.save(user);
            response.setSuccess(true);
            Map<String, Object> result = new HashMap<>();
            result.put("id", user.getId());
            response.setResult(result);
        } catch (Throwable t) {
            response.setSuccess(false);
            response.setError("Can't create user");
            //Можно расшить информацию об ошибке, которую необходимо передавать клиенту

            log.info("Create user. " + user, t);
        }
        return response;

    }

    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    public ApiResponse getUser(@PathVariable String userId) {
        ApiResponse response = new ApiResponse();
        try {
            User user = userDAO.findById(userId);
            if (user != null) {
                response.setSuccess(true);
                response.setResult(user);
            } else {
                response.setSuccess(false);
                response.setError("User " + userId + " was not found.");
            }
        } catch (Throwable t) {
            response.setSuccess(false);
            response.setError("Can't find user " + userId);

            log.error("Finding user " + userId, t);
        }
        return response;

    }

    @RequestMapping(value = "/userstatus", method = RequestMethod.POST)
    public ApiResponse updateStatus(@RequestBody UserStatusParams params) {
        ApiResponse response = new ApiResponse();
        try {
            params.setStatus(normalizeStatus(params.getStatus()));
            if (params.getStatus() != null) {

                String oldStatus = userDAO.updateStatus(params.getUserId(), params.getStatus());
                Map<String, String> result = new HashMap<>();
                result.put("oldStatus", oldStatus);
                result.put("newStatus", params.getStatus());
                result.put("userId", params.getUserId());

                response.setSuccess(true);
                response.setResult(result);
            } else {
                response.setSuccess(false);
                response.setError("Bad new status");
            }
        } catch (NotFoundUserException e) {
            response.setSuccess(false);
            response.setError("User " + params.getUserId() + " was not found.");

            if (log.isDebugEnabled())
                log.debug("User " + params.getUserId() + " status update ", e);
        } catch (Throwable t) {
            response.setSuccess(false);
            response.setError("Can't change user status");

            log.error("User " + params.getUserId() + " status update ", t);
        }

        try {
            externalApiService.updateUserStatus(params.getUserId(), params.getStatus());
        } catch (Throwable t) {
            log.warn("External API return exception", t);
        }
        return response;
    }

    @RequestMapping(value = "/statistics", method = RequestMethod.POST)
    public ApiResponse statistics(@RequestBody(required = false) StatisticsParams params) {
        ApiResponse response = new ApiResponse();
        try {
            List<UserStatistic> statistics = userDAO.getStatistics(normalizeStatus(params.getStatus()), params.getTimestamp());
            response.setSuccess(true);
            response.setResult(statistics);
        } catch (Throwable t) {
            response.setSuccess(false);
            response.setError("Can't get statistics");

            log.error("Statistics. Status=" + params.getStatus() + ", timestamp=" + params.getTimestamp(), t);
        }
        return response;

    }

    private static String normalizeStatus(String status) {
        if ("online".equalsIgnoreCase(status))
            return "Online";
        else if ("offline".equalsIgnoreCase(status))
            return "Offline";
        return null;
    }
}
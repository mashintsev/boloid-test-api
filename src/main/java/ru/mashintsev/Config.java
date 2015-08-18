package ru.mashintsev;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by mashintsev@gmail.com on 17.08.15.
 */
@Component
public class Config {

    @Value("${upload.rootfolder}")
    private String imagesRootFolder;

    public String getImagesRootFolder() {
        return imagesRootFolder;
    }

    public void setImagesRootFolder(String imagesRootFolder) {
        this.imagesRootFolder = imagesRootFolder;
    }
}

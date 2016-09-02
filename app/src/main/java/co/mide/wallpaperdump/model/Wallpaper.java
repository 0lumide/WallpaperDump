package co.mide.wallpaperdump.model;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import co.mide.wallpaperdump.util.Objects;

/**
 * Class used to represent a single image in a wallpaper dump
 */
@SuppressWarnings("unused")
public class Wallpaper  {

    private String imageId = "";
    private Boolean isNSFW = Boolean.FALSE;
    private List<String> tags = new ArrayList<>();


    /**
     * The unique image id needed for retrieving the image
     **/
    public String getImageId() {
        return imageId;
    }
    public void setImageId(String imageId) {
        this.imageId = imageId;
    }


    /**
     * Represents if the image is not safe for work as marked by clarifai.
     **/
    public Boolean getIsNSFW() {
        return isNSFW;
    }
    public void setIsNSFW(Boolean isNSFW) {
        this.isNSFW = isNSFW;
    }


    /**
     * String representing the hexadecimal representation of the color.
     **/
    public List<String> getTags() {
        return tags;
    }
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Wallpaper wallpaper = (Wallpaper) o;
        return Objects.equals(imageId, wallpaper.imageId) &&
                Objects.equals(isNSFW, wallpaper.isNSFW) &&
                Objects.equals(tags, wallpaper.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageId, isNSFW, tags);
    }

    @Override
    public String toString()  {
        return new Gson().toJson(this);
    }
}


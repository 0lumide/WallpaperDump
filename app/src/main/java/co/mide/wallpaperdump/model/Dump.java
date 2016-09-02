package co.mide.wallpaperdump.model;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import co.mide.wallpaperdump.util.Objects;

/**
 * Class used to represent a Wallpaper Dump
 */
@SuppressWarnings("unused")
public class Dump {
    //INTEGER_ID | ALBUM_ID/DUMP_ID | IS_NSFW | UPLOADER | UPLOADED_DATE | WALLPAPERS |
    private String dumpId = "";
    private Boolean isNSFW = Boolean.FALSE;
    private String uploadedBy = "";
    private String title = "";
    private Long timestamp = 0L;
    private List<String> images = new ArrayList<>();


    /**
     * The unique dump id needed for retrieving the dump
     **/
    public String getDumpId() {
        return dumpId;
    }
    public void setDumpId(String dumpId) {
        this.dumpId = dumpId;
    }

    /**
     * The unique dump title
     **/
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * The user id of the dump uploadedBy
     **/
    public String getUploadedBy() {
        return uploadedBy;
    }
    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    /**
     * The timestamp of when the dump was first added
     **/
    public Long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }


    /**
     * Whether the album is NSFW as marked by the uploadeder
     **/
    public Boolean getIsNSFW() {
        return isNSFW;
    }
    public void setIsNSFW(Boolean isNSFW) {
        this.isNSFW = isNSFW;
    }


    /**
     * An array of wallpaper objects
     **/
    public List<String> getImages() {
        return images;
    }
    public void setImages(List<String> images) {
        this.images = images;
    }


    @Override
    public String toString(){
        return new Gson().toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Dump dump = (Dump) o;
        return Objects.equals(dumpId, dump.dumpId) &&
                Objects.equals(isNSFW, dump.isNSFW) &&
                Objects.equals(images, dump.images) &&
                Objects.equals(timestamp, dump.timestamp) &&
                Objects.equals(uploadedBy, dump.uploadedBy);
    }
}


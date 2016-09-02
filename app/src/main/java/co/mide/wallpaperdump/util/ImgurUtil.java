package co.mide.wallpaperdump.util;

import android.support.annotation.NonNull;

/**
 * A basic Imgur Utility class
 */
public class ImgurUtil {
    public static String userProfileLinkFromId(@NonNull String userId){
        return "https://imgur.com/user/"+userId;
    }

    public static String userProfileHtmlLinkFromId(@NonNull String userId){
        return "<a href=\""+userProfileLinkFromId(userId)+"\">"+userId+"</a>";
    }

    public static String imageLinkFromId(@NonNull String imageId){
        return "http://i.imgur.com/"+imageId+".jpg";
    }

    public static String smallerImageLinkFromId(@NonNull String imageId){
        return "http://i.imgur.com/"+imageId+"l.jpg";
    }
}

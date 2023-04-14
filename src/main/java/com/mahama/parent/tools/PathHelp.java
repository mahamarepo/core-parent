package com.mahama.parent.tools;


import com.mahama.common.utils.StringUtil;
import com.mahama.parent.utils.HttpUtil;

import java.util.ArrayList;
import java.util.List;

public class PathHelp {
    public static String AddPrePath(String url) {
        if (StringUtil.isNullOrEmpty(url)) {
            return url;
        }
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }
        String fileHttpPath = StorageTool.getFileHttpPath();
        if (fileHttpPath.startsWith("http://") || fileHttpPath.startsWith("https://")) {
            return fileHttpPath + url;
        }
        String origin = HttpUtil.getOrigin();
        return origin + fileHttpPath + url;
    }

    public static String[] AddPrePath(String[] urls) {
        String[] result = new String[urls.length];
        for (int i = 0; i < urls.length; i++) {
            result[i] = AddPrePath(urls[i]);
        }
        return result;
    }

    public static String cleanPathToSave(String path) {
        if (StringUtil.isNullOrEmpty(path)) {
            return path;
        }
        String origin = HttpUtil.getOrigin();
        if (StringUtil.isNotNullOrEmpty(origin) && path.startsWith(origin)) {
            path = path.replace(origin, "");
        }
        String savedPath = StorageTool.getFileSavePath();
        String httpPath = StorageTool.getFileHttpPath();

        path = path.replace("\\", "/");
        if (path.startsWith("http://") || path.startsWith("https://") || savedPath.startsWith("oss:")) {
            return path.replaceFirst("^" + httpPath, "");
        }
        return path.replaceFirst("^" + savedPath + "/", "").replaceFirst("^" + httpPath, "");
    }

    public static List<String> cleanPathToSave(List<String> paths) {
        List<String> list = new ArrayList<>();
        paths.forEach(item -> {
            list.add(cleanPathToSave(item));
        });
        return list;
    }

    public static String AddAvatarPrePath(String url) {
        if (StringUtil.isNullOrEmpty(url)) {
            return url;
        }
        if (url.startsWith("data:")) {
            return url;
        }
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }
        String avatarHttpPath = StorageTool.getAvatarHttpPath();
        if (avatarHttpPath.startsWith("http://") || avatarHttpPath.startsWith("https://")) {
            return avatarHttpPath + url;
        }
        String origin = HttpUtil.getOrigin();
        return origin + avatarHttpPath + url;
    }

    public static String cleanAvatarPathToSave(String path) {
        if (StringUtil.isNullOrEmpty(path)) {
            return path;
        }
        String origin = HttpUtil.getOrigin();
        if (StringUtil.isNotNullOrEmpty(origin) && path.startsWith(origin)) {
            path = path.replace(origin, "");
        }
        path = path.replace("\\", "/");
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return path.replaceFirst("^" + StorageTool.getAvatarHttpPath(), "");
        }
        return path.replaceFirst("^" + StorageTool.getAvatarSavePath() + "/", "").replaceFirst("^" + StorageTool.getAvatarHttpPath() + "/", "");
    }
}

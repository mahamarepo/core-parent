package com.mahama.parent.tools;

import com.mahama.common.utils.CfgUtil;
import com.mahama.common.utils.DateUtil;

public class StorageTool {
    public static String getFileSavePathByMonth() {
        return String.join("/", getFileSavePath(), DateUtil.getYear(), DateUtil.getMonthInt() + "", DateUtil.getDayInt() + "");
    }

    public static String getFileSavePath() {
        return CfgUtil.getString("files.upload.savePath", "files");
    }

    public static String getFileHttpPath() {
        return CfgUtil.getString("files.download.httpPath", "https://download.com/");
    }

    public static String getAvatarSavePath() {
        return CfgUtil.getString("avatar.upload.savePath", "files/avatar");
    }

    public static String getAvatarHttpPath() {
        return CfgUtil.getString("avatar.download.httpPath", "https://download.com/avatar/");
    }
}

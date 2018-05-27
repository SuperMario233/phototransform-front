package top.zibin.luban;

import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

enum Checker {
  SINGLE;

  private static List<String> format = new ArrayList<>();
  private static final String JPG = "jpg";
  private static final String JPEG = "jpeg";
  private static final String PNG = "png";
  private static final String WEBP = "webp";
  private static final String GIF = "gif";

  static {
    format.add(JPG);
    format.add(JPEG);
    format.add(PNG);
    format.add(WEBP);
    format.add(GIF);
  }

  @Deprecated
  boolean isImage(String path) {
    if (TextUtils.isEmpty(path)) {
      return false;
    }

    String suffix = path.substring(path.lastIndexOf(".") + 1, path.length());
    return format.contains(suffix.toLowerCase());
  }

  boolean isJPG(String path) {
    if (TextUtils.isEmpty(path)) {
      return false;
    }

    String suffix = path.substring(path.lastIndexOf("."), path.length()).toLowerCase();
    return suffix.contains(JPG) || suffix.contains(JPEG);
  }

  String extSuffix(String path) {
    if (TextUtils.isEmpty(path)) {
      return ".jpg";
    }

    return path.substring(path.lastIndexOf("."), path.length());
  }

  boolean needCompress(int leastCompressSize, String path) {
    if (leastCompressSize > 0) {
      File source = new File(path);
      return source.exists() && source.length() > (leastCompressSize << 10);
    }
    return true;
  }
}

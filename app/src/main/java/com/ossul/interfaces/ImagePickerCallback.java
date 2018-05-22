package com.ossul.interfaces;

import java.io.File;

/**
 * @author Rajan Tiwari on 13-Dec-16
 */
public interface ImagePickerCallback {

    void onImageClicked(File file,String filePath,String imageName);
}

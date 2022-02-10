/*
 * TSBattery - A new way to save your battery avoid cancer apps hacker it.
 * Copyright (C) 2019-2022 qzmmcn@163.com
 * https://github.com/fankes/TSBattery
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by ferredoxin.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 *
 * This file is Created by zpp0196 on 2019/2/9.
 */
package com.fankes.tsbattery.utils;

import android.content.Context;

import com.fankes.tsbattery.BuildConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@SuppressWarnings("ALL")
public class FileUtils {

    private static final String FILE_PREF_NAME = BuildConfig.APPLICATION_ID + "_preferences.xml";

    public static boolean copyFile(File srcFile, File targetFile) {
        FileInputStream ins = null;
        FileOutputStream out = null;
        try {
            if (targetFile.exists()) {
                targetFile.delete();
            }
            File targetParent = targetFile.getParentFile();
            if (!targetParent.exists()) {
                targetParent.mkdirs();
            }
            targetFile.createNewFile();
            ins = new FileInputStream(srcFile);
            out = new FileOutputStream(targetFile);
            byte[] b = new byte[1024];
            int n;
            while ((n = ins.read(b)) != -1) {
                out.write(b, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (ins != null) {
                    ins.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static File getDataDir(Context context) {
        return new File(context.getApplicationInfo().dataDir);
    }

    public static File getPrefDir(Context context) {
        return new File(getDataDir(context), "shared_prefs");
    }

    public static File getDefaultPrefFile(Context context) {
        return new File(getPrefDir(context), FILE_PREF_NAME);
    }
}

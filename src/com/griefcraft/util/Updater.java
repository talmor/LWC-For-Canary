package com.griefcraft.util;

import com.griefcraft.LWCInfo;
import com.griefcraft.logging.Logger;
// import com.sun.net.ssl.internal.ssl.Provider;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

public class Updater {
    private Logger              logger        = Logger.getLogger(getClass().getSimpleName());
    // private static final String UPDATE_SITE =
    // "https://github.com/Hidendra/LWC/raw/master/";
    private static final String UPDATE_SITE   = "https://github.com/talmor/LWC-For-Canary/raw/master/DIST/";
    private static final String VERSION_FILE  = "VERSION";
    private static final String DIST_FILE     = "LWC.jar";
    private List<UpdaterFile>   needsUpdating = new ArrayList();

    public Updater() {
        // enableSSL();
    }

    public void check() {
        String[] paths = { "lib/sqlite.jar", "lib/" + getOSSpecificFileName() };

        for (String path : paths) {
            File file = new File(path);

            if ((file != null) && (!file.exists()) && (!file.isDirectory())) {
                UpdaterFile updaterFile = new UpdaterFile(UPDATE_SITE + path+"?raw=true");
                updaterFile.setLocalLocation(path);

                this.needsUpdating.add(updaterFile);
            }
        }

        double latestVersion = getLatestVersion();

        if (latestVersion > 1.44D) {
            this.logger.info("Update detected for LWC");
            this.logger.info("Latest version: " + latestVersion);
        }
    }

    public boolean checkDist() {
        double latestVersion = getLatestVersion();

        if (latestVersion > LWCInfo.VERSIONF) {
            UpdaterFile updaterFile = new UpdaterFile(UPDATE_SITE + DIST_FILE);
            updaterFile.setLocalLocation("plugins/LWC.jar");

            this.needsUpdating.add(updaterFile);
            try {
                update();
                this.logger.info("Updated successful");
                return true;
            } catch (Exception e) {
                this.logger.info("Update failed: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return false;
    }

    public double getLatestVersion() {
        try {
            URL url = new URL(UPDATE_SITE + VERSION_FILE);

            InputStream inputStream = url.openStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            double version = Double.parseDouble(bufferedReader.readLine());

            bufferedReader.close();

            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0.0D;
    }

    // private void enableSSL()
    // {
    // Security.addProvider(new Provider());
    // System.setProperty("java.protocol.handler.pkgs",
    // "com.sun.net.ssl.internal.www.protocol");
    // }

    public String getOSSpecificFileName() {
        String osname = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch");

        if (osname.contains("windows")) {
            osname = "win";
            arch = "x86";
        } else if (osname.contains("mac")) {
            osname = "mac";
            arch = "universal";
        } else if (osname.contains("nix")) {
            osname = "linux";
        } else if (osname.equals("sunos")) {
            osname = "linux";
        }

        if ((arch.startsWith("i")) && (arch.endsWith("86"))) {
            arch = "x86";
        }

        return osname + "-" + arch + ".lib";
    }

    public void update() throws Exception {
        if (this.needsUpdating.size() == 0) {
            return;
        }

        File folder = new File("lib");

        if ((folder.exists()) && (!folder.isDirectory()))
            throw new Exception("Folder \"lib\" cannot be created ! It is a file!");
        if (!folder.exists()) {
            this.logger.info("Creating folder : lib");
            folder.mkdir();
        }

        this.logger.info("Need to download " + this.needsUpdating.size() + " object(s)");

        for (UpdaterFile item : this.needsUpdating) {
            this.logger.info(" - Downloading file : " + item.getRemoteLocation());

            URL url = new URL(item.getRemoteLocation());
            File file = new File(item.getLocalLocation());

            if (file.exists()) {
                file.delete();
            }

            InputStream inputStream = url.openStream();
            OutputStream outputStream = new FileOutputStream(file);

            saveTo(inputStream, outputStream);

            inputStream.close();
            outputStream.close();

            this.logger.info("  + Download complete");
        }
    }

    private void saveTo(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;

        while ((len = inputStream.read(buffer)) > 0)
            outputStream.write(buffer, 0, len);
    }
}

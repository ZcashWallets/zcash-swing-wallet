package com.cabecinha84.zcashui;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import com.vaklinov.zcashui.Log;
import com.vaklinov.zcashui.OSUtil;
import com.vaklinov.zcashui.ZCashClientCaller;


public class AppLock {
    private static File f;
    private static FileChannel channel;
    private static FileLock lock;

    public static boolean lock() {
        try {
        	String settingsDir = OSUtil.getSettingsDirectory();
            String fileName = "jstock.lock";
            f = new File(settingsDir + File.separator + fileName);
            channel = new RandomAccessFile(f, "rw").getChannel();
            lock = channel.tryLock();
            if(lock == null) {
                channel.close();
                return false;
            }
        } catch (Exception ex) {            
        	Log.warning("Error obtaining checking wallet lock due to: {0} {1}",
					ex.getClass().getName(), ex.getMessage());
        }
        return true;
    }

    public static void unlock() {
        // release and delete file lock
        try {
            if (lock != null) {
                lock.release();
                channel.close();
                f.delete();
                try {
                	//if daemon is running for any reason we try to stop it here because wallet is closing.
	                ZCashClientCaller initialClientCallerA = new ZCashClientCaller(OSUtil.getProgramDirectory());
	                initialClientCallerA.stopDaemon();
                }
                catch (Exception ex) {
                	Log.warning("Error stopping daemon due to: {0} {1}",
        					ex.getClass().getName(), ex.getMessage());
                }
            }
        } catch(IOException e) {
        	Log.warning("Error ounlocking wallet lock due to: {0} {1}",
					e.getClass().getName(), e.getMessage());
        }        
    }
}
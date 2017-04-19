package xyz.lailin.atool.Storage;


import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.github.mikephil.charting.charts.Chart.LOG_TAG;

/**
 * Created by laili on 2017/4/18.
 * 保存数据到文件
 */

public class FileStorage {

    /**
     * 文件对象
     */
    private File file;

    public FileStorage(String filePath){
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        File dir =getAlbumStorageDir(filePath);
        file = new File(dir,sdf.format(d)+".txt");

    }


    /**
     * 获取文件夹，如果不存在就创建
     * @param dirName 文件夹名称
     * @return File file 文件夹File对象
     */
    private File getAlbumStorageDir(String dirName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStorageDirectory(), dirName);
        if (!file.exists()&&!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
    }

    /**
     * 对文件添加数据
     * @param  data 数据
     */
    public void addData(List data){
        try{
            String string="";
            for (int i = 0; i <data.size() ; i++) {
                String  tmp=(String)  data.get(i);
                string+=tmp+"\n";
            }
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(string.getBytes());
            outputStream.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public List getData(){
        return new ArrayList();
    }

    /**
     * 获取文件对象
     * @return file 文件对象
     */
    public File getFile(){
        return file;
    }
}

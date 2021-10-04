package ro.utcn.helper;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * For files
 * Created by Lucian on 6/1/2017.
 */

@Component
public class FileHelper {

    public static void saveFile(Workbook workbook, File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file.getAbsoluteFile());
        workbook.write(fos);
        fos.close();
    }
}

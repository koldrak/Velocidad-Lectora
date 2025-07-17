import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;
import java.io.*;

public class RarExtractor {
    public static void extraer(String rarPath, String outputDir) throws Exception {
        Archive archive = new Archive(new File(rarPath));
        FileHeader fh;
        while ((fh = archive.nextFileHeader()) != null) {
            if (fh.isDirectory()) continue;

            File out = new File(outputDir + File.separator + fh.getFileNameString().trim());
            try (FileOutputStream os = new FileOutputStream(out)) {
                archive.extractFile(fh, os);
            }
        }
    }
}

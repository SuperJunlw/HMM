import java.io.File;
import java.io.IOException;
import java.util.*;

public class FileIO {

    public static int readZbot() throws IOException {
        List<String> list = new ArrayList<>();
        int count = 0;
        File folder = new File("zbot");

        File fileList[] = folder.listFiles();
        Scanner sc = null;
        for(File file : fileList) {

            //Instantiating the Scanner class
            count++;
        }
        return count;
    }
}

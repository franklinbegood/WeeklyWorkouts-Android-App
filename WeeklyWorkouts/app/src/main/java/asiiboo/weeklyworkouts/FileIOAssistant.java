package asiiboo.weeklyworkouts;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static android.content.Context.MODE_PRIVATE;


class FileIOAssistant {
    private static final String fileName = "u_info";

    /**
     * Static method for saving object into file of MODE_PRIVATE
     * Throws IOException
     *
     * @param context  - the context of the method
     * @param fileName - name of the file to be saved
     * @param obj      - object to be saved
     */
    static void save(Context context, String fileName, Object obj) throws IOException {
        FileOutputStream fos = context.openFileOutput(fileName, MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(obj);
        oos.close();
    }

    /**
     * Static method for loading object from file
     * Throws IOException | ClassNotFoundException
     *
     * @param context  - the context of the method
     * @param fileName - name of the file to be loaded
     * @return - returns object loaded from file
     */
    static Object load(Context context, String fileName) throws IOException, ClassNotFoundException {
        FileInputStream fis = context.openFileInput(fileName);
        ObjectInputStream ois = new ObjectInputStream(fis);
        UserInformation temp = (UserInformation) ois.readObject();
        ois.close();
        return temp;
    }

    /**
     * Static method for getting UserInformation filename
     *
     * @return - returns file name
     */
    static String getFileName() {
        return fileName;
    }


}

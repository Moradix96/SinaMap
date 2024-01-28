package ir.co.holoo.sinamap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DB_NAME = "sinamap";
    private static final int DB_VERSION = 1;
    // The Android's default system path of your application database.
    private static String DB_PATH = "";
    private final Context myContext;
    private SQLiteDatabase myDatabase;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;
        DB_PATH = context.getDatabasePath(DB_NAME).getPath();
    }

    public void createDatabase() throws IOException {

        // Check if the database already exists.
        final boolean databaseExists = checkDatabase();

        if (databaseExists) { //This is just for being backward compatible every time
            removeDatabase();
        }

        // Create an empty database into the default system path of your application database.
        this.getReadableDatabase();

        try {
            // Copy the database from assets folder to the created empty database.
            copyDatabase();
            Log.d(TAG, "Database created successfully");
        } catch (IOException e) {
            throw new Error("Error copying database");
        }

    }

    /**
     * This creates the db from file when it's not available
     **/
    public void createDatabaseOld() throws IOException {

        // Check if the database already exists.
        final boolean databaseExists = checkDatabase();

        if (!databaseExists) {
            // Create an empty database into the default system path of your application database.
            this.getReadableDatabase();

            try {
                // Copy the database from assets folder to the created empty database.
                copyDatabase();
                Log.d(TAG, "Database created successfully");
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean removeDatabase() {
        final File databaseFile = new File(DB_PATH);
        return databaseFile.delete();
    }

    private boolean checkDatabase() {
        // Check if the database already exists.
        final File databaseFile = new File(DB_PATH);
        return databaseFile.exists();
    }

    private void copyDatabase() throws IOException {
        // Open the local database as the input stream.
        final InputStream inputStream = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty database.
        final OutputStream outputStream = new FileOutputStream(DB_PATH);

        // Transfer bytes from the input file to the output file.
        final byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        // Close the streams.
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    public SQLiteDatabase openDatabase() {
        // Open the database.
        myDatabase = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        return myDatabase;
    }

    @Override
    public synchronized void close() {
        if (myDatabase != null)
            myDatabase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // This method is not used since the database is already created from the assets folder.
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This method is not used since the database is already created from the assets folder.
    }
}
package nl.mheijden.prog3app.model.data;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Gemaakt door Maarten van der Heijden on 1-1-2018.
 */

public class SQLiteLocalDatabase extends SQLiteOpenHelper {
    /**
     * Name of the database and the current version
     */
    private static final String DATABASE_NAME = "StudentMaaltijden";
    private static final int DATABASE_VERSION = 9;

    /**
     * @param context of the application
     */
    public SQLiteLocalDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * @param sqLiteDatabase from the SQLiteOpenHelper class
     */
    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Students; DROP TABLE IF EXISTS Meals; DROP TABLE IF EXISTS FellowEaters");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS `FellowEaters` (\n" +
                "  `ID` int(11) PRIMARY KEY UNIQUE NOT NULL,\n" +
                "  `AmountOfGuests` int(11) NOT NULL DEFAULT '0',\n" +
                "  `StudentNumber` int(11) NOT NULL,\n" +
                "  `MealID` int(11) NOT NULL\n" +
                ")");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS `Meals` (\n" +
                "  `ID` int(11) PRIMARY KEY UNIQUE NOT NULL,\n" +
                "  `Dish` varchar(100) NOT NULL,\n" +
                "  `DateTime` datetime NOT NULL,\n" +
                "  `Info` text NOT NULL,\n" +
                "  `ChefID` int(11) NOT NULL,\n" +
                "  `Picture` longblob NOT NULL,\n" +
                "  `Price` decimal(10,0) NOT NULL,\n" +
                "  `MaxFellowEaters` int(11) NOT NULL,\n" +
                "  `DoesCookEat` tinyint(1) NOT NULL DEFAULT '1'\n" +
                ")");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS `Students` (\n" +
                "  `StudentNumber` int(11) PRIMARY KEY UNIQUE NOT NULL,\n" +
                "  `FirstName` varchar(50) NOT NULL,\n" +
                "  `Insertion` varchar(50) DEFAULT NULL,\n" +
                "  `LastName` varchar(50) NOT NULL,\n" +
                "  `Email` varchar(50) NOT NULL,\n" +
                "  `PhoneNumber` varchar(30) DEFAULT NULL\n" +
                ")");
    }

    /**
     * @param sqLiteDatabase from the SQLiteOpenHelper class
     * @param i              old version
     * @param i1             new version
     */
    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Students; DROP TABLE IF EXISTS Meals; DROP TABLE IF EXISTS FellowEaters");
        this.onCreate(sqLiteDatabase);
    }
}

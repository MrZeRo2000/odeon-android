package com.romanpulov.odeon;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.romanpulov.odeon.db.AppDatabase;
import com.romanpulov.odeon.db.ArtifactType;
import com.romanpulov.odeon.db.DBManager;
import com.romanpulov.odeon.db.MDBReader;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.util.List;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DBPopulateTest {

    private static class TestDBManager extends DBManager {
        public TestDBManager(Context mContext) {
            super(mContext);
        }

        @Override
        protected AppDatabase obtainDatabase(Context context) {
            return Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        }
    }
    private static TestDBManager dbManager;
    private static MDBReader reader;

    @BeforeClass
    public static void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        dbManager = new TestDBManager(context);
    }

    @Test
    public void test100PrePopulate() throws Exception {
        dbManager.prepare();
        AppDatabase db = dbManager.getDatabase();

        List<ArtifactType> artifactTypes = db.artifactTypeDAO().getAll();
        Assert.assertEquals(3, artifactTypes.size());
    }

    @Test
    public void test200Read() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        File file = new File(context.getFilesDir(), "Archive/Cat2000/Cat2000.mdb");
        reader = new MDBReader(file);
        reader.readAll();
    }

    @Test
    public void test300Load() throws Exception {
        dbManager.getDatabase().artistDAO().insertAll(reader.getArtists());
        dbManager.getDatabase().artifactDAO().insertAll(reader.getArtifacts());
        dbManager.getDatabase().compositionDAO().insertAll(reader.getCompositions());
    }

    @Test
    public void test400Counts() throws Exception {
        Assert.assertEquals(dbManager.getDatabase().artistDAO().getAll().size(), reader.getArtists().size());
        Assert.assertEquals(dbManager.getDatabase().artifactDAO().getAll().size(), reader.getArtifacts().size());
        Assert.assertEquals(dbManager.getDatabase().compositionDAO().getAll().size(), reader.getCompositions().size());
    }

}

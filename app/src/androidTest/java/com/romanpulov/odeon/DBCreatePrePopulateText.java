package com.romanpulov.odeon;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.romanpulov.odeon.db.AppDatabase;
import com.romanpulov.odeon.db.ArtifactType;
import com.romanpulov.odeon.db.DBManager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Assert.*;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class DBCreatePrePopulateText {

    private static class TestDBManager extends DBManager {
        public TestDBManager(Context mContext) {
            super(mContext);
        }

        @Override
        protected AppDatabase obtainDatabase(Context context) {
            return Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        }
    }
    private TestDBManager dbManager;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        dbManager = new TestDBManager(context);
    }

    @After
    public void closeDb() {
        dbManager.close();
    }

    @Test
    public void testPrePopulate() throws Exception {
        dbManager.prepare();
        AppDatabase db = dbManager.getDatabase();

        List<ArtifactType> artifactTypes = db.artifactTypeDAO().getAll();
        Assert.assertEquals(3, artifactTypes.size());
    }
}

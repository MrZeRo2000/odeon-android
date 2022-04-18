package com.romanpulov.odeon;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.romanpulov.odeon.db.MDBReader;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Assert;

import java.io.File;

@RunWith(AndroidJUnit4.class)
public class MDBReaderTest {
    File file;
    MDBReader reader;

    @Before
    public void getFile() {
        Context context = ApplicationProvider.getApplicationContext();
        file = new File(context.getFilesDir(), "Archive/Cat2000/Cat2000.mdb");
        reader = new MDBReader(file);
    }

    @Test
    public void testRead() throws Exception {
        Assert.assertTrue(file.exists());
        reader.readAll();

        Assert.assertEquals(2513, reader.getArtifacts().size());
    }
}

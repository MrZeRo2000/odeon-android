package com.romanpulov.odeon;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.romanpulov.odeon.db.Artifact;
import com.romanpulov.odeon.db.Artist;
import com.romanpulov.odeon.db.MDBReader;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Assert;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MDBReaderTest {
    static File file;
    static MDBReader reader;

    @BeforeClass
    public static void getFile() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        file = new File(context.getFilesDir(), "Archive/Cat2000/Cat2000.mdb");
        reader = new MDBReader(file);
    }

    @Test
    public void testRead() throws Exception {
        Assert.assertTrue(file.exists());
        reader.readAll();

        Assert.assertTrue("Artist list is not empty", reader.getArtists().size() > 0);
        Assert.assertTrue("Artifacts list is not empty", reader.getArtifacts().size() > 0);
        Assert.assertTrue("Compositions list is not empty", reader.getCompositions().size() > 0);
    }

    @Test
    public void testReadArtifacts() {
        // common quantity
        Assert.assertEquals(2513 + 1119, reader.getArtifacts().size());

        //artifacts
        Assert.assertEquals(9+3, reader.getArtifacts().stream().filter(a -> a.getArtistId() == 5).count());
    }

    @Test
    public void testReadCompositions() {
        // compositions
        Assert.assertEquals(12, reader.getCompositions().stream().filter(c -> c.getArtifactId() == 8192).count());
    }

    @Test
    public void testReadArtists() {
        //calc and validate artists
        Set<Integer> artists = reader.getArtifacts().stream().map(Artifact::getArtistId).collect(Collectors.toSet());
        Assert.assertEquals(reader.getArtists().size(), artists.size());

        Integer[] getArtistsArray = reader.getArtists().stream().map(Artist::getId).toArray(Integer[]::new);
        Integer[] artistsArray = artists.toArray(new Integer[0]);

        Arrays.sort(getArtistsArray);
        Arrays.sort(artistsArray);

        Assert.assertArrayEquals(getArtistsArray, artistsArray);
    }

}

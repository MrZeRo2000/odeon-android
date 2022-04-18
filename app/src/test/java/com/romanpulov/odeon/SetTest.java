package com.romanpulov.odeon;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.*;

public class SetTest {


    @Test
    public void test1() {
        Set<Integer> ints = new HashSet<>();
        assertEquals(0, ints.size());

        ints.add(12);
        assertEquals(1, ints.size());
        ints.add(1);
        assertEquals(2, ints.size());

        ints.add(12);
        assertEquals(2, ints.size());
    }
}

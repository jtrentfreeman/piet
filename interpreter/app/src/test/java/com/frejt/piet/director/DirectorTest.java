package com.frejt.piet.director;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.reflection.Whitebox;

public class DirectorTest {

    private static Director director;

    @BeforeEach
    public void initDirector() {

        director = Director.getInstance();
        Whitebox.setInternalState(director, "dp", DP.RIGHT);
        Whitebox.setInternalState(director, "cc", CC.LEFT);

    }

    /**
     * Tests that, when the {@link Director#rotateCC(Integer)} function has an even
     * value passed in, the Director's CC value is not changed.
     */
    @Test
    public void rotateCC_EvenValue_DoesNotRotate() {

        CC expected = CC.LEFT;

        director.rotateCC(0);

        CC actual = director.getCC();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when the {@link Director#rotateCC(Integer)} function has an odd
     * value passed in, the Director's CC value is changed.
     */
    @Test
    public void rotateCC_OddValue_Rotates() {

        CC expected = CC.RIGHT;

        director.rotateCC(1);

        CC actual = director.getCC();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when the {@link Director#rotateDP(Integer)} function has a
     * 0 passed in, the Director's DP value is not changed.
     */
    @Test
    public void rotateDP_Zero_DoesNotRotate() {

        DP expected = DP.RIGHT;

        director.rotateDP(0);

        DP actual = director.getDP();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when the {@link Director#rotateDP(Integer)} function has a
     * positive integer passed in, the Director's DP value is rotated clockwise.
     */
    @Test
    public void rotateDP_PositiveInt_DPIsRight_RotatesClockwise() {

        DP expected = DP.DOWN;

        director.rotateDP(1);

        DP actual = director.getDP();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when the {@link Director#rotateDP(Integer)} function has a
     * negative integer passed in, the Director's DP value is rotated anticlockwise.
     */
    @Test
    public void rotateDP_NegativeInt_DPIsRight_RotatesAnticlockwise() {

        DP expected = DP.UP;

        director.rotateDP(-1);

        DP actual = director.getDP();

        assertEquals(expected, actual);

    }

}

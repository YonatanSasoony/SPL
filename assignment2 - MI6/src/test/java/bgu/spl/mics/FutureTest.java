package bgu.spl.mics;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



import static org.junit.jupiter.api.Assertions.*;

public class FutureTest {

    Future<Boolean> booleanFuture;

    @BeforeEach
    public void setUp(){
        booleanFuture = new Future<>();
    }

    @Test
    public void testGet(){

        booleanFuture.resolve(true);
        assertTrue(booleanFuture.get());
    }

    @Test
    public void testResolve(){

        booleanFuture.resolve(false);
        assertTrue(booleanFuture.get()==false);
    }

    @Test
    public void testIsDone(){

        booleanFuture.resolve(true);
        assertTrue(booleanFuture.isDone());


    }







}

package edu.uob.util;

import edu.uob.exception.Response;
import edu.uob.exception.STAGException;

public class Assert {
    public static void isTrue(Boolean bool, Response response) throws STAGException {
        if (!bool) {
            throw new STAGException(response);
        }
    }

    public static void notNull(Object object, Response response) throws STAGException {
        if (object == null) {
            throw new STAGException(response);
        }
    }

}
package edu.uob.util;

import edu.uob.exception.Response;
import edu.uob.exception.STAGException;

public class Assert {
    /**
     * check if input is true, otherwise throw exception with response
     */
    public static void isTrue(Boolean bool, Response response) throws STAGException {
        if (!bool) {
            throw new STAGException(response);
        }
    }

    /**
     * check if input is not null, otherwise throw exception with response
     */
    public static void notNull(Object object, Response response) throws STAGException {
        if (object == null) {
            throw new STAGException(response);
        }
    }

}

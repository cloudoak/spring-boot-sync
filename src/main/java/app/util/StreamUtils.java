package app.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class StreamUtils {

    private static Logger logger = LoggerFactory.getLogger(StreamUtils.class);

    private static final Integer BUFFER_SIZE = 1024;

    public static String of(InputStream inputStream) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            logger.error("read stream find a fail, {}", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.error("close stream find a fail, {}", e);
                }
            }
        }
        return builder.toString();
    }

    public static String ofBytes(InputStream inputStream)
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int len = 0;
        try
        {
            while ((len = inputStream.read(buffer, 0, buffer.length)) != -1)
            {
                out.write(buffer, 0, len);
            }
            return new String(out.toByteArray());
        }
        catch (IOException e)
        {
            logger.error("read stream find a fail, {}", e);
        }finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error("close stream find a fail, {}", e);
                }
            }
        }
        return null;
    }

}

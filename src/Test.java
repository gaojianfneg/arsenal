import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Test implements Serializable {

    public static void main(String[] args) throws IOException {
        List<String> list = new ArrayList<String>();
        list.add("abc");
        list.add("ABC");
        //System.out.println(writeToStr(list));
        System.out.println(zip(writeToStr(list)));

//        final List<String> serList = (List<String>) deserializeFromStr(writeToStr(list));
//        for (final String result : serList) {
//            System.out.println(result);
//        }
    }
    private static final ObjectMapper mapper = new ObjectMapper();
    /**
     * 根据json字符串转换成map
     *
     * @param json 原始字符串
     * @return 对应的map
     * @throws IOException 转换异常
     */
    public static Map<String, String> getMapbyJson(final String json) throws IOException {
        return mapper.readValue(json, new TypeReference<HashMap<String, String>>() {
        });
    }
    /**
     * 对hbase里面的列排序，取出最新的一列，然后得到对应的json，转换成用户map
     *
     * @param rawMap 原始map
     * @return 转换后得到的用户信息
     */
    private Map<String, String> getResultMap(final Map<String, String> rawMap) {
        if (!rawMap.isEmpty()) {
            List<String> setList = new ArrayList<String>(rawMap.keySet());
            Collections.sort(setList, new Comparator<Object>() {
                @Override
                public int compare(Object o1, Object o2) {
                    return o1.toString().compareTo(o2.toString());
                }
            });
            for (final Map.Entry<String, String> entry : rawMap.entrySet())
                if (entry.getKey().equals(setList.get(0))) {
                    try {
                        return getMapbyJson(entry.getValue());
                    } catch (IOException e) {
                       System.out.println(e);
                    }
                    break;
                }
        }
        return new HashMap<>();
    }

    private static final String DEFAULT_ENCODING = "ISO-8859-1";

    public static Object deserializeFromStr(String serStr) throws IOException {
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            //String deserStr = java.net.URLDecoder.decode(serStr, DEFAULT_ENCODING);
            byteArrayInputStream = new ByteArrayInputStream(serStr.getBytes(DEFAULT_ENCODING));
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return objectInputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            objectInputStream.close();
            byteArrayInputStream.close();
        }
        return null;
    }

    public static String writeToStr(Object obj) throws IOException {
        // 此类实现了一个输出流，其中的数据被写入一个 byte 数组。
        // 缓冲区会随着数据的不断写入而自动增长。可使用 toByteArray() 和 toString() 获取数据。
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 专用于java对象序列化，将对象进行序列化
        ObjectOutputStream objectOutputStream = null;
        String serStr = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            serStr = byteArrayOutputStream.toString(DEFAULT_ENCODING);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            objectOutputStream.close();
        }
        return serStr;
    }

    public static String zip(String str) {
        if (str == null) return null;
        byte[] compressed;
        String compressedStr = null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); ZipOutputStream zout = new ZipOutputStream(out)) {

            zout.putNextEntry(new ZipEntry("0"));
            zout.write(str.getBytes(Charset.forName("utf-8")));
            zout.closeEntry();
            compressed = out.toByteArray();
            compressedStr = Base64.getEncoder().encodeToString(compressed);

        } catch (IOException e) {
            //logger.error("zip error ", e);

        }

        return compressedStr;
    }
}

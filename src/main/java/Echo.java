import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author wangqiupeng
 * @date 2019年12月11日19:02:52
 * @desc goReplay中间件脚本
 */
public class Echo {


    private static final String SPLITTER_HEADER_BODY_SPLITTER = "\r\n\r\n";
    private static final String SPLITTER_HEAD_FIRST_LINE = "\n";
    private static final String SPLITTER_HEADER_ITEM = " ";
    /**
     * payload type, possible values: 1 - request, 2 - original response, 3 - replayed response
     */
    private static final String PAYLOAD_TYPE_REQUEST = "1";
    private static final String PAYLOAD_TYPE_ORIGINAL_RESPONSE = "2";

    //第一行项目数
    private static final int  FIRST_ITEMS = 3;

    /**
     * 定义新增加的requestId参数名称
     */
    private static String INJECT_TO_REQUEST_ENTITY_REQUEST_ID = "RequestId";

    public static String decodeHexString(String s) throws Exception {
        byte[] decodedHex = DatatypeConverter.parseHexBinary(s);
        String decodedString = new String(decodedHex, "UTF-8");
        return decodedString;
    }

    public static String encodeHexString(String str) {
        if (str == null) {
            return null;
        }
        byte[] strBytes = str.getBytes();
        String encodeString = DatatypeConverter.printHexBinary(strBytes);
        return encodeString;
    }

    public static String transformHTTPMessage(String req) {
        String content = req;
        String[] lines = content.split(SPLITTER_HEAD_FIRST_LINE);
        String result = content;
        if (lines == null || lines.length <=1){
            return result;
        }
        String firstLine = lines[0];
        String secondLine = lines[1];
        String[] firstLineItems = firstLine.split(SPLITTER_HEADER_ITEM);
        if (firstLineItems.length != FIRST_ITEMS){
            return result;
        }else{
            String payloadType = firstLineItems[0];
            String requestId = firstLineItems[1];
            if (PAYLOAD_TYPE_REQUEST.equals(payloadType)){
                String[]  secondListItems = secondLine.split(SPLITTER_HEADER_ITEM);
                String requestUrl = secondListItems[1];
                int index = requestUrl.indexOf("?");
                String rewriteUrl = null;
                if (index > 0){
                    rewriteUrl = requestUrl + "&" + INJECT_TO_REQUEST_ENTITY_REQUEST_ID + "=" + requestId ;
                }else {
                    rewriteUrl = requestUrl + "?" + INJECT_TO_REQUEST_ENTITY_REQUEST_ID + "=" + requestId ;
                }
                secondListItems[1] =  rewriteUrl;
                StringBuilder tempResult = new StringBuilder(20);
                for (String item : secondListItems){
                    tempResult.append(item).append(SPLITTER_HEADER_ITEM);
                }
                lines[1] = tempResult.toString().substring(0,tempResult.toString().length()-1);
                StringBuilder stringBuilder = new StringBuilder(100);
                for (int i=0;i<lines.length;i++){
                    if (i== lines.length-1){
                        stringBuilder.append(lines[i]).append(SPLITTER_HEAD_FIRST_LINE);
                    }else{
                        stringBuilder.append(lines[i]);
                    }
                }
                result = stringBuilder.toString();
            }
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        if(args != null){
            for(String arg : args){
                System.out.println(arg);
            }
        }
        BufferedReader stdin = new BufferedReader(new InputStreamReader(
                System.in));
        String line = null;
        try {
            while ((line = stdin.readLine()) != null) {
                String decodedLine = decodeHexString(line);
                String transformedLine = transformHTTPMessage(decodedLine);
                String encodedLine = encodeHexString(transformedLine);
                System.out.println(encodedLine);
            }
        } catch (IOException e) {
        }
    }

}

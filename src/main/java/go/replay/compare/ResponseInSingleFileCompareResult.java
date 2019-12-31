package go.replay.compare;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import com.topwqp.micro.gor.Constants;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author  wangqiupeng
 * @date 2019年12月19日14:05:16
 * @desc 响应在单个文件中的比较处理，请求和响应信息在单个文件中
 */
public class ResponseInSingleFileCompareResult {
    private static final String lineSeparator = System.getProperty("line.separator");

    /**
     * HTTP解析时用到的分割器
     */
    private static final Splitter httpParseSplitter = Splitter.on(" ");

    private String inputFile;

    private File resultFile;



    /**
     * @param inputFile             比较文件路径
     * @param resultFileName           输出的结果文件路径
     * @throws IOException
     */
    public ResponseInSingleFileCompareResult(String inputFile, String resultFileName) throws IOException {
        this.inputFile = inputFile;
        this.resultFile = new File(resultFileName);
        Files.write("Http compare result", resultFile, Charsets.UTF_8);
        Files.append(lineSeparator, resultFile, Charsets.UTF_8);
    }

    /**
     * 将内容追加入文件
     *
     * @param contents
     * @throws IOException
     */
    private void append(final String contents) throws IOException {
        Files.append(contents, resultFile, Charsets.UTF_8);
        Files.append(lineSeparator, resultFile, Charsets.UTF_8);
    }

    /**
     * 将文件解析成对象列表
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    private Map<String,Map<String,HttpResponseEntity>> parseFile(String fileName) throws Exception {
        Map<String,Map<String,HttpResponseEntity>> readLines = Files.readLines(new File(fileName), Charsets.UTF_8, new LineProcessor<Map<String,Map<String,HttpResponseEntity>>>() {
            private Map<String,HttpResponseEntity>   originResponse = Maps.newHashMap();
            private Map<String,HttpResponseEntity>   replayResponse = Maps.newHashMap();
            private Map<String,Map<String,HttpResponseEntity>>  map = Maps.newHashMap();

            /**
             * 每一个请求响应解析结果所保存的对象
             */
            private HttpResponseEntity entity;

            /**
             * 标记当前处理的是从实体开始的第几行(每个实体以三个特殊的unicode字符开始)
             */
            private int lineNumber = 0;

            /**
             * true代表是响应，false代表是请求
             */
            private boolean isResponse = false;

            /**
             * 标志是否在处理响应体,
             * 根据HTTP协议描述:
             *  请求消息和响应消息都是由开始行，消息报头（可选），空行（只有CRLF的行），消息正文（可选）组成
             *
             */
            private boolean isResponseBody = false;

            /**
             * 标记解析响应的Chunked编码的内容值,初始从0开始
             * 这样可以根据奇数偶数来判断当前是在处理Chunked头还是Chunked的内容
             */
            private int processChunkedContent = 0;

            @Override
            public boolean processLine(String line) throws IOException {
                if (line.startsWith(Constants.START_FLAG)) {
                    //lineNumber值复位为0
                    lineNumber = 0;
                    isResponse = false;
                    isResponseBody = false;
                    processChunkedContent = 0;
                } else {
                    lineNumber++;
                    if (lineNumber == 1){
                        //第一项代表响应， 2、3代表响应结果， 1代表请求
                        String type =Iterables.get(httpParseSplitter.split(line), 0);
                        // 为2、3 代表响应， 2代表原始响应， 3代表回放响应
                        if (Constants.TWO.equals(type)){
                            //第二项表示标志ID
                            isResponse = true;
                            entity = new HttpResponseEntity();
                            entity.setId(Iterables.get(httpParseSplitter.split(line), 1));
                            originResponse.put(entity.getId(),entity);
                        }else if(Constants.THREE.equals(type)){
                            //第二项表示标志ID
                            isResponse = true;
                            entity = new HttpResponseEntity();
                            entity.setId(Iterables.get(httpParseSplitter.split(line), 1));
                            replayResponse.put(entity.getId(),entity);
                        }
                    }else {
                        if (isResponse){
                            //在处理响应
                            if (line.equals("")) {
                                isResponseBody = true;
                            } else if (line.startsWith(Constants.TRANSFER_ENCODING)) {
                                //第二项表示Transfer-Encoding的内容
                                entity.setResponseTransferEncoding(Iterables.get(httpParseSplitter.split(line), 1));
                            } else if (line.startsWith(Constants.CONTENT_TYPE)) {
                                //第二项表示Content-Type的内容
                                entity.setResponseContentType(Iterables.get(httpParseSplitter.split(line), 1));
                                if (entity.getResponseContentType().startsWith(Constants.APPLICATION_JSON)) {
                                    //表示响应内容为json格式
                                    entity.setResponseJson(true);
                                }
                            } else if (line.startsWith(Constants.CONTENT_LENGTH)) {
                                //第二项表示Content-Length的内容
                                entity.setResponseContentLength(Iterables.get(httpParseSplitter.split(line), 1));
                            } else if (isResponseBody) {
                                //表示响应的内容是用Chunked编码
                                if (entity.getResponseTransferEncoding() != null) {
                                    processChunkedContent++;
                                    //处理Chunked内容
                                    if ((processChunkedContent & 1) == 0) {
                                        entity.getResponseContent().append(line);
                                    }
                                }
                                //表示响应的内容是用正常编码
                                if (entity.getResponseTransferEncoding() == null || entity.getResponseContentLength() != null) {
                                    entity.getResponseContent().append(line);
                                }
                            }
                        }
                    }
                }
                return true;
            }

            @Override
            public Map<String,Map<String,HttpResponseEntity>> getResult() {
                map.put("originResponse",originResponse);
                map.put("replayResponse",replayResponse);
                return map;
            }
        });

        return readLines;
    }

    /**
     * 比较内容是否相同
     *
     * @param leftContent    左边的内容
     * @param rightContent   被比较的右边的内容
     * @param isJson         内容是否是json
     * @return
     */
    private boolean isContentSame(String leftContent, String rightContent, boolean isJson) {
        //分别对内容进行MD5比较，如果MD5值相等，必相等，否则，再进行进一步比较
        String leftContentMD5 = MD5Util.getMD5String(leftContent);
        String rightContentMD5 = MD5Util.getMD5String(rightContent);
        if (leftContentMD5.equals(rightContentMD5)){
            return true;
        }
        boolean result;
        if (isJson) {
            JsonContentCompare jsonContentCompare = new JsonContentCompare(leftContent, rightContent);
            result = jsonContentCompare.compare();
        } else {
            result = leftContent.equals(rightContent);
        }

        return result;
    }

    /**
     * 比较HTTP日志文件
     *
     * @throws Exception
     */
    public void compare() throws Exception {
        Map<String,Map<String,HttpResponseEntity>> resultMap = parseFile(inputFile);
        Map<String,HttpResponseEntity>  leftMap = resultMap.get("originResponse");
        append("left file 总行数:" + leftMap.size());
        Map<String,HttpResponseEntity> rightMap = resultMap.get("replayResponse");
        append("right file 总行数:" + rightMap.size());
        for (Map.Entry<String,HttpResponseEntity> entry : leftMap.entrySet()){
            HttpResponseEntity  leftResponse =  entry.getValue();
            HttpResponseEntity  rightResponse = rightMap.get(entry.getKey());
            if (rightResponse == null) {
                continue;
            }
            String leftResponseContent = leftResponse.getResponseContent().toString();
            String rightResponseContent = rightResponse.getResponseContent().toString();
            /**
             * 将响应报文比较后不同的响应内容记录到结果文件
             */
            if (!isContentSame(leftResponseContent, rightResponseContent, leftResponse.isResponseJson())) {
                append("===================================");
                append(leftResponse.getId());
                append("");
                append(rightResponse.getId());
                append("");
                append("left 响应内容 :");
                append(leftResponse.getResponseContent().toString());
                append("");
                append("right 响应内容 :");
                append(rightResponse.getResponseContent().toString());
                append("===================================");
                append("");
                append("");
                append("");
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ResponseInSingleFileCompareResult compareHttpResponseJsonResult = new ResponseInSingleFileCompareResult(
                "/Users/topwqp/Documents/work/tech/1223/1223.log",
                "/Users/topwqp/Documents/work/tech/1223/result.txt");
        compareHttpResponseJsonResult.compare();

    }
}

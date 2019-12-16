package go.replay.compare;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author wangqiupeng
 * @desc 比较http response json result
 * @date 2019年12月16日11:14:39
 */
public class CompareHttpResponseJsonResult {
    private static final String lineSeparator = System.getProperty("line.separator");

    /**
     * HTTP解析时用到的分割器
     */
    private static final Splitter httpParseSplitter = Splitter.on(" ");

    /**
     * properties文件解析时用到的分割器
     */
    private static final Splitter propertiesParseSplitter = Splitter.on(",");

    public static final String ONE = "1";

    private String leftFileName;

    private String rightFileName;

    private File resultFile;

    /**
     * @param leftFileName             比较文件路径
     * @param rightFileName            被比较文件路径
     * @param resultFileName           输出的结果文件路径
     * @throws IOException
     */
    public CompareHttpResponseJsonResult(String leftFileName, String rightFileName, String resultFileName) throws IOException {
        this.leftFileName = leftFileName;
        this.rightFileName = rightFileName;

        /**
         * 比较结果文件处理
         */
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
    private List<HttpResponseEntity> parseFile(String fileName) throws Exception {
        List<HttpResponseEntity> readLines = Files.readLines(new File(fileName), Charsets.UTF_8, new LineProcessor<List<HttpResponseEntity>>() {
            private List<HttpResponseEntity> result = Lists.newArrayList();

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
                if (line.startsWith("\uD83D\uDC35\uD83D\uDE48\uD83D\uDE49")) {
                    lineNumber = 0;//lineNumber值复位为0
                    isResponse = false;
                    isResponseBody = false;
                    processChunkedContent = 0;
                } else {
                    lineNumber++;
                    if (lineNumber == 1){
                        //第一项代表响应， 2、3代表响应结果， 1代表请求
                        String type =Iterables.get(httpParseSplitter.split(line), 0);
                        // 为2、3 代表响应， 2代表原始响应， 3代表回放响应
                        if (!ONE.equals(type)){
                            //第二项表示标志ID
                            isResponse = true;
                            entity = new HttpResponseEntity();
                            entity.setId(Iterables.get(httpParseSplitter.split(line), 1));
                            result.add(entity);
                        }
                    }else {
                        if (isResponse){
                            //在处理响应
                            if (line.equals("")) {
                                isResponseBody = true;
                            } else if (line.startsWith("Transfer-Encoding")) {
                                entity.setResponseTransferEncoding(Iterables.get(httpParseSplitter.split(line), 1));//第二项表示Transfer-Encoding的内容
                            } else if (line.startsWith("Content-Type")) {
                                entity.setResponseContentType(Iterables.get(httpParseSplitter.split(line), 1));//第二项表示Content-Type的内容
                                if (entity.getResponseContentType().startsWith("application/json")) {
                                    entity.setResponseJson(true);//表示响应内容为json格式
                                }
                            } else if (line.startsWith("Content-Length")) {
                                entity.setResponseContentLength(Iterables.get(httpParseSplitter.split(line), 1));//第二项表示Content-Length的内容
                            } else if (isResponseBody) {
                                if (entity.getResponseTransferEncoding() != null) {//表示响应的内容是用Chunked编码
                                    processChunkedContent++;
                                    if ((processChunkedContent & 1) == 0) {//在处理Chunked内容
                                        entity.getResponseContent().append(line);
                                    }
                                }
                                if (entity.getResponseContentLength() != null) {//表示响应的内容是用正常编码
                                    entity.getResponseContent().append(line);
                                }
                            }
                        }
                    }
                }
                return true;
            }

            @Override
            public List<HttpResponseEntity> getResult() {
                return result;
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
        List<HttpResponseEntity> leftList = parseFile(leftFileName);
        append("left file 总行数:" + leftList.size());
        List<HttpResponseEntity> rightList = parseFile(rightFileName);
        append("right file 总行数:" + rightList.size());
        for (int i = 0; i < leftList.size(); i++) {
            HttpResponseEntity leftEntity = leftList.get(i);
            String leftId = leftEntity.getId();
            HttpResponseEntity rightEntity = null;
            for (int j = 0; j < rightList.size(); j++) {
                if (leftId.equals(rightList.get(j).getId())) {
                    rightEntity = rightList.get(j);
                    break;
                }
            }
            if (rightEntity == null) {
                continue;
            }
            String rightId = rightEntity.getId();

            String leftResponseContent = leftEntity.getResponseContent().toString();
            String rightResponseContent = rightEntity.getResponseContent().toString();

            /**
             * 将响应报文比较后不同的响应内容记录到结果文件
             */
            if (!isContentSame(leftResponseContent, rightResponseContent, leftEntity.isResponseJson())) {
                append("===================================");
                append(leftId);
                append("");
                append(rightId);
                append("");
                append("left 响应内容 :");
                append(leftEntity.getResponseContent().toString());
                append("");
                append("right 响应内容 :");
                append(rightEntity.getResponseContent().toString());
                append("===================================");
                append("");
                append("");
                append("");
            }
        }
    }

    public static void main(String[] args) throws Exception {
        CompareHttpResponseJsonResult compareHttpResponseJsonResult = new CompareHttpResponseJsonResult("/Users/topwqp/Documents/work/tech/mul_json_bak/online_mul_0.gor",
                "/Users/topwqp/Documents/work/tech/mul_json_bak/test_mul_0.gor",
                "/Users/topwqp/Documents/work/tech/mul_json_bak/result.gor");
        compareHttpResponseJsonResult.compare();

    }
}

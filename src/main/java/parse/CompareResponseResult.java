package parse;

/**
 * @author wangqiupeng
 * @date 2019年12月12日16:46:44
 * @desc 比较响应结果  基于流量标识，requestId进行标识
 * 这里我详细说一下：
 * 流量的复制 并保存：
 * sudo ./gor --input-raw-track-response --middleware "java Echo" --input-raw :9901  --output-file requests.gor
 * 上述命令是 把请求和响应都保存在request.gor中，便于后期流量回放， --middleware "java Echo"， Echo是我自己写的对流量进行标记
 * 方便后期进行对比的消息
 * 2、流量的回放
 * 执行以下命令进行流量回放：
 * ./gor --input-file requests_0.gor --output-http="http://localhost:9902"
 * 3、监控并捕获流量回放信息
 * 监听9902端口进行流量监控，并把请求响应结果保存在 replay_result.gor中
 * sudo ./gor --input-raw-track-response  --input-raw :9902  --output-file replay_result.gor
 */
public class CompareResponseResult {


}

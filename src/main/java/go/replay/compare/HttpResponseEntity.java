package go.replay.compare;

/**
 * @author wangqiupeng
 * @desc 请求响应实体
 * @date 2019年12月12日17:54:54
 */
public class HttpResponseEntity {
    private String id;
    private String responseContentType;
    private String responseTransferEncoding;
    private boolean isResponseJson = false;
    private String responseContentLength;
    private StringBuilder responseContent = new StringBuilder();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResponseContentType() {
        return responseContentType;
    }

    public void setResponseContentType(String responseContentType) {
        this.responseContentType = responseContentType;
    }

    public String getResponseTransferEncoding() {
        return responseTransferEncoding;
    }

    public void setResponseTransferEncoding(String responseTransferEncoding) {
        this.responseTransferEncoding = responseTransferEncoding;
    }

    public boolean isResponseJson() {
        return isResponseJson;
    }

    public void setResponseJson(boolean responseJson) {
        isResponseJson = responseJson;
    }

    public String getResponseContentLength() {
        return responseContentLength;
    }

    public void setResponseContentLength(String responseContentLength) {
        this.responseContentLength = responseContentLength;
    }

    public StringBuilder getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(StringBuilder responseContent) {
        this.responseContent = responseContent;
    }

    @Override
    public String toString() {
        return "ReqRespEntity{" +
                "id='" + id + '\'' +
                ", responseContentType='" + responseContentType + '\'' +
                ", responseTransferEncoding='" + responseTransferEncoding + '\'' +
                ", isResponseJson=" + isResponseJson +
                ", responseContentLength='" + responseContentLength + '\'' +
                ", responseContent=" + responseContent +
                '}';
    }
}

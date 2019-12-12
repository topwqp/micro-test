# micro-test

# 流量复制并回放后对 原结果和响应结果进行比对

### 使用场景： 
一个核心的复杂功能进行重构： 比如收益计算等，需要模拟各种复杂的场景，
在测试环境中，很难模拟线上复杂的场景，需要拷贝线上流量为 A， 然后通过线上流量
A进行在仿真环境（重构过的新的部署环境）进行回放
A中保存了线上的流量请求和响应
回放以后，可以直接监听保存为B
B中为回放的文件，然后通过解析对比（在A和B中都有流量标识）标识一个请求信息，因为只有请求才能进行转发，
所以需要对请求信息进行标识！
目前我写入标识为 RequestId

### 流量复制并保存
```shell script
# 把请求和响应都保存在request.gor中，便于后期流量回放， --middleware "java Echo"， Echo是我自己写的对流量进行标记
sudo ./gor --input-raw-track-response --middleware "java Echo" --input-raw :9901  --output-file requests.gor
```

### 注意： 以下两步流量回放和流量捕获是同时进行的

### 流量回放
```shell script
# 把请求回放到9902端口
./gor --input-file requests_0.gor --output-http="http://localhost:9902"
```

### 监控并捕获流量回放信息
```shell script
#监听9902端口进行流量监控，并把请求响应结果保存在 replay_result.gor中
sudo ./gor --input-raw-track-response  --input-raw :9902  --output-file replay_result.gor
```





